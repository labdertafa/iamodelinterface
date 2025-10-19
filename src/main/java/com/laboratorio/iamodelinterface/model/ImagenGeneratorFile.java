package com.laboratorio.iamodelinterface.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * author Rafael
 * version 1.0
 * created 20/08/2024
 * updated 20/08/2024
 */

@Getter @Setter @AllArgsConstructor
public class ImagenGeneratorFile {
    private String filePath;
    private String contentType;
}