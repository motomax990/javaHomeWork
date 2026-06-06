package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.service.AnalyticsService;

import java.util.List;
import java.util.Map;

public class CategoriesCommand implements Command {

    private final AnalyticsService analytics;

    public CategoriesCommand(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @Override
    public String name() {
        return "categories";
    }

    @Override
    public String description() {
        return "количество новостей по категориям";
    }

    @Override
    public void execute(List<String> args) {
        Map<String, Long> counts = analytics.countByCategory();
        if (counts.isEmpty()) {
            System.out.println("Новостей пока нет.");
            return;
        }
        System.out.println("Новости по категориям:");
        counts.forEach((category, count) -> System.out.printf("  %-16s %d%n", category, count));
    }
}
