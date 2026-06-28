package com.moat.company;

public class DuplicateFinancialReportException extends RuntimeException {

    public DuplicateFinancialReportException(int fiscalYear) {
        super("Raport za rok " + fiscalYear + " już istnieje dla tej spółki.");
    }
}
