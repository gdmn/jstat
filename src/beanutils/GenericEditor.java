/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/**
 *
 * @author Damian
 */
public class GenericEditor extends DefaultCellEditor {

    Class[] argTypes = new Class[]{String.class};
    java.lang.reflect.Constructor constructor;
    Object value;

    TableValueSetterInterface tableSetter;
    int row, col;

    public GenericEditor() {
        super(new JTextField());
        getComponent().setName("Table.editor");
    }

    public boolean stopCellEditing() {
        String s = (String)super.getCellEditorValue();
        // Here we are dealing with the case where a user
        // has deleted the string value in a cell, possibly
        // after a failed validation. Return null, so that
        // they have the option to replace the value with
        // null or use escape to restore the original.
        // For Strings, return "" for backward compatibility.
        if ("".equals(s)) {
            if (constructor.getDeclaringClass() == String.class) {
                value = s;
            }
            super.stopCellEditing();
        }

        try {
            value = constructor.newInstance(new Object[]{s});
            //Object old = tableModel.getValueAt(row, col);
            if (!tableSetter.tryToSetValueAt(value, row, col)) {
                /* dzięki temu wyjątkowi, jeśli nastąpi np. PropertyVetoException,
                 * to ramka komórki robi się czerwona i nie pozwala na zakończenie
                 * edycji
                 */
                throw new Exception();
            }
        } catch (Exception e) {
            ((JComponent)getComponent()).setBorder(new LineBorder(Color.red));
            return false;
        }
        return super.stopCellEditing();
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected,
            int row, int column) {
        this.value = null;

        if (table instanceof TableValueSetterInterface) {
            @SuppressWarnings("unchecked")
            TableValueSetterInterface tmp = (TableValueSetterInterface) table;
            tableSetter = tmp;
        } else {
            new RuntimeException("JTable nie implementuje TableValueSetterInterface");
        }
        this.row = row;
        this.col = column;

        ((JComponent)getComponent()).setBorder(new LineBorder(Color.black));
        try {
            @SuppressWarnings("unchecked")
            Class type = ((BeanEditorTable)table).getMyCellClass(row, column);
            //Class type = table.getColumnClass(column);
            // Since our obligation is to produce a value which is
            // assignable for the required type it is OK to use the
            // String constructor for columns which are declared
            // to contain Objects. A String is an Object.
            if (type == Object.class) {
                type = String.class;
            }
            type = ValueOfParser.bazowyDoOpakowujacego(type);
            @SuppressWarnings("unchecked")
            java.lang.reflect.Constructor tmp = type.getConstructor(argTypes);
            constructor = tmp;
        } catch (Exception e) {
            return null;
        }
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    public Object getCellEditorValue() {
        return value;
    }
}