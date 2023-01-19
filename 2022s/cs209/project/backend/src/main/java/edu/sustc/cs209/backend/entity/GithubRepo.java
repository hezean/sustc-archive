package edu.sustc.cs209.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sustc.cs209.backend.util.AuthGithubApiRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer"}, ignoreUnknown = true)
@EnableAsync
public class GithubRepo implements Serializable {
    @JsonIgnoreProperties
    private static final Logger logger = Logger.getLogger(String.valueOf(GithubRepo.class));

    @JsonIgnoreProperties
    private static final int PAGE_SIZE = 100;

    @JsonIgnoreProperties
    private static final ObjectMapper mapper = new ObjectMapper();

    @JsonIgnoreProperties
    private static final AuthGithubApiRequest webProxy = new AuthGithubApiRequest();

    @Id
    private Long id;
    private String name;

    @ManyToOne(cascade = CascadeType.MERGE)
    private GithubUser owner;

    private Long stars;
    @JsonProperty("subscribers_count")
    private Long watchers;
    @JsonProperty("forks_count")
    private Long forks;

    @OrderColumn
    private String[] topics;
    private String language;
    private String description;

    @OneToMany(cascade = CascadeType.MERGE)
    @ToString.Exclude
    private Set<StarLog> starHistOverlook;

    private Date infoUpdatedAt;

    private Integer issueCnt;
    @OneToMany(cascade = CascadeType.MERGE)
    @ToString.Exclude
    private List<RepoIssue> issues;

    @OneToMany
    private List<RepoIssueLabel> labels;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JsonIgnoreProperties
    private List<RepoContributor> contributors;

    @JsonCreator
    @SuppressWarnings("rawtypes, unchecked")
    public GithubRepo(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("owner") Map<String, Object> owner,
            @JsonProperty("stargazers_count") long stars
    ) throws ExecutionException, InterruptedException {
        this.id = id;
        this.infoUpdatedAt = new Date(System.currentTimeMillis());
        this.name = name;
        this.stars = stars;
        this.contributors = new CopyOnWriteArrayList<>();

        setupUser(owner);

        try {
            Map<String, Object> info = webProxy.get(String.format("https://api.github.com/search/issues?q=repo:%s/%s+type:issue&page=0&per_page=1", this.owner.getUsername(), this.name),
                    null, Map.class).get();
            this.issueCnt = (int) info.get("total_count");
        } catch (IOException e) {
            this.issueCnt = 2000;
        }

        ConcurrentSkipListSet<RepoIssueLabel> ils = new ConcurrentSkipListSet<>();
        Set<GithubUser> cil = new HashSet<>();
        Set<StarLog> hst = new HashSet<>();
        this.issues = new ArrayList<>(this.issueCnt);

        Map[] css = new Map[0];
        try (var os = new URL(String.format("https://api.github.com/repos/%s/%s/contributors", this.owner.getUsername(), this.name)).openStream()) {
            css = mapper.readValue(os, Map[].class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 1; i <= Math.ceil(this.issueCnt / 100f); i++) {
            final int finalI = i;
            futures.add(CompletableFuture.runAsync(() -> extendIssueList(finalI, ils)));
        }
        for (int i = 1, maxQry = Math.min((int) Math.ceil(this.stars / 30f), 20); i <= maxQry; i++) {
            final int finalI = (int) Math.ceil(this.stars / 30f * i / 20);
            futures.add(CompletableFuture.runAsync(() -> extendStarHistory(finalI, hst)));
        }
        Arrays.stream(css).forEach(m -> {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    GithubUser usr = extendContributors((String) m.get("login")).get();
                    var cl = new RepoContributor(this, usr, (int) m.get("contributions"));
                    this.contributors.add(cl);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }));
        });
        for (var f : futures) {
            f.get();
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRunAsync(() -> {
                    this.labels = ils.stream().toList();
                    this.starHistOverlook = hst;
                    this.starHistOverlook.add(new StarLog(LocalDate.now(), this.stars));
                });
    }

    @Async
    void extendIssueList(int page, ConcurrentSkipListSet<RepoIssueLabel> ils) {
        try {
            String url = String.format("https://api.github.com/search/issues?q=repo:%s/%s%%20is:issue&state=all&per_page=100&page=%d", this.owner.getUsername(), this.name, page);
            byte[] resp = webProxy.get(url, null).get();

            JsonNode node = mapper.readTree(resp);
            JsonNode items = node.get("items");
            List<RepoIssue> pageIssues = Arrays.asList(mapper.treeToValue(items, RepoIssue[].class));

//            logger.info(String.format("Fetching issue:: <page=%d, cnt=%d>", page, pageIssues.size()));

            if (pageIssues.isEmpty()) {
                return;
            }
            this.issues.addAll(pageIssues);
            pageIssues.forEach(i -> ils.addAll(i.getLabels()));
        } catch (IOException e) {
            logger.warning("Fetch failed:: " + e.getMessage() + " &page=" + page);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @SuppressWarnings("rawtypes")
    void extendStarHistory(int page, Set<StarLog> hst) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/stargazers?per_page=30&page=%d", this.owner.getUsername(), this.name, page);
            Map[] res = webProxy.get(url, Map.of("Accept", "application/vnd.github.v3.star+json"), Map[].class).get();

            LocalDate starTime = LocalDateTime.parse((String) res[res.length - 1].get("starred_at"), DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
            StarLog rec = new StarLog(starTime, (page - 1) * 30L + res.length);

            LocalDate starTime1 = LocalDateTime.parse((String) res[0].get("starred_at"), DateTimeFormatter.ISO_DATE_TIME).toLocalDate();
            StarLog rec1 = new StarLog(starTime1, (page - 1) * 30L + 1);

            hst.add(rec);
            hst.add(rec1);
        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.warning("Get star history failed:: " + e.getMessage() + " &page=" + page);
        }
    }

    @Async
    Future<GithubUser> extendContributors(String login) {
        try {
            return new AsyncResult<>(webProxy.get(String.format("https://api.github.com/users/%s", URLEncoder.encode(login)), null, GithubUser.class).get());
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    void setupUser(Map<String, Object> owner) {
        try {
            this.owner = webProxy.get(String.format("https://api.github.com/users/%s", owner.get("login")), null, GithubUser.class).get();
        } catch (InterruptedException | IOException | ExecutionException e) {
            logger.warning("Fetch user failed:: " + e.getMessage());
        }
    }
}
