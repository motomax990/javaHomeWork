package orm.core;

import java.lang.reflect.Field;

public final class ColumnMetadata {
    private final Field field;
    private final String columnName;
    private final boolean primaryKey;
    private final boolean nullable;

    public ColumnMetadata(Field field, String columnName, boolean primaryKey, boolean nullable) {
        this.field = field;
        this.columnName = columnName;
        this.primaryKey = primaryKey;
        this.nullable = nullable;
        this.field.setAccessible(true);
    }

    public Field getField() {
        return field;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Class<?> getJavaType() {
        return field.getType();
    }

    public String getFieldName() {
        return field.getName();
    }
}
