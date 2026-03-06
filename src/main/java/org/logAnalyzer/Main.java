package org.logAnalyzer;

import org.logAnalyzer.analyzer.LogAnalyzer;
import org.logAnalyzer.cli.CliArgs;
import org.logAnalyzer.cli.CliParser;
import org.logAnalyzer.exception.InvalidArgumentException;
import org.logAnalyzer.formatter.FormatterFactory;
import org.logAnalyzer.formatter.ReportFormatter;
import org.logAnalyzer.model.Statistics;
import org.logAnalyzer.writer.ReportWriter;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        configureLogging();
        try {
            CliArgs cliArgs = CliParser.parse(args);
            Statistics statistics = new LogAnalyzer(cliArgs).analyze();
            ReportFormatter formatter = FormatterFactory.create(cliArgs.format());
            String report = formatter.format(statistics);
            ReportWriter.write(report, cliArgs.output());
            log.info("Program finished successfully");
            System.exit(0);
        } catch (InvalidArgumentException e) {
            log.severe("Invalid arguments: " + e.getMessage());
            System.exit(2);
        } catch (Exception e) {
            log.severe("Unexpected error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void configureLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.ALL);

        StreamHandler handler = new StreamHandler(System.out, new SimpleFormatter());
        handler.setLevel(Level.ALL);

        for (var h : rootLogger.getHandlers()) {
            rootLogger.removeHandler(h);
        }
        rootLogger.addHandler(handler);
    }
}
