package ru.newsaggregator;

import ru.newsaggregator.cli.CommandRegistry;
import ru.newsaggregator.cli.ConsoleApplication;
import ru.newsaggregator.cli.commands.AddSourceCommand;
import ru.newsaggregator.cli.commands.AnalyticsCommand;
import ru.newsaggregator.cli.commands.AutoCommand;
import ru.newsaggregator.cli.commands.CategoriesCommand;
import ru.newsaggregator.cli.commands.CleanupCommand;
import ru.newsaggregator.cli.commands.ExitCommand;
import ru.newsaggregator.cli.commands.ExportCommand;
import ru.newsaggregator.cli.commands.FilterCommand;
import ru.newsaggregator.cli.commands.HelpCommand;
import ru.newsaggregator.cli.commands.HistoryCommand;
import ru.newsaggregator.cli.commands.ListCommand;
import ru.newsaggregator.cli.commands.SearchCommand;
import ru.newsaggregator.cli.commands.ShowCommand;
import ru.newsaggregator.cli.commands.SourcesCommand;
import ru.newsaggregator.cli.commands.UpdateCommand;
import ru.newsaggregator.config.AppConfig;
import ru.newsaggregator.export.ExporterFactory;
import ru.newsaggregator.fetch.HttpPageDownloader;
import ru.newsaggregator.fetch.NewsFetcher;
import ru.newsaggregator.fetch.RssNewsFetcher;
import ru.newsaggregator.fetch.RssParser;
import ru.newsaggregator.model.Source;
import ru.newsaggregator.process.ArticleProcessor;
import ru.newsaggregator.process.CategoryClassifier;
import ru.newsaggregator.process.ContentCleaner;
import ru.newsaggregator.process.KeywordExtractor;
import ru.newsaggregator.process.Summarizer;
import ru.newsaggregator.service.AggregationService;
import ru.newsaggregator.service.AnalyticsService;
import ru.newsaggregator.service.SearchService;
import ru.newsaggregator.service.UpdateScheduler;
import ru.newsaggregator.storage.ArticleRepository;
import ru.newsaggregator.storage.Database;
import ru.newsaggregator.storage.HistoryRepository;
import ru.newsaggregator.storage.SourceRepository;
import ru.newsaggregator.storage.sqlite.SqliteArticleRepository;
import ru.newsaggregator.storage.sqlite.SqliteHistoryRepository;
import ru.newsaggregator.storage.sqlite.SqliteSourceRepository;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class App {

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));

        AppConfig config = AppConfig.defaults();
        Database database = new Database(config.jdbcUrl());

        ArticleRepository articles = new SqliteArticleRepository(database);
        SourceRepository sources = new SqliteSourceRepository(database);
        HistoryRepository history = new SqliteHistoryRepository(database);
        seedSources(sources, config);

        NewsFetcher fetcher = new RssNewsFetcher(new HttpPageDownloader(), new RssParser());
        ArticleProcessor processor = new ArticleProcessor(
                new ContentCleaner(), new CategoryClassifier(), new KeywordExtractor(), new Summarizer());

        AggregationService aggregation = new AggregationService(sources, articles, history, fetcher, processor);
        SearchService search = new SearchService(articles);
        AnalyticsService analytics = new AnalyticsService(articles);
        UpdateScheduler scheduler = new UpdateScheduler();
        ExporterFactory exporters = new ExporterFactory();

        CommandRegistry registry = new CommandRegistry();
        ConsoleApplication application = new ConsoleApplication(registry);

        registry.register(new HelpCommand(registry));
        registry.register(new UpdateCommand(aggregation));
        registry.register(new ListCommand(articles));
        registry.register(new ShowCommand(articles));
        registry.register(new SearchCommand(search));
        registry.register(new FilterCommand(search));
        registry.register(new CategoriesCommand(analytics));
        registry.register(new AnalyticsCommand(analytics));
        registry.register(new SourcesCommand(sources));
        registry.register(new AddSourceCommand(sources));
        registry.register(new ExportCommand(search, exporters));
        registry.register(new CleanupCommand(aggregation));
        registry.register(new HistoryCommand(history));
        registry.register(new AutoCommand(scheduler, aggregation));
        registry.register(new ExitCommand(application, scheduler));

        application.run();

        scheduler.shutdown();
        database.close();
    }

    private static void seedSources(SourceRepository repository, AppConfig config) {
        if (!repository.findAll().isEmpty()) {
            return;
        }
        for (Source source : config.getDefaultSources()) {
            repository.add(source);
        }
    }
}
