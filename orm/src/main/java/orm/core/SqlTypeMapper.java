package orm.core;

import java.time.LocalDate;

public final class SqlTypeMapper {

    private SqlTypeMapper() {
    }

    public static String toSqlType(Class<?> javaType) {
        if (javaType == Long.class || javaType == long.class) return "BIGINT";
        if (javaType == Integer.class || javaType == int.class) return "INT";
        if (javaType == Double.class || javaType == double.class) return "DOUBLE";
        if (javaType == Boolean.class || javaType == boolean.class) return "BOOLEAN";
        if (javaType == String.class) return "VARCHAR(255)";
        if (javaType == LocalDate.class) return "DATE";
        throw new OrmException("Неподдерживаемый Java-тип: " + javaType.getName());
    }
}
