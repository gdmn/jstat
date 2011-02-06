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
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

/**
 *
 * @author Damian
 */
public class BooleanEditor extends DefaultCellEditor {

    TableValueSetterInterface tableSetter;
    int row, col;

    public BooleanEditor() {
        super(new JCheckBox());
        JCheckBox checkBox = (JCheckBox)getComponent();
        checkBox.setHorizontalAlignment(JCheckBox.CENTER);
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

        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

}
