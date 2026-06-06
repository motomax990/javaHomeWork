package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.service.AggregationReport;
import ru.newsaggregator.service.AggregationService;

import java.util.List;

public class UpdateCommand implements Command {

    private final AggregationService aggregation;

    public UpdateCommand(AggregationService aggregation) {
        this.aggregation = aggregation;
    }

    @Override
    public String name() {
        return "update";
    }

    @Override
    public String description() {
        return "загрузить свежие новости из всех источников";
    }

    @Override
    public void execute(List<String> args) {
        System.out.println("Обновление новостной ленты...");
        AggregationReport report = aggregation.aggregate();
        System.out.printf("Обработано: %d, добавлено новых: %d%n",
                report.getProcessed(), report.getAdded());
        if (!report.getErrors().isEmpty()) {
            System.out.println("Источники с ошибками:");
            for (String message : report.errorMessages()) {
                System.out.println("  " + message);
            }
        }
    }
}
