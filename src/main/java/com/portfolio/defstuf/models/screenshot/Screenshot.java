package com.portfolio.defstuf.models.screenshot;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;

/**
 * Domain model representing a screenshot capture
 */
public class Screenshot {

    private BufferedImage fullScreenshot;
    private WritableImage fxScreenshot;
    private Rectangle2D selectedArea;

    public Screenshot(BufferedImage fullScreenshot, WritableImage fxScreenshot) {
        this.fullScreenshot = fullScreenshot;
        this.fxScreenshot = fxScreenshot;
    }

    public BufferedImage getFullScreenshot() {
        return fullScreenshot;
    }

    public void setFullScreenshot(BufferedImage fullScreenshot) {
        this.fullScreenshot = fullScreenshot;
    }

    public WritableImage getFxScreenshot() {
        return fxScreenshot;
    }

    public void setFxScreenshot(WritableImage fxScreenshot) {
        this.fxScreenshot = fxScreenshot;
    }

    public Rectangle2D getSelectedArea() {
        return selectedArea;
    }

    public void setSelectedArea(Rectangle2D selectedArea) {
        this.selectedArea = selectedArea;
    }
}

