package ru.newsaggregator.fetch;

public class FetchException extends RuntimeException {

    public FetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
