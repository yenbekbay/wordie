/**
 * This abstract class is a framework for the states of the game.
 */

package kz.yenbekbay.Wordie.GameState;

import java.awt.Graphics2D;

import kz.yenbekbay.Wordie.Main.StateManager;

public abstract class GameState {
    protected StateManager gsm;

    /**
     * Default constructor.
     * @param gsm the game state manager to which the state belongs
     */
    public GameState(StateManager gsm) {
        this.gsm = gsm;
    }

    /**
     * Initializes the state and saves all the images in the buffer.
     */
    public abstract void init();

    /**
     * Draws everything on the canvas.
     * @param g the graphics object to render to
     */
    public abstract void draw(Graphics2D g);

    /**
     * Calls the action corresponding to the point on the screen.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public abstract void mouseClicked(int x, int y);
}
