package ru.newsaggregator.process;

public class Summarizer {

    private final int maxLength;

    public Summarizer() {
        this(280);
    }

    public Summarizer(int maxLength) {
        this.maxLength = maxLength;
    }

    public String summarize(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String normalized = text.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        String[] sentences = normalized.split("(?<=[.!?])\\s+");
        StringBuilder builder = new StringBuilder();
        for (String sentence : sentences) {
            if (builder.length() + sentence.length() > maxLength) {
                break;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(sentence);
        }
        if (builder.length() == 0) {
            return cutAtWord(normalized);
        }
        return builder.toString();
    }

    private String cutAtWord(String text) {
        String cut = text.substring(0, maxLength);
        int lastSpace = cut.lastIndexOf(' ');
        if (lastSpace > 0) {
            cut = cut.substring(0, lastSpace);
        }
        return cut + "…";
    }
}
