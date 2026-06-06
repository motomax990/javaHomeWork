package ru.newsaggregator.service;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.storage.ArticleRepository;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsService {

    private final ArticleRepository articles;

    public AnalyticsService(ArticleRepository articles) {
        this.articles = articles;
    }

    public long total() {
        return articles.count();
    }

    public Map<String, Long> countByCategory() {
        Map<String, Long> counts = new HashMap<>();
        for (Article article : articles.findAll()) {
            String category = article.getCategory() == null ? "Без категории" : article.getCategory();
            counts.merge(category, 1L, Long::sum);
        }
        return sortedByValue(counts, counts.size());
    }

    public Map<String, Long> countBySource() {
        Map<String, Long> counts = new HashMap<>();
        for (Article article : articles.findAll()) {
            String source = article.getSource() == null ? "Неизвестно" : article.getSource();
            counts.merge(source, 1L, Long::sum);
        }
        return sortedByValue(counts, counts.size());
    }

    public Map<String, Long> topKeywords(int limit) {
        Map<String, Long> counts = new HashMap<>();
        for (Article article : articles.findAll()) {
            for (String keyword : article.getKeywords()) {
                counts.merge(keyword, 1L, Long::sum);
            }
        }
        return sortedByValue(counts, limit);
    }

    private Map<String, Long> sortedByValue(Map<String, Long> source, int limit) {
        Map<String, Long> result = new LinkedHashMap<>();
        source.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(limit)
                .forEach(entry -> result.put(entry.getKey(), entry.getValue()));
        return result;
    }
}
