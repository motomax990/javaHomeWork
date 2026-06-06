package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.service.AggregationService;

import java.util.List;

public class CleanupCommand implements Command {

    private final AggregationService aggregation;

    public CleanupCommand(AggregationService aggregation) {
        this.aggregation = aggregation;
    }

    @Override
    public String name() {
        return "cleanup";
    }

    @Override
    public String description() {
        return "удалить новости старше N дней (cleanup <дней>)";
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            System.out.println("Использование: cleanup <количество дней>");
            return;
        }
        int days;
        try {
            days = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            System.out.println("Количество дней должно быть числом.");
            return;
        }
        int removed = aggregation.cleanup(days);
        System.out.println("Удалено устаревших новостей: " + removed);
    }
}
