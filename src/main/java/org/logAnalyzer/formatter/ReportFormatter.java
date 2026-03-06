package org.logAnalyzer.formatter;

import org.logAnalyzer.model.Statistics;

public interface ReportFormatter {
    String format(Statistics statistics);
}
