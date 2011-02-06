/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

import dmnutils.SwingUtils;
import javax.swing.*;

import java.awt.*;
import java.net.*;

import jstat.gui.Main;

/**
 *
 * @author Damian
 */

public class JStatApplet extends JApplet {
    public void init() {
        showStatus("wczytywanie...");
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't successfully complete");
        }
    }

    private void createGUI() {
        //(new jstat.SwingUtils(false, false)).setLookFeel(SwingUtils.metal);
        //UIManager.put("swing.boldMetal", Boolean.FALSE);
        getContentPane().setLayout(new BorderLayout());
        //getContentPane().add(new SwingSet2(this), BorderLayout.CENTER);
                SwingUtils su = new dmnutils.SwingUtils(false, false);
        if (System.getProperty("os.name").toLowerCase().equals("linux")) {
            SwingUtils.setLookFeel(SwingUtils.metal);
        } else {
            SwingUtils.setLookFeel(null);
        }
        Main m = new Main(this);
        getContentPane().add(m.getContentPane(), BorderLayout.CENTER);
        setJMenuBar(m.getJMenuBar());

        //JPanel m = wykres.Wykres.wykresNoweOkno(new Wykres(-10, 10, -10, 10, "1/sqrt(abs(0.2*x*x-5+sin(x)*x))"));
        getContentPane().add(m, BorderLayout.CENTER);
    }

    public void start() {
        showStatus(this.getCodeBase().toString());
    }

    public void stop() {

    }

    public void destroy() {

    }

    public URL getURL(String filename) {
        URL codeBase = this.getCodeBase();
        URL url = null;

        try {
            url = new URL(codeBase, filename);
            System.out.println(url);
        } catch (java.net.MalformedURLException e) {
            System.out.println("Error: badly specified URL");
            return null;
        }

        return url;
    }


}
