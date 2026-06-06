package ru.newsaggregator.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.newsaggregator.fetch.NewsFetcher;
import ru.newsaggregator.model.Article;
import ru.newsaggregator.model.Source;
import ru.newsaggregator.process.ArticleProcessor;
import ru.newsaggregator.process.CategoryClassifier;
import ru.newsaggregator.process.ContentCleaner;
import ru.newsaggregator.process.KeywordExtractor;
import ru.newsaggregator.process.Summarizer;
import ru.newsaggregator.storage.ArticleRepository;
import ru.newsaggregator.storage.Database;
import ru.newsaggregator.storage.HistoryRepository;
import ru.newsaggregator.storage.SourceRepository;
import ru.newsaggregator.storage.sqlite.SqliteArticleRepository;
import ru.newsaggregator.storage.sqlite.SqliteHistoryRepository;
import ru.newsaggregator.storage.sqlite.SqliteSourceRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AggregationServiceTest {

    private Database database;
    private ArticleRepository articles;
    private HistoryRepository history;
    private AggregationService aggregation;

    @BeforeEach
    void setUp() {
        database = new Database("jdbc:sqlite::memory:");
        articles = new SqliteArticleRepository(database);
        SourceRepository sources = new SqliteSourceRepository(database);
        history = new SqliteHistoryRepository(database);
        sources.add(new Source("Тестовый источник", "http://test/feed"));

        NewsFetcher fetcher = source -> List.of(
                raw("https://test/1", "Учёные открыли планету", "<p>Большое научное открытие.</p>"),
                raw("https://test/2", "Сборная выиграла матч", "Команда победила в чемпионате."));

        ArticleProcessor processor = new ArticleProcessor(
                new ContentCleaner(), new CategoryClassifier(), new KeywordExtractor(), new Summarizer());

        aggregation = new AggregationService(sources, articles, history, fetcher, processor);
    }

    @AfterEach
    void tearDown() {
        database.close();
    }

    @Test
    void savesNewArticlesAndRecordsHistory() {
        AggregationReport report = aggregation.aggregate();
        assertEquals(2, report.getAdded());
        assertEquals(2, articles.count());
        assertEquals(2, history.recent(10).size());
    }

    @Test
    void skipsDuplicatesOnSecondRun() {
        aggregation.aggregate();
        AggregationReport second = aggregation.aggregate();
        assertEquals(0, second.getAdded());
        assertEquals(2, articles.count());
    }

    private Article raw(String link, String title, String content) {
        Article article = new Article();
        article.setLink(link);
        article.setTitle(title);
        article.setContent(content);
        return article;
    }
}
