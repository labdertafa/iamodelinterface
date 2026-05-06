package com.laboratorio.iamodelinterface.model;

public record RetrievedDocument(
        int documentId,
        String content,
        String imageName
) {
}