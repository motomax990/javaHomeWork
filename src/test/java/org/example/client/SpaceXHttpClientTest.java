package org.example.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class SpaceXHttpClientTest {

    private SpaceXHttpClient client;

    @BeforeEach
    void setUp() {
        client = new SpaceXHttpClient();
    }

    @Test
    void buildUrlWithLeadingSlash() {
        String url = client.buildUrl("/v5/launches");
        assertEquals("https://api.spacexdata.com/v5/launches", url);
    }

    @Test
    void buildUrlWithoutLeadingSlash() {
        String url = client.buildUrl("v5/launches");
        assertEquals("https://api.spacexdata.com/v5/launches", url);
    }

    @Test
    void buildUrlWithLatestEndpoint() {
        String url = client.buildUrl("/v5/launches/latest");
        assertEquals("https://api.spacexdata.com/v5/launches/latest", url);
    }

    @Test
    void buildUrlWithQueryEndpoint() {
        String url = client.buildUrl("/v5/launches/query");
        assertEquals("https://api.spacexdata.com/v5/launches/query", url);
    }

    @Test
    void handleResponseReturnsBodyOnSuccess() throws IOException {
        String body = "{\"test\": true}";
        String result = client.handleResponse(200, body);
        assertEquals(body, result);
    }

    @Test
    void handleResponseThrowsOn404() {
        IOException ex = assertThrows(IOException.class, () ->
                client.handleResponse(404, "Not Found"));
        assertTrue(ex.getMessage().contains("404"));
        assertTrue(ex.getMessage().contains("Not Found"));
    }

    @Test
    void handleResponseThrowsOn500() {
        IOException ex = assertThrows(IOException.class, () ->
                client.handleResponse(500, "Internal Server Error"));
        assertTrue(ex.getMessage().contains("500"));
        assertTrue(ex.getMessage().contains("Internal Server Error"));
    }

    @Test
    void handleResponseThrowsOn403() {
        IOException ex = assertThrows(IOException.class, () ->
                client.handleResponse(403, "Forbidden"));
        assertTrue(ex.getMessage().contains("403"));
    }

    @Test
    void handleResponseThrowsOn400() {
        IOException ex = assertThrows(IOException.class, () ->
                client.handleResponse(400, "Bad Request"));
        assertTrue(ex.getMessage().contains("400"));
        assertTrue(ex.getMessage().contains("Bad Request"));
    }

    @Test
    void handleResponseReturnsEmptyBodyOnSuccess() throws IOException {
        String result = client.handleResponse(200, "");
        assertEquals("", result);
    }
}
