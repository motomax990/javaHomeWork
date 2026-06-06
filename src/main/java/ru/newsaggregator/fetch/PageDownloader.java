package ru.newsaggregator.fetch;

import java.io.IOException;

public interface PageDownloader {

    String download(String url) throws IOException;
}
