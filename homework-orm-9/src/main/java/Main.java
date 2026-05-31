import orm.repository.DBConfig;
import orm.repository.EntityManager;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection connection = DBConfig.getConnection()) {
            EntityManager em = new EntityManager(connection);

            em.createTable(Book.class);
            System.out.println("Таблица books создана");

            List<Book> books = List.of(
                    new Book("Война и мир", "Лев Толстой", 1869, true),
                    new Book("Анна Каренина", "Лев Толстой", 1877, true),
                    new Book("Преступление и наказание", "Фёдор Достоевский", 1866, true),
                    new Book("Мастер и Маргарита", "Михаил Булгаков", 1967, false),
                    new Book("Идиот", "Фёдор Достоевский", 1869, true)
            );
            em.saveAll(books);
            System.out.println("saveAll сохранил 5 книг:");
            for (Book b : books) {
                System.out.println("  " + b);
            }

            List<Book> tolstoy = em.findAllWhere(Book.class, "author", "Лев Толстой");
            System.out.println("Книги Льва Толстого (" + tolstoy.size() + "):");
            for (Book b : tolstoy) {
                System.out.println("  " + b);
            }

            Book first = tolstoy.get(0);
            first.setAvailable(false);
            int updated = em.update(first);
            System.out.println("update вернул " + updated + " (обновили '" + first.getTitle() + "')");

            Optional<Book> warAndPeace = em.findOneWhere(Book.class, "title", "Война и мир");
            System.out.println("findOneWhere title='Война и мир' = " + warAndPeace.orElse(null));

            int deleted = em.delete(books.get(books.size() - 1));
            System.out.println("delete вернул " + deleted);

            long total = em.count(Book.class);
            System.out.println("count = " + total);
        }
    }
}
