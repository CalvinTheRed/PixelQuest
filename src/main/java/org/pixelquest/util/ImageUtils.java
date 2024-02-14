package org.pixelquest.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ImageUtils {

    public static BufferedImage joinImageHorz(BufferedImage i1, BufferedImage i2) {
        return joinImageHorz(i1, 0, 0, i1.getWidth(), i1.getHeight(), i2, 0, 0, i2.getWidth(), i2.getHeight());
    }

    public static BufferedImage joinImageHorz(
            BufferedImage i1, int i1x1, int i1y1, int i1x2, int i1y2,
            BufferedImage i2, int i2x1, int i2y1, int i2x2, int i2y2
    ) {
        // TODO probably an inefficient solution
        BufferedImage joinedImage = new BufferedImage(
                i1x2 + i2x2 - i1x1 - i2x1,
                Math.max(i1y2 - i1y1, i2y2 - i2y1),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2 = joinedImage.createGraphics();
        g2.drawImage(i1,
                0,
                0,
                i1x2 - i1x1,
                i1y2 - i1y1,
                i1x1,
                i1y1,
                i1x2,
                i1y2,
                null
        );
        g2.drawImage(i2,
                i1x2 - i1x1,
                0,
                (i1x2 - i1x1) + i2x2 - i2x1,
                i2y2 - i2y1,
                i2x1,
                i2y1,
                i2x2,
                i2y2,
                null
        );

        g2.dispose();

        return joinedImage;
    }

}
