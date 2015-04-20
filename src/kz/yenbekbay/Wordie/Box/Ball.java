/**
 * This class creates and renders the ball object for the box world.
 */

package kz.yenbekbay.Wordie.Box;

import kz.yenbekbay.Wordie.Main.BoxWorld;
import kz.yenbekbay.Wordie.Main.GameFrame;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Vector2;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

public class Ball extends GameObject {
    private BodyFixture circleFixture;
    public char letter;

    /** Colors */
    public static final Color GREEN = new Color(0, 232, 66);
    public static final Color BLUE = new Color(0, 158, 255);
    public static final Color YELLOW = new Color(232, 232, 0);
    public static final Color ORANGE = new Color(232, 136, 0);
    public static final Color RED = new Color(255, 2, 0);
    public Color colors[];

    /** Whether is active or not */
    public boolean active;

    /** Frame number */
    private int currentFrame;

    /** Fade when deactivated */
    private boolean fade;

    /**
     * Ball constructor.
     * @param ballRadius ball radius in meters
     * @param offsetX translation in x axis in meters
     * @param offsetY translation in y axis in meters
     */
    public Ball(double ballRadius, double offsetX, double offsetY) {
        // initialize colors
        colors = new Color[]{GREEN, BLUE, YELLOW, ORANGE, RED};
        // set random color
        Random random = new Random();
        color = colors[random.nextInt(5)];
        // set random letter
        letter = (char)((int)'A' + Math.random() * ((int)'Z' - (int)'A' + 1));
        // create the object
        Circle circle = new Circle(ballRadius);
        circleFixture = new BodyFixture(circle);
        circleFixture.setRestitution(0.1);
        addFixture(circleFixture);
        setMass();
        translate(offsetX, offsetY);
        setLinearDamping(0.1);
        applyForce(new Vector2(random.nextInt(5), 0));
        // set to inactive
        active = false;
    }

    /**
     * Draws the circle, its outline, the letter, and animated border if applicable.
     * @param g the graphics object to render to
     */
    public void draw(Graphics2D g) {
        // save the original transform
        AffineTransform ot = g.getTransform();
        // transform the coordinate system from world coordinates to local coordinates
        AffineTransform lt = new AffineTransform();
        lt.translate(this.transform.getTranslationX() * SCALE, this.transform.getTranslationY() * SCALE);
        lt.rotate(this.transform.getRotation());
        // apply the transform
        g.transform(lt);
        // get the circle dimensions
        Circle circle = (Circle)circleFixture.getShape();
        double radius = circle.getRadius();
        Vector2 center = circle.getCenter();
        double diameter = 2.0 * radius;
        // create the circle with local coordinates
        Ellipse2D.Double c = new Ellipse2D.Double(
                (center.x - radius) * SCALE,
                (center.y - radius) * SCALE,
                diameter * SCALE,
                diameter * SCALE);
        // fill the shape
        g.setColor(color);
        g.fill(c);
        // draw the outline
        g.setColor(Graphics2DRenderer.getOutlineColor(color));
        g.draw(c);
        // draw the letter
        g.setColor(Color.white);
        String string = Character.toString(letter);
        g.setFont(GameFrame.getFont((int)(radius * SCALE)));
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D rect = fm.getStringBounds(string, g);
        g.transform(AffineTransform.getScaleInstance(1, -1));
        g.drawString(string, (int)(c.getX() + (c.getWidth() - rect.getWidth()) / 2 + 1),
                             (int)(c.getY() + (c.getHeight() - rect.getHeight()) / 2 + fm.getAscent() + 2));
        // draw the border
        if (active) {
            g.rotate(currentFrame * 0.03);
            g.drawImage(BoxWorld.ballBorder, (int)(c.getX() + (c.getWidth() - diameter * SCALE) / 2),
                                             (int)(c.getY() + (c.getHeight() - diameter * SCALE) / 2),
                                             (int)(diameter * SCALE), (int)(diameter * SCALE), null);
            if (currentFrame == 360) currentFrame = 1;
            else currentFrame++;
        }
        // highlight in red
        if (fade) {
            g.setColor(new Color(255, 0, 0, 100 - currentFrame * 5));
            g.fill(c);
            if (currentFrame == 20) {
                fade = false;
                currentFrame = 0;
            } else {
                currentFrame++;
            }
        }
        // set the original transform
        g.setTransform(ot);
    }

    /**
     * Checks if the given point is inside the ball.
     * @param x the x coordinate
     * @param y the y coordinate
     * @return boolean whether the ball contains this point
     */
    public boolean contains(int x, int y) {
        double radius = circleFixture.getShape().getRadius() * SCALE;
        Vector2 pos = new Vector2(getWorldCenter().x * SCALE,
                (GameFrame.HEIGHT - getWorldCenter().y * SCALE));
        return (((x <= pos.x && x >= pos.x - radius) || (x >= pos.x && x <= pos.x + radius)) &&
                ((y <= pos.y && y >= pos.y - radius) || (y >= pos.y && y <= pos.y + radius)));
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        active = false;
    }

    public void fade() {
        fade = true;
        currentFrame = 0;
    }
}
