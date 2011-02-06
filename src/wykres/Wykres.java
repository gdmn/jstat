/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package wykres;

import java.io.*;
import kalkulator.*;
import static wykres.Settings.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author dmn
 */
public class Wykres implements Externalizable {

    protected double xMin,  xMax,  yMin,  yMax;
    //public static final int POINTS = 100;
    public int POINTS = 100;
    private double zmiennaXval;
    private WyrazeniePostfix wyrazenie;
    private String wyrazenieString; //potrzebne do eksternalizacji obiektu
    private JLabel assignedLabel;
    private RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    protected int width = 100,  height = 100;

    public Wykres(double xMin, double xMax, double yMin, double yMax, WyrazeniePostfix wyrazenie) {
        this.wyrazenie = wyrazenie;
        this.wyrazenieString = null;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        constructorHelper();
    }

    public Wykres(double xMin, double xMax, double yMin, double yMax, String wyrazenie) {
        this.wyrazenie = new WyrazeniePostfix(WyrazenieInfix.valueOf(wyrazenie, myFunctions));
        this.wyrazenieString = wyrazenie;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        constructorHelper();
    }

    public void setXYMinMax(double xMin, double xMax, double yMin, double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }

    /**
     * Uwaga: tylko dla potrzeb eksternalizacji. NIE UŻYWAĆ!
     */
    public Wykres() {
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    private void constructorHelper() {
        this.wyrazenie.setUserArrayOfFunctions(myFunctions);
        renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        throw new UnsupportedOperationException("Nie można wywołać Wykres.writeExternal");
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Nie można wywołać Wykres.readExternal");
    }

    public void setAssignedLabel(JLabel l) {
        assignedLabel = l;
    }

    public JLabel getAssignedLabel() {
        return assignedLabel;
    }

    protected void say(String s) {
        if (assignedLabel != null) {
            assignedLabel.setText(s);
        }
    }
    private FunkcjaInterface zmiennaX = new FunkcjaInterface() {

        public String toString() {
            return "X";
        }

        public int getArgumentsCount() {
            return 0;
        }

        public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
            return new Liczba(zmiennaXval);
        }
    };
    private FunkcjaInterface[] myFunctions = new FunkcjaInterface[] {zmiennaX};

    private double oblicz(double x) {
        double result;
        zmiennaXval = x;
        //pow(x; 0-1)
        try {
            result = ((Liczba) wyrazenie.getValue()).getValueAsDouble();
        } catch (NumberFormatException ex) {
            return Double.NaN;
        } catch (InvalidArgumentException ex) {
            return Double.NaN;
        } catch (ArithmeticException ex) {
            return Double.NaN;
        }
        return result;
    }

    /**
     * Zamienia x z osi OX do x w pikselach na wykresie
     */
    protected int convertToX(double x) {
        return (int) ((width - 1) * (x - xMin) / (xMax - xMin));
    }

    /**
     * Zamienia y z osi OY do y w pikselach na wykresie
     */
    protected int convertToY(double y) {
        return (int) ((height - 1) * (yMax - y) / (yMax - yMin));
    }

    /**
     * Zamienia x w pikselach na wykresie do x z osi OX
     */
    protected double convertFromX(int x) {
        return ((double) x - 0) * (xMax - xMin) / (width - 1) + xMin;
    }

    /**
     * Zamienia y w pikselach na wykresie do y z osi OY
     */
    protected double convertFromY(int y) {
        return -((double) y) * (yMax - yMin) / (height - 1) + yMax;
    }

    @SuppressWarnings("cast")
    protected void customPaint_wykres(Graphics g) {
        int maxWidth = width;
        POINTS = maxWidth / 2;
        double[] pts;
        double d;
        double hstep = (double) maxWidth / ((double) POINTS - 1);
        pts = new double[POINTS];
        for (int i = 0; i < POINTS; i++) {
            d = oblicz(((double) i / (POINTS - 1) * (xMax - xMin) + xMin));
            if (Double.isInfinite(d) || Double.isNaN(d)) {
                pts[i] = d;
            } else {
                pts[i] = convertToY(d);
            }
        }
        g.setColor(COLORGRAPH);
        for (int i = 1; i < POINTS; i++) {
            double y1 = pts[i - 1];
            double y2 = pts[i];
            if (Double.isInfinite(y1) || Double.isInfinite(y2) || Double.isNaN(y1) || Double.isNaN(y2)) {
                continue;
            }
            int x1 = (int) ((i - 1) * hstep);
            int x2 = (int) (i * hstep);
            g.drawLine(x1, (int) y1, x2, (int) y2);
        }
    }

