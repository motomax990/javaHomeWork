package ru.newsaggregator.model;

import java.time.LocalDateTime;

public class ChangeRecord {

    private long id;
    private ChangeType type;
    private String title;
    private String link;
    private LocalDateTime at;

    public ChangeRecord() {
    }

    public ChangeRecord(ChangeType type, String title, String link, LocalDateTime at) {
        this.type = type;
        this.title = title;
        this.link = link;
        this.at = at;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ChangeType getType() {
        return type;
    }

    public void setType(ChangeType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public LocalDateTime getAt() {
        return at;
    }

    public void setAt(LocalDateTime at) {
        this.at = at;
    }
}
