package ru.newsaggregator.cli.commands;

import ru.newsaggregator.cli.Command;
import ru.newsaggregator.model.ChangeRecord;
import ru.newsaggregator.storage.HistoryRepository;
import ru.newsaggregator.util.Time;

import java.util.List;

public class HistoryCommand implements Command {

    private final HistoryRepository history;

    public HistoryCommand(HistoryRepository history) {
        this.history = history;
    }

    @Override
    public String name() {
        return "history";
    }

    @Override
    public String description() {
        return "история изменений ленты (history [количество])";
    }

    @Override
    public void execute(List<String> args) {
        int limit = args.isEmpty() ? 20 : parseLimit(args.get(0));
        List<ChangeRecord> records = history.recent(limit);
        if (records.isEmpty()) {
            System.out.println("История пуста.");
            return;
        }
        for (ChangeRecord record : records) {
            System.out.printf("%s  новость %s: %s%n",
                    Time.display(record.getAt()),
                    record.getType().label(),
                    record.getTitle());
        }
    }

    private int parseLimit(String value) {
        try {
            return Math.max(1, Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return 20;
        }
    }
}
