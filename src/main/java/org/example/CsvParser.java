package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class CsvParser {

    public <T> List<T> parseFromCsv(String filename, Class<T> klass) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(openInput(filename), StandardCharsets.UTF_8))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                return new ArrayList<>();
            }
            String[] headers = headerLine.split(",", -1);

            Map<String, List<Field>> headerMap = new LinkedHashMap<>();
            collectLeafFields(klass, new ArrayList<>(), "", headerMap);

            List<T> result = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] values = line.split(",", -1);
                T obj = klass.getDeclaredConstructor().newInstance();
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    List<Field> chain = headerMap.get(headers[i]);
                    if (chain == null) {
                        continue;
                    }
                    setValueByChain(obj, chain, values[i]);
                }
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка чтения CSV: " + filename, e);
        }
    }

    public <T> void saveToCsv(String filename, Collection<T> collection, Class<T> klass) {
        Map<String, List<Field>> headerMap = new LinkedHashMap<>();
        collectLeafFields(klass, new ArrayList<>(), "", headerMap);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(openOutput(filename), StandardCharsets.UTF_8))) {

            writer.write(String.join(",", headerMap.keySet()));
            writer.newLine();

            for (T obj : collection) {
                List<String> row = new ArrayList<>();
                for (List<Field> chain : headerMap.values()) {
                    row.add(extractValueByChain(obj, chain));
                }
                writer.write(String.join(",", row));
                writer.newLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка записи CSV: " + filename, e);
        }
    }

    private InputStream openInput(String filename) throws IOException {
        if (filename.endsWith(".zip")) {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(filename));
            zis.getNextEntry();
            return zis;
        }
        if (filename.endsWith(".gz")) {
            return new GZIPInputStream(new FileInputStream(filename));
        }
        return new FileInputStream(filename);
    }

    private OutputStream openOutput(String filename) throws IOException {
        if (filename.endsWith(".zip")) {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(filename));
            String base = new File(filename).getName();
            base = base.substring(0, base.length() - 4) + ".csv";
            zos.putNextEntry(new ZipEntry(base));
            return zos;
        }
        if (filename.endsWith(".gz")) {
            return new GZIPOutputStream(new FileOutputStream(filename));
        }
        return new FileOutputStream(filename);
    }

    private void collectLeafFields(Class<?> klass, List<Field> path, String prefix,
                                   Map<String, List<Field>> out) {
        for (Field field : klass.getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.isAnnotationPresent(CsvName.class)
                    ? field.getAnnotation(CsvName.class).value()
                    : field.getName();
            String header = prefix.isEmpty() ? name : prefix + "." + name;
            Class<?> type = field.getType();

            if (isLeafType(type)) {
                List<Field> chain = new ArrayList<>(path);
                chain.add(field);
                out.put(header, chain);
            } else {
                List<Field> next = new ArrayList<>(path);
                next.add(field);
                collectLeafFields(type, next, header, out);
            }
        }
    }

    private boolean isLeafType(Class<?> type) {
        if (type.isPrimitive()) return true;
        if (type == String.class) return true;
        if (type == Integer.class || type == Long.class || type == Double.class || type == Boolean.class) return true;
        if (List.class.isAssignableFrom(type)) return true;
        return false;
    }

    private void setValueByChain(Object root, List<Field> chain, String raw) throws Exception {
        Object current = root;
        for (int i = 0; i < chain.size() - 1; i++) {
            Field f = chain.get(i);
            Object next = f.get(current);
            if (next == null) {
                next = f.getType().getDeclaredConstructor().newInstance();
                f.set(current, next);
            }
            current = next;
        }
        Field leaf = chain.get(chain.size() - 1);
        leaf.set(current, convertValue(raw, leaf));
    }

    private String extractValueByChain(Object root, List<Field> chain) throws Exception {
        Object current = root;
        for (int i = 0; i < chain.size() - 1; i++) {
            Field f = chain.get(i);
            current = f.get(current);
            if (current == null) {
                return "";
            }
        }
        Field leaf = chain.get(chain.size() - 1);
        Object value = leaf.get(current);
        if (value == null) {
            return "";
        }
        if (value instanceof List<?>) {
            String delimiter = leaf.isAnnotationPresent(CsvCollection.class)
                    ? leaf.getAnnotation(CsvCollection.class).delimiter()
                    : "|";
            List<String> parts = new ArrayList<>();
            for (Object item : (List<?>) value) {
                parts.add(item == null ? "" : item.toString());
            }
            return String.join(delimiter, parts);
        }
        return value.toString();
    }

    private Object convertValue(String raw, Field field) {
        Class<?> type = field.getType();
        if (List.class.isAssignableFrom(type)) {
            String delimiter = field.isAnnotationPresent(CsvCollection.class)
                    ? field.getAnnotation(CsvCollection.class).delimiter()
                    : "|";
            Class<?> elementType = String.class;
            Type generic = field.getGenericType();
            if (generic instanceof ParameterizedType) {
                Type arg = ((ParameterizedType) generic).getActualTypeArguments()[0];
                if (arg instanceof Class<?>) {
                    elementType = (Class<?>) arg;
                }
            }
            if (raw == null || raw.isEmpty()) {
                return new ArrayList<>();
            }
            String[] parts = raw.split(java.util.regex.Pattern.quote(delimiter), -1);
            List<Object> list = new ArrayList<>();
            for (String p : parts) {
                list.add(parsePrimitive(p, elementType));
            }
            return list;
        }
        return parsePrimitive(raw, type);
    }

    private Object parsePrimitive(String raw, Class<?> type) {
        if (raw == null) {
            return null;
        }
        if (type == String.class) return raw;
        if (raw.isEmpty() && !type.isPrimitive()) return null;
        if (type == int.class || type == Integer.class) return Integer.parseInt(raw);
        if (type == long.class || type == Long.class) return Long.parseLong(raw);
        if (type == double.class || type == Double.class) return Double.parseDouble(raw);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(raw);
        throw new IllegalArgumentException("Неподдерживаемый тип: " + type);
    }
}
