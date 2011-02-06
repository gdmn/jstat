/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */
package jstat.pages.sheet;

import com.thoughtworks.xstream.XStream;
import dmnutils.document.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import jstat.gui.MyAction;
import kalkulator.*;
import dmnutils.SwingUtils;

/**
 * Define the look/content for a cell in the row header
 * In this instance uses the JTables header properties
 **/
class RowHeaderRenderer extends JLabel implements ListCellRenderer {

    /**
     * Constructor creates all cells the same
     * To change look for individual cells put code in
     * getListCellRendererComponent method
     **/
    RowHeaderRenderer(JTable table) {
        JTableHeader header = table.getTableHeader();
        setOpaque(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(CENTER);
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
    }

    /**
     * Returns the JLabel after setting the text of the cell
     **/
    public Component getListCellRendererComponent(JList list,
            Object value, int index, boolean isSelected, boolean cellHasFocus) {

        setText((value == null) ? "" : value.toString());
        return this;
    }
}

public class PageSheet extends AbstractDocumentPage<PageSheet> implements Externalizable, ActionListener {

    private JLabel statusLabel = new JLabel("<html><br><br>");
    private JTable table;
    private String[] columnNames;
    private Class[] columnClasses;
    private Object[][] data = null;
    private JPopupMenu popup;

    /**
     *
     * @return nazwy zmiennych
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /**
     * Pobiera dane arkusza.
     * @return zawartość tabeli arkusza.
     */
    public Object[][] getData() {
        return data;
    }

    /**
     * Pobiera dane arkusza.
     * @return zawartość tabeli arkusza skonwertowana na macierz.
     */
    public Macierz getDataAsMacierz() {
        Macierz result;
        WyrazenieInfix[][] minfix = new WyrazenieInfix[data[0].length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                String s = data[i][j] == null ? "" : data[i][j].toString();
                minfix[j][i] = s.equals("") ? WyrazenieInfix.valueOf("0") : WyrazenieInfix.valueOf(data[i][j].toString());
            }
        }
        result = (new Macierz(minfix));//.getValue();
        return result;
    }

    class MyTableModel extends AbstractTableModel {

        public int getColumnCount() {
            return columnClasses == null ? 0 : columnClasses.length;
        }

        public int getRowCount() {
            return getData() == null ? 0 : getData().length;
        }

        public String getColumnName(int col) {
            if (columnNames != null && col < columnNames.length && columnNames[col] != null) {
                return "<html>" + super.getColumnName(col) + "<br>" + columnNames[col];
            }
            return super.getColumnName(col);
        }

        public Object getValueAt(int row, int col) {
            return getData()[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            //return getValueAt(0, c).getClass();
            return columnClasses[c];
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            //--return !(col<2);
            return true;
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    public void updateLabel() {
        int currentRow[], currentCol[];
        currentRow = table.getSelectedRows();
        currentCol = table.getSelectedColumns();
        StringBuilder s = new StringBuilder("<html>");
        s.append("Kolumny: ");
        if (currentCol != null) {
            for (int i : currentCol) {
                s.append(i + ", ");
            }
        }
        s.append("<br>");
        s.append("Wiersze: ");
        if (currentRow != null) {
            for (int i : currentRow) {
                s.append(i + ", ");
            }
        }
        statusLabel.setText(s.toString());
    }

    /**
     * Uwaga: tylko dla potrzeb eksternalizacji. NIE UŻYWAĆ!
     */
    public PageSheet() {
    }

    public PageSheet(Class[] columnClasses, String[] columnNames, int rowCount) {
        this.columnClasses = columnClasses;
        this.columnNames = columnNames;
        data = new Object[rowCount][columnClasses.length];
        StringBuilder s = new StringBuilder("Arkusz danych");
        if (getData() != null) {
            s.append(" [");
            //s.append(data.length); s.append('x');
            s.append(getData()[0].length);
            s.append(" zm.");
            s.append(']');
        }
        setCaption(s.toString());
        constructorHelper();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(columnClasses);
        out.writeObject(columnNames);
        out.writeObject(getData());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        columnClasses = (Class[]) in.readObject();
        // TODO: usunąć w finalnej wersji, napisane ponieważ wcześniej stworzone
        // dokumenty mają ustawione jako klasę Object.
//        for (int i = 0; i < columnClasses.length; i++) {
//            columnClasses[i] = Liczba.class;
//        }

//        for (Class c : columnClasses) {
//            c = Liczba.class;
//        }
        columnNames = (String[]) in.readObject();
        data = (Object[][]) in.readObject();
//        for (int i = 0; i < data.length; i++) {
//            for (int j = 0; j < data[i].length; j++) {
//                data[i][j] = new Liczba((String)data[i][j]);}
//        }

        constructorHelper();
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    private void constructorHelper() {
        //tworzenie tabeli
        TableModel tableModel = (new MyTableModel());
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(300, 200));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        //zaznaczanie:
        table.setCellSelectionEnabled(true);
        ListSelectionModel rowSM = table.getSelectionModel();
        ListSelectionModel colSM = table.getColumnModel().getSelectionModel();
        rowSM.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION); //psuje się dla MULTIPLE_INTERVAL_SELECTION, zaznacza komórki na "skrzyżowaniu"
        colSM.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        {
            ListSelectionListener l = new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    //ListSelectionModel lsm = (ListSelectionModel)e.getSource();
                    updateLabel();
                }
                };
            rowSM.addListSelectionListener(l);
            colSM.addListSelectionListener(l);
        }

