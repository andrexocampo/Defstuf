package com.portfolio.defstuf.util;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * Utilities for image handling
 */
public class ImageUtils {

    /**
     * Gets the file extension for saving the image
     */
    public static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0 && lastDot < fileName.length() - 1) {
            String ext = fileName.substring(lastDot + 1).toLowerCase();
            if (ext.equals("jpg") || ext.equals("jpeg")) {
                return "jpg";
            }
            return ext;
        }
        return "png";
    }

    /**
     * Saves an image to the specified file
     */
    public static void saveImage(BufferedImage image, File file) throws IOException {
        String extension = getFileExtension(file.getName());
        ImageIO.write(image, extension, file);
    }
}



