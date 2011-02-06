/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ColorEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    Color currentColor;
    JButton button;
    JColorChooser colorChooser;
    JDialog dialog;
    protected static final String EDIT = "edit";

    private int row, col;
    TableValueSetterInterface tableSetter;

    public ColorEditor() {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton("...");
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);

        //Set up the dialog that the button brings up.
        colorChooser = new JColorChooser();
        dialog = JColorChooser.createDialog(button,
                "Pick a Color",
                true,  //modal
                colorChooser,
                this,  //OK button handler
                null); //no CANCEL button handler
    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            //button.setBackground(currentColor);
            colorChooser.setColor(currentColor);
            dialog.setVisible(true);

            //Make the renderer reappear.
            fireEditingStopped();

        } else { //User pressed dialog's "OK" button.
            Color tmp = colorChooser.getColor();
            if (!tableSetter.tryToSetValueAt(tmp, row, col)) {
                JOptionPane.showMessageDialog(dialog, "Wystąpiły błędy podczas przypisywania wartości.\nZajrzyj do dziennika, aby uzyskać więcej informacji.", "Błąd", JOptionPane.ERROR_MESSAGE);
                //textArea.requestFocusInWindow();
            } else {
                currentColor = tmp;
                dialog.setVisible(false);
            }
        }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return currentColor;
    }

    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
            Object value,
            boolean isSelected,
            int row,
            int column) {

        if (table instanceof TableValueSetterInterface) {
            @SuppressWarnings("unchecked")
            TableValueSetterInterface tmp = (TableValueSetterInterface) table;
            tableSetter = tmp;
        } else {
            new RuntimeException("JTable nie implementuje TableValueSetterInterface");
        }
        this.row = row;
        this.col = column;

        currentColor = (Color)value;
        button.setText("0x" + Integer.toHexString(currentColor.getRGB()).substring(2).toUpperCase());
        return button;
    }
}
