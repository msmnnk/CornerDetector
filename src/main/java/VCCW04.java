import Basic.CornerDetector;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
 * This file contains small code fragments
 * from the initial file of lab 6 (VC06.java)
 */

public class VCCW04 extends Component implements KeyListener {
    // variables for images
    private BufferedImage image;
    private BufferedImage image2;
    private final File file;

    public VCCW04() {
        super();
        // Task 4a: load an image to be processed by the algorithms
        file = new File("Room.jpg");
        try {
            image = ImageIO.read(file);
            image2 = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Image reading error!\n");
            e.printStackTrace();
        }
        addKeyListener(this);
    }

    public void finalImage() {
        try {
            // read and link the file with the images variables
            image = ImageIO.read(file);
            image2 = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Image reading error!\n");
            e.printStackTrace();
        }
        // Harris coefficient
        double k = 0.04;
        // find R value for the first image
        double[][] R;
        try {
            R = CornerDetector.findR(image, k, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CornerDetector.normalize(R);
        CornerDetector.threshold(R);
        // find R value for the second image
        double[][] R2;
        try {
            R2 = CornerDetector.findR(image2, k, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CornerDetector.normalize(R2);
        CornerDetector.threshold(R2);
        // write the results to new files
        File outputHarris = new File("OutputHarris.jpg");
        File outputShiTomasi = new File("OutputShiTomasi.jpg");
        try {
            ImageIO.write(CornerDetector.cornerDetector(R, image), "jpg", outputHarris);
            ImageIO.write(CornerDetector.cornerDetector(R2, image2), "jpg", outputShiTomasi);
        } catch (Exception e) {
            System.err.println("Image writing error!\n");
            e.printStackTrace();
        }
        // read the result images and repaint
        try {
            image = ImageIO.read(outputHarris);
            image2 = ImageIO.read(outputShiTomasi);
        } catch (Exception e) {
            System.err.println("Image reading error!\n");
            e.printStackTrace();
        }
        repaint();
    }

    // Task 4d: display images side-by-side
    @Override
    public Dimension getPreferredSize() {
        // sum the widths
        int w = (image.getWidth() + image2.getWidth());
        // get preferable height
        int h1 = image.getHeight();
        int h2 = image2.getHeight();
        int h = Math.max(h1, h2);
        return new Dimension(w, h);
    }

    @Override
    public void paint(Graphics g) {
        // define captions on the images
        String key1 = "Harris";
        String key2 = "Shi-Tomasi";
        // Task 4a: display an image to be processed by the algorithms
        g.drawImage(image, 0, 0, this);
        g.drawImage(image2, image.getWidth(), 0, this);
        // draw captions
        g.setFont(new Font("Arial Black", Font.PLAIN, 16));
        g.drawString(key1, 198, 25);
        g.drawString(key2, 628, 25);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
            // Task 4d: display the corner detection results
            // using the two algorithms when the key 'p'/'P' is pressed
        } else if (ke.getKeyChar() == 'p' || ke.getKeyChar() == 'P') {
            finalImage();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) throws IOException {
        // create a separate thread for the commands below
        Runnable r = () -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception useDefault) {
                System.err.println("Error!\n");
                useDefault.printStackTrace();
            }
            VCCW04 obj = new VCCW04();
            JFrame frame = new JFrame("Coursework 4"); // set the window name
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationByPlatform(true);
            frame.add("Center", obj);
            frame.pack();
            frame.setMinimumSize(frame.getSize());
            obj.requestFocusInWindow();
            frame.setVisible(true);
        };
        SwingUtilities.invokeLater(r);
    }

}
