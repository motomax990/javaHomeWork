package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvParserTest {

    @TempDir
    Path tempDir;

    private CsvParser parser;

    @BeforeEach
    void setUp() {
        parser = new CsvParser();
    }

    @Test
    @DisplayName("Сохранение и чтение обычного CSV с примитивами и @CsvName")
    void saveAndParseBasicCsv() {
        Path file = tempDir.resolve("people.csv");
        List<Person> people = Arrays.asList(
                new Person(1, "Ivan Ivanov", 25),
                new Person(2, "Maria Petrova", 30)
        );

        parser.saveToCsv(file.toString(), people, Person.class);
        List<Person> read = parser.parseFromCsv(file.toString(), Person.class);

        assertEquals(2, read.size());
        assertEquals(1, read.get(0).getId());
        assertEquals("Ivan Ivanov", read.get(0).getName());
        assertEquals(25, read.get(0).getAge());
        assertEquals(2, read.get(1).getId());
        assertEquals("Maria Petrova", read.get(1).getName());
        assertEquals(30, read.get(1).getAge());
    }

    @Test
    @DisplayName("Заголовок CSV использует значение из @CsvName, а не имя поля")
    void csvHeaderUsesAnnotationValue() throws IOException {
        Path file = tempDir.resolve("header.csv");
        parser.saveToCsv(file.toString(), List.of(new Person(1, "X", 10)), Person.class);
        String content = Files.readString(file);
        String header = content.lines().findFirst().orElseThrow();
        assertEquals("id,full_name,age", header);
    }

    @Test
    @DisplayName("Поддержка GZIP — файл реально сжатый")
    void gzipFormat() throws IOException {
        Path file = tempDir.resolve("people.csv.gz");
        List<Person> people = List.of(new Person(7, "Gz Test", 99));

        parser.saveToCsv(file.toString(), people, Person.class);

        try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file.toFile()))) {
            assertNotNull(gzis);
        }
        List<Person> read = parser.parseFromCsv(file.toString(), Person.class);
        assertEquals(1, read.size());
        assertEquals("Gz Test", read.get(0).getName());
    }

    @Test
    @DisplayName("Поддержка расширения .gz")
    void gzExtension() {
        Path file = tempDir.resolve("data.gz");
        parser.saveToCsv(file.toString(), List.of(new Person(1, "A", 1)), Person.class);
        List<Person> read = parser.parseFromCsv(file.toString(), Person.class);
        assertEquals(1, read.size());
    }

    @Test
    @DisplayName("Поддержка ZIP — внутри один CSV-файл")
    void zipFormat() throws IOException {
        Path file = tempDir.resolve("people.zip");
        parser.saveToCsv(file.toString(), List.of(new Person(5, "Zip", 18)), Person.class);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file.toFile()))) {
            ZipEntry entry = zis.getNextEntry();
            assertNotNull(entry);
            assertTrue(entry.getName().endsWith(".csv"));
        }
        List<Person> read = parser.parseFromCsv(file.toString(), Person.class);
        assertEquals("Zip", read.get(0).getName());
    }

    @Test
    @DisplayName("Вложенные объекты сериализуются через точечную нотацию")
    void nestedObjectsDotNotation() throws IOException {
        Path file = tempDir.resolve("emps.csv");
        List<Employee> employees = Arrays.asList(
                new Employee(1, "Ivan Ivanov", new Address("Lenina 10", 42)),
                new Employee(2, "Maria Petrova", new Address("Pushkina 5", 7))
        );
        parser.saveToCsv(file.toString(), employees, Employee.class);

        String content = Files.readString(file);
        String header = content.lines().findFirst().orElseThrow();
        assertEquals("id,full_name,address.street,address.flat", header);

        List<Employee> read = parser.parseFromCsv(file.toString(), Employee.class);
        assertEquals(2, read.size());
        assertEquals("Lenina 10", read.get(0).getAddress().getStreet());
        assertEquals(42, read.get(0).getAddress().getFlat());
        assertEquals("Pushkina 5", read.get(1).getAddress().getStreet());
        assertEquals(7, read.get(1).getAddress().getFlat());
    }

    @Test
    @DisplayName("Произвольная глубина вложенности поддерживается")
    void deepNestedObjects() {
        Path file = tempDir.resolve("deep.csv");
        Level3 leaf = new Level3();
        leaf.setMark("deep");
        Level2 mid = new Level2();
        mid.setLevel3(leaf);
        Level1 root = new Level1();
        root.setLabel("root");
        root.setLevel2(mid);

        parser.saveToCsv(file.toString(), List.of(root), Level1.class);
        List<Level1> read = parser.parseFromCsv(file.toString(), Level1.class);

        assertEquals(1, read.size());
        assertEquals("root", read.get(0).getLabel());
        assertEquals("deep", read.get(0).getLevel2().getLevel3().getMark());
    }

    @Test
    @DisplayName("Коллекция: @CsvCollection задаёт разделитель")
    void collectionWithCustomDelimiter() throws IOException {
        Path file = tempDir.resolve("courses.csv");
        List<Course> courses = List.of(new Course(
                "Java Basics",
                Arrays.asList("oop", "collections", "streams"),
                Arrays.asList("Gandalf", "Frodo")
        ));
        parser.saveToCsv(file.toString(), courses, Course.class);

        String content = Files.readString(file);
        assertTrue(content.contains("oop;collections;streams"));
        assertTrue(content.contains("Gandalf|Frodo"));

        List<Course> read = parser.parseFromCsv(file.toString(), Course.class);
        assertEquals(Arrays.asList("oop", "collections", "streams"), read.get(0).getTags());
        assertEquals(Arrays.asList("Gandalf", "Frodo"), read.get(0).getAuthors());
    }

    @Test
    @DisplayName("Коллекция без аннотации использует разделитель по умолчанию '|'")
    void collectionDefaultDelimiter() throws IOException {
        Path file = tempDir.resolve("c.csv");
        Course c = new Course("X", List.of("a"), Arrays.asList("u1", "u2", "u3"));
        parser.saveToCsv(file.toString(), List.of(c), Course.class);
        String content = Files.readString(file);
        assertTrue(content.contains("u1|u2|u3"));
    }

    @Test
    @DisplayName("Коллекция с одним элементом — разделитель не появляется")
    void collectionSingleElement() {
        Path file = tempDir.resolve("single.csv");
        Course c = new Course("Solo", List.of("only"), List.of("alone"));
        parser.saveToCsv(file.toString(), List.of(c), Course.class);

        List<Course> read = parser.parseFromCsv(file.toString(), Course.class);
        assertEquals(List.of("only"), read.get(0).getTags());
        assertEquals(List.of("alone"), read.get(0).getAuthors());
    }

    @Test
    @DisplayName("Коллекции примитивных обёрток разных типов")
    void collectionOfDifferentTypes() {
        Path file = tempDir.resolve("nums.csv");
        NumberContainer nc = new NumberContainer();
        nc.setInts(Arrays.asList(1, 2, 3));
        nc.setLongs(Arrays.asList(10L, 20L));
        nc.setDoubles(Arrays.asList(1.5, 2.5));
        nc.setBools(Arrays.asList(true, false, true));

        parser.saveToCsv(file.toString(), List.of(nc), NumberContainer.class);
        List<NumberContainer> read = parser.parseFromCsv(file.toString(), NumberContainer.class);

        assertEquals(Arrays.asList(1, 2, 3), read.get(0).getInts());
        assertEquals(Arrays.asList(10L, 20L), read.get(0).getLongs());
        assertEquals(Arrays.asList(1.5, 2.5), read.get(0).getDoubles());
        assertEquals(Arrays.asList(true, false, true), read.get(0).getBools());
    }

    @Test
    @DisplayName("Все примитивные типы и обёртки корректно сохраняются и читаются")
    void allPrimitiveTypes() {
        Path file = tempDir.resolve("prim.csv");
        AllTypes a = new AllTypes();
        a.setPInt(7);
        a.setWInt(8);
        a.setPLong(100L);
        a.setWLong(200L);
        a.setPDouble(1.25);
        a.setWDouble(2.5);
        a.setPBool(true);
        a.setWBool(false);
        a.setStr("hello");

        parser.saveToCsv(file.toString(), List.of(a), AllTypes.class);
        List<AllTypes> read = parser.parseFromCsv(file.toString(), AllTypes.class);
        AllTypes r = read.get(0);

        assertEquals(7, r.getPInt());
        assertEquals(8, r.getWInt());
        assertEquals(100L, r.getPLong());
        assertEquals(200L, r.getWLong());
        assertEquals(1.25, r.getPDouble());
        assertEquals(2.5, r.getWDouble());
        assertTrue(r.isPBool());
        assertEquals(Boolean.FALSE, r.getWBool());
        assertEquals("hello", r.getStr());
    }

    @Test
    @DisplayName("Пустая коллекция объектов даёт файл только с заголовками")
    void emptyCollectionWritesOnlyHeader() throws IOException {
        Path file = tempDir.resolve("empty.csv");
        parser.saveToCsv(file.toString(), Collections.<Person>emptyList(), Person.class);

        List<String> lines = Files.readAllLines(file);
        assertEquals(1, lines.size());
        assertEquals("id,full_name,age", lines.get(0));

        List<Person> read = parser.parseFromCsv(file.toString(), Person.class);
        assertTrue(read.isEmpty());
    }

    @Test
    @DisplayName("Чтение пустого файла возвращает пустой список")
    void parseEmptyFile() throws IOException {
        Path file = tempDir.resolve("zero.csv");
        Files.writeString(file, "");
        List<Person> read = parser.parseFromCsv(file.toString(), Person.class);
        assertTrue(read.isEmpty());
    }

    @Test
    @DisplayName("Несуществующий файл -> RuntimeException")
    void missingFileThrows() {
        assertThrows(RuntimeException.class,
                () -> parser.parseFromCsv(tempDir.resolve("nope.csv").toString(), Person.class));
    }

    @Test
    @DisplayName("Пустые ячейки коллекции дают пустой список")
    void emptyCollectionCellParsesAsEmptyList() throws IOException {
        Path file = tempDir.resolve("ec.csv");
        Files.writeString(file, "name,tags,authors\nFoo,,\n");
        List<Course> read = parser.parseFromCsv(file.toString(), Course.class);
        assertEquals(1, read.size());
        assertEquals("Foo", read.get(0).getName());
        assertTrue(read.get(0).getTags().isEmpty());
        assertTrue(read.get(0).getAuthors().isEmpty());
    }

    @Test
    @DisplayName("Пустое значение для wrapper-типа -> null")
    void emptyWrapperParsesAsNull() throws IOException {
        Path file = tempDir.resolve("w.csv");
        Files.writeString(file, "pInt,wInt,pLong,wLong,pDouble,wDouble,pBool,wBool,str\n0,,0,,0.0,,false,,abc\n");
        List<AllTypes> read = parser.parseFromCsv(file.toString(), AllTypes.class);
        AllTypes r = read.get(0);
        assertNull(r.getWInt());
        assertNull(r.getWLong());
        assertNull(r.getWDouble());
        assertNull(r.getWBool());
        assertEquals("abc", r.getStr());
    }

    @Test
    @DisplayName("Round-trip через все три формата сохраняет данные")
    void roundTripAcrossFormats() {
        List<Person> original = Arrays.asList(
                new Person(1, "A", 10),
                new Person(2, "B", 20),
                new Person(3, "C", 30)
        );
        for (String ext : List.of(".csv", ".csv.gz", ".gz", ".zip")) {
            Path file = tempDir.resolve("rt" + ext.replace('.', '_') + ext);
            parser.saveToCsv(file.toString(), original, Person.class);
            List<Person> read = parser.parseFromCsv(file.toString(), Person.class);
            assertEquals(original.size(), read.size(), "ext=" + ext);
            for (int i = 0; i < original.size(); i++) {
                assertEquals(original.get(i).getId(), read.get(i).getId());
                assertEquals(original.get(i).getName(), read.get(i).getName());
                assertEquals(original.get(i).getAge(), read.get(i).getAge());
            }
        }
    }
}
