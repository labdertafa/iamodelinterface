package com.laboratorio.iamodelinterface.service;

import com.laboratorio.clientapilibrary.utils.ReaderConfig;
import com.laboratorio.iamodelinterface.config.ChutesImageConfiguration;
import com.laboratorio.iamodelinterface.model.ImagenGeneratorFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {ChutesImageConfiguration.class, ImageService.class})
public class ImageServiceTest {
    @Autowired
    private ImageService imageService;

    private final ReaderConfig config = new ReaderConfig("config//ia_models_config.properties");

    @Test
    public void simpleImageTest() {
        // String prompt = "Crea una imagen de un oso pardo en una cascada en la naturaleza. No agregues texto a la imagen";

        String prompt = """
                Create a detailed, visually appealing illustration that represents the following text conceptually and symbolically:
                "On December 31, 1999, the world celebrated the arrival of the new millennium and the success in avoiding the feared 'Year 2000 problem' or Y2K, which could have caused failures in computer systems when the century changed."
                Focus on key visual elements, atmosphere, and emotions suggested by the text.
                Use a modern, cinematic, or artistic style depending on the theme.
                No text, no letters, no typography, no captions, no logos.
                """;

        ImagenGeneratorFile file = this.imageService.generateImage(prompt);

        String destinationFile = this.config.getProperty("temporal_image_path");
        String contentType = this.config.getProperty("image_media_type");

        assertNotNull(file);
        assertEquals(destinationFile, file.getFilePath());
        assertEquals(contentType, file.getContentType());
    }
}