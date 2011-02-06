/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.functioneditor;

import dmnutils.SwingUtils;
import java.awt.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import kalkulator.*;

/**
 *
 * @author dmn
 */
public class FunctionArgumentsPanel extends JPanel {
    private String funkcja;
    private JTextField[] textFields;
    private JTextField wyrazenieTF,  wynikTF;
    private FunctionEditor parentFunctionEditor;
    private ElementWyrazenia wynik;

    /**
     * Tworzy panel z polami do wprowadzania argumentów,
     * wyrażeniem i wynikiem.
     * @param parentFunctionEditor może być null. Jeśli nie jest null,
     * to deaktywuje przyciski "wklej wyrażenie" i "wklej wynik",
     * gdy jest taka potrzeba.
     */
    public FunctionArgumentsPanel(FunctionEditor parentFunctionEditor) {
        setLayout(new SpringLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));
        wyrazenieTF = new JTextField();
        wynikTF = new JTextField();
        Dimension minsize = new Dimension(5, 10 + 5 * 3 + 3 * (int) wynikTF.getPreferredSize().getHeight());
        setMinimumSize(minsize);
        setPreferredSize(minsize);
        for (JTextField t : Arrays.asList(wyrazenieTF, wynikTF)) {
            t.setEditable(false);
        }
        this.parentFunctionEditor = parentFunctionEditor;
    }

    public void editFunction(Funkcja f) {
        removeAll();
        wynikTF.setText("");
        wyrazenieTF.setText("");
        funkcja = f.toString();
        JComponent prev = null;
        final int liczbaPol = f.getArgumentsCount();
        textFields = new JTextField[liczbaPol];
        JLabel l;
        JTextField t;
        for (int i = 0; i < liczbaPol; i++) {
            l = new JLabel("arg" + i);
            t = new JTextField();
            add(l);
            add(t);
            textFields[i] = t;
            SwingUtils.springSetAsRow(prev, 5, l, 100, t, 0);
            prev = t;
            t.getDocument().addDocumentListener(myDocumentListener);
        }
        Dimension minsize = new Dimension(5, 10 + 5 * liczbaPol + liczbaPol * (int) wynikTF.getPreferredSize().getHeight());

        l = new JLabel("Wyrażenie");
        add(l);
        add(wyrazenieTF);
        SwingUtils.springSetAsRow(prev, 5, l, 100, wyrazenieTF, 0);
        l = new JLabel("Wynik");
        add(l);
        add(wynikTF);
        SwingUtils.springSetAsRow(wyrazenieTF, 5, l, 100, wynikTF, 0);
        minsize.height += 5 * 2 + 2 * (int) wynikTF.getPreferredSize().getHeight();
        myDocumentListener.insertUpdate(null); // jak zeroargumentowa funkcja to wypełnia pola

        setMinimumSize(minsize);
        setPreferredSize(minsize);
        setMaximumSize(minsize);
        setSize(minsize);
        System.out.println("" + minsize);
        revalidate();
        repaint();

        myDocumentListener.insertUpdate(null);
    }

    public String getWynikString() {
        return wynikTF.getText();
    }

    public ElementWyrazenia getWynik() {
        return wynik;
    }



    public String getWyrazenieString() {
        return wyrazenieTF.getText();
    }

    DocumentListener myDocumentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            wynikTF.setText("");
            wyrazenieTF.setText("");
            wynik = null;
            boolean pusta = false;
            StringBuilder w = new StringBuilder();
            w.append(funkcja);
            if (textFields.length > 0) {
                w.append("(");
                for (int i = 0; i < textFields.length; i++) {
                    if (i > 0) {
                        w.append("; ");
                    }
                    String s = textFields[i].getText();
                    if (s == null || s.equals("")) {
                        pusta = true;
                        break;
                    }
                    w.append(textFields[i].getText());
                }
                w.append(")");
            }
            if (!pusta) {
                wyrazenieTF.setText(w.toString());
                try {
                    ElementWyrazenia ew = (new WyrazeniePostfix(WyrazenieInfix.valueOf(w.toString(), Funkcja.funkcjaX))).getValue();
                    String result = "" + ew;
                    wynik = ew;
                    wynikTF.setText(result);
                    wynikTF.setBackground(wyrazenieTF.getBackground());
                    wynikTF.setForeground(wyrazenieTF.getForeground());
                    if (parentFunctionEditor != null) {
                        parentFunctionEditor.setButtonsEnabled(true);
                    }
                } catch (Exception ex) {
                    wynikTF.setText(ex.toString());
                    wynikTF.setOpaque(true);
                    //wynik.setBackground(Color.RED);
                    wynikTF.setForeground(Color.RED);
                    if (parentFunctionEditor != null) {
                        parentFunctionEditor.setButtonsEnabled(false);
                    }
                }
            } else if (parentFunctionEditor != null) {
                parentFunctionEditor.setButtonsEnabled(false);
            }
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            insertUpdate(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            insertUpdate(e);
        }
    };
}
