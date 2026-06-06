package ru.newsaggregator.fetch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import ru.newsaggregator.model.Article;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RssParser {

    private static final DateTimeFormatter[] OFFSET_FORMATS = {
            DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss Z", Locale.ENGLISH),
            DateTimeFormatter.ISO_OFFSET_DATE_TIME
    };

    private static final DateTimeFormatter[] ZONED_FORMATS = {
            DateTimeFormatter.ofPattern("d MMM yyyy HH:mm:ss zzz", Locale.ENGLISH),
            DateTimeFormatter.ISO_ZONED_DATE_TIME
    };

    public List<Article> parse(String xml) {
        if (xml == null || xml.isBlank()) {
            return List.of();
        }
        Document document = Jsoup.parse(xml, "", Parser.xmlParser());
        Elements items = document.getElementsByTag("item");
        if (items.isEmpty()) {
            items = document.getElementsByTag("entry");
        }
        List<Article> articles = new ArrayList<>();
        for (Element item : items) {
            Article article = toArticle(item);
            if (article != null) {
                articles.add(article);
            }
        }
        return articles;
    }

    private Article toArticle(Element item) {
        String title = textOf(item, "title");
        String link = extractLink(item);
        if (isBlank(title) || isBlank(link)) {
            return null;
        }
        Article article = new Article();
        article.setTitle(title.trim());
        article.setLink(link.trim());
        article.setPublishedAt(extractDate(item));
        article.setContent(extractContent(item));
        article.setMediaUrl(extractMedia(item));
        return article;
    }

    private String extractLink(Element item) {
        Elements links = item.getElementsByTag("link");
        for (Element link : links) {
            String text = link.text();
            if (!isBlank(text)) {
                return text;
            }
        }
        for (Element link : links) {
            String rel = link.attr("rel");
            if (rel.isBlank() || rel.equalsIgnoreCase("alternate")) {
                String href = link.attr("href");
                if (!isBlank(href)) {
                    return href;
                }
            }
        }
        Element guid = item.getElementsByTag("guid").first();
        if (guid != null && guid.text().startsWith("http")) {
            return guid.text();
        }
        return null;
    }

    private LocalDateTime extractDate(Element item) {
        String[] tags = {"pubDate", "published", "updated", "dc:date", "date"};
        for (String tag : tags) {
            Element element = item.getElementsByTag(tag).first();
            if (element != null && !isBlank(element.text())) {
                LocalDateTime parsed = parseDate(element.text());
                if (parsed != null) {
                    return parsed;
                }
            }
        }
        return null;
    }

    private LocalDateTime parseDate(String raw) {
        String value = raw.trim().replaceFirst("^[A-Za-z]{3,9},?\\s+", "");
        for (DateTimeFormatter format : OFFSET_FORMATS) {
            try {
                return OffsetDateTime.parse(value, format).toLocalDateTime();
            } catch (RuntimeException ignored) {
            }
        }
        for (DateTimeFormatter format : ZONED_FORMATS) {
            try {
                return ZonedDateTime.parse(value, format).toLocalDateTime();
            } catch (RuntimeException ignored) {
            }
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private String extractContent(Element item) {
        Element content = firstByTag(item, "content:encoded", "content", "description", "summary");
        return content == null ? "" : content.text();
    }

    private String extractMedia(Element item) {
        Element enclosure = item.getElementsByTag("enclosure").first();
        if (enclosure != null && !isBlank(enclosure.attr("url"))) {
            return enclosure.attr("url");
        }
        Element media = firstByTag(item, "media:content", "media:thumbnail");
        if (media != null && !isBlank(media.attr("url"))) {
            return media.attr("url");
        }
        Element body = firstByTag(item, "content:encoded", "description", "content");
        if (body != null) {
            Element image = Jsoup.parse(body.text()).selectFirst("img");
            if (image != null && !isBlank(image.attr("src"))) {
                return image.attr("src");
            }
        }
        return null;
    }

    private Element firstByTag(Element parent, String... tags) {
        for (String tag : tags) {
            Element element = parent.getElementsByTag(tag).first();
            if (element != null) {
                return element;
            }
        }
        return null;
    }

    private String textOf(Element parent, String tag) {
        Element element = parent.getElementsByTag(tag).first();
        return element == null ? null : element.text();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
