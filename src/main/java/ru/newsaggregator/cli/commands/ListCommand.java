package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.ArticleView;
import ru.newsaggregator.cli.Command;
import ru.newsaggregator.model.Article;
import ru.newsaggregator.storage.ArticleRepository;

import java.util.List;

public class ListCommand implements Command {

    private final ArticleRepository articles;

    public ListCommand(ArticleRepository articles) {
        this.articles = articles;
    }

    @Override
    public String name() {
        return "list";
    }

    @Override
    public String description() {
        return "показать последние новости (list [количество])";
    }

    @Override
    public void execute(List<String> args) {
        int limit = args.isEmpty() ? 20 : parseLimit(args.get(0));
        List<Article> all = articles.findAll();
        ArticleView.printList(all.subList(0, Math.min(limit, all.size())));
    }

    private int parseLimit(String value) {
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return 20;
        }
    }
}
