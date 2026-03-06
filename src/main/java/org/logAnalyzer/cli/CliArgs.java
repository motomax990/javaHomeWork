package org.logAnalyzer.cli;

import java.time.LocalDate;
import java.util.List;

public record CliArgs(
    List<String> paths,
    String format,
    String output,
    LocalDate from,
    LocalDate to
) {}
