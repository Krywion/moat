package com.moat.esef;

import java.time.LocalDate;

public record Context(
        String id,
        boolean instant,
        LocalDate start,
        LocalDate end,
        boolean hasDimensions) {
}
