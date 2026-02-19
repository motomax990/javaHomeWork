package org.dynamicArray.allocators;

import org.dynamicArray.capacitors.CapacityStrategy;
import java.lang.reflect.Array;

public class ArrayAllocator<T> implements Allocator<T> {
    private final Class<T> type;
    private final CapacityStrategy strategy;

    public ArrayAllocator(Class<T> type, CapacityStrategy strategy) {
        this.type = type;
        this.strategy = strategy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T[] newArray(int requiredCapacity) {
        return (T[]) Array.newInstance(type, requiredCapacity);
    }

    @Override
    public CapacityStrategy getCapacityStrategy() {
        return strategy;
    }
}