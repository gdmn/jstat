/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */
package jstat.plugins.wrappers;

import dmnutils.WorkingDialog;
import dmnutils.document.AbstractDocumentPage;
import javax.swing.JOptionPane;
import jstat.gui.MyInternalFrame;
import kalkulator.*;

/**
 *
 * @author dmn
 */
public class Tools {

    public static void progress(String s) {
        WorkingDialog.setTempProgressText(s);
    }

    public static ElementWyrazenia calc(String fromUser) {
        WyrazeniePostfix wPostfix = null;
        WyrazenieInfix wInfix = null;
        wInfix = WyrazenieInfix.valueOf(fromUser);
        wPostfix = new WyrazeniePostfix(wInfix);
        ElementWyrazenia wynik = wPostfix.getValue();
//        String wynikString = wynik.toString();
        return wynik;
    }

    public static AbstractDocumentPage getActivePage(boolean showErrorMessage, Class<? extends AbstractDocumentPage> c) {
        MyInternalFrame mif = jstat.gui.Main.getMain().getActiveFrame(showErrorMessage);
        if (mif != null) {
            return mif.getActiveDocumentPage(showErrorMessage, c);
        }
        return null;
    }

    public static AbstractDocumentPage getActivePage(Class<? extends AbstractDocumentPage> c) {
        return getActivePage(true, c);
    }

    /**
     * Wczytuje tekst od użytkownika.
     * @param information pokazywana informacja
     * @param def domyślna wartość w polu edycji.
     * @return wprowadzony tekst lub <code>null</code>, gdy anulowano.
     */
    public static String getText(String information, String def) {
        String s = (String) JOptionPane.showInputDialog(jstat.gui.Main.getMain(), information, "Wprowadź tekst",
                JOptionPane.QUESTION_MESSAGE, null, null, def);
        return s;
    }

    /**
     * Wczytuje tekst od użytkownika.
     * @param information pokazywana informacja
     * @return wprowadzony tekst lub <code>null</code>, gdy anulowano.
     */
    public static String getText(String information) {
        return getText(information, "");
    }

    /**
     * Wczytuje tekst od użytkownika. Przekształca go na obiekt podanej klasy,
     * o ile podana klasa ma metodę publiczną <code>valueOf(String s)</code>.
     * Używa mechanizmów refleksji do znalezienia i wywołania tej metody.
     * @param information informacja dla użytkownika,
     * @param klasa Klasa obiektu, np. <code>kalkulator.Liczba.class</code>.
     * @return obiekt podanej klasy lub <code>null</code> gdy anulowano.
     */
    public static Object getObjectFromText(String information, Class klasa) {
        return getObjectFromText(information, null, klasa);
    }

    /**
     * Wczytuje tekst od użytkownika. Przekształca go na obiekt podanej klasy,
     * o ile podana klasa ma metodę publiczną <code>valueOf(String s)</code>.
     * Używa mechanizmów refleksji do znalezienia i wywołania tej metody.
     * @param information informacja dla użytkownika,
     * @param def wartość domyślna,
     * @param klasa Klasa obiektu, np. <code>kalkulator.Liczba.class</code>.
     * @return obiekt podanej klasy lub <code>null</code> gdy anulowano.
     */
    public static Object getObjectFromText(String information, String def, Class klasa) {
        Object result = null;
        boolean ok = false;
        String r = def;
        do {
            r = getText(information, r);
            if (r != null) {
                @SuppressWarnings("unchecked")
                beanutils.ValueOfParser v = new beanutils.ValueOfParser(klasa);
                try {
                    result = v.valueOf(r);
                    ok = true;
                } catch (Exception e) {
                    System.err.println("\"" + r + "\" nie jest typu " + klasa.getSimpleName());
                }
            } else {
                ok = true;
            }
        } while (ok != true);
        return result;
    }

    public static boolean getOkCancel(String question) {
        int n = JOptionPane.showOptionDialog(jstat.gui.Main.getMain(), question, "Pytanie",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, null, null);
        return n == JOptionPane.OK_OPTION;
    }

    public static boolean getYesNo(String question) {
        int n = JOptionPane.showOptionDialog(jstat.gui.Main.getMain(), question, "Pytanie",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, null, null);
        return n == JOptionPane.YES_OPTION;
    }

    public static void showErr(String err) {
        JOptionPane.showMessageDialog(jstat.gui.Main.getMain(), err,
                "Błąd", JOptionPane.ERROR_MESSAGE);
    }

    public static void showInf(String inf) {
        JOptionPane.showMessageDialog(jstat.gui.Main.getMain(), inf,
                "Informacja", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showWarn(String inf) {
        JOptionPane.showMessageDialog(jstat.gui.Main.getMain(), inf,
                "Uwaga", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Główne okno aplikacji.
     * @return uchwyt do głównego okna aplikacji.
     */
    public static jstat.gui.Main getMain() {
        return jstat.gui.Main.getMain();
    }

    public static ElementWyrazenia funkcjaGetValue(Funkcja.Functions funkcja, ElementWyrazenia[] args) throws InvalidArgumentException {
        Funkcja f = new Funkcja(funkcja);
        return f.getValue(args);
    }
}
