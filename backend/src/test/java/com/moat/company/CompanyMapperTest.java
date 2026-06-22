package com.moat.company;

import com.moat.api.model.FinancialReportResponse;
import com.moat.pipeline.WarningFlagEvaluator;
import com.moat.report.FinancialReport;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyMapperTest {

    private final CompanyMapper mapper = new CompanyMapper(new WarningFlagEvaluator());

    private FinancialReport report(int year) {
        FinancialReport r = new FinancialReport();
        r.setFiscalYear(year);
        r.setCurrency("PLN");
        r.setRevenue(BigDecimal.valueOf(1000));
        r.setNetProfit(BigDecimal.valueOf(100));
        r.setNetMargin(BigDecimal.valueOf(0.1));
        r.setPe(BigDecimal.valueOf(20));
        r.setCreatedAt(OffsetDateTime.now());
        return r;
    }

    @Test
    void toReport_copiesFieldsAndComputesFlags() {
        FinancialReportResponse resp = mapper.toReport(report(2024), null);

        assertThat(resp.getFiscalYear()).isEqualTo(2024);
        assertThat(resp.getCurrency()).isEqualTo("PLN");
        assertThat(resp.getRevenue()).isEqualByComparingTo("1000");
        assertThat(resp.getPe()).isEqualByComparingTo("20");
        assertThat(resp.getFlags()).isNotNull();
    }
}
