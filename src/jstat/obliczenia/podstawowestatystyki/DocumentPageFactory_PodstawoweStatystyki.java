/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.podstawowestatystyki;

import dmnutils.document.*;
import dmnutils.wizard.*;
import java.util.*;
import javax.swing.*;
import jstat.gui.MyInternalFrame;
import jstat.obliczenia.*;
import jstat.obliczenia.steps.*;
import jstat.pages.html.PageHtmlOutput;
import jstat.pages.sheet.PageSheet;
import kalkulator.*;

/**
 *
 * @author dmn
 */
public class DocumentPageFactory_PodstawoweStatystyki implements DocumentPageFactory<PageNotepadForObliczenia> {

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
        WizardStepPanel[] steps = new WizardStepPanel[1];
        WizardStepPanel step = null;
        JLabel l;
        steps[0] = new WizardStepPanel_ZmienneCheckBoxes(doc);
        //step.setFirstFocused(zmienne);
        ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(Arrays.asList(steps));
        WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);
        WizardDialog dlg = new WizardDialog(null, "Nowy arkusz", wizard);
        if (dlg.execute().equals(WizardMainPanel.ACTION_OK)) {
            result = new PageNotepadForObliczenia();
            result.setCaption("Podstawowe statystyki");
            PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(false, null, doc, PageSheet.class);
            Macierz m = ps.getDataAsMacierz();
            PageNotepadForObliczenia result2 = (PageNotepadForObliczenia) result;
            result2.beginUpdate();
            result2.printRAW("<table>");
            try {

                /** tablica z nazwami zmiennych */
                ArrayList<String> nazwyZmiennych = ((WizardStepPanel_ZmienneCheckBoxes) steps[0]).getNazwyZmiennych();
                result2.printTableRow("Nazwy zmiennych",nazwyZmiennych.toString()+"");

                /** macierz */
                Macierz m2 = ((WizardStepPanel_ZmienneCheckBoxes) steps[0]).getMacierz(m);
                result2.printTableRow("macierz", PageHtmlOutput.macierzToHtmlTable(m2).toString()+"");

                /** wyniki */
                result2.printTableRow("sumy:", PageHtmlOutput.macierzToHtmlTable(kalkulator.PodstawoweStatystyki.suma(m2)).toString()+"");
                result2.printTableRow("średnia:", PageHtmlOutput.macierzToHtmlTable(kalkulator.PodstawoweStatystyki.srednia(m2)).toString()+"");
            } finally {
                result2.printRAW("</table>");
                result2.endUpdate();
            }
        }
        return result;
    }
}
