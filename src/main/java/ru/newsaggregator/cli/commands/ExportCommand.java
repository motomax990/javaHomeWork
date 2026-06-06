package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.cli.CriteriaParser;
import ru.newsaggregator.export.Exporter;
import ru.newsaggregator.export.ExporterFactory;
import ru.newsaggregator.model.Article;
import ru.newsaggregator.service.SearchCriteria;
import ru.newsaggregator.service.SearchService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ExportCommand implements Command {

    private final SearchService search;
    private final ExporterFactory exporters;

    public ExportCommand(SearchService search, ExporterFactory exporters) {
        this.search = search;
        this.exporters = exporters;
    }

    @Override
    public String name() {
        return "export";
    }

    @Override
    public String description() {
        return "экспорт в файл (export <csv|json|html> <файл> [фильтры])";
    }

    @Override
    public void execute(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Использование: export <csv|json|html> <файл> [category=.. source=.. ...]");
            System.out.println("Доступные форматы: " + String.join(", ", exporters.supportedFormats()));
            return;
        }
        Exporter exporter = exporters.forFormat(args.get(0));
        Path target = Path.of(args.get(1));
        Map<String, String> options = CriteriaParser.options(args.subList(2, args.size()));
        SearchCriteria criteria = CriteriaParser.fromOptions(options);
        List<Article> articles = search.filter(criteria);

        String content = exporter.render(articles);
        try {
            Files.writeString(target, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Не удалось записать файл: " + e.getMessage());
            return;
        }
        System.out.printf("Экспортировано %d новостей в %s%n", articles.size(), target.toAbsolutePath());
    }
}
