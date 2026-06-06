package ru.newsaggregator.cli;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CommandRegistry {

    private final Map<String, Command> commands = new LinkedHashMap<>();

    public void register(Command command) {
        commands.put(command.name(), command);
    }

    public Optional<Command> find(String name) {
        return Optional.ofNullable(commands.get(name));
    }

    public Collection<Command> all() {
        return commands.values();
    }
}
