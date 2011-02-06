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
import dmnutils.browser.DisplayPane;
import dmnutils.document.AbstractDocumentPage;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import jstat.gui.Help;
import jstat.gui.Main;
import jstat.gui.MyInternalFrame;
import jstat.pages.bsh.PageBeanShell;
import jstat.pages.calculator.PageCalculator;
import jstat.pages.graph.PageGraph;
import jstat.pages.notepad.PageNotepad;
import jstat.pages.sheet.PageSheet;
import kalkulator.Funkcja;
import kalkulator.Funkcja.Functions;
import kalkulator.Liczba;

/**
 *
 * @author dmn
 */
public class FunctionEditor extends JPanel implements ActionListener {
    private JButton btn_zamknij,  btn_wklejwyrazenie,  btn_wklejwynik;
    private DisplayPane pane;
    private FunctionArgumentsPanel editorPanel;

    public FunctionEditor() {
        btn_zamknij = new JButton("Zamknij", dmnutils.SwingUtils.createNavigationIcon("dialog-close"));
        btn_wklejwynik = new JButton("Wstaw wynik", dmnutils.SwingUtils.createNavigationIcon("dialog-ok"));
        btn_wklejwyrazenie = new JButton("Wstaw wyrażenie", dmnutils.SwingUtils.createNavigationIcon("dialog-yes"));
        setLayout(new BorderLayout());
        JPanel btnpanel = new JPanel();
        for (JButton b : Arrays.asList(btn_wklejwynik, btn_wklejwyrazenie)) {
            b.setEnabled(false);
        }
        for (JButton b : Arrays.asList(btn_zamknij, btn_wklejwynik, btn_wklejwyrazenie)) {
            b.addActionListener(this);
            btnpanel.add(b);
        }
        add(btnpanel, BorderLayout.SOUTH);
        JList list = new JList(new AbstractListModel() {
            @Override
            public int getSize() {
                return Funkcja.Functions.values().length;
            }

            @Override
            public Object getElementAt(int index) {
                Functions[] f = Funkcja.Functions.values();
                return Arrays.asList(f).get(index).toString();
            }
        });
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
                if (e.getSource() instanceof JList) {
                    JList l = (JList) e.getSource();
                    Funkcja f = new Funkcja((String) l.getModel().getElementAt(l.getSelectedIndex()));
                    System.out.println("" + l.getSelectedIndex() + " " + f.toString());
                    editorPanel.editFunction(f);
                    try {
                        URL www = Help.getAddr("funkcje.html#" + l.getModel().getElementAt(l.getSelectedIndex()).toString());
                        pane.navigate(www);
                    //pane.navigate(www);
                    } catch (FileNotFoundException ex) {
                        StringWriter stringWriter = new StringWriter();
                        ex.printStackTrace(new PrintWriter(stringWriter));
                        pane.setText("<html><pre>" + stringWriter.toString() + "</pre>");
                    //Logger.getLogger(FunctionEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

        pane = new DisplayPane();
        pane.setText("Wybierz funkcję z listy po lewej stronie, aby wyświetlić jej opis oraz pola do wprowadzenia argumentów.");
        editorPanel = new FunctionArgumentsPanel(this);
        JScrollPane scrollPane = new JScrollPane(pane);
        JScrollPane scrollPane2 = new JScrollPane(editorPanel);
        scrollPane.setBorder(null);
        scrollPane2.setBorder(null);
        scrollPane2.setPreferredSize(editorPanel.getPreferredSize());
        scrollPane2.setMinimumSize(editorPanel.getPreferredSize());
        JSplitPane eastincenterpanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scrollPane, scrollPane2);
        setJSplitPaneProperties(eastincenterpanel);
        eastincenterpanel.setResizeWeight(1.0);

        scrollPane = new JScrollPane(list);
        scrollPane.setBorder(null);
        JSplitPane centerpanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scrollPane, eastincenterpanel);
        centerpanel.setDividerLocation(150);
        setJSplitPaneProperties(centerpanel);
        add(centerpanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(600, 400));
    }

