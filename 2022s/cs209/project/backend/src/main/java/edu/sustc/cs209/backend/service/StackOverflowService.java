package edu.sustc.cs209.backend.service;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.sustc.cs209.backend.dao.StackOverflowCompLangDAO;
import edu.sustc.cs209.backend.dao.StackOverflowPageUpdateLogDAO;
import edu.sustc.cs209.backend.dao.StackOverflowQuestionDAO;
import edu.sustc.cs209.backend.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class StackOverflowService {

    private static final Logger logger = Logger.getLogger(String.valueOf(StackOverflowService.class));
    //    private final StackOverflowQuestionPageProc processor = new StackOverflowQuestionPageProc();
    private final StackOverflowQuestionPageProc processor = StackOverflowQuestionPageProc.INSTANCE;

    private final StackOverflowQuestionDAO questionDAO;
    private final StackOverflowPageUpdateLogDAO updateLogDAO;
    private final StackOverflowCompLangDAO compLangDAO;

    private static final Properties props = new Properties();
    private static final StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

    private final Lock modifyQueryQueue = new ReentrantLock();
    private static final Map<String, Lock> querying = new HashMap<>();

    private final Lock modifyParseQueue = new ReentrantLock();
    private static final Map<String, Lock> parsing = new HashMap<>();

    public static final StackOverflowTag INST_EXCEPTION = new StackOverflowTag("exception");


    static {
        props.setProperty("annotators", "tokenize, ssplit, pos, parse");
        props.setProperty("coref.algorithm", "neural");
    }


    @Autowired
    public StackOverflowService(StackOverflowQuestionDAO questionDAO,
                                StackOverflowPageUpdateLogDAO updateLogDAO,
                                StackOverflowCompLangDAO compLangDAO) {
        this.questionDAO = questionDAO;
        this.updateLogDAO = updateLogDAO;
        this.compLangDAO = compLangDAO;
    }

    public List<StackOverflowQuestion> $$(String api, String tab, int page) {
        String pos = null;
        if (api.length() > 20) {
            pos = String.format("/exps/%s/%d", tab, page);
        } else {
            pos = String.format("%s/%s/%d", api, tab, page);
        }
        try {
            updateLogDAO.deleteById(pos);
        } catch (Exception ignored) {
        }
        try {
            questionDAO.deleteByPosition(pos);
        } catch (Exception ignored) {
        }

        modifyQueryQueue.lock();
        boolean containsKey = querying.containsKey(pos);
        modifyQueryQueue.unlock();

        List<CompletableFuture<Void>> futures = new LinkedList<>();
        List<StackOverflowQuestion> res = new CopyOnWriteArrayList<>();

        if (containsKey) {
            Lock l = querying.get(pos);
            l.lock();  // wait for the first request in other running method
            l.unlock();
            var dbs = questionDAO.findByPosition(pos);
            if (!dbs.isEmpty()) {
                logger.info("Got repo after waiting for other query");
                return dbs;
            }
        }

        modifyQueryQueue.lock();
        Lock isQuerying = new ReentrantLock();
        isQuerying.lock();
        querying.put(pos, isQuerying);
        modifyQueryQueue.unlock();

        try {
            for (int i = 1; i <= page; i++) {
                String url = String.format("https://stackoverflow.com/%s?sort=%s&edited=true&page=%d&pagesize=50", api, tab, i);
                String finalPos = pos;
                futures.add(CompletableFuture.runAsync(() -> {
                    try {
                        List<StackOverflowQuestion> qs = processor.procPage(url).get();
                        qs.forEach(q -> q.setPosition(finalPos));
                        try {
                            questionDAO.saveAll(qs);
                        } catch (Exception e) {
                            logger.warning(e.getMessage());
                        }
                        res.addAll(qs);
                    } catch (InterruptedException | ExecutionException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }));
            }

            futures.parallelStream()
                    .forEach(f -> {
                        try {
                            f.get();
                        } catch (InterruptedException | ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    });
//            questionDAO.saveAll(res);
            updateLogDAO.save(new StackOverflowPageUpdateLog(pos, LocalDateTime.now()));
        } finally {
            isQuerying.unlock();
            modifyQueryQueue.lock();
            querying.remove(pos);
            modifyQueryQueue.unlock();
        }

        return res;
    }

    public List<StackOverflowQuestion> $(String api, String tab, int page, boolean forceUpdate) {
        String pos = null;
        if (api.length() > 20) {
            pos = String.format("/exps/%s/%d", tab, page);
        } else {
            pos = String.format("%s/%s/%d", api, tab, page);
        }
        if (!forceUpdate) {
            List<StackOverflowQuestion> qs = questionDAO.findByPosition(pos);
            if (!qs.isEmpty()) {
                logger.info("DB hit " + pos);
                return qs;
            }
        }
        return $$(api, tab, page);
    }


    public Map<String, Long> countFreqWordsInQuestionTitle(String api, String tab, int page, boolean noun) {
        List<StackOverflowQuestion> qs = $(api, tab, page, false);

        Map<String, Long> res = qs.stream()
                .map(is -> {
                    if (is.getTitleNouns() != null) {
                        return Arrays.asList((noun ? is.getTitleNouns() : is.getTitleVerbs()));
                    }
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
                questionDAO.saveAll(qs);
                break;
            } catch (Exception e) {
                logger.warning(e.getMessage());
            }
        }
        return res;
    }

    public Map<String, Long> countException(String tab, int page) {
        final String api = "/questions/tagged/exception+or+nullpointerexception+or+indexoutofboundsexception+or+nullreferenceexception+or+classnotfoundexception+or+filenotfoundexception+or+classcastexception+or+ioexception+or+illegalstateexception+or+runtimeexception+or+illegalargumentexception+or+sqlexception+or+numberformatexception+or+unhandled-exception+or+socketexception+or+nosuchelementexception+or+sslhandshakeexception+or+invalidoperationexception+or+indexoutofrangeexception+or+timeoutexception+java";
        List<StackOverflowQuestion> qs = $(api, tab, page, false);
        return qs.stream()
                .flatMap(q -> q.getTags().parallelStream()
                        .filter(t -> t.getTitle() != null && t.getTitle().contains("exception"))
                        .map(t -> new TagViews(t, q.getViews())))
                .collect(Collectors.toMap(
                                tv -> tv.getTag().getTitle(),
                                TagViews::getViews,
                                Long::sum
                        )
                );
    }

    @Data
    @AllArgsConstructor
    private static class TagViews {
        private StackOverflowTag tag;
        private long views;
    }

    public final static String[] COMP_LANG = new String[]{
            "javascript",
            "python",
            "go",
            "c%23", // c#
            "c",
            "c%2b%2b",  // c++
            "r",
            "swift",
            "php",
            "dart",
            "kotlin",
            "ruby",
            "rust",
            "scala",
            "java",  // itself
    };

    public List<StackOverflowCompLang> compareLanguages(String[] toCompare) {
        List<StackOverflowCompLang> res = new ArrayList<>();
        List<String> toFetch = new CopyOnWriteArrayList<>();
        Arrays.stream(toCompare)
                .forEach(s -> {
                    var ds = compLangDAO.findById(s);
                    if (ds.isPresent()) {
                        res.add(ds.get());
                    } else {
                        toFetch.add(s);
                    }
                });

        List<CompletableFuture<Void>> futures = new LinkedList<>();

        toFetch.forEach(l ->
                futures.add(CompletableFuture.runAsync(() -> {
                            try {
                                res.add(new StackOverflowCompLang(l, processor.questionCnt(l).get(), processor.questionCnt("java", l).get()));
                            } catch (InterruptedException | ExecutionException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                )
        );

        futures.parallelStream().forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });

        compLangDAO.saveAll(res);
        return res;
    }

}
