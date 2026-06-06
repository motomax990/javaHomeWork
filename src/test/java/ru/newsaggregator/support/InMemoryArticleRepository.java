package ru.newsaggregator.support;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.storage.ArticleRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryArticleRepository implements ArticleRepository {

    private final List<Article> store = new ArrayList<>();
    private long sequence = 0;

    @Override
    public boolean save(Article article) {
        if (existsByLink(article.getLink())) {
            return false;
        }
        article.setId(++sequence);
        store.add(article);
        return true;
    }

    @Override
    public boolean existsByLink(String link) {
        return store.stream().anyMatch(a -> Objects.equals(a.getLink(), link));
    }

    @Override
    public Optional<Article> findById(long id) {
        return store.stream().filter(a -> a.getId() == id).findFirst();
    }

    @Override
    public List<Article> findAll() {
        return new ArrayList<>(store);
    }

    @Override
    public List<Article> searchByText(String query) {
        String needle = query.toLowerCase();
        return store.stream()
                .filter(a -> contains(a.getTitle(), needle) || contains(a.getContent(), needle))
                .collect(Collectors.toList());
    }

    @Override
    public List<Article> deleteOlderThan(LocalDateTime threshold) {
        List<Article> removed = store.stream()
                .filter(a -> a.getPublishedAt() != null && a.getPublishedAt().isBefore(threshold))
                .collect(Collectors.toList());
        store.removeAll(removed);
        return removed;
    }

    @Override
    public long count() {
        return store.size();
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase().contains(needle);
    }
}
