package ru.newsaggregator.process;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeywordExtractorTest {

    private final KeywordExtractor extractor = new KeywordExtractor();

    @Test
    void mostFrequentWordGoesFirst() {
        List<String> keywords = extractor.extract("рубль рубль рубль экономика экономика россия", 5);
        assertEquals("рубль", keywords.get(0));
    }

    @Test
    void skipsShortWordsAndStopWords() {
        List<String> keywords = extractor.extract("это был очень но сон при над год", 10);
        assertFalse(keywords.contains("это"));
        assertFalse(keywords.contains("был"));
    }

    @Test
    void respectsLimit() {
        List<String> keywords = extractor.extract("альфа бета гамма дельта эпсилон дзета", 3);
        assertEquals(3, keywords.size());
    }

    @Test
    void emptyTextGivesEmptyList() {
        assertTrue(extractor.extract("   ", 5).isEmpty());
    }
}
