package ru.newsaggregator.storage;

import ru.newsaggregator.model.Source;

import java.util.List;

public interface SourceRepository {

    void add(Source source);

    List<Source> findAll();

    List<Source> findEnabled();

    boolean existsByUrl(String url);
}
