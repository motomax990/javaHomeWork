package org.dynamicArray.capacitors;

public class GoldenRatioStrategy implements CapacityStrategy {
    private static final double GOLDEN_RATIO = 1.618;

    @Override
    public int nextCapacity(int currentCapacity, int minCapacity) {
        if (currentCapacity == 0) return Math.max(1, minCapacity);
        int newCap = (int) (currentCapacity * GOLDEN_RATIO) + 1;
        return newCap > minCapacity ? newCap : minCapacity;
    }
}