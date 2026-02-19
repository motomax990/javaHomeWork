package org.dynamicArray;

import org.dynamicArray.allocators.ArrayAllocator;
import org.dynamicArray.capacitors.*;
import org.dynamicArray.serializers.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("=== Стратегии расширения ===");
        CapacityStrategy doubling = new DoublingStrategy();
        CapacityStrategy fixed = new FixedIncrementStrategy(3);
        CapacityStrategy golden = new GoldenRatioStrategy();

        int cap = 5;
        System.out.println("Текущая ёмкость: " + cap + ", min=7");
        System.out.println("Doubling: " + doubling.nextCapacity(cap, 7));
        System.out.println("Fixed+3: " + fixed.nextCapacity(cap, 7));
        System.out.println("Golden: " + golden.nextCapacity(cap, 7));

        DynamicArray<String> array = new DynamicArrayList<>(
                new ArrayAllocator<>(String.class, new FixedIncrementStrategy(2))
        );
        array.add("Java");
        array.add("Python");
        array.add("C++");
        array.add("JavaScript");
        array.add("Rust");
        System.out.println("\nМассив: " + array);

        System.out.println("\n=== Сериализация ===");
        String[] formats = {"json", "xml", "csv", "bin"};
        for (String fmt : formats) {
            ArraySerializer serializer = SerializerFactory.forFormat("test." + fmt);
            String serialized = serializer.serialize(array);
            System.out.println(fmt.toUpperCase() + ": " + serialized.substring(0, Math.min(50, serialized.length())) + "...");
            // Сохраняем в файл
            Files.writeString(Path.of("array." + fmt), serialized);
        }

        System.out.println("\n=== Загрузка из файлов ===");
        for (String fmt : formats) {
            String content = Files.readString(Path.of("array." + fmt));
            ArraySerializer serializer = SerializerFactory.forFormat("test." + fmt);
            DynamicArray<?> loaded = serializer.deserialize(content);
            System.out.println(fmt + " size: " + loaded.size() + ", first: " + loaded.get(0));
        }

        System.out.println("\n=== Binary с числами ===");
        DynamicArray<Integer> numbers = new DynamicArrayList<>(Integer.class);
        numbers.add(100);
        numbers.add(200);
        numbers.add(300);
        BinaryArraySerializer binSer = new BinaryArraySerializer();
        String binData = binSer.serialize(numbers);
        DynamicArray<?> loadedNumbers = binSer.deserialize(binData);
        System.out.println("Loaded numbers: " + loadedNumbers.get(0) + ", " + loadedNumbers.get(1) + ", " + loadedNumbers.get(2));
    }
}