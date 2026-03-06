package org.logAnalyzer;

import org.junit.jupiter.api.Test;
import org.logAnalyzer.analyzer.PercentileCalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class PercentileCalculatorTest {

    @Test
    void returnsZeroWhenNoValuesAdded() {
        assertThat(new PercentileCalculator(0.95).getEstimate()).isEqualTo(0.0);
    }

    @Test
    void returnsSingleValueForOneElement() {
        PercentileCalculator calc = new PercentileCalculator(0.95);
        calc.add(42);
        assertThat(calc.getEstimate()).isEqualTo(42.0);
    }

    @Test
    void estimatesP95ForLargeUniformDistribution() {
        PercentileCalculator calc = new PercentileCalculator(0.95);
        for (int i = 1; i <= 10_000; i++) {
            calc.add(i);
        }
        assertThat(calc.getEstimate()).isCloseTo(9500.0, within(200.0));
    }

    @Test
    void estimatesMedianForUniformDistribution() {
        PercentileCalculator calc = new PercentileCalculator(0.5);
        for (int i = 1; i <= 10_000; i++) {
            calc.add(i);
        }
        assertThat(calc.getEstimate()).isCloseTo(5000.0, within(200.0));
    }

    @Test
    void throwsForZeroPercentile() {
        assertThatThrownBy(() -> new PercentileCalculator(0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsForOnePercentile() {
        assertThatThrownBy(() -> new PercentileCalculator(1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsForNegativePercentile() {
        assertThatThrownBy(() -> new PercentileCalculator(-0.5))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void handlesAllSameValues() {
        PercentileCalculator calc = new PercentileCalculator(0.95);
        for (int i = 0; i < 1000; i++) {
            calc.add(100.0);
        }
        assertThat(calc.getEstimate()).isCloseTo(100.0, within(1.0));
    }

    @Test
    void worksWithFewerThanFiveElements() {
        PercentileCalculator calc = new PercentileCalculator(0.5);
        calc.add(10);
        calc.add(20);
        calc.add(30);
        assertThat(calc.getEstimate()).isEqualTo(20.0);
    }

    @Test
    void memoryFootprintIsConstant() {
        PercentileCalculator calc = new PercentileCalculator(0.95);
        for (int i = 0; i < 1_000_000; i++) {
            calc.add(i);
        }
        assertThat(calc.getEstimate()).isCloseTo(950_000.0, within(10_000.0));
    }
}
