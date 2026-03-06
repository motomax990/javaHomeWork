package org.logAnalyzer.formatter.dto;

public record RequestsPerDateDto(
    String date,
    String weekday,
    long totalRequestsCount,
    double totalRequestsPercentage
) {}
