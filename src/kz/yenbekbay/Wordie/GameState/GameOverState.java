/**
 * This class displays a popup window with the player's score,
 * the best score, and the replay button.
 */

package kz.yenbekbay.Wordie.GameState;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import kz.yenbekbay.Wordie.Main.GameFrame;
import kz.yenbekbay.Wordie.Main.StateManager;

public class GameOverState extends GameState {
    private Rectangle replayButtonBounds;

    public GameOverState(StateManager gsm) {
        super(gsm);
    }

    public void init() { }

    public void draw(Graphics2D g) {
        // Draw GAME OVER string
        String title = "GAME OVER";
        if (gsm.frame.getHighScore() == gsm.frame.score) title = "HOORAY!";
        g.setColor(Color.white);
        g.setFont(GameFrame.getFont(30));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D gameOverRect = fm.getStringBounds(title, g);
        g.drawString(title, (int)(GameFrame.WIDTH / 2 - gameOverRect.getWidth() / 2),
                (int)(GameFrame.HEIGHT / 2 - Popup.height / 2 + gameOverRect.getHeight() + 20));
        // Draw score
        if (gsm.frame.getHighScore() == gsm.frame.score) {
            drawScore(g, "NEW HIGH SCORE:");
        } else {
            drawScore(g, "YOUR SCORE:");
            drawScore(g, "BEST SCORE:");
        }
        // Draw replay button
        g.setFont(GameFrame.getFont(20));
        fm = g.getFontMetrics();
        Rectangle2D replayRect = fm.getStringBounds("REPLAY", g);
        g.drawString("REPLAY", (int)(GameFrame.WIDTH / 2 - replayRect.getWidth() / 2),
                (int)(GameFrame.HEIGHT / 2 + 70 + replayRect.getHeight()));
        replayButtonBounds = new Rectangle((int)(GameFrame.WIDTH / 2 - replayRect.getWidth() / 2 - 10),
                GameFrame.HEIGHT / 2 + 70,
                (int)replayRect.getWidth() + 20, (int)replayRect.getHeight() + 10);
        g.setStroke(new BasicStroke(3));
        g.drawRect((int)(GameFrame.WIDTH / 2 - replayRect.getWidth() / 2 - 10),
                GameFrame.HEIGHT / 2 + 70,
                (int)replayRect.getWidth() + 20, (int)replayRect.getHeight() + 10);
    }

    public void mouseClicked(int x, int y)  {
        if (replayButtonBounds.contains(x, y)) {
            gsm.frame.startGame();
        }
    }

    /**
     * Draws the score message with the score number.
     * @param g the graphics object to render to
     * @param message String message that is placed before the score
     */
    public void drawScore(Graphics2D g, String message) {
        String score = "YOUR SCORE";
        int offsetY = 0;
        int lineSpace = 0;
        int size1;
        int size2;
        if (message.equals("NEW HIGH SCORE:")) {
            score = gsm.frame.getScore();
            size1 = 20;
            size2 = 30;
            lineSpace = 5;
        } else {
            size1 = 16;
            size2 = 20;
            lineSpace = 2;
            if(message.equals("YOUR SCORE:")) {
                offsetY = -30;
                score = gsm.frame.getScore();
            } else if(message.equals("BEST SCORE:")) {
                offsetY = 30;
                score = Integer.toString(gsm.frame.getHighScore());
            }
        }
        g.setFont(GameFrame.getFont(size1));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D scoreTextRect = fm.getStringBounds(message, g);
        g.drawString(message, (int)(GameFrame.WIDTH / 2 - scoreTextRect.getWidth() / 2),
                GameFrame.HEIGHT / 2 - lineSpace + offsetY);
        g.setFont(GameFrame.getFont(size2));
        fm = g.getFontMetrics();
        Rectangle2D scoreRect = fm.getStringBounds(score, g);
        g.drawString(score, (int)(GameFrame.WIDTH / 2 - scoreRect.getWidth() / 2),
                (int)(GameFrame.HEIGHT / 2 + scoreTextRect.getHeight() + lineSpace + offsetY));
    }
}

