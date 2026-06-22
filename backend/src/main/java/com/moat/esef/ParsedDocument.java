package com.moat.esef;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record ParsedDocument(
        List<Fact> facts,
        Map<String, Context> contexts,
        Map<String, String> units,
        String entityName,
        LocalDate reportingDate) {
}
