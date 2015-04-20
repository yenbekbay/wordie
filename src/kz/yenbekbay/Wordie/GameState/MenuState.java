/**
 * This class displays the menu screen with two buttons:
 * the start button and the exit button.
 */

package kz.yenbekbay.Wordie.GameState;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import kz.yenbekbay.Wordie.Main.GameFrame;
import kz.yenbekbay.Wordie.Main.StateManager;

public class MenuState extends GameState {
    private BufferedImage bg;
    private BufferedImage startButton;
    private BufferedImage exitButton;
    private Rectangle startButtonBounds;
    private Rectangle exitButtonBounds;

    public MenuState(StateManager gsm) {
        super(gsm);
    }

    public void init() {
        try {
            bg = ImageIO.read(new File(GameFrame.dir + "/images/menuBg.png"));
            startButton = ImageIO.read(new File(GameFrame.dir + "/images/startButton.png"));
            exitButton = ImageIO.read(new File(GameFrame.dir + "/images/exitButton.png"));
        } catch (IOException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        }
    }

    public void draw(Graphics2D g) {
        Dimension buttonSize = new Dimension(131, 61);
        g.drawImage(bg, 0, 0, GameFrame.WIDTH, GameFrame.HEIGHT, null);
        g.drawImage(startButton, GameFrame.WIDTH / 2 - buttonSize.width / 2,
                GameFrame.HEIGHT / 2, buttonSize.width, buttonSize.height, null);
        g.drawImage(exitButton, GameFrame.WIDTH / 2 - buttonSize.width / 2,
                GameFrame.HEIGHT / 2 + buttonSize.height + 10,
                buttonSize.width, buttonSize.height, null);
        startButtonBounds = new Rectangle(GameFrame.WIDTH / 2 - buttonSize.width / 2,
                GameFrame.HEIGHT / 2, buttonSize.width, buttonSize.height);
        exitButtonBounds = new Rectangle(GameFrame.WIDTH / 2 - buttonSize.width / 2,
                GameFrame.HEIGHT / 2 + buttonSize.height + 10,
                buttonSize.width, buttonSize.height);
    }

    public void mouseClicked(int x, int y)  {
        if (startButtonBounds.contains(x, y)) {
            gsm.frame.startGame();
        } else if (exitButtonBounds.contains(x, y)) {
            System.exit(0);
        }
    }
}

