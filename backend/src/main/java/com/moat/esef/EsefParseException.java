package com.moat.esef;

public class EsefParseException extends RuntimeException {

    public EsefParseException(String message) {
        super(message);
    }

    public EsefParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