    public void paintTo(Graphics g, boolean tylkoOsie) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.addRenderingHints(renderingHints);
        g2d.setColor(wykres.Settings.COLORBACKGROUND);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(wykres.Settings.COLORBORDER);
        g2d.drawRect(0, 0, width - 1, height - 1);
        customPaint_osie(g);
        if (!tylkoOsie) {
            customPaint_wykres(g);
        }
    }

    public void paintTo(Graphics g) {
        paintTo(g, false);
    }


    /**
     * Zapisuje bieżący wykres do podanego pliku.
     * @param file plik do zapisania.
     * @throws java.io.IOException jeśli błędy przy zapisie.
     */
    public void savePNG(File file) throws IOException {
        savePNG(file, width, height);
    }

    /**
     * Zapisuje bieżący wykres do podanego pliku.
     * @param file plik do zapisania,
     * @param imgwidth szerokość obrazka,
     * @param imgheight wysokość obrazka.
     * @throws java.io.IOException jeśli błędy przy zapisie.
     */
    public void savePNG(File file, int imgwidth, int imgheight) throws IOException {
        int oldwidth = this.width;
        int oldheight = this.height;
        this.width = imgwidth;
        this.height = imgheight;
        // http://java.sun.com/docs/books/tutorial/2d/images/drawonimage.html
        BufferedImage img = new BufferedImage(imgwidth, imgheight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        paintTo(g2);
        this.width = oldwidth;
        this.height = oldheight;
        ImageIO.write(img, "png", file);
    }

    public WyrazeniePostfix getWyrazenie() {
        return wyrazenie;
    }

    public String getWyrazenieString() {
        return wyrazenieString;
    }

    public void setWyrazenie(WyrazeniePostfix wyrazenie) {
        this.wyrazenie = wyrazenie;
        wyrazenieString = null;
    }

    public void setWyrazenie(String wyrazenie) {
        this.wyrazenieString = wyrazenie;
        this.wyrazenie = new WyrazeniePostfix(WyrazenieInfix.valueOf(wyrazenie, myFunctions));
    }

    private String doubleToString(double d) {
        long newdouble = Math.round(d * 100);
        return String.format("%." +
                //((int)d == d ? 0 : (Math.round(d*10) == d*10 ? 1 : 2))+ //trzeba rozwiązać inaczej ze względu na zaokrąglenia
                (newdouble % 100 == 0 ? 0 : (newdouble % 10 == 0 ? 1 : 2)) +
                "f", new Double(0.01d * newdouble));
    }

    @SuppressWarnings("unused")
    private static int getIntCloserToZero(double v) {
        return (v > 0) ? (int) Math.round(Math.floor(v)) : (int) Math.round(Math.ceil(v));
    }
    public static int PREFEREDKRATKASIZE = 50;

    private static double obliczJednostke(double min, double max, int pixelsWidth) {
        double dlugoscdouble = max - min;
        double jednostkiPoczatkowe[] = new double[] {1, 0.5, 0.25, 0.2, 0.1};
        while (jednostkiPoczatkowe[0] * pixelsWidth / dlugoscdouble < PREFEREDKRATKASIZE) {
            for (int i = 0; i < jednostkiPoczatkowe.length; i++) {
                jednostkiPoczatkowe[i] *= 10;
            }
        }
        int jednostkaId = 0;
        double jednostka = jednostkiPoczatkowe[jednostkaId];
        double wielkoscKratki;
        do {
            jednostkaId = 0;
            do {
                jednostka = jednostkiPoczatkowe[jednostkaId++];
                double jednostkadivdlugoscdouble = jednostka / dlugoscdouble;
                wielkoscKratki = (jednostkadivdlugoscdouble * pixelsWidth);
            } while (jednostkaId < jednostkiPoczatkowe.length && wielkoscKratki >= PREFEREDKRATKASIZE);
            if (wielkoscKratki >= PREFEREDKRATKASIZE) {
                for (int i = 0; i < jednostkiPoczatkowe.length; i++) {
                    jednostkiPoczatkowe[i] *= .1;
                }
            }
        } while (wielkoscKratki >= PREFEREDKRATKASIZE);
        return jednostka;
    }

    @SuppressWarnings("cast")
    protected void customPaint_osie(Graphics g) {
        int firstPixX = 0;//insets.left;
        int firstPixY = 0;//insets.top;
        int lastPixX = width;//() - insets.right;
        int lastPixY = height;//() - insets.bottom;

        int x0 = convertToX(0);
        int y0 = convertToY(0);

        //int maxWidth = getWidth();
        int maxWidth = width;//() - insets.left - insets.right;
        double xStep = obliczJednostke(xMin, xMax, maxWidth);

        //int maxHeight = getHeight();
        int maxHeight = height;//() - insets.top - insets.bottom;
        double yStep = obliczJednostke(yMin, yMax, maxHeight);

        int xStepCount = (int) ((xMax - xMin) / xStep);
        //int xStepCount =(int)Math.floor((xMax-xMin) / xStep);
        double xStart = 0;
        //rozpoczęcie rysowania siatki od wielokrotności kroku:
        if (xMin < 0) {
            while (xStart > xMin) {
                xStart -= xStep;
            }
            xStart += xStep;
        } else {
            while (xStart < xMin) {
                xStart += xStep;
            }
        }


        int yStepCount = (int) ((yMax - yMin) / yStep);
        double yStart = 0;
        //rozpoczęcie rysowania siatki od wielokrotności kroku:
        if (yMin < 0) {
            while (yStart > yMin) {
                yStart -= yStep;
            }
            yStart += yStep;
        } else {
            while (yStart < yMin) {
                yStart += yStep;
            }
        }

        Graphics2D g2d = (Graphics2D) g.create(); //copy g

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        //g2d.setFont(new Font("Serif", Font.PLAIN, 30));
        FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());

        g2d.setColor(COLORGRIDV);
        for (int ix = 0; ix < xStepCount + 1; ix++) { //pionowe linie siatki
            double xVal = xStart + xStep * ix;
            int i = convertToX(xVal);
            g2d.drawLine(i, firstPixY, i, lastPixY);
        }
        g2d.setColor(COLORGRIDH);
        for (int iy = 0; iy < yStepCount + 1; iy++) { //poziome linie siatki
            double yVal = yStart + yStep * iy;
            int i = convertToY(yVal);
            g2d.drawLine(firstPixX, i, lastPixX, i);
        }

        { //oś OX
            g2d.setColor(COLORAXISOX);
            g2d.drawLine(firstPixX, y0, lastPixX, y0);
            //g2d.fillPolygon(new int[] {1,100, 400}, new int[] {1,100, 1}, 3);
            g2d.setColor(COLORAXISOXINTERSECTION);
            g2d.fillPolygon(new int[] {lastPixX, lastPixX - 8, lastPixX - 8}, new int[] {y0, y0 - 4, y0 + 4}, 3);

        }
        { //oś OY
            g2d.setColor(COLORAXISOY);
            g2d.drawLine(x0, firstPixY, x0, lastPixY);
            g2d.setColor(COLORAXISOYINTERSECTION);
            g2d.fillPolygon(new int[] {x0, x0 - 4, x0 + 4, x0}, new int[] {firstPixY, firstPixY + 8, firstPixY + 8, firstPixY - 1}, 4); //rysuje symetryczną strzałkę dzieki temu "-1"
        }

        {
            int yNapisy = (int) (y0/*-metrics.getHeight()*.5*/) - 2 - 5;
            if (y0 > lastPixY - 2) {
                if (y0 <= lastPixY) {
                    yNapisy = lastPixY - 2 - 5;
                } else {
                    yNapisy = lastPixY - 2;
                }
            } else if (y0 < firstPixY + metrics.getHeight() / 2 + 2 + 5) {
                if (y0 >= firstPixY) {
                    yNapisy = y0 + metrics.getHeight() / 2 + 2 + 5;
                } else {
                    yNapisy = firstPixY + metrics.getHeight() / 2 + 2;
                }
            }
            for (int ix = 0; ix < xStepCount + 1; ix++) { //napisy nad osią OX
                double xVal = xStart + xStep * ix;
                int i = convertToX(xVal);
                String text = doubleToString(xVal);
                if (text.equals("0")) {
                    continue;
                }
                int textWidth = metrics.stringWidth(text);
                {
                    int xNapisy = i - textWidth / 2;
                    if (xNapisy >= firstPixX && xNapisy + textWidth < lastPixX) {
                        g2d.setColor(COLORAXISOXTEXT);
                        g2d.drawString(text, xNapisy, yNapisy);
                        g2d.setColor(COLORAXISOXINTERSECTION); //przecięcia osi na kolorowo
                        g2d.drawLine(i, y0 - 3, i, y0 + 3);

                    }
                }
            }
        }
        {
            int xNapisy = x0 + 5;
            int textWidth = (int) kalkulator.Tools.getMax((double) metrics.stringWidth(doubleToString(yStart + yStep)),
                    metrics.stringWidth(doubleToString(yStart + yStep * 2)),
                    metrics.stringWidth(doubleToString(yStart + yStep * 3)),
                    metrics.stringWidth(doubleToString(yStart + yStep * (yStepCount - 1))),
                    metrics.stringWidth(doubleToString(yStart + yStep * (yStepCount - 2))),
                    metrics.stringWidth(doubleToString(yStart + yStep * (yStepCount - 3))));
            if (x0 < firstPixX + 2 + 5) {
                if (x0 >= firstPixX) {
                    xNapisy = firstPixX + 2 + 5;
                } else {
                    xNapisy = firstPixX + 2;
                }
            } else if (x0 > lastPixX - textWidth - 2 - 5) {
                if (x0 <= lastPixX) {
                    xNapisy = x0 - textWidth - 2 - 5;
                } else {
                    xNapisy = lastPixX - textWidth - 2;
                }
            }
            for (int iy = 0; iy < yStepCount + 1; iy++) { //napisy nad osią OY
                double yVal = yStart + yStep * iy;
                int i = convertToY(yVal);
                String text = doubleToString(yVal);
                if (text.equals("0")) {
                    continue;
                }
                textWidth = metrics.stringWidth(text);
                int textHeight = metrics.getHeight();
                {
                    int yNapisy = (int) (i + textHeight * .25);
                    if (yNapisy - textHeight >= firstPixY && yNapisy < lastPixY) {
                        g2d.setColor(COLORAXISOYTEXT);
                        g2d.drawString(text, xNapisy, yNapisy);
                        g2d.setColor(COLORAXISOYINTERSECTION); //przecięcia osi na kolorowo
                        g2d.drawLine(x0 - 3, i, x0 + 3, i);
                    }
                }
            }
        }

        g2d.dispose(); //release the copy's resources
    }
    private boolean repaintWhenMouseReleased = false;

    public void scroll(double xCenter, double yCenter) {
        double xDelta = xMax - xMin;
        double yDelta = yMax - yMin;
        xMin = xCenter - xDelta * 0.5;
        yMin = yCenter - yDelta * 0.5;
        xMax = xMin + xDelta;
        yMax = yMin + yDelta;
    }

    public void scrollAlittle(double xCenter, double yCenter) {
        double xDelta = xMax - xMin;
        double yDelta = yMax - yMin;
        xMin = xMin + (-xMin + (xCenter - xDelta * 0.5)) * .05;
        yMin = yMin + (-yMin + (yCenter - yDelta * 0.5)) * .05;
        xMax = xMin + xDelta;
        yMax = yMin + yDelta;
    }

    public void zoom(double xCenter, double yCenter, double amountX, double amountY) {
        if (amountX < 0.0D) {
            amountX = -1D / amountX;
        }
        if (amountY < 0.0D) {
            amountY = -1D / amountY;
        }
        double xDelta = (xMax - xMin) * amountX;
        double yDelta = (yMax - yMin) * amountY;
        xMin = xCenter - xDelta * 0.5D;
        yMin = yCenter - yDelta * 0.5D;
        xMax = xMin + xDelta;
        yMax = yMin + yDelta;
    }

    public void zoom(double xCenter, double yCenter, double amount) {
        zoom(xCenter, yCenter, amount, amount);
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }


}
