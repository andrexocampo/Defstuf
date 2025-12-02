package com.portfolio.defstuf.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utility class for managing image files
 */
public class ImageFileManager {
    
    private static final String IMAGES_DIR = "images/notes";
    
    /**
     * Saves a WritableImage to disk and returns the file path
     */
    public static String saveImage(WritableImage image, String mimeType) throws IOException {
        // Create images directory if it doesn't exist
        Path imagesPath = Paths.get(IMAGES_DIR);
        if (!Files.exists(imagesPath)) {
            Files.createDirectories(imagesPath);
        }
        
        // Generate unique filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getExtensionFromMimeType(mimeType);
        String filename = timestamp + "_" + uuid + "." + extension;
        
        Path filePath = imagesPath.resolve(filename);
        
        // Convert WritableImage to BufferedImage and save
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, extension, filePath.toFile());
        
        return filePath.toString().replace("\\", "/"); // Normalize path separators
    }
    
    /**
     * Saves a BufferedImage to disk and returns the file path
     */
    public static String saveImage(BufferedImage image, String mimeType) throws IOException {
        // Create images directory if it doesn't exist
        Path imagesPath = Paths.get(IMAGES_DIR);
        if (!Files.exists(imagesPath)) {
            Files.createDirectories(imagesPath);
        }
        
        // Generate unique filename
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getExtensionFromMimeType(mimeType);
        String filename = timestamp + "_" + uuid + "." + extension;
        
        Path filePath = imagesPath.resolve(filename);
        
        // Save image
        ImageIO.write(image, extension, filePath.toFile());
        
        return filePath.toString().replace("\\", "/"); // Normalize path separators
    }
    
    /**
     * Gets file size in bytes
     */
    public static long getFileSize(String filePath) {
        try {
            return Files.size(Paths.get(filePath));
        } catch (IOException e) {
            return 0;
        }
    }
    
    /**
     * Gets extension from MIME type
     */
    private static String getExtensionFromMimeType(String mimeType) {
        if (mimeType == null) {
            return "png";
        }
        
        switch (mimeType.toLowerCase()) {
            case "image/png":
                return "png";
            case "image/jpeg":
            case "image/jpg":
                return "jpg";
            case "image/gif":
                return "gif";
            case "image/bmp":
                return "bmp";
            default:
                return "png";
        }
    }
}

