package com.moat.company;

public class DuplicateCompanyException extends RuntimeException {

    private DuplicateCompanyException(String message) {
        super(message);
    }

    public static DuplicateCompanyException forName(String name) {
        return new DuplicateCompanyException("Spółka o nazwie \"" + name + "\" już istnieje na Twojej liście.");
    }

    public static DuplicateCompanyException forTicker(String ticker) {
        return new DuplicateCompanyException("Spółka z tickerem \"" + ticker + "\" już istnieje na Twojej liście.");
    }
}
