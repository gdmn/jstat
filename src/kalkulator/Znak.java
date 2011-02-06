/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

import java.math.*;
import kalkulator.Funkcja.*;

/**
 * Klasa dziedzicząca po ElementWyrazenia.
 * Reprezentuje znaki obliczeń: "+", "-", "*", "/", "%" (dzielenie modulo), "^" (potęgowanie).
 * @author Damian
 */
public class Znak extends ElementWyrazenia {

    char charRepresentation;

    /** Creates a new instance of Znak */
    public Znak(String str) {
        super(str);
        charRepresentation = stringRepresentation.charAt(0);
    }

    public int getPriority() {
        return getPriority(charRepresentation);
    } //getPriority

    public static int getPriority(char charRepresentation) {
        switch (charRepresentation) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
            case '|':
            case '%': //reszta z dzielenia
                return 2;
            case '^': //potęga
                return 3;
        } //switch
        return -1;
    } //getPriority

    public int getArgumentsCount() {
        return 2;
    }

    /**
     * Dodaje macierze, skalary, lub macierz i skalar.
     */
    public static ElementWyrazenia dodajElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        if ((args0 instanceof Macierz) && (args1 instanceof Macierz)) {
            Macierz m1 = (Macierz) args0;
            Macierz m2 = (Macierz) args1;
            if ((m1.getLengthX() != m2.getLengthX()) || (m1.getLengthY() != m2.getLengthY())) {
                throw new InvalidArgumentException("(macierze mają różne wymiary)");
            }
            Macierz result = new Macierz(new WyrazenieInfix[m1.getLengthX()][m1.getLengthY()]);
            //dodanie wszystkich elementów:
            for (int x = 0; x < m1.getLengthX(); x++) {
                for (int y = 0; y < m1.getLengthY(); y++) {
                    result.set(x, y, new Liczba(m1.getValueAt(x, y).getValueAsBigDecimal().add(m2.getValueAt(x, y).getValueAsBigDecimal())));
                }
            }
            return result;
        } else if ((args0 instanceof Liczba) && (args1 instanceof Liczba)) {
            return new Liczba(((Liczba) args0).getValueAsBigDecimal().add(((Liczba) args1).getValueAsBigDecimal()));
        } else if (((args0 instanceof Liczba) && (args1 instanceof Macierz)) || ((args0 instanceof Macierz) && (args1 instanceof Liczba))) {
            BigDecimal l;
            Macierz m;
            if (args0 instanceof Liczba) {
                l = ((Liczba) args0).getValueAsBigDecimal();
                m = (Macierz) args1;
            } else {
                l = ((Liczba) args1).getValueAsBigDecimal();
                m = (Macierz) args0;
            }
            Macierz result = new Macierz(new WyrazenieInfix[m.getLengthX()][m.getLengthY()]);
            //dodanie wszystkich elementów:
            for (int x = 0; x < m.getLengthX(); x++) {
                for (int y = 0; y < m.getLengthY(); y++) {
                    result.set(x, y, new Liczba(m.getValueAt(x, y).getValueAsBigDecimal().add(l)));
                }
            }
            return result;
        } else {
            throw new InvalidArgumentException(ElementWyrazenia.getArgumentsInfo(args0, args1));
        }
    }

    /**
     * Odejmuje macierze, skalary, lub macierz i skalar.
     */
    public static ElementWyrazenia odejmijElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        return dodajElementy(args0, przeciwnyElement(args1));
    }

    /**
     * Zwraca liczbę lub macierz przeciwną.
     */
    public static ElementWyrazenia przeciwnyElement(ElementWyrazenia args0) throws InvalidArgumentException {
        if (args0 instanceof Macierz) {
            Macierz m1 = (Macierz) args0;
            Macierz result = new Macierz(new WyrazenieInfix[m1.getLengthX()][m1.getLengthY()]);
            for (int x = 0; x < m1.getLengthX(); x++) {
                for (int y = 0; y < m1.getLengthY(); y++) {
                    result.set(x, y, new Liczba(m1.getValueAt(x, y).getValueAsBigDecimal().negate()));
                }
            }
            return result;
        } else if (args0 instanceof Liczba) {
            return new Liczba(((Liczba) args0).getValueAsBigDecimal().negate());
        } else {
            throw new InvalidArgumentException(ElementWyrazenia.getArgumentsInfo(args0));
        }
    }

    /**
     * Mnoży macierze, skalary, lub macierz i skalar.
     */
    public static ElementWyrazenia pomnozElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        if ((args0 instanceof Macierz) && (args1 instanceof Macierz)) {
            Macierz m1 = (Macierz) args0;
            Macierz m2 = (Macierz) args1;
            if (m1.getLengthX() != m2.getLengthY()) {
                throw new InvalidArgumentException("(ilość kolumn pierwszej macierzy jest różna od ilości wierszy drugiej)");
            }
            Macierz result = new Macierz(new WyrazenieInfix[m2.getLengthX()][m1.getLengthY()]);
            //System.out.println("Mnożenie macierzy - wynik: wiersze="+m1.getLengthY()+", kolumny="+m2.getLengthX());
            //pomnożenie wszystkich elementów:
            BigDecimal wynik;
            for (int x = 0; x < m2.getLengthX(); x++) { //tyle ile wierszy ma pierwsza macierz
                for (int y = 0; y < m1.getLengthY(); y++) { //tyle ile kolumn ma druga
                    wynik = new BigDecimal(0);
                    for (int j = 0; j < m2.getLengthY(); j++) {
                        wynik = wynik.add(m1.getValueAt(j, y).getValueAsBigDecimal().multiply(m2.getValueAt(x, j).getValueAsBigDecimal()));
                    }
                    result.set(x, y, new Liczba(wynik));
                }
            }
            return result;
        } else if ((args0 instanceof Liczba) && (args1 instanceof Liczba)) {
            return new Liczba(((Liczba) args0).getValueAsBigDecimal().multiply(((Liczba) args1).getValueAsBigDecimal()));
        } else if (((args0 instanceof Liczba) && (args1 instanceof Macierz)) || ((args0 instanceof Macierz) && (args1 instanceof Liczba))) {
            BigDecimal l;
            Macierz m;
            if (args0 instanceof Liczba) {
                l = ((Liczba) args0).getValueAsBigDecimal();
                m = (Macierz) args1;
            } else {
                l = ((Liczba) args1).getValueAsBigDecimal();
                m = (Macierz) args0;
            }
            Macierz result = new Macierz(new WyrazenieInfix[m.getLengthX()][m.getLengthY()]);
            //pomnożenie wszystkich elementów:
            for (int x = 0; x < m.getLengthX(); x++) {
                for (int y = 0; y < m.getLengthY(); y++) {
                    result.set(x, y, new Liczba(m.getValueAt(x, y).getValueAsBigDecimal().multiply(l)));
                }
            }
            return result;
        } else {
            throw new InvalidArgumentException(ElementWyrazenia.getArgumentsInfo(args0, args1));
        }
