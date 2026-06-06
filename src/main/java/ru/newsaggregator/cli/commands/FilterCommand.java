package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.ArticleView;
import ru.newsaggregator.cli.Command;
import ru.newsaggregator.cli.CriteriaParser;
import ru.newsaggregator.service.SearchCriteria;
import ru.newsaggregator.service.SearchService;

import java.util.List;
import java.util.Map;

public class FilterCommand implements Command {

    private final SearchService search;

    public FilterCommand(SearchService search) {
        this.search = search;
    }

    @Override
    public String name() {
        return "filter";
    }

    @Override
    public String description() {
        return "фильтр: filter category=.. source=.. keyword=.. from=ГГГГ-ММ-ДД to=ГГГГ-ММ-ДД sort=..";
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Укажите хотя бы один параметр, например: filter category=Спорт");
            return;
        }
        Map<String, String> options = CriteriaParser.options(args);
        SearchCriteria criteria = CriteriaParser.fromOptions(options);
        ArticleView.printList(search.filter(criteria));
    }
}
