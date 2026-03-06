package org.logAnalyzer.analyzer;

import org.logAnalyzer.cli.CliArgs;
import org.logAnalyzer.exception.LogAnalyzerException;
import org.logAnalyzer.model.LogEntry;
import org.logAnalyzer.model.Statistics;
import org.logAnalyzer.parser.LogParser;
import org.logAnalyzer.path.PathResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class LogAnalyzer {

    private static final Logger log = Logger.getLogger(LogAnalyzer.class.getName());
    private static final int TOP_RESOURCES_LIMIT = 10;

    private final CliArgs args;

    public LogAnalyzer(CliArgs args) {
        this.args = args;
    }

    public Statistics analyze() {
        List<Path> paths = PathResolver.resolve(args.paths());
        log.info("Analyzing " + paths.size() + " file(s)");

        long totalRequests = 0;
        long totalBytes = 0;
        long maxBytes = 0;
        PercentileCalculator p95Calculator = new PercentileCalculator(0.95);
        Map<String, Long> resourceCounts = new HashMap<>();
        Map<Integer, Long> statusCounts = new HashMap<>();
        Map<LocalDate, Long> requestsPerDate = new HashMap<>();

        for (Path path : paths) {
            log.info("Processing file: " + path);
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) continue;
                    Optional<LogEntry> entryOpt = LogParser.parse(line);
                    if (entryOpt.isEmpty()) {
                        log.warning("Could not parse log line: " + line);
                        continue;
                    }
                    LogEntry entry = entryOpt.get();
                    if (!isInDateRange(entry)) continue;

                    totalRequests++;
                    totalBytes += entry.bodyBytesSent();
                    if (entry.bodyBytesSent() > maxBytes) maxBytes = entry.bodyBytesSent();
                    p95Calculator.add(entry.bodyBytesSent());

                    resourceCounts.merge(entry.resource(), 1L, Long::sum);
                    statusCounts.merge(entry.status(), 1L, Long::sum);
                    requestsPerDate.merge(entry.timeLocal().toLocalDate(), 1L, Long::sum);
                }
            } catch (IOException e) {
                throw new LogAnalyzerException("Failed to read file: " + path, e);
            }
        }

        double avgBytes = totalRequests > 0
            ? Math.round((double) totalBytes / totalRequests * 100.0) / 100.0
            : 0;

        LinkedHashMap<String, Long> topResources = resourceCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(TOP_RESOURCES_LIMIT)
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue,
                (a, b) -> a, LinkedHashMap::new
            ));

        LinkedHashMap<Integer, Long> sortedCodes = statusCounts.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue,
                (a, b) -> a, LinkedHashMap::new
            ));

        LinkedHashMap<LocalDate, Long> sortedDates = requestsPerDate.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue,
                (a, b) -> a, LinkedHashMap::new
            ));

        List<String> fileNames = paths.stream()
            .map(p -> p.getFileName().toString())
            .toList();

        log.info("Analysis complete. Total requests: " + totalRequests);

        return new Statistics(
            fileNames,
            args.from(),
            args.to(),
            totalRequests,
            avgBytes,
            maxBytes,
            p95Calculator.getEstimate(),
            topResources,
            sortedCodes,
            sortedDates
        );
    }

    private boolean isInDateRange(LogEntry entry) {
        LocalDate entryDate = entry.timeLocal().toLocalDate();
        if (args.from() != null && entryDate.isBefore(args.from())) return false;
        if (args.to() != null && entryDate.isAfter(args.to())) return false;
        return true;
    }
}
