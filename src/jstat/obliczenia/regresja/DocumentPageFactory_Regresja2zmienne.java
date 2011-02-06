/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.regresja;

import jstat.obliczenia.podstawowestatystyki.zbiorowosci.*;
import dmnutils.document.*;
import dmnutils.wizard.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import jstat.gui.MyInternalFrame;
import jstat.obliczenia.*;
import jstat.obliczenia.steps.*;
import jstat.pages.sheet.PageSheet;
import kalkulator.*;

/**
 *
 * @author dmn
 */
public class DocumentPageFactory_Regresja2zmienne implements DocumentPageFactory<PageNotepadForObliczenia> {

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

    public AbstractDocumentPage<?> create2(final Document doc) {
        /** kolumna ze zmienną xi */
        //final int KOL_XI = 0;
        AbstractDocumentPage<?> result = null;
        final WizardStepPanel[] steps = new WizardStepPanel[1];
        WizardStepPanel step = null;


        steps[0] = new WizardStepPanel_ZmienneComboBoxes(doc, "y (objaśniana)", "x (objaśniająca)");
        steps[0].setTitle("Wybierz \"x\" i \"y\"");

        ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(Arrays.asList(steps));
        WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);
        WizardDialog dlg = new WizardDialog(null, "Regresja", wizard);
        if (dlg.execute().equals(WizardMainPanel.ACTION_OK)) {
            result = new PageNotepadForObliczenia();
            PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(false, null, doc, PageSheet.class);
            Macierz m = ps.getDataAsMacierz();
            PageNotepadForObliczenia result2 = (PageNotepadForObliczenia) result;
            result2.beginUpdate();
            result2.printRAW("<table>");
            try {
                Integer[] map = ((WizardStepPanel_ZmienneComboBoxes) steps[0]).getOdwzorowanie();
                Macierz macierz_x = null,
                        macierz_y = null;

                if (map == null || map.length != 2) {
                    throw new UnsupportedOperationException("DocumentPageFactory_CharakterystykiLiczbowe.create2 - map == null || map.length != 2");
                } else {
                    macierz_x = Macierz.pobierzKolumny(m, map[1]);
                    macierz_y = Macierz.pobierzKolumny(m, map[0]);
                    Liczba kowariancja = kalkulator.Regresja.kowariancja(macierz_y, macierz_x);
                    result2.printTableRow("kowariancja", kowariancja + "");
                    Liczba rxy = kalkulator.Regresja.wspolczynnikKorelacjiLiniowej(macierz_y, macierz_x);
                    result2.printTableRow("rxy", rxy + "");
                    Macierz macierz_a = kalkulator.Regresja.wspolczynnikA(macierz_y, macierz_x);
                    Macierz macierz_b = kalkulator.Regresja.wspolczynnikB(macierz_y, macierz_x);
                    result2.printTableRow("a", macierz_a + "");
                    result2.printTableRow("b", macierz_b + "");
                    Macierz macierz_teoretyczne = kalkulator.Regresja.obliczTeoretyczne(macierz_x, macierz_a, macierz_b);
                    Liczba wspolczynnikR2 = kalkulator.Regresja.wspolczynnikDeterminacjiR2(macierz_y, macierz_teoretyczne);
                    result2.printTableRow("teoretyczne", macierz_teoretyczne + "");
                    result2.printTableRow("R^2", wspolczynnikR2 + "");
                    Liczba wariancjaResztowego = kalkulator.Regresja.wariancjaSkladnikaResztowego(macierz_y, macierz_teoretyczne, new Liczba(2));
                    result2.printTableRow("wariancja skł. resztowego", wariancjaResztowego + "");
                    Liczba odchylenieStandardoweReszt = kalkulator.Regresja.odchylenieStandardoweReszt(macierz_y, macierz_teoretyczne, new Liczba(2));
                    result2.printTableRow("odchylenie standardowe reszt", odchylenieStandardoweReszt + "");
                }
            } finally {
                result2.printRAW("</table>");
                result2.endUpdate();
            }
        }
        return result;
    }
}
