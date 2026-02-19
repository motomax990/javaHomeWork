package org.srv;

import java.util.concurrent.ThreadLocalRandom;

public class DefaultConnectionManager implements ConnectionManager {
    private static final double FAULTY_PROBABILITY = 0.5;

    @Override
    public Connection getConnection() {
        if (ThreadLocalRandom.current().nextDouble() < FAULTY_PROBABILITY) {
            return new FaultyConnection();
        } else {
            return new StableConnection();
        }
    }
}