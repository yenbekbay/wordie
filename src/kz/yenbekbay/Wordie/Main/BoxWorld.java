/**
 * This class represents a physics world in which the balls will be in action.
 */

package kz.yenbekbay.Wordie.Main;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import kz.yenbekbay.Wordie.Box.Ball;
import kz.yenbekbay.Wordie.Box.GameObject;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Vector2;

import javax.imageio.ImageIO;

public class BoxWorld extends World {
    /** Pixels per meter */
    public static final double SCALE = 45.0;

    /** Dimensions */
    double width;
    double height;

    /** Balls */
    public ArrayList<Ball> balls;
    public int numOfBalls;
    private int numPerRow;
    private Ball[] rowBalls;
    private double ballRadius;
    private ArrayList<Double> ballLocs;

    /** Images */
    private BufferedImage bg;
    public static BufferedImage ballBorder;

    /** Timer */
    public double ticks;

    /** Random */
    private Random random;

    /**
     * Constructor for the world.
     */
    public BoxWorld() {
        try {
            bg = ImageIO.read(new File(GameFrame.dir + "/images/playBg.png"));
            ballBorder = ImageIO.read(new File(GameFrame.dir + "/images/ballBorder.png"));
        } catch (IOException e) {
            System.err.println("Caught IOException: " +  e.getMessage());
        }
        // set dimensions
        width = GameFrame.WIDTH / SCALE;
        height = GameFrame.HEIGHT / SCALE;
        // initialize random
        random = new Random();
        // initialize balls
        balls = new ArrayList<Ball>();
        numPerRow = 6;
        rowBalls = new Ball[numPerRow]; // array for creating a row
        double d = 0.05; // distance between balls
        ballRadius = (width - d * 2 - d * numPerRow - 20 / SCALE) / (2 * numPerRow);
        // initialize locations for balls in a row
        ballLocs = new ArrayList<Double>();
        for(int i = 0; i < numPerRow; i++) {
            ballLocs.add(ballRadius + 10 / SCALE + d + (2 * ballRadius + d) * i);
        }
        addBounds();
    }

    /**
     * Creates the screen bounds and adds them to the world.
     */
    private void addBounds() {
        GameObject bounds = new GameObject();
        Segment leftBound = new Segment(new Vector2(10 / SCALE, 0.0), new Vector2(10 / SCALE, height));
        Segment rightBound = new Segment(new Vector2(width - 10 / SCALE, 0.0),
                                         new Vector2(width - 10 / SCALE, height));
        Segment bottomBound = new Segment(new Vector2(0.0, 70 / SCALE),
                                          new Vector2(width, 70 / SCALE));
        bounds.addFixture(leftBound);
        bounds.addFixture(rightBound);
        bounds.addFixture(bottomBound);
        addBody(bounds);
    }

    /**
     * Creates a specified number of balls.
     * @param atOnce the number of balls to create
     */
    public void addBalls(int atOnce) {
        if (isRowFull()) clearRow(); // if the row is full, start a new one
        for (int i = 0; i < atOnce; i++) { // create multiple balls at once
            if (numOfBalls > 0) { // if there are still balls left to create
                addBall();
                numOfBalls--;
            }
        }
    }

    /**
     * Add the ball to the array of balls and positions it correctly on the screen.
     */
    public void addBall() {
        if (isRowFull()) clearRow();
        int ballPos;
        do { // find a free position in the row
            ballPos = random.nextInt(numPerRow);
        } while (rowBalls[ballPos] != null);
        double ballLoc = ballLocs.get(ballPos); // set the location
        // create the ball
        Ball ball = new Ball(ballRadius, ballLoc, height + ballRadius * 2);
        addBody(ball);
        rowBalls[ballPos] = ball;
        balls.add(ball);
    }

    /**
     * Checks if the row is full.
     * @return boolean whether the row is full
     */
    public boolean isRowFull() {
        for (int i = 0; i < numPerRow; i++) {
            if (rowBalls[i] == null) return false;
        }
        return true;
    }

    /**
     * Clears the array of balls in one row.
     */
    private void clearRow() {
        for(int i = 0; i < numPerRow; i++) {
            rowBalls[i] = null;
        }
    }

    /**
     * Removes the ball from the canvas and from the array of balls.
     * @param ball the ball to remove
     */
    public void removeBall(Ball ball) {
        balls.remove(ball);
        removeBody(ball);
    }

    /**
     * Removes all balls from the canvas and clears the array of balls.
     */
    public void removeAllBalls() {
        for (Ball ball : balls) {
            removeBody(ball);
        }
        balls.clear();
    }

    /**
     * Draws the background and the balls.
     * @param g the graphics object to render to
     */
    protected void draw(Graphics2D g) {
        g.drawImage(bg, 0, 0, GameFrame.WIDTH, GameFrame.HEIGHT, null);
        // flip the y axis
        g.transform(AffineTransform.getScaleInstance(1, -1));
        g.transform(AffineTransform.getTranslateInstance(0, -GameFrame.HEIGHT));
        // draw all the objects in the world
        for (int i = 0; i < getBodyCount(); i++) {
            GameObject go = (GameObject) getBody(i);
            go.draw(g);
        }
    }

    /**
     * Checks if the screen is full of balls.
     * @return boolean whether the amount of balls is enough to fill the screen
     */
    public boolean isScreenFull() {
        return ((int)((GameFrame.HEIGHT - 70) / (SCALE * ballRadius * 2)) + 1 <= balls.size() / numPerRow);
    }
}