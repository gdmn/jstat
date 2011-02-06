/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */


package dmnutils.wizard;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

/**
 *
 * @author Damian
 */
public class SlickPanel extends JPanel {
    private Color first;
    private int[] firstRGB = new int[4];
    private Color second;
    private int[] secondRGB = new int[4];

    /** Creates a new instance of SlickPanel */
    public SlickPanel(Color first, Color second) {
        setOpaque(true);
        setFirst(first);
        setSecond(second);
        setBackground(first);
    }

    Image img;
    int imgWidth = 0, imgHeight = 0;
    public void prepareImage(int width, int height) {
        int pixels[] = new int[width * height];
        int c[] = new int[4];   //temporarily holds R,G,B,A information
        int index = 0;
        for (int i = 0; i < width; i++) {
            c[0] = c[1] = c[2] = 0;
            c[3] = 255;
            float progress = (1f/(width-1))*i;
            c[0] = (int)( (1.0-progress)*(double)firstRGB[0]+progress*(double)secondRGB[0] );
            c[1] = (int)( (1.0-progress)*(double)firstRGB[1]+progress*(double)secondRGB[1] );
            c[2] = (int)( (1.0-progress)*(double)firstRGB[2]+progress*(double)secondRGB[2] );
            {
                int temp = ((c[3] << 24) |
                        (c[0] << 16) |
                        (c[1] << 8) |
                        c[2]);
                for (int j = 0; j < height; j++) {
                    //pixels[index++] = temp;
                    pixels[j*width+i] = temp;
                }
            }
        }
        imgWidth = width;
        imgHeight = height;
        img = createImage(new MemoryImageSource(width, height,
                ColorModel.getRGBdefault(), pixels, 0, width));
    }


    protected void paintComponent(Graphics g) {
        Insets ins = this.getInsets();
        int width = getWidth() - ins.left - ins.right -0;
        int height = getHeight() - ins.top - ins.bottom -0;
        if (img == null) prepareImage(width, height);
        if (width != imgWidth || height != imgHeight) prepareImage(width, height);
        g.drawImage(img, ins.left, ins.top, null);
    }

    public Color getFirst() {
        return first;
    }

    private int[] colorToRGB(Color c) {
        int[] result = new int[4];
        result[2] = c.getRGB() & 0xFF;
        result[1] = (c.getRGB() >> 8) & 0xFF;
        result[0] = (c.getRGB() >> 16) & 0xFF;
        result[3] = (c.getRGB() >> 24);
        return result;
    }

    public void setFirst(Color first) {
        this.first = first;
        firstRGB = colorToRGB(first);
    }

    public void setSecond(Color second) {
        this.second = second;
        secondRGB = colorToRGB(second);
    }


}
