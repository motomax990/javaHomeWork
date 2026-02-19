package org.srv;

import java.util.concurrent.ThreadLocalRandom;

public class FaultyConnection implements Connection {
    private static final double FAILURE_PROBABILITY = 0.5;

    @Override
    public void execute(String command) {
        if (ThreadLocalRandom.current().nextDouble() < FAILURE_PROBABILITY) {
            throw new ConnectionException("Не удалось выполнить команду FaultyConnection: " + command);
        }
        System.out.println("Выполнение FaultyConnection: " + command);
    }

    @Override
    public void close() {
        System.out.println("FaultyConnection закрыт");
    }
}