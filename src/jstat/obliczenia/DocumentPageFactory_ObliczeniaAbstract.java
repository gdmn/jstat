/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia;

import dmnutils.document.*;
import dmnutils.wizard.*;
import java.util.*;
import java.util.ArrayList;
import javax.swing.*;
import jstat.gui.MyInternalFrame;
import jstat.obliczenia.steps.*;
import jstat.pages.sheet.PageSheet;
import kalkulator.*;

/**
 *
 * @author dmn
 */
public class DocumentPageFactory_ObliczeniaAbstract implements DocumentPageFactory<PageNotepadForObliczenia> {

    /**
     * Sprawdza, czy zaznaczono odpowiednią stronę w dokumencie typu
     * <code>PageSheet</code> i przekazuje sterowanie do <code>create2(Document doc)</code>
     * @param doc
     * @return
     */
    public PageNotepadForObliczenia create(Document doc) {
        PageNotepadForObliczenia result;
        PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
        result = ps == null ? null : (PageNotepadForObliczenia) create2(doc);
        return result;
    }

    public AbstractDocumentPage<?> create2(Document doc) {
        AbstractDocumentPage<?> result = null;
        WizardStepPanel[] steps = new WizardStepPanel[3];
        WizardStepPanel step = null;
        JLabel l;
        steps[0] = new WizardStepPanel_ZmienneComboBox(doc);
        steps[1] = new WizardStepPanel_ZmienneComboBoxes(doc, (String[]) Arrays.asList("a", "b").toArray());
        steps[2] = new WizardStepPanel_ZmienneCheckBoxes(doc);
        //step.setFirstFocused(zmienne);
        ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(Arrays.asList(steps));
        WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);
        WizardDialog dlg = new WizardDialog(null, "Nowy arkusz", wizard);
        if (dlg.execute().equals(WizardMainPanel.ACTION_OK)) {
            result = new PageNotepadForObliczenia();
            result.setCaption("ObliczeniaAbstract");
            PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(false, null, doc, PageSheet.class);
            Macierz m = ps.getDataAsMacierz();
            PageNotepadForObliczenia result2 = (PageNotepadForObliczenia) result;
            result2.beginUpdate();
            result2.printRAW("<table>");
            try {
                result2.printTableRow("wybrane",ps.getColumnNames()[((WizardStepPanel_ZmienneComboBox) steps[0]).getWybrana()]+"");

                /** odwzorowanie */
                Integer[] map = ((WizardStepPanel_ZmienneComboBoxes) steps[1]).getOdwzorowanie();
                result2.printTableRow("odwzorowanie",Arrays.deepToString(map)+"");

                /** tablica z nazwami zmiennych */
                ArrayList<String> nazwyZmiennych = ((WizardStepPanel_ZmienneCheckBoxes) steps[2]).getNazwyZmiennych();
                result2.printTableRow("nazwyZmiennych",nazwyZmiennych.toString()+"");

                /** macierz */
                Macierz m2 = ((WizardStepPanel_ZmienneCheckBoxes) steps[2]).getMacierz(m);
                result2.printTableRow("macierz",m2.toString()+"");

            } finally {
                result2.printRAW("</table>");
                result2.endUpdate();
            }
        }
        return result;
    }
}
