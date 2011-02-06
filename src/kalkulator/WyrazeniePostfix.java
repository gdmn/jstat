/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

// TODO: późne rozwijanie argumentów, na potrzeby całkowania - zrobione

import java.util.*;

/**
 * Reprezentuje wyrażenie w notacji przyrostkowej (postfix notation),
 * nazwane notacją polską (Reverse Polish Notation).
 * @author Damian
 */
public class WyrazeniePostfix extends WyrazenieAbstract {

    /**
     * Tworzy <code>WyrazeniePostfix</code> z podanego <code>WyrazenieInfix</code>.
     * Przejmuje z niego <code>userArrayOfFunctions</code>.<br>
     * Preferowana metoda tworzenia wyrażeń postfix:
     * <code>WyrazeniePostfix.valueOf(String wyrazenie, FunkcjaInterface[] userArrayOfFunctions)</code>.
     * @param wyrazenieInfix
     */
    public WyrazeniePostfix(WyrazenieInfix wyrazenieInfix) {
        super(wyrazenieInfix.getAll(), wyrazenieInfix.getUserArrayOfFunctions());
        doConversion();
    }

/**
     * Konstruktor tworzący obiekt <code>WyrazeniePostfix</code>.     *
     * @param e tablica <code>ElementWyrazenia[]</code>
     * @param userArrayOfFunctions tablica <code>FunkcjaInterface[]</code>.
     * Lista funkcji dodanych przez użytkownika, np. fcja X przy rysowaniu wykresu.
     */
    public WyrazeniePostfix(ElementWyrazenia[] e, FunkcjaInterface... userArrayOfFunctions) {
        super(e, userArrayOfFunctions);
        myElements = new ArrayList<ElementWyrazenia>(Arrays.asList(e));
    }

    /**
     * Konstruktor tworzący obiekt <code>WyrazenieInfix</code>.     *
     * @param e tablica <code>Collection<? extends ElementWyrazenia></code>
     * @param userArrayOfFunctions tablica <code>FunkcjaInterface[]</code>.
     * Lista funkcji dodanych przez użytkownika, np. fcja X przy rysowaniu wykresu.
     */
    public WyrazeniePostfix(Collection<? extends ElementWyrazenia> e, FunkcjaInterface... userArrayOfFunctions)  {
        super(e, userArrayOfFunctions);
        myElements = new ArrayList<ElementWyrazenia>(e);
    }

    /**
     * Zwraca obiekt WyrazeniePostfix, który jest zgodny z podanym argumentem.
     * @param wyrazenie String z wyrażeniem infix (standardowa notacja).
     * @param userArrayOfFunctions tablica <code>FunkcjaInterface[]</code>.
     * Lista funkcji dodanych przez użytkownika, np. fcja X przy rysowaniu wykresu.
     * @return Obiekt WyrazeniePostfix.
     * @throws IllegalArgumentException, NumberFormatException.
     */
    public static WyrazeniePostfix valueOf(String wyrazenie, FunkcjaInterface... userArrayOfFunctions) throws IllegalArgumentException, NumberFormatException {
        WyrazenieInfix infix = WyrazenieInfix.valueOf(wyrazenie, userArrayOfFunctions);
        return new WyrazeniePostfix(infix);
    }


