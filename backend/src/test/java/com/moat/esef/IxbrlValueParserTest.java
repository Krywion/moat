package com.moat.esef;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class IxbrlValueParserTest {

    private final IxbrlValueParser parser = new IxbrlValueParser();

    @Test
    void parsesCommaDecimalWithScale() {
        // "1 234,5" * 10^3 = 1234500
        BigDecimal v = parser.parse("1 234,5", 3, false, "ixt:num-comma-decimal");
        assertThat(v).isEqualByComparingTo("1234500");
    }

    @Test
    void appliesNegativeSign() {
        BigDecimal v = parser.parse("100", 0, true, "ixt:num-comma-decimal");
        assertThat(v).isEqualByComparingTo("-100");
    }

    @Test
    void parsesDotDecimal() {
        BigDecimal v = parser.parse("1,234.50", 0, false, "ixt:num-dot-decimal");
        assertThat(v).isEqualByComparingTo("1234.50");
    }

    @Test
    void nullScaleMeansNoMultiplier() {
        BigDecimal v = parser.parse("250", null, false, "ixt:num-comma-decimal");
        assertThat(v).isEqualByComparingTo("250");
    }

    @Test
    void blankOrDashIsNull() {
        assertThat(parser.parse("  ", 0, false, "ixt:num-comma-decimal")).isNull();
        assertThat(parser.parse("-", 0, false, "ixt:num-comma-decimal")).isNull();
        assertThat(parser.parse(null, 0, false, null)).isNull();
    }
}
