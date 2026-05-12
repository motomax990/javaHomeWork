package org.example;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        CsvParser parser = new CsvParser();

        List<Person> people = Arrays.asList(
                new Person(1, "Ivan Ivanov", 25),
                new Person(2, "Maria Petrova", 30)
        );

        parser.saveToCsv("people.csv", people, Person.class);
        parser.saveToCsv("people.csv.gz", people, Person.class);
        parser.saveToCsv("people.zip", people, Person.class);

        System.out.println("=== Чтение из people.csv ===");
        parser.parseFromCsv("people.csv", Person.class).forEach(System.out::println);

        System.out.println("=== Чтение из people.csv.gz ===");
        parser.parseFromCsv("people.csv.gz", Person.class).forEach(System.out::println);

        System.out.println("=== Чтение из people.zip ===");
        parser.parseFromCsv("people.zip", Person.class).forEach(System.out::println);

        List<Employee> employees = Arrays.asList(
                new Employee(1, "Ivan Ivanov", new Address("Lenina 10", 42)),
                new Employee(2, "Maria Petrova", new Address("Pushkina 5", 7))
        );
        parser.saveToCsv("employees.csv", employees, Employee.class);
        System.out.println("=== Чтение сотрудников (вложенные объекты) ===");
        parser.parseFromCsv("employees.csv", Employee.class).forEach(System.out::println);

        List<Course> courses = Arrays.asList(
                new Course("Java Basics",
                        Arrays.asList("oop", "collections", "streams"),
                        Arrays.asList("Gandalf", "Frodo")),
                new Course("Spring Basics",
                        Arrays.asList("spring", "boot", "web"),
                        Arrays.asList("Arathorn", "Legolas"))
        );
        parser.saveToCsv("courses.csv", courses, Course.class);
        System.out.println("=== Чтение курсов (коллекции в полях) ===");
        parser.parseFromCsv("courses.csv", Course.class).forEach(System.out::println);
    }
}
