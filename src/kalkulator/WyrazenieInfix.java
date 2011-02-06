/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

import java.util.*;

import walidacja.*;
//import jstat.*;

/**
 * Reprezentuje wyrażenie zapisane w notacji wrostkowej (infix notation).
 * @author Damian
 */
public class WyrazenieInfix extends WyrazenieAbstract {

    /**
     * Konstruktor tworzący obiekt <code>WyrazenieInfix</code>.
     * W celu przekształcenia tekstu na obiekt należy użyć funkcji <code>valueOf().</code>
     *
     * @param e tablica <code>ElementWyrazenia[]</code>
     * @param userArrayOfFunctions tablica <code>FunkcjaInterface[]</code>.
     * Lista funkcji dodanych przez użytkownika, np. fcja X przy rysowaniu wykresu.
     */
    public WyrazenieInfix(ElementWyrazenia[] e, FunkcjaInterface... userArrayOfFunctions) {
        super(e, userArrayOfFunctions);
    }

    /**
     * Konstruktor tworzący obiekt <code>WyrazenieInfix</code>.
     * W celu przekształcenia tekstu na obiekt należy użyć funkcji <code>valueOf().</code>
     *
     * @param e tablica <code>Collection<? extends ElementWyrazenia></code>
     * @param userArrayOfFunctions tablica <code>FunkcjaInterface[]</code>.
     * Lista funkcji dodanych przez użytkownika, np. fcja X przy rysowaniu wykresu.
     */
    public WyrazenieInfix(Collection<? extends ElementWyrazenia> e, FunkcjaInterface... userArrayOfFunctions)  {
        super(e, userArrayOfFunctions);
    }

