package ru.newsaggregator.service;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.storage.ArticleRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SearchService {

    private final ArticleRepository articles;

    public SearchService(ArticleRepository articles) {
        this.articles = articles;
    }

    public List<Article> search(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return articles.searchByText(text.trim());
    }

    public List<Article> filter(SearchCriteria criteria) {
        return articles.findAll().stream()
                .filter(article -> matchesCategory(article, criteria.getCategory()))
                .filter(article -> matchesSource(article, criteria.getSource()))
                .filter(article -> matchesKeyword(article, criteria.getKeyword()))
                .filter(article -> matchesFrom(article, criteria.getFrom()))
                .filter(article -> matchesTo(article, criteria.getTo()))
                .sorted(comparator(criteria.getSortOrder()))
                .collect(Collectors.toList());
    }

    private boolean matchesCategory(Article article, String category) {
        return category == null || category.equalsIgnoreCase(article.getCategory());
    }

    private boolean matchesSource(Article article, String source) {
        return source == null
                || (article.getSource() != null
                && article.getSource().toLowerCase().contains(source.toLowerCase()));
    }

    private boolean matchesKeyword(Article article, String keyword) {
        if (keyword == null) {
            return true;
        }
        String needle = keyword.toLowerCase();
        if (article.getKeywords().stream().anyMatch(k -> k.toLowerCase().contains(needle))) {
            return true;
        }
        String title = article.getTitle() == null ? "" : article.getTitle().toLowerCase();
        String content = article.getContent() == null ? "" : article.getContent().toLowerCase();
        return title.contains(needle) || content.contains(needle);
    }

    private boolean matchesFrom(Article article, LocalDateTime from) {
        return from == null
                || (article.getPublishedAt() != null && !article.getPublishedAt().isBefore(from));
    }

    private boolean matchesTo(Article article, LocalDateTime to) {
        return to == null
                || (article.getPublishedAt() != null && !article.getPublishedAt().isAfter(to));
    }

    private Comparator<Article> comparator(SortOrder order) {
        Comparator<Article> byDate = Comparator.comparing(
                Article::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        return switch (order) {
            case DATE_ASC -> byDate;
            case SOURCE -> Comparator.comparing(
                    Article::getSource, Comparator.nullsLast(String::compareToIgnoreCase))
                    .thenComparing(byDate.reversed());
            case CATEGORY -> Comparator.comparing(
                    Article::getCategory, Comparator.nullsLast(String::compareToIgnoreCase))
                    .thenComparing(byDate.reversed());
            default -> byDate.reversed();
        };
    }
}
