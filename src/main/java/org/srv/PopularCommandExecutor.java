package org.srv;

public final class PopularCommandExecutor {
    private final ConnectionManager manager;
    private final int maxAttempts;

    public PopularCommandExecutor(ConnectionManager manager, int maxAttempts) {
        this.manager = manager;
        this.maxAttempts = maxAttempts;
    }

    public void updatePackages() {
        tryExecute("apt update && apt upgrade -y");
    }

    void tryExecute(String command) {
        ConnectionException lastException = null;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try (Connection connection = manager.getConnection()) {
                connection.execute(command);
                return;
            } catch (ConnectionException e) {
                lastException = e;
                System.out.println("Попытка " + attempt + " ошибка выполнения: " + e.getMessage());
            } catch (Exception e) {
                lastException = new ConnectionException("Не удалось закрыть соединение", e);
                System.out.println("Попфтка " + attempt + " ошибказакрытия: " + e.getMessage());
            }
        }
        throw new ConnectionException(
                "Не удалось выполнить команду после " + maxAttempts + " попыток",
                lastException
        );
    }
}