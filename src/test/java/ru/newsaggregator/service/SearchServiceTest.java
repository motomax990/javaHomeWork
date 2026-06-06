package ru.newsaggregator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.newsaggregator.model.Article;
import ru.newsaggregator.support.InMemoryArticleRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchServiceTest {

    private InMemoryArticleRepository repository;
    private SearchService search;

    @BeforeEach
    void setUp() {
        repository = new InMemoryArticleRepository();
        repository.save(article("Матч дня", "Спорт", "Lenta.ru",
                LocalDateTime.of(2026, 6, 1, 10, 0), List.of("матч")));
        repository.save(article("Курс рубля", "Экономика", "РБК",
                LocalDateTime.of(2026, 6, 3, 10, 0), List.of("рубль")));
        repository.save(article("Чемпионат страны", "Спорт", "РБК",
                LocalDateTime.of(2026, 6, 5, 10, 0), List.of("чемпионат")));
        search = new SearchService(repository);
    }

    @Test
    void filtersByCategoryAndSortsByDateDesc() {
        List<Article> result = search.filter(new SearchCriteria().setCategory("Спорт"));
        assertEquals(2, result.size());
        assertEquals("Чемпионат страны", result.get(0).getTitle());
    }

    @Test
    void filtersBySourcePartialMatch() {
        List<Article> result = search.filter(new SearchCriteria().setSource("рбк"));
        assertEquals(2, result.size());
    }

    @Test
    void filtersByDateFrom() {
        List<Article> result = search.filter(
                new SearchCriteria().setFrom(LocalDateTime.of(2026, 6, 2, 0, 0)));
        assertEquals(2, result.size());
    }

    @Test
    void filtersByKeyword() {
        List<Article> result = search.filter(new SearchCriteria().setKeyword("рубль"));
        assertEquals(1, result.size());
        assertEquals("Курс рубля", result.get(0).getTitle());
    }

    @Test
    void searchByTextFindsTitle() {
        assertTrue(search.search("матч").stream().anyMatch(a -> a.getTitle().equals("Матч дня")));
    }

    private Article article(String title, String category, String source,
                            LocalDateTime publishedAt, List<String> keywords) {
        Article article = new Article();
        article.setTitle(title);
        article.setLink("https://news.example/" + title.hashCode());
        article.setCategory(category);
        article.setSource(source);
        article.setPublishedAt(publishedAt);
        article.setContent(title);
        article.setKeywords(new java.util.ArrayList<>(keywords));
        return article;
    }
}
