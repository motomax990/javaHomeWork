package ru.newsaggregator.service;

import java.time.LocalDateTime;

public class SearchCriteria {

    private String category;
    private String source;
    private String keyword;
    private LocalDateTime from;
    private LocalDateTime to;
    private SortOrder sortOrder = SortOrder.DATE_DESC;

    public String getCategory() {
        return category;
    }

    public SearchCriteria setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getSource() {
        return source;
    }

    public SearchCriteria setSource(String source) {
        this.source = source;
        return this;
    }

    public String getKeyword() {
        return keyword;
    }

    public SearchCriteria setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public SearchCriteria setFrom(LocalDateTime from) {
        this.from = from;
        return this;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public SearchCriteria setTo(LocalDateTime to) {
        this.to = to;
        return this;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public SearchCriteria setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }
}
