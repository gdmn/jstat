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
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.*;
import jstat.gui.MyInternalFrame;
import jstat.pages.sheet.PageSheet;
import kalkulator.Macierz;

/**
 * Strona ze wszystkimi zmiennymi w dokumencie wylistowanymi w postaci <code>JCheckBox</code>.
 * @author dmn
 */
public class WizardStepPanel_ZmienneCheckBoxes extends WizardStepPanel {

    private ZmienneCheckboxes zmienneScrollPane;
    private Document doc;

    public ArrayList<JCheckBox> getCheckboxes() {
        return zmienneScrollPane.getCheckboxes();
    }

    static class ZmienneCheckboxes extends JScrollPane {

        private ArrayList<JCheckBox> checkboxes;

        public ZmienneCheckboxes(PageSheet doc) {
            JPanel zmienne = new JPanel(new GridLayout(0, 1));
            checkboxes = new ArrayList<JCheckBox>(doc.getColumnNames().length);
            int checkboxesCounter = 0;
            for (String s : doc.getColumnNames()) {
                JCheckBox cb = new JCheckBox(s, true);
                checkboxes.add(cb);
                zmienne.add(cb);
                checkboxesCounter++;
                cb.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        JCheckBox source = (JCheckBox) e.getSource();
                        WizardStepPanel step = (WizardStepPanel) SwingUtils.findParentWithGivenClass(source, WizardStepPanel.class);
                        ZmienneCheckboxes zmienneCheckboxes = (ZmienneCheckboxes) SwingUtils.findParentWithGivenClass(source, ZmienneCheckboxes.class);
                        int liczbaZaznaczonych = 0;
                        for (JCheckBox i : zmienneCheckboxes.getCheckboxes()) {
                            if (i.isSelected()) {
                                liczbaZaznaczonych++;
                            }
                        }
                        step.setForwardEnabled(liczbaZaznaczonych > 0);
                    }
                    });
            }
            this.setViewportView(zmienne);
        }

        public ArrayList<JCheckBox> getCheckboxes() {
            return checkboxes;
        }
        }

    private static JScrollPane createZmienneCheckboxes(Document doc) {
        PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
        return new ZmienneCheckboxes(ps);
    }

    public WizardStepPanel_ZmienneCheckBoxes(Document doc) {
        super("Zmienne objaśniające");
        this.doc = doc;
        setLayout(new BorderLayout());
        zmienneScrollPane = (ZmienneCheckboxes) createZmienneCheckboxes(doc);
        add(zmienneScrollPane);
    }

    /**
     * Pobiera listę zaznaczonych zmiennych
     * @return nazwy zmiennych
     */
    public ArrayList<String> getNazwyZmiennych() {
        ArrayList<String> nazwyZmiennych = new ArrayList<String>();
        int wybranych = 0;
        for (JCheckBox i : getCheckboxes()) {
            if (i.isSelected()) {
                wybranych++;
                nazwyZmiennych.add(i.getText());
            }
        }
        return nazwyZmiennych;
    }

    /**
     * Tworzy macierz z wybranych zmiennych. Wybiera tylko te kolumny macierzy danych, których zmienne zostały wybrane.
     * @param m wynik: <code>PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(false, null, doc, PageSheet.class);<br>
            Macierz m = ps.getDataAsMacierz();</code>
     * @return macierz zbudowana z zaznaczonych zmiennych
     */
    public Macierz getMacierz(Macierz m) {
        int[] lista = getWybrane();
        return Macierz.pobierzKolumny(m, lista);
    }

    /**
     * Lista wybranych indeksów wybranych zmiennych.
     * @return tablica z indeksami.
     */
    public int[] getWybrane() {
        int wybranych = 0;
        for (JCheckBox i : getCheckboxes()) {
            if (i.isSelected()) {
                wybranych++;
            }
        }
        int[] lista = new int[wybranych];
        int j = 0;
        for (int i = 0; i < getCheckboxes().size(); i++) {
            if (getCheckboxes().get(i).isSelected()) {
                lista[j++] = i;
            }
        }
        return lista;
    }
}
