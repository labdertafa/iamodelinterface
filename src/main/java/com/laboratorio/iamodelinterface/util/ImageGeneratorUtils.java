package com.laboratorio.iamodelinterface.util;

import java.awt.*;
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

    // Redimensiona una imagen cargada en memoria
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // Crear una imagen nueva con el tamaño especificado
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

        // Crear un BufferedImage con el tamaño deseado
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

        // Dibujar la imagen redimensionada en el nuevo BufferedImage
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }

    // Redimensiona una imagen de un fichero
    public static void resizeImage(String imagePath, double factor) {
        try {
            // 1. Cargar el archivo desde el disco
            File imageFile = new File(imagePath);
            BufferedImage originalImage = ImageIO.read(imageFile);

            int newWidth = (int)(originalImage.getWidth() * factor);
            int newHeight = (int)(originalImage.getHeight() * factor);

            BufferedImage resizedImage = resizeImage(originalImage, newWidth, newHeight);
            ImageIO.write(resizedImage, "jpg", imageFile);
        } catch (IOException e) {
            log.warn("Ha ocurrido un error cambiando las dimensiones de la imagen. Se conserva la imagen original.");
        }
    }
}