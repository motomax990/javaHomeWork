package org.logAnalyzer.exception;

public class LogAnalyzerException extends RuntimeException {

    public LogAnalyzerException(String message) {
        super(message);
    }

    public LogAnalyzerException(String message, Throwable cause) {
        super(message, cause);
    }
}
