package ru.newsaggregator.process;

import ru.newsaggregator.model.Article;

public class ArticleProcessor {

    private final ContentCleaner cleaner;
    private final CategoryClassifier classifier;
    private final KeywordExtractor keywordExtractor;
    private final Summarizer summarizer;

    public ArticleProcessor(ContentCleaner cleaner,
                            CategoryClassifier classifier,
                            KeywordExtractor keywordExtractor,
                            Summarizer summarizer) {
        this.cleaner = cleaner;
        this.classifier = classifier;
        this.keywordExtractor = keywordExtractor;
        this.summarizer = summarizer;
    }

    public Article process(Article article) {
        String cleanText = cleaner.clean(article.getContent());
        article.setContent(cleanText);
        article.setSummary(summarizer.summarize(cleanText));
        article.setCategory(classifier.classify(article.getTitle(), cleanText));
        article.setKeywords(keywordExtractor.extract(article.getTitle() + " " + cleanText, 7));
        return article;
    }
}
