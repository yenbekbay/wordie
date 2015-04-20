package kz.yenbekbay.Wordie.Box;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class GameObject extends Body {
    /** The scale SCALE pixels per meter */
    public static final double SCALE = 45.0;
    /** The color of the object */
    public Color color;

    /**
     * Default constructor.
     */
    public GameObject() {
        color = new Color(0, 0, 0, 0);
    }

    /**
     * Constructor with color setting.
     */
    public GameObject(Color objectColor) {
        color = objectColor;
    }

    /**
     * Draws the body.
     * Only coded for polygons and circles.
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
        // loop over all the body fixtures for this body
        for (BodyFixture fixture : this.fixtures) {
            // get the shape on the fixture
            Convex convex = fixture.getShape();
            Graphics2DRenderer.render(g, convex, SCALE, color);
        }
        // set the original transform
        g.setTransform(ot);
    }
}