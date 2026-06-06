package ru.newsaggregator.cli;

import java.util.List;

public interface Command {

    String name();

    String description();

    void execute(List<String> args);
}
