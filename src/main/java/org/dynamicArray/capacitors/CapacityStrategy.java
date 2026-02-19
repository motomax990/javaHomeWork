package org.dynamicArray.capacitors;

public interface CapacityStrategy {
    int nextCapacity(int currentCapacity, int minCapacity);
}