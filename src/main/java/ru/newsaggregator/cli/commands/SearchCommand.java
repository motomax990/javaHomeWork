package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.ArticleView;
import ru.newsaggregator.cli.Command;
import ru.newsaggregator.service.SearchService;

import java.util.List;

public class SearchCommand implements Command {

    private final SearchService search;

    public SearchCommand(SearchService search) {
        this.search = search;
    }

    @Override
    public String name() {
        return "search";
    }

    @Override
    public String description() {
        return "поиск по заголовку и тексту (search <запрос>)";
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Укажите поисковый запрос: search <текст>");
            return;
        }
        String query = String.join(" ", args);
        ArticleView.printList(search.search(query));
    }
}
