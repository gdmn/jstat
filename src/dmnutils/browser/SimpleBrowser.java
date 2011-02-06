/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.browser;

import java.awt.BorderLayout;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dmn
 */
public class SimpleBrowser extends JPanel {
    private DisplayPane pane;

    public SimpleBrowser(boolean toolbars, URL url) {
        setLayout(new BorderLayout());
        pane = new DisplayPane();
//        pane.setBorder(new EmptyBorder(0,0,0,0));
        if (toolbars) {
            Toolbar t = new Toolbar();
            pane.setToolbar(t);
            add(t, BorderLayout.NORTH);
            t.getLocationTextField().requestFocusInWindow();
        }
        if (url != null) {
            pane.navigate(url);
        }
        JScrollPane scroll = new JScrollPane(pane);
        scroll.setBorder(new EmptyBorder(0,0,0,0));
        add(scroll, BorderLayout.CENTER);
        if (!toolbars) {
            pane.requestFocusInWindow();
        }
    }

    public SimpleBrowser(boolean toolbars) {
        this(toolbars, null);
    }

    public SimpleBrowser(URL url) {
        this(true, url);
    }

    public SimpleBrowser(String url) {
        this(true, null);
        navigate(url);
    }

    public SimpleBrowser() {
        this(true, null);
    }

    public DisplayPane getDisplayPane() {
        return pane;
    }

    public void navigate(URL url) {
        pane.navigate(url);
    }

    public void navigate(String url) {
        pane.navigate(url);
    }

    /**
     * pokazuje panel w JFrame
     */
    public JFrame showFrame() {
        JFrame f = new JFrame("SimpleBrowser");
        f.setContentPane(this);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        return f;
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dmnutils.SwingUtils.classForImages = jstat.EmptyClass.class;
                dmnutils.SwingUtils.run(new SimpleBrowser("http://www.google.pl/"));
//                createAndShowGUI();
            }
        });
    }
}
