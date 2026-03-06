package org.logAnalyzer.parser;

import org.logAnalyzer.model.LogEntry;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LogParser {

    private static final DateTimeFormatter LOG_DATE_FORMAT =
        DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    private static final Pattern LOG_PATTERN = Pattern.compile(
        "^(\\S+) - (\\S+) \\[([^\\]]+)] \"([^\"]*)\" (\\d+) (\\d+) \"([^\"]*)\" \"([^\"]*)\"$"
    );

    private LogParser() {}

    public static Optional<LogEntry> parse(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }
        String[] requestParts = parseRequest(matcher.group(4));
        return Optional.of(new LogEntry(
            matcher.group(1),
            matcher.group(2),
            ZonedDateTime.parse(matcher.group(3), LOG_DATE_FORMAT),
            requestParts[0],
            requestParts[1],
            requestParts[2],
            Integer.parseInt(matcher.group(5)),
            Long.parseLong(matcher.group(6)),
            matcher.group(7),
            matcher.group(8)
        ));
    }

    private static String[] parseRequest(String request) {
        String[] parts = request.split(" ", 3);
        if (parts.length == 3) return parts;
        if (parts.length == 2) return new String[]{parts[0], parts[1], ""};
        return new String[]{"", request, ""};
    }
}
