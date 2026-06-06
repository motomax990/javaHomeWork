package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.model.Source;
import ru.newsaggregator.storage.SourceRepository;

import java.util.List;

public class AddSourceCommand implements Command {

    private final SourceRepository sources;

    public AddSourceCommand(SourceRepository sources) {
        this.sources = sources;
    }

    @Override
    public String name() {
        return "addsource";
    }

    @Override
    public String description() {
        return "добавить RSS-источник (addsource <название> <url>)";
    }

    @Override
    public void execute(List<String> args) {
        if (args.size() < 2) {
            System.out.println("Использование: addsource <название> <url>");
            return;
        }
        String url = args.get(args.size() - 1);
        String title = String.join(" ", args.subList(0, args.size() - 1));
        if (sources.existsByUrl(url)) {
            System.out.println("Такой источник уже добавлен.");
            return;
        }
        sources.add(new Source(title, url));
        System.out.println("Источник добавлен: " + title);
    }
}
