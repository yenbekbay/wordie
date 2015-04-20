/**
 * This class decides which game state to draw and
 * handles switching between different screens.
 */

package kz.yenbekbay.Wordie.Main;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import kz.yenbekbay.Wordie.GameState.GameOverState;
import kz.yenbekbay.Wordie.GameState.GameState;
import kz.yenbekbay.Wordie.GameState.MenuState;
import kz.yenbekbay.Wordie.GameState.PauseState;
import kz.yenbekbay.Wordie.GameState.PlayState;

public class StateManager {
    public GameState[] gameStates;
    public int currentState;
    public int previousState;

    public static final int NUM_STATES = 4;
    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int PAUSE = 2;
    public static final int GAMEOVER = 3;

    public GameFrame frame;

    private MouseEvent lastMouseEvent;

    /**
     * Initializes the array of states.
     * @param gameFrame the active frame
     */
    public StateManager(GameFrame gameFrame) {
        frame = gameFrame;
        gameStates = new GameState[NUM_STATES];
    }

    /**
     * Initializes the specified game state and
     * updates the mouse listen  er
     * @param i number corresponding to the game state
     */
    public void setState(int i) {
        previousState = currentState;
        unloadState(previousState);
        currentState = i;
        if(i == MENU) {
            gameStates[i] = new MenuState(this);
        } else if(i == PLAY) {
            gameStates[i] = new PlayState(this);
        } else if(i == PAUSE) {
            gameStates[i] = new PauseState(this);
        } else if(i == GAMEOVER) {
            gameStates[i] = new GameOverState(this);
        }
        gameStates[currentState].init();
        updateListener();
    }

    /**
     *
     * Reset the specified game state.
     * @param i number corresponding to the game state
     */
    public void unloadState(int i) {
        gameStates[i] = null;
    }

    public void updateListener() {
        frame.canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                if (mouseEvent != lastMouseEvent) {
                    lastMouseEvent = mouseEvent;
                    gameStates[currentState].mouseClicked(mouseEvent.getX(), mouseEvent.getY());
                }
            }
        });
    }

    /**
     * Draws the active game state.
     * @param g the graphics object to render to
     */
    public void draw(Graphics2D g) {
        gameStates[currentState].draw(g);
    }
}
