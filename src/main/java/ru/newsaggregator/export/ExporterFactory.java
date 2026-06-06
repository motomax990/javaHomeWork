package ru.newsaggregator.export;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExporterFactory {

    private final Map<String, Exporter> exporters = new LinkedHashMap<>();

    public ExporterFactory() {
        register(new CsvExporter());
        register(new JsonExporter());
        register(new HtmlExporter());
    }

    public void register(Exporter exporter) {
        exporters.put(exporter.format().toLowerCase(), exporter);
    }

    public Exporter forFormat(String format) {
        Exporter exporter = exporters.get(format == null ? "" : format.toLowerCase());
        if (exporter == null) {
            throw new IllegalArgumentException("Неизвестный формат экспорта: " + format);
        }
        return exporter;
    }

    public List<String> supportedFormats() {
        return List.copyOf(exporters.keySet());
    }
}
