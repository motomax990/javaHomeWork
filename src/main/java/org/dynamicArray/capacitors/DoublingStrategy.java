package org.dynamicArray.capacitors;

public class DoublingStrategy implements CapacityStrategy {
    @Override
    public int nextCapacity(int currentCapacity, int minCapacity) {
        if (currentCapacity == 0) return Math.max(1, minCapacity);
        int newCap = currentCapacity * 2;
        return newCap > minCapacity ? newCap : minCapacity;
    }
}