package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.ArticleView;
import ru.newsaggregator.cli.Command;
import ru.newsaggregator.storage.ArticleRepository;

import java.util.List;

public class ShowCommand implements Command {

    private final ArticleRepository articles;

    public ShowCommand(ArticleRepository articles) {
        this.articles = articles;
    }

    @Override
    public String name() {
        return "show";
    }

    @Override
    public String description() {
        return "показать новость целиком (show <id>)";
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Укажите идентификатор: show <id>");
            return;
        }
        long id;
        try {
            id = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            System.out.println("Идентификатор должен быть числом.");
            return;
        }
        articles.findById(id).ifPresentOrElse(
                ArticleView::printDetails,
                () -> System.out.println("Новость с id " + id + " не найдена."));
    }
}
