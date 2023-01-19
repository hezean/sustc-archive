package edu.sustc.cs209.backend.service;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import deepnetts.data.DataSets;
import deepnetts.data.preprocessing.scale.Standardizer;
import deepnetts.net.FeedForwardNetwork;
import deepnetts.net.NeuralNetwork;
import deepnetts.net.layers.activation.ActivationType;
import deepnetts.net.loss.LossType;
import deepnetts.net.train.BackpropagationTrainer;
import deepnetts.net.train.Trainer;
import deepnetts.util.Tensor;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.sustc.cs209.backend.controller.GithubRepoController;
import edu.sustc.cs209.backend.dao.RepoContributorDAO;
import edu.sustc.cs209.backend.dao.StarLogDAO;
import edu.sustc.cs209.backend.dto.BarChartDisplayDTO;
import edu.sustc.cs209.backend.dao.GithubRepoDAO;
import edu.sustc.cs209.backend.dao.RepoIssueDAO;
import edu.sustc.cs209.backend.dto.IssueSummaryDTO;
import edu.sustc.cs209.backend.dto.RealPredDTO;
import edu.sustc.cs209.backend.entity.*;
import edu.sustc.cs209.backend.util.GithubRepoFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.sqlite.SQLiteException;

import javax.visrec.ml.data.DataSet;
import java.io.*;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@Service
@EnableScheduling
public class GithubRepoService {

    private final GithubRepoDAO repoDAO;
    private final RepoIssueDAO issueDAO;
    private final StarLogDAO starLogDAO;
    private final RepoContributorDAO contributorDAO;

    private static final CsvMapper csvMapper = new CsvMapper();
    private static final CsvSchema schema = csvMapper.schemaFor(RawIssueDateCnt.class);

    private static final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(3);

    private static final StandardDeviation sd = new StandardDeviation(false);
    private static final Mean mean = new Mean();

    private static final Properties props = new Properties();
    private static final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    private final Lock modifyQueryQueue = new ReentrantLock();
    private static final Map<String, Lock> querying = new HashMap<>();

    private static final Logger logger = Logger.getLogger(String.valueOf(GithubRepoService.class));

    static {
        props.setProperty("annotators", "tokenize, ssplit, pos, parse");
        props.setProperty("coref.algorithm", "neural");
    }


    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Data
    @JsonPropertyOrder({"dateStamp", "cnt"})
    private static class RawIssueDateCnt implements Comparable<RawIssueDateCnt> {
        private Long dateStamp;
        private Integer cnt;

        @Override
        public int compareTo(RawIssueDateCnt o) {
            return Long.compare(this.dateStamp, o.dateStamp);
        }
    }

    @Autowired
    public GithubRepoService(GithubRepoDAO repoDAO,
                             RepoIssueDAO issueDAO,
                             StarLogDAO starLogDAO,
                             RepoContributorDAO contributorDAO) {
        this.repoDAO = repoDAO;
        this.issueDAO = issueDAO;
        this.starLogDAO = starLogDAO;
        this.contributorDAO = contributorDAO;
    }

    public GithubRepo get(String username, String repoName, boolean forceUpdate) {
        logger.info("Start fetching repo information:: " + username + "/" + repoName);
        if (!forceUpdate) {
            try {
                Optional<GithubRepo> repo = repoDAO.findByNameAndOwner_UsernameAllIgnoreCase(repoName, username);
                if (repo.isPresent()) {
                    logger.info("Repo hit cache in database");
                    return repo.get();
                }
            } catch (Exception ignored) {
            }
        }

        String fullName = String.format("%s/%s", username, repoName);

        modifyQueryQueue.lock();
        boolean containsKey = querying.containsKey(fullName);
        modifyQueryQueue.unlock();

        if (containsKey) {
            Lock l = querying.get(fullName);
            l.lock();  // wait for the first request in other running method
            l.unlock();
            Optional<GithubRepo> repo = repoDAO.findByNameAndOwner_UsernameAllIgnoreCase(repoName, username);
            if (repo.isPresent()) {
                logger.info("Got repo after waiting for other query");
                return repo.get();
            }
        }

        modifyQueryQueue.lock();
        Lock isQuerying = new ReentrantLock();
        isQuerying.lock();
        querying.put(fullName, isQuerying);
        modifyQueryQueue.unlock();

        GithubRepo repoObj = null;
        try {
            repoObj = GithubRepoFactory.$(username, repoName);
            if (repoObj != null) {
                repoDAO.save(repoObj);
            }
        } finally {
            isQuerying.unlock();
            modifyQueryQueue.lock();
            querying.remove(fullName);
            modifyQueryQueue.unlock();
        }
        return repoObj;
    }

