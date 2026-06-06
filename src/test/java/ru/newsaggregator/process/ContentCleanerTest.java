package ru.newsaggregator.process;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentCleanerTest {

    private final ContentCleaner cleaner = new ContentCleaner();

    @Test
    void removesHtmlTags() {
        String result = cleaner.clean("<p>Привет <b>мир</b>!</p>");
        assertEquals("Привет мир!", result);
    }

    @Test
    void normalizesWhitespace() {
        String result = cleaner.clean("Слишком    много\n\nпробелов");
        assertEquals("Слишком много пробелов", result);
    }

    @Test
    void removesNoiseMarkers() {
        String result = cleaner.clean("Текст новости. Реклама на сайте.");
        assertTrue(result.contains("Текст новости"));
        assertFalse(result.toLowerCase().contains("реклама"));
    }

    @Test
    void returnsEmptyForNull() {
        assertEquals("", cleaner.clean(null));
    }
}
