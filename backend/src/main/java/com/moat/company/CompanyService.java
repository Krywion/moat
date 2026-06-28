package com.moat.company;

import com.moat.api.model.CompanyDetailResponse;
import com.moat.api.model.CompanySummaryResponse;
import com.moat.api.model.CreateCompanyRequest;
import com.moat.api.model.FinancialForm;
import com.moat.api.model.FinancialReportResponse;
import com.moat.esef.EsefParser;
import com.moat.esef.ParsedEsef;
import com.moat.pipeline.PipelineContext;
import com.moat.pipeline.PipelineExecutor;
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
    private final EsefParser esefParser;
    private final CompanyMapper companyMapper;

    public CompanyService(CompanyRepository companyRepository,
                          FinancialReportRepository reportRepository,
                          UserRepository userRepository,
                          PipelineExecutor pipelineExecutor,
                          EsefParser esefParser,
                          CompanyMapper companyMapper) {
        this.companyRepository = companyRepository;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.pipelineExecutor = pipelineExecutor;
        this.esefParser = esefParser;
        this.companyMapper = companyMapper;
    }

    @Transactional
    public CompanyDetailResponse createCompany(UUID ownerId, CreateCompanyRequest req) {
        Company company = new Company();
        company.setOwner(userRepository.getReferenceById(ownerId));
        company.setName(req.getName());
        company.setTicker(req.getTicker());
        company.setCreatedAt(OffsetDateTime.now());
        company = companyRepository.save(company);
        pipelineExecutor.run(new PipelineContext(company, req.getFinancials()));
        return toDetail(company);
    }

    @Transactional
    public FinancialReportResponse updateFinancials(UUID ownerId, UUID companyId, FinancialForm form) {
        Company company = requireOwnedCompany(ownerId, companyId);
        FinancialReport report = pipelineExecutor.run(new PipelineContext(company, form));
        FinancialReport prior = reportRepository
                .findByCompanyIdAndFiscalYear(companyId, report.getFiscalYear() - 1).orElse(null);
        return companyMapper.toReport(report, prior);
    }

    @Transactional(readOnly = true)
    public List<CompanySummaryResponse> listCompanies(UUID ownerId) {
        List<CompanySummaryResponse> result = new ArrayList<>();
        for (Company company : companyRepository.findByOwnerId(ownerId)) {
            FinancialReport latest = reportRepository
                    .findFirstByCompanyIdOrderByFiscalYearDesc(company.getId()).orElse(null);
            result.add(companyMapper.toSummary(company, latest));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public CompanyDetailResponse getCompany(UUID ownerId, UUID companyId) {
        return toDetail(requireOwnedCompany(ownerId, companyId));
    }

    @Transactional
    public FinancialReportResponse addFinancialsFromEsef(UUID ownerId, UUID companyId, byte[] xbri) {
        Company company = requireOwnedCompany(ownerId, companyId);
        ParsedEsef parsed = esefParser.parse(xbri);
        if (!parsed.companyName().equals(company.getName())) {
            throw new CompanyMismatchException(parsed.companyName(), company.getName());
        }
        FinancialReport report = pipelineExecutor.run(new PipelineContext(company, parsed.data()));
        FinancialReport prior = reportRepository
                .findByCompanyIdAndFiscalYear(companyId, report.getFiscalYear() - 1).orElse(null);
        return companyMapper.toReport(report, prior);
    }

    @Transactional
    public CompanyDetailResponse createCompanyFromEsef(UUID ownerId, byte[] xbri, String ticker) {
        ParsedEsef parsed = esefParser.parse(xbri);
        Company company = new Company();
        company.setOwner(userRepository.getReferenceById(ownerId));
        company.setName(parsed.companyName());
        company.setTicker(ticker);
        company.setCreatedAt(OffsetDateTime.now());
        company = companyRepository.save(company);
        pipelineExecutor.run(new PipelineContext(company, parsed.data()));
        return toDetail(company);
    }

    private Company requireOwnedCompany(UUID ownerId, UUID companyId) {
        return companyRepository.findByIdAndOwnerId(companyId, ownerId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));
    }

    private CompanyDetailResponse toDetail(Company company) {
        return companyMapper.toDetail(company,
                reportRepository.findByCompanyIdOrderByFiscalYearAsc(company.getId()));
    }
}
