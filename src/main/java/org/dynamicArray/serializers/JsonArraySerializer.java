package org.dynamicArray.serializers;

import org.dynamicArray.DynamicArray;
import org.dynamicArray.DynamicArrayList;
import java.util.stream.IntStream;

public class JsonArraySerializer implements ArraySerializer {
    @Override
    public String serialize(DynamicArray<?> array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) sb.append(",");
            Object elem = array.get(i);
            if (elem instanceof String) {
                sb.append("\"").append(elem.toString().replace("\"", "\\\"")).append("\"");
            } else {
                sb.append(elem);
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public DynamicArray<?> deserialize(String data) {
        // Очень примитивный парсер: убираем [ и ], разбиваем по запятой, обрезаем кавычки
        String trimmed = data.trim();
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            String content = trimmed.substring(1, trimmed.length() - 1).trim();
            if (content.isEmpty()) return new DynamicArrayList<>(Object.class, 0);
            String[] items = content.split(",");
            DynamicArray<Object> result = new DynamicArrayList<>(Object.class, items.length);
            for (String item : items) {
                String val = item.trim();
                if (val.startsWith("\"") && val.endsWith("\"")) {
                    result.add(val.substring(1, val.length() - 1).replace("\\\"", "\""));
                } else {
                    // Попытка интерпретировать как число
                    try {
                        if (val.contains(".")) {
                            result.add(Double.parseDouble(val));
                        } else {
                            result.add(Integer.parseInt(val));
                        }
                    } catch (NumberFormatException e) {
                        result.add(val); // остаётся строкой
                    }
                }
            }
            return result;
        }
        throw new IllegalArgumentException("Invalid JSON array format");
    }
}