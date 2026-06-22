package com.moat.company;

import com.moat.api.model.CompanyDetailResponse;
import com.moat.api.model.CreateCompanyRequest;
import com.moat.api.model.FinancialForm;
import com.moat.pipeline.PipelineExecutor;
import com.moat.pipeline.WarningFlagEvaluator;
import com.moat.report.FinancialReport;
import com.moat.report.FinancialReportRepository;
import com.moat.user.User;
import com.moat.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompanyServiceTest {

    private final CompanyRepository companyRepository = mock(CompanyRepository.class);
    private final FinancialReportRepository reportRepository = mock(FinancialReportRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PipelineExecutor pipelineExecutor = mock(PipelineExecutor.class);
    private final com.moat.esef.EsefParser esefParser = mock(com.moat.esef.EsefParser.class);
    private final CompanyService service = new CompanyService(
            companyRepository, reportRepository, userRepository, pipelineExecutor,
            esefParser, new CompanyMapper(new WarningFlagEvaluator()));

    private FinancialForm form() {
        return new FinancialForm().fiscalYear(2024).currency("PLN")
                .revenue(BigDecimal.valueOf(1000)).ebit(BigDecimal.valueOf(200))
                .depreciation(BigDecimal.valueOf(10)).netProfit(BigDecimal.valueOf(100))
                .totalDebt(BigDecimal.valueOf(100)).equity(BigDecimal.valueOf(500))
                .operatingCashFlow(BigDecimal.valueOf(50));
    }

    @Test
    void getCompany_ofAnotherUser_throwsNotFound() {
        UUID ownerId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        when(companyRepository.findByIdAndOwnerId(companyId, ownerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCompany(ownerId, companyId))
                .isInstanceOf(CompanyNotFoundException.class);
    }

    @Test
    void createCompany_savesCompanyAndRunsPipeline() {
        UUID ownerId = UUID.randomUUID();
        when(userRepository.getReferenceById(ownerId)).thenReturn(new User());
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> {
            Company c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });
        when(reportRepository.findByCompanyIdOrderByFiscalYearAsc(any())).thenReturn(List.of());

        CompanyDetailResponse result = service.createCompany(ownerId,
                new CreateCompanyRequest().name("Acme").ticker("ACM").financials(form()));

        assertThat(result.getName()).isEqualTo("Acme");
    }
}
