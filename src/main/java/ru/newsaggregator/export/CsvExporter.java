package ru.newsaggregator.export;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.util.Time;

import java.util.List;

public class CsvExporter implements Exporter {

    @Override
    public String format() {
        return "csv";
    }

    @Override
    public String extension() {
        return "csv";
    }

    @Override
    public String render(List<Article> articles) {
        StringBuilder builder = new StringBuilder();
        builder.append("id,title,source,category,published_at,keywords,link\n");
        for (Article article : articles) {
            builder.append(escape(String.valueOf(article.getId()))).append(',')
                    .append(escape(article.getTitle())).append(',')
                    .append(escape(article.getSource())).append(',')
                    .append(escape(article.getCategory())).append(',')
                    .append(escape(date(article))).append(',')
                    .append(escape(String.join(" ", article.getKeywords()))).append(',')
                    .append(escape(article.getLink())).append('\n');
        }
        return builder.toString();
    }

    private String date(Article article) {
        return article.getPublishedAt() == null ? "" : Time.store(article.getPublishedAt());
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
