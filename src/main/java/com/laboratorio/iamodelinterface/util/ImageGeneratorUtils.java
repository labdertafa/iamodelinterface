package com.laboratorio.iamodelinterface.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * author Rafael
 * version 1.2
 * created 20/08/2024
 * updated 02/01/2026
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
    public static String decodificarImagenBase64(String codedImage, String destinationFile) {
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
            return null;
        }

        try {
            os.close();
        } catch (IOException e) {
            log.warn("Ha ocurrido un error cerrando el fichero de la imagen decodificada");
        }

        return destinationFile;
    }

    public static String almacenarImagen(byte[] imageData, String destinationFile) {
        OutputStream os;

        try {
            // Si existe el fichero se borra
            deleteFile(destinationFile);

            os = new FileOutputStream(destinationFile);
            os.write(imageData);
            os.close();
        } catch (IOException e) {
            log.error("Ha ocurrido un error almacenando la imagen recibida");
            logException(e);
            return null;
        }

        try {
            os.close();
        } catch (IOException e) {
            log.warn("Ha ocurrido un error cerrando el fichero de la imagen almacenada");
        }

        return destinationFile;
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

    // Guarda una imagen desde un URL y la almacena en el fichero destinationFile
    public static String downloadImage(String imageUrl, String destinationFile) {
        try {
            // Si existe el fichero se borra
            deleteFile(destinationFile);

            // Descargar la imagen desde la URL
            BufferedImage originalImage = ImageIO.read(new URL(imageUrl));

            // Redimensionar la imagen a 800x600
            BufferedImage resizedImage = resizeImage(originalImage, 800, 600);

            // Guardar la imagen redimensionada
            ImageIO.write(resizedImage, "jpg", new File(destinationFile));
        } catch (IOException e) {
            log.error("Ha ocurrido un error descargando la imagen desde la URL");
            logException(e);
            return null;
        }

        return destinationFile;
    }
}