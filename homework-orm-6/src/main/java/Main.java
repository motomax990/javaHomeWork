import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);

            em.createTable(Student.class);
            System.out.println("Таблица students создана");

            Long id1 = em.save(new Student("Иван Иванов", 20, 4.5));
            Long id2 = em.save(new Student("Мария Петрова", 22, 4.8));
            Long id3 = em.save(new Student("Алексей Сидоров", 19, 4.2));

            System.out.println("Сохранён студент, id = " + id1);
            System.out.println("Сохранён студент, id = " + id2);
            System.out.println("Сохранён студент, id = " + id3);
        }
    }
}
