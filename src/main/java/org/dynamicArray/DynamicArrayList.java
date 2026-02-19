package org.dynamicArray;

import org.dynamicArray.allocators.Allocator;
import org.dynamicArray.allocators.ArrayAllocator;
import org.dynamicArray.capacitors.DoublingStrategy;
import java.util.Arrays;
import java.util.Objects;

public class DynamicArrayList<T> implements DynamicArray<T> {
    private T[] data;
    private int size;
    private final Allocator<T> allocator;

    public DynamicArrayList(Class<T> type) {
        this(type, 10, new ArrayAllocator<>(type, new DoublingStrategy()));
    }

    public DynamicArrayList(Class<T> type, int initialCapacity) {
        this(type, initialCapacity, new ArrayAllocator<>(type, new DoublingStrategy()));
    }

    public DynamicArrayList(Allocator<T> allocator) {
        this(null, 10, allocator);
    }

    public DynamicArrayList(int initialCapacity, Allocator<T> allocator) {
        this(null, initialCapacity, allocator);
    }

    private DynamicArrayList(Class<T> ignoredType, int initialCapacity, Allocator<T> allocator) {
        if (initialCapacity < 0) throw new IllegalArgumentException("Initial capacity cannot be negative");
        this.data = allocator.newArray(initialCapacity);
        this.size = 0;
        this.allocator = allocator;
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > data.length) {
            int newCapacity = allocator.getCapacityStrategy().nextCapacity(data.length, minCapacity);
            T[] newData = allocator.newArray(newCapacity);
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(Object element) {
        return indexOf(element) >= 0;
    }

    @Override
    public boolean add(T e) {
        ensureCapacity(size + 1);
        data[size++] = e;
        return true;
    }

    @Override
    public boolean containsAll(DynamicArray<?> c) {
        for (int i = 0; i < c.size(); i++) {
            if (!contains(c.get(i))) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(DynamicArray<? extends T> c) {
        return addAll(size, c);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean addAll(int index, DynamicArray<? extends T> c) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("Index out of range: " + index);
        int numNew = c.size();
        if (numNew == 0) return false;
        ensureCapacity(size + numNew);
        int moved = size - index;
        if (moved > 0) System.arraycopy(data, index, data, index + numNew, moved);
        for (int i = 0; i < numNew; i++) {
            data[index + i] = (T) c.get(i); // safe because ? extends T
        }
        size += numNew;
        return true;
    }

    @Override
    public boolean removeAll(DynamicArray<?> c) {
        boolean modified = false;
        int writeIdx = 0;
        for (int readIdx = 0; readIdx < size; readIdx++) {
            T value = data[readIdx];
            if (!c.contains(value)) {
                data[writeIdx++] = value;
            } else {
                modified = true;
            }
        }
        size = writeIdx;
        return modified;
    }

    @Override
    public boolean retainAll(DynamicArray<?> c) {
        boolean modified = false;
        int writeIdx = 0;
        for (int readIdx = 0; readIdx < size; readIdx++) {
            T value = data[readIdx];
            if (c.contains(value)) {
                data[writeIdx++] = value;
            } else {
                modified = true;
            }
        }
        size = writeIdx;
        return modified;
    }

    @Override
    public void sort() {
        throw new UnsupportedOperationException("sort not implemented for generic array");
    }

    @Override
    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index out of range: " + index);
        return data[index];
    }

    @Override
    public T set(int index, T element) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index out of range: " + index);
        T old = data[index];
        data[index] = element;
        return old;
    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException("Index out of range: " + index);
        ensureCapacity(size + 1);
        int moved = size - index;
        if (moved > 0) System.arraycopy(data, index, data, index + 1, moved);
        data[index] = element;
        size++;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException("Index out of range: " + index);
        T old = data[index];
        int moved = size - index - 1;
        if (moved > 0) System.arraycopy(data, index + 1, data, index, moved);
        data[--size] = null;
        return old;
    }

    @Override
    public int indexOf(Object element) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(data[i], element)) return i;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object element) {
        for (int i = size - 1; i >= 0; i--) {
            if (Objects.equals(data[i], element)) return i;
        }
        return -1;
    }
}