package com.moat.esef;

public record Fact(
        String tag,
        String contextRef,
        String unitRef,
        Integer scale,
        boolean negative,
        String format,
        String rawValue) {
}
