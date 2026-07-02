package com.laboratorio.iamodelinterface.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * author Rafael
 * version 1.3
 * created 20/08/2024
 * updated 02/07/2026
 */
public class ImageGeneratorUtils {
    private static final Logger log = LogManager.getLogger(ImageGeneratorUtils.class);

    private ImageGeneratorUtils() {
    }

    private static void logException(Exception e) {
        log.error("Error: {}", e.getMessage());
        if (e.getCause() != null) {
            log.error("Causa: {}", e.getCause().getMessage());
        }
    }

    // Si existe borra el fichero destinationFile
    private static void deleteFile(String destinationFile) {
        File fichero = new File(destinationFile);
        if (fichero.exists()) {
            fichero.delete();
        }
    }

    // Guarda una imagen codificada en base 64 en el fichero destinationFile
    public static String decodificarImagenBase64(String codedImage, String destinationFile) throws IOException {
        OutputStream os;

        try {
            // Si existe el fichero se borra
            deleteFile(destinationFile);

            byte[] imageBytes = Base64.getDecoder().decode(codedImage);
            os = new FileOutputStream(destinationFile);
            os.write(imageBytes);
            os.close();
        } catch (IOException e) {
            log.error("Ha ocurrido un error decodificando una image en base 64");
            logException(e);
            throw e;
        }

        try {
            os.close();
        } catch (IOException e) {
            log.warn("Ha ocurrido un error cerrando el fichero de la imagen decodificada");
        }

        return destinationFile;
    }

    // Nos asegura que el formato de la imagen generada sea JPG
    public static void toJpgFormat(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            BufferedImage originalImage = ImageIO.read(imageFile);
            ImageIO.write(originalImage, "jpg", imageFile);
        } catch (IOException e) {
            log.warn("Ha ocurrido un error cambiando el formato de la imagen generada.");
        }
    }
}