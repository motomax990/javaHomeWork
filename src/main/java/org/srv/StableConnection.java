package org.srv;

public class StableConnection implements Connection {
    @Override
    public void execute(String command) {
        System.out.println("StableConnection выополняется: " + command);
    }

    @Override
    public void close() {
        System.out.println("StableConnection закрыто");
    }
}