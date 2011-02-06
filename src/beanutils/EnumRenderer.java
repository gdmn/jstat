/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.lang.reflect.*;
import java.util.*;
import javax.swing.table.*;

/**
 *
 * @author dmn
 */
public class EnumRenderer extends DefaultTableCellRenderer.UIResource implements TableCellRenderer {

    /** Creates a new instance of ArrayRenderer */
    public EnumRenderer() {
        super();
    }

    public void setValue(Object value) {
        //Enum enumvalue= (Enum) value;
        if (value != null) {
            setText(value.toString());
        } else {
            setText("");
        }
    }
}
