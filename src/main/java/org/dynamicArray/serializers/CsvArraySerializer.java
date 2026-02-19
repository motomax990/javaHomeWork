package org.dynamicArray.serializers;

import org.dynamicArray.DynamicArray;
import org.dynamicArray.DynamicArrayList;

public class CsvArraySerializer implements ArraySerializer {
    @Override
    public String serialize(DynamicArray<?> array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) sb.append(",");
            Object elem = array.get(i);
            String str = elem.toString();
            if (str.contains(",") || str.contains("\"")) {
                str = "\"" + str.replace("\"", "\"\"") + "\"";
            }
            sb.append(str);
        }
        return sb.toString();
    }

    @Override
    public DynamicArray<?> deserialize(String data) {
        // Простейший CSV парсер (не учитывает сложные случаи вроде кавычек внутри)
        DynamicArray<Object> result = new DynamicArrayList<>(Object.class);
        if (data.trim().isEmpty()) return result;
        String[] items = data.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)"); // regex для кавычек
        for (String item : items) {
            String val = item.trim();
            if (val.startsWith("\"") && val.endsWith("\"")) {
                val = val.substring(1, val.length() - 1).replace("\"\"", "\"");
            }
            result.add(val);
        }
        return result;
    }
}