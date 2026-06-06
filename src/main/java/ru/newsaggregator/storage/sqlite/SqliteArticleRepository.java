package ru.newsaggregator.storage.sqlite;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.storage.ArticleRepository;
import ru.newsaggregator.storage.Database;
import ru.newsaggregator.storage.StorageException;
import ru.newsaggregator.util.Time;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SqliteArticleRepository implements ArticleRepository {

    private final Connection connection;

    public SqliteArticleRepository(Database database) {
        this.connection = database.connection();
    }

    @Override
    public synchronized boolean save(Article article) {
        if (existsByLink(article.getLink())) {
            return false;
        }
        String sql = "INSERT INTO articles " +
                "(title, link, source, category, published_at, summary, content, keywords, media_url, fetched_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, article.getTitle());
            ps.setString(2, article.getLink());
            ps.setString(3, article.getSource());
            ps.setString(4, article.getCategory());
            ps.setString(5, article.getPublishedAt() == null ? null : Time.store(article.getPublishedAt()));
            ps.setString(6, article.getSummary());
            ps.setString(7, article.getContent());
            ps.setString(8, String.join(",", article.getKeywords()));
            ps.setString(9, article.getMediaUrl());
            ps.setString(10, article.getFetchedAt() == null ? null : Time.store(article.getFetchedAt()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    article.setId(keys.getLong(1));
                }
            }
            return true;
        } catch (SQLException e) {
            throw new StorageException("Не удалось сохранить новость: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByLink(String link) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM articles WHERE link = ?")) {
            ps.setString(1, link);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка проверки дубликата: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Article> findById(long id) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM articles WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка чтения новости: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Article> findAll() {
        return query("SELECT * FROM articles ORDER BY published_at DESC");
    }

    @Override
    public List<Article> searchByText(String text) {
        String needle = text.toLowerCase();
        return findAll().stream()
                .filter(article -> contains(article.getTitle(), needle) || contains(article.getContent(), needle))
                .collect(Collectors.toList());
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase().contains(needle);
    }

    @Override
    public synchronized List<Article> deleteOlderThan(LocalDateTime threshold) {
        String boundary = Time.store(threshold);
        List<Article> removed = new ArrayList<>();
        try (PreparedStatement select = connection.prepareStatement(
                "SELECT * FROM articles WHERE published_at IS NOT NULL AND published_at < ?")) {
            select.setString(1, boundary);
            try (ResultSet rs = select.executeQuery()) {
                while (rs.next()) {
                    removed.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new StorageException("Ошибка выборки устаревших новостей: " + e.getMessage(), e);
        }
        try (PreparedStatement delete = connection.prepareStatement(
                "DELETE FROM articles WHERE published_at IS NOT NULL AND published_at < ?")) {
            delete.setString(1, boundary);
            delete.executeUpdate();
        } catch (SQLException e) {
            throw new StorageException("Ошибка удаления устаревших новостей: " + e.getMessage(), e);
        }
        return removed;
    }

    @Override
    public long count() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM articles")) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new StorageException("Ошибка подсчёта новостей: " + e.getMessage(), e);
        }
    }

    private List<Article> query(String sql) {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Article> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new StorageException("Ошибка выборки новостей: " + e.getMessage(), e);
        }
    }

    private Article map(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(rs.getLong("id"));
        article.setTitle(rs.getString("title"));
        article.setLink(rs.getString("link"));
        article.setSource(rs.getString("source"));
        article.setCategory(rs.getString("category"));
        String published = rs.getString("published_at");
        if (published != null) {
            article.setPublishedAt(Time.parse(published));
        }
        article.setSummary(rs.getString("summary"));
        article.setContent(rs.getString("content"));
        article.setKeywords(splitKeywords(rs.getString("keywords")));
        article.setMediaUrl(rs.getString("media_url"));
        String fetched = rs.getString("fetched_at");
        if (fetched != null) {
            article.setFetchedAt(Time.parse(fetched));
        }
        return article;
    }

    private List<String> splitKeywords(String raw) {
        if (raw == null || raw.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
