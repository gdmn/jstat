/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

/**
 *
 * @author dmn
 */
public abstract class Dynamika {

    public static Macierz sredniaChronologiczna(Macierz macierz) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 3) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), 1);
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            Liczba wynik = new Liczba(0);
            for (int yi = 0; yi < macierz.getLengthY(); yi++) {
                if (yi > 0 && yi < macierz.getLengthY() - 1) {
                    wynik = (Liczba) Znak.dodajElementy(wynik, macierz.getValueAt(xi, yi));
                } else {
                    wynik = (Liczba) Znak.dodajElementy(wynik, Znak.pomnozElementy(macierz.getValueAt(xi, yi), new Liczba(0.5)));
                }
            }
            wynik = (Liczba) Znak.podzielElementy(wynik, new Liczba(macierz.getLengthY() - 1));
            result.set(xi, 0, wynik);
        }
        return result;
    }

    /**
     * Oblicza przyrosty absolutne po kolumnach macierzy.
     * @param macierz
     * @return
     */
    public static Macierz przyrostyAbsolutneLancuchowe(Macierz macierz) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 2) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            for (int yi = 1; yi < macierz.getLengthY(); yi++) {
                Liczba wynik = (Liczba) Znak.odejmijElementy(macierz.getValueAt(xi, yi), macierz.getValueAt(xi, yi - 1));
                result.set(xi, yi, wynik);
            }
        }
        return result;
    }

    /**
     * Oblicza przyrosty absolutne po kolumnach macierzy.
     * @param macierz
     * @param podstawa numer okresu (pierwszy to 0), który przyjmuje się za podstawę.
     * @return
     */
    public static Macierz przyrostyAbsolutneJednopodstawowe(Macierz macierz, int podstawa) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 2 || podstawa >= macierz.getLengthY()) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            Liczba podst = macierz.getValueAt(xi, podstawa);
            for (int yi = 0; yi < macierz.getLengthY(); yi++) {
                Liczba wynik = (Liczba) Znak.odejmijElementy(macierz.getValueAt(xi, yi), podst);
                result.set(xi, yi, wynik);
            }
        }
        return result;
    }

    /**
     * Oblicza przyrosty względne po kolumnach macierzy.
     * @param macierz
     * @return
     */
    public static Macierz przyrostyWzgledneLancuchowe(Macierz macierz) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 2) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            for (int yi = 1; yi < macierz.getLengthY(); yi++) {
                Liczba poprzedni = macierz.getValueAt(xi, yi - 1);
                if (poprzedni.compareTo(new Liczba(0)) != 0) {
                    Liczba wynik = (Liczba) Znak.odejmijElementy(macierz.getValueAt(xi, yi), poprzedni);
                    wynik = (Liczba) Znak.podzielElementy(wynik, poprzedni);
                    result.set(xi, yi, wynik);
                }
            }
        }
        return result;
    }

    /**
     * Oblicza przyrosty względne po kolumnach macierzy.
     * @param macierz
     * @param podstawa numer okresu (pierwszy to 0), który przyjmuje się za podstawę.
     * @return
     */
    public static Macierz przyrostyWzgledneJednopodstawowe(Macierz macierz, int podstawa) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 2 || podstawa >= macierz.getLengthY()) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            Liczba podst = macierz.getValueAt(xi, podstawa);
            if (podst.compareTo(new Liczba(0)) != 0) {
                for (int yi = 0; yi < macierz.getLengthY(); yi++) {
                    Liczba wynik = (Liczba) Znak.odejmijElementy(macierz.getValueAt(xi, yi), podst);
                    wynik = (Liczba) Znak.podzielElementy(wynik, podst);
                    result.set(xi, yi, wynik);
                }
            }
        }
        return result;
    }

    /**
     * Oblicza łańcuchowe indywidualne indeksy dynamiki po kolumnach macierzy.
     * @param macierz
     * @return
     */
    public static Macierz indywidualneIndeksyDynamikiLancuchowe(Macierz macierz) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 2) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            for (int yi = 1; yi < macierz.getLengthY(); yi++) {
                Liczba poprzedni = macierz.getValueAt(xi, yi - 1);
                if (poprzedni.compareTo(new Liczba(0)) != 0) {
                    Liczba wynik = (Liczba) Znak.podzielElementy(macierz.getValueAt(xi, yi), poprzedni);
                    result.set(xi, yi, wynik);
                }
            }
        }
        return result;
    }

    /**
     * Oblicza średnią geometryczną indeksów łańcuchowych liczonych po kolumnach macierzy.
     * @param macierz
     * @return
     */
    public static Macierz indywidualneIndeksyDynamikiLancuchowe_sredniaGeometryczna(Macierz macierz) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 2) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), 1);
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            Liczba pierwszy = macierz.getValueAt(xi, 0);
            Liczba ostatni = macierz.getValueAt(xi, macierz.getLengthY() - 1);
            if (pierwszy.compareTo(new Liczba(0)) != 0) {
                Liczba wynik = (Liczba) Znak.podzielElementy(ostatni, pierwszy);
                Liczba stopien = (Liczba) Znak.podzielElementy(new Liczba(1), new Liczba(macierz.getLengthY() - 1));
                wynik = (Liczba) (new Funkcja(Funkcja.Functions.POW)).getValue(wynik, stopien);
                result.set(xi, 0, wynik);
            }
        }
        return result;
    }

    /**
     * Oblicza względne indywidualne indeksy dynamiki po kolumnach macierzy.
     * @param macierz
     * @param podstawa numer okresu (pierwszy to 0), który przyjmuje się za podstawę.
     * @return
     */
    public static Macierz indywidualneIndeksyDynamikiJednopodstawowe(Macierz macierz, int podstawa) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 2 || podstawa >= macierz.getLengthY()) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            Liczba podst = macierz.getValueAt(xi, podstawa);
            if (podst.compareTo(new Liczba(0)) != 0) {
                for (int yi = 0; yi < macierz.getLengthY(); yi++) {
                    Liczba wynik = (Liczba) Znak.podzielElementy(macierz.getValueAt(xi, yi), podst);
                    result.set(xi, yi, wynik);
                }
            }
        }
        return result;
    }

    /**
     * Wygładza szereg czasowy metodą średniej ruchomej.
     * @param macierz
     * @param okresy liczba okresów, dla których liczona jest średnia
     * @return macierz wymiaru takiego samego jak podana w parametrze
     * <code>macierz</code>, ale początkowe (w ilości
     * <code>okresy-1</code>) i końcowe wiersze mają wartości <code>null</code>.
     */
    public static Macierz sredniaRuchoma(Macierz macierz, int okresy) {
        if (!Macierz.isOk(macierz) || macierz.getLengthY() < 3 || okresy > macierz.getLengthY() - 2) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
        boolean parzyste = okresy % 2 == 0;
        int roznica = 0;
        if (parzyste) { //parzyste: 4, 6, ..
            roznica = okresy / 2;
        } else { //nieparzyste
            roznica = okresy / 2;
        }
//        System.out.println("roznica "+roznica);
        for (int xi = 0; xi < macierz.getLengthX(); xi++) {
            for (int yi = roznica; yi < macierz.getLengthY() - (roznica); yi++) {
                Liczba wynik = new Liczba(0);
                for (int i = yi - roznica; i <= yi + roznica; i++) {
                    if (parzyste && (i == yi - roznica || i == yi + roznica)) {
                        wynik = (Liczba) Znak.dodajElementy(wynik, (Liczba) Znak.pomnozElementy(macierz.getValueAt(xi, i), new Liczba(0.5)));
                    } else {
                        wynik = (Liczba) Znak.dodajElementy(wynik, macierz.getValueAt(xi, i));
                    }
//                    System.out.println("i "+i);
                }
//                System.out.println("od " + (yi - roznica) + ", suma "+wynik+", okresy "+okresy);
                wynik = (Liczba) Znak.podzielElementy(wynik, new Liczba(okresy));
                result.set(xi, yi, wynik);
            }
        }
        return result;
    }

    /**
     * Tworzy macierz elementów reprezentujących okresy czasu.<br />Dla
     * funkcji z klasy <code>Regresja</code> zastępuje <code>macierz_x</code>.
     * @param count ilość okresów czasu.
     * @return pionowy wektor z liczbami od <code>1</code> do <code>count</code>.
     */
    public static Macierz macierzT(int count) {
        Macierz macierz_t = new Macierz(1, count);
        for (int i = 0; i < count; i++) {
            macierz_t.set(0, i, new Liczba(i+1));
        }
        return macierz_t;
    }

    /**
     * Oblicza współczynniki kierunkowe dla funkcji regresji liniowej.
     * @param macierz zmienna objaśniana
     * @return macierz o ilości kolumn równej ilości kolumn
     * <code>macierz</code>, ilość wierszy wynosi 1.
     */
    public static Macierz trendWspolczynnikA(Macierz macierz) {
        Macierz macierz_t = macierzT(macierz.getLengthY());
        return Regresja.wspolczynnikA(macierz, macierz_t);
    }

    /**
     * Oblicza wyraz wolny dla funkcji regresji liniowej.
     * @param macierz
     * @return macierz o wymiarach 1 na 1.
     */
    public static Macierz trendWspolczynnikB(Macierz macierz) {
        Macierz macierz_t = macierzT(macierz.getLengthY());
        return Regresja.wspolczynnikB(macierz, macierz_t);
    }
}
