/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;

/**
 *
 * @author dmn
 */
public class EnumEditor extends DefaultCellEditor implements ActionListener {

    TableValueSetterInterface tableSetter;
    int row, col;
    JComboBox comboBox;

    /** Creates a new instance of EnumEditor */
    public EnumEditor() {
        super(new JComboBox());

        comboBox = (JComboBox)getComponent();
        comboBox.addActionListener(this);
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected,
            int row, int column) {

        if (table instanceof TableValueSetterInterface) {
            @SuppressWarnings("unchecked")
            TableValueSetterInterface tmp = (TableValueSetterInterface) table;
            tableSetter = tmp;
        } else {
            new RuntimeException("JTable nie implementuje TableValueSetterInterface");
        }
        this.row = row;
        this.col = column;

        Enum enumvalue = (Enum) value;
        comboBox.removeActionListener(this);
        comboBox.removeAllItems();
        comboBox.setSelectedIndex(-1);
        comboBox.setMaximumRowCount(20);
        Method m_values = null;
        try {
            m_values = enumvalue.getClass().getMethod("values");
            for (Object k : (Object[]) m_values.invoke(null)) {
                comboBox.addItem(k);
            }
            comboBox.addActionListener(this);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }


        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    public void actionPerformed(ActionEvent e) {
    }



}
