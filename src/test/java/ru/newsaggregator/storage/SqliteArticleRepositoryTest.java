package ru.newsaggregator.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.newsaggregator.model.Article;
import ru.newsaggregator.storage.sqlite.SqliteArticleRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqliteArticleRepositoryTest {

    private Database database;
    private ArticleRepository repository;

    @BeforeEach
    void setUp() {
        database = new Database("jdbc:sqlite::memory:");
        repository = new SqliteArticleRepository(database);
    }

    @AfterEach
    void tearDown() {
        database.close();
    }

    @Test
    void savesArticleAndAssignsId() {
        Article article = sample("https://news/1", "Заголовок", LocalDateTime.of(2026, 6, 5, 9, 0));
        assertTrue(repository.save(article));
        assertTrue(article.getId() > 0);
        assertEquals(1, repository.count());
    }

    @Test
    void doesNotSaveDuplicateLink() {
        repository.save(sample("https://news/1", "Первый", LocalDateTime.now()));
        assertFalse(repository.save(sample("https://news/1", "Дубликат", LocalDateTime.now())));
        assertEquals(1, repository.count());
    }

    @Test
    void searchByTextMatchesTitle() {
        repository.save(sample("https://news/1", "Экономика растёт", LocalDateTime.now()));
        repository.save(sample("https://news/2", "Спортивные итоги", LocalDateTime.now()));
        List<Article> found = repository.searchByText("экономика");
        assertEquals(1, found.size());
    }

    @Test
    void deletesOldArticles() {
        repository.save(sample("https://news/old", "Старая", LocalDateTime.of(2020, 1, 1, 0, 0)));
        repository.save(sample("https://news/new", "Свежая", LocalDateTime.now()));
        List<Article> removed = repository.deleteOlderThan(LocalDateTime.of(2021, 1, 1, 0, 0));
        assertEquals(1, removed.size());
        assertEquals(1, repository.count());
    }

    private Article sample(String link, String title, LocalDateTime publishedAt) {
        Article article = new Article();
        article.setTitle(title);
        article.setLink(link);
        article.setSource("Тест");
        article.setCategory("Прочее");
        article.setPublishedAt(publishedAt);
        article.setSummary(title);
        article.setContent(title);
        article.setKeywords(List.of("тест"));
        article.setFetchedAt(LocalDateTime.now());
        return article;
    }
}
