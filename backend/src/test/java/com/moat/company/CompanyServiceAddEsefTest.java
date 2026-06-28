package com.moat.company;

import com.moat.api.model.FinancialReportResponse;
import com.moat.esef.EsefParser;
import com.moat.esef.ParsedEsef;
import com.moat.pipeline.FinancialData;
import com.moat.pipeline.PipelineContext;
import com.moat.pipeline.PipelineExecutor;
import com.moat.pipeline.WarningFlagEvaluator;
import com.moat.report.FinancialReport;
import com.moat.report.FinancialReportRepository;
import com.moat.user.User;
import com.moat.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CompanyServiceAddEsefTest {

    private final CompanyRepository companyRepository = mock(CompanyRepository.class);
    private final FinancialReportRepository reportRepository = mock(FinancialReportRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PipelineExecutor pipelineExecutor = mock(PipelineExecutor.class);
    private final EsefParser esefParser = mock(EsefParser.class);
    private final CompanyService service = new CompanyService(
            companyRepository, reportRepository, userRepository, pipelineExecutor,
            esefParser, new CompanyMapper(new WarningFlagEvaluator()));

    private Company ownedCompany(UUID ownerId, String name) {
        Company c = new Company();
        c.setId(UUID.randomUUID());
        c.setOwner(new User());
        c.setName(name);
        c.setCreatedAt(OffsetDateTime.now());
        return c;
    }

    @Test
    void addFinancialsFromEsef_matchingName_succeeds() {
        UUID ownerId = UUID.randomUUID();
        Company company = ownedCompany(ownerId, "Rainbow Tours S.A.");

        FinancialData data = new FinancialData(2025, "PLN", BigDecimal.valueOf(1000), null, null,
                BigDecimal.valueOf(100), null, null, BigDecimal.valueOf(500), null);
        when(esefParser.parse(any())).thenReturn(new ParsedEsef("Rainbow Tours S.A.", data));
        when(companyRepository.findByIdAndOwnerId(company.getId(), ownerId))
                .thenReturn(Optional.of(company));

        FinancialReport report = new FinancialReport();
        report.setFiscalYear(2025);
        report.setCurrency("PLN");
        report.setRevenue(BigDecimal.valueOf(1000));
        when(pipelineExecutor.run(any(PipelineContext.class))).thenReturn(report);
        when(reportRepository.findByCompanyIdAndFiscalYear(any(), any(int.class)))
                .thenReturn(Optional.empty());

        FinancialReportResponse result = service.addFinancialsFromEsef(
                ownerId, company.getId(), new byte[]{1, 2});

        assertThat(result.getFiscalYear()).isEqualTo(2025);
    }

    @Test
    void addFinancialsFromEsef_mismatchedName_throwsCompanyMismatchException() {
        UUID ownerId = UUID.randomUUID();
        Company company = ownedCompany(ownerId, "Acme Corp");

        FinancialData data = new FinancialData(2025, "PLN", BigDecimal.valueOf(1000), null, null,
                BigDecimal.valueOf(100), null, null, BigDecimal.valueOf(500), null);
        when(esefParser.parse(any())).thenReturn(new ParsedEsef("Rainbow Tours S.A.", data));
        when(companyRepository.findByIdAndOwnerId(company.getId(), ownerId))
                .thenReturn(Optional.of(company));

        assertThatThrownBy(() -> service.addFinancialsFromEsef(
                ownerId, company.getId(), new byte[]{1, 2}))
                .isInstanceOf(CompanyMismatchException.class)
                .hasMessageContaining("Rainbow Tours S.A.")
                .hasMessageContaining("Acme Corp");
    }

    @Test
    void addFinancialsFromEsef_duplicateYear_throwsDuplicateFinancialReportException() {
        UUID ownerId = UUID.randomUUID();
        Company company = ownedCompany(ownerId, "Rainbow Tours S.A.");

        FinancialData data = new FinancialData(2025, "PLN", BigDecimal.valueOf(1000), null, null,
                BigDecimal.valueOf(100), null, null, BigDecimal.valueOf(500), null);
        when(esefParser.parse(any())).thenReturn(new ParsedEsef("Rainbow Tours S.A.", data));
        when(companyRepository.findByIdAndOwnerId(company.getId(), ownerId))
                .thenReturn(Optional.of(company));

        FinancialReport existing = new FinancialReport();
        existing.setFiscalYear(2025);
        when(reportRepository.findByCompanyIdAndFiscalYear(company.getId(), 2025))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.addFinancialsFromEsef(
                ownerId, company.getId(), new byte[]{1, 2}))
                .isInstanceOf(DuplicateFinancialReportException.class)
                .hasMessageContaining("2025");

        verify(pipelineExecutor, never()).run(any(PipelineContext.class));
    }
}
