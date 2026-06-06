package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.cli.CommandRegistry;

import java.util.List;

public class HelpCommand implements Command {

    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "показать список команд";
    }

    @Override
    public void execute(List<String> args) {
        System.out.println("Доступные команды:");
        for (Command command : registry.all()) {
            System.out.printf("  %-12s — %s%n", command.name(), command.description());
        }
    }
}
