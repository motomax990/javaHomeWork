package ru.newsaggregator.process;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryClassifierTest {

    private final CategoryClassifier classifier = new CategoryClassifier();

    @Test
    void detectsPolitics() {
        assertEquals("Политика", classifier.classify("Президент провёл переговоры", ""));
    }

    @Test
    void detectsSport() {
        assertEquals("Спорт", classifier.classify("Сборная выиграла матч чемпионата", ""));
    }

    @Test
    void detectsScienceFromBody() {
        assertEquals("Наука", classifier.classify("Новое открытие", "Учёные провели исследование в космосе"));
    }

    @Test
    void fallsBackToDefault() {
        assertEquals(CategoryClassifier.DEFAULT_CATEGORY, classifier.classify("Просто заметка", "ни о чём конкретном"));
    }
}
