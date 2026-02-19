package org.dynamicArray;

public interface DynamicArray<T> {
    int size();
    boolean isEmpty();
    boolean contains(Object element);
    boolean add(T e);
    boolean containsAll(DynamicArray<?> c);
    boolean addAll(DynamicArray<? extends T> c);
    boolean addAll(int index, DynamicArray<? extends T> c);
    boolean removeAll(DynamicArray<?> c);
    boolean retainAll(DynamicArray<?> c);
    default void sort() {
        throw new UnsupportedOperationException("sort not implemented");
    }
    void clear();
    T get(int index);
    T set(int index, T element);
    void add(int index, T element);
    T remove(int index);
    int indexOf(Object element);
    int lastIndexOf(Object element);
}