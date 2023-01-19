package edu.sustc.cs209.backend.controller;

import edu.sustc.cs209.backend.entity.StackOverflowCompLang;
import edu.sustc.cs209.backend.entity.StackOverflowQuestion;
import edu.sustc.cs209.backend.service.StackOverflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/so")
public class StackoverflowController {

    private final StackOverflowService sos;

    @Autowired
    public StackoverflowController(StackOverflowService sos) {
        this.sos = sos;
    }

    @CrossOrigin
    @GetMapping(value = "/raw")
    @ResponseBody
    public List<StackOverflowQuestion> getRawPageQuestions(@RequestParam String api,  // "/questions/tagged/java"
                                                           @RequestParam String tab,  // MostFreq
                                                           @RequestParam Integer pages,
                                                           @RequestParam Boolean forceUpdate) {
        return sos.$(api, tab, pages, forceUpdate);
    }

    @CrossOrigin
    @GetMapping(value = "/questionWC")
    @ResponseBody
    public List<Object> countFreqWordsInIssueTitle(@RequestParam String api,  // "/questions/tagged/java"
                                                   @RequestParam String tab,  // MostFreq
                                                   @RequestParam Integer pages,
                                                   @RequestParam Boolean noun) {
        return Collections.singletonList(sos.countFreqWordsInQuestionTitle(api, tab, pages, noun).entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(1500)
                .map(e -> Map.of("name", e.getKey(), "value", e.getValue())));
    }

    @CrossOrigin
    @GetMapping(value = "/exps")
    @ResponseBody
    public List<Object> countExceptionTags(@RequestParam String tab,  // MostFreq
                                           @RequestParam Integer pages) {
        var res = sos.countException(tab, pages);
        return Collections.singletonList(res.entrySet().stream()
                .map(e -> Map.of("name", e.getKey(), "value", e.getValue())));
    }

    @CrossOrigin
    @GetMapping(value = "/comp")
    @ResponseBody
    public List<StackOverflowCompLang> compLanguages() {
        return sos.compareLanguages(StackOverflowService.COMP_LANG);
    }

}
