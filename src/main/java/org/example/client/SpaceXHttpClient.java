package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class SpaceXHttpClient {

    private static final String BASE_URL = "https://api.spacexdata.com";

    public String get(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        int statusCode = connection.getResponseCode();
        String body = readBody(connection, statusCode);
        return handleResponse(statusCode, body);
    }

    public String post(String url, String jsonBody) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        int statusCode = connection.getResponseCode();
        String body = readBody(connection, statusCode);
        return handleResponse(statusCode, body);
    }

    public String buildUrl(String path) {
        if (path.startsWith("/")) {
            return BASE_URL + path;
        }
        return BASE_URL + "/" + path;
    }

    public String handleResponse(int statusCode, String body) throws IOException {
        if (statusCode == 200) {
            return body;
        }
        throw new IOException("HTTP " + statusCode + ": " + body);
    }

    private String readBody(HttpURLConnection connection, int statusCode) throws IOException {
        BufferedReader reader;
        if (statusCode >= 200 && statusCode < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
}
