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
 *
 * @author Damian
 */
public class ArrayRenderer<T> extends DefaultTableCellRenderer.UIResource implements TableCellRenderer {
    Class<?> kind;

    /** Creates a new instance of ArrayRenderer */
    public ArrayRenderer(Class<?> kind) {
        super();
        this.kind = kind;
    }

    public void setValue(Object value) {
        final int len = Array.getLength(value);
        StringBuilder ss = new StringBuilder(len+": ");

        if (kind == byte.class || kind == long.class ||
                kind == short.class || kind == double.class ||
                kind == float.class ||
                kind == char.class || kind == boolean.class  ) {
            ArrayList<T> a = new ArrayList<T>(len);
            for (int i = 0; i < len; i++) {
                @SuppressWarnings("unchecked")
                T oo = (T) Array.get(value, i);
                a.add(oo);
            }
            ss.append(a.toString());
        } else {
            try {
            @SuppressWarnings("unchecked")
            ArrayList<T> a = new ArrayList<T>(Arrays.asList((T[]) value)); //nie działa dla typów prymitywnych
            ss.append(a.toString());
            } catch ( java.lang.ClassCastException e) {
                System.err.println(e.toString()+" dla "+kind.getCanonicalName());
            }
        }

        setText(ss.toString());
    }

}
