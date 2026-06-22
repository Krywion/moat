package com.moat.esef;

import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class IxbrlParser {

    private static final String ENTITY_NAME_TAG =
            "ifrs-full:NameOfReportingEntityOrOtherMeansOfIdentification";
    private static final String XSI_NS = "http://www.w3.org/2001/XMLSchema-instance";

    /** Replaces Unicode thousand-separator spaces (NBSP U+00A0, thin space U+2009,
     *  narrow NBSP U+202F) with ordinary ASCII space so numeric parsing works. */
    private static String normalizeSpaces(String text) {
        return text
                .replace(' ', ' ')
                .replace(' ', ' ')
                .replace(' ', ' ');
    }

    public ParsedDocument parse(byte[] xhtml) {
        Map<String, Context> contexts = new HashMap<>();
        Map<String, String> units = new HashMap<>();
        List<Fact> facts = new ArrayList<>();
        String entityName = null;

        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, true);
        factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);

        try (InputStream in = new ByteArrayInputStream(xhtml)) {
            XMLStreamReader r = factory.createXMLStreamReader(in);
            while (r.hasNext()) {
                if (r.next() != XMLStreamConstants.START_ELEMENT) {
                    continue;
                }
                switch (r.getLocalName()) {
                    case "context" -> {
                        Context c = readContext(r, r.getAttributeValue(null, "id"));
                        if (c.id() != null) {
                            contexts.put(c.id(), c);
                        }
                    }
                    case "unit" -> {
                        String id = r.getAttributeValue(null, "id");
                        String currency = readUnitCurrency(r);
                        if (id != null && currency != null) {
                            units.put(id, currency);
                        }
                    }
                    case "nonFraction" -> facts.add(readNonFraction(r));
                    case "nonNumeric" -> {
                        if (ENTITY_NAME_TAG.equals(r.getAttributeValue(null, "name"))) {
                            String text = readElementText(r);
                            if (text != null && !text.isBlank()) {
                                entityName = text.trim();
                            }
                        }
                    }
                    default -> { }
                }
            }
            r.close();
        } catch (XMLStreamException | IOException e) {
            throw new EsefParseException("Invalid iXBRL document", e);
        }

        LocalDate reportingDate = contexts.values().stream()
                .map(Context::end).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);
        return new ParsedDocument(facts, contexts, units, entityName, reportingDate);
    }

    private Context readContext(XMLStreamReader r, String id) throws XMLStreamException {
        boolean instant = false;
        boolean hasDimensions = false;
        LocalDate start = null;
        LocalDate end = null;
        while (r.hasNext()) {
            int ev = r.next();
            if (ev == XMLStreamConstants.START_ELEMENT) {
                switch (r.getLocalName()) {
                    case "instant" -> { instant = true; end = parseDate(readElementText(r)); }
                    case "startDate" -> start = parseDate(readElementText(r));
                    case "endDate" -> end = parseDate(readElementText(r));
                    case "explicitMember", "typedMember" -> { hasDimensions = true; readElementText(r); }
                    default -> { }
                }
            } else if (ev == XMLStreamConstants.END_ELEMENT && "context".equals(r.getLocalName())) {
                break;
            }
        }
        return new Context(id, instant, start, end, hasDimensions);
    }

    private String readUnitCurrency(XMLStreamReader r) throws XMLStreamException {
        String currency = null;
        while (r.hasNext()) {
            int ev = r.next();
            if (ev == XMLStreamConstants.START_ELEMENT && "measure".equals(r.getLocalName())) {
                String m = readElementText(r).trim();
                int colon = m.indexOf(':');
                String code = colon >= 0 ? m.substring(colon + 1) : m;
                if (currency == null) {
                    currency = code;
                }
            } else if (ev == XMLStreamConstants.END_ELEMENT && "unit".equals(r.getLocalName())) {
                break;
            }
        }
        return currency;
    }

    private Fact readNonFraction(XMLStreamReader r) throws XMLStreamException {
        String tag = r.getAttributeValue(null, "name");
        String contextRef = r.getAttributeValue(null, "contextRef");
        String unitRef = r.getAttributeValue(null, "unitRef");
        String scaleStr = r.getAttributeValue(null, "scale");
        boolean negative = "-".equals(r.getAttributeValue(null, "sign"));
        String format = r.getAttributeValue(null, "format");
        String nil = r.getAttributeValue(XSI_NS, "nil");
        Integer scale = scaleStr == null ? null : Integer.valueOf(scaleStr.trim());
        String raw = readElementText(r);
        if ("true".equals(nil)) {
            raw = null;
        }
        return new Fact(tag, contextRef, unitRef, scale, negative, format, raw);
    }

    /** Accumulates text until END_ELEMENT of the current element (called after START). */
    private String readElementText(XMLStreamReader r) throws XMLStreamException {
        StringBuilder sb = new StringBuilder();
        int depth = 1;
        while (depth > 0 && r.hasNext()) {
            int ev = r.next();
            switch (ev) {
                case XMLStreamConstants.START_ELEMENT -> depth++;
                case XMLStreamConstants.END_ELEMENT -> depth--;
                case XMLStreamConstants.CHARACTERS, XMLStreamConstants.CDATA ->
                        sb.append(normalizeSpaces(r.getText()));
                case XMLStreamConstants.ENTITY_REFERENCE -> sb.append(' ');
                default -> { }
            }
        }
        return sb.toString();
    }

    private LocalDate parseDate(String s) {
        if (s == null) {
            return null;
        }
        try {
            return LocalDate.parse(s.trim());
        } catch (RuntimeException e) {
            return null;
        }
    }
}
