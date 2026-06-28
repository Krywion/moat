package com.moat.company;

public class CompanyMismatchException extends RuntimeException {

    public CompanyMismatchException(String parsedName, String expectedName) {
        super("Plik ESEF dotyczy innej spółki: " + parsedName + " (oczekiwano: " + expectedName + ")");
    }
}
