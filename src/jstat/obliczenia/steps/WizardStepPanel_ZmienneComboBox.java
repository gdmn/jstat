/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.steps;

import dmnutils.document.Document;
import dmnutils.wizard.WizardStepPanel;
import java.awt.BorderLayout;
import java.util.Arrays;
import javax.swing.*;
import jstat.gui.MyInternalFrame;
import jstat.obliczenia.steps.WizardStepPanel_ZmienneComboBoxes.ZmienneComboboxes;
import jstat.pages.sheet.PageSheet;

/**
 * Strona ze wszystkimi zmiennymi w dokumencie wylistowanymi w 1 polu typu <code>JComboBox</code>.
 * @author dmn
 */
public class WizardStepPanel_ZmienneComboBox extends WizardStepPanel {

    //private ZmienneComboBox zmienneScrollPane;
    private ZmienneComboboxes zmienne;

    /* public JComboBox getComboBox() {
        return zmienne.getComboBoxes().get(0);
    } */

    /**
     * Pobiera wybraną zmienną.
     * @return indeks wybranej zmiennej
     */
    public int getWybrana() {
        return zmienne.getComboBoxes().get(0).getSelectedIndex();
    }

    private static JPanel createZmienneComboBox(Document doc) {
        PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
        return new WizardStepPanel_ZmienneComboBoxes.ZmienneComboboxes(ps, (String[]) Arrays.asList("Zmienna").toArray());// ZmienneComboBox(ps);
    }

    public WizardStepPanel_ZmienneComboBox(Document doc) {
        super("Zmienna objaśniana");
        setLayout(new BorderLayout());
        zmienne = (ZmienneComboboxes) createZmienneComboBox(doc);
        add(zmienne);
    }
}
