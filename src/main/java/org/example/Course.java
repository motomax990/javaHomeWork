package org.example;

import java.util.ArrayList;
import java.util.List;

public class Course {

    private String name;

    @CsvCollection(delimiter = ";")
    private List<String> tags;

    private List<String> authors;

    public Course() {
    }

    public Course(String name, List<String> tags, List<String> authors) {
        this.name = name;
        this.tags = tags;
        this.authors = authors;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }

    @Override
    public String toString() {
        return "Course{name='" + name + "', tags=" + tags + ", authors=" + authors + "}";
    }
}
