/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package walidacja;

import java.util.*;

/**
 *
 * @author Damian
 */
public class Walidator {

    /**
     * Zwraca 0 jeśli ok, inaczej złą pozycję, -coś jeśli brak zamykającego
     */
    public static int sprawdzNawiasy(String tekst) {
        LinkedList<Character> stos = new LinkedList<Character>();
        for (int i=0; i<tekst.length(); i++) {
            char ch = tekst.charAt(i);
            switch (ch) {
                case '{': case '[': case '(':
                    stos.addFirst(new Character(ch));
                    break;
                case '}': case ']': case ')':
                    if (!stos.isEmpty()) {
                        Character ostatni = stos.removeFirst();//.toChars(0)[0];
                        if (    (ch=='}' && ostatni!='{') ||
                                (ch==']' && ostatni!='[') ||
                                (ch==')' && ostatni!='(') ) {
                            return i;
                        } //if {}[]()
                    } //if (!stos.isEmpty())
                    else
                        return i;
                    break;
                default:
                    break;
            } //switch
        } //for
        return (stos.isEmpty() ? 0 : -stos.size());
    } //sprawdzNawiasy

    /**
     * Funkcja dzieli podany tekst według średników.
     * Zakłada "parzystość" nawiasów. Nie sprawdza ich poprawności!
     * @return tablica argumentów typu String, np. dla "1;2-1" zwraca dwuelementową tablicę
     */
    public static Collection<String> podzielArgumenty(String tekst) {
        int stosCount = 0, ostatniSrednik = 0;
        LinkedList<String> result = new LinkedList<String>();
        for (int i=0; i<tekst.length(); i++) {
            char ch = tekst.charAt(i);
            switch (ch) {
                case '{': case '[': case '(':
                    stosCount++;
                    break;
                case '}': case ']': case ')':
                    stosCount--;
                    break;
                case ';':
                    if (stosCount == 0) {
                        String wyciety = tekst.substring(ostatniSrednik, i);
                        result.add(wyciety);
                        ostatniSrednik = i+1;
                    }
                    break;
                default:
                    break;
            }
        }
        String wyciety = tekst.substring(ostatniSrednik, tekst.length());
        result.add(wyciety);
        return result;
    }

    /**
     * Zwraca pozycję zamykającego nawias z pozycji podanej w argumencie
     * jeśli result<=0 to błąd
     */
    public static int zamykajacyNawias(String tekst, int otwierajacyNawiasPos) {
        LinkedList<Character> stos = new LinkedList<Character>();
        for (int i=otwierajacyNawiasPos; i<tekst.length(); i++) {
            char ch = tekst.charAt(i);
            switch (ch) {
                case '{': case '[': case '(':
                    stos.addFirst(new Character(ch));
                    break;
                case '}': case ']': case ')':
                    if (!stos.isEmpty()) {
                        Character ostatni = stos.removeFirst();//.toChars(0)[0];
                        if (    (ch=='}' && ostatni!='{') ||
                                (ch==']' && ostatni!='[') ||
                                (ch==')' && ostatni!='(') ) {
                            return -i;
                        } //if {}[]()
                    } //if (!stos.isEmpty())
                    else
                        return -i;
                    break;
                default:
                    break;
            } //switch
            if (stos.isEmpty())
                return i;
        } //for
        return (stos.isEmpty() ? 0 : -stos.size());
    } //zamykajacyNawias

    /**
     * Zwraca rozmiar macierzy ze stringu typu {{a;b}{dd;e}}
     * array: ilość kolumn, ilość wierszy (x, y)
     */
    public static int[] wymiarMacierzyStr(String macierz) {
        final int wymiarX = 0;
        final int wymiarY = 1;
        if (sprawdzNawiasy(macierz) != 0) {
            return null;
        }
        LinkedList<Character> stos = new LinkedList<Character>();
        Character ostatni;
        int[] result = new int[2];
        int[] resultPreverious = new int[2];
        boolean inside = false; //określa, czy aktualnie szukamy średników
        int startPos = 0; //numer pozycji w stringu wejściowym, od której komórkę macierzy trzeba pobrać
        result[wymiarX] = 0;
        result[wymiarY] = 0;
        resultPreverious[wymiarX] = -1;
        resultPreverious[wymiarY] = -1;
        for (int i=0; i<macierz.length(); i++) {
            inside = (stos.size() == 2) && ((Character)stos.getFirst() == '{') && ((Character)stos.getLast() == '{');
            char ch = macierz.charAt(i);
            switch (ch) {
                case '{':
                    stos.addFirst(new Character(ch));
                    break;
                case '[': case '(':
                    stos.addFirst(new Character(ch));
                    break;
                case ';':
                    if (inside) {
                        result[wymiarX]++;
                    }
                    break;
                case '}':
                    if (stos.isEmpty())
                        return null;
                    ostatni = (stos.removeFirst());
                    if ((ostatni == '{') && (stos.size() == 1) && ((Character)stos.getFirst() == '{')) {
                        result[wymiarY]++;
                    } else if (ostatni != '{') {
                        return null;
                    }
                    if (inside && (stos.size() == 1)) {
                        result[wymiarX]++;
                        inside = false;
                        //sprawdzamy, czy w poprzednim wierszu macierzy było tyle samo kolumn co w bieżącym:
                        if ((resultPreverious[wymiarX] > -1) && (resultPreverious[wymiarX] != result[wymiarX]))
                            return null;
                        System.arraycopy(result, 0, resultPreverious, 0, result.length);
                        result[wymiarX] = 0;
                    }
                    break;
                case ']': case ')':
                    if (!stos.isEmpty()) {
                        ostatni = (stos.removeFirst());
                        if (    (ch==']' && ostatni!='[') ||
                                (ch==')' && ostatni!='(') ) {
                            return null;
                        } //if []()
                    } //if (!stos.isEmpty())
                    else
                        return null;
                    break;
                default:
                    break;
            } //switch
            //na każdym etapie, poza końcowym, musi być otwierający nawias { na dole stosu:
            if ((i<macierz.length()-1) && (stos.size()==0))
                return null;
        } //for
        if (resultPreverious[wymiarX] >= 0) {
            result[wymiarX] = resultPreverious[wymiarX]; //bo result się zeruje gdy '}'
        }
        return (((result[wymiarX]>0) && (result[wymiarY]>0)) ? result : null);
    }

} //class
