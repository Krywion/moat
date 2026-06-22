package com.moat.esef;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ContextResolverTest {

    private final ContextResolver resolver = new ContextResolver();

    private Fact fact(String tag, String ctx) {
        return new Fact(tag, ctx, "PLN", 3, false, "ixt:num-comma-decimal", "100");
    }

    private ParsedDocument doc(List<Fact> facts, Map<String, Context> ctx) {
        LocalDate reporting = LocalDate.of(2025, 12, 31);
        return new ParsedDocument(facts, ctx, Map.of("PLN", "PLN"), "Acme", reporting);
    }

    @Test
    void picksCurrentPeriodNonDimensionalFact() {
        Context current = new Context("Ccur", false, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), false);
        Context prior = new Context("Cprior", false, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), false);
        ParsedDocument d = doc(
                List.of(fact("ifrs-full:Revenue", "Cprior"), fact("ifrs-full:Revenue", "Ccur")),
                Map.of("Ccur", current, "Cprior", prior));

        assertThat(resolver.resolve(d, "ifrs-full:Revenue")).get()
                .extracting(Fact::contextRef).isEqualTo("Ccur");
    }

    @Test
    void skipsDimensionalContext() {
        Context dimensional = new Context("Cdim", false, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), true);
        ParsedDocument d = doc(
                List.of(fact("ifrs-full:Revenue", "Cdim")),
                Map.of("Cdim", dimensional));

        assertThat(resolver.resolve(d, "ifrs-full:Revenue")).isEmpty();
    }

    @Test
    void emptyWhenTagAbsent() {
        Context current = new Context("Ccur", false, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), false);
        ParsedDocument d = doc(List.of(fact("ifrs-full:Equity", "Ccur")), Map.of("Ccur", current));

        assertThat(resolver.resolve(d, "ifrs-full:Revenue")).isEmpty();
    }
}
