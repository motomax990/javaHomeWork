package ru.newsaggregator.process;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeywordExtractor {

    private static final Set<String> STOP_WORDS = Set.of(
            "это", "которые", "который", "которая", "также", "более", "очень", "когда", "после",
            "около", "между", "через", "потому", "однако", "просто", "сейчас", "будет", "может",
            "если", "чтобы", "пока", "ещё", "уже", "его", "она", "они", "оно", "тот", "при",
            "над", "под", "для", "что", "как", "так", "все", "всё", "был", "была", "были",
            "быть", "есть", "нет", "год", "году", "дня", "том", "тем", "тех", "этом",
            "своих", "свою", "этого", "стал", "стала", "слов"
    );

    private final int minLength;

    public KeywordExtractor() {
        this(4);
    }

    public KeywordExtractor(int minLength) {
        this.minLength = minLength;
    }

    public List<String> extract(String text, int limit) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        Map<String, Integer> frequency = new LinkedHashMap<>();
        for (String token : text.toLowerCase().split("[^\\p{L}]+")) {
            if (token.length() < minLength || STOP_WORDS.contains(token)) {
                continue;
            }
            frequency.merge(token, 1, Integer::sum);
        }
        return frequency.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }
}