    /**
     * Ustawia aktywację/dezaktywację przycisków
     * "wstaw wynik" i "wstaw wyrażenie".
     * @param e
     */
    public void setButtonsEnabled(boolean e) {
        if (Main.getMain() != null) {
            boolean mod1 = true, mod2 = true;
            mod1 = !true;
            mod2 = !true;
            MyInternalFrame frame = Main.getMain().getActiveFrame(false);
            AbstractDocumentPage page = (frame == null) ? null : frame.getActiveDocumentPage(false, null);
            if (page != null) {
                if (page instanceof PageSheet) {
                    mod1 = true;
                } else if (page instanceof PageBeanShell ||
                        page instanceof PageCalculator ||
                        page instanceof PageGraph ||
                        page instanceof PageNotepad) {
                    mod1 = true;
                    mod2 = true;
                }
            }
            btn_wklejwynik.setEnabled(e && mod1);
            btn_wklejwyrazenie.setEnabled(!editorPanel.getWyrazenieString().equals("") && !(page instanceof PageSheet));//page instanceof PageGraph || (e && mod2)); //nie zwraca uwagi na e, bo czasami może nie istnieć wartość dla x=0 a jak się wstawia funkcję do wykresu, to przecież x się zmienia.

        }
    }

    private void setJSplitPaneProperties(JSplitPane p) {
        p.setOneTouchExpandable(false);
        p.setContinuousLayout(true);
    }

    public static void showDialog(java.awt.Frame parent) {
        JDialog frame = new JDialog(parent, "Edytor funkcji");
        frame.setModal(false);
        final FunctionEditor fe = new FunctionEditor();
        frame.add(fe);
        //frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                fe.actionPerformed(new ActionEvent(fe.btn_zamknij, 0, null));
            //wizardPanel.setValue(WizardMainPanel.ACTION_CANCEL);
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_zamknij) {
            SwingUtils.findParentWithGivenClass(this, JDialog.class).setVisible(false);
        } else if (e.getSource() == btn_wklejwynik || e.getSource() == btn_wklejwyrazenie) {
            String doWklejenia = null;
            if (e.getSource() == btn_wklejwynik) {
                doWklejenia = editorPanel.getWynikString();
            }
            if (e.getSource() == btn_wklejwyrazenie) {
                doWklejenia = editorPanel.getWyrazenieString();
            }
            MyInternalFrame frame = Main.getMain().getActiveFrame(false);
            AbstractDocumentPage page = (frame == null) ? null : frame.getActiveDocumentPage(false, null);
            if (page != null) {
                JTextComponent textComponent = null;
                if (page instanceof PageNotepad) {
                    PageNotepad p = (PageNotepad) page;
                    textComponent = p.getTextArea();
                } else if (page instanceof PageCalculator) {
                    PageCalculator p = (PageCalculator) page;
                    textComponent = p.getTextInput();
                } else if (page instanceof PageGraph) {
                    PageGraph p = (PageGraph) page;
                    textComponent = p.getTextInput();
                } else if (page instanceof PageBeanShell) {
                    PageBeanShell p = (PageBeanShell) page;
                    textComponent = p.getTextInput();
                    if (e.getSource() == btn_wklejwynik) {
                        doWklejenia = "new " + editorPanel.getWynik().getClass().getSimpleName() + "(\"" + editorPanel.getWynikString() + "\")";
                    }
                    if (e.getSource() == btn_wklejwyrazenie) {
                        doWklejenia = "calc(\"" + editorPanel.getWyrazenieString() + "\")";
                    }
                }
                if (textComponent != null) {
                    try {
                        textComponent.getDocument().remove(textComponent.getSelectionStart(), textComponent.getSelectionEnd() - textComponent.getSelectionStart());
                        textComponent.getDocument().insertString(textComponent.getSelectionEnd(), doWklejenia, null);
                    } catch (BadLocationException ex) {
                        Logger.getLogger(FunctionEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (page instanceof PageSheet) {
                    PageSheet p = (PageSheet) page;
                    JTable table = p.getTable();
                    table.getCellEditor().stopCellEditing(); // inaczej nie wstawia się do aktualnie edytowanej

                    if (table.getSelectedColumnCount() > 0 && table.getSelectedRowCount() > 0) {
                        Liczba l = new Liczba(doWklejenia);
                        for (int i = 0; i < table.getSelectedColumnCount(); i++) {
                            for (int j = 0; j < table.getSelectedRowCount(); j++) {
                                int r = table.getSelectedRows()[j];
                                int c = table.getSelectedColumns()[i];
                                //System.out.println("wstaw " + l + " w " + r + "x" + c);
                                table.setValueAt(l, r, c);
                            }
                        }
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public static void main(String[] args) {
        dmnutils.SwingUtils.classForImages = jstat.EmptyClass.class;
        final FunctionEditor h = new FunctionEditor();
        if (h != null) {
            javax.swing.SwingUtilities.invokeLater(new
                  Runnable() {
            public void run() {
                    dmnutils.SwingUtils.run(h);
                }
            });
        } else {
            System.exit(1);
        }
    }
}
