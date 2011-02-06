/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.steps;

import dmnutils.SwingUtils;
import dmnutils.document.Document;
import dmnutils.wizard.WizardStepPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.SpringLayout;
import jstat.gui.MyInternalFrame;
import jstat.pages.sheet.PageSheet;

/**
 * Strona zawierająca wylistowane role zmiennych i pozwalająca
 * na przypisanie zmiennych do ról.
 * @author dmn
 */
public class WizardStepPanel_ZmienneComboBoxes extends WizardStepPanel {

    private ZmienneComboboxes zmienneScrollPane;
    protected Document doc;
    protected String[] zmienneRole;

    public ArrayList<JComboBox> getComboBoxes() {
        return zmienneScrollPane.getComboBoxes();
    }

    /**
     * Pobiera odwzorowanie zmiennych.
     * @return zwraca tablicę, której indeksami są kolejno
     * ponumerowane role, a wartościami indeksy zmiennych.
     */
    public Integer[] getOdwzorowanie() {
        return zmienneScrollPane.getOdwzorowanie();
    }

    public static class ZmienneComboboxes extends JPanel {

        //private JComboBox comboBox;
        private String[] zmienneRole;
        private ArrayList<JComboBox> comboBoxes;

        public ZmienneComboboxes(PageSheet doc, String... zmienneRole) {
            this.setLayout(new SpringLayout());
            this.zmienneRole = zmienneRole;
            comboBoxes = new ArrayList<JComboBox>(zmienneRole.length);
            JLabel prevL = null;
            JComboBox prevC = null;
            {
                Dimension minimumSize = new Dimension(200, zmienneRole.length*((new JComboBox()).getPreferredSize().height+10)+5*(zmienneRole.length-1));
                // (new JComboBox()).getPreferredSize().height+10  -->
                //                  +10 bo w wyglądzie gtk jakoś źle pobiera wysokość
                setMinimumSize(minimumSize);
                setPreferredSize(minimumSize);
            }
            for (int i = 0; i < zmienneRole.length; i++) {
                String rola = zmienneRole[i];
                JComboBox comboBox = new JComboBox(doc.getColumnNames());
                if (i < doc.getColumnNames().length) {
                    comboBox.setSelectedIndex(i);
                } else {
                    comboBox.setSelectedIndex(0);
                }
                add(comboBox);
                JLabel l = new JLabel(rola);
                add(l);
                if (prevC == null) {
                    dmnutils.SwingUtils.springSetAsRow(null, 0, l, 120, comboBox, 0);
                } else {
                    dmnutils.SwingUtils.springSetAsRow(prevC, 5, l, 120, comboBox, 0);
                }
                prevL = l;
                prevC = comboBox;
                comboBoxes.add(comboBox);

                comboBox.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // sprawdzenie, czy wybrane zmienne się nie powtarzają
                        JComboBox source = (JComboBox) e.getSource();
                        WizardStepPanel step = (WizardStepPanel) SwingUtils.findParentWithGivenClass(source, WizardStepPanel.class);
                        ZmienneComboboxes zmienneComboboxes = (ZmienneComboboxes) SwingUtils.findParentWithGivenClass(source, ZmienneComboboxes.class);
                        boolean konflikt = false;
                        for (JComboBox i : zmienneComboboxes.getComboBoxes()) {
                            for (JComboBox j : zmienneComboboxes.getComboBoxes()) {
                                if ((i != j) && (i.getSelectedIndex() == j.getSelectedIndex())) {
                                    konflikt = true;
                                    break;
                                }
                            }
                            if (konflikt) {
                                break;
                            }
                        }
                        step.setForwardEnabled(!konflikt);
                    }
                });
            }
        }

        public ArrayList<JComboBox> getComboBoxes() {
            return comboBoxes;
        }

        public Integer[] getOdwzorowanie() {
            Integer[] result = new Integer[zmienneRole.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = comboBoxes.get(i).getSelectedIndex();
            }
            return result;
        }
        }

    public static JPanel createZmienneComboBoxes(Document doc, String... zmienneRole) {
        if (zmienneRole == null || zmienneRole.length == 0 || doc == null) {
            return new JPanel();
        } else {
            PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
            return new ZmienneComboboxes(ps, zmienneRole);
        }
    }

    protected WizardStepPanel_ZmienneComboBoxes(Document doc) {
        super("Przypisz zmienne");
        setLayout(new BorderLayout());
        this.doc = doc;
    }

    /**
     * Tworzy krok kreatora z polami do wyboru roli zmiennych.
     * @param doc dokument, dla którego ma być utworzony krok kreatora
     * @param zmienneRole lista, do jakich ról można zmienne przyporządkowywać
     */
    public WizardStepPanel_ZmienneComboBoxes(Document doc, String... zmienneRole) {
        super("Przypisz zmienne");
        setLayout(new BorderLayout());
        this.doc = doc;
        this.zmienneRole = zmienneRole;
        zmienneScrollPane = (ZmienneComboboxes) createZmienneComboBoxes(doc, zmienneRole);
        add(zmienneScrollPane, BorderLayout.NORTH);
    }
}
