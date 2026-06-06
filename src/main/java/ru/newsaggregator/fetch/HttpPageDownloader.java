package ru.newsaggregator.fetch;

import org.jsoup.Jsoup;

import java.io.IOException;

public class HttpPageDownloader implements PageDownloader {

    private static final String USER_AGENT =
            "Mozilla/5.0 (compatible; NewsAggregator/1.0; +https://edu.tbank.ru)";

    private final int timeoutMillis;

    public HttpPageDownloader() {
        this(15000);
    }

    public HttpPageDownloader(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    @Override
    public String download(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .timeout(timeoutMillis)
                .maxBodySize(0)
                .ignoreContentType(true)
                .followRedirects(true)
                .execute()
                .body();
    }
}
