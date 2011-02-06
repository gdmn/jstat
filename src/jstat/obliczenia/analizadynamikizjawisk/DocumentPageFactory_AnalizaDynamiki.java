/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.analizadynamikizjawisk;

import dmnutils.document.*;
import dmnutils.wizard.*;
import java.awt.*;
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
public class DocumentPageFactory_AnalizaDynamiki implements DocumentPageFactory<PageNotepadForObliczenia> {

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
        final WizardStepPanel[] steps = new WizardStepPanel[2];
        WizardStepPanel step = null;

        steps[0] = new MyWizardStepPanel1(doc);
        steps[1] = new MyWizardStepPanel2(doc);

        ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(Arrays.asList(steps));
        WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);
        WizardDialog dlg = new WizardDialog(null, "Analiza dynamiki", wizard);
        if (dlg.execute().equals(WizardMainPanel.ACTION_OK)) {
            result = new PageNotepadForObliczenia();
            result.setCaption("Analiza dynamiki");
            PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(false, null, doc, PageSheet.class);
            Macierz m = ps.getDataAsMacierz();
            PageNotepadForObliczenia result2 = (PageNotepadForObliczenia) result;
            result2.beginUpdate();
            result2.printRAW("<table>");
            try {
                Integer[] map = ((WizardStepPanel_ZmienneComboBoxes) steps[0]).getOdwzorowanie();
                int bazowy = ((MyWizardStepPanel1) steps[0]).getWybranyIndeks();
                int sredniaRuchomaOkresy = ((MyWizardStepPanel2) steps[1]).getSredniaRuchomaOkresy();
                Macierz macierz_y = null;

                if (map == null || map.length != 1) {
                    throw new UnsupportedOperationException("DocumentPageFactory_AnalizaDynamiki.create2 - map == null || map.length != 2");
                } else {
                    macierz_y = Macierz.pobierzKolumny(m, map[0]);
                    Macierz chronologiczna = kalkulator.Dynamika.sredniaChronologiczna(macierz_y);
                    result2.printTableRow("średnia chronologiczna", chronologiczna.getValueAt(0, 0)+"");
                    Macierz[] przyrosty = new Macierz[] {
                        kalkulator.Dynamika.przyrostyAbsolutneLancuchowe(macierz_y),
                        kalkulator.Dynamika.przyrostyAbsolutneJednopodstawowe(macierz_y, bazowy),
                        kalkulator.Dynamika.przyrostyWzgledneLancuchowe(macierz_y),
                        kalkulator.Dynamika.przyrostyWzgledneJednopodstawowe(macierz_y, bazowy),
                        kalkulator.Dynamika.indywidualneIndeksyDynamikiLancuchowe(macierz_y),
                        kalkulator.Dynamika.indywidualneIndeksyDynamikiJednopodstawowe(macierz_y, bazowy),
                        kalkulator.Dynamika.indywidualneIndeksyDynamikiLancuchowe_sredniaGeometryczna(macierz_y),
                        kalkulator.Dynamika.sredniaRuchoma(macierz_y, sredniaRuchomaOkresy)
                    };
                    for (int i = 0; i < przyrosty.length; i++) {
                        String s = null;
                        switch (i) {
                            case 0:
                                s = "przyrosty absolutne łańcuchowe";
                                break;
                            case 1:
                                s = "przyrosty absolutne jednopodstawowe";
                                break;
                            case 2:
                                s = "przyrosty względne łańcuchowe";
                                break;
                            case 3:
                                s = "przyrosty względne jednopodstawowe";
                                break;
                            case 4:
                                s = "indywidualne indeksy dynamiki łańcuchowe";
                                break;
                            case 5:
                                s = "indywidualne indeksy dynamiki jednopodstawowe";
                                break;
                            case 6:
                                s = "średnie tempo zmian zjawiska w czasie";
                                break;
                            case 7:
                                s = "średnia ruchoma, k=" + sredniaRuchomaOkresy;
                                break;
                        }
                        result2.printTableRow(s, PageHtmlOutput.macierzToHtmlTable(przyrosty[i])+"");
                    }
                    Macierz macierz_x = Dynamika.macierzT(macierz_y.getLengthY());
                    { // wycięte z DocumentPageFactory_Regresja.java
                        Macierz macierz_a = kalkulator.Regresja.wspolczynnikA(macierz_y, macierz_x);
                        Macierz macierz_b = kalkulator.Regresja.wspolczynnikB(macierz_y, macierz_x);
                        result2.printTableRow("a", macierz_a.getValueAt(0, 0)+"");
                        result2.printTableRow("b", macierz_b.getValueAt(0, 0)+"");
                        Macierz macierz_teoretyczne = kalkulator.Regresja.obliczTeoretyczne(macierz_x, macierz_a, macierz_b);
                        Liczba wspolczynnikR2 = kalkulator.Regresja.wspolczynnikDeterminacjiR2(macierz_y, macierz_teoretyczne);
                        result2.printTableRow("teoretyczne", PageHtmlOutput.macierzToHtmlTable(macierz_teoretyczne)+"");
                        result2.printTableRow("R^2", wspolczynnikR2+"");
                        Liczba wariancjaResztowego = kalkulator.Regresja.wariancjaSkladnikaResztowego(macierz_y, macierz_teoretyczne, new Liczba(2));
                        result2.printTableRow("wariancja skł. resztowego", wariancjaResztowego+"");
                        Liczba odchylenieStandardoweReszt = kalkulator.Regresja.odchylenieStandardoweReszt(macierz_y, macierz_teoretyczne, new Liczba(2));
                        result2.printTableRow("odchylenie standardowe reszt", odchylenieStandardoweReszt+"");
                    } // koniec wycięcia
                }
            } finally {
                result2.printRAW("</table>");
                result2.endUpdate();
            }
        }
        return result;
    }
}