//args0.getClass().getName()+" i "+args1.getClass().getName());
    }

    /**
     * Oblicza iloczyn skalarny wektorów (pionowych lub poziomych) (bez sumowania!).
     * Gdy macierze mają te same rozmiary, liczy iloczyn każdego elementu pierwszej
     * z odpowiednim elementem drugiej.
     * Gdy jedna macierz jest pionowa lub pozioma, a jej dłuższy wymiar
     * zgadza się z wymiarem odpowiadającym drugiej, to działanie jest
     * wykonywane na wierszach (kolumnach).
     */
    public static ElementWyrazenia pomnozSkalarnieElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        if ((args0 instanceof Macierz) && (args1 instanceof Macierz)) {
            Macierz m1 = (Macierz) args0;
            Macierz m2 = (Macierz) args1;
            if (m1.getLengthX() == m2.getLengthX() && m1.getLengthY() == m2.getLengthY()) {
                Macierz result = new Macierz(m2.getLengthX(), m1.getLengthY());
                //BigDecimal wynik;
                for (int x = 0; x < m1.getLengthX(); x++) {
                    for (int y = 0; y < m1.getLengthY(); y++) {
                        //wynik = m1.getValueAt(x, y).getValue().multiply(m2.getValueAt(x, y).getValue());
                        //result.set(x, y, new Liczba(wynik));
                        result.set(x, y, (Liczba) Znak.pomnozElementy(m1.getValueAt(x, y), m2.getValueAt(x, y)));
                    }
                }
                return result;
            } else if (m1.getLengthX() == m2.getLengthX() && (m1.getLengthY() == 1 || m2.getLengthY() == 1)) {
                boolean wybierz1 = m1.getLengthY() == 1;
                Macierz mala = wybierz1 ? m1 : m2;
                Macierz duza = wybierz1 ? m2 : m1;
                Macierz result = new Macierz(duza.getLengthX(), duza.getLengthY());
                for (int x = 0; x < duza.getLengthX(); x++) {
                    Liczba malavalue = mala.getValueAt(x, 0);
                    for (int y = 0; y < duza.getLengthY(); y++) {
                        result.set(x, y, (Liczba) Znak.pomnozElementy(duza.getValueAt(x, y), malavalue));
                    }
                }
                return result;
            } else if (m1.getLengthY() == m2.getLengthY() && (m1.getLengthX() == 1 || m2.getLengthX() == 1)) {
                boolean wybierz1 = m1.getLengthX() == 1;
                Macierz mala = wybierz1 ? m1 : m2;
                Macierz duza = wybierz1 ? m2 : m1;
                Macierz result = new Macierz(duza.getLengthX(), duza.getLengthY());
                for (int y = 0; y < duza.getLengthY(); y++) {
                    Liczba malavalue = mala.getValueAt(0, y);
                    for (int x = 0; x < duza.getLengthX(); x++) {
                        result.set(x, y, (Liczba) Znak.pomnozElementy(duza.getValueAt(x, y), malavalue));
                    }
                }
                return result;
            }
            throw new InvalidArgumentException("(wymiary macierzy się nie zgadzają)");
        } else if ((args0 instanceof Liczba) && (args1 instanceof Liczba)) {
            return new Liczba(((Liczba) args0).getValueAsBigDecimal().multiply(((Liczba) args1).getValueAsBigDecimal()));
        } else {
            throw new InvalidArgumentException(ElementWyrazenia.getArgumentsInfo(args0, args1));
        }
    }

    /**
     * Oblicza "sumę skalarną" wektorów (pionowych lub poziomych).
     * Gdy macierze mają te same rozmiary, liczy sumę każdego elementu pierwszej
     * z odpowiednim elementem drugiej.
     * Gdy jedna macierz jest pionowa lub pozioma, a jej dłuższy wymiar
     * zgadza się z wymiarem odpowiadającym drugiej, to działanie jest
     * wykonywane na wierszach (kolumnach).
     */
    public static ElementWyrazenia dodajSkalarnieElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        if ((args0 instanceof Macierz) && (args1 instanceof Macierz)) {
            Macierz m1 = (Macierz) args0;
            Macierz m2 = (Macierz) args1;
            if (m1.getLengthX() == m2.getLengthX() && m1.getLengthY() == m2.getLengthY()) {
                Macierz result = new Macierz(m2.getLengthX(), m1.getLengthY());
                //BigDecimal wynik;
                for (int x = 0; x < m1.getLengthX(); x++) {
                    for (int y = 0; y < m1.getLengthY(); y++) {
                        result.set(x, y, (Liczba) Znak.dodajElementy(m1.getValueAt(x, y), m2.getValueAt(x, y)));
                    }
                }
                return result;
            } else if (m1.getLengthX() == m2.getLengthX() && (m1.getLengthY() == 1 || m2.getLengthY() == 1)) {
                boolean wybierz1 = m1.getLengthY() == 1;
                Macierz mala = wybierz1 ? m1 : m2;
                Macierz duza = wybierz1 ? m2 : m1;
                Macierz result = new Macierz(duza.getLengthX(), duza.getLengthY());
                for (int x = 0; x < duza.getLengthX(); x++) {
                    Liczba malavalue = mala.getValueAt(x, 0);
                    for (int y = 0; y < duza.getLengthY(); y++) {
                        result.set(x, y, (Liczba) Znak.dodajElementy(duza.getValueAt(x, y), malavalue));
                    }
                }
                return result;
            } else if (m1.getLengthY() == m2.getLengthY() && (m1.getLengthX() == 1 || m2.getLengthX() == 1)) {
                boolean wybierz1 = m1.getLengthX() == 1;
                Macierz mala = wybierz1 ? m1 : m2;
                Macierz duza = wybierz1 ? m2 : m1;
                Macierz result = new Macierz(duza.getLengthX(), duza.getLengthY());
                for (int y = 0; y < duza.getLengthY(); y++) {
                    Liczba malavalue = mala.getValueAt(0, y);
                    for (int x = 0; x < duza.getLengthX(); x++) {
                        result.set(x, y, (Liczba) Znak.dodajElementy(duza.getValueAt(x, y), malavalue));
                    }
                }
                return result;
            }
            throw new InvalidArgumentException("(wymiary macierzy się nie zgadzają)");
        } else return dodajElementy(args0, args1);
    }

    /**
     * Oblicza "różnicę skalarną" wektorów (pionowych lub poziomych).
     * Gdy macierze mają te same rozmiary, liczy różnicę każdego elementu pierwszej
     * z odpowiednim elementem drugiej.
     * Gdy jedna macierz jest pionowa lub pozioma, a jej dłuższy wymiar
     * zgadza się z wymiarem odpowiadającym drugiej, to działanie jest
     * wykonywane na wierszach (kolumnach).
     */
    public static ElementWyrazenia odejmijSkalarnieElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        return dodajSkalarnieElementy(args0, Znak.przeciwnyElement(args1));
    }

    /**
     * Dzieli skalary, macierze lub macierze i skalary.
     */
    public static ElementWyrazenia podzielElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        ElementWyrazenia w = null;
        w = (new Funkcja(Functions.INV)).getValue(args1);
        return pomnozElementy(args0, w);
    }

    /**
     * Dzieli skalarnie macierze.
     * Gdy macierze mają te same rozmiary, dzieli każdy element pierwszej
     * przez odpowiedni element drugiej.
     * Gdy jedna macierz jest pionowa lub pozioma, a jej dłuższy wymiar
     * zgadza się z wymiarem odpowiadającym drugiej, to działanie jest
     * wykonywane na wierszach (kolumnach).
     */
    public static ElementWyrazenia podzielSkalarnieElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        ElementWyrazenia w = null;
        if (args1 instanceof Macierz) {
            w = Macierz.getValueApplyFunction1((Macierz) args1, new Funkcja(Functions.INV));
        } else {
            w = (new Funkcja(Functions.INV)).getValue(args1);
        }
        return pomnozSkalarnieElementy(args0, w);
    }

    /**
     * Podnosi element args0 do potęgi args1.
     */
    public static ElementWyrazenia potegujElementy(ElementWyrazenia args0, ElementWyrazenia args1) throws InvalidArgumentException {
        if ((args0 instanceof Macierz) && (args1 instanceof Liczba)) {
            Liczba l1 = (Liczba) args1;
            Liczba l = (Liczba) Funkcja.getValue(Functions.ROUND, l1);
            Macierz m = (Macierz) args0;
            if (l1.compareTo(l) == 0) {
                if (l.compareTo(Liczba.ZERO) == 0) {
                    Macierz result = new Macierz(m.getLengthX(), m.getLengthY());
                    result.fill(new Liczba(1));
                    return result;
                } else if (l.compareTo(Liczba.JEDEN) == 0) {
                    return m;
                } else if (l.compareTo(Liczba.ZERO) < 0) {
                    Macierz result = Macierz.odwrotna(m);
                    for (int i = 1; i < -l.getValueAsInt(); i++) {
                        result = (Macierz) Znak.pomnozElementy(result, Macierz.transpozycja(result));
                    }
                    return result;
                } else { // l > 0
                    Macierz result = (Macierz) Znak.pomnozElementy(m, Liczba.JEDEN); // klonuje macierz
                    for (int i = 1; i < l.getValueAsInt(); i++) {
                        result = (Macierz) Znak.pomnozElementy(result, Macierz.transpozycja(result));
                    }
                    return result;
                }
            } else {
                throw new InvalidArgumentException(ElementWyrazenia.getArgumentsInfo(args0, args1) + " - nie można wyciągać pierwiastków z macierzy");
            }
        } else if ((args0 instanceof Liczba) && (args1 instanceof Liczba)) {
            return new Liczba(((Liczba) args0).pow(((Liczba) args1).getValueAsBigDecimal()));
        } else {
            throw new InvalidArgumentException(ElementWyrazenia.getArgumentsInfo(args0, args1));
        }
    }

    public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException, ArithmeticException {
        ElementWyrazenia result;
        switch (charRepresentation) {
            case '+':
                //return args[0]+args[1];
                try {
                    result = dodajElementy(args[0], args[1]);
                    return result;
                } catch (InvalidArgumentException ex) {
                    throw new InvalidArgumentException(this.toString() + " " + ex.getMessage());
                }
            case '-':
                //return args[0]-args[1];
                try {
                    result = dodajElementy(args[0], przeciwnyElement(args[1]));
                    return result;
                } catch (InvalidArgumentException ex) {
                    throw new InvalidArgumentException(this.toString() + " " + ex.getMessage());
                }
            case '*':
                //return args[0]*args[1];
                try {
                    result = pomnozElementy(args[0], args[1]);
                    return result;
                } catch (InvalidArgumentException ex) {
                    throw new InvalidArgumentException(this.toString() + " " + ex.getMessage());
                }
            case '|':
                //return args[0] | args[1]; skalarny iloczyn
                try {
                    result = pomnozSkalarnieElementy(args[0], args[1]);
                    return result;
                } catch (InvalidArgumentException ex) {
                    throw new InvalidArgumentException(this.toString() + " " + ex.getMessage());
                }
            case '/':
                //return args[0]/args[1];
                try {
                    result = podzielElementy(args[0], args[1]);
                    return result;
                } catch (InvalidArgumentException ex) {
                    throw new InvalidArgumentException(this.toString() + " " + ex.getMessage());
                }
            case '^':
                try {
                    result = potegujElementy(args[0], args[1]);
                    return result;
                } catch (InvalidArgumentException ex) {
                    throw new InvalidArgumentException(this.toString() + " " + ex.getMessage());
                }
            case '%':
                //return args[0] % args[1];
                if ((args[0] instanceof Macierz) && (args[1] instanceof Macierz)) {
                    throw new InvalidArgumentException(this.toString() + " " + ElementWyrazenia.getArgumentsInfo(args));
                } else if ((args[0] instanceof Liczba) && (args[1] instanceof Liczba)) {
                    return new Liczba(((Liczba) args[0]).getValueAsBigDecimal().divideAndRemainder(((Liczba) args[1]).getValueAsBigDecimal())[1]);
                } else {
                    throw new InvalidArgumentException(this.toString() + " " + ElementWyrazenia.getArgumentsInfo(args));
                }
        } //switch
        return null;
    }
}
