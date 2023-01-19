package edu.sustc.cs209.backend.service;


import edu.sustc.cs209.backend.entity.StackOverflowQuestion;
import edu.sustc.cs209.backend.entity.StackOverflowTag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StackOverflowQuestionPageProc {

    public final static StackOverflowQuestionPageProc INSTANCE = new StackOverflowQuestionPageProc();

    private static final Logger logger = Logger.getLogger(String.valueOf(StackOverflowQuestionPageProc.class));

    private StackOverflowQuestionPageProc() {
    }

    private static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        trustEveryone();
    }


    @Async
    public Future<List<StackOverflowQuestion>> procPage(String url) throws IOException {
        logger.info("Starting parse " + url);

        Document doc = Jsoup.connect(url).get();
        Element questionsContainer = doc.getElementById("questions");
        Elements questions = questionsContainer != null ? questionsContainer.getElementsByClass("s-post-summary js-post-summary") : null;

        return new AsyncResult<>(questions != null
                ? questions.stream()
                .map(e -> {
                    var upperTag = e.getElementsByClass("s-post-summary--stats js-post-summary-stats").get(0);
                    var contentTag = e.getElementsByClass("s-post-summary--content").get(0);

                    return new StackOverflowQuestion(
                            Long.parseLong(e.attr("data-post-id")),  // id
                            contentTag.getElementsByClass("s-post-summary--content-title").get(0).text(),  // title
                            Long.parseLong(upperTag.getElementsByClass("s-post-summary--stats-item-number").get(0).text()),  // votes
                            Integer.parseInt(upperTag.children().get(1)
                                    .getElementsByClass("s-post-summary--stats-item-number").get(0)
                                    .text()),  // answers
                            Long.parseLong(upperTag.children().get(2)
                                    .attr("title").split(" ")[0]),  // views
                            null, null, // parsed nlp
                            contentTag.children().get(2)
                                    .children().get(0)
                                    .children()
                                    .stream()
                                    .map(t -> new StackOverflowTag(t.text()))
                                    .collect(Collectors.toList()),  // tags
                            null  // page position
                    );
                })
                .toList()
                : List.of());
    }

    @Async
    public Future<Long> questionCnt(String... tags) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("https://stackoverflow.com/questions/tagged/");
        Arrays.stream(tags)
                .forEach(t -> sb.append(t).append("%20"));
        sb.append("?tab=frequent&pagesize=15");
        String url = sb.toString();

        logger.info("Starting parse " + url);

        Document doc = Jsoup.connect(url).get();
        Element mainBar = doc.getElementById("mainbar");
        Element cntElem = mainBar != null ? mainBar.getElementsByClass("fs-body3").get(0) : null;

        assert cntElem != null;
        return new AsyncResult<>(Long.parseLong(cntElem.text().split(" ")[0].replaceAll(",", "")));
    }
}
