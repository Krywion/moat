package com.moat.esef;

import com.moat.pipeline.FinancialData;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class EsefFinancialMapper {

    private static final String[] DEBT_TAGS = {
            "ifrs-full:CurrentBorrowings",
            "ifrs-full:NoncurrentBorrowings",
            "ifrs-full:OtherCurrentFinancialLiabilities",
            "ifrs-full:OtherNoncurrentFinancialLiabilities",
            "ifrs-full:CurrentLeaseLiabilities",
            "ifrs-full:NoncurrentLeaseLiabilities"
    };

    private final ContextResolver resolver;
    private final IxbrlValueParser valueParser;

    public EsefFinancialMapper(ContextResolver resolver, IxbrlValueParser valueParser) {
        this.resolver = resolver;
        this.valueParser = valueParser;
    }

    public FinancialData map(ParsedDocument doc) {
        BigDecimal revenue = value(doc, "ifrs-full:Revenue");
        BigDecimal ebit = value(doc, "ifrs-full:ProfitLossFromOperatingActivities", "ifrs-full:OperatingProfitLoss");
        BigDecimal depreciation = value(doc, "ifrs-full:AdjustmentsForDepreciationAndAmortisationExpense");
        BigDecimal netProfit = value(doc, "ifrs-full:ProfitLoss");
        BigDecimal equity = value(doc, "ifrs-full:Equity", "ifrs-full:EquityAttributableToOwnersOfParent");
        BigDecimal operatingCashFlow = value(doc, "ifrs-full:CashFlowsFromUsedInOperatingActivities");
        BigDecimal cash = value(doc, "ifrs-full:CashAndCashEquivalents");
        BigDecimal totalDebt = sumDebt(doc);
        BigDecimal netDebt = (totalDebt != null && cash != null) ? totalDebt.subtract(cash) : null;

        int fiscalYear = doc.reportingDate() != null ? doc.reportingDate().getYear() : 0;
        String currency = currency(doc);

        return new FinancialData(fiscalYear, currency, revenue, ebit, depreciation,
                netProfit, totalDebt, netDebt, equity, operatingCashFlow);
    }

    private BigDecimal value(ParsedDocument doc, String... tags) {
        for (String tag : tags) {
            Optional<Fact> fact = resolver.resolve(doc, tag);
            if (fact.isPresent()) {
                BigDecimal v = valueParser.parse(fact.get().rawValue(), fact.get().scale(),
                        fact.get().negative(), fact.get().format());
                if (v != null) {
                    return v;
                }
            }
        }
        return null;
    }

    private BigDecimal sumDebt(ParsedDocument doc) {
        BigDecimal sum = null;
        for (String tag : DEBT_TAGS) {
            BigDecimal v = value(doc, tag);
            if (v != null) {
                sum = (sum == null) ? v : sum.add(v);
            }
        }
        return sum;
    }

    private String currency(ParsedDocument doc) {
        Fact f = resolver.resolve(doc, "ifrs-full:Revenue")
                .orElse(doc.facts().isEmpty() ? null : doc.facts().get(0));
        return f == null ? null : doc.units().get(f.unitRef());
    }
}
