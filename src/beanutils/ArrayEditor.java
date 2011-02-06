/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author Damian
 */
public class ArrayEditor<T> extends AbstractCellEditor implements TableCellEditor, ActionListener {
    Class<?> kind;

    TableValueSetterInterface tableSetter;
    private int row, col;
    private ArrayList<T> current;

    JButton button;
    JDialog dialog;
    protected static final String EDIT = "edit";

    /** Creates a new instance of ArrayEditor */
    public ArrayEditor(Class<?> kind) {
        super();
        this.kind = kind;
        button = new JButton("...");
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
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
            //colorChooser.setColor(currentColor);
            dialog = new MyEditor();
            dialog.setVisible(true);

            //Make the renderer reappear.
            fireEditingStopped();

        } else { //User pressed dialog's "OK" button.
            //currentColor = colorChooser.getColor();
        }
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    /**
     * Trzeba zwrócić T[]. Mała sztuczka z TIJ4, s. 559
     */
    public Object getCellEditorValue() {
        return convertArrayList(current);
    }

    private Object convertArrayList(ArrayList<T> current) {
        Object a = Array.newInstance(kind, current.size());
        for (int i = 0; i < current.size(); i++) {
            Array.set(a, i, current.get(i));
        }
        return a;
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

        final int len = Array.getLength(value);

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
            current = a;
        } else {
            @SuppressWarnings("unchecked")
            ArrayList<T> a = new ArrayList<T>(Arrays.asList((T[]) value)); //nie działa dla typów prymitywnych
            current = a;
        }

        this.row = row;
        this.col = column;

        return button;
    }

    class MyEditor extends JDialog implements ActionListener {
        private JTextArea textArea = new JTextArea();
        private JButton bOk = new JButton("OK"), bCancel = new JButton("Anuluj");
        public MyEditor() {
            super((JFrame)null);
            setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
            setTitle("Edytor "+kind.getSimpleName() + "[]");
            setLayout(new BorderLayout());
            add(new JScrollPane(textArea), BorderLayout.CENTER);
            JPanel btns = new JPanel(new FlowLayout());
            bOk.addActionListener(this);
            bCancel.addActionListener(this);
            bOk.setPreferredSize(bCancel.getPreferredSize());
            btns.add(bOk);
            btns.add(bCancel);
            add(btns, BorderLayout.SOUTH);

            for (T i : current) {
                textArea.append(i+"\n");
            }
            setMinimumSize(new Dimension((int)(bOk.getPreferredSize().width*2.5),
                    //(int)(this.getPreferredSize().height))
                    200
                    ));
            pack();
            setLocationRelativeTo(null);
        }

        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(bOk)) {
                String[] splitted = textArea.getText().split("\n");
                //System.out.println(textArea.getText()+"#"+splitted.length);
                ArrayList<T> current2 = new ArrayList<T>(splitted.length);
                String s = null;
                ValueOfParser<T> parser = new ValueOfParser<T>(kind);
                boolean wszystkook = false;
                int wiersz = 0;
                try {
                    if (!textArea.getText().equals("")) {
                        for (String tmp : splitted) {
                            s = tmp;
                            current2.add(parser.valueOf(s));
                            wiersz++;
                        }
                    }
                    wszystkook = true;
                } catch (ClassCastException ex) {
                    System.err.println(ex.getClass().getSimpleName()+": "+kind.getSimpleName()+".valueOf("+s+")");
                } catch (IllegalArgumentException ex) {
                    System.err.println(ex.getClass().getSimpleName()+": "+kind.getSimpleName()+".valueOf("+s+")");
                } catch (InvocationTargetException ex) {
                    if (ex.getCause() != null) { //java.beans.PropertyVetoException
                        System.err.println(ex.getCause().getClass().getSimpleName()+": "+ex.getCause().getMessage());
                    } else {
                        System.err.println(ex.getClass().getSimpleName()+": "+kind.getSimpleName()+".valueOf("+s+")");
                    }
                } catch (IllegalAccessException ex) {
                    System.err.println(ex.getClass().getSimpleName()+": "+kind.getSimpleName()+".valueOf("+s+")");
                }
                if (wszystkook) {
                    if (!tableSetter.tryToSetValueAt(convertArrayList(current2), row, col)) {
                        /* dzięki temu wyjątkowi, jeśli nastąpi np. PropertyVetoException,
                         * to ramka komórki robi się czerwona i nie pozwala na zakończenie
                         * edycji
                         */
                        wszystkook = false;
                        JOptionPane.showMessageDialog(this, "Wystąpiły błędy podczas przypisywania wartości.\nZajrzyj do dziennika, aby uzyskać więcej informacji.", "Błąd", JOptionPane.ERROR_MESSAGE);
                        textArea.requestFocusInWindow();
                    } else {
                        current = current2;
                        setVisible(false);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Wystąpiły błędy podczas przetwarzania "+(wiersz+1)+" wiersza.\nZajrzyj do dziennika, aby uzyskać więcej informacji.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    textArea.requestFocusInWindow();
                }
            } else {
                setVisible(false);
            }
        }
    }

}
