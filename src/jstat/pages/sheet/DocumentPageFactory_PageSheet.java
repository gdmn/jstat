/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.pages.sheet;

import dmnutils.document.*;
import dmnutils.wizard.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.BadLocationException;

/**
 * Klasa zawiera kreator tworzenia nowego arkusza.
 * @author dmn
 */
class DocumentPageFactory_PageSheet implements DocumentPageFactory<PageSheet> {

    private JSpinner zmienne , obserwacje;
    private boolean zmienneNazwyEdited = false;
    private JTextArea zmienneNazwy;
    private JLabel zmienneNazwyLabel = new JLabel();

    /** Creates a new instance of DocumentPageFactory_PageSheet */
    public DocumentPageFactory_PageSheet() {
    }

    public PageSheet create(Document doc) {
        PageSheet result = null;
        WizardStepPanel[] steps = new WizardStepPanel[2];
        WizardStepPanel step;

        JLabel l;

        //-- strona 1:
        step = new WizardStepPanel("Zmienne");
        steps[0] = step;
        SpringLayout layout = new SpringLayout();
        step.setLayout(layout);

        l = new JLabel("Liczba zmiennych:");
        zmienne = new JSpinner(new SpinnerNumberModel(3, 1, 100, 1));
        step.add(l);
        step.add(zmienne);
        step.setFirstFocused(zmienne);

        dmnutils.SwingUtils.springSetAsRow(null, 0, l, 150, zmienne, 0);
        //dmnutils.SwingUtils.springSetAsRow(null, zmienne, 0, l, 150);

        l = new JLabel("Liczba przypadków:"); //, JLabel.TRAILING);
        obserwacje = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
        step.add(l); step.add(obserwacje);

        dmnutils.SwingUtils.springSetAsRow(zmienne, 5, l, 150, obserwacje, 0);
        step = null;
		
        //-- strona 2:
        step = new WizardStepPanel("Nazwy");
        steps[1] = step;
        zmienneNazwy = new JTextArea();
        //kiedy zmieniono zawartość pola, już nie można automatycznie generować nazw zmiennych:
        zmienneNazwy.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                //System.out.println("key: "+e.toString());
                zmienneNazwyEdited = true;
            }
            public void keyReleased(KeyEvent e) {
                int powinno = (Integer)zmienne.getValue(),
                        jest = zmienneNazwy.getLineCount();
                String ostatniaLinijka = null;
                try {
                    int p1 = zmienneNazwy.getLineStartOffset(jest-1),
                        p2 = zmienneNazwy.getLineEndOffset(jest-1);
                    ostatniaLinijka = zmienneNazwy.getText().substring(p1, p2);
                    //System.out.println(p1+" - "+p2+": "+ostatniaLinijka+".");
                    if (p1 == p2) jest--;
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                zmienneNazwyLabel.setText("Zmiennych: "+powinno+", wprowadzono: "+jest);
                ((WizardStepPanel)zmienneNazwyLabel.getParent()).setForwardEnabled(jest==powinno);
            }
        });
        step.setLayout(new BorderLayout());
        step.add(new JScrollPane(zmienneNazwy), BorderLayout.CENTER);
        step.setFirstFocused(zmienneNazwy);
        step.add(new JLabel("Wprowadź nazwy zmiennych:"), BorderLayout.NORTH);
        step.add(zmienneNazwyLabel, BorderLayout.SOUTH);
        step.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                //System.out.println("gained");
                if (!zmienneNazwyEdited) {
                    zmienneNazwy.setText("");
                    for (String s : generujNazwy(0, (Integer)zmienne.getValue())) {
                        zmienneNazwy.append(s+"\n");
                    }
                } else { System.out.println("Zmieniono nazwy zmiennych, nie generuję automatycznie"); }
            }
        });
        step = null;

        //-- przygotowanie kreatora:
        ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(Arrays.asList(steps));
        WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);
        WizardDialog dlg = new WizardDialog(null,"Nowy arkusz",wizard);
        if (dlg.execute() == WizardMainPanel.ACTION_OK) {
            Class[] classes = new Class[(Integer)zmienne.getValue()];
            for (int i = 0; i < classes.length; i++) {
                //classes[i] = Double.class;
                classes[i] = kalkulator.Liczba.class;
            }

            result = new PageSheet(
                    classes,
                    zmienneNazwy.getText().split("\n"),
                    (Integer)obserwacje.getValue()
                    );
        }
        return result;
    }

    private static String[] generujNazwy(int opcja, int ilosc) {
        String[] result = new String[ilosc];
        for (int i = 0; i < ilosc; i++) {
            String s;
            switch (opcja) {
                case 0: s = "Zmn"+(i+1); break;
                default: s = ""+(i+1);
            }
            result[i] = s;
        }
        return result;
    }

}
