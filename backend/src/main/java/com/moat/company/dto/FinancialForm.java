package com.moat.company.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record FinancialForm(
        @Min(1900) @Max(2100) int fiscalYear,
        @NotBlank @Size(min = 3, max = 3) String currency,
        BigDecimal revenue,
        BigDecimal ebit,
        BigDecimal depreciation,
        BigDecimal netProfit,
        BigDecimal totalDebt,
        BigDecimal netDebt,
        BigDecimal equity,
        BigDecimal operatingCashFlow) {
}