    private void doConversion() {
        ArrayList<ElementWyrazenia> newElements = new ArrayList<ElementWyrazenia>(myElements.size());
        LinkedList<ElementWyrazenia> stos = new LinkedList<ElementWyrazenia>();
        for (ElementWyrazenia el : myElements) {
            if (el instanceof Liczba) {
                Liczba e2 = (Liczba)el;
                newElements.add(e2);
            } else if (el instanceof WyrazeniePostfix) {
                WyrazeniePostfix e2 = (WyrazeniePostfix)el;
                newElements.add(e2);
            } else if (el instanceof Macierz) {
                Macierz e2 = (Macierz)el;
                newElements.add(e2);
            } else if (el instanceof Nawiasy) {
                Nawiasy e2 = (Nawiasy)el;
                if (e2.isOpening()) stos.addFirst(e2);
                else {
                    ElementWyrazenia zeStosu;
                    while (!stos.isEmpty()) {
                        zeStosu = stos.removeFirst();
                        if (!(zeStosu instanceof Nawiasy)) newElements.add(zeStosu);
                        else break;
                    }
                }
            } else if (el instanceof SeparatorArgumentu) {
                //jeśli trafiamy na średnik, przeszukujemy stos do nawiasu otwierającego (początek argumentów) i przerzucamy wszystko ze stosu na wyjściowy, ale nawias otwierający zostaje na stosie
                SeparatorArgumentu e2 = (SeparatorArgumentu)el;
                while ((!stos.isEmpty()) && (!(stos.peek() instanceof Nawiasy))) {
                    newElements.add(stos.removeFirst());
                }
            } else if ((el instanceof Znak) || (el instanceof Funkcja)) {
                ElementWyrazenia e2 = el;
                if (stos.isEmpty()) stos.add(e2);
                else {
                    ElementWyrazenia zeStosu;
                    while ((!stos.isEmpty()) && (!(stos.peek() instanceof Nawiasy))) {
                        zeStosu = stos.removeFirst();
                        if (zeStosu.getPriority() < e2.getPriority()) {
                            stos.addFirst(zeStosu);
                            break;
                        } else newElements.add(zeStosu);
                    }
                    stos.addFirst(e2);
                }
            } else if (el instanceof NawiasyMacierzy) {
                NawiasyMacierzy e2 = (NawiasyMacierzy)el;
            }
        } //for
        while (!(stos.isEmpty())) {
            newElements.add(stos.removeFirst());
        }
        newElements.trimToSize();
        myElements = newElements;
    } //doConversion

    /**
     * Oblicza wartość wyrażenia, z którym został wywołany konstruktor.
     * @return Wynik obliczeń.
     */
    public ElementWyrazenia getValue() throws InvalidArgumentException, NumberFormatException {
        LinkedList<ElementWyrazenia> stos = new LinkedList<ElementWyrazenia>();
        for (ElementWyrazenia el : myElements) {
            //System.out.println(el.getClass().getSimpleName());
            if (el instanceof Liczba) {
                stos.addFirst(el);
            } else if (el instanceof WyrazeniePostfix) {
                stos.addFirst(el);
            } else if (el instanceof Macierz) {
                stos.addFirst(el);
            } else if (el instanceof Znak) {
                Znak e2 = (Znak)el;
                int argsc = e2.getArgumentsCount();
                if (stos.size()<argsc)
                    throw new InvalidArgumentException(e2.toString());
                ElementWyrazenia[] args = new ElementWyrazenia[argsc];
                for (int i = 0; i < argsc; i++)
                    args[argsc-1-i] = stos.removeFirst();
                stos.addFirst(e2.getValue(args));
            } else if (el instanceof Funkcja) {
                Funkcja e2 = (Funkcja)el;
                int argsc = e2.getArgumentsCount();
                if (stos.size()<argsc)
                    throw new InvalidArgumentException(e2.toString());
                ElementWyrazenia[] args = new ElementWyrazenia[argsc];
                for (int i = 0; i < argsc; i++)
                    args[argsc-1-i] = stos.removeFirst();
                stos.addFirst(e2.getValue(args));
            }
        }
        ElementWyrazenia result = stos.removeFirst();
        if ((myElements.size() == 1) && (result instanceof Macierz))
            result = ((Macierz)result).getValue((ElementWyrazenia[])null);
        return result;
    }

    public static ElementWyrazenia getValue(WyrazenieInfix infix) {
        ElementWyrazenia result;
        result = (new WyrazeniePostfix(infix)).getValue();
        return result;
    }
}
