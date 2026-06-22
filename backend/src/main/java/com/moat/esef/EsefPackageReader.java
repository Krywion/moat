package com.moat.esef;

import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class EsefPackageReader {

    /** Znajduje plik raportu iXBRL (reports/*.xhtml) w paczce ZIP .xbri. */
    public byte[] readReport(byte[] xbri) {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(xbri))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String name = entry.getName().toLowerCase();
                if (!entry.isDirectory() && name.contains("/reports/") && name.endsWith(".xhtml")) {
                    return zis.readAllBytes();
                }
            }
        } catch (IOException e) {
            throw new EsefParseException("Cannot read ESEF package", e);
        }
        throw new EsefParseException("No report file (reports/*.xhtml) in ESEF package");
    }
}
