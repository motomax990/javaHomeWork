package ru.newsaggregator.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AggregationReport {

    private int added;
    private int processed;
    private final Map<String, String> errors = new LinkedHashMap<>();

    public void countAdded() {
        added++;
    }

    public void countProcessed() {
        processed++;
    }

    public void addError(String source, String message) {
        errors.put(source, message);
    }

    public int getAdded() {
        return added;
    }

    public int getProcessed() {
        return processed;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public List<String> errorMessages() {
        List<String> messages = new ArrayList<>();
        errors.forEach((source, message) -> messages.add(source + ": " + message));
        return messages;
    }
}