        //kolumna z numerami wierszy:
        ListModel listModel = new AbstractListModel() {

            public int getSize() {
                return getData().length;
            }

            public Object getElementAt(int index) {
                return String.valueOf(index + 1);
            }
            };
        JList rowHeader = new JList(listModel);
        rowHeader.setFixedCellWidth(40);
        rowHeader.setFixedCellHeight(table.getRowHeight());
        rowHeader.setCellRenderer(new RowHeaderRenderer(table));
        rowHeader.setBackground(table.getTableHeader().getBackground());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setRowHeaderView(rowHeader); // Adds row-list left of the table
        scroll.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));

        setLayout(new BorderLayout());
        add(scroll, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        popup = new JPopupMenu();
        JMenuItem menuItem;
        ExcelAdapter myAd = new ExcelAdapter(table);
        menuItem = popup.add(new JMenuItem(new MyAction(myAd, ExcelAdapter.ACTION_COPY, "Kopiuj", null, null, SwingUtils.createNavigationIcon("edit-copy"))));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menuItem = popup.add(new JMenuItem(new MyAction(myAd, ExcelAdapter.ACTION_CUT, "Wytnij", null, null, SwingUtils.createNavigationIcon("edit-cut"))));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menuItem = popup.add(new JMenuItem(new MyAction(myAd, ExcelAdapter.ACTION_PASTE, "Wklej", null, null, SwingUtils.createNavigationIcon("edit-paste"))));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));

        popup.addSeparator();
        CopyCutPasteMacierz myCCP = new CopyCutPasteMacierz(table);
        menuItem = popup.add(new JMenuItem(new MyAction(myCCP, CopyCutPasteMacierz.ACTION_COPY_MACIERZ, "Kopiuj jako macierz", null, null, SwingUtils.createNavigationIcon("edit-copy"))));
        menuItem = popup.add(new JMenuItem(new MyAction(myCCP, CopyCutPasteMacierz.ACTION_CUT_MACIERZ, "Wytnij jako macierz", null, null, SwingUtils.createNavigationIcon("edit-cut"))));
        menuItem = popup.add(new JMenuItem(new MyAction(myCCP, CopyCutPasteMacierz.ACTION_PASTE_MACIERZ, "Wklej macierz", null, null, SwingUtils.createNavigationIcon("edit-paste"))));
        //Add listener to components that can bring up popup menus.
        table.addMouseListener(new dmnutils.PopupListener(popup));

        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                PageSheet.this.changed();
            }
            });



    }
    final static String ACTION_WKLEJ = MyAction.createNewId();

    public void actionPerformed(ActionEvent e) {
        final String ac = e.getActionCommand();
        if (ac.equals(ACTION_WKLEJ)) {
            System.out.println("wklejjjjjjjj");
        } else {
            throw new UnsupportedOperationException("Not supported yet: " + e.toString());
        }
    }

    public JTable getTable() {
        return table;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        //(new jstat.SwingUtils(false, false)).setLookFeel(null);
        PageSheet sheet = PageSheet.factory.create(null);
        //BeanSaverLoader.saveBean("/home/dmn/Desktop/t.jstat", sheet);
        XStream xstream = new XStream();
        String s = xstream.toXML(sheet);
        System.out.println(s);
        PageSheet object2 = (PageSheet) (new XStream()).fromXML(s);

        if (sheet != null) {
            dmnutils.SwingUtils.run(sheet);
        } else {
            System.exit(1);
        }
    }
    /**
     * Tworzenie nowego arkusza w dokumencie
     */
    public static DocumentPageFactory<PageSheet> factory = new DocumentPageFactory_PageSheet();
}
