package ru.newsaggregator.storage.sqlite;

import ru.newsaggregator.model.ChangeRecord;
import ru.newsaggregator.model.ChangeType;
import ru.newsaggregator.storage.Database;
import ru.newsaggregator.storage.HistoryRepository;
import ru.newsaggregator.storage.StorageException;
import ru.newsaggregator.util.Time;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteHistoryRepository implements HistoryRepository {

    private final Connection connection;

    public SqliteHistoryRepository(Database database) {
        this.connection = database.connection();
    }

    @Override
    public void record(ChangeRecord record) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO history (type, title, link, at) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, record.getType().name());
            ps.setString(2, record.getTitle());
            ps.setString(3, record.getLink());
            ps.setString(4, Time.store(record.getAt()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Не удалось записать историю: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ChangeRecord> recent(int limit) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM history ORDER BY at DESC, id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                List<ChangeRecord> result = new ArrayList<>();
                while (rs.next()) {
                    ChangeRecord record = new ChangeRecord();
                    record.setId(rs.getLong("id"));
                    record.setType(ChangeType.valueOf(rs.getString("type")));
                    record.setTitle(rs.getString("title"));
                    record.setLink(rs.getString("link"));
                    record.setAt(Time.parse(rs.getString("at")));
                    result.add(record);
                }
                return result;
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка чтения истории: " + e.getMessage(), e);
        }
    }
}
