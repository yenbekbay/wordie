/**
 * This class represents the active state of the game
 * and handles the gameplay.
 */
package kz.yenbekbay.Wordie.GameState;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import kz.yenbekbay.Wordie.Box.Ball;
import kz.yenbekbay.Wordie.Main.BoxWorld;
import kz.yenbekbay.Wordie.Main.GameFrame;
import kz.yenbekbay.Wordie.Main.StateManager;
import kz.yenbekbay.Wordie.Main.Word;

public class PlayState extends GameState {
    private BoxWorld boxWorld;
    private Word word;
    private ArrayList<Ball> activeBalls;

    private BufferedImage checkButton;
    private BufferedImage cancelButton;
    private BufferedImage pauseButton;
    private Rectangle checkButtonBounds;
    private Rectangle cancelButtonBounds;
    private Rectangle pauseButtonBounds;

    public PlayState(StateManager gsm) {
        super(gsm);
    }

    public void init() {
        boxWorld = gsm.frame.boxWorld;
        word = gsm.frame.word;
        activeBalls = new ArrayList<Ball>();
        try {
            checkButton = ImageIO.read(new File(GameFrame.dir + "/images/checkButton.png"));
            cancelButton = ImageIO.read(new File(GameFrame.dir + "/images/cancelButton.png"));
            pauseButton = ImageIO.read(new File(GameFrame.dir + "/images/pauseButton.png"));
        } catch (IOException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        }
    }

    public void draw(Graphics2D g) {
        // flip the y axis
        g.transform(AffineTransform.getScaleInstance(1, -1));
        g.transform(AffineTransform.getTranslateInstance(0, -GameFrame.HEIGHT));
        // get the timer and the word
        String timer = gsm.frame.getTimer();
        String scoreString = gsm.frame.getScore();
        String wordString = word.getString();
        // set the color
        g.setColor(Color.white);
        // draw the timer
        g.setFont(GameFrame.getFont(20));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D timerRect = fm.getStringBounds(timer, g);
        g.drawString(timer, 15, (int)(timerRect.getHeight() / 2) + 20);
        // draw the score
        g.setFont(GameFrame.getFont(30));
        fm = g.getFontMetrics();
        Rectangle2D scoreRect = fm.getStringBounds(scoreString, g);
        g.drawString(scoreString, (int)(GameFrame.WIDTH / 2 - scoreRect.getWidth() / 2),
                (int)(scoreRect.getHeight() / 2) + 20);
        // draw the word
        g.setFont(GameFrame.getFont(20));
        fm = g.getFontMetrics();
        Rectangle2D wordRect = fm.getStringBounds(wordString, g);
        g.drawString(wordString, (int)(GameFrame.WIDTH / 2 - wordRect.getWidth() / 2),
                                 (int)(GameFrame.HEIGHT - 28 - wordRect.getHeight() / 2 + fm.getAscent()));
        // Draw buttons
        Dimension buttonSize = new Dimension(35, 38);
        g.drawImage(checkButton, GameFrame.WIDTH - 15 - buttonSize.width,
                GameFrame.HEIGHT - 10 - buttonSize.height,
                buttonSize.width, buttonSize.height, null);
        g.drawImage(cancelButton, 15, GameFrame.HEIGHT - 10 - buttonSize.height,
                buttonSize.width, buttonSize.height, null);
        // Pause icon
        g.drawImage(pauseButton, GameFrame.WIDTH - 14 - pauseButton.getWidth() / 2, 14,
                pauseButton.getWidth() / 2, pauseButton.getHeight() / 2, null);
        // Set button bounds
        checkButtonBounds = new Rectangle(GameFrame.WIDTH - 15 - buttonSize.width,
                GameFrame.HEIGHT - 10 - buttonSize.height, buttonSize.width, buttonSize.height);
        cancelButtonBounds = new Rectangle(15, GameFrame.HEIGHT - 10 - buttonSize.height,
                buttonSize.width, buttonSize.height);
        pauseButtonBounds = new Rectangle(GameFrame.WIDTH - 14 - pauseButton.getWidth() / 2, 14,
                pauseButton.getWidth() / 2, pauseButton.getHeight() / 2);
    }

    public void mouseClicked(int x, int y)  {
        if (!boxWorld.balls.isEmpty()) {
            if (checkButtonBounds.contains(x, y)) {
                if (word.isValid()) {
                    System.out.println("'" + word.getString().toLowerCase() + "' is a correct word!");
                    gsm.frame.score += word.calculatePoints(activeBalls);
                    resetWord(true, false);
                } else {
                    System.out.println("Word '" + word.getString().toLowerCase() + "' doesn't exist.");
                    resetWord(false, true);
                }
            } else if (cancelButtonBounds.contains(x, y)) {
                resetWord(false, false);
            } else if (pauseButtonBounds.contains(x, y)) {
                if (!gsm.frame.isStopped()) {
                    gsm.frame.setStatus(GameFrame.STOPPED);
                    gsm.frame.gameLoop();
                    gsm.frame.showState(StateManager.PAUSE);
                }
            } else {
                for (Ball ball : boxWorld.balls) {
                    if (ball.contains(x, y)) {
                        if (activeBalls.size() < 12) {
                            if (!activeBalls.contains(ball)) {
                                word.addLetter(ball.letter);
                                activeBalls.add(ball);
                                ball.activate();
                            } else if (activeBalls.get(activeBalls.size()-1) == ball) {
                                word.removeLastLetter();
                                activeBalls.remove(ball);
                                ball.deactivate();
                            }
                        } else {
                            ball.fade();
                        }
                    }
                }
            }
       }
    }

    /**
     * Clears the active balls array and,
     * if specified, removes the balls from the screen.
     * @param remove boolean if should be removed from the screen
     * @param fade boolean if should highlight the balls with red color
     */
    public void resetWord(boolean remove, boolean fade) {
        word.reset();
        for (Ball activeBall : activeBalls) {
            if (remove) {
                boxWorld.removeBall(activeBall);
            } else {
                activeBall.deactivate();
                if (fade) activeBall.fade();
            }
        }
        activeBalls.clear();
    }
}

