package ru.newsaggregator.fetch;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.model.Source;

import java.io.IOException;
import java.util.List;

public class RssNewsFetcher implements NewsFetcher {

    private final PageDownloader downloader;
    private final RssParser parser;

    public RssNewsFetcher(PageDownloader downloader, RssParser parser) {
        this.downloader = downloader;
        this.parser = parser;
    }

    @Override
    public List<Article> fetch(Source source) {
        String xml;
        try {
            xml = downloader.download(source.getUrl());
        } catch (IOException e) {
            throw new FetchException("Не удалось загрузить " + source.getUrl() + ": " + e.getMessage(), e);
        }
        List<Article> articles = parser.parse(xml);
        for (Article article : articles) {
            article.setSource(source.getName());
        }
        return articles;
    }
}
