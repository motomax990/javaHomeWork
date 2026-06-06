package ru.newsaggregator.storage;

import ru.newsaggregator.model.ChangeRecord;

import java.util.List;

public interface HistoryRepository {

    void record(ChangeRecord record);

    List<ChangeRecord> recent(int limit);
}
