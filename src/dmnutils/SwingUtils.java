/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */
package dmnutils;
/*
 * SwingUtils.java
 */

import java.awt.Container;
import javax.swing.*;

public class SwingUtils {

    // Possible Look & Feels
    // procedury getXXX dla obsługi JavaBeans
    public static final String mac =
            "com.sun.java.swing.plaf.mac.MacLookAndFeel";

    public String getMac() {
        return motif;
    }
    public static final String metal =
            "javax.swing.plaf.metal.MetalLookAndFeel";

    public String getMetal() {
        return metal;
    }
    public static final String motif =
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

    public String getMotif() {
        return motif;
    }
    public static final String windows =
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

    public String getWindows() {
        return windows;
    }
    public static final String gtk =
            "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";

    public String getGtk() {
        return gtk;
    }
    public static final String plasticWin =
            "com.jgoodies.looks.windows.WindowsLookAndFeel";

    public String getPlasticWin() {
        return plasticWin;
    }
    public static final String plastic =
            "com.jgoodies.looks.windows.PlasticLookAndFeel";

    public String getPlastic() {
        return plastic;
    }
    public static final String plastic3d =
            "com.jgoodies.looks.windows.Plastic3DLookAndFeel";

    public String getPlastic3d() {
        return plastic3d;
    }
    public static final String plasticXp =
            "com.jgoodies.looks.windows.PlasticXPLookAndFeel";

    public String getPlasticXp() {
        return plasticXp;
    }

    /** Creates a new instance of SwingUtils. Ustawia udekorowane okna jako domyślne. */
    public SwingUtils() {
        this(true, true);
    }

    public SwingUtils(boolean jFrameDecorated, boolean jDialogDecorated) {
        JFrame.setDefaultLookAndFeelDecorated(jFrameDecorated);
        JDialog.setDefaultLookAndFeelDecorated(jDialogDecorated);
        UIManager.put("swing.boldMetal", Boolean.FALSE);
    }

    public static void setDefaultLookAndFeelDecorated(boolean jFrameDecorated, boolean jDialogDecorated) {
        JFrame.setDefaultLookAndFeelDecorated(jFrameDecorated);
        JDialog.setDefaultLookAndFeelDecorated(jDialogDecorated);
    }

    /**
     * lookFeel może być:
     *      null - windowsowy dla win, gtk dla lin,
     *      SwingUtils.gtk,
     *      SwingUtils.mac,
     *      SwingUtils.metal,
     *      SwingUtils.motif,
     *      SwingUtils.windows.
     * Zwraca true jeśli ok.
     */
    public static boolean setLookFeel(String lookFeel) {
        try {
            if ((lookFeel == null) || (lookFeel.equals(""))) {
                lookFeel = UIManager.getSystemLookAndFeelClassName();
                String osName = System.getProperty("os.name").toLowerCase();
                if (osName.indexOf("windows") != -1) {
                    lookFeel = windows;
                } else if (osName.indexOf("linux") != -1) {
                    lookFeel = gtk;
                } else if (osName.indexOf("mac") != -1) {
                    lookFeel = mac;
                }
            }
            UIManager.setLookAndFeel(lookFeel);
        } catch (InstantiationException ex) {
            return false;
        } catch (UnsupportedLookAndFeelException ex) {
            return false;
        } catch (ClassNotFoundException ex) {
            return false;
        } catch (IllegalAccessException ex) {
            return false;
        }
        return true;
    }

    // Create a title string from the class name:
    public static String title(Object o) {
        String t = o.getClass().toString();
        // Remove the word "class":
        if (t.indexOf("class") != -1) {
            t = t.substring(6);
        }
        return t;
    }

