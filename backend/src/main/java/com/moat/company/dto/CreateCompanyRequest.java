package com.moat.company.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(
        @NotBlank String name,
        String ticker,
        @Valid FinancialForm financials) {
}
