package com.moat.esef;

import com.moat.pipeline.FinancialData;

public record ParsedEsef(String companyName, FinancialData data) {
}
