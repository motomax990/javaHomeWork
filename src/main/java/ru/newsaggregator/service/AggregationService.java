package ru.newsaggregator.service;

import ru.newsaggregator.fetch.FetchException;
import ru.newsaggregator.fetch.NewsFetcher;
import ru.newsaggregator.model.Article;
import ru.newsaggregator.model.ChangeRecord;
import ru.newsaggregator.model.ChangeType;
import ru.newsaggregator.model.Source;
import ru.newsaggregator.process.ArticleProcessor;
import ru.newsaggregator.storage.ArticleRepository;
import ru.newsaggregator.storage.HistoryRepository;
import ru.newsaggregator.storage.SourceRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AggregationService {

    private final SourceRepository sources;
    private final ArticleRepository articles;
    private final HistoryRepository history;
    private final NewsFetcher fetcher;
    private final ArticleProcessor processor;

    public AggregationService(SourceRepository sources,
                              ArticleRepository articles,
                              HistoryRepository history,
                              NewsFetcher fetcher,
                              ArticleProcessor processor) {
        this.sources = sources;
        this.articles = articles;
        this.history = history;
        this.fetcher = fetcher;
        this.processor = processor;
    }

    public AggregationReport aggregate() {
        AggregationReport report = new AggregationReport();
        for (Source source : sources.findEnabled()) {
            try {
                collectFrom(source, report);
            } catch (FetchException e) {
                report.addError(source.getName(), e.getMessage());
            }
        }
        return report;
    }

    private void collectFrom(Source source, AggregationReport report) {
        for (Article article : fetcher.fetch(source)) {
            report.countProcessed();
            if (article.getLink() == null || articles.existsByLink(article.getLink())) {
                continue;
            }
            processor.process(article);
            LocalDateTime now = LocalDateTime.now();
            article.setFetchedAt(now);
            if (article.getPublishedAt() == null) {
                article.setPublishedAt(now);
            }
            if (articles.save(article)) {
                report.countAdded();
                history.record(new ChangeRecord(ChangeType.ADDED, article.getTitle(), article.getLink(), now));
            }
        }
    }

    public int cleanup(int days) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(days);
        List<Article> removed = articles.deleteOlderThan(threshold);
        LocalDateTime now = LocalDateTime.now();
        for (Article article : removed) {
            history.record(new ChangeRecord(ChangeType.REMOVED, article.getTitle(), article.getLink(), now));
        }
        return removed.size();
    }
}
