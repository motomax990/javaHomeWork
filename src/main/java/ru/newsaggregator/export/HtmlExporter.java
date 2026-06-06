package ru.newsaggregator.export;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.util.Time;

import java.util.List;

public class HtmlExporter implements Exporter {

    @Override
    public String format() {
        return "html";
    }

    @Override
    public String extension() {
        return "html";
    }

    @Override
    public String render(List<Article> articles) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n");
        builder.append("<html lang=\"ru\">\n<head>\n");
        builder.append("<meta charset=\"UTF-8\">\n");
        builder.append("<title>Агрегатор новостей</title>\n");
        builder.append("<style>")
                .append("body{font-family:Arial,sans-serif;margin:24px;}")
                .append("table{border-collapse:collapse;width:100%;}")
                .append("th,td{border:1px solid #ccc;padding:8px;text-align:left;vertical-align:top;}")
                .append("th{background:#f4f4f4;}")
                .append("</style>\n");
        builder.append("</head>\n<body>\n");
        builder.append("<h1>Агрегатор новостей</h1>\n");
        builder.append("<p>Всего материалов: ").append(articles.size()).append("</p>\n");
        builder.append("<table>\n<tr>")
                .append("<th>Дата</th><th>Источник</th><th>Категория</th>")
                .append("<th>Заголовок</th><th>Краткое описание</th></tr>\n");
        for (Article article : articles) {
            builder.append("<tr>")
                    .append("<td>").append(escape(date(article))).append("</td>")
                    .append("<td>").append(escape(article.getSource())).append("</td>")
                    .append("<td>").append(escape(article.getCategory())).append("</td>")
                    .append("<td><a href=\"").append(escape(article.getLink())).append("\">")
                    .append(escape(article.getTitle())).append("</a></td>")
                    .append("<td>").append(escape(article.getSummary())).append("</td>")
                    .append("</tr>\n");
        }
        builder.append("</table>\n</body>\n</html>\n");
        return builder.toString();
    }

    private String date(Article article) {
        return article.getPublishedAt() == null ? "" : Time.display(article.getPublishedAt());
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