    private static ElementWyrazenia zbadajLiczbeLubFunkcje(String s, FunkcjaInterface... userArrayOfFunctions) throws IllegalArgumentException, NumberFormatException {
        if ((s == null) || (s.equals(""))) return null;
        boolean czyFunkcja = false;
        s = s.trim();
        if (s.equals("")) return null;
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case '1': case '2': case '3': case '4': case '5':
                case '6': case '7': case '8': case '9': case '0':
                case '.': case ',': //sprawdzać locale?
                    break;
                default:
                    czyFunkcja = true;
            } //switch
            if (czyFunkcja) break;
        }
        ElementWyrazenia result;
        if (czyFunkcja) {
            result = new Funkcja(s, userArrayOfFunctions);
        } else {
            result = new Liczba(s);
        }
        return result;
    }

    /**
     * Zwraca obiekt WyrazenieInfix, który jest zgodny z podanym argumentem.
     * Preferowane jest jednak używanie metody <code>WyrazeniePostfix.valueOf()</code>,
     * która od razu zwraca obiekt odpowiedni do przeprowadzania obliczeń.
     * @param wyrazenie String z wyrażeniem infix (standardowa notacja).
     * @param userArrayOfFunctions tablica <code>FunkcjaInterface[]</code>.
     * Lista funkcji dodanych przez użytkownika, np. fcja X przy rysowaniu wykresu.
     * @return Obiekt WyrazenieInfix.
     * @throws IllegalArgumentException, NumberFormatException.
     */
    public static WyrazenieInfix valueOf(String wyrazenie, FunkcjaInterface... userArrayOfFunctions) throws IllegalArgumentException, NumberFormatException {
        if (Walidator.sprawdzNawiasy(wyrazenie) != 0) {
            throw new IllegalArgumentException("Nawiasy się nie zgadzają");
        }
        ArrayList<ElementWyrazenia> result = new ArrayList<ElementWyrazenia>(wyrazenie.length()); //najprawdopodobniej mniejszy

        ElementWyrazenia lastAdded = null;
        int startPos = 0; //numer pozycji w stringu wejściowym, od której kolejny element trzeba pobrać
        String s;
        int i;
        for (i=0; i<wyrazenie.length(); i++) {
            char ch = wyrazenie.charAt(i);
            switch (ch) {
                case '{':
                    s = wyrazenie.substring(startPos, i);
                    lastAdded = zbadajLiczbeLubFunkcje(s, userArrayOfFunctions);
                    if (lastAdded != null) result.add(lastAdded);
                    int pozycjaZamykajacego = Walidator.zamykajacyNawias(wyrazenie, i)+1;
                    if (pozycjaZamykajacego < 1) throw new NumberFormatException("Macierz rozpoczynająca się od pozycji "+i);
                    String calaMacierz = wyrazenie.substring(i, pozycjaZamykajacego);
                    int[] wymiar = Walidator.wymiarMacierzyStr(calaMacierz);
                    if (wymiar == null) throw new NumberFormatException(calaMacierz);
                    Macierz macierz = Macierz.valueOf(calaMacierz, userArrayOfFunctions);
                    if (macierz == null) throw new NumberFormatException(calaMacierz);
                    lastAdded = macierz;
                    result.add(lastAdded);
                    i = pozycjaZamykajacego-1;
                    startPos = i+1;
                    break;
                case '(': case ')':
                    s = wyrazenie.substring(startPos, i);
                    lastAdded = zbadajLiczbeLubFunkcje(s, userArrayOfFunctions);
                    if (lastAdded != null) {
                        result.add(lastAdded);
                        if ((lastAdded instanceof Funkcja) && (((Funkcja)lastAdded).getFunkcjaInterface() instanceof MetaFunkcjaInterface)) {
                            System.out.print("MetaFunkcjaInterface: "+lastAdded.toString());
                            int zamykajacy = walidacja.Walidator.zamykajacyNawias(wyrazenie, i);
                            String argumenty = wyrazenie.substring(i+1, zamykajacy);
                            Collection<String> argumentyTablica = Walidator.podzielArgumenty(argumenty);
                            System.out.println(", nawias: "+i+" i "+zamykajacy+" argumenty: "+argumenty+", jako tablica: "+argumentyTablica.toString());
                            //WyrazeniePostfix pArgumenty = WyrazeniePostfix.valueOf(argumenty, userArrayOfFunctions);
                            //System.out.println("postfix argumentów: "+pArgumenty);
                            WyrazeniePostfix[] argumentyTablicaPostfix = new WyrazeniePostfix[argumentyTablica.size()];
                            {
                                int p = 0;
                                for (String argument : argumentyTablica) {
                                    WyrazeniePostfix wp = WyrazeniePostfix.valueOf(argument, userArrayOfFunctions);
                                    result.add(wp);
                                    argumentyTablicaPostfix[p++] = wp;
                                }
                            }
                            //WyrazeniePostfix pArgumenty = WyrazeniePostfix.valueOf(argumenty, userArrayOfFunctions);
                            //System.out.println("postfix argumentów: "+Arrays.asList(argumentyTablicaPostfix));
                            //result.add(pArgumenty);
                            startPos = zamykajacy+1;
                            i = startPos-1;
                            break;
                        }
                    }
                    lastAdded = new Nawiasy(Character.toString(ch));
                    result.add(lastAdded);
                    startPos = i+1;
                    break;
                case ';':
                    s = wyrazenie.substring(startPos, i);
                    lastAdded = zbadajLiczbeLubFunkcje(s, userArrayOfFunctions);
                    if (lastAdded != null) result.add(lastAdded);
                    lastAdded = new SeparatorArgumentu(Character.toString(ch));
                    result.add(lastAdded);
                    startPos = i+1;
                    break;
                case '+': case '-': case '*': case '/': case '%': case '^': case '|':
                    s = wyrazenie.substring(startPos, i);
                    ElementWyrazenia prev = lastAdded;
                    lastAdded = zbadajLiczbeLubFunkcje(s, userArrayOfFunctions);
                    if (lastAdded != null) result.add(lastAdded);
                    if (
                            (ch=='-') &&
                            (
                            (result.size() == 0) ||
                            ((lastAdded == null) && (prev instanceof Nawiasy) && ((Nawiasy)prev).isOpening()) ||
                            ((lastAdded == null) && (prev instanceof SeparatorArgumentu))
                            )
                            ) {
                        result.add(new Liczba(0));
                        lastAdded = new Znak(Character.toString(ch));
                    } else {
                        lastAdded = new Znak(Character.toString(ch));
                    }
                    result.add(lastAdded);
                    startPos = i+1;
                    break;
                default:
                    break;
            } //switch
        } //for
        if (startPos<i) {
            s = wyrazenie.substring(startPos, i);
            lastAdded = zbadajLiczbeLubFunkcje(s, userArrayOfFunctions);
            if (lastAdded != null) result.add(lastAdded);
        }
        result.trimToSize();
        return (new WyrazenieInfix(result, userArrayOfFunctions));
    } //valueOf

}
