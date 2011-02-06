/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.podstawowestatystyki.zbiorowosci;

import dmnutils.SwingUtils;
import dmnutils.document.*;
import dmnutils.wizard.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
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
public class DocumentPageFactory_CharakterystykiLiczbowe implements DocumentPageFactory<PageNotepadForObliczenia> {

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

        steps[1] = new WizardStepPanel_ZmienneCheckBoxes(doc);

        steps[0] = new WizardStepPanel_TypSzeregu(doc);

        steps[0].setTitle("Wybierz zmienną \"xi\"");
        steps[0].addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {
//                System.out.println("steps1 focusgained");
            }

            public void focusLost(FocusEvent e) {
//                System.out.println("steps1 focuslost");
            }
        });
        steps[1].setTitle("Wybierz zmienne \"ni\"");

        steps[1].addFocusListener(new FocusAdapter() {

            /** unieaktywnia JCheckBox dla zmiennej, która została wybrana w poprzednim kroku */
            public void focusGained(FocusEvent e) {
                WizardStepPanel_TypSzeregu prev = (WizardStepPanel_TypSzeregu) steps[0];
                WizardStepPanel_ZmienneCheckBoxes now = (WizardStepPanel_ZmienneCheckBoxes) steps[1];
                Integer[] map = (prev).getOdwzorowanie();
                for (int i = 0; i < now.getCheckboxes().size(); i++) {
                    JCheckBox j = now.getCheckboxes().get(i);
                    if (map == null || map.length < 1) {
                        j.setEnabled(true);
                    } else {
                        for (int k = 0; k < map.length; k++) {
                            if (map[k] != i) {
                                j.setEnabled(true);
                            } else {
                                j.setEnabled(false);
                                j.setSelected(false);
                                break;
                            }
                        }
                    }
                }
            }
        });

        ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(Arrays.asList(steps));
        WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);
        WizardDialog dlg = new WizardDialog(null, "Charakterystyki liczbowe struktury zbiorowości", wizard);
        if (dlg.execute().equals(WizardMainPanel.ACTION_OK)) {
            //TODO: sprawdzić, czy przedziały są po kolei (sortować?), szereg szczegółowy sortować

            result = new PageNotepadForObliczenia();
            result.setCaption("Charakterystyki liczbowe");
            PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(false, null, doc, PageSheet.class);
            Macierz m = ps.getDataAsMacierz();
            PageNotepadForObliczenia result2 = (PageNotepadForObliczenia) result;
            result2.beginUpdate();
            result2.printRAW("<table>");
            try {

                //result2.printTableRowHead("Dane", 2);
                ArrayList<String> nazwyZmiennych = null;
                Macierz macierz_ni = null;
                Macierz macierz_xi = null, macierz_xi_poczatek = null, macierz_xi_koniec = null;
                Integer[] map = ((WizardStepPanel_TypSzeregu) steps[0]).getOdwzorowanie();
                int typszeregu = 0;

                if (map == null || map.length == 0) { // szereg szczegółowy
                    typszeregu = 0;
                    // tworzymy macierz ni z samych 1
                    macierz_ni = new Macierz(1, m.getLengthY());
                    for (int i = 0; i < macierz_ni.getLengthY(); i++) {
                        macierz_ni.set(0, i, new Liczba(new BigDecimal(1)));
                    }
                    //result2.print("ni: " + macierz_ni.toString());

                    nazwyZmiennych = ((WizardStepPanel_ZmienneCheckBoxes) steps[1]).getNazwyZmiennych();
                    macierz_xi = ((WizardStepPanel_ZmienneCheckBoxes) steps[1]).getMacierz(m);
                    //result2.printTableRow("xi", nazwyZmiennych.toString() + ", " + macierz_xi.toString() + "");
                } else {
                    typszeregu = map.length;
                    nazwyZmiennych = ((WizardStepPanel_ZmienneCheckBoxes) steps[1]).getNazwyZmiennych();
                    macierz_ni = ((WizardStepPanel_ZmienneCheckBoxes) steps[1]).getMacierz(m);
                    //result2.printTableRow("ni", nazwyZmiennych.toString() + ", " + macierz_ni.toString() + "");

                    if (map.length == 1) { // szereg rozdzielczy
                        macierz_xi = Macierz.pobierzKolumny(m, map[0]);
                        //result2.printTableRow("xi", ps.getColumnNames()[map[0]] + ", " + macierz_xi.toString() + "");
                    } else if (map.length == 2) { // szereg rozdzielczy z przedziałami klasowymi
                        macierz_xi_poczatek = Macierz.pobierzKolumny(m, map[0]);
                        macierz_xi_koniec = Macierz.pobierzKolumny(m, map[1]);
                        macierz_xi = (Macierz) Znak.pomnozElementy(new Liczba(0.5), Znak.dodajElementy(macierz_xi_poczatek, macierz_xi_koniec));
                        result2.printTableRow("xi poczatek", ps.getColumnNames()[map[0]] + ", " + macierz_xi_poczatek.toString() + "");
                        result2.printTableRow("xi koniec", ps.getColumnNames()[map[1]] + ", " + macierz_xi_koniec.toString() + "");
                        result2.printTableRow("xi środek", macierz_xi.toString() + "");
                    } else {
                        throw new UnsupportedOperationException("DocumentPageFactory_CharakterystykiLiczbowe.create2 - map.length = " + map.length);
                    }
                }

                //obliczanie sum ni
                Macierz macierz_ni_sumy = PodstawoweStatystyki.suma(macierz_ni); //poziomy
                result2.printTableRow(""+(typszeregu > 0 ? "sumy ni" : "ilość obserwacji"), PageHtmlOutput.macierzToHtmlTable(macierz_ni_sumy).toString() + "");
				
                //obliczenie średniej
                //result2.printRAW("</table><h2>reszta</h2><table>");
                result2.printTableRowHead("Średnia", 2);
                Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
                Macierz macierz_sredniearytmetyczne = PodstawoweStatystyki.sredniaArytmetyczna(typszeregu, macierz_xi, macierz_ni);
                result2.printTableRow("średnie arytmetyczne", PageHtmlOutput.macierzToHtmlTable(macierz_sredniearytmetyczne).toString() + "");

                //obliczenie średniej geometrycznej
                Macierz macierz_sredniegeometryczne = PodstawoweStatystyki.sredniaGeometryczna(typszeregu, macierz_xi, macierz_ni);
                result2.printTableRow("średnie geometryczne", PageHtmlOutput.macierzToHtmlTable(macierz_sredniegeometryczne).toString() + "");

                //obliczanie częstości
                result2.printTableRowHead("Częstość", 2);
                Macierz macierz_wi = PodstawoweStatystyki.czestosci(typszeregu, macierz_xi, macierz_ni);
                result2.printTableRow("w<sub>i</sub>", PageHtmlOutput.macierzToHtmlTable(macierz_wi).toString() + "");
                result2.printTableRow("w<sub>i</sub> skumulowane", PageHtmlOutput.macierzToHtmlTable(PodstawoweStatystyki.kumuluj(macierz_wi)).toString() + "");


                Macierz macierz_wisk = PodstawoweStatystyki.kumuluj(macierz_wi);

                if (typszeregu > 0) {
                    Macierz macierz_xiwi = (Macierz) Znak.pomnozSkalarnieElementy(macierz_xi, macierz_wi);
                    result2.printTableRow("xi*wi", PageHtmlOutput.macierzToHtmlTable(macierz_xiwi).toString() + "");
                    Macierz macierz_xiwi_sumy = PodstawoweStatystyki.suma(macierz_xiwi);
                    result2.printTableRow("sumy xi*wi", PageHtmlOutput.macierzToHtmlTable(macierz_xiwi_sumy).toString() + "");
                }

                result2.printTableRowHead("Odchylenie", 2);
                //obliczanie odchylenia standardowego
                Macierz macierz_s2 = PodstawoweStatystyki.wariancja(typszeregu, macierz_xi, macierz_ni);
                Macierz macierz_s = PodstawoweStatystyki.odchylenieStandardowe(macierz_s2);
                result2.printTableRow("s^2", PageHtmlOutput.macierzToHtmlTable(macierz_s2).toString() + "");
                result2.printTableRow("s",PageHtmlOutput.macierzToHtmlTable(macierz_s).toString() + "");

                //obliczenie odchylenia przeciętnego:
                Macierz macierz_d = PodstawoweStatystyki.odchyleniePrzecietne(typszeregu, macierz_xi, macierz_ni);
                result2.printTableRow("d", PageHtmlOutput.macierzToHtmlTable(macierz_d).toString() + "");

                result2.printTableRowHead("Współczynniki zmienności", 2);
                //obliczenie współczynników zmienności
                Macierz macierz_vs = PodstawoweStatystyki.wspolczynnikZmiennosciVs(typszeregu, macierz_xi, macierz_ni);
                Macierz macierz_vd = PodstawoweStatystyki.wspolczynnikZmiennosciVd(typszeregu, macierz_xi, macierz_ni);
                result2.printTableRow("Vs", PageHtmlOutput.macierzToHtmlTable(macierz_vs) + "");
                result2.printTableRow("Vd", PageHtmlOutput.macierzToHtmlTable(macierz_vd) + "");

                result2.printTableRowHead("Miary położenia", 2);
                //obliczenie modalnej/dominanty: (szereg bez przedziałów klasowych)
                Macierz macierz_max = typszeregu > 0 ? PodstawoweStatystyki.max(macierz_ni) : PodstawoweStatystyki.max(macierz_xi);
                result2.printTableRow("max", PageHtmlOutput.macierzToHtmlTable(macierz_max).toString() + "");
                Macierz macierz_min = typszeregu > 0 ? PodstawoweStatystyki.min(macierz_ni) : PodstawoweStatystyki.min(macierz_xi);
                result2.printTableRow("min", PageHtmlOutput.macierzToHtmlTable(macierz_min).toString() + "");
                Macierz macierz_mo = PodstawoweStatystyki.modalna(typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
                result2.printTableRow("Mo", PageHtmlOutput.macierzToHtmlTable(macierz_mo) + "");

                // obliczenie kwantyli
                Macierz macierz_me = PodstawoweStatystyki.mediana(typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
                result2.printTableRow("Me", PageHtmlOutput.macierzToHtmlTable(macierz_me) + "");
                Macierz macierz_q1 = PodstawoweStatystyki.kwartyl(1, typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
                result2.printTableRow("Q1", PageHtmlOutput.macierzToHtmlTable(macierz_q1) + "");
                Macierz macierz_q2 = PodstawoweStatystyki.kwartyl(2, typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
                result2.printTableRow("Q2", PageHtmlOutput.macierzToHtmlTable(macierz_q2) + "");
                Macierz macierz_q3 = PodstawoweStatystyki.kwartyl(3, typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
                result2.printTableRow("Q3", PageHtmlOutput.macierzToHtmlTable(macierz_q3) + "");

                //obliczenie współczynników skośności
                result2.printTableRowHead("Współczynniki skośności", 2);
                Macierz macierz_as = PodstawoweStatystyki.wspolczynnikSkosnosciAs(typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
                Macierz macierz_ad = PodstawoweStatystyki.wspolczynnikSkosnosciAd(typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
                result2.printTableRow("As", PageHtmlOutput.macierzToHtmlTable(macierz_as) + "");
                result2.printTableRow("Ad", PageHtmlOutput.macierzToHtmlTable(macierz_ad) + "");

                result2.printTableRowHead("Współczynniki - pozostałe", 2);

                //obliczanie współczynnika asymetrii
                Macierz macierz_a = PodstawoweStatystyki.wspolczynnikAsymetriiA(typszeregu, macierz_xi, macierz_ni);
                result2.printTableRow("A", PageHtmlOutput.macierzToHtmlTable(macierz_a) + "");

                //obliczanie współczynnika skupienia
                Macierz macierz_k = PodstawoweStatystyki.wspolczynnikSkupieniaK(typszeregu, macierz_xi, macierz_ni);
                result2.printTableRow("K", PageHtmlOutput.macierzToHtmlTable(macierz_k) + "");

            } finally {
                result2.printRAW("</table>");
                result2.endUpdate();
            }
        }
        return result;
    }
}

class WizardStepPanel_TypSzeregu extends WizardStepPanel {

    Document doc;
    WizardStepPanel_ZmienneComboBoxes.ZmienneComboboxes comboBoxes;
    JPanel rbrnsPanel;

    /**
     * Tworzy krok kreatora z polami do wyboru roli zmiennych.
     * @param doc dokument, dla którego ma być utworzony krok kreatora
     * @param zmienneRole lista, do jakich ról można zmienne przyporządkowywać
     */
    public WizardStepPanel_TypSzeregu(Document doc) {
        //super(doc);
        super("");
        this.setTitle("Typ szeregu");
        this.doc = doc;
        setLayout(new BorderLayout());
        add(utworzStrone());

    }

    public ArrayList<JComboBox> getComboBoxes() {
        return comboBoxes == null ? null : comboBoxes.getComboBoxes();
    }

    public Integer[] getOdwzorowanie() {
        return comboBoxes == null ? null : comboBoxes.getOdwzorowanie();
    }

    abstract class MyActionListener implements ActionListener {

        public int wybrano = -1;
        }
    MyActionListener rbrnsActionListener = new MyActionListener() {

        public void actionPerformed(ActionEvent e) {
            wybrano = Integer.parseInt(e.getActionCommand());
//            System.out.println(wybrano + " ");
            WizardStepPanel_TypSzeregu parent = (WizardStepPanel_TypSzeregu) SwingUtils.findParentWithGivenClass((Container) e.getSource(), WizardStepPanel_TypSzeregu.class);
            //if (comboBoxes != null) {parent.remove(comboBoxes);            }
            parent.removeAll();
            parent.setForwardEnabled(true);
            switch (wybrano) {
                case 0:
                    comboBoxes = null;
                    break;
                case 1:
                    comboBoxes = (WizardStepPanel_ZmienneComboBoxes.ZmienneComboboxes) WizardStepPanel_ZmienneComboBoxes.createZmienneComboBoxes(doc, "xi");
                    parent.add(comboBoxes, BorderLayout.CENTER);
                    break;
                case 2:
                    comboBoxes = (WizardStepPanel_ZmienneComboBoxes.ZmienneComboboxes) WizardStepPanel_ZmienneComboBoxes.createZmienneComboBoxes(doc, "xi początek", "xi koniec");
                    parent.add(comboBoxes, BorderLayout.CENTER);
                    break;
                }
            parent.add(rbrnsPanel, BorderLayout.NORTH);
            parent.validate();
            parent.repaint();
        }
        };

    public JPanel utworzStrone() {
        JPanel result = new JPanel(new BorderLayout());
        final JRadioButton[] rbtns = new JRadioButton[3];
        rbtns[0] = new JRadioButton("szczegółowy");
        rbtns[1] = new JRadioButton("rozdzielczy");
        rbtns[2] = new JRadioButton("rozdzielczy z przedziałami klasowymi");
        //rbtns[1].setSelected(true);
        setForwardEnabled(false);
        PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
        rbtns[1].setEnabled(ps.getColumnNames().length > 1);
        rbtns[2].setEnabled(ps.getColumnNames().length > 2);
        rbrnsPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup rbtnsgroup = new ButtonGroup();
        {
            int k = 0;
            for (JRadioButton i : rbtns) {
                rbtnsgroup.add(i);
                rbrnsPanel.add(i);
                i.addActionListener(rbrnsActionListener);
                i.setActionCommand("" + (k++));
            }
        }

        result.add(rbrnsPanel, BorderLayout.NORTH);
        return result;

    }
}
