package com.moat.report;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialReportRepository extends JpaRepository<FinancialReport, UUID> {

    Optional<FinancialReport> findByCompanyIdAndFiscalYear(UUID companyId, int fiscalYear);

    List<FinancialReport> findByCompanyIdOrderByFiscalYearAsc(UUID companyId);

    Optional<FinancialReport> findFirstByCompanyIdOrderByFiscalYearDesc(UUID companyId);
}
