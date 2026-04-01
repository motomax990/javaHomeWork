package org.example.model;

import java.util.List;

public class QueryResponse {

    private List<Launch> docs;

    public List<Launch> getDocs() {
        return docs;
    }

    public void setDocs(List<Launch> docs) {
        this.docs = docs;
    }
}
