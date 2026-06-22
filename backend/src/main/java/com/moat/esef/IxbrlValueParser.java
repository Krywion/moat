package com.moat.esef;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class IxbrlValueParser {

    public BigDecimal parse(String rawValue, Integer scale, boolean negative, String format) {
        if (rawValue == null) {
            return null;
        }
        String s = rawValue.replace(' ', ' ').trim();
        if (s.isEmpty() || "-".equals(s)) {
            return null;
        }
        s = s.replaceAll("\\s", ""); // separatory tysięcy / spacje
        boolean comma = format != null && format.contains("comma-decimal");
        if (comma) {
            s = s.replace(".", "").replace(',', '.');
        } else {
            s = s.replace(",", ""); // dot-decimal lub domyślnie
        }
        BigDecimal value;
        try {
            value = new BigDecimal(s);
        } catch (NumberFormatException e) {
            return null;
        }
        if (scale != null && scale != 0) {
            value = value.movePointRight(scale);
        }
        return negative ? value.negate() : value;
    }
}
