package org.srv;

public interface Connection extends AutoCloseable {
    void execute(String command);
}