    public Map<Object, Integer> countIssuesGroupBy(String username, String repoName,
                                                   GithubRepoController.CountIssueByInterval interval, boolean byCreate) {
        Function<RepoIssue, Object> cvt = switch (interval) {
            case Year -> i -> Year.from(byCreate ? i.getCreatedAt() : Objects.requireNonNull(i.getClosedAt()));
            case Month -> i -> YearMonth.from(byCreate ? i.getCreatedAt() : Objects.requireNonNull(i.getClosedAt()));
            case Day -> i -> (byCreate ? i.getCreatedAt() : Objects.requireNonNull(i.getClosedAt())).toLocalDate();
        };

        List<RepoIssue> issues = get(username, repoName, false).getIssues();
        return issues.stream()
                .filter(i -> byCreate || i.getClosedAt() != null)
                .collect(Collectors.toMap(cvt, i -> 1, Integer::sum));
    }

    public List<BarChartDisplayDTO> forFrontend(Map<Object, Integer> raw) {
        return raw.entrySet()
                .stream()
                .map(r -> new BarChartDisplayDTO(r.getKey(), r.getValue()))
                .toList();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RealPredDTO predict(String username, String repoName, boolean byCreate) throws IOException {
        Map<LocalDate, Integer> historyData = (Map) countIssuesGroupBy(username, repoName, GithubRepoController.CountIssueByInterval.Day, byCreate);

        if (historyData.isEmpty()) {
            return new RealPredDTO();
        }

        Map<LocalDate, Integer> realIssueGR = new HashMap<>();

        AtomicLong res = new AtomicLong(0);
        final long[] firstDay = {Long.MAX_VALUE};
        final long[] lastDay = {0};
        List<Long> pastDays = new LinkedList<>();

        String csv = csvMapper.writer(schema).writeValueAsString(
                historyData.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> {
                            var ds = new RawIssueDateCnt(e.getKey().toEpochDay(), e.getValue() + res.intValue());
                            res.addAndGet(e.getValue());
                            firstDay[0] = Math.min(firstDay[0], ds.dateStamp);
                            lastDay[0] = Math.max(lastDay[0], ds.dateStamp);
                            pastDays.add(ds.dateStamp);
                            realIssueGR.put(e.getKey(), e.getValue() + res.intValue());
                            return ds;
                        })
                        .toList()
        );

        File tmp = File.createTempFile("deepnetts", ".csv");
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            fos.write(csv.getBytes());
        }

        DataSet ds = DataSets.readCsv(tmp, 1, 1, false, DataSets.DELIMITER_COMMA);
        Standardizer std = new Standardizer(ds);
        std.apply(ds);

