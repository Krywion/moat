package com.moat.report;

import com.moat.company.Company;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Sprawozdanie finansowe spółki za dany rok wraz z policzonymi wskaźnikami.
 *
 * <p>Zasada (ROADMAP sekcja 5): wskaźnik nigdy nie jest zapisany bez surowych
 * danych, z których powstał. Kolumny liczone i rynkowe są nullable — brakująca
 * dana jest obsłużona jawnie (np. niekompletne otagowanie ESEF, brak danych
 * rynkowych z API).
 */
@Entity
@Table(
        name = "financial_reports",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_report_company_year",
                columnNames = {"company_id", "fiscal_year"}))
@Getter
@Setter
@NoArgsConstructor
public class FinancialReport {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "fiscal_year", nullable = false)
    private int fiscalYear;

    /** Kod waluty sprawozdania (ISO 4217), np. PLN, EUR. */
    @Column(nullable = false, length = 3)
    private String currency;

    // --- surowe dane ze sprawozdania ---
    @Column(precision = 19, scale = 4)
    private BigDecimal revenue;

    @Column(precision = 19, scale = 4)
    private BigDecimal ebit;

    @Column(precision = 19, scale = 4)
    private BigDecimal depreciation;

    @Column(name = "net_profit", precision = 19, scale = 4)
    private BigDecimal netProfit;

    @Column(name = "total_debt", precision = 19, scale = 4)
    private BigDecimal totalDebt;

    @Column(name = "net_debt", precision = 19, scale = 4)
    private BigDecimal netDebt;

    @Column(precision = 19, scale = 4)
    private BigDecimal equity;

    @Column(name = "operating_cash_flow", precision = 19, scale = 4)
    private BigDecimal operatingCashFlow;

    // --- wskaźniki liczone (nullable) ---
    @Column(precision = 19, scale = 4)
    private BigDecimal ebitda;

    @Column(name = "operating_margin", precision = 19, scale = 4)
    private BigDecimal operatingMargin;

    @Column(name = "ebitda_margin", precision = 19, scale = 4)
    private BigDecimal ebitdaMargin;

    @Column(name = "net_margin", precision = 19, scale = 4)
    private BigDecimal netMargin;

    @Column(precision = 19, scale = 4)
    private BigDecimal roe;

    @Column(name = "debt_to_equity", precision = 19, scale = 4)
    private BigDecimal debtToEquity;

    @Column(name = "revenue_growth_yoy", precision = 19, scale = 4)
    private BigDecimal revenueGrowthYoy;

    @Column(name = "profit_growth_yoy", precision = 19, scale = 4)
    private BigDecimal profitGrowthYoy;

    // --- dane rynkowe (nullable, Etap 2 — wymaga API zewnętrznego) ---
    @Column(name = "share_price", precision = 19, scale = 4)
    private BigDecimal sharePrice;

    @Column(name = "market_cap", precision = 19, scale = 4)
    private BigDecimal marketCap;

    @Column(precision = 19, scale = 4)
    private BigDecimal pe;

    @Column(name = "ev_ebitda", precision = 19, scale = 4)
    private BigDecimal evEbitda;

    @Column(precision = 19, scale = 4)
    private BigDecimal pbv;

    @Column(name = "dividend_yield", precision = 19, scale = 4)
    private BigDecimal dividendYield;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}
