/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Damian
 */
public class IconRenderer extends DefaultTableCellRenderer.UIResource {
    public IconRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
    }

    public void setValue(Object value) {
        setIcon((value instanceof Icon) ? (Icon) value : null);
    }
}