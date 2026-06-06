package ru.newsaggregator.fetch;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.model.Source;

import java.util.List;

public interface NewsFetcher {

    List<Article> fetch(Source source);
}
