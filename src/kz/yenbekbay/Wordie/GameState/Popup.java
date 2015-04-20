/**
 * This class handles drawing a popup window.
 */

package kz.yenbekbay.Wordie.GameState;

import kz.yenbekbay.Wordie.Main.GameFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Popup {
    private BufferedImage popupBg;
    public static int width;
    public static int height;

    public Popup() {
        try {
            popupBg = ImageIO.read(new File(GameFrame.dir + "/images/popupBg.png"));
        } catch (IOException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        }
        width = popupBg.getWidth() / 2;
        height = popupBg.getHeight() / 2;
    }

    public void draw(Graphics2D g) {
        // Darken the background
        g.setColor(new Color(0f, 0f, 0f, 0.5f));
        g.fillRect(0, 0, GameFrame.WIDTH, GameFrame.HEIGHT);
        // Draw the frame
        g.drawImage(popupBg, GameFrame.WIDTH / 2 - width / 2, GameFrame.HEIGHT / 2 - width / 2,
                width, height, null);
    }
}
