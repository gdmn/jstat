/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.pages.calculator;

import jstat.plugins.wrappers.Tools;
import dmnutils.document.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import dmnutils.ColoredTextPane;
import java.io.*;
import java.util.Arrays;
import kalkulator.*;
import walidacja.Walidator;

/**
 *
 * @author Damian
 */
public class PageCalculator extends AbstractDocumentPage<PageCalculator> implements ActionListener, Externalizable {

    protected JTextField textInput;
    protected ColoredTextPane logTextArea;

    /** Creates a new instance of PageCalculator */
    public PageCalculator() {
        super();
        setCaption("Kalkulator");
        constructorHelper();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(logTextArea.getText());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        constructorHelper();
        logTextArea.setText((String) in.readObject());
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    private void constructorHelper() {
        textInput = new JTextField(20);
        textInput.addActionListener(this);

        logTextArea = new ColoredTextPane();
        logTextArea.setPreferredSize(new Dimension(500, 350));
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);

        logTextArea.setFont(textInput.getFont()); //przy wyglądzie windows jest zmniejszona czcionka w logTextArea

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 5, 5));
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(textInput, BorderLayout.SOUTH);
        bottomPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        add(bottomPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);

        FocusListener foc = new FocusListener() {

            public void focusGained(FocusEvent e) {
                textInput.requestFocusInWindow();
                textInput.selectAll();
            }

            public void focusLost(FocusEvent e) {}

        };

        for (Component o : Arrays.asList(this, bottomPanel, scrollPane) ) {
            o.addFocusListener(foc);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == textInput) {
            try {
                String fromUser = textInput.getText();
                logTextArea.print(fromUser, ColoredTextPane.USER);
                long time1 = System.nanoTime();
                int w = Walidator.sprawdzNawiasy(fromUser);
                if (w != 0) {
                    logTextArea.print("nawiasy: " + w +
                            ((w > 0) ? (new StringBuffer(fromUser)).insert(w, '#').insert(w + 2, '#').insert(0, " ").toString() : ""));
                    return;
                }
                WyrazeniePostfix wPostfix = null;
                WyrazenieInfix wInfix = null;
                try {
                    wInfix = WyrazenieInfix.valueOf(fromUser, myFunctions);
                    wPostfix = new WyrazeniePostfix(wInfix);
                //                    wPostfix = WyrazeniePostfix.valueOf(fromUser, myFunctions);
                } catch (NumberFormatException ex) {
                    logTextArea.printerr("To nie jest liczba/macierz: " + ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    logTextArea.printerr("To nie jest funkcja: " + ex.getMessage());
                }
                dmnutils.Log.print("1) " + wInfix.toString(), ColoredTextPane.ITALIC);
                dmnutils.Log.print("2) " + wPostfix.toString(), ColoredTextPane.ITALIC);
                try {
					long timex1 = System.nanoTime();
                    ElementWyrazenia wynik = wPostfix.getValue();
                    long timex2 = System.nanoTime();
                    String wynikString = wynik.toString();
                    logTextArea.print(wynikString);
                    long time2 = System.nanoTime();
                    System.out.println("czas obliczeń: " + (0.000000001d * (timex2 - timex1)) + " s. (" + (0.000000001d * (time2 - time1)) + " s.)");
                //GraphWindow.handle.rysuj(fromUser);
                } catch (InvalidArgumentException ex) {
                    logTextArea.printerr("Zły typ/liczba argumentów dla: " + ex.getMessage());
                } catch (ArithmeticException ex) {
                    logTextArea.printerr("Wyrażenie jest nieoznaczone: " + ex.getMessage());
                } catch (NumberFormatException ex) {
                    logTextArea.printerr("Wyrażenie jest nieoznaczone: " + ex.getMessage());

                    String wynikString = Tools.calc(fromUser).toString();
                    logTextArea.print(wynikString);
                }
            } finally {
                textInput.selectAll();
                this.changed();
            //TODO doc.changed()
            }
        }
    }

    public JTextField getTextInput() {
        return textInput;
    }



    public static DocumentPageFactory<PageCalculator> factory = new DocumentPageFactory<PageCalculator>() {

        public PageCalculator create(Document doc) {
            PageCalculator result;
            result = new PageCalculator();
            return result;
        }
    };

    //static class ZmiennaX implements FunkcjaInterface {
    static FunkcjaInterface fZmiennaX = new FunkcjaInterface() {

        public String toString() {
            return "X";
        }

        public int getArgumentsCount() {
            return 0;
        }

        public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
            return new Liczba(0d);
        }
    };
    static FunkcjaInterface fTest = new FunkcjaInterface() {

		@Override
        public String toString() {
            return "TEST";
        }

        public int getArgumentsCount() {
            return 0;
        }

        public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
            int iloscprob = 100;
            Macierz result = new Macierz(new WyrazenieInfix[iloscprob][2]);
            long t1 = System.currentTimeMillis();
            for (int i = 0; i < iloscprob; i++) {
                Macierz m = Macierz.macierzHilberta(7);
                m = (Macierz) Znak.pomnozElementy(m, new Liczba(360360));
                result.set(i, 0, Macierz.wyznacznikGauss(Macierz.macierzGrama(m)));
                result.set(i, 1, new Liczba(System.currentTimeMillis() - t1));
            }
            long t2 = System.currentTimeMillis();
            return new Tekst("Wynik testu (" + iloscprob + " prób): " + (t2 - t1));
        }
    };
    static private FunkcjaInterface[] myFunctions = new FunkcjaInterface[] {fZmiennaX, fTest};
}
