package ru.newsaggregator.cli;

import ru.newsaggregator.service.SearchCriteria;
import ru.newsaggregator.service.SortOrder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CriteriaParser {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private CriteriaParser() {
    }

    public static Map<String, String> options(List<String> args) {
        Map<String, String> options = new LinkedHashMap<>();
        for (String arg : args) {
            int eq = arg.indexOf('=');
            if (eq > 0) {
                options.put(arg.substring(0, eq).toLowerCase(), arg.substring(eq + 1));
            }
        }
        return options;
    }

    public static SearchCriteria fromOptions(Map<String, String> options) {
        SearchCriteria criteria = new SearchCriteria();
        if (options.containsKey("category")) {
            criteria.setCategory(options.get("category"));
        }
        if (options.containsKey("source")) {
            criteria.setSource(options.get("source"));
        }
        if (options.containsKey("keyword")) {
            criteria.setKeyword(options.get("keyword"));
        }
        if (options.containsKey("from")) {
            criteria.setFrom(LocalDate.parse(options.get("from"), DATE).atStartOfDay());
        }
        if (options.containsKey("to")) {
            criteria.setTo(LocalDate.parse(options.get("to"), DATE).atTime(23, 59, 59));
        }
        if (options.containsKey("sort")) {
            criteria.setSortOrder(parseSort(options.get("sort")));
        }
        return criteria;
    }

    private static SortOrder parseSort(String value) {
        return switch (value.toLowerCase()) {
            case "date_asc", "old" -> SortOrder.DATE_ASC;
            case "source" -> SortOrder.SOURCE;
            case "category" -> SortOrder.CATEGORY;
            default -> SortOrder.DATE_DESC;
        };
    }
}
