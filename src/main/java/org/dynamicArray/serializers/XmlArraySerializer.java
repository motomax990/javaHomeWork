package org.dynamicArray.serializers;

import org.dynamicArray.DynamicArray;
import org.dynamicArray.DynamicArrayList;

public class XmlArraySerializer implements ArraySerializer {
    @Override
    public String serialize(DynamicArray<?> array) {
        StringBuilder sb = new StringBuilder("<array>");
        for (int i = 0; i < array.size(); i++) {
            sb.append("<item>").append(escapeXml(array.get(i).toString())).append("</item>");
        }
        sb.append("</array>");
        return sb.toString();
    }

    private String escapeXml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    @Override
    public DynamicArray<?> deserialize(String data) {
        // Очень простой парсер: ищем теги <item>...</item>
        DynamicArray<Object> result = new DynamicArrayList<>(Object.class);
        int pos = 0;
        while (true) {
            int start = data.indexOf("<item>", pos);
            if (start < 0) break;
            int end = data.indexOf("</item>", start);
            if (end < 0) break;
            String item = data.substring(start + 6, end);
            result.add(unescapeXml(item));
            pos = end + 7;
        }
        return result;
    }

    private String unescapeXml(String s) {
        return s.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }
}