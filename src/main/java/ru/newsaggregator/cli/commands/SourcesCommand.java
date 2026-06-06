package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.model.Source;
import ru.newsaggregator.storage.SourceRepository;

import java.util.List;

public class SourcesCommand implements Command {

    private final SourceRepository sources;

    public SourcesCommand(SourceRepository sources) {
        this.sources = sources;
    }

    @Override
    public String name() {
        return "sources";
    }

    @Override
    public String description() {
        return "список источников новостей";
    }

    @Override
    public void execute(List<String> args) {
        List<Source> all = sources.findAll();
        if (all.isEmpty()) {
            System.out.println("Источники не настроены.");
            return;
        }
        System.out.println("Источники:");
        for (Source source : all) {
            System.out.printf("  [%s] %s%n      %s%n",
                    source.isEnabled() ? "вкл" : "выкл",
                    source.getName(),
                    source.getUrl());
        }
    }
}
