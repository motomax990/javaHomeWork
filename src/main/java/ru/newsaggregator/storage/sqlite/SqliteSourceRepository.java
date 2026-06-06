package ru.newsaggregator.storage.sqlite;

import ru.newsaggregator.model.Source;
import ru.newsaggregator.storage.Database;
import ru.newsaggregator.storage.SourceRepository;
import ru.newsaggregator.storage.StorageException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqliteSourceRepository implements SourceRepository {

    private final Connection connection;

    public SqliteSourceRepository(Database database) {
        this.connection = database.connection();
    }

    @Override
    public void add(Source source) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO sources (name, url, enabled) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, source.getName());
            ps.setString(2, source.getUrl());
            ps.setInt(3, source.isEnabled() ? 1 : 0);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    source.setId(keys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Не удалось добавить источник: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Source> findAll() {
        return query("SELECT * FROM sources ORDER BY name");
    }

    @Override
    public List<Source> findEnabled() {
        return query("SELECT * FROM sources WHERE enabled = 1 ORDER BY name");
    }

    @Override
    public boolean existsByUrl(String url) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM sources WHERE url = ?")) {
            ps.setString(1, url);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка проверки источника: " + e.getMessage(), e);
        }
    }

    private List<Source> query(String sql) {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Source> result = new ArrayList<>();
            while (rs.next()) {
                Source source = new Source();
                source.setId(rs.getLong("id"));
                source.setName(rs.getString("name"));
                source.setUrl(rs.getString("url"));
                source.setEnabled(rs.getInt("enabled") == 1);
                result.add(source);
            }
            return result;
        } catch (SQLException e) {
            throw new StorageException("Ошибка чтения источников: " + e.getMessage(), e);
        }
    }
}
