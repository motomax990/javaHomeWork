package org.logAnalyzer.formatter;

import java.util.Map;

final class HttpStatusName {

    private static final Map<Integer, String> STATUS_NAMES = Map.ofEntries(
        Map.entry(100, "Continue"),
        Map.entry(101, "Switching Protocols"),
        Map.entry(200, "OK"),
        Map.entry(201, "Created"),
        Map.entry(202, "Accepted"),
        Map.entry(204, "No Content"),
        Map.entry(206, "Partial Content"),
        Map.entry(301, "Moved Permanently"),
        Map.entry(302, "Found"),
        Map.entry(304, "Not Modified"),
        Map.entry(400, "Bad Request"),
        Map.entry(401, "Unauthorized"),
        Map.entry(403, "Forbidden"),
        Map.entry(404, "Not Found"),
        Map.entry(405, "Method Not Allowed"),
        Map.entry(408, "Request Timeout"),
        Map.entry(409, "Conflict"),
        Map.entry(410, "Gone"),
        Map.entry(429, "Too Many Requests"),
        Map.entry(500, "Internal Server Error"),
        Map.entry(501, "Not Implemented"),
        Map.entry(502, "Bad Gateway"),
        Map.entry(503, "Service Unavailable"),
        Map.entry(504, "Gateway Timeout")
    );

    private HttpStatusName() {}

    static String of(int code) {
        return STATUS_NAMES.getOrDefault(code, "Unknown");
    }
}
