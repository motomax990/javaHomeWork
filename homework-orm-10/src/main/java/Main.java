import orm.core.ValidationException;
import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);

            em.createTable(RegistrationForm.class);
            System.out.println("Таблица registration_form создана");

            RegistrationForm valid = new RegistrationForm("alex123", "superSecret1", 25);
            Long id = em.save(valid);
            System.out.println("Валидная форма сохранена, id = " + id);

            RegistrationForm invalid = new RegistrationForm("ab", null, 15);
            try {
                em.save(invalid);
                System.out.println("Сюда попасть не должны");
            } catch (ValidationException e) {
                System.out.println("Невалидная форма — " + e.getViolations().size() + " нарушений:");
                e.getViolations().forEach(v -> System.out.println(
                        "  поле '" + v.field() + "' (" + v.invalidValue() + ") — "
                                + v.message() + " [" + v.annotation().getSimpleName() + "]"
                ));
            }

            List<RegistrationForm> batch = List.of(
                    new RegistrationForm("user1", "password1", 30),
                    new RegistrationForm("u2", "weak", 5),
                    new RegistrationForm("user3", "anotherPass", 40)
            );
            try {
                em.saveAll(batch);
                System.out.println("Сюда попасть не должны");
            } catch (ValidationException e) {
                System.out.println("saveAll откатился — " + e.getViolations().size() + " нарушений на всю пачку:");
                e.getViolations().forEach(v -> System.out.println(
                        "  поле '" + v.field() + "' (" + v.invalidValue() + ") — "
                                + v.message() + " [" + v.annotation().getSimpleName() + "]"
                ));
            }

            long total = em.count(RegistrationForm.class);
            System.out.println("Итоговое количество строк в registration_form = " + total
                    + " (пачка действительно откатилась)");
        }
    }
}
