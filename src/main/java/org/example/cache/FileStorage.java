package org.example.cache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class FileStorage {

    private static final long TTL_MINUTES = 5;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String cacheDir;
    private final String metaFile;
    private final Gson gson = new Gson();

    public FileStorage(String cacheDir) {
        this.cacheDir = cacheDir;
        this.metaFile = cacheDir + File.separator + "cache_meta.json";
        new File(cacheDir).mkdirs();
    }

    public void save(String key, String data) throws IOException {
        String filePath = cacheDir + File.separator + key;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(data);
        }
        updateMeta(key, LocalDateTime.now());
    }

    public String load(String key) throws IOException {
        String filePath = cacheDir + File.separator + key;
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!sb.isEmpty()) {
                    sb.append("\n");
                }
                sb.append(line);
            }
        }
        return sb.toString();
    }

    public boolean isValid(String key) {
        Map<String, String> meta = loadMeta();
        String timestamp = meta.get(key);
        if (timestamp == null) {
            return false;
        }
        LocalDateTime savedTime = LocalDateTime.parse(timestamp, FORMATTER);
        return LocalDateTime.now().minusMinutes(TTL_MINUTES).isBefore(savedTime);
    }

    public String getSavedTime(String key) {
        Map<String, String> meta = loadMeta();
        return meta.get(key);
    }

    public boolean exists(String key) {
        File file = new File(cacheDir + File.separator + key);
        return file.exists();
    }

    public void clearCache() {
        File dir = new File(cacheDir);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    private void updateMeta(String key, LocalDateTime time) throws IOException {
        Map<String, String> meta = loadMeta();
        meta.put(key, time.format(FORMATTER));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(metaFile))) {
            writer.write(gson.toJson(meta));
        }
    }

    private Map<String, String> loadMeta() {
        File file = new File(metaFile);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            Type type = new TypeToken<Map<String, String>>() {}.getType();
            Map<String, String> meta = gson.fromJson(sb.toString(), type);
            return meta != null ? meta : new HashMap<>();
        } catch (IOException e) {
            return new HashMap<>();
        }
    }
}
