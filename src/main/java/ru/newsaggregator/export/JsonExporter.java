package ru.newsaggregator.export;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.util.Time;

import java.util.List;

public class JsonExporter implements Exporter {

    @Override
    public String format() {
        return "json";
    }

    @Override
    public String extension() {
        return "json";
    }

    @Override
    public String render(List<Article> articles) {
        StringBuilder builder = new StringBuilder();
        builder.append("[\n");
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            builder.append("  {\n");
            builder.append("    \"id\": ").append(article.getId()).append(",\n");
            builder.append("    \"title\": ").append(quote(article.getTitle())).append(",\n");
            builder.append("    \"source\": ").append(quote(article.getSource())).append(",\n");
            builder.append("    \"category\": ").append(quote(article.getCategory())).append(",\n");
            builder.append("    \"publishedAt\": ").append(quote(date(article))).append(",\n");
            builder.append("    \"summary\": ").append(quote(article.getSummary())).append(",\n");
            builder.append("    \"keywords\": ").append(array(article.getKeywords())).append(",\n");
            builder.append("    \"mediaUrl\": ").append(quote(article.getMediaUrl())).append(",\n");
            builder.append("    \"link\": ").append(quote(article.getLink())).append("\n");
            builder.append(i == articles.size() - 1 ? "  }\n" : "  },\n");
        }
        builder.append("]\n");
        return builder.toString();
    }

    private String date(Article article) {
        return article.getPublishedAt() == null ? "" : Time.store(article.getPublishedAt());
    }

    private String array(List<String> values) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < values.size(); i++) {
            builder.append(quote(values.get(i)));
            if (i < values.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.append("]").toString();
    }

    private String quote(String value) {
        if (value == null) {
            return "\"\"";
        }
        StringBuilder builder = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                case '\n' -> builder.append("\\n");
                case '\r' -> builder.append("\\r");
                case '\t' -> builder.append("\\t");
                default -> builder.append(c);
            }
        }
        return builder.append("\"").toString();
    }
}
