package ru.newsaggregator.config;

import ru.newsaggregator.model.Source;

import java.util.List;

public class AppConfig {

    private final String databaseFile;
    private final int defaultIntervalMinutes;
    private final List<Source> defaultSources;

    public AppConfig(String databaseFile, int defaultIntervalMinutes, List<Source> defaultSources) {
        this.databaseFile = databaseFile;
        this.defaultIntervalMinutes = defaultIntervalMinutes;
        this.defaultSources = defaultSources;
    }

    public static AppConfig defaults() {
        List<Source> sources = List.of(
                new Source("Lenta.ru", "https://lenta.ru/rss/news"),
                new Source("РИА Новости", "https://ria.ru/export/rss2/archive/index.xml"),
                new Source("РБК", "https://rssexport.rbc.ru/rbcnews/news/30/full.rss"),
                new Source("Газета.ru", "https://www.gazeta.ru/export/rss/first.xml"),
                new Source("ТАСС", "https://tass.ru/rss/v2.xml")
        );
        return new AppConfig("news.db", 30, sources);
    }

    public String jdbcUrl() {
        return "jdbc:sqlite:" + databaseFile;
    }

    public String getDatabaseFile() {
        return databaseFile;
    }

    public int getDefaultIntervalMinutes() {
        return defaultIntervalMinutes;
    }

    public List<Source> getDefaultSources() {
        return defaultSources;
    }
}
