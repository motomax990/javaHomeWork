package org.logAnalyzer.formatter.dto;

import java.util.List;

public record ReportDto(
    List<String> files,
    long totalRequestsCount,
    ResponseSizeDto responseSizeInBytes,
    List<ResourceDto> resources,
    List<ResponseCodeDto> responseCodes,
    List<RequestsPerDateDto> requestsPerDate
) {}
