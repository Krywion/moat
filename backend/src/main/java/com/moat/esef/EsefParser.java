package com.moat.esef;

import com.moat.pipeline.FinancialData;
import org.springframework.stereotype.Component;

@Component
public class EsefParser {

    private final EsefPackageReader reader;
    private final IxbrlParser ixbrlParser;
    private final EsefFinancialMapper mapper;

    public EsefParser(EsefPackageReader reader, IxbrlParser ixbrlParser, EsefFinancialMapper mapper) {
        this.reader = reader;
        this.ixbrlParser = ixbrlParser;
        this.mapper = mapper;
    }

    public ParsedEsef parse(byte[] xbri) {
        byte[] xhtml = reader.readReport(xbri);
        ParsedDocument doc = ixbrlParser.parse(xhtml);
        FinancialData data = mapper.map(doc);
        String name = (doc.entityName() != null && !doc.entityName().isBlank())
                ? doc.entityName().trim() : "Unknown";
        return new ParsedEsef(name, data);
    }
}
