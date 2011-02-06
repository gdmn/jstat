/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.gui;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Filtr do otwierania/zapisywania dokumentów
 * @author dmn
 */
public class JStatFileFilter extends dmnutils.MyFileFilter {

    public final static String jstat = "jstat";

    public JStatFileFilter() {
        super(jstat);
    }

    public static String setJstatExtension(File f) {
        JStatFileFilter filtr = new JStatFileFilter();
        return filtr.setDefaultExtension(f);
    }
}
