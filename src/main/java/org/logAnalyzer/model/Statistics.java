package org.logAnalyzer.model;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

public record Statistics(
    List<String> files,
    LocalDate fromDate,
    LocalDate toDate,
    long totalRequestsCount,
    double averageResponseSizeBytes,
    long maxResponseSizeBytes,
    double p95ResponseSizeBytes,
    LinkedHashMap<String, Long> topResources,
    LinkedHashMap<Integer, Long> responseCodes,
    LinkedHashMap<LocalDate, Long> requestsPerDate
) {}
