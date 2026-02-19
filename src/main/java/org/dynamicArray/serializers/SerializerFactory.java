package org.dynamicArray.serializers;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {
    private static final Map<String, ArraySerializer> SERIALIZERS = new HashMap<>();

    static {
        SERIALIZERS.put("json", new JsonArraySerializer());
        SERIALIZERS.put("xml", new XmlArraySerializer());
        SERIALIZERS.put("csv", new CsvArraySerializer());
        SERIALIZERS.put("bin", new BinaryArraySerializer());
    }

    public static ArraySerializer forFormat(String filename) {
        String ext = getExtension(filename).toLowerCase();
        ArraySerializer serializer = SERIALIZERS.get(ext);
        if (serializer == null) {
            throw new IllegalArgumentException("Unsupported format: " + ext);
        }
        return serializer;
    }

    private static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        return (dot == -1) ? "" : filename.substring(dot + 1);
    }
}