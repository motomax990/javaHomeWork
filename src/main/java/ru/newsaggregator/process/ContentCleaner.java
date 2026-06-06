package ru.newsaggregator.process;

import org.jsoup.Jsoup;

import java.util.List;

public class ContentCleaner {

    private static final List<String> NOISE = List.of(
            "читать далее",
            "читать полностью",
            "подробнее на",
            "реклама",
            "фото:",
            "видео:",
            "источник:"
    );

    public String clean(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }
        String text = Jsoup.parse(raw).text();
        String lower = text.toLowerCase();
        for (String marker : NOISE) {
            int index = lower.indexOf(marker);
            while (index >= 0) {
                int end = lower.indexOf('.', index);
                if (end < 0) {
                    text = text.substring(0, index);
                    break;
                }
                text = text.substring(0, index) + text.substring(end + 1);
                lower = text.toLowerCase();
                index = lower.indexOf(marker);
            }
        }
        return text.replaceAll("\\s+", " ").trim();
    }
}