class MyWizardStepPanel1 extends WizardStepPanel_ZmienneComboBoxes {

    private JComboBox indeksBox;

    /**
     * Tworzy krok kreatora z polami do wyboru zmiennej i indeksu bazowego.
     * @param doc dokument, dla którego ma być utworzony krok kreatora
     */
    public MyWizardStepPanel1(Document doc) {
        super(doc, "Obserwacje");
        setTitle("Wybierz zmienną");
        add(utworzStrone(), BorderLayout.CENTER);
    }

    public JPanel utworzStrone() {
        JPanel result = new JPanel(new SpringLayout());
        PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
        int wysokosc = ps.getDataAsMacierz().getLengthY();
        Object[] opcje = new Object[wysokosc];
        for (int i = 0; i < wysokosc; i++) {
            opcje[i] = new Integer(i + 1);
        }

        JLabel l = new JLabel("Indeks bazowy");
        indeksBox = new JComboBox(opcje);
        indeksBox.setSelectedIndex(0);
        result.add(l);
        result.add(indeksBox);
        dmnutils.SwingUtils.springSetAsRow(null, 5, l, 120, indeksBox, 0);

        Dimension minimumSize = new Dimension(200, 1 * (new JComboBox()).getPreferredSize().height + 5 * (1 - 1) + 5);
        result.setMinimumSize(minimumSize);
        result.setPreferredSize(minimumSize);
        return result;
    }

    public int getWybranyIndeks() {
        return indeksBox != null ? indeksBox.getSelectedIndex() : -1;
    }
}

class MyWizardStepPanel2 extends WizardStepPanel {

    private Document doc;
    private JComboBox okresyBox;

    public MyWizardStepPanel2(Document doc) {
        super("Wygładzanie szeregu");
        this.doc = doc;
        setLayout(new BorderLayout());
        add(utworzStrone(), BorderLayout.CENTER);
    }

    public JPanel utworzStrone() {
        JPanel result = new JPanel(new SpringLayout());
        PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
        int iloscObserwacji = ps.getDataAsMacierz().getLengthY();
        this.setForwardEnabled(iloscObserwacji > 3);
        Object[] opcje = new Object[iloscObserwacji - 1 - 3];
        for (int i = 0; i < iloscObserwacji - 1 - 3; i++) {
            opcje[i] = new Integer(i + 3);
        }

        JLabel l = new JLabel("Średnia ruchoma - okresy");
        okresyBox = new JComboBox(opcje);
        okresyBox.setSelectedIndex(0);
        result.add(l);
        result.add(okresyBox);
        dmnutils.SwingUtils.springSetAsRow(null, 5, l, 220, okresyBox, 0);

        return result;
    }

    public int getSredniaRuchomaOkresy() {
        return okresyBox != null ? okresyBox.getSelectedIndex() + 3 : -1;
    }
}
