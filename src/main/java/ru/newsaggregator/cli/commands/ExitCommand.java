package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.cli.ConsoleApplication;
import ru.newsaggregator.service.UpdateScheduler;

import java.util.List;

public class ExitCommand implements Command {

    private final ConsoleApplication application;
    private final UpdateScheduler scheduler;

    public ExitCommand(ConsoleApplication application, UpdateScheduler scheduler) {
        this.application = application;
        this.scheduler = scheduler;
    }

    @Override
    public String name() {
        return "exit";
    }

    @Override
    public String description() {
        return "выйти из программы";
    }

    @Override
    public void execute(List<String> args) {
        scheduler.shutdown();
        application.stop();
        System.out.println("Завершение работы.");
    }
}
