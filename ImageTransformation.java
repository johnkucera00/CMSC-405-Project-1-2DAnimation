/*
* File: ImageTransformation.java
* Author: John Kucera (Original template provided by Prof. Amitava Karmaker)
* Date: August 25, 2020
* Purpose: This Java program is meant to display images created in ImageCreation.java
* and then transform them in an animation. A JPanel is created, then the
* BufferedImages from ImageCreation.java are drawn onto it. The animation starts,
* displaying transformations in sequence frame by frame. The current frame is
* always output in the console. The images continue to cycle, always returning
* back to the intiial position/state and then transforming again.
*/

// import necessary java classes
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

// Class: ImageTransformation
public class ImageTransformation extends JPanel {

    // Variable initialization
    // A counter that increases by one in each frame.
    private int frameNumber;
    // The time, in milliseconds, since the animation started.
    private long elapsedTimeMillis;
    // This is the measure of a pixel in the coordinate system
    // set up by calling the applyLimits method.  It can be used
    // for setting line widths, for example.
    private float pixelSize;
    private int imageCount = 1;
    
    // Initial image state
    static int translateX = 0;
    static int translateY = 0;
    static double rotation = 0.0;
    static double scaleX = 1.0;
    static double scaleY = 1.0;
    
    // Create images
    ImageCreation myImages = new ImageCreation();
    BufferedImage imageW = myImages.getImage(ImageCreation.letterW);
    BufferedImage image5 = myImages.getImage(ImageCreation.number5);
    BufferedImage imageStar = myImages.getImage(ImageCreation.shapeStar);
    
    // Method: main
    public static void main(String[] args) {
        // TODO code application logic here
        JFrame window;
        window = new JFrame("Java Animation");  // The parameter shows in the window title bar.
        final ImageTransformation panel = new ImageTransformation(); // The drawing area.
        window.setContentPane(panel); // Show the panel in the window.
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // End program when window closes.
        window.pack();  // Set window size based on the preferred sizes of its contents.
        window.setResizable(false); // Don't let user resize window.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation( // Center window on screen.
                (screen.width - window.getWidth()) / 2,
                (screen.height - window.getHeight()) / 2);
        Timer animationTimer;  // A Timer that will emit events to drive the animation.
        final long startTime = System.currentTimeMillis();
        // Taken from AnimationStarter
        // Modified to change timing and allow for recycling
        animationTimer = new Timer(1600, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (panel.frameNumber >= 5) {
                    panel.frameNumber = 1;
                } else {
                    panel.frameNumber++;
                }
                panel.elapsedTimeMillis = System.currentTimeMillis() - startTime;
                panel.repaint();
            }
        });
        window.setVisible(true); // Open the window, making it visible on the screen.
        animationTimer.start();  // Start the animation running.
    } // end of main method

    // Method: creates JFrame
    public ImageTransformation() {
        // Size of Frame
        setPreferredSize(new Dimension(600, 600));
    } // end of method

    // This is where all of the action takes place
    // Code taken from AnimationStarter.java but modified to add the specific Images
    // Also added looping structure for Different transformations
    protected void paintComponent(Graphics g) {

        /* First, create a Graphics2D drawing context for drawing on the panel.
         * (g.create() makes a copy of g, which will draw to the same place as g,
         * but changes to the returned copy will not affect the original.)
         */
        Graphics2D g2 = (Graphics2D) g.create();

        /* Turn on antialiasing in this graphics context, for better drawing.
         */
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /* Fill in the entire drawing area with white.
         */
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight()); // From the old graphics API!

        /* Here, I set up a new coordinate system on the drawing area, by calling
         * the applyLimits() method that is defined below.  Without this call, I
         * would be using regular pixel coordinates.  This function sets the value
         * of the global variable pixelSize, which I need for stroke widths in the
         * transformed coordinate system.
         */
        // Controls your zoom and area you are looking at
        applyWindowToViewportTransformation(g2, -200, 0, 0, 200, true);

        AffineTransform savedTransform = g2.getTransform();
        // Preventing "Frame is 0" from appearing in beginning of run
        if (frameNumber != 0) {
            System.out.println("Frame is " + frameNumber);
        } // end if
        switch (frameNumber) {
            case 1: // First frame is unmodified.
                resetImages();
                break;
            case 2: // Second frame translates each image by (-5, 7).
                translateX = -5;
                translateY = 7;
                break;
            case 3: // Third frame rotates each image by 45 degrees Counter-clockwise
                rotation = 45 * Math.PI / 180.0;
                break;
            case 4: // Fourth frame rotates each image by 90 degrees Clockwise
                rotation -= 90 * Math.PI / 180.0;
                break;
            case 5: // Fifth frame scales 2 for x component, 0.5 for y component
                scaleX = 2.0;
                scaleY = 0.5;
                break;
            default:
                break;
        } // End switch
        
        // Drawing 3 images in frame
        drawMyImage(g2, savedTransform, imageW);
        drawMyImage(g2, savedTransform, image5);
        drawMyImage(g2, savedTransform, imageStar);

        // Reset image count
        imageCount = 1;
    } // end of method

    // Method taken directly from AnimationStarter.java Code
    private void applyWindowToViewportTransformation(Graphics2D g2,
            double left, double right, double bottom, double top,
            boolean preserveAspect) {
        int width = getWidth();   // The width of this drawing area, in pixels.
        int height = getHeight(); // The height of this drawing area, in pixels.
        if (preserveAspect) {
            // Adjust the limits to match the aspect ratio of the drawing area.
            double displayAspect = Math.abs((double) height / width);
            double requestedAspect = Math.abs((bottom - top) / (right - left));
            if (displayAspect > requestedAspect) {
                // Expand the viewport vertically.
                double excess = (bottom - top) * (displayAspect / requestedAspect - 1);
                bottom += excess / 2;
                top -= excess / 2;
            } else if (displayAspect < requestedAspect) {
                // Expand the viewport vertically.
                double excess = (right - left) * (requestedAspect / displayAspect - 1);
                right += excess / 2;
                left -= excess / 2;
            }
        }
        g2.scale(width / (right - left), height / (bottom - top));
        g2.translate(-left, -top);
        double pixelWidth = Math.abs((right - left) / width);
        double pixelHeight = Math.abs((bottom - top) / height);
        pixelSize = (float) Math.max(pixelWidth, pixelHeight);
    } // end of method
    
    // Method: resetImages brings images back to initial state
    private static void resetImages() {
        translateX = 0;
        translateY = 0;
        rotation = 0.0;
        scaleX = 1.0;
        scaleY = 1.0;
    } // end of method
    
    // Method: drawMyImage, used for properly creating each image from imageCreation
    private void drawMyImage(Graphics2D g2, AffineTransform savedTransform, BufferedImage image) {
        imageCount++;
        g2.translate(translateX, translateY);
        g2.translate(-37*imageCount,37*imageCount);
        g2.rotate(rotation);
        g2.scale(scaleX, scaleY);
        g2.drawImage(image, 0, 0, this);
        g2.setTransform(savedTransform);
    } // end of method
} // end of class
