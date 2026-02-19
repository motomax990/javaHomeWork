package org.dynamicArray.serializers;

import org.dynamicArray.DynamicArray;
import org.dynamicArray.DynamicArrayList;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;

public class BinaryArraySerializer implements ArraySerializer {
    @Override
    public String serialize(DynamicArray<?> array) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            ArrayList<Object> list = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                list.add(array.get(i));
            }
            oos.writeObject(list);
            oos.flush();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public DynamicArray<?> deserialize(String data) {
        byte[] bytes = Base64.getDecoder().decode(data);
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
            ArrayList<Object> list = (ArrayList<Object>) ois.readObject();
            DynamicArray<Object> result = new DynamicArrayList<>(Object.class, list.size());
            for (Object item : list) {
                result.add(item);
            }
            return result;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}