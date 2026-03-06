package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.logAnalyzer.exception.InvalidArgumentException;
import org.logAnalyzer.formatter.FormatterFactory;
import org.logAnalyzer.formatter.JsonFormatter;
import org.logAnalyzer.formatter.MarkdownFormatter;
import org.logAnalyzer.formatter.ReportFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormatterFactoryTest {

    @Test
    void createsJsonFormatter() {
        ReportFormatter formatter = FormatterFactory.create("json");
        assertThat(formatter).isInstanceOf(JsonFormatter.class);
    }

    @Test
    void createsMarkdownFormatter() {
        ReportFormatter formatter = FormatterFactory.create("markdown");
        assertThat(formatter).isInstanceOf(MarkdownFormatter.class);
    }

    @Test
    void throwsForUnknownFormat() {
        assertThatThrownBy(() -> FormatterFactory.create("xml"))
            .isInstanceOf(InvalidArgumentException.class)
            .hasMessageContaining("Unsupported format");
    }

    @Test
    void throwsForEmptyFormat() {
        assertThatThrownBy(() -> FormatterFactory.create(""))
            .isInstanceOf(InvalidArgumentException.class);
    }

    @Test
    void throwsForNullFormat() {
        assertThatThrownBy(() -> FormatterFactory.create(null))
            .isInstanceOf(Exception.class);
    }
}
