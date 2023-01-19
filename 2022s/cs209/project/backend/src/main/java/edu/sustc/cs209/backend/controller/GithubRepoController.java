package edu.sustc.cs209.backend.controller;

import edu.sustc.cs209.backend.dao.GithubRepoAnalyzeDAO;
import edu.sustc.cs209.backend.dao.RepoIssueDAO;
import edu.sustc.cs209.backend.dto.IssueSummaryDTO;
import edu.sustc.cs209.backend.dto.IssueTrackerDTO;
import edu.sustc.cs209.backend.dto.RealPredDTO;
import edu.sustc.cs209.backend.entity.GithubRepo;
import edu.sustc.cs209.backend.entity.RepoContributor;
import edu.sustc.cs209.backend.entity.StarLog;
import edu.sustc.cs209.backend.service.GithubRepoService;
import edu.sustc.cs209.backend.util.AuthGithubApiRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@RestController
@RequestMapping("/api/gh")
public class GithubRepoController {

    private LocalDateTime lastFuzzySearch = LocalDateTime.of(1991, 1, 1, 0, 0, 0);
    private List<Map<String, String>> lastFuzzyResult = List.of();

    private final AuthGithubApiRequest webProxy = new AuthGithubApiRequest();
    private final GithubRepoService grs;
    private final GithubRepoAnalyzeDAO analyzeDAO;
    private final RepoIssueDAO issueDAO;

    private static final Logger logger = Logger.getLogger(String.valueOf(GithubRepoController.class));


    @Autowired
    public GithubRepoController(GithubRepoService gps,
                                GithubRepoAnalyzeDAO analyzeDAO,
                                RepoIssueDAO issueDAO) {
        this.grs = gps;
        this.analyzeDAO = analyzeDAO;
        this.issueDAO = issueDAO;
    }


    @CrossOrigin
    @GetMapping(value = "/fuzzy")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> fuzzySearchRepoByName(@RequestParam String q) {
        logger.info("Fuzzy search:: " + q);
        try {
            if (ChronoUnit.SECONDS.between(lastFuzzySearch, LocalDateTime.now()) < 1) {
                Thread.sleep(1);
            }
            lastFuzzySearch = LocalDateTime.now();
            Map<String, Object> res = webProxy.get(String.format("https://api.github.com/search/repositories?q=%s+language%%3Ajava&per_page=5&page=0",
                    URLEncoder.encode(q)), null, Map.class).get();
            var reposRaw = (List<Map<String, Object>>) res.get("items");
            lastFuzzyResult = reposRaw.stream()
                    .map(i -> Collections.singletonMap("value", (String) i.get("full_name")))
                    .toList();
        } catch (Exception ignored) {
        }
        return lastFuzzyResult;
    }

    public enum CountIssueByInterval {
        Day, Month, Year
    }

    private static String[] splitFullName(String fullName) {
        String[] res = fullName.split("/");
        if (res.length != 2) {
            throw new RuntimeException();
        }
        return res;
    }

    @CrossOrigin
    @GetMapping(value = "/issues")
    @ResponseBody
    @SuppressWarnings({"rawtypes", "unchecked"})
    public IssueTrackerDTO getIssuesCountRawData(@RequestParam String user,
                                                 @RequestParam String repo,
                                                 @RequestParam CountIssueByInterval interval) {
        return new IssueTrackerDTO(
                grs.forFrontend(grs.countIssuesGroupBy(user, repo, interval, true)),
                grs.forFrontend(grs.countIssuesGroupBy(user, repo, interval, false))
        );
    }

    @CrossOrigin
    @GetMapping(value = "/issues1")
    @ResponseBody
    @SuppressWarnings({"rawtypes", "unchecked"})
    public IssueTrackerDTO getIssuesCountRawData(@RequestParam String fullName,
                                                 @RequestParam CountIssueByInterval interval) {
        var res = splitFullName(fullName);
        return new IssueTrackerDTO(
                grs.forFrontend(grs.countIssuesGroupBy(res[0], res[1], interval, true)),
                grs.forFrontend(grs.countIssuesGroupBy(res[0], res[1], interval, false))
        );
    }

