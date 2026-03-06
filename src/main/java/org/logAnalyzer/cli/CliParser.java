package org.logAnalyzer.cli;

import org.apache.commons.cli.*;
import org.logAnalyzer.exception.InvalidArgumentException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public final class CliParser {

    private static final Set<String> SUPPORTED_FORMATS = Set.of("json", "markdown");

    private static final String OPT_PATH   = "path";
    private static final String OPT_FORMAT = "format";
    private static final String OPT_OUTPUT = "output";
    private static final String OPT_FROM   = "from";
    private static final String OPT_TO     = "to";

    private CliParser() {}

    public static CliArgs parse(String[] args) {
        Options options = buildOptions();
        CommandLine cmd = parseCommandLine(options, args);

        List<String> paths = extractPaths(cmd);
        String format = cmd.getOptionValue(OPT_FORMAT);
        String output = cmd.getOptionValue(OPT_OUTPUT);
        LocalDate from = extractDate(cmd, OPT_FROM);
        LocalDate to   = extractDate(cmd, OPT_TO);

        validateFormat(format);
        validateOutputExtension(output, format);
        validateDateRange(from, to);

        return new CliArgs(paths, format, output, from, to);
    }

    private static Options buildOptions() {
        Options options = new Options();

        options.addOption(Option.builder("p")
            .longOpt(OPT_PATH).hasArgs().required()
            .desc("Path(s) to NGINX log files (supports glob patterns)")
            .build());

        options.addOption(Option.builder("f")
            .longOpt(OPT_FORMAT).hasArg().required()
            .desc("Output format: json or markdown")
            .build());

        options.addOption(Option.builder("o")
            .longOpt(OPT_OUTPUT).hasArg().required()
            .desc("Output file path")
            .build());

        options.addOption(Option.builder()
            .longOpt(OPT_FROM).hasArg()
            .desc("Start date ISO8601 (yyyy-MM-dd)")
            .build());

        options.addOption(Option.builder()
            .longOpt(OPT_TO).hasArg()
            .desc("End date ISO8601 (yyyy-MM-dd)")
            .build());

        return options;
    }

    private static CommandLine parseCommandLine(Options options, String[] args) {
        try {
            return new DefaultParser().parse(options, args);
        } catch (MissingOptionException e) {
            throw new InvalidArgumentException("Missing required option(s): " + e.getMissingOptions());
        } catch (ParseException e) {
            throw new InvalidArgumentException("Failed to parse arguments: " + e.getMessage());
        }
    }

    private static List<String> extractPaths(CommandLine cmd) {
        String[] values = cmd.getOptionValues(OPT_PATH);
        if (values == null || values.length == 0) {
            throw new InvalidArgumentException("--path (-p) requires at least one value");
        }
        return Arrays.asList(values);
    }

    private static LocalDate extractDate(CommandLine cmd, String option) {
        String value = cmd.getOptionValue(option);
        if (value == null) return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            throw new InvalidArgumentException(
                "Date for --" + option + " must be ISO8601 (yyyy-MM-dd), got: " + value
            );
        }
    }

    private static void validateFormat(String format) {
        if (!SUPPORTED_FORMATS.contains(format)) {
            throw new InvalidArgumentException(
                "Unsupported format: '" + format + "'. Supported: " + SUPPORTED_FORMATS
            );
        }
    }

    private static void validateOutputExtension(String output, String format) {
        String expected = switch (format) {
            case "json"     -> ".json";
            case "markdown" -> ".md";
            default -> throw new InvalidArgumentException("Unsupported format: " + format);
        };
        if (!output.endsWith(expected)) {
            throw new InvalidArgumentException(
                "Output file must have extension '" + expected + "' for format '" + format + "'"
            );
        }
    }

    private static void validateDateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && !from.isBefore(to)) {
            throw new InvalidArgumentException("--from must be strictly before --to");
        }
    }
}