        FeedForwardNetwork model = FeedForwardNetwork.builder()
                .addInputLayer(1)
                .addOutputLayer(1, ActivationType.LINEAR)
                .lossFunction(LossType.MEAN_SQUARED_ERROR)
                .build();
        try {
            Field trainerField = NeuralNetwork.class.getDeclaredField("trainer");
            trainerField.setAccessible(true);
            Trainer mt = (Trainer) trainerField.get(model);

            Field trainMaxEpoch = BackpropagationTrainer.class.getDeclaredField("maxEpochs");
            trainMaxEpoch.setAccessible(true);
            trainMaxEpoch.set(mt, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.train(ds);

        double[] predTs = LongStream.range(firstDay[0], lastDay[0] + 30).mapToDouble(l -> l).toArray();
        float[] stdPred = standardize(pastDays, predTs, std);

        Map<LocalDate, Float> pred = new HashMap<>();
        for (int i = 0; i < stdPred.length; i++) {
            pred.put(LocalDate.ofEpochDay((long) predTs[i]), model.predict(new float[]{stdPred[i]})[0]);
        }
        return new RealPredDTO(realIssueGR, pred);
    }

    private static float[] standardize(List<Long> pastDays, double[] raw, Standardizer ser) {
        double std, mn;
        try {
            Field meanF = Standardizer.class.getDeclaredField("mean");
            Field stdF = Standardizer.class.getDeclaredField("std");

            meanF.setAccessible(true);
            stdF.setAccessible(true);

            Tensor meanTensor = (Tensor) meanF.get(ser);
            Tensor stdTensor = (Tensor) stdF.get(ser);

            Field tensorVal = Tensor.class.getDeclaredField("values");
            tensorVal.setAccessible(true);

            mn = ((float[]) tensorVal.get(meanTensor))[0];
            std = ((float[]) tensorVal.get(stdTensor))[0];
        } catch (Exception e) {
            e.printStackTrace();
            double[] pda = pastDays.stream().mapToDouble(l -> l).toArray();
            std = sd.evaluate(pda);
            mn = mean.evaluate(pda);
        }

        double finalMn = mn;
        double finalStd = std;
        var par = Arrays.stream(raw)
                .map(r -> (r - finalMn) / finalStd)
                .toArray();
        float[] res = new float[par.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = (float) par[i];
        }
        return res;
    }


    Map<YearMonth, Integer> calcLabelInDiffMonth(RepoIssueLabel label) {
        List<RepoIssue> issues = issueDAO.findByLabels_id(label.getId());
        return issues.stream()
                .map(i -> YearMonth.from(i.getCreatedAt()))
                .collect(Collectors.toMap(i -> i, i -> 1, Integer::sum));
    }

    Map<Year, Integer> calcLabelInDiffYear(RepoIssueLabel label) {
        List<RepoIssue> issues = issueDAO.findByLabels_id(label.getId());
        return issues.stream()
                .map(i -> Year.from(i.getCreatedAt()))
                .collect(Collectors.toMap(i -> i, i -> 1, Integer::sum));
    }

    @SuppressWarnings("unchecked")
    public List<IssueSummaryDTO> summaryLabels(String username, String repoName) {
        GithubRepo repo = get(username, repoName, false);
        var res = repo.getLabels().stream()
                .collect(Collectors.toMap(
                        l -> l,
                        l -> List.of(calcLabelInDiffMonth(l), calcLabelInDiffYear(l)),
                        (m1, m2) -> m1
                ));

        return res.entrySet().stream()
                .map(e -> new IssueSummaryDTO(e.getKey(), (Map<YearMonth, Integer>) e.getValue().get(0), (Map<Year, Integer>) e.getValue().get(1)))
                .toList();
    }

    public Map<String, Long> countFreqWordsInIssueTitle(String username, String repoName, boolean noun) {
        GithubRepo repo = get(username, repoName, false);
        Map<String, Long> res = repo.getIssues().stream()
                .map(is -> {
                    if (is.getTitleNouns() != null) {
                        return Arrays.asList((noun ? is.getTitleNouns() : is.getTitleVerbs()));
                    }
                    logger.info("Analyzing title::: " + is.getTitle());
                    CoreDocument doc = new CoreDocument(is.getTitle());
                    pipeline.annotate(doc);

                    List<String> nounPhrases = new LinkedList<>();
                    List<String> verbPhrases = new LinkedList<>();

                    for (CoreSentence sent : doc.sentences()) {
                        for (int i = 0; i < sent.tokens().size(); i++) {
                            if (sent.posTags() == null || sent.posTags().get(i) == null) {
                                continue;
                            }
                            if (sent.posTags().get(i).contains("NN")) {
                                nounPhrases.add(sent.tokens().get(i).originalText());
                            } else if (sent.posTags().get(i).contains("VB")) {
                                verbPhrases.add(sent.tokens().get(i).originalText());
                            }
                        }
                    }
                    is.setTitleNouns(nounPhrases.toArray(new String[0]));
                    is.setTitleVerbs(verbPhrases.toArray(new String[0]));
//                    logger.info(nounPhrases + "  " + verbPhrases);
                    return noun ? nounPhrases : verbPhrases;
                })
                .flatMap(Collection::stream)
                .filter(w -> w.length() > 1)
                .collect(Collectors.toMap(
                        String::toLowerCase,
                        w -> 1L,
                        Long::sum
                ));
        while (true) {
            try {
                repoDAO.save(repo);
                break;
            } catch (Exception ignored) {
            }
        }
        return res;
    }

    public Set<StarLog> getStarHistory(String username, String repoName) {
        var repo = get(username, repoName, false);
        return repo.getStarHistOverlook();
    }

    public Map<String, Long> contributorsGroupByCountry(String username, String repoName) {
        var repo = get(username, repoName, false);
        return repo.getContributors().stream()
                .collect(Collectors.toMap(
                        RepoContributor::getCountry,
                        RepoContributor::getContributions,
                        Long::sum
                ));
    }

}
