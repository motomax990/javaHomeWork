package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.service.AggregationReport;
import ru.newsaggregator.service.AggregationService;
import ru.newsaggregator.service.UpdateScheduler;

import java.util.List;

public class AutoCommand implements Command {

    private final UpdateScheduler scheduler;
    private final AggregationService aggregation;

    public AutoCommand(UpdateScheduler scheduler, AggregationService aggregation) {
        this.scheduler = scheduler;
        this.aggregation = aggregation;
    }

    @Override
    public String name() {
        return "auto";
    }

    @Override
    public String description() {
        return "автообновление (auto <минут> | auto off)";
    }

    @Override
    public void execute(List<String> args) {
        if (args.isEmpty()) {
            printStatus();
            return;
        }
        if (args.get(0).equalsIgnoreCase("off")) {
            scheduler.stop();
            System.out.println("Автообновление выключено.");
            return;
        }
        int minutes;
        try {
            minutes = Integer.parseInt(args.get(0));
        } catch (NumberFormatException e) {
            System.out.println("Интервал должен быть числом минут.");
            return;
        }
        if (minutes <= 0) {
            System.out.println("Интервал должен быть больше нуля.");
            return;
        }
        scheduler.start(minutes, this::runUpdate);
        System.out.println("Автообновление включено, интервал " + minutes + " мин.");
    }

    private void runUpdate() {
        AggregationReport report = aggregation.aggregate();
        System.out.println("[Автообновление] добавлено новых: " + report.getAdded());
    }

    private void printStatus() {
        if (scheduler.isRunning()) {
            System.out.println("Автообновление включено, интервал " + scheduler.getIntervalMinutes() + " мин.");
        } else {
            System.out.println("Автообновление выключено.");
        }
    }
}
