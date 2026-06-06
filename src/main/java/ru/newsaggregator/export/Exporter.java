package ru.newsaggregator.export;

import ru.newsaggregator.model.Article;

import java.util.List;

public interface Exporter {

    String format();

    String extension();

    String render(List<Article> articles);
}
