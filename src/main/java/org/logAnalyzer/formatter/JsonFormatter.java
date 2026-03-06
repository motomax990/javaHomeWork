package org.logAnalyzer.formatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.logAnalyzer.formatter.dto.*;
import org.logAnalyzer.model.Statistics;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class JsonFormatter implements ReportFormatter {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create();

    @Override
    public String format(Statistics stats) {
        ReportDto dto = new ReportDto(
            stats.files(),
            stats.totalRequestsCount(),
            new ResponseSizeDto(
                stats.averageResponseSizeBytes(),
                stats.maxResponseSizeBytes(),
                stats.p95ResponseSizeBytes()
            ),
            buildResources(stats),
            buildResponseCodes(stats),
            buildRequestsPerDate(stats)
        );
        return GSON.toJson(dto);
    }

    private List<ResourceDto> buildResources(Statistics stats) {
        return stats.topResources().entrySet().stream()
            .map(e -> new ResourceDto(e.getKey(), e.getValue()))
            .toList();
    }

    private List<ResponseCodeDto> buildResponseCodes(Statistics stats) {
        return stats.responseCodes().entrySet().stream()
            .map(e -> new ResponseCodeDto(e.getKey(), e.getValue()))
            .toList();
    }

    private List<RequestsPerDateDto> buildRequestsPerDate(Statistics stats) {
        long total = stats.totalRequestsCount();
        return stats.requestsPerDate().entrySet().stream()
            .map(e -> toDto(e, total))
            .toList();
    }

    private RequestsPerDateDto toDto(Map.Entry<LocalDate, Long> entry, long total) {
        LocalDate date = entry.getKey();
        long count = entry.getValue();
        double percentage = total > 0 ? Math.round(count * 10000.0 / total) / 100.0 : 0;
        String weekday = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return new RequestsPerDateDto(date.toString(), weekday, count, percentage);
    }
}
