package com.portfolio.defstuf.services.screenshot;

import com.portfolio.defstuf.models.screenshot.Screenshot;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.WritableImage;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/**
 * Service for screen capture functionality
 * Service layer - contains business logic for screen captures
 */
public class ScreenshotCaptureService {

    /**
     * Captures the full screen
     */
    public Screenshot captureFullScreen() throws AWTException {
        Robot robot = new Robot();
        Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        BufferedImage fullScreenshot = robot.createScreenCapture(screenRect);
        
        // Convert to WritableImage for JavaFX
        WritableImage fxScreenshot = SwingFXUtils.toFXImage(fullScreenshot, null);
        
        return new Screenshot(fullScreenshot, fxScreenshot);
    }

    /**
     * Crops an image according to the selected area
     */
    public BufferedImage cropImage(Screenshot screenshot, Rectangle2D selectedArea) {
        if (screenshot == null || screenshot.getFullScreenshot() == null || selectedArea == null) {
            throw new IllegalArgumentException("Screenshot and selected area cannot be null");
        }

        int x = (int) selectedArea.getMinX();
        int y = (int) selectedArea.getMinY();
        int width = (int) selectedArea.getWidth();
        int height = (int) selectedArea.getHeight();

        // Validate that the area is within the image boundaries
        BufferedImage fullImage = screenshot.getFullScreenshot();
        if (x < 0 || y < 0 || x + width > fullImage.getWidth() || y + height > fullImage.getHeight()) {
            throw new IllegalArgumentException("Selected area is outside image boundaries");
        }

        return fullImage.getSubimage(x, y, width, height);
    }

    /**
     * Validates if a selected area is valid (has minimum size)
     */
    public boolean isValidSelectionArea(Rectangle2D area, double minWidth, double minHeight) {
        if (area == null) {
            return false;
        }
        return area.getWidth() >= minWidth && area.getHeight() >= minHeight;
    }
}

