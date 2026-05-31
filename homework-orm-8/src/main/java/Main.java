import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);

            em.createTable(Student.class);
            System.out.println("Таблица students создана");

            Long id1 = em.save(new Student("Иван Иванов", 20, 4.5, LocalDate.of(2023, 9, 1)));
            Long id2 = em.save(new Student("Мария Петрова", 22, 4.8, LocalDate.of(2022, 9, 1)));
            Long id3 = em.save(new Student("Алексей Сидоров", 19, 4.2, LocalDate.of(2024, 2, 15)));
            System.out.println("Сохранены студенты с id: " + id1 + ", " + id2 + ", " + id3);

            Optional<Student> found = em.findById(Student.class, id2);
            System.out.println("findById(" + id2 + ") = " + found.orElse(null));

            List<Student> all = em.findAll(Student.class);
            System.out.println("findAll вернул " + all.size() + " записей:");
            for (Student s : all) {
                System.out.println("  " + s);
            }

            Optional<Student> missing = em.findById(Student.class, 9999L);
            System.out.println("findById(9999) пустой? " + missing.isEmpty());
        }
    }
}
