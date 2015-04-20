/**
 * This class displays a popup window with the resume button
 * and handles continuing the game.
 */

package kz.yenbekbay.Wordie.GameState;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import kz.yenbekbay.Wordie.Main.GameFrame;
import kz.yenbekbay.Wordie.Main.StateManager;

public class PauseState extends GameState {
    private Rectangle resumeButtonBounds;

    public PauseState(StateManager gsm) {
        super(gsm);
    }

    public void init() {  }

    public void draw(Graphics2D g) {
        // Draw resume button
        g.setColor(Color.white);
        g.setFont(GameFrame.getFont(30));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D resumeRect = fm.getStringBounds("RESUME", g);
        g.drawString("RESUME", (int)(GameFrame.WIDTH / 2 - resumeRect.getWidth() / 2),
                (int)(GameFrame.HEIGHT / 2 + resumeRect.getHeight() / 2 - 5));
        resumeButtonBounds = new Rectangle((int)(GameFrame.WIDTH / 2 - resumeRect.getWidth() / 2 - 10),
                (int)(GameFrame.HEIGHT / 2 - resumeRect.getHeight() / 2 - 5),
                (int)resumeRect.getWidth() + 20, (int)resumeRect.getHeight() + 10);
        g.setStroke(new BasicStroke(3));
        g.drawRect((int)(GameFrame.WIDTH / 2 - resumeRect.getWidth() / 2 - 10),
                (int)(GameFrame.HEIGHT / 2 - resumeRect.getHeight() / 2 - 5),
                (int)resumeRect.getWidth() + 20, (int)resumeRect.getHeight() + 10);
    }

    public void mouseClicked(int x, int y)  {
        if (resumeButtonBounds.contains(x, y)) {
            gsm.frame.resumeGame();
        }
    }
}

