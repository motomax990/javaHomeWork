package ru.newsaggregator.process;

import org.junit.jupiter.api.Test;
import ru.newsaggregator.model.Article;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArticleProcessorTest {

    private final ArticleProcessor processor = new ArticleProcessor(
            new ContentCleaner(), new CategoryClassifier(), new KeywordExtractor(), new Summarizer());

    @Test
    void cleansClassifiesAndFillsKeywords() {
        Article article = new Article();
        article.setTitle("Учёные сделали открытие");
        article.setContent("<p>Учёные провели исследование. Это важное научное открытие в космосе.</p>");

        processor.process(article);

        assertFalse(article.getContent().contains("<"));
        assertEquals("Наука", article.getCategory());
        assertFalse(article.getKeywords().isEmpty());
        assertTrue(article.getSummary().length() > 0);
    }
}
