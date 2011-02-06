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
import javax.swing.JTextField;

/**
 * NumberEditor z kompensacją zacierania (TIJ4, rozdz. 15, str. 552)
 * @author Damian
 */
public class NumberEditor<T extends Number> extends GenericEditor {
    Class<T> kind;

    public NumberEditor(Class<T> kind) {
        ((JTextField)getComponent()).setHorizontalAlignment(JTextField.RIGHT);
        this.kind = kind;
    }

    public T getCellEditorValue() {
        String s = null;
        try {
            // FIXME: robić to sprawdzanie, czy zostawić na ew. Veto?
            if (value != null)
                s = value.toString();
            else
                s = "0";
            return ValueOfParser.<T>valueOf(s, kind);
        } catch (NullPointerException ex) {
            System.err.println("NullPointerException: "+kind.getSimpleName()+".valueOf("+s+")");
        } catch (IllegalArgumentException ex) {
            System.err.println("IllegalArgumentException: "+kind.getSimpleName()+".valueOf("+s+")");
        } catch (IllegalAccessException ex) {
            System.err.println("IllegalAccessException: "+kind.getSimpleName()+".valueOf("+s+")");
        } catch (InvocationTargetException ex) {
            System.err.println("InvocationTargetException: "+kind.getSimpleName()+".valueOf("+s+")");
        }
        return null;
    }

}