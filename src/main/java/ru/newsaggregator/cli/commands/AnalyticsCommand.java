package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.service.AnalyticsService;

import java.util.List;
import java.util.Map;

public class AnalyticsCommand implements Command {

    private final AnalyticsService analytics;

    public AnalyticsCommand(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    @Override
    public String name() {
        return "analytics";
    }

    @Override
    public String description() {
        return "сводная статистика по собранным новостям";
    }

    @Override
    public void execute(List<String> args) {
        System.out.println("Всего новостей в базе: " + analytics.total());

        System.out.println();
        System.out.println("По категориям:");
        print(analytics.countByCategory());

        System.out.println();
        System.out.println("По источникам:");
        print(analytics.countBySource());

        System.out.println();
        System.out.println("Самые упоминаемые темы:");
        print(analytics.topKeywords(10));
    }

    private void print(Map<String, Long> counts) {
        if (counts.isEmpty()) {
            System.out.println("  данных нет");
            return;
        }
        counts.forEach((key, value) -> System.out.printf("  %-20s %d%n", key, value));
    }
}
