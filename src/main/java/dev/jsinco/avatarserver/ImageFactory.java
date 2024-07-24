package dev.jsinco.avatarserver;

import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public final class ImageFactory {

    private static final String TEXTURES_URL = "https://textures.minecraft.net/texture/";

    public static BufferedImage getAvatarImage(String texture) {
        try {
            return getAvatarImage(new URL(TEXTURES_URL + texture));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static BufferedImage getAvatarImage(URL textureURL) {
        if (textureURL == null) return null;
        try {
            BufferedImage skinImage = ImageIO.read(textureURL);

            int faceStartX = 8, faceStartY = 8;
            int height = 8, width = 8;

            int overlayStartX = 40;
            int overlayStartY = 8;

            BufferedImage faceImage = skinImage.getSubimage(faceStartX, faceStartY, width, height);
            BufferedImage overlayImage = skinImage.getSubimage(overlayStartX, overlayStartY, width, height);
            return overlayImages(faceImage, overlayImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static BufferedImage overlayImages(BufferedImage background, BufferedImage foreground) {
        int width = Math.max(background.getWidth(), foreground.getWidth());
        int height = Math.max(background.getHeight(), foreground.getHeight());

        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        g.drawImage(background, 0, 0, null);
        g.drawImage(foreground, 0, 0, null); // Adjust x and y for desired placement
        g.dispose(); // Dispose of the graphics context
        return combinedImage;
    }

    // Credit: https://stackoverflow.com/questions/7951290/re-sizing-an-image-without-losing-quality
    public static BufferedImage resizeUsingJavaAlgo(BufferedImage sourceImage, int width, int height) {
        if (sourceImage == null) {
            return null;
        }
        double ratio = (double) sourceImage.getWidth()/sourceImage.getHeight();
        if (width < 1) {
            width = (int) (height * ratio + 0.4);
        } else if (height < 1) {
            height = (int) (width /ratio + 0.4);
        }

        Image scaled = sourceImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING);
        BufferedImage bufferedScaled = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedScaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(scaled, 0, 0, width, height, null);
        g2d.dispose();
        return bufferedScaled;
    }
}
