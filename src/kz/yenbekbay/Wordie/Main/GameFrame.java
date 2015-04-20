/**
 * This class loads up a JFrame window and initializes the game.
 */

package kz.yenbekbay.Wordie.Main;

import kz.yenbekbay.Wordie.GameState.Popup;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.io.*;
import java.util.ArrayList;

public class GameFrame extends JFrame {
    public Word word;
    public BoxWorld boxWorld;
    public StateManager gsm;

    /** Window dimensions */
    public static final int WIDTH = 320;
    public static final int HEIGHT = 460;

    /** Drawing */
    public Canvas canvas;
    public Graphics2D g;

    /** Status */
    public int status;
    public static final int STOPPED = 0;
    public static final int RUNNING = 1;

    /** The time stamp for the last iteration */
    public long last;

    /** Time elapsed since the beginning of the game */
    public double timer;

    /** The conversion factor from nano to base */
    public static final double NANO_TO_BASE = 1.0e9;

    /** Game score */
    public int score;

    /** Font */
    public static Font font;

    /** Scores */
    ArrayList<Integer> scores;

    /** Directory of the project */
    public static String dir;

    /**
     * Creates and initializes the game window.
     */
    public static void main(String[] args) {
        GameFrame frame = new GameFrame();
        frame.setVisible(true);
        frame.init();
    }

