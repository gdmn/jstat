/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import kalkulator.Funkcja.Functions;
import walidacja.*;

/**
 *
 * @author Damian
 */
public class Macierz extends ElementWyrazenia {
    /** pierwszy wymiar to kolumny, drugi wiersze. Np. [1][3] to wektor (pionowy). */
    private WyrazenieInfix[][] matrix;
    /** dla szybszych przeliczeń */
    private WyrazeniePostfix[][] matrixPostfix;
    public static final int WYMIARX = 0;
    public static final int WYMIARY = 1;

    /**
     * Tworzy Macierz
     * TODO: zamienione wiersze z kolumnami są, trzeba napisać odwrotnie i sprawdzić, czy nigdzie nie będzie kolidowało
     * @param matrix dwuwymiarowa tablica elementów
     */
    public Macierz(WyrazenieInfix[][] matrix) {
        super("");
        this.matrix = matrix;
        this.matrixPostfix = new WyrazeniePostfix[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != null) {
                    this.matrixPostfix[i][j] = new WyrazeniePostfix(matrix[i][j]);
                }
            }
        }
        stringRepresentation = null;//toString();

    }

    /**
     * Tworzy Macierz
     * @param matrix dwuwymiarowa tablica elementów
     */
    public Macierz(double[][] matrixDouble) {
        super("");
        matrix = new WyrazenieInfix[matrixDouble[0].length][matrixDouble.length];
        matrixPostfix = new WyrazeniePostfix[matrixDouble[0].length][matrixDouble.length];
        for (int i = 0; i < matrixDouble.length; i++) {
            for (int j = 0; j < matrixDouble[0].length; j++) {
                matrixPostfix[j][i] = new WyrazeniePostfix(new ElementWyrazenia[]{new Liczba(matrixDouble[i][j])});
                matrix[j][i] = new WyrazenieInfix(new ElementWyrazenia[]{new Liczba(matrixDouble[i][j])});
            }
        }
        stringRepresentation = null;//toString();

    }

    /**
     * Tworzy Macierz
     * @param matrix dwuwymiarowa tablica elementów
     */
    public Macierz(Liczba[][] matrixLiczba) {
        super("");
        matrix = new WyrazenieInfix[matrixLiczba[0].length][matrixLiczba.length];
        matrixPostfix = new WyrazeniePostfix[matrixLiczba[0].length][matrixLiczba.length];
        for (int i = 0; i < matrixLiczba.length; i++) {
            for (int j = 0; j < matrixLiczba[0].length; j++) {
                matrixPostfix[j][i] = new WyrazeniePostfix(new ElementWyrazenia[]{matrixLiczba[i][j]});
                matrix[j][i] = new WyrazenieInfix(new ElementWyrazenia[]{matrixLiczba[i][j]});
            }
        }
        stringRepresentation = null;//toString();

    }

    /**
     * Tworzy macierz.
     * @param x ilość kolumn
     * @param y ilość wierszy
     */
    public Macierz(int x, int y) {
        this(new WyrazenieInfix[x][y]);
    }

    /**
     * Tworzy macierz ze String.
     * @param macierz
     */
    public Macierz(String macierz) {
        this(macierz, true, null);
    }


    /**
     * Tworzy macierz ze String.
     * @param macierz
     * @param obliczElementy jeśli <code>true</code> oblicza każdą komórkę macierzy, np. "3-2,3" zamienia na "0,7".
     * Taki sam efekt, jakby na utworzonej macierzy wywołać metodę <code>getValue()</code>.
     * @param userArrayOfFunctions tablica funkcji użytkownika.
     */
    public Macierz(String macierz, boolean obliczElementy, FunkcjaInterface[] userArrayOfFunctions) throws InvalidArgumentException {
        super("");
        //tutaj sprawdzamy macierz (sprawdzNawiasy i wymiarMacierzyStr), potem pomijamy błędy nawiasów itp.:
        if (Walidator.sprawdzNawiasy(macierz) != 0) {
            throw new InvalidArgumentException();
        }
        int[] wymiar = Walidator.wymiarMacierzyStr(macierz);
        if (wymiar == null) {
            throw new InvalidArgumentException();
        }
        LinkedList<Character> stos = new LinkedList<Character>();
        Character ostatni;
        matrix = new WyrazenieInfix[(wymiar[0])][(wymiar[1])];
        int iX = 0, //iterator po pierwszym wymiarze macierzy
                iY = 0; //po drugim

        boolean inside = false; //określa, czy aktualnie szukamy średników (czyli czy przekształcamy wiersze macierzy)

        int startPos = 0; //numer pozycji w stringu wejściowym, od której komórkę macierzy trzeba pobrać

        for (int i = 0; i < macierz.length(); i++) {
            inside = (stos.size() == 2) && ((Character) stos.getFirst() == '{') && ((Character) stos.getLast() == '{');
            char ch = macierz.charAt(i);
            switch (ch) {
                case '{':
                    //inside = (stos.size() == 1) && ((Character)stos.getFirst() == '{'); if (inside) startPos = i+1;
                    if ((stos.size() == 1) && ((Character) stos.getFirst() == '{')) {
                        startPos = i + 1;
                    }
                    stos.addFirst(new Character(ch));
                    break;
                case '[':
                case '(':
                    stos.addFirst(new Character(ch));
                    break;
                case ';':
                    if (inside) {
                        //result[iX++][iY] = Double.valueOf(macierz.substring(startPos, i));
                        WyrazenieInfix infix = WyrazenieInfix.valueOf(macierz.substring(startPos, i), userArrayOfFunctions);
                        if (obliczElementy) {
                            WyrazeniePostfix postfix = new WyrazeniePostfix(infix);
                            ElementWyrazenia val = postfix.getValue();
                            if (!(val instanceof Liczba)) {
                                throw new InvalidArgumentException("macierz - jeden z elementów nie jest liczbą");
                            }
                            infix = new WyrazenieInfix(new ElementWyrazenia[]{val});
                        }
                        matrix[iX++][iY] = infix;
                        startPos = i + 1;
                    }
                    break;
                case '}':
                    ostatni = stos.removeFirst();
                    if ((ostatni == '{') && (stos.size() == 1) && ((Character) stos.getFirst() == '{')) {
                        //result[iX++][iY] = Double.valueOf(macierz.substring(startPos, i));
                        WyrazenieInfix infix = WyrazenieInfix.valueOf(macierz.substring(startPos, i), userArrayOfFunctions);
                        if (obliczElementy) {
                            WyrazeniePostfix postfix = new WyrazeniePostfix(infix);
                            ElementWyrazenia val = postfix.getValue();
                            if (!(val instanceof Liczba)) {
                                throw new InvalidArgumentException("macierz - jeden z elementów nie jest liczbą");
                            }
                            infix = new WyrazenieInfix(new ElementWyrazenia[]{val});
                        }
                        matrix[iX++][iY] = infix;
                    }
                    if (inside && (stos.size() == 1)) {
                        iY++;
                        iX = 0;
                        inside = false;
                    }
                    break;
                case ']':
                case ')':
                    ostatni = stos.removeFirst();
                    break;
                default:
                    break;
            } //switch

        } //for

        this.matrixPostfix = new WyrazeniePostfix[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != null) {
                    this.matrixPostfix[i][j] = new WyrazeniePostfix(matrix[i][j]);
                }
            }
        }
        stringRepresentation = null;//toString();

    }

    /**
     * Tworzy wektor transponowany (poziomy).
     * @param x elementy wektora
     * @return wektor transponowany
     */
    public static Macierz createX(WyrazenieInfix... x) {
        WyrazenieInfix[][] r = new WyrazenieInfix[x.length][1];
        for (int i = 0; i < x.length; i++) {
            r[i][0] = x[i];
        }
        return new Macierz(r);
    }

    /**
     * Tworzy wektor (pionowy).
     * @param x elementy wektora
     * @return wektor
     */
    public static Macierz createY(WyrazenieInfix... x) {
        WyrazenieInfix[][] r = new WyrazenieInfix[1][x.length];
        for (int i = 0; i < x.length; i++) {
            r[0][i] = x[i];
        }
        return new Macierz(r);
    }

    /**
     * Tworzy wektor transponowany (poziomy).
     * @param x elementy wektora
     * @return wektor transponowany
     */
    public static Macierz createX(Liczba... x) {
        WyrazenieInfix[][] r = new WyrazenieInfix[x.length][1];
        for (int i = 0; i < x.length; i++) {
            r[i][0] = new WyrazenieInfix(new ElementWyrazenia[]{x[i]});
        }
        return new Macierz(r);
    }

    /**
     * Tworzy wektor (pionowy).
     * @param x elementy wektora
     * @return wektor
     */
    public static Macierz createY(Liczba... x) {
        WyrazenieInfix[][] r = new WyrazenieInfix[1][x.length];
        for (int i = 0; i < x.length; i++) {
            r[0][i] = new WyrazenieInfix(new ElementWyrazenia[]{x[i]});
        }
        return new Macierz(r);
    }

    /**
     * Zwraca ilość kolumn macierzy
     */
    public int getLengthX() {
        return matrix.length;
    }

    /**
     * Zwraca ilość wierszy macierzy
     */
    public int getLengthY() {
        return ((matrix.length > 0) ? (matrix[0].length) : 0);
    }

    /**
     * Zwraca element macierzy na pozycji X,Y.
     * @param x numer kolumny,
     * @param y numer wiersza.
     * @return WyrazenieInfix.
     */
    public WyrazenieInfix get(int x, int y) {
        return matrix[x][y];
    }

    /**
     * Zwraca przechowywane elemnty macierzy
     * @return tablica dwuwymiarowa.
     */
    public WyrazenieInfix[][] getMatrix() {
        return matrix;
    }

    /**
     * Zwraca element macierzy na pozycji X,Y po przeliczeniu (rozwinięciu wszystkich funkcji itp.).
     * @param x numer kolumny,
     * @param y numer wiersza.
     * @return Liczba, bo element macierzy nie może być macierzą. Zwraca null, jeśli wynik obliczeń nie jest liczbą.
     */
    public Liczba getValueAt(int x, int y) {
        ElementWyrazenia result;
        try {
            result = matrixPostfix[x][y].getValue();
        } catch (InvalidArgumentException ex) {
            return null;
        } catch (NullPointerException ex) {
            return null;
        }
        return (result instanceof Liczba) ? (Liczba) result : null;
    }

    /**
     * Zwraca macierz po przeliczeniu formuły w każdej komórce (rozwinięciu wszystkich funkcji itp.).
     * @return ElementWyrazenia, zawsze Macierz o takich wymiarach jak obiekt z którego metoda zostala wywolana.
     */
    public Macierz getValue() throws InvalidArgumentException {
        Macierz result;
        result = new Macierz(new WyrazenieInfix[this.getLengthX()][this.getLengthY()]);
        for (int x = 0; x < result.getLengthX(); x++) {
            for (int y = 0; y < result.getLengthY(); y++) {
                ElementWyrazenia l = this.getValueAt(x, y);
                if (!(l instanceof Liczba)) {
                    throw new InvalidArgumentException("macierz - jeden z elementów nie jest liczbą");
                }
                result.set(x, y, (Liczba) l);
            }
        }
        return result;
    }

    /**
     * Zwraca macierz po przeliczeniu formuły w każdej komórce (rozwinięciu wszystkich funkcji itp.).
     * @param args pomijane.
     * @return ElementWyrazenia, zawsze Macierz o takich wymiarach jak obiekt z którego metoda zostala wywolana.
     */
    public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
        //throw new InvalidArgumentException("Metoda ElementWyrazenia getValue(ElementWyrazenia[] args) jest niepoprawna dla klasy "+this.getClass().getName());
        return getValue();
    }

    /**
     * Ustawia element macierzy na pozycji X,Y.
     * @param x numer kolumny,
     * @param y numer wiersza,
     * @param val element do wstawienia.
     */
    public void set(int x, int y, WyrazenieInfix val) {
        matrix[x][y] = val;
        matrixPostfix[x][y] = new WyrazeniePostfix(val);
    }

    /**
     * Ustawia liczbę do macierzy na pozycji X,Y.
     * <code>userArrayOfFunctions = null</code>, bo to tylko liczba.
     * @param x numer kolumny,
     * @param y numer wiersza,
     * @param val liczba do wstawienia.
     */
    public void set(int x, int y, Liczba val) {
        if (val != null) {
            matrix[x][y] = new WyrazenieInfix(new ElementWyrazenia[]{val});
            matrixPostfix[x][y] = new WyrazeniePostfix(matrix[x][y]);
        } else {
            matrix[x][y] = null;
            matrixPostfix[x][y] = null;
        }
    }

    /**
     * Wypełnia każdą komórkę macierzy.
     * @param val Liczba, jaka ma się znaleźć w każdej komórce.
     */
    public void fill(Liczba val) {
        for (int i = 0; i < getLengthX(); i++) {
            for (int j = 0; j < getLengthY(); j++) {
                set(i, j, val);
            }
        }
    }

    /**
     * Przekształca macierz na String typu {{a;b}{c;e}}
     */
    public String toString() {
        if ((getLengthX() == 0) || (getLengthY() == 0)) {
            return null;
        }
        StringBuilder result = new StringBuilder("{");
        for (int x = 0; x < matrix[0].length; x++) {
            result.append("{");
            for (int y = 0; y < matrix.length; y++) {
                if (y > 0) {
                    result.append(";");
                }
                //result.append(Double.toString(matrix[y][x]));
                //wycinamy skrajne "[" oraz "]":
                if (matrix[y][x] != null) {
                    String s = matrix[y][x].toString();
                    if (s.length() > 2) {
                        result.append(s.substring(1, s.length() - 1));
                    } else {
                        result.append(s);
                    }
                } else {
                    result.append("null");
                }
            }
            result.append("}");
        }
        return result.append("}").toString();
    }

    /**
     * przekształca String na Macierz
     */
    public static Macierz valueOf(String macierz) throws IllegalArgumentException {
        return new Macierz(macierz);
    }

    /**
     * przekształca String na Macierz
     */
    public static Macierz valueOf(String macierz, FunkcjaInterface[] userArrayOfFunctions) throws IllegalArgumentException {
        return new Macierz(macierz, true, userArrayOfFunctions);
    }

    public int getArgumentsCount() {
        return 0;
    }

    /**
     * Stosuje funkcję do każdego elementu macierzy.
     * @param f funkcja do zastosowania
     * @param m macierz
     * @return macierz po zastosowaniu funkcji
     */
    public static Macierz getValueApplyFunction1(Macierz m, FunkcjaInterface f) throws InvalidArgumentException {
        Macierz result = new Macierz(new WyrazenieInfix[m.getLengthX()][m.getLengthY()]);
        for (int x = 0; x < m.getLengthX(); x++) {
            for (int y = 0; y < m.getLengthY(); y++) {
                ElementWyrazenia w = m.getValueAt(x, y);
                //w = f.getValue(new ElementWyrazenia[]{w});
                w = f.getValue(w);
                result.set(x, y, (Liczba) w);
            }
        }
        return result;
    }

    public static Macierz getValueApplyFunction1(Macierz m, Funkcja f) throws InvalidArgumentException {
        Macierz result = new Macierz(new WyrazenieInfix[m.getLengthX()][m.getLengthY()]);
        for (int x = 0; x < m.getLengthX(); x++) {
            for (int y = 0; y < m.getLengthY(); y++) {
                ElementWyrazenia w = m.getValueAt(x, y);
                w = f.getValue(w);
                result.set(x, y, (Liczba) w);
            }
        }
        return result;
    }

    /**
     * Zwraca macierz jednostkową wymiaru (n) na (n)
     * @param n wymiar
     * @return macierz jednostkowa
     */
    public static Macierz macierzJednostkowa(int n) throws InvalidArgumentException {
        if (n < 1) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy jednostkowej");
        }
        Macierz result = new Macierz(new WyrazenieInfix[n][n]);
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                result.set(x, y, new Liczba(
                        ((x != y) ? 0 : 1)));
            }
        }
        return result;
    }

    /**
     * Zwraca macierz Hilberta wymiaru (n) na (n)
     * @param n stopień macierzy
     * @return macierz Hilberta
     */
    public static Macierz macierzHilberta(int n) throws InvalidArgumentException {
        if (n < 1) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy Hilberta");
        }
        Macierz result = new Macierz(new WyrazenieInfix[n][n]);
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                result.set(x, y, new Liczba(
                        //1d*ww/(x+y+1)
                        (new BigDecimal(1)).divide(new BigDecimal(x + y + 1), Settings.divideContext())));
            }
        }
        return result;
    }

    /**
     * Oblicza macierz Grama, czyli G = transpozycja(A)*A.
     * Skąd to jest?
     * @param macierz macierz A
     * @return macierz G
     */
    public static Macierz macierzGrama(Macierz macierz) throws InvalidArgumentException {
        if (!Macierz.isOk(macierz)) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = (Macierz) Znak.pomnozElementy(transpozycja(macierz), macierz);
        return result;
    }

    /**
     * Zwraca podmacierz z macierzy powstałą przez skreślenie kolumny i wiersza.
     * @param macierz macierz wyjściowa
     * @param xdel numer kolumny (pierwsza to 0)
     * @param ydel numer wiersza (pierwszy to 0)
     * @return podmacierz
     */
    public static Macierz podmacierz(Macierz macierz, int xdel, int ydel) throws InvalidArgumentException {
        if ((!Macierz.isOk(macierz)) || (macierz.getLengthX() < 2) || (macierz.getLengthY() < 2) || (macierz.getLengthX() <= xdel) || (macierz.getLengthY() <= ydel)) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy, nie można usunąć kolumny " + xdel + " i wiersza " + ydel);
        }
        Macierz result = new Macierz(new WyrazenieInfix[macierz.getLengthX() - 1][macierz.getLengthY() - 1]);
        for (int xi = 0; xi < result.getLengthX(); xi++) {
            for (int yi = 0; yi < result.getLengthY(); yi++) {
                result.set(xi, yi, macierz.get((xi >= xdel) ? xi + 1 : xi, (yi >= ydel) ? yi + 1 : yi));
            }
        }
        return result;
    }

    /**
     * Zamienia miejscami kolumny w macierzy
     * @return macierz z zamienionymi kolumnami
     */
    public static Macierz zamienKolumny(Macierz macierz, int x1, int x2) throws InvalidArgumentException {
        if ((!Macierz.isOk(macierz)) || (macierz.getLengthX() <= x1) || (macierz.getLengthX() <= x2)) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy, nie można zamienić kolumn " + x1 + " i " + x2);
        }
        Macierz result = new Macierz(new WyrazenieInfix[macierz.getLengthX()][macierz.getLengthY()]);
        for (int xi = 0; xi < result.getLengthX(); xi++) {
            for (int yi = 0; yi < result.getLengthY(); yi++) {
                int xdocelowy;
                if (xi == x1) {
                    xdocelowy = x2;
                } else if (xi == x2) {
                    xdocelowy = x1;
                } else {
                    xdocelowy = xi;
                }
                result.set(xi, yi, macierz.get(xdocelowy, yi));
            }
        }
        return result;
    }

    /**
     * Pobiera kolumny z macierzy
     * @param macierz macierz
     * @param x1 kolumny do pobrania
     * @return macierz uworzona z wybranych kolumn
     */
    public static Macierz pobierzKolumny(Macierz macierz, int... x1) throws InvalidArgumentException {
        final String blad = "Nieprawidłowy wymiar macierzy";
        if (!Macierz.isOk(macierz)) {
            throw new InvalidArgumentException(blad);
        }
        Macierz result = new Macierz(new WyrazenieInfix[x1.length][macierz.getLengthY()]);
        for (int xcounter = 0; xcounter < x1.length; xcounter++) {
            int xi = x1[xcounter];
            if (xi >= macierz.getLengthX()) {
                throw new InvalidArgumentException(blad + ", brak kolumny " + xi);
            }
            for (int yi = 0; yi < result.getLengthY(); yi++) {
                result.set(xcounter, yi, macierz.get(xi, yi));
            }
        }
        return result;
    }

    /**
     * Pobiera wiersze z macierzy
     * @param macierz macierz
     * @param y1 kolumny do pobrania
     * @return macierz uworzona z wybranych wierszy
     */
    public static Macierz pobierzWiersze(Macierz macierz, int... y1) throws InvalidArgumentException {
        final String blad = "Nieprawidłowy wymiar macierzy";
        if (!Macierz.isOk(macierz)) {
            throw new InvalidArgumentException(blad);
        }
        Macierz result = new Macierz(macierz.getLengthX(), y1.length);
        for (int ycounter = 0; ycounter < y1.length; ycounter++) {
            int yi = y1[ycounter];
            if (yi >= macierz.getLengthY()) {
                throw new InvalidArgumentException(blad + ", brak wiersza " + yi);
            }
            for (int xi = 0; xi < result.getLengthY(); xi++) {
                result.set(xi, ycounter, macierz.get(xi, yi));
            }
        }
        return result;
    }

    /**
     * Pobiera część macierzy.
     * @param macierz macierz źródłowa
     * @param x1 numer kolumny początkowej (numerowanie zaczyna się od 0)
     * @param y1 numer wiersza początkowego (numerowanie zaczyna się od 0)
     * @param lenx ilość kolumn, jaka ma zostać pobrana
     * @param leny ilość wierszy, jaka ma zostać pobrana
     * @return zwraca macierz o wymarach <code>lenx</code> na <code>leny</code>.
     * @throws kalkulator.InvalidArgumentException
     */
    public static Macierz pobierz(Macierz macierz, int x1, int y1, int lenx, int leny) throws InvalidArgumentException {
        final String blad = "Nieprawidłowy wymiar macierzy lub pozycji do pobrania";
        if (!Macierz.isOk(macierz) ||
                macierz.getLengthX() < x1 + lenx ||
                macierz.getLengthY() < y1 + leny ||
                leny < 1 || lenx < 1) {
            throw new InvalidArgumentException(blad);
        }
        Macierz result = new Macierz(lenx, leny);
        for (int cy = 0; cy < leny; cy++) {
            for (int cx = 0; cx < lenx; cx++) {
                result.set(cx, cy, macierz.getValueAt(cx + x1, cy + y1));
            }
        }
        return result;
    }

    /**
     * Wkleja komórki z jednej macierzy do drugiej.
     * @param dst macierz, do której mają zostać wklejone komórki
     * @param src macierz, która ma zostać wklejona
     * @param x1 numer kolumny macierzy <code>dst</code>, od której
     * rozpocząć wklejanie macierzy <code>src</code>
     * @param y1 numer wiersza macierzy <code>dst</code>, od którego
     * rozpocząć wklejanie macierzy <code>src</code>
     * @return macierz powstała przez wklejenie macierzy <code>src</code>
     * do <code>dst</code>. Wymiary są takie same jak macierzy <code>dst</code>.
     * @throws kalkulator.InvalidArgumentException
     */
    public static Macierz wklej(Macierz dst, Macierz src, int x1, int y1) throws InvalidArgumentException {
        final String blad = "Nieprawidłowy wymiar macierzy";
        if (!Macierz.isOk(dst, src) ||
                dst.getLengthX() < x1 + src.getLengthX() ||
                dst.getLengthY() < y1 + src.getLengthY()) {
            throw new InvalidArgumentException(blad);
        }
        Macierz result = new Macierz(dst.getLengthX(), dst.getLengthY());
        for (int cy = 0; cy < dst.getLengthY(); cy++) {
            for (int cx = 0; cx < dst.getLengthX(); cx++) {
                if (cx >= x1 && cy >= y1 && cx < src.getLengthX() + x1 && cy < src.getLengthY() + y1) {
                    result.set(cx, cy, src.getValueAt(cx - x1, cy - y1));
                } else {
                    //System.out.println("xx "+cx+"X"+cy);
                    result.set(cx, cy, dst.getValueAt(cx, cy));
                }
            }
        }
        return result;
    }

    /**
     * Zamienia miejscami wiersze w macierzy
     * @return macierz z zamienionymi kolumnami
     */
    public static Macierz zamienWiersze(Macierz macierz, int y1, int y2) throws InvalidArgumentException {
        if ((!Macierz.isOk(macierz)) || (macierz.getLengthY() <= y1) || (macierz.getLengthY() <= y2)) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy, nie można zamienić kolumn " + y1 + " i " + y2);
        }
        Macierz result = new Macierz(new WyrazenieInfix[macierz.getLengthX()][macierz.getLengthY()]);
        for (int xi = 0; xi < result.getLengthX(); xi++) {
            for (int yi = 0; yi < result.getLengthY(); yi++) {
                int ydocelowy;
                if (yi == y1) {
                    ydocelowy = y2;
                } else if (yi == y2) {
                    ydocelowy = y1;
                } else {
                    ydocelowy = yi;
                }
                result.set(xi, yi, macierz.get(xi, ydocelowy));
            }
        }
        return result;
    }

    /**
     * Zamienia miejscami wiersze z kolumnami w macierzy
     * @return macierz transponowana
     */
    public static Macierz transpozycja(Macierz macierz) throws InvalidArgumentException {
        //TODO: (macierz.getLengthY() < 1) || (macierz.getLengthY() < 1)) ??
        if ((!Macierz.isOk(macierz)) || (macierz.getLengthY() < 1) || (macierz.getLengthY() < 1)) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(new WyrazenieInfix[macierz.getLengthY()][macierz.getLengthX()]);
        for (int xi = 0; xi < result.getLengthX(); xi++) {
            for (int yi = 0; yi < result.getLengthY(); yi++) {
                result.set(xi, yi, macierz.getValueAt(yi, xi));
            }
        }
        return result;
    }

    /**
     * Wyrzuca wyjątek jeśli niekwadratowa lub za mała
     */
    private static void wyznacznik_sprawdzWymiarMacierzy(int x, int y) throws InvalidArgumentException {
        if ((x == 0) || (y == 0)) {
            throw new InvalidArgumentException("Nie można policzyć wyznacznika pustej macierzy");
        }
        if (x != y) {
            throw new InvalidArgumentException("Nie można policzyć wyznacznika niekwadratowej macierzy");
        }
    }

    /**
     * Oblicza wyznacznik stosując rozwinięcia Laplace'a dla macierzy o stopniu wyższym niż 2.
     * @param macierz macierz z której liczony jest wyznacznik
     * @return wyznacznik
     */
    public static Liczba wyznacznikLaplace(Macierz macierz) throws InvalidArgumentException {
        /*
         *det(hilbert(7)*360360)=
         * =powinno 381614277072600, tutaj: 3.8161|7159211008E14 (przed zastosowaniem BigDecimal)
         */
        //Macierz m = (Macierz) macierz.getValue((ElementWyrazenia[])null);
        int lx = macierz.getLengthX(), ly = macierz.getLengthY();
        wyznacznik_sprawdzWymiarMacierzy(lx, ly);
        if (lx == 1) //już wiadomo, że kwadratowa
        {
            return macierz.getValueAt(0, 0);
        }
        if (lx == 2) {
            BigDecimal wynik = macierz.getValueAt(0, 0).getValueAsBigDecimal().multiply(macierz.getValueAt(1, 1).getValueAsBigDecimal()).subtract(
                    macierz.getValueAt(0, 1).getValueAsBigDecimal().multiply(macierz.getValueAt(1, 0).getValueAsBigDecimal()));
            return new Liczba(wynik);
        }
        //rozwinięcie względem pierwszego wiersza:
        BigDecimal wynik = new BigDecimal(0d), wynikczesciowy;
        for (int x = 0; x < macierz.getLengthX(); x++) {
            wynikczesciowy = wyznacznikLaplace(Macierz.podmacierz(macierz, x, 0)).getValueAsBigDecimal();
            wynikczesciowy = wynikczesciowy.multiply(macierz.getValueAt(x, 0).getValueAsBigDecimal());
            wynik = wynik.add(wynikczesciowy.multiply(Liczba.bd(Math.pow(-1, x + 2))));
        }
        //System.out.println("det( "+macierz.toString() +") = "+wynik);
        return new Liczba(wynik);
    }

    /**
     * Oblicza wyznacznik stosując metodę eliminacji Gaussa.
     * @param macierz macierz, z której liczony jest wyznacznik
     * @return wyznacznik.
     */
    public static Liczba wyznacznikGauss(Macierz macierz) throws InvalidArgumentException {
        int lx = macierz.getLengthX(), ly = macierz.getLengthY();
        wyznacznik_sprawdzWymiarMacierzy(lx, ly);
        if (lx == 1) //już wiadomo, że kwadratowa
        {
            return macierz.getValueAt(0, 0);
        }
        if (lx == 2) {
            BigDecimal wynik = macierz.getValueAt(0, 0).getValueAsBigDecimal().multiply(macierz.getValueAt(1, 1).getValueAsBigDecimal()).subtract(
                    macierz.getValueAt(0, 1).getValueAsBigDecimal().multiply(macierz.getValueAt(1, 0).getValueAsBigDecimal()));
            return new Liczba(wynik);
        }
        BigDecimal glownaprzekatna; //kolejne elementy z głównej przekątnej

        BigDecimal dziel;
        BigDecimal biezacyelement;
        BigDecimal wynik = new BigDecimal(1); //przy przestawianiu wierszy lub kolumn znak zmienia się na przeciwny

        Macierz m = (Macierz) macierz.getValue((ElementWyrazenia[]) null);
        for (int x = 0; x < m.getLengthX(); x++) { //po kolumnach

            glownaprzekatna = m.getValueAt(x, x).getValueAsBigDecimal(); //element na głównej przekątnej

            if (glownaprzekatna.compareTo(Liczba.bd(0)) == 0) {
                //System.out.println("glownaprzekatna == 0 (1)");
                //trzeba znaleźć inny w bieżącym wierszu i zamienić wiersze
                for (int szukanyy = x + 1; szukanyy < m.getLengthY(); szukanyy++) { //szukamy w innym wierszu

                    glownaprzekatna = m.getValueAt(x, szukanyy).getValueAsBigDecimal();
                    if (glownaprzekatna.compareTo(Liczba.bd(0)) != 0) {
                        m = Macierz.zamienWiersze(m, x, szukanyy);
                        wynik = wynik.negate();
                        break;
                    }
                }
                if (glownaprzekatna.compareTo(Liczba.bd(0)) == 0) {
                    //System.out.println("glownaprzekatna == 0 (2)");
                    //trzeba znaleźć inny w bieżącej kolumnie i zamienić kolumny
                    for (int szukanyx = x + 1; szukanyx < m.getLengthX(); szukanyx++) { //szukamy w innej kolumnie

                        glownaprzekatna = m.getValueAt(szukanyx, x).getValueAsBigDecimal();
                        if (glownaprzekatna.compareTo(Liczba.bd(0)) != 0) {
                            m = Macierz.zamienKolumny(m, x, szukanyx);
                            wynik = wynik.negate();
                            break;
                        }
                    }
                }
                if (glownaprzekatna.compareTo(Liczba.bd(0)) == 0) {
                    //System.out.println("glownaprzekatna == 0 (3)");
                    return new Liczba(0);
                }
            }
            for (int y = x + 1; y < m.getLengthY(); y++) { //po wierszach

                biezacyelement = m.getValueAt(x, y).getValueAsBigDecimal();
                dziel = biezacyelement.divide(glownaprzekatna, Settings.divideContext());
                if (dziel.compareTo(Liczba.bd(0)) != 0) {
                    for (int i = 0; i < m.getLengthX(); i++) { //odejmij od wiersza poniżej bieżąco rozpatrywanego odpowiednio pomnożony wiersz bieżący, aby otrzymać 0 w dolnym trójkącie macierzy
                        //biezacywiersz[i] = m.getValueAt(i, x).getValue() * dziel;

                        m.set(i, y, new Liczba(
                                m.getValueAt(i, y).getValueAsBigDecimal().subtract(
                                m.getValueAt(i, x).getValueAsBigDecimal().multiply(dziel))));
                    }
                } //if dziel <> 0

            } //for y

        } //for x

        for (int x = 0; x < m.getLengthX(); x++) { //iloczyn elementów z głównej przekątnej

            wynik = wynik.multiply(m.getValueAt(x, x).getValueAsBigDecimal());
        }
        return new Liczba(wynik);
    }

    /**
     * Znajduje pozycję w macierzy równą parametrowi.
     * @param macierz macierz do przeszukania
     * @param value wartość do znalezienia
     * @return <code>Point</code>, gdzie <code>x</code> to indeks kolumny,
     * a <code>y</code> indeks wiersza. Może być <code>null</code>, jeśli
     * nie znaleziono
     */
    public static Point znajdzWartosc(Macierz macierz, Liczba value) {
        if ((!Macierz.isOk(macierz)) || (macierz.getLengthY() < 1) || (macierz.getLengthX() < 1)) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        BigDecimal valuebd = value.getValueAsBigDecimal();
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            for (int yi = 0; yi < macierz.getLengthY(); yi++) {
                BigDecimal k = macierz.getValueAt(xi, yi).getValueAsBigDecimal();
                if (valuebd.compareTo(k) == 0) {
                    return new Point(xi, yi);
                }
            }
        }
        return null;
    }

    /**
     * Sortuje JEDNĄ wybraną kolumnę. <code>Korzysta z Arrays.sort()</code>.
     * @param macierz macierz do sortowania
     * @param kolumna indeks kolumny do posortowania
     * @return posortowana kolumna jako macierz
     */
    public static Macierz sortuj(Macierz macierz, int kolumna) {
        //Macierz result = new Macierz(1, macierz.getLengthY());
        Liczba[] l = new Liczba[macierz.getLengthY()];
        for (int i = 0; i < macierz.getLengthY(); i++) {
            l[i] = macierz.getValueAt(kolumna, i);
        }
        Arrays.sort(l);
        return Macierz.createY(l);
    }

    /**
     * Sprawdza, czy macierze są różne od <code>null</code> oraz czy nie są puste
     * @param macierze macierze do sprawdzenia.
     * @return <code>true</code>, jeśli wszystkie podane macierze spełniają
     * warunki, <code>false</code> w przeciwnym wypadku.
     */
    public static boolean isOk(Macierz... macierze) {
        for (Macierz m : macierze) {
            if (m == null || m.getLengthX() < 1 || m.getLengthY() < 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Oblicza macierz odwrotną stosując metodę eliminacji Gaussa.
     * @param macierz macierz, którą trzeba odwrócić
     * @return macierz odwrotna do podanej w parametrze.
     */
    public static Macierz odwrotna(Macierz macierz) throws InvalidArgumentException {

        //odwr({{1;0;1}{3;3;0}{0;2;2}})
        int lx = macierz.getLengthX(), ly = macierz.getLengthY();
        wyznacznik_sprawdzWymiarMacierzy(lx, ly);
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
        for (int i = 0; i < lx; i++) {
            for (int j = 0; j < lx; j++) {
                result.set(i, j, new Liczba(0));
            }
        }

        if (lx == 1) //już wiadomo, że kwadratowa
        {
            result.set(0, 0, (Liczba) Znak.podzielElementy(new Liczba(1), macierz.getValueAt(0, 0)));
            return result;
        }
        int[] w = new int[lx];
        for (int i = 0; i < lx; i++) {
            w[i] = i;
        }
        //System.out.println("macierz początkowa "+macierz);
        Funkcja Abs = new Funkcja(Funkcja.Functions.ABS);
        boolean cond = true;
        int k = 0;// - 1;

        do {
            //k++;
            Liczba max = (Liczba) Abs.getValue(macierz.getValueAt(k, k));
            int ih = k;
            for (int i = k + 1; i < lx; i++) {
                Liczba m = (Liczba) Abs.getValue(macierz.getValueAt(k, i));
                if (m.compareTo(max) > 0) {
                    //System.out.println("stary max="+max.toString()+", nowy "+m.toString());
                    max = m;
                    ih = i;
                }

            }
            if (max.compareTo(new Liczba(0)) == 0) {
                cond = false;
            //System.out.println("cond=false!");
            } else {
                if (ih != k) {
                    int j = w[k];
                    w[k] = w[ih];
                    w[ih] = j;
                    for (int j1 = 0; j1 < lx; j1++) {
                        //System.out.println("zamiana "+macierz.getValueAt(j1, k)+" "+macierz.getValueAt(j1, ih));
                        Liczba m = macierz.getValueAt(j1, k);
                        macierz.set(j1, k, macierz.getValueAt(j1, ih));
                        macierz.set(j1, ih, m);
                    }
                //System.out.println("po zamianach: "+macierz);
                }
                for (int i = k + 1; i < lx; i++) {
                    //System.out.println("dzielenie i="+i+" k="+k+"  "+macierz.getValueAt(k, i)+"/"+ macierz.getValueAt(k, k));
                    Liczba m = (Liczba) Znak.podzielElementy(macierz.getValueAt(k, i), macierz.getValueAt(k, k));
                    macierz.set(k, i, m);
                    for (int j = k + 1; j < lx; j++) {
                        macierz.set(j, i, (Liczba) Znak.odejmijElementy(macierz.getValueAt(j, i), Znak.pomnozElementy(m, macierz.getValueAt(j, k))));
                    }
                }
            //System.out.println("ih="+ih+", k="+k+", a="+macierz);
            }
        } while ((++k < lx - 1) && cond);
        if (!cond || (macierz.getValueAt(lx - 1, lx - 1)).compareTo(new Liczba(0)) == 0) {
            throw new InvalidArgumentException("Macierz jest osobliwa");
        }
        ////System.out.println("a po while: "+macierz);

        for (int j = 0; j < lx - 1; j++) {
            result.set(j, j + 1, (Liczba) Znak.przeciwnyElement(macierz.getValueAt(j, j + 1)));
            //System.out.println("result "+result);
            for (int i = j + 2; i < lx; i++) {
                Liczba m = (Liczba) Znak.przeciwnyElement(macierz.getValueAt(j, i));
                for (int k1 = j + 1; k1 <= i - 1; k1++) {
                    //System.out.println("m="+m+"-"+macierz.getValueAt(k1, i)+"*"+result.getValueAt(j, k1));
                    m = (Liczba) Znak.odejmijElementy(m, (Liczba) Znak.pomnozElementy(macierz.getValueAt(k1, i), result.getValueAt(j, k1)));
                }
                result.set(j, i, m);
            //System.out.println("result4 "+result);
            }
        }
        ////System.out.println("a po for 1: " + macierz);
        //System.out.println("result po for 1:" + result);

        for (int j = 0; j < lx; j++) {
            result.set(j, j, (Liczba) Funkcja.getValue(Functions.INV, macierz.getValueAt(j, j)));
            //for (int i=j-1 downto 1 do
            for (int i = j - 1; i >= 0; i--) {
                Liczba m = new Liczba(0);
                for (int k1 = i + 1; k1 <= j; k1++) {
                    m = (Liczba) Znak.odejmijElementy(m, (Liczba) Znak.pomnozElementy(macierz.getValueAt(k1, i), result.getValueAt(j, k1)));
                }
                Liczba ll = (Liczba) Znak.podzielElementy(m, macierz.getValueAt(i, i));
                result.set(j, i, ll);
            }
        }
        ////System.out.println("a po for 2: " + macierz);
        //System.out.println("result po for 2:" + result);

        for (int i = 0; i < lx; i++) {
            for (int j = 0; j < lx; j++) {
                if (i > j) {
                    Liczba m = new Liczba(0);
                    //System.out.println("1) m="+m);
                    for (int k1 = i; k1 < lx; k1++) {
                        //System.out.println("m="+m+"+"+result.getValueAt(k1, i)+"*"+result.getValueAt(j, k1));
                        m = (Liczba) Znak.dodajElementy(m, Znak.pomnozElementy(result.getValueAt(k1, i), result.getValueAt(j, k1)));
                    }
                    macierz.set(j, i, m);
                //System.out.println("1) j="+j+",i="+i+", m="+m);
                } else {
                    Liczba m = result.getValueAt(j, i);
                    //System.out.println("2) m="+m);
                    for (int k1 = j + 1; k1 < lx; k1++) {
                        //System.out.println("m="+m+"+"+result.getValueAt(k1, i)+"*"+result.getValueAt(j, k1));
                        m = (Liczba) Znak.dodajElementy(m, Znak.pomnozElementy(result.getValueAt(k1, i), result.getValueAt(j, k1)));
                    }
                    macierz.set(j, i, m);
                //System.out.println("2) j="+j+",i="+i+", m="+m);
                }
            }
        }

        /*System.out.print("w na końcu: " + Arrays.asList(w).toString() + " ");
        for (int i = 0; i < w.length; i++) {
        int j = w[i];
        System.out.print(", " + j);
        }*/
        //System.out.println("");

        //System.out.println("a na końcu: " + macierz.toString());

        for (int k1 = 0; k1 < lx; k1++) {
            int j = w[k1];
            if (j == k1) {
                for (int i = 0; i < lx; i++) {
                    result.set(k1, i, macierz.getValueAt(k1, i));
                }
            } else {
                for (int i = 0; i < lx; i++) {
                    result.set(j, i, macierz.getValueAt(k1, i));
                }
            }
        }

        return result;
    }
} //class

