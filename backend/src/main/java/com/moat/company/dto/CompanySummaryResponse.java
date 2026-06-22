package com.moat.company.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CompanySummaryResponse(
        UUID id, String name, String ticker, Integer latestFiscalYear, BigDecimal netMargin) {
}
