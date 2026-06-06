package ru.newsaggregator.fetch;

import org.junit.jupiter.api.Test;
import ru.newsaggregator.model.Article;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RssParserTest {

    private static final String RSS = """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
              <channel>
                <title>Тестовая лента</title>
                <item>
                  <title>Учёные открыли новую планету</title>
                  <link>https://example.com/news/1</link>
                  <pubDate>Mon, 02 Jun 2026 10:30:00 +0300</pubDate>
                  <description><![CDATA[<p>Большое научное открытие в космосе.</p>]]></description>
                  <enclosure url="https://example.com/img/1.jpg" type="image/jpeg" length="12345"/>
                </item>
                <item>
                  <title>Второй матч чемпионата</title>
                  <link>https://example.com/news/2</link>
                  <pubDate>Tue, 03 Jun 2026 12:00:00 +0300</pubDate>
                  <description>Краткое описание матча</description>
                </item>
              </channel>
            </rss>
            """;

    private final RssParser parser = new RssParser();

    @Test
    void parsesAllItems() {
        List<Article> articles = parser.parse(RSS);
        assertEquals(2, articles.size());
    }

    @Test
    void parsesTitleLinkAndDate() {
        Article first = parser.parse(RSS).get(0);
        assertEquals("Учёные открыли новую планету", first.getTitle());
        assertEquals("https://example.com/news/1", first.getLink());
        assertNotNull(first.getPublishedAt());
        assertEquals(2026, first.getPublishedAt().getYear());
        assertEquals(30, first.getPublishedAt().getMinute());
    }

    @Test
    void parsesEnclosureAsMedia() {
        Article first = parser.parse(RSS).get(0);
        assertEquals("https://example.com/img/1.jpg", first.getMediaUrl());
    }

    @Test
    void extractsContent() {
        Article first = parser.parse(RSS).get(0);
        assertTrue(first.getContent().contains("научное"));
    }

    @Test
    void emptyInputGivesEmptyList() {
        assertTrue(parser.parse("").isEmpty());
    }
}
