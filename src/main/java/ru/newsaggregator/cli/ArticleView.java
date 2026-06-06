package ru.newsaggregator.cli;

import ru.newsaggregator.model.Article;
import ru.newsaggregator.util.Time;

import java.util.List;

public final class ArticleView {

    private ArticleView() {
    }

    public static void printList(List<Article> articles) {
        if (articles.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        for (Article article : articles) {
            System.out.printf("#%-4d %s  [%s]  %s%n",
                    article.getId(),
                    date(article),
                    article.getCategory() == null ? "—" : article.getCategory(),
                    article.getSource() == null ? "—" : article.getSource());
            System.out.println("      " + article.getTitle());
        }
        System.out.println("Всего: " + articles.size());
    }

    public static void printDetails(Article article) {
        System.out.println("=".repeat(70));
        System.out.println("Заголовок:  " + article.getTitle());
        System.out.println("Источник:   " + article.getSource());
        System.out.println("Категория:  " + article.getCategory());
        System.out.println("Дата:       " + date(article));
        if (!article.getKeywords().isEmpty()) {
            System.out.println("Ключевые:   " + String.join(", ", article.getKeywords()));
        }
        if (article.getMediaUrl() != null) {
            System.out.println("Медиа:      " + article.getMediaUrl());
        }
        System.out.println("Ссылка:     " + article.getLink());
        System.out.println("-".repeat(70));
        System.out.println(article.getSummary());
        System.out.println("=".repeat(70));
    }

    private static String date(Article article) {
        return article.getPublishedAt() == null ? "—" : Time.display(article.getPublishedAt());
    }
}
