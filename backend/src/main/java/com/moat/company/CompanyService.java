package com.moat.company;

import com.moat.company.dto.CompanyDetailResponse;
import com.moat.company.dto.CompanySummaryResponse;
import com.moat.company.dto.CreateCompanyRequest;
import com.moat.company.dto.FinancialForm;
import com.moat.company.dto.FinancialReportResponse;
import com.moat.pipeline.PipelineContext;
import com.moat.pipeline.PipelineExecutor;
import com.moat.pipeline.WarningFlagEvaluator;
import com.moat.report.FinancialReport;
import com.moat.report.FinancialReportRepository;
import com.moat.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final FinancialReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PipelineExecutor pipelineExecutor;
    private final WarningFlagEvaluator flagEvaluator;

    public CompanyService(CompanyRepository companyRepository,
                          FinancialReportRepository reportRepository,
                          UserRepository userRepository,
                          PipelineExecutor pipelineExecutor,
                          WarningFlagEvaluator flagEvaluator) {
        this.companyRepository = companyRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.pipelineExecutor = pipelineExecutor;
        this.flagEvaluator = flagEvaluator;
    }

    @Transactional
    public CompanyDetailResponse createCompany(UUID ownerId, CreateCompanyRequest req) {
        Company company = new Company();
        company.setOwner(userRepository.getReferenceById(ownerId));
        company.setName(req.name());
        company.setTicker(req.ticker());
        company.setCreatedAt(OffsetDateTime.now());
        company = companyRepository.save(company);

        pipelineExecutor.run(new PipelineContext(company, req.financials()));
        return toDetailResponse(company);
    }

    @Transactional
    public FinancialReportResponse updateFinancials(UUID ownerId, UUID companyId, FinancialForm form) {
        Company company = requireOwnedCompany(ownerId, companyId);
        FinancialReport report = pipelineExecutor.run(new PipelineContext(company, form));
        FinancialReport prior = reportRepository
                .findByCompanyIdAndFiscalYear(companyId, report.getFiscalYear() - 1)
                .orElse(null);
        return FinancialReportResponse.from(report, prior, flagEvaluator);
    }

    @Transactional(readOnly = true)
    public List<CompanySummaryResponse> listCompanies(UUID ownerId) {
        List<CompanySummaryResponse> result = new ArrayList<>();
        for (Company company : companyRepository.findByOwnerId(ownerId)) {
            FinancialReport latest = reportRepository
                    .findFirstByCompanyIdOrderByFiscalYearDesc(company.getId())
                    .orElse(null);
            result.add(new CompanySummaryResponse(
                    company.getId(), company.getName(), company.getTicker(),
                    latest == null ? null : latest.getFiscalYear(),
                    latest == null ? null : latest.getNetMargin()));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CompanyDetailResponse getCompany(UUID ownerId, UUID companyId) {
        return toDetailResponse(requireOwnedCompany(ownerId, companyId));
    }

    private Company requireOwnedCompany(UUID ownerId, UUID companyId) {
        return companyRepository.findByIdAndOwnerId(companyId, ownerId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }

    private CompanyDetailResponse toDetailResponse(Company company) {
        List<FinancialReport> reports =
                reportRepository.findByCompanyIdOrderByFiscalYearAsc(company.getId());
        List<FinancialReportResponse> responses = new ArrayList<>();
        FinancialReport prior = null;
        for (FinancialReport report : reports) {
            FinancialReport priorForThis =
                    (prior != null && prior.getFiscalYear() == report.getFiscalYear() - 1) ? prior : null;
            responses.add(FinancialReportResponse.from(report, priorForThis, flagEvaluator));
            prior = report;
        }
        return new CompanyDetailResponse(
                company.getId(), company.getName(), company.getTicker(), responses);
    }
}
