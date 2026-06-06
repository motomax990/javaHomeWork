package ru.newsaggregator.process;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SummarizerTest {

    @Test
    void shortTextStaysAsIs() {
        Summarizer summarizer = new Summarizer(280);
        assertEquals("Короткая новость.", summarizer.summarize("Короткая новость."));
    }

    @Test
    void keepsWholeSentencesWithinLimit() {
        Summarizer summarizer = new Summarizer(40);
        String result = summarizer.summarize("Первое предложение тут. Второе предложение уже не помещается целиком.");
        assertEquals("Первое предложение тут.", result);
    }

    @Test
    void cutsLongSingleSentence() {
        Summarizer summarizer = new Summarizer(20);
        String result = summarizer.summarize("ОченьДлинноеСловоБезПробеловКотороеНеВлезает целиком");
        assertTrue(result.endsWith("…"));
    }

    @Test
    void emptyTextGivesEmptyString() {
        assertEquals("", new Summarizer().summarize(null));
    }
}
