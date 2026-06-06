package ru.newsaggregator.cli;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApplication {

    private final CommandRegistry registry;
    private volatile boolean running = true;

    public ConsoleApplication(CommandRegistry registry) {
        this.registry = registry;
    }

    public void run() {
        printBanner();
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            while (running && scanner.hasNextLine()) {
                String line = scanner.nextLine().replace("﻿", "").trim();
                if (line.isEmpty()) {
                    continue;
                }
                dispatch(tokenize(line));
            }
        }
    }

    public void stop() {
        running = false;
    }

    private void dispatch(List<String> tokens) {
        String name = tokens.get(0).toLowerCase();
        List<String> args = tokens.subList(1, tokens.size());
        registry.find(name).ifPresentOrElse(
                command -> execute(command, args),
                () -> System.out.println("Неизвестная команда: " + name + ". Введите help для списка команд."));
    }

    private void execute(Command command, List<String> args) {
        try {
            command.execute(args);
        } catch (RuntimeException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private List<String> tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean quoted = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                quoted = !quoted;
            } else if (c == ' ' && !quoted) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        return tokens;
    }

    private void printBanner() {
        System.out.println("Автоматизированный агрегатор новостей");
        System.out.println("Введите help, чтобы увидеть доступные команды.");
        System.out.println();
    }
}
