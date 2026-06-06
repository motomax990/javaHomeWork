package ru.newsaggregator.export;

import org.junit.jupiter.api.Test;
import ru.newsaggregator.model.Article;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportersTest {

    private final List<Article> articles = List.of(
            article(1, "Новость, с запятой", "Спорт", "Lenta.ru"),
            article(2, "Обычная новость", "Экономика", "РБК"));

    @Test
    void csvHasHeaderAndQuotesCommas() {
        String csv = new CsvExporter().render(articles);
        assertTrue(csv.startsWith("id,title,source,category"));
        assertTrue(csv.contains("\"Новость, с запятой\""));
    }

    @Test
    void jsonContainsFieldsAndEscaping() {
        String json = new JsonExporter().render(articles);
        assertTrue(json.contains("\"title\": \"Новость, с запятой\""));
        assertTrue(json.trim().startsWith("["));
        assertTrue(json.trim().endsWith("]"));
    }

    @Test
    void htmlContainsTableAndEscapesAngleBrackets() {
        Article risky = article(3, "Заголовок <script>", "Прочее", "Тест");
        String html = new HtmlExporter().render(List.of(risky));
        assertTrue(html.contains("<table"));
        assertTrue(html.contains("&lt;script&gt;"));
    }

    private Article article(long id, String title, String category, String source) {
        Article article = new Article();
        article.setId(id);
        article.setTitle(title);
        article.setCategory(category);
        article.setSource(source);
        article.setLink("https://news/" + id);
        article.setSummary("Краткое описание " + id);
        article.setPublishedAt(LocalDateTime.of(2026, 6, 5, 12, 0));
        article.setKeywords(List.of("ключ"));
        return article;
    }
}
