/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

 package jstat.pages.sheet;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.util.*;
import jstat.plugins.wrappers.Tools;

/**
 * ExcelAdapter enables Copy-Paste Clipboard functionality on JTables.
 * The clipboard data format used by the adapter is compatible with
 * the clipboard format used by Excel. This provides for clipboard
 * interoperability between enabled JTables and Excel.
 *
 * źródło: http://www.javaworld.com/javaworld/javatips/jw-javatip77.html
 */
public class ExcelAdapter implements ActionListener {

    private String rowstring,  value;
    private Clipboard system;
    private StringSelection stsel;
    private JTable jTable1;

    public static String ACTION_COPY = "ACTION_COPY";
    public static String ACTION_PASTE = "ACTION_PASTE";
    public static String ACTION_CUT = "ACTION_CUT";

    /**
     * The Excel Adapter is constructed with a
     * JTable on which it enables Copy-Paste and acts
     * as a Clipboard listener.
     */
    public ExcelAdapter(JTable myJTable) {
        jTable1 = myJTable;
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
        // Identifying the copy KeyStroke user can modify this
        // to copy on some other Key combination.
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
        // Identifying the Paste KeyStroke user can modify thisACTION_COPY
        //to copy on some other Key combination.
        KeyStroke cut = KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false);
        jTable1.registerKeyboardAction(this, ACTION_COPY, copy, JComponent.WHEN_FOCUSED);
        jTable1.registerKeyboardAction(this, ACTION_PASTE, paste, JComponent.WHEN_FOCUSED);
        jTable1.registerKeyboardAction(this, ACTION_CUT, cut, JComponent.WHEN_FOCUSED);
        system = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * Public Accessor methods for the Table on which this adapter acts.
     */
    public JTable getJTable() {
        return jTable1;
    }

    public void setJTable(JTable jTable1) {
        this.jTable1 = jTable1;
    }

    /**
     * This method is activated on the Keystrokes we are listening to
     * in this implementation. Here it listens for Copy and Paste ActionCommands.
     * Selections comprising non-adjacent cells result in invalid selection and
     * then copy action cannot be performed.
     * Paste is done by aligning the upper left corner of the selection with the
     * 1st element in the current selection of the JTable.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo(ACTION_COPY) == 0 || e.getActionCommand().compareTo(ACTION_CUT) == 0) {
            boolean cutting = e.getActionCommand().compareTo(ACTION_CUT) == 0;
            StringBuffer sbf = new StringBuffer();
            // Check to ensure we have selected only a contiguous block of
            // cells
            int numcols = jTable1.getSelectedColumnCount();
            int numrows = jTable1.getSelectedRowCount();
            int[] rowsselected = jTable1.getSelectedRows();
            int[] colsselected = jTable1.getSelectedColumns();
            if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] &&
                    numrows == rowsselected.length) &&
                    (numcols - 1 == colsselected[colsselected.length - 1] - colsselected[0] &&
                    numcols == colsselected.length))) {
                Tools.showErr("Nieprawidłowe zaznaczenie.");
                return;
            }
            for (int i = 0; i < numrows; i++) {
                for (int j = 0; j < numcols; j++) {
                    Object b = jTable1.getValueAt(rowsselected[i], colsselected[j]);
                    if (b != null) {
                        sbf.append(b);
                    }
                    if (j < numcols - 1) {
                        sbf.append("\t");
                    }
                    if (cutting) {
                        jTable1.setValueAt(null, rowsselected[i], colsselected[j]);
                    }
                }
                sbf.append("\n");
            }
            stsel = new StringSelection(sbf.toString());
            system = Toolkit.getDefaultToolkit().getSystemClipboard();
            system.setContents(stsel, stsel);
        } else if (e.getActionCommand().compareTo(ACTION_PASTE) == 0) {
            //System.out.println("Trying to Paste");
            if (jTable1.getSelectedRowCount() == 0 || jTable1.getSelectedColumnCount() == 0) {
                Tools.showErr("Zaznacz komórkę, do której wkleić dane.");
                return;
            }
            int startRow = (jTable1.getSelectedRows())[0];
            int startCol = (jTable1.getSelectedColumns())[0];
            try {
                String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
                //System.out.println("String is:" + trstring);
                StringTokenizer st1 = new StringTokenizer(trstring, "\n");
                for (int i = 0; st1.hasMoreTokens(); i++) {
                    rowstring = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(rowstring, "\t", true); //zwraca "/t" na końcu, wtedy uwzględnia puste komórki
                    int j = 0;
                    String prev = null;
                    while (st2.hasMoreTokens()) {
                        value = st2.nextToken();
                        if (value.equals("\t")) {
                            if (prev == null || prev.equals("\t")) {
                                jTable1.setValueAt(null, startRow + i, startCol + j);
                                j++;
                            }
                        } else {
                            if (startRow + i < jTable1.getRowCount() &&
                                    startCol + j < jTable1.getColumnCount()) {
                                jTable1.setValueAt(value, startRow + i, startCol + j);
                            }
                            j++;
                        }
                        prev = value;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
