/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.gui;

import dmnutils.SwingUtils;
import dmnutils.browser.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

// Mini przeglądarka internetowa.
import javax.swing.border.EmptyBorder;
public class HelpMini extends JFrame {
    private DisplayPane display;

    public HelpMini() {
        // Ustawienie tytułu.
        super("Mini przeglądarka");
        // Rozmiar okna.
        setSize(680, 480);

        getContentPane().setLayout(new BorderLayout());
//        Toolbar t = new Toolbar();
        display = new DisplayPane();
//        display.setToolbar(t);
//        getContentPane().add(t, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(display);
        scroll.setBorder(new EmptyBorder(0,0,0,0));
        getContentPane().add(scroll, BorderLayout.CENTER);
        //getContentPane().add(new JScrollPane(display), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        display.requestFocusInWindow();
    }

    private void actionExit() {
        System.exit(0);
    }

    /**
     * Tworzy panel z pomocą.
     * @param htmlfile np. "funkcje.html#COS"
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static HelpMini help(String htmlfile) throws FileNotFoundException {
        HelpMini h = new HelpMini();
        h.setTitle("Pomoc " + jstat.Version.NAME);
        //h.display.navigate(Help.getAddr("funkcje.html#COS"));
        if (htmlfile != null) {
            h.display.navigate(Help.getAddr(htmlfile));
        } else {
            h.display.navigate(Help.getAddr());
        }
        h.setSize(680, 480);
        Dimension d = new Dimension(680, 480);
        //h.setMinimumSize(d);
        h.setPreferredSize(d);
        return h;
    }

    /**
     * Tworzy panel z pomocą.
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static HelpMini help() throws FileNotFoundException {
        return help(null);
    }

    /**
     * Przechodzi do strony pomocy.
     * @param s np. "funkcje.html#COS"
     */
    public void navigate(String s) {
        display.navigate(s);
    }

    public static void main(String[] args) {
        try {
            SwingUtils.classForImages = jstat.EmptyClass.class;
            HelpMini h = help();
            if (h != null) {
                dmnutils.SwingUtils.run(h);
            } else {
                System.exit(1);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Help.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}