package org.dynamicArray.allocators;

import org.dynamicArray.capacitors.CapacityStrategy;

public interface Allocator<T> {
    T[] newArray(int requiredCapacity);
    CapacityStrategy getCapacityStrategy();
}