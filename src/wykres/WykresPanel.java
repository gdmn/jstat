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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import dmnutils.SwingUtils;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dmn
 */
public class WykresPanel extends JPanel implements Externalizable, MouseInputListener, MouseWheelListener {

    private Wykres wykres;
    /**
     * <li>1 - automatycznie przeglądanie</li>
     * <li>2 - przeciąganie</li>
     * <li>3 - zoom</li>
     */
    private int scrollmode;

    public WykresPanel(double xMin, double xMax, double yMin, double yMax, WyrazeniePostfix wyrazenie) {
        wykres = new Wykres(xMin, xMax, yMin, yMax, wyrazenie);
        constructorHelper();
    }

    public WykresPanel(double xMin, double xMax, double yMin, double yMax, String wyrazenie) {
        wykres = new Wykres(xMin, xMax, yMin, yMax, wyrazenie);
        constructorHelper();
    }

    /**
     * Uwaga: tylko dla potrzeb eksternalizacji. NIE UŻYWAĆ!
     */
    public WykresPanel() {
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    private void constructorHelper() {
        setPreferredSize(new Dimension(200, 200));
        setMinimumSize(new Dimension(100, 100));
        setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, COLORBORDER));
        setBackground(COLORBACKGROUND);
        setOpaque(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public Wykres getWykres() {
        return wykres;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(wykres.getWyrazenieString());
        double[] rozmiar = {wykres.xMin, wykres.xMax, wykres.yMin, wykres.yMax};
        out.writeObject(rozmiar);
        constructorHelper();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        String wyrazenieString = (String) in.readObject();
        double[] rozmiar = (double[]) in.readObject();
        wykres = new Wykres(rozmiar[0], rozmiar[1], rozmiar[2], rozmiar[3], wyrazenieString);
        constructorHelper();
    }

    public void setAssignedLabel(JLabel l) {
        wykres.setAssignedLabel(l);
    }

    public JLabel getAssignedLabel() {
        return wykres.getAssignedLabel();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        wykres.width = getWidth();
        wykres.height = getHeight();
        if (Settings.LIVESCROLLING || (!scrolling)) {
            repaintWhenMouseReleased = false;
            wykres.paintTo(g);
        } else {
            repaintWhenMouseReleased = true;
            wykres.paintTo(g, true);
        }
    }

    public WyrazeniePostfix getWyrazenie() {
        return wykres.getWyrazenie();
    }

    public String getWyrazenieString() {
        return wykres.getWyrazenieString();
    }

    public void setWyrazenie(WyrazeniePostfix wyrazenie) {
        wykres.setWyrazenie(wyrazenie);
        repaint();
    }

    public void setWyrazenie(String wyrazenie) {
        wykres.setWyrazenie(wyrazenie);
        repaint();
    }
    private boolean repaintWhenMouseReleased = false;

    public void mouseClicked(MouseEvent e) {
    }
    private boolean scrolling = false;

    class ScrollingThread extends javax.swing.SwingWorker implements java.io.Serializable {

        protected Object doInBackground() throws Exception {
            while (scrolling && (!this.isCancelled())) {
                wykres.scrollAlittle(wykres.convertFromX(mouseX), wykres.convertFromY(mouseY));
                repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    break;
                }
            }
            return null;
        }
    }
    ScrollingThread myScrollingThread = (new ScrollingThread());

    public void mousePressed(MouseEvent e) {
        if (scrolling) {
            return;
        }
        if (e.getButton() == 1) {
            if (e.isShiftDown()) {
                mouseX = e.getX();
                mouseY = e.getY();
                scrolling = true;
                scrollmode = 1;
                myScrollingThread = new ScrollingThread();
                myScrollingThread.execute();
                return;
            }
            if (e.isControlDown()) {
                mouseX = e.getX();
                mouseY = e.getY();
                scrolling = true;
                scrollmode = 3;
                return;
            } else {
                mouseX = e.getX();
                mouseY = e.getY();
                scrolling = true;
                scrollmode = 2;
                return;
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        scrolling = false;
        myScrollingThread.cancel(true);
        if (repaintWhenMouseReleased) {
            repaint();
        }
    }
    int mouseX = 0, mouseY = 0;

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        if (!scrolling) {
            return;
        }
        int oldMouseX = mouseX;
        int oldMouseY = mouseY;
        mouseX = e.getX();
        mouseY = e.getY();
        switch (scrollmode) {
            case 1: // '\001'
            {
                break;
            }

            case 2: // '\002'
            {
                double dx = wykres.convertFromX(mouseX) - wykres.convertFromX(oldMouseX);
                double dy = wykres.convertFromY(mouseY) - wykres.convertFromY(oldMouseY);
                double xDelta = wykres.xMax - wykres.xMin;
                double yDelta = wykres.yMax - wykres.yMin;
                double xCenter = wykres.xMin + xDelta * 0.5D;
                double yCenter = wykres.yMin + yDelta * 0.5D;
                wykres.scroll(xCenter - dx, yCenter - dy);
                repaint();
                break;
            }

            case 3: // '\003'
            {
                double dx = wykres.convertFromX(mouseX) - wykres.convertFromX(oldMouseX);
                double dy = wykres.convertFromY(mouseY) - wykres.convertFromY(oldMouseY);
                double xDelta = wykres.xMax - wykres.xMin;
                double yDelta = wykres.yMax - wykres.yMin;
                double xCenter = wykres.xMin + xDelta * 0.5D;
                double yCenter = wykres.yMin + yDelta * 0.5D;
                wykres.zoom(xCenter, yCenter, 1.0D + (2D * dx) / xDelta, 1.0D + (2D * dy) / yDelta);
                repaint();
                break;
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        double amount = e.getUnitsToScroll();
        amount = 1d + amount * 0.1;
        wykres.zoom(0.5 * (wykres.xMax + wykres.xMin), 0.5 * (wykres.yMax + wykres.yMin), amount);
        repaint();
    }

    private static class DemoWindow extends JPanel implements ActionListener {

        private JTextField textInput;
        private WykresPanel wykres;

        public DemoWindow(final WykresPanel handle) {
            super();
            this.wykres = handle;
            setLayout(new BorderLayout());

            StringBuilder caption = new StringBuilder();
            caption.append("dla x \u0454 <" + handle.wykres.xMin + "; " + handle.wykres.xMax + "> i  y \u0454 <" + handle.wykres.yMin + "; " + handle.wykres.yMax + ">");
            JLabel l = new JLabel(caption + "");
            handle.setAssignedLabel(l);

            handle.setOpaque(true);
            add(handle, BorderLayout.CENTER);

            textInput = new JTextField(20);
            textInput.addActionListener(this);
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(textInput, BorderLayout.SOUTH);
            bottomPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
            add(bottomPanel, BorderLayout.SOUTH);
        }

        public void actionPerformed(ActionEvent actionEvent) {
            String fromUser = textInput.getText();
            wykres.setWyrazenie(fromUser);
        }
    }

    public static JPanel wykresNoweOkno(final WykresPanel handle) {
        return new DemoWindow(handle);
    }

    public static void main(String[] args) {
        //wykresNoweOkno(new Wykres2(-10, 6, -10, 6, "0.2*x*x-5")).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //wykresNoweOkno(new Wykres2(-5, -0.2, -5, 0.1, "0.2*x*x-5")).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel f = wykresNoweOkno(new WykresPanel(-10, 10, -10, 10, "1/sqrt(abs(0.2*x*x-5+sin(x)*x))"));
        //f.setVisible(false);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.run(f);
    }
}
