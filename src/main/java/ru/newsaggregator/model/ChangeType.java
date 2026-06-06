package ru.newsaggregator.model;

public enum ChangeType {
    ADDED("добавлена"),
    REMOVED("удалена");

    private final String label;

    ChangeType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
