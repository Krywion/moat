package com.moat.company;

import com.moat.api.model.CompanyDetailResponse;
import com.moat.esef.EsefParser;
import com.moat.esef.ParsedEsef;
import com.moat.pipeline.FinancialData;
import com.moat.pipeline.PipelineExecutor;
import com.moat.pipeline.WarningFlagEvaluator;
import com.moat.report.FinancialReportRepository;
import com.moat.user.User;
import com.moat.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CompanyServiceEsefTest {

    private final CompanyRepository companyRepository = mock(CompanyRepository.class);
    private final FinancialReportRepository reportRepository = mock(FinancialReportRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PipelineExecutor pipelineExecutor = mock(PipelineExecutor.class);
    private final EsefParser esefParser = mock(EsefParser.class);
    private final CompanyService service = new CompanyService(
            companyRepository, reportRepository, userRepository, pipelineExecutor,
            esefParser, new CompanyMapper(new WarningFlagEvaluator()));

    @Test
    void createCompanyFromEsef_usesParsedNameAndRunsPipeline() {
        UUID ownerId = UUID.randomUUID();
        FinancialData data = new FinancialData(2025, "PLN", BigDecimal.valueOf(1000), null, null,
                BigDecimal.valueOf(100), null, null, BigDecimal.valueOf(500), null);
        when(esefParser.parse(any())).thenReturn(new ParsedEsef("Rainbow Tours S.A.", data));
        when(userRepository.getReferenceById(ownerId)).thenReturn(new User());
        when(companyRepository.save(any(Company.class))).thenAnswer(inv -> {
            Company c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });
        when(reportRepository.findByCompanyIdOrderByFiscalYearAsc(any())).thenReturn(List.of());

        CompanyDetailResponse result = service.createCompanyFromEsef(ownerId, new byte[]{1, 2}, "RBW");

        assertThat(result.getName()).isEqualTo("Rainbow Tours S.A.");
    }
}
