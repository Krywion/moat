package com.moat.esef;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

@Component
public class ContextResolver {

    /**
     * Wybiera fakt dla danego tagu w bieżącym okresie raportu (kontekst bez
     * wymiarów). Przy wielu kandydatach preferuje najwcześniejszy start
     * (pełny rok dla pozycji duration).
     */
    public Optional<Fact> resolve(ParsedDocument doc, String tag) {
        LocalDate reportingDate = doc.reportingDate();
        if (reportingDate == null) {
            return Optional.empty();
        }
        return doc.facts().stream()
                .filter(f -> tag.equals(f.tag()))
                .filter(f -> {
                    Context c = doc.contexts().get(f.contextRef());
                    return c != null && !c.hasDimensions() && reportingDate.equals(c.end());
                })
                .min(Comparator.comparing(
                        f -> startOf(doc, f),
                        Comparator.nullsLast(Comparator.naturalOrder())));
    }

    private LocalDate startOf(ParsedDocument doc, Fact f) {
        Context c = doc.contexts().get(f.contextRef());
        return c == null ? null : c.start();
    }
}