    @CrossOrigin
    @GetMapping(value = "/predict")
    @ResponseBody
    public RealPredDTO predictOneMonth(@RequestParam String user,
                                       @RequestParam String repo) {
        try {
            return grs.predict(user, repo, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @CrossOrigin
    @GetMapping(value = "/predict1")
    @ResponseBody
    public RealPredDTO predictOneMonth(@RequestParam String fullName) {
        var res = splitFullName(fullName);
        try {
            return grs.predict(res[0], res[1], true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @CrossOrigin
    @GetMapping(value = "/forceUpdate")
    public void forceUpdate(@RequestParam String user,
                            @RequestParam String repo) {
        grs.get(user, repo, true);
    }

    @CrossOrigin
    @GetMapping(value = "/forceUpdate1")
    public void forceUpdate(@RequestParam String fullName) {
        var res = splitFullName(fullName);
        grs.get(res[0], res[1], true);
    }

    @CrossOrigin
    @GetMapping(value = "/labels")
    @ResponseBody
    public List<IssueSummaryDTO> summaryLabels(@RequestParam String user,
                                               @RequestParam String repo) {
        return grs.summaryLabels(user, repo);
    }

    @CrossOrigin
    @GetMapping(value = "/stars")
    @ResponseBody
    public List<StarLog> getStarHistory(@RequestParam String user,
                                        @RequestParam String repo) {
        return grs.getStarHistory(user, repo).stream().toList();
    }

    @CrossOrigin
    @GetMapping(value = "/stars1")
    @ResponseBody
    public List<StarLog> getStarHistory(@RequestParam String fullName) {
        var res = splitFullName(fullName);
        return grs.getStarHistory(res[0], res[1]).stream().toList();
    }

    @CrossOrigin
    @GetMapping(value = "/labels1")
    @ResponseBody
    public List<IssueSummaryDTO> summaryLabels(@RequestParam String fullName) {
        var res = splitFullName(fullName);
        return grs.summaryLabels(res[0], res[1]);
    }

    @CrossOrigin
    @GetMapping(value = "/issueTitleWC")
    @ResponseBody
    public Map<String, Long> countFreqWordsInIssueTitle(@RequestParam String user,
                                                        @RequestParam String repo,
                                                        @RequestParam boolean noun) {
        return grs.countFreqWordsInIssueTitle(user, repo, noun);
    }

    @CrossOrigin
    @GetMapping(value = "/issueTitleWC1")
    @ResponseBody
    public Map<String, Long> countFreqWordsInIssueTitle(@RequestParam String fullName,
                                                        @RequestParam boolean noun) {
        var res = splitFullName(fullName);
        return grs.countFreqWordsInIssueTitle(res[0], res[1], noun);
    }

    @CrossOrigin
    @GetMapping(value = "/metadata")
    @ResponseBody
    public Map<String, Object> getMetadata(@RequestParam String user,
                                           @RequestParam String repo) {
        GithubRepo repoObj = grs.get(user, repo, false);
        return Map.of("id", repoObj.getId(),
                "name", repoObj.getName(),
                "owner", repoObj.getOwner(),
                "stars", repoObj.getStars(),
                "forks", repoObj.getForks(),
                "topics", List.of(repoObj.getTopics()),
                "language", repoObj.getLanguage(),
                "description", repoObj.getDescription(),
                "issues", repoObj.getIssueCnt(),
                "updateAt", repoObj.getInfoUpdatedAt());
    }

    @CrossOrigin
    @GetMapping(value = "/metadata1")
    @ResponseBody
    public Map<String, Object> getMetadata(@RequestParam String fullName) {
        var res = splitFullName(fullName);
        return getMetadata(res[0], res[1]);
    }

    @CrossOrigin
    @GetMapping(value = "/contributors")
    @ResponseBody
    public Map<String, Long> getContributors(@RequestParam String user,
                                                @RequestParam String repo) {
        return grs.contributorsGroupByCountry(user, repo);
    }

    @CrossOrigin
    @GetMapping(value = "/contributors1")
    @ResponseBody
    public Map<String, Long> getContributors(@RequestParam String fullName) {
        var res = splitFullName(fullName);
        return getContributors(res[0], res[1]);
    }

}
