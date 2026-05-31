package orm.repository;

import orm.core.ColumnMetadata;
import orm.core.EntityMetadata;
import orm.core.OrmException;
import orm.core.SqlTypeMapper;
import orm.core.ValidationException;
import orm.core.Validator;
import orm.core.Violation;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityManager {

    private final Connection connection;

    public EntityManager(Connection connection) {
        this.connection = connection;
    }

    public void createTable(Class<?> clazz) {
        EntityMetadata meta = EntityMetadata.of(clazz);
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sql.append(meta.getTableName()).append(" (");
        sql.append(meta.getIdColumn().getColumnName()).append(" BIGINT AUTO_INCREMENT PRIMARY KEY");
        for (ColumnMetadata col : meta.getDataColumns()) {
            sql.append(", ").append(col.getColumnName()).append(' ')
                    .append(SqlTypeMapper.toSqlType(col.getJavaType()));
            if (!col.isNullable()) {
                sql.append(" NOT NULL");
            }
        }
        sql.append(')');
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql.toString());
        } catch (SQLException e) {
            throw new OrmException("Не удалось создать таблицу " + meta.getTableName(), e);
        }
    }

    public Long save(Object entity) {
        if (entity == null) throw new OrmException("Нельзя сохранить null");
        validateOrThrow(entity);
        EntityMetadata meta = EntityMetadata.of(entity.getClass());
        checkNullableConstraints(meta, entity);

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(meta.getTableName()).append(" (");
        StringBuilder placeholders = new StringBuilder();
        List<ColumnMetadata> dataCols = meta.getDataColumns();
        for (int i = 0; i < dataCols.size(); i++) {
            if (i > 0) {
                sql.append(", ");
                placeholders.append(", ");
            }
            sql.append(dataCols.get(i).getColumnName());
            placeholders.append('?');
        }
        sql.append(") VALUES (").append(placeholders).append(')');

        try (PreparedStatement ps = connection.prepareStatement(
                sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < dataCols.size(); i++) {
                Object value = readField(dataCols.get(i), entity);
                bindValue(ps, i + 1, value, dataCols.get(i).getJavaType());
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    writeField(meta.getIdColumn(), entity, id);
                    return id;
                }
                throw new OrmException("База не вернула сгенерированный id");
            }
        } catch (SQLException e) {
            throw new OrmException("Ошибка при сохранении объекта", e);
        }
    }

    public <T> Optional<T> findById(Class<T> clazz, Long id) {
        EntityMetadata meta = EntityMetadata.of(clazz);
        String sql = buildSelectAll(meta) + " WHERE " + meta.getIdColumn().getColumnName() + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(meta, rs, clazz));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new OrmException("Ошибка при поиске по id", e);
        }
    }

    public <T> List<T> findAll(Class<T> clazz) {
        EntityMetadata meta = EntityMetadata.of(clazz);
        String sql = buildSelectAll(meta);
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(map(meta, rs, clazz));
            }
            return result;
        } catch (SQLException e) {
            throw new OrmException("Ошибка при чтении всех строк", e);
        }
    }

    public int update(Object entity) {
        if (entity == null) throw new OrmException("Нельзя обновить null");
        validateOrThrow(entity);
        EntityMetadata meta = EntityMetadata.of(entity.getClass());
        checkNullableConstraints(meta, entity);

        List<ColumnMetadata> dataCols = meta.getDataColumns();
        StringBuilder sql = new StringBuilder("UPDATE ").append(meta.getTableName()).append(" SET ");
        for (int i = 0; i < dataCols.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(dataCols.get(i).getColumnName()).append(" = ?");
        }
        sql.append(" WHERE ").append(meta.getIdColumn().getColumnName()).append(" = ?");

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (ColumnMetadata col : dataCols) {
                bindValue(ps, idx++, readField(col, entity), col.getJavaType());
            }
            Object idValue = readField(meta.getIdColumn(), entity);
            if (idValue == null) throw new OrmException("Нельзя обновить объект без id");
            ps.setLong(idx, ((Number) idValue).longValue());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new OrmException("Ошибка при обновлении", e);
        }
    }

    public int delete(Object entity) {
        if (entity == null) throw new OrmException("Нельзя удалить null");
        EntityMetadata meta = EntityMetadata.of(entity.getClass());
        Object idValue = readField(meta.getIdColumn(), entity);
        if (idValue == null) throw new OrmException("Нельзя удалить объект без id");
        return deleteByIdInternal(meta, ((Number) idValue).longValue());
    }

    public int deleteById(Class<?> clazz, Long id) {
        EntityMetadata meta = EntityMetadata.of(clazz);
        return deleteByIdInternal(meta, id);
    }

    private int deleteByIdInternal(EntityMetadata meta, long id) {
        String sql = "DELETE FROM " + meta.getTableName() + " WHERE "
                + meta.getIdColumn().getColumnName() + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new OrmException("Ошибка при удалении", e);
        }
    }

    public void saveAll(List<?> entities) {
        if (entities == null) throw new OrmException("Список не должен быть null");
        if (entities.isEmpty()) return;

        Class<?> firstClass = null;
        for (Object e : entities) {
            if (e == null) throw new OrmException("Элементы списка не должны быть null");
            if (firstClass == null) firstClass = e.getClass();
            else if (e.getClass() != firstClass) {
                throw new OrmException("Все элементы должны быть одного класса");
            }
        }

        EntityMetadata meta = EntityMetadata.of(firstClass);
        List<Violation> all = new ArrayList<>();
        for (Object e : entities) {
            all.addAll(Validator.validate(e));
        }
        if (!all.isEmpty()) throw new ValidationException(all);

        for (Object e : entities) {
            checkNullableConstraints(meta, e);
        }

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(meta.getTableName()).append(" (");
        StringBuilder placeholders = new StringBuilder();
        List<ColumnMetadata> dataCols = meta.getDataColumns();
        for (int i = 0; i < dataCols.size(); i++) {
            if (i > 0) {
                sql.append(", ");
                placeholders.append(", ");
            }
            sql.append(dataCols.get(i).getColumnName());
            placeholders.append('?');
        }
        sql.append(") VALUES (").append(placeholders).append(')');

        boolean previousAutoCommit;
        try {
            previousAutoCommit = connection.getAutoCommit();
        } catch (SQLException ex) {
            throw new OrmException("Не удалось получить autoCommit", ex);
        }

        try (PreparedStatement ps = connection.prepareStatement(
                sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            for (Object e : entities) {
                for (int i = 0; i < dataCols.size(); i++) {
                    bindValue(ps, i + 1, readField(dataCols.get(i), e), dataCols.get(i).getJavaType());
                }
                ps.addBatch();
            }
            ps.executeBatch();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                int idx = 0;
                while (keys.next() && idx < entities.size()) {
                    writeField(meta.getIdColumn(), entities.get(idx), keys.getLong(1));
                    idx++;
                }
            }
            connection.commit();
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                ex.addSuppressed(rollbackEx);
            }
            throw new OrmException("Ошибка batch-вставки, транзакция откатилась", ex);
        } finally {
            try {
                connection.setAutoCommit(previousAutoCommit);
            } catch (SQLException ignore) {
            }
        }
    }

    public <T> List<T> findAllWhere(Class<T> clazz, String fieldName, Object value) {
        EntityMetadata meta = EntityMetadata.of(clazz);
        ColumnMetadata col = requireField(meta, fieldName, value);
        String sql = buildSelectAll(meta) + " WHERE " + col.getColumnName() + " = ?";
        List<T> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindValue(ps, 1, value, col.getJavaType());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(meta, rs, clazz));
                return result;
            }
        } catch (SQLException e) {
            throw new OrmException("Ошибка при findAllWhere", e);
        }
    }

    public <T> Optional<T> findOneWhere(Class<T> clazz, String fieldName, Object value) {
        EntityMetadata meta = EntityMetadata.of(clazz);
        ColumnMetadata col = requireField(meta, fieldName, value);
        String sql = buildSelectAll(meta) + " WHERE " + col.getColumnName() + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            bindValue(ps, 1, value, col.getJavaType());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                T first = map(meta, rs, clazz);
                if (rs.next()) {
                    throw new OrmException("findOneWhere нашёл больше одной строки для "
                            + fieldName + " = " + value);
                }
                return Optional.of(first);
            }
        } catch (SQLException e) {
            throw new OrmException("Ошибка при findOneWhere", e);
        }
    }

    public long count(Class<?> clazz) {
        EntityMetadata meta = EntityMetadata.of(clazz);
        String sql = "SELECT COUNT(*) FROM " + meta.getTableName();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
            return 0L;
        } catch (SQLException e) {
            throw new OrmException("Ошибка при count", e);
        }
    }

    public boolean existsById(Class<?> clazz, Long id) {
        EntityMetadata meta = EntityMetadata.of(clazz);
        String sql = "SELECT 1 FROM " + meta.getTableName() + " WHERE "
                + meta.getIdColumn().getColumnName() + " = ? LIMIT 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new OrmException("Ошибка при existsById", e);
        }
    }

    private ColumnMetadata requireField(EntityMetadata meta, String fieldName, Object value) {
        ColumnMetadata col = meta.findColumnByFieldName(fieldName);
        if (col == null) {
            throw new OrmException("Поле " + fieldName + " не найдено в "
                    + meta.getEntityClass().getSimpleName());
        }
        if (value != null && !isCompatible(col.getJavaType(), value)) {
            throw new OrmException("Тип значения " + value.getClass().getSimpleName()
                    + " не совместим с полем " + fieldName);
        }
        return col;
    }

    private boolean isCompatible(Class<?> fieldType, Object value) {
        if (fieldType == long.class || fieldType == Long.class) return value instanceof Long || value instanceof Integer;
        if (fieldType == int.class || fieldType == Integer.class) return value instanceof Integer;
        if (fieldType == double.class || fieldType == Double.class) return value instanceof Double || value instanceof Float;
        if (fieldType == boolean.class || fieldType == Boolean.class) return value instanceof Boolean;
        if (fieldType == String.class) return value instanceof String;
        if (fieldType == LocalDate.class) return value instanceof LocalDate;
        return fieldType.isInstance(value);
    }

    private String buildSelectAll(EntityMetadata meta) {
        StringBuilder sql = new StringBuilder("SELECT ");
        List<ColumnMetadata> all = meta.getAllColumns();
        for (int i = 0; i < all.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(all.get(i).getColumnName());
        }
        sql.append(" FROM ").append(meta.getTableName());
        return sql.toString();
    }

    private <T> T map(EntityMetadata meta, ResultSet rs, Class<T> clazz) {
        try {
            T entity = clazz.getDeclaredConstructor().newInstance();
            for (ColumnMetadata col : meta.getAllColumns()) {
                Object value = readResultSet(rs, col);
                writeField(col, entity, value);
            }
            return entity;
        } catch (ReflectiveOperationException e) {
            throw new OrmException("Не удалось создать объект " + clazz.getName()
                    + " (нужен конструктор без аргументов)", e);
        } catch (SQLException e) {
            throw new OrmException("Ошибка чтения ResultSet", e);
        }
    }

    private Object readResultSet(ResultSet rs, ColumnMetadata col) throws SQLException {
        Class<?> type = col.getJavaType();
        String name = col.getColumnName();
        if (type == long.class) return rs.getLong(name);
        if (type == Long.class) {
            long v = rs.getLong(name);
            return rs.wasNull() ? null : v;
        }
        if (type == int.class) return rs.getInt(name);
        if (type == Integer.class) {
            int v = rs.getInt(name);
            return rs.wasNull() ? null : v;
        }
        if (type == double.class) return rs.getDouble(name);
        if (type == Double.class) {
            double v = rs.getDouble(name);
            return rs.wasNull() ? null : v;
        }
        if (type == boolean.class) return rs.getBoolean(name);
        if (type == Boolean.class) {
            boolean v = rs.getBoolean(name);
            return rs.wasNull() ? null : v;
        }
        if (type == String.class) return rs.getString(name);
        if (type == LocalDate.class) {
            Date d = rs.getDate(name);
            return d == null ? null : d.toLocalDate();
        }
        throw new OrmException("Неподдерживаемый тип при чтении: " + type.getName());
    }

    private void bindValue(PreparedStatement ps, int idx, Object value, Class<?> type) throws SQLException {
        if (value == null) {
            ps.setNull(idx, mapSqlType(type));
            return;
        }
        if (type == LocalDate.class) {
            ps.setDate(idx, Date.valueOf((LocalDate) value));
        } else if (type == long.class || type == Long.class) {
            ps.setLong(idx, ((Number) value).longValue());
        } else if (type == int.class || type == Integer.class) {
            ps.setInt(idx, ((Number) value).intValue());
        } else if (type == double.class || type == Double.class) {
            ps.setDouble(idx, ((Number) value).doubleValue());
        } else if (type == boolean.class || type == Boolean.class) {
            ps.setBoolean(idx, (Boolean) value);
        } else if (type == String.class) {
            ps.setString(idx, (String) value);
        } else {
            ps.setObject(idx, value);
        }
    }

    private int mapSqlType(Class<?> type) {
        if (type == Long.class || type == long.class) return Types.BIGINT;
        if (type == Integer.class || type == int.class) return Types.INTEGER;
        if (type == Double.class || type == double.class) return Types.DOUBLE;
        if (type == Boolean.class || type == boolean.class) return Types.BOOLEAN;
        if (type == String.class) return Types.VARCHAR;
        if (type == LocalDate.class) return Types.DATE;
        return Types.OTHER;
    }

    private Object readField(ColumnMetadata col, Object entity) {
        try {
            return col.getField().get(entity);
        } catch (IllegalAccessException e) {
            throw new OrmException("Не удалось прочитать поле " + col.getFieldName(), e);
        }
    }

    private void writeField(ColumnMetadata col, Object entity, Object value) {
        try {
            col.getField().set(entity, value);
        } catch (IllegalAccessException e) {
            throw new OrmException("Не удалось записать поле " + col.getFieldName(), e);
        }
    }

    private void validateOrThrow(Object entity) {
        List<Violation> violations = Validator.validate(entity);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

    private void checkNullableConstraints(EntityMetadata meta, Object entity) {
        for (ColumnMetadata col : meta.getDataColumns()) {
            if (!col.isNullable() && readField(col, entity) == null) {
                throw new OrmException(
                        "Поле " + col.getFieldName() + " имеет nullable = false, но значение null"
                );
            }
        }
    }
}
