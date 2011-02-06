/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.pages.sheet;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import jstat.plugins.wrappers.Tools;
import kalkulator.Macierz;

/**
 *
 * @author dmn
 */
public class CopyCutPasteMacierz implements ActionListener {
    private String rowstring,  value;
    private Clipboard system;
    private StringSelection stsel;
    private JTable jTable1;
    public static String ACTION_COPY_MACIERZ = "ACTION_COPYMATRIX";
    public static String ACTION_PASTE_MACIERZ = "ACTION_PASTEMATRIX";
    public static String ACTION_CUT_MACIERZ = "ACTION_CUTMATRIX";

    public CopyCutPasteMacierz(JTable myJTable) {
        jTable1 = myJTable;
        system = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo(ACTION_COPY_MACIERZ) == 0 || e.getActionCommand().compareTo(ACTION_CUT_MACIERZ) == 0) {
            boolean cutting = e.getActionCommand().compareTo(ACTION_CUT_MACIERZ) == 0;

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
            sbf.append("{");
            for (int i = 0; i < numrows; i++) {
                sbf.append("{");
                for (int j = 0; j < numcols; j++) {
                    Object b = jTable1.getValueAt(rowsselected[i], colsselected[j]);
                    sbf.append(b == null ? "0" : b);
                    if (j < numcols - 1) {
                        sbf.append("; ");
                    }
                    if (cutting) {
                        jTable1.setValueAt(null, rowsselected[i], colsselected[j]);
                    }
                }
                sbf.append("}");
            }
            sbf.append("}");

            stsel = new StringSelection(sbf.toString());
            system = java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
            system.setContents(stsel, stsel);
        } else if (e.getActionCommand().compareTo(ACTION_PASTE_MACIERZ) == 0) {
            //System.out.println("Trying to Paste");
            if (jTable1.getSelectedRowCount() == 0 || jTable1.getSelectedColumnCount() == 0) {
                Tools.showErr("Zaznacz komórkę, do której wkleić dane.");
                return;
            }
            int startRow = (jTable1.getSelectedRows())[0];
            int startCol = (jTable1.getSelectedColumns())[0];
            try {
                String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
                try {
                    Macierz m = Macierz.valueOf(trstring);
                    for (int i = 0; i < m.getLengthX(); i++) {
                        for (int j = 0; j < m.getLengthY(); j++) {
                                    jTable1.setValueAt(m.getValueAt(i, j), startRow + j, startCol + i);
                        }
                    }
                } catch (kalkulator.InvalidArgumentException ek) {
                    JOptionPane.showMessageDialog(null, "Nieprawidłowe dane w schowku", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
