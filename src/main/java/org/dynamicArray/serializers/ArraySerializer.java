package org.dynamicArray.serializers;

import org.dynamicArray.DynamicArray;

public interface ArraySerializer {
    String serialize(DynamicArray<?> array);
    DynamicArray<?> deserialize(String data);
}