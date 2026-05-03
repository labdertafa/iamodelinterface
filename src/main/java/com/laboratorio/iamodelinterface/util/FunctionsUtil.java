package com.laboratorio.iamodelinterface.util;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class FunctionsUtil {
    private FunctionsUtil() {}

    public static String getMonthName(LocalDate date, IdiomaEnum idioma) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constantes.MONTH_PATTERN,
                Locale.forLanguageTag(idioma.getValue()));
        return date.format(formatter);
    }

    public static List<String> findSimilarDocumentsInSpecificDayOfMonth(VectorStore vectorStore, String query, int day, int month) {
        String filterExpr = String.format("contextDay == %d AND contextMonth == %d", day, month);

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(5)
                .filterExpression(filterExpr)
                .build();

        List<Document> documents = vectorStore.similaritySearch(request);

        return documents.stream()
                .map(Document::getFormattedContent)
                .toList();
    }
}