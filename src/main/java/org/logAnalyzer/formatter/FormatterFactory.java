package org.logAnalyzer.formatter;

import org.logAnalyzer.exception.InvalidArgumentException;

public final class FormatterFactory {

    private FormatterFactory() {}

    public static ReportFormatter create(String format) {
        return switch (format) {
            case "json" -> new JsonFormatter();
            case "markdown" -> new MarkdownFormatter();
            default -> throw new InvalidArgumentException("Unsupported format: " + format);
        };
    }
}
