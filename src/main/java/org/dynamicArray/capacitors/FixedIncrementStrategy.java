package org.dynamicArray.capacitors;

public class FixedIncrementStrategy implements CapacityStrategy {
    private final int increment;

    public FixedIncrementStrategy(int increment) {
        if (increment <= 0) throw new IllegalArgumentException("increment must be positive");
        this.increment = increment;
    }

    @Override
    public int nextCapacity(int currentCapacity, int minCapacity) {
        if (currentCapacity == 0) return Math.max(increment, minCapacity);
        int newCap = currentCapacity + increment;
        return newCap > minCapacity ? newCap : minCapacity;
    }
}