package org.logAnalyzer.formatter;

import org.logAnalyzer.model.Statistics;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;

public class MarkdownFormatter implements ReportFormatter {

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public String format(Statistics stats) {
        StringBuilder sb = new StringBuilder();
        appendGeneralInfo(sb, stats);
        appendTopResources(sb, stats);
        appendResponseCodes(sb, stats);
        if (!stats.requestsPerDate().isEmpty()) {
            appendRequestsPerDate(sb, stats);
        }
        return sb.toString();
    }

    private void appendGeneralInfo(StringBuilder sb, Statistics stats) {
        sb.append("#### Общая информация\n\n");
        sb.append("| Метрика | Значение |\n");
        sb.append("|:----------------------:|-------------:|\n");
        sb.append("| Файл(-ы) | ").append(String.join(", ", stats.files())).append(" |\n");
        sb.append("| Начальная дата | ").append(formatDate(stats.fromDate())).append(" |\n");
        sb.append("| Конечная дата | ").append(formatDate(stats.toDate())).append(" |\n");
        sb.append("| Количество запросов | ").append(formatNumber(stats.totalRequestsCount())).append(" |\n");
        sb.append("| Средний размер ответа | ").append(stats.averageResponseSizeBytes()).append("b |\n");
        sb.append("| Максимальный размер ответа | ").append(stats.maxResponseSizeBytes()).append("b |\n");
        sb.append("| 95p размера ответа | ").append(stats.p95ResponseSizeBytes()).append("b |\n\n");
    }

    private void appendTopResources(StringBuilder sb, Statistics stats) {
        sb.append("#### Запрашиваемые ресурсы\n\n");
        sb.append("| Ресурс | Количество |\n");
        sb.append("|:---------------:|----------:|\n");
        for (Map.Entry<String, Long> e : stats.topResources().entrySet()) {
            sb.append("| ").append(e.getKey()).append(" | ").append(formatNumber(e.getValue())).append(" |\n");
        }
        sb.append("\n");
    }

    private void appendResponseCodes(StringBuilder sb, Statistics stats) {
        sb.append("#### Коды ответа\n\n");
        sb.append("| Код | Имя | Количество |\n");
        sb.append("|:---:|:----------------------:|-----------:|\n");
        for (Map.Entry<Integer, Long> e : stats.responseCodes().entrySet()) {
            sb.append("| ").append(e.getKey())
              .append(" | ").append(HttpStatusName.of(e.getKey()))
              .append(" | ").append(formatNumber(e.getValue())).append(" |\n");
        }
        sb.append("\n");
    }

    private void appendRequestsPerDate(StringBuilder sb, Statistics stats) {
        long total = stats.totalRequestsCount();
        sb.append("#### Распределение запросов по датам\n\n");
        sb.append("| Дата | День недели | Количество | Процент |\n");
        sb.append("|:----------:|:----------:|-----------:|-------:|\n");
        for (Map.Entry<LocalDate, Long> e : stats.requestsPerDate().entrySet()) {
            LocalDate date = e.getKey();
            long count = e.getValue();
            double pct = total > 0 ? Math.round(count * 10000.0 / total) / 100.0 : 0;
            String weekday = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ru"));
            sb.append("| ").append(date.format(DISPLAY_DATE))
              .append(" | ").append(weekday)
              .append(" | ").append(formatNumber(count))
              .append(" | ").append(pct).append("% |\n");
        }
        sb.append("\n");
    }

    private String formatDate(LocalDate date) {
        return date != null ? date.format(DISPLAY_DATE) : "-";
    }

    private String formatNumber(long number) {
        if (number < 1000) return Long.toString(number);
        String s = Long.toString(number);
        StringBuilder result = new StringBuilder();
        int mod = s.length() % 3;
        for (int i = 0; i < s.length(); i++) {
            if (i != 0 && (i - mod) % 3 == 0) result.append('_');
            result.append(s.charAt(i));
        }
        return result.toString();
    }
}
