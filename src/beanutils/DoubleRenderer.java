/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.text.NumberFormat;

/**
 *
 * @author Damian
 */
public class DoubleRenderer extends NumberRenderer {
    NumberFormat formatter;

    public DoubleRenderer() {
        super();
    }

    public void setValue(Object value) {
        if (formatter == null) {
            formatter = NumberFormat.getInstance();
        }
        setText((value == null) ? "" : formatter.format(value));
    }
}