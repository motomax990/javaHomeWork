package orm.core;

import orm.annotation.Column;
import orm.annotation.Id;
import orm.annotation.Table;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class EntityMetadata {

    private static final ConcurrentHashMap<Class<?>, EntityMetadata> CACHE = new ConcurrentHashMap<>();

    private final Class<?> entityClass;
    private final String tableName;
    private final ColumnMetadata idColumn;
    private final List<ColumnMetadata> dataColumns;
    private final List<ColumnMetadata> allColumns;

    private EntityMetadata(Class<?> entityClass) {
        this.entityClass = entityClass;

        Table table = entityClass.getAnnotation(Table.class);
        if (table == null) {
            throw new OrmException(
                    "Класс " + entityClass.getName() + " не помечен @Table"
            );
        }
        this.tableName = table.name().isEmpty()
                ? entityClass.getSimpleName().toLowerCase()
                : table.name();

        ColumnMetadata id = null;
        List<ColumnMetadata> dataCols = new ArrayList<>();

        for (Field field : entityClass.getDeclaredFields()) {
            Id idAnn = field.getAnnotation(Id.class);
            Column columnAnn = field.getAnnotation(Column.class);

            if (idAnn != null) {
                Class<?> type = field.getType();
                if (type != Long.class && type != long.class) {
                    throw new OrmException(
                            "Поле @Id должно иметь тип Long или long: " + field.getName()
                    );
                }
                if (id != null) {
                    throw new OrmException(
                            "Найдено больше одного @Id в классе " + entityClass.getName()
                    );
                }
                String name = columnAnn != null && !columnAnn.name().isEmpty()
                        ? columnAnn.name()
                        : field.getName();
                id = new ColumnMetadata(field, name, true, true);
            } else if (columnAnn != null) {
                String name = columnAnn.name().isEmpty() ? field.getName() : columnAnn.name();
                dataCols.add(new ColumnMetadata(field, name, false, columnAnn.nullable()));
            }
        }

        if (id == null) {
            throw new OrmException(
                    "В классе " + entityClass.getName() + " не найдено поле с @Id"
            );
        }

        this.idColumn = id;
        this.dataColumns = Collections.unmodifiableList(dataCols);

        List<ColumnMetadata> all = new ArrayList<>();
        all.add(id);
        all.addAll(dataCols);
        this.allColumns = Collections.unmodifiableList(all);
    }

    public static EntityMetadata of(Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, EntityMetadata::new);
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public ColumnMetadata getIdColumn() {
        return idColumn;
    }

    public List<ColumnMetadata> getDataColumns() {
        return dataColumns;
    }

    public List<ColumnMetadata> getAllColumns() {
        return allColumns;
    }

    public ColumnMetadata findColumnByFieldName(String fieldName) {
        for (ColumnMetadata col : allColumns) {
            if (col.getFieldName().equals(fieldName)) return col;
        }
        return null;
    }
}
