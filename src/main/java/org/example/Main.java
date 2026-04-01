package org.example;

import com.google.gson.JsonSyntaxException;
import org.example.cache.FileStorage;
import org.example.client.SpaceXHttpClient;
import org.example.model.Core;
import org.example.model.Failure;
import org.example.model.Launch;
import org.example.parser.JsonBuilder;
import org.example.parser.JsonParser;
import org.example.service.LaunchService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final LaunchService launchService = new LaunchService(
            new SpaceXHttpClient(),
            new JsonParser(),
            new JsonBuilder(),
            new FileStorage("cache")
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllLaunches();
                case "2" -> showLatestLaunch();
                case "3" -> searchByDate(scanner);
                case "4" -> showBySuccess(true);
                case "5" -> showBySuccess(false);
                case "6" -> clearCache();
                case "7" -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("=== SpaceX Launch Explorer ===");
        System.out.println("1. Показать все запуски");
        System.out.println("2. Показать последний запуск");
        System.out.println("3. Поиск запусков по дате");
        System.out.println("4. Показать только успешные запуски");
        System.out.println("5. Показать только неудачные запуски");
        System.out.println("6. Очистить кеш");
        System.out.println("7. Выход");
        System.out.print("Ваш выбор: ");
    }

    private static void showAllLaunches() {
        try {
            List<Launch> launches = launchService.getAllLaunches();
            for (Launch launch : launches) {
                printLaunchShort(launch);
            }
            System.out.println("Всего запусков: " + launches.size());
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка при разборе JSON: " + e.getMessage());
        }
    }

    private static void showLatestLaunch() {
        try {
            Launch launch = launchService.getLatestLaunch();
            printLaunchDetailed(launch);
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка при разборе JSON: " + e.getMessage());
        }
    }

    private static void searchByDate(Scanner scanner) {
        System.out.print("Введите дату начала (YYYY-MM-DD): ");
        String from = scanner.nextLine().trim();
        System.out.print("Введите дату конца (YYYY-MM-DD): ");
        String to = scanner.nextLine().trim();

        try {
            List<Launch> launches = launchService.searchByDate(from, to);
            if (launches.isEmpty()) {
                System.out.println("Запуски не найдены.");
            } else {
                for (Launch launch : launches) {
                    printLaunchShort(launch);
                }
                System.out.println("Найдено запусков: " + launches.size());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка при разборе JSON: " + e.getMessage());
        }
    }

    private static void showBySuccess(boolean success) {
        try {
            List<Launch> launches = launchService.getBySuccess(success);
            String label = success ? "успешных" : "неудачных";
            if (launches.isEmpty()) {
                System.out.println("Запуски не найдены.");
            } else {
                for (Launch launch : launches) {
                    printLaunchShort(launch);
                }
                System.out.println("Всего " + label + " запусков: " + launches.size());
            }
        } catch (IOException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            System.out.println("Ошибка при разборе JSON: " + e.getMessage());
        }
    }

    private static void clearCache() {
        launchService.clearCache();
        System.out.println("Кеш очищен.");
    }

    private static void printLaunchShort(Launch launch) {
        String date = launch.getDateUtc() != null ? launch.getDateUtc().substring(0, 10) : "н/д";
        String successStr = launch.getSuccess() == null ? "н/д" : (launch.getSuccess() ? "да" : "нет");
        System.out.println("#" + launch.getFlightNumber() + " " + launch.getName()
                + " | " + date + " | Успех: " + successStr);
    }

    private static void printLaunchDetailed(Launch launch) {
        String successStr = launch.getSuccess() == null ? "н/д" : (launch.getSuccess() ? "да" : "нет");
        System.out.println("Запуск: " + launch.getName());
        System.out.println("Номер: " + launch.getFlightNumber());
        System.out.println("Дата: " + launch.getDateUtc());
        System.out.println("Успех: " + successStr);
        System.out.println("Описание: " + (launch.getDetails() != null ? launch.getDetails() : "нет"));

        if (launch.getFailures() != null && !launch.getFailures().isEmpty()) {
            System.out.println("Отказы:");
            for (Failure f : launch.getFailures()) {
                System.out.println("  - Время: " + f.getTime() + "с, Высота: "
                        + (f.getAltitude() != null ? f.getAltitude() + " км" : "н/д")
                        + ", Причина: " + f.getReason());
            }
        }

        if (launch.getCores() != null && !launch.getCores().isEmpty()) {
            System.out.println("Ускорители:");
            for (Core c : launch.getCores()) {
                System.out.println("  - ID: " + (c.getCore() != null ? c.getCore() : "н/д")
                        + ", Полет: " + (c.getFlight() != null ? c.getFlight() : "н/д")
                        + ", Повторное использование: " + (c.getReused() != null && c.getReused() ? "да" : "нет")
                        + ", Посадка: " + (c.getLandingSuccess() != null && c.getLandingSuccess() ? "да" : "нет"));
            }
        }
    }
}