    /**
     * Constructor for the window.
     */
    public GameFrame() {
        super("Wordie"); // set the title
        // set the project directory
        try {
            File currentDirectory = new File(new File(".").getAbsolutePath());
            dir = currentDirectory.getCanonicalPath();
        } catch (IOException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        }
        setFont();
        setScores();
        word = new Word(); // create the word handler
        boxWorld = new BoxWorld(); // create the physics world
        gsm = new StateManager(this); // create the state manager
        canvas = new Canvas(); // create the canvas
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        add(canvas);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true); // show the window
    }

    /**
     * Sets the canvas, gets scores and shows the menu.
     */
    private void init() {
        canvas.setIgnoreRepaint(true); // don't allow AWT to paint the canvas
        canvas.createBufferStrategy(2); // enable double buffering
        showState(StateManager.MENU);
    }

    /**
     * Sets the game font.
     */
    private void setFont() {
        try {
            File currentDirectory = new File(new File(".").getAbsolutePath());
            String dir = currentDirectory.getCanonicalPath();
            font = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(dir + "/resources/BPreplayBold.ttf"));
        } catch (IOException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        } catch (FontFormatException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        }
    }

    /**
     * Returns the font of specified size.
     */
    public static Font getFont(int size) {
        font = font.deriveFont((float)size);
        return font;
    }

    /**
     * Creates a thread and runs the loop.
     */
    public void startGame() {
        gsm.setState(StateManager.PLAY); // set the game state
        setStatus(RUNNING); // set the status
        boxWorld.removeAllBalls(); // reset the balls
        boxWorld.numOfBalls = 30; // set the initial number of balls
        word.reset(); // reset word
        timer = 60; // set the timer
        score = 0; // reset the score
        runThread();
    }

    public void resumeGame() {
        gsm.setState(StateManager.PLAY); // set the game state
        setStatus(GameFrame.RUNNING);
        runThread();
    }

    /**
     * Creates a thread an runs it infinitely until stopped.
     */
    public void runThread() {
        last = System.nanoTime(); // initialize the last update time
        // create a thread
        Thread thread = new Thread() {
            public void run() {
                // perform an infinite loop
                while (!isStopped()) {
                    gameLoop();
                    Thread.yield();
                }
            }
        };
        thread.setDaemon(true); // set the game loop thread to a daemon thread
        thread.start(); // start the game loop
    }

    /**
     * Drives the game.
     */
    public void gameLoop() {
        // get the graphics object to render to
        Graphics2D g = (Graphics2D)canvas.getBufferStrategy().getDrawGraphics();
        if (!boxWorld.isScreenFull() && timer > 0) {
            boxWorld.draw(g); // draw the world
            gsm.draw(g); // draw the state
            g.dispose(); // dispose graphics
            // show the canvas
            BufferStrategy strategy = canvas.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
            Toolkit.getDefaultToolkit().sync(); // sync the display on some systems
            long time = System.nanoTime(); // get the current time
            long diff = time - last; // get the elapsed time from the last iteration
            last = time; // set the last time
            double elapsedTime = (double)diff / NANO_TO_BASE; // convert from nanoseconds to seconds
            boxWorld.update(elapsedTime); // update the world with the elapsed time
            int atOnce = 3; // how many balls should fall at once
            if ((int)((boxWorld.ticks + elapsedTime) * 4) > (int)(boxWorld.ticks * 4) && boxWorld.numOfBalls > 0) {
                // create the initial set of balls
                boxWorld.addBalls(atOnce);
            } else if (boxWorld.numOfBalls == 0) {
                if ((int)((boxWorld.ticks + elapsedTime) / 5) > (int)(boxWorld.ticks / 5) &&
                        boxWorld.ticks > boxWorld.balls.size()/(4 * atOnce) + 5) {
                    // add a ball every 3 seconds
                    boxWorld.addBall();
                }
                // update timer when the initial set of balls finished falling
                timer -= elapsedTime;
            }
            boxWorld.ticks += elapsedTime;
        } else {
            setStatus(STOPPED);
            addScore(score);
            showState(StateManager.GAMEOVER);
        }
    }

    /**
     * Shows the specified state of the game.
     * @param stateNum number corresponding to a game state
     */
    public void showState(int stateNum) {
        gsm.setState(stateNum);
        // get the graphics object to render to
        Graphics2D g = (Graphics2D)canvas.getBufferStrategy().getDrawGraphics();
        // draw the popup window if the game is paused or over
        if (stateNum == StateManager.PAUSE || stateNum == StateManager.GAMEOVER) {
            Popup popup = new Popup();
            popup.draw(g);
        }
        gsm.draw(g); // draw the state
        // show the canvas
        BufferStrategy strategy = canvas.getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    /**
     * Sets the specified status.
     * @param i number corresponding to a status
     */
    public synchronized void setStatus(int i) {
        status = i;
    }

    /**
     * Returns true if the game is stopped.
     * @return boolean true if stopped
     */
    public synchronized boolean isStopped() {
        return (status == STOPPED);
    }

    /**
     * Returns the value of the timer.
     * @return String left time
     */
    public String getTimer() {
        return Integer.toString((int)timer);
    }

    /**
     * Returns the score value.
     * @return String current score
     */
    public String getScore() {
        return Integer.toString(score);
    }

    /**
     * Add the score to the scores array and to the text file
     * @param score int number of points
     */
    public void addScore(int score) {
        scores.add(score);
        if (score != 0) {
            try {
                String file = dir + "/resources/scores.txt";
                FileWriter fw = new FileWriter(file, true);
                fw.write(score + "\n");
                fw.close();
            } catch(IOException e) {
                System.err.println("Caught IOException: " +  e.getMessage());
            }
        }
    }

    /**
     * Initializes the array of scores and adds the scores from the text file to it.w
     */
    public void setScores() {
        try {
            // Open the file as a buffered reader
            BufferedReader bf = new BufferedReader(new FileReader(dir + "/resources/scores.txt"));
            // initialize the array
            scores = new ArrayList<Integer>();
            // declare a string to hold our current line
            String line;
            // Loop through each line, add each score to the array
            while ((line = bf.readLine()) != null) {
                scores.add(Integer.parseInt(line));
            }
            // Close the file after done searching
            bf.close();
        }
        catch (IOException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        }
    }

    /**
     * Returns the highest score from the array of scores.
     * @return int high score
     */
    public int getHighScore() {
        int highScore = 0;
        for (int score : scores) {
            if (highScore == 0 || score > highScore) highScore = score;
        }
        return highScore;
    }
}
