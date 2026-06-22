package com.moat.company.dto;

import java.util.List;
import java.util.UUID;

public record CompanyDetailResponse(
        UUID id, String name, String ticker, List<FinancialReportResponse> reports) {
}
