package ru.newsaggregator.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database implements AutoCloseable {

    private final Connection connection;

    public Database(String jdbcUrl) {
        try {
            this.connection = DriverManager.getConnection(jdbcUrl);
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            }
            createSchema();
        } catch (SQLException e) {
            throw new StorageException("Не удалось открыть базу данных: " + e.getMessage(), e);
        }
    }

    public Connection connection() {
        return connection;
    }

    private void createSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS sources (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        url TEXT NOT NULL UNIQUE,
                        enabled INTEGER NOT NULL DEFAULT 1
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS articles (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        link TEXT NOT NULL UNIQUE,
                        source TEXT,
                        category TEXT,
                        published_at TEXT,
                        summary TEXT,
                        content TEXT,
                        keywords TEXT,
                        media_url TEXT,
                        fetched_at TEXT
                    )
                    """);
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        type TEXT NOT NULL,
                        title TEXT,
                        link TEXT,
                        at TEXT NOT NULL
                    )
                    """);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка при закрытии базы данных: " + e.getMessage(), e);
        }
    }
}
