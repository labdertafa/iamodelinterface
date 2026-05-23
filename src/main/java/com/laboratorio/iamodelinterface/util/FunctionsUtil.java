package com.laboratorio.iamodelinterface.util;

import com.laboratorio.iamodelinterface.model.RetrievedDocument;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FunctionsUtil {
    private static final int MAX_DOCUMENTS = 5;
    private FunctionsUtil() {
    }

    public static String getMonthName(LocalDate date, IdiomaEnum idioma) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constantes.MONTH_PATTERN,
                Locale.forLanguageTag(idioma.getValue()));
        return date.format(formatter);
    }

    private static SearchRequest getSearchRequest(String query, int day, int month) {
        String filterExpr = String.format("contextDay == %d AND contextMonth == %d", day, month);
        return SearchRequest.builder()
                .query(query)
                .topK(MAX_DOCUMENTS)
                .filterExpression(filterExpr)
                .build();
    }

    public static List<String> findSimilarDocumentsInSpecificDayOfMonth(
            VectorStore vectorStore, String query, int day, int month) {

        SearchRequest request = getSearchRequest(query, day, month);
        List<Document> documents = vectorStore.similaritySearch(request);

        return documents.stream()
                .map(Document::getFormattedContent)
                .toList();
    }

    public static List<RetrievedDocument> findSimilarDocumentsInSpecificDayOfMonthList(
            VectorStore vectorStore, String query, int day, int month) {

        SearchRequest request = getSearchRequest(query, day, month);
        List<Document> documents = vectorStore.similaritySearch(request);

        int i = 1;
        List<RetrievedDocument> retrievedDocuments = new ArrayList<>();
        for (Document document : documents) {
            var f1Document = new RetrievedDocument(
                    i,
                    document.getFormattedContent(),
                    (String) document.getMetadata().get("imagename")
            );
            i++;
            retrievedDocuments.add(f1Document);
        }

        return retrievedDocuments;
    }

    public static String getImageName(List<RetrievedDocument> documents, int selectedId) {
        return documents.stream()
                .filter(doc -> doc.documentId() == selectedId)
                .findFirst()
                .map(RetrievedDocument::imageName)
                .orElse(null);
    }
}