    public static void run(final JFrame frame, final int width, final int height) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (frame.getWindowListeners().length == 0) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                if (frame.getTitle() == null || frame.getTitle().equals("")) {
                    frame.setTitle(frame.getClass().getSimpleName());
                }
                frame.setSize(width, height);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static void run(final JFrame frame) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (frame.getWindowListeners().length == 0) {
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
                if (frame.getTitle() == null || frame.getTitle().equals("")) {
                    frame.setTitle(frame.getClass().getSimpleName());
                }
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public static JFrame run(JApplet applet, int width, int height) {
        JFrame frame = new JFrame(title(applet));
        if (frame.getWindowListeners().length == 0) {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        frame.getContentPane().add(applet);
        frame.setSize(width, height);
        applet.init();
        applet.start();
        frame.setVisible(true);
        return frame;
    }

    public static JFrame run(JPanel panel, int width, int height) {
        panel.setOpaque(true);
        JFrame f = new JFrame(title(panel));
        f.setContentPane(panel);
        run(f, width, height);
        return f;
    }

    public static JFrame run(JPanel panel) {
        panel.setOpaque(true);
        JFrame f = new JFrame(title(panel));
        f.setContentPane(panel);
        run(f);
        return f;
    }
    /**
     * klasa do pobierania zasobów obrazkowych
     */
    static public Class classForImages;

    /** Returns an ImageIcon, or null if the path was invalid. */
    public static ImageIcon createNavigationIcon(String imageName) {
        if (classForImages == null) {
            throw new RuntimeException("Ustaw SwingUtils.classForImages, aby korzystać z tej funkcji, albo korzystaj z wersji funkcji, w której się podaje klasę w parametrze.");
        }
        return createNavigationIcon(imageName, classForImages);
    }

    public static ImageIcon createNavigationIcon(String imageName, Class classForImages) {
        if (classForImages == null) return null;
        String imgLocation;
        java.net.URL imageURL;
        imgLocation = "images/" + imageName + ".gif";
        imageURL = classForImages.getResource(imgLocation);
        if (imageURL == null) {
            imgLocation = "images/" + imageName + ".png";
            imageURL = classForImages.getResource(imgLocation);
        }
        if (imageURL == null) {
            System.err.println("Resource not found: " + imgLocation);
            return null;
        }
        ImageIcon result = new ImageIcon(imageURL);
        //JOptionPane.showMessageDialog(null, result+"");
        return result;
    }

    /**
     * Ustawia komponenty <code>c1</code> i <code>c2</code> w wierszu, w podanym
     * odstępie poniżej innego komponentu,
     * tak by wypełniały całą dostępną szerokość komponentu, na którym są umieszczone.<br>
     *
     * <p>
     * <code>c1.getParent().getLayout()</code> musi być typu <code>SpringLayout</code>,<br>
     * ponadto <code>c1.getParent().getLayout() = c2.getParent().getLayout()</code>.
     * <p>
     * Jeśli <code>w1 > 0</code>, wtedy komponent <code>c1</code> ma stałą szerokość,
     * a komponent <code>c2</code> jest wyrównywany do szerokości <code>parentComponent</code>
     * (i na odwrót jeśli <code>w2 > 0</code>).
     *
     * @param below komponent, pod którym są układane komponenty (może być <code>null</code>),
     *      wtedy zostaną ułożone tuż pod górną krawędzią komponentu, na którym są ułożone
     * @param space odstęp między dolną krawędzią komponentu <code>below</code> a górną <code>c1</code> i <code>c2</code>
     * @param c1 pierwszy komponent
     * @param w1 pożądana szerokość pierwszego komponentu, lub <code>0</code> dla maksymalnej możliwej
     * @param c2 drugi komponent
     * @param w2 pożądana szerokość drugiego komponentu, lub <code>0</code> dla maksymalnej możliwej
     */
    public static void springSetAsRow(JComponent below, int space, JComponent c1, int w1, JComponent c2, int w2) {
        java.awt.Container parentComponent = c1.getParent();
        SpringLayout layout = (SpringLayout) parentComponent.getLayout();

        //Adjust constraints for the label so it's at (5,5).
        SpringLayout.Constraints labelCons = layout.getConstraints(c1);
        SpringLayout.Constraints textFieldCons = layout.getConstraints(c2);

        if (w1 > 0) {
            labelCons.setWidth(Spring.constant(w1));
            labelCons.setConstraint(SpringLayout.WEST, textFieldCons.getConstraint(SpringLayout.WEST));
        }

        if (w2 > 0) {
            textFieldCons.setWidth(Spring.constant(w2));
            textFieldCons.setConstraint(SpringLayout.EAST, layout.getConstraint(SpringLayout.EAST, parentComponent));
        }

        if (w1 <= 0) {
            labelCons.setX(Spring.constant(space));
            labelCons.setConstraint(SpringLayout.EAST, textFieldCons.getConstraint(SpringLayout.WEST));
        }

        if (w2 <= 0) {
            textFieldCons.setX(Spring.sum(Spring.constant(5), labelCons.getConstraint(SpringLayout.EAST)));
            textFieldCons.setConstraint(SpringLayout.EAST, layout.getConstraint(SpringLayout.EAST, parentComponent));
        }

        if (below != null) {
            SpringLayout.Constraints belowCons = layout.getConstraints(below);
            labelCons.setY(Spring.sum(Spring.constant(space), belowCons.getConstraint(SpringLayout.SOUTH)));
            textFieldCons.setY(Spring.sum(Spring.constant(space), belowCons.getConstraint(SpringLayout.SOUTH)));
        } else {
            labelCons.setY(Spring.constant(space));
            textFieldCons.setY(Spring.constant(space));
        }
    //Adjust constraints for the content pane.
    //setContainerSize(steps[0], 5);
    }

    /**
     * Ustawia komponent poniżej drugiego komponentu
     * w podanym odstępie, tak by wypełniał całą dostępną szerokość.
     * <p>
     * <code>c1.getParent().getLayout()</code> musi być typu <code>SpringLayout</code>.
     *
     * @param below komponent, pod którym jest układany (może być null)
     * @param space odstęp między dolną krawędzią komponentu <code>below</code> a górną <code>c1</code>
     * @param c1 pierwszy komponent
     */
    public static void springPutBelow(JComponent below, int space, JComponent c1) {
        java.awt.Container parentComponent = c1.getParent();
        SpringLayout layout = (SpringLayout) parentComponent.getLayout();

        //Adjust constraints for the label so it's at (5,5).
        SpringLayout.Constraints labelCons = layout.getConstraints(c1);

        labelCons.setConstraint(SpringLayout.EAST, layout.getConstraint(SpringLayout.EAST, parentComponent));
        labelCons.setConstraint(SpringLayout.WEST, layout.getConstraint(SpringLayout.WEST, parentComponent));

        if (below != null) {
            SpringLayout.Constraints belowCons = layout.getConstraints(below);
            labelCons.setY(Spring.sum(Spring.constant(5), belowCons.getConstraint(SpringLayout.SOUTH)));
        } else {
            labelCons.setY(Spring.constant(space));
        }
    //Adjust constraints for the content pane.
    //setContainerSize(steps[0], 5);
    }

    /**
     * Dla obiektu typu <code>Container</code> przeszukuje hierarchię (<code>Container</code>)
     * w poszukiwaniu rodzica podanej klasy i zwraca go.
     * @param a <code>Container</code> do przeszukania
     * @param c poszukiwana klasa rodzica
     * @return rodzic podanej klasy lub <code>null</code>.
     */
    public static Container findParentWithGivenClass(Container a, Class c) {
        while (a != null && !c.isInstance(a)) {
            a = a.getParent();
        }
        return a;
    }
}
