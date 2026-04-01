package org.example.service;

import org.example.cache.FileStorage;
import org.example.client.SpaceXHttpClient;
import org.example.model.Launch;
import org.example.model.QueryResponse;
import org.example.parser.JsonBuilder;
import org.example.parser.JsonParser;

import java.io.IOException;
import java.util.List;

public class LaunchService {

    private final SpaceXHttpClient httpClient;
    private final JsonParser jsonParser;
    private final JsonBuilder jsonBuilder;
    private final FileStorage fileStorage;

    public LaunchService(SpaceXHttpClient httpClient, JsonParser jsonParser,
                         JsonBuilder jsonBuilder, FileStorage fileStorage) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.jsonBuilder = jsonBuilder;
        this.fileStorage = fileStorage;
    }

    public List<Launch> getAllLaunches() throws IOException {
        String cacheKey = "launches_all.json";
        String json = fetchWithCache(cacheKey, () ->
                httpClient.get(httpClient.buildUrl("/v5/launches")));
        return jsonParser.parseLaunches(json);
    }

    public Launch getLatestLaunch() throws IOException {
        String cacheKey = "launches_latest.json";
        String json = fetchWithCache(cacheKey, () ->
                httpClient.get(httpClient.buildUrl("/v5/launches/latest")));
        return jsonParser.parseLaunch(json);
    }

    public List<Launch> searchByDate(String from, String to) throws IOException {
        String cacheKey = "query_" + from + "_" + to + ".json";
        String body = jsonBuilder.buildDateQuery(from, to);
        String json = fetchWithCache(cacheKey, () ->
                httpClient.post(httpClient.buildUrl("/v5/launches/query"), body));
        QueryResponse response = jsonParser.parseQueryResponse(json);
        return response.getDocs();
    }

    public List<Launch> getBySuccess(boolean success) throws IOException {
        String cacheKey = "query_success_" + success + ".json";
        String body = jsonBuilder.buildSuccessQuery(success);
        String json = fetchWithCache(cacheKey, () ->
                httpClient.post(httpClient.buildUrl("/v5/launches/query"), body));
        QueryResponse response = jsonParser.parseQueryResponse(json);
        return response.getDocs();
    }

    public void clearCache() {
        fileStorage.clearCache();
    }

    private String fetchWithCache(String cacheKey, DataFetcher fetcher) throws IOException {
        if (fileStorage.isValid(cacheKey)) {
            String cached = fileStorage.load(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        try {
            String json = fetcher.fetch();
            fileStorage.save(cacheKey, json);
            return json;
        } catch (IOException e) {
            if (fileStorage.exists(cacheKey)) {
                String cached = fileStorage.load(cacheKey);
                if (cached != null) {
                    String savedTime = fileStorage.getSavedTime(cacheKey);
                    System.out.println("[!] Сервер недоступен. Показаны данные из кеша (сохранены " + savedTime + ")");
                    return cached;
                }
            }
            throw e;
        }
    }

    @FunctionalInterface
    interface DataFetcher {
        String fetch() throws IOException;
    }
}
