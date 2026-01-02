package com.laboratorio.iamodelinterface.service;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.iamodelinterface.exception.IaModelException;
import com.laboratorio.iamodelinterface.model.ImagenGeneratorFile;
import com.laboratorio.iamodelinterface.util.ImageGeneratorUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageModel imageModel;

    public ImagenGeneratorFile generateImage(String instructions) {
        try {
            ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

            ImagePrompt imagePrompt = new ImagePrompt(instructions);
            ImageResponse response = this.imageModel.call(imagePrompt);

            String destinationFile = config.getProperty("temporal_image_path");
            ImageGeneratorUtils.decodificarImagenBase64(response.getResult().getOutput().getB64Json(), destinationFile);
            ImageGeneratorUtils.toJpgFormat(destinationFile);

            String contentType = config.getProperty("image_media_type");

            return new ImagenGeneratorFile(destinationFile, contentType);
        } catch (Exception e) {
            throw new IaModelException("Error generando una imagen con IA", e);
        }
    }
}