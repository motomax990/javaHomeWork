package ru.newsaggregator.storage;

import ru.newsaggregator.model.Article;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArticleRepository {

    boolean save(Article article);

    boolean existsByLink(String link);

    Optional<Article> findById(long id);

    List<Article> findAll();

    List<Article> searchByText(String query);

    List<Article> deleteOlderThan(LocalDateTime threshold);

    long count();
}
