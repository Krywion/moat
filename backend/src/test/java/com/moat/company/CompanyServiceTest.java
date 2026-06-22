package com.moat.company;

import com.moat.company.dto.CompanyDetailResponse;
import com.moat.company.dto.CreateCompanyRequest;
import com.moat.company.dto.FinancialForm;
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
    private final WarningFlagEvaluator flagEvaluator = new WarningFlagEvaluator();
    private final CompanyService service = new CompanyService(
            companyRepository, reportRepository, userRepository, pipelineExecutor, flagEvaluator);

    private FinancialForm form() {
        return new FinancialForm(2024, "PLN", BigDecimal.valueOf(1000), BigDecimal.valueOf(200),
                BigDecimal.valueOf(10), BigDecimal.valueOf(100), BigDecimal.valueOf(100), null,
                BigDecimal.valueOf(500), BigDecimal.valueOf(50));
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
                new CreateCompanyRequest("Acme", "ACM", form()));

        assertThat(result.name()).isEqualTo("Acme");
    }
}
