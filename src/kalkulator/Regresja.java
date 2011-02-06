/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

import java.util.ArrayList;
import java.util.Arrays;
import kalkulator.Funkcja.Functions;

/**
 *
 * @author dmn
 */
public abstract class Regresja {

    /**
     * Oblicza współczynnik korelacji liniowej rxy.
     * @param macierz_y
     * @param macierz_x
     * @return współczynnik korelacji.
     */
    public static Liczba wspolczynnikKorelacjiLiniowej(Macierz macierz_y, Macierz macierz_x) {
        Macierz macierz_x_srednia = PodstawoweStatystyki.srednia(macierz_x);
        Macierz macierz_y_srednia = PodstawoweStatystyki.srednia(macierz_y);
        Macierz macierz_xminusxsr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_x, macierz_x_srednia);
        Macierz macierz_yminusysr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_y, macierz_y_srednia);
        Macierz macierz_xminusxsrkwadrat = (Macierz) Znak.pomnozSkalarnieElementy(macierz_xminusxsr, macierz_xminusxsr);
        Macierz macierz_yminusysrkwadrat = (Macierz) Znak.pomnozSkalarnieElementy(macierz_yminusysr, macierz_yminusysr);
        Macierz macierz_licznik = PodstawoweStatystyki.suma((Macierz) Znak.pomnozSkalarnieElementy(macierz_xminusxsr, macierz_yminusysr));
        Macierz macierz_mianownik = (Macierz) Znak.pomnozSkalarnieElementy(PodstawoweStatystyki.suma(macierz_xminusxsrkwadrat), PodstawoweStatystyki.suma(macierz_yminusysrkwadrat));
        macierz_mianownik = Macierz.getValueApplyFunction1(macierz_mianownik, new Funkcja(Funkcja.Functions.SQRT));
        Macierz result = (Macierz) Znak.podzielSkalarnieElementy(macierz_licznik, macierz_mianownik);
        return result.getValueAt(0, 0);
    }

    /**
     * Oblicza kowariancję dla szeregów szczegółowych.
     * @param macierz_y
     * @param macierz_x
     * @return
     */
    public static Liczba kowariancja(Macierz macierz_y, Macierz macierz_x) {
        Macierz macierz_x_srednia = PodstawoweStatystyki.srednia(macierz_x);
        Macierz macierz_y_srednia = PodstawoweStatystyki.srednia(macierz_y);
        Macierz macierz_xminusxsr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_x, macierz_x_srednia);
        Macierz macierz_yminusysr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_y, macierz_y_srednia);
        Macierz macierz_licznik = PodstawoweStatystyki.suma((Macierz) Znak.pomnozSkalarnieElementy(macierz_xminusxsr, macierz_yminusysr));
        Macierz result = (Macierz) Znak.podzielElementy(macierz_licznik, new Liczba(macierz_y.getLengthY()));
        return result.getValueAt(0, 0);
    }

    /**
     * Oblicza macierz korelacji.
     * @param macierz zmiennych objaśniających
     * @return macierz kwadratowa o wymiarze równym ilości kolumn macierzy
     * podanej w parametrze, której elementy są współczynnikami korelacji
     * między zmiennymi objaśniającymi
     */
    public static Macierz macierzKorelacji(Macierz macierz) {
        if (!Macierz.isOk(macierz)) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthX());
        for (int i = 0; i < macierz.getLengthX(); i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j) {
                    //result.set(i, i, kowariancja(macierz.pobierzKolumny(macierz, i), macierz.pobierzKolumny(macierz, i)));
                    result.set(i, i, new Liczba(1));
                } else {
                    Liczba v = wspolczynnikKorelacjiLiniowej(macierz.pobierzKolumny(macierz, i), macierz.pobierzKolumny(macierz, j));
                    result.set(i, j, v);
                    result.set(j, i, v);
                }
            }
        }
        return result;
    }

    /**
     * Oblicza wektor korelacji.
     * @param macierz_y zmienna objaśniana (wektor pionowy),
     * @param macierz_x zmienne objaśniające.
     * @return wektor pionowy, którego kolejnymi elementami są współczynniki
     * korelacji między zmienną objaśnianą i zmiennymi objasniającymi.
     */
    public static Macierz wektorKorelacji(Macierz macierz_y, Macierz macierz_x) {
        if (!Macierz.isOk(macierz_x, macierz_y) ||
                macierz_y.getLengthX() != 1) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = new Macierz(1, macierz_x.getLengthX());
        //for (int i = 0; i < macierz_y.getLengthY(); i++) {
        for (int j = 0; j < macierz_x.getLengthX(); j++) {
            Liczba v = wspolczynnikKorelacjiLiniowej(Macierz.pobierzKolumny(macierz_x, j), macierz_y);
            result.set(0, j, v);
        }
        // }
        return result;
    }

    /**
     * Oblicza rozszerzoną macierz korelacji.
     * @param macierz_y zmienna objaśniana (wektor pionowy),
     * @param macierz_x zmienne objaśniające.
     * @return rozszerzona macierz korelacji R*
     */
    public static Macierz rozszerzonaMacierzKorelacji(Macierz macierz_y, Macierz macierz_x) {
        /** wektor korelacji */
        Macierz R0 = wektorKorelacji(macierz_y, macierz_x);
        /** macierz korelacji */
        Macierz R = macierzKorelacji(macierz_x);
        /** wynik */
        Macierz result = new Macierz(macierz_x.getLengthX() + 1, macierz_x.getLengthX() + 1);
        for (int i = 0; i < R0.getLengthY(); i++) {
            result.set(i + 1, 0, R0.get(0, i));
            result.set(0, i + 1, R0.get(0, i));
        }
        for (int i = 0; i < R.getLengthX(); i++) {
            for (int j = 0; j < R.getLengthY(); j++) {
                result.set(i + 1, j + 1, R.getValueAt(i, j));
            }
        }
        result.set(0, 0, new Liczba(1));
        return result;
    }

    public static Liczba wspolczynnikKorelacjiWielorakiej(Macierz macierzKorelacji, Macierz rozszerzonaMacierzKorelacji) {
        Liczba result;
        result = (Liczba) Znak.odejmijElementy(new Liczba(1), Znak.podzielElementy(Macierz.wyznacznikGauss(rozszerzonaMacierzKorelacji), Macierz.wyznacznikGauss(macierzKorelacji)));
        result = (Liczba) (new Funkcja(Funkcja.Functions.SQRT)).getValue(result);
        return result;
    }

    /**
     * Oblicza współczynniki kierunkowe dla funkcji regresji liniowej.
     * @param macierz_y zmienna objaśniana
     * @param macierz_x zmienne objaśniające
     * @return macierz o ilości kolumn równej ilości kolumn
     * <code>macierz_x</code>, ilość wierszy wynosi 1.
     */
    public static Macierz wspolczynnikA(Macierz macierz_y, Macierz macierz_x) {
        if (!Macierz.isOk(macierz_x, macierz_y) ||
                macierz_y.getLengthX() != 1 ||
                macierz_x.getLengthX() != 1) {
            throw new InvalidArgumentException("Tylko gdy ilość kolumn macierzy wynosi 1");
        }
        Macierz macierz_x_srednia = PodstawoweStatystyki.srednia(macierz_x);
        Macierz macierz_y_srednia = PodstawoweStatystyki.srednia(macierz_y);
        Macierz macierz_xminusxsr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_x, macierz_x_srednia);
        Macierz macierz_yminusysr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_y, macierz_y_srednia);
        Macierz macierz_licznik = PodstawoweStatystyki.suma((Macierz) Znak.pomnozSkalarnieElementy(macierz_xminusxsr, macierz_yminusysr));
        Macierz macierz_xminusxsrkwadrat = (Macierz) Znak.pomnozSkalarnieElementy(macierz_xminusxsr, macierz_xminusxsr);
        Macierz macierz_mianownik = PodstawoweStatystyki.suma(macierz_xminusxsrkwadrat);
        //TODO: odwracanie macierzy
        Macierz result = (Macierz) Znak.podzielElementy(macierz_licznik, macierz_mianownik.getValueAt(0, 0));
        return result;
    }

    /**
     * Dodaje na końcu kolumnę złożoną z <b>1</b> do macierzy
     * obserwacji zmiennych objaśniających.
     * @param macierz_x macierz obserwacji zmiennych objaśniających
     * @return macierz z dodaną kolumną jedynek.
     */
    public static Macierz dodajWyrazWolny(Macierz macierz_x) {
        Macierz macierz_x_1 = new Macierz(macierz_x.getLengthX() + 1, macierz_x.getLengthY());
        macierz_x_1 = Macierz.wklej(macierz_x_1, macierz_x, 0, 0);
        Macierz j = new Macierz(1, macierz_x.getLengthY());
        j.fill(Liczba.JEDEN);
        macierz_x_1 = Macierz.wklej(macierz_x_1, j, macierz_x_1.getLengthX() - 1, 0);
        return macierz_x_1;
    }

    /**
     * Oblicza ocenę wektora parametrów alfa dla modelu KMNK.
     * @param macierz_y
     * @param macierz_x macierz obserwacji zmiennych objaśniających
     * @param wyraz_wolny jeśli <code>true</code> dodaje na końcu kolumnę
     * złożoną z <b>1</b> do macierzy obserwacji zmiennych objaśniających.
     * @return parametry alfa do modelu <i>y_teoret = a0 +a1x1 + ... + amxm</i>.
     */
    public static Macierz parametryAlfa(Macierz macierz_y, Macierz macierz_x, boolean wyraz_wolny) {
        // a = (XTX)^(-1) * XTy
        Macierz mx;
        if (wyraz_wolny) {
            mx = dodajWyrazWolny(macierz_x);
        } else {
            mx = macierz_x;
        }
		Macierz xtx = (Macierz) Znak.pomnozElementy(Funkcja.getValue(Functions.TRANSP, mx), mx);
		Macierz xtxinv = (Macierz) Funkcja.getValue(Functions.INV, xtx);
		Macierz xty = (Macierz) Znak.pomnozElementy(Funkcja.getValue(Functions.TRANSP, mx), macierz_y);
        Macierz result = (Macierz) Znak.pomnozElementy(xtxinv, xty);
        return result;
    }

    /**
     * Oblicza wyraz wolny dla funkcji regresji liniowej.
     * @param macierz_y
     * @param macierz_x
     * @return macierz o wymiarach 1 na 1.
     */
    public static Macierz wspolczynnikB(Macierz macierz_y, Macierz macierz_x) {
        if (!Macierz.isOk(macierz_x, macierz_y) ||
                macierz_y.getLengthX() != 1 ||
                macierz_x.getLengthX() != 1) {
            throw new InvalidArgumentException("Tylko gdy ilość kolumn macierzy wynosi 1");
        }
        Macierz macierz_x_srednia = PodstawoweStatystyki.srednia(macierz_x);
        Macierz macierz_y_srednia = PodstawoweStatystyki.srednia(macierz_y);
        Macierz wsp_a = wspolczynnikA(macierz_y, macierz_x);
        Macierz result = (Macierz) Znak.odejmijElementy(macierz_y_srednia, Znak.pomnozSkalarnieElementy(wsp_a, macierz_x_srednia));
        return result;
    }

    /**
     * Oblicza teroretyczne wartości funkcji regresji.
     * @param macierz_x macierz obserwacji
     * @param wspolczynnik_a macierz współczynników kierunkowych. Ilość kolumn
     * musi się równać ilości kolumn <code>macierz_x</code>.
     * @param wspolczynnik_b macierz wyrazów wolnych. Ilość kolumn i wierszy
     * musi się równać 1.
     * @return macierz wartości teoretycznych. Ilość kolumn = 1.
     * Ilość wierszy = ilość wierszy <code>macierz_x</code>.
     */
    public static Macierz obliczTeoretyczne(Macierz macierz_x, Macierz wspolczynnik_a, Macierz wspolczynnik_b) {
        if (!Macierz.isOk(macierz_x, wspolczynnik_a, wspolczynnik_b) ||
                macierz_x.getLengthX() != wspolczynnik_a.getLengthX() ||
                wspolczynnik_b.getLengthX() != 1 || wspolczynnik_b.getLengthY() != 1) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz result = (Macierz) Znak.pomnozSkalarnieElementy(macierz_x, wspolczynnik_a);
        result = PodstawoweStatystyki.sumaPoWierszach(result);
        result = (Macierz) Znak.dodajSkalarnieElementy(result, wspolczynnik_b);
        return result;
    }

	/**
	 * Oblicza sumę kwadratów wszystkich kolumn macierzy podanej w parametrze.
	 * @param macierz
	 * @return Zwraca macierz o ilości kolumn równej ilości
	 * kolumn macierzy podanej w parametrze i ilości wierszy równej jeden.
	 */
	public static Macierz sumaKwadratow(Macierz macierz) {
		return PodstawoweStatystyki.suma((Macierz) Znak.pomnozSkalarnieElementy(macierz, macierz));
	}

    /**
     * Oblicza teroretyczne wartości funkcji regresji.
     * @param macierz_x macierz obserwacji.
     * @param parametry_alfa macierz parametrów alfa (wektor pionowy).
     * @return teoretyczne wartości funkcji regresji.
     */
    public static Macierz obliczTeoretyczne(Macierz macierz_x, Macierz parametry_alfa) {
        if (!Macierz.isOk(macierz_x, parametry_alfa) ||
                macierz_x.getLengthX() != parametry_alfa.getLengthY()) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Macierz t = Macierz.transpozycja(parametry_alfa);
        Macierz result = (Macierz) Znak.pomnozSkalarnieElementy(macierz_x, t);
        result = PodstawoweStatystyki.sumaPoWierszach(result);
        return result;
    }

    public static Liczba wspolczynnikZbieznosciFi2(Macierz macierz_y, Macierz macierz_y_teoretyczne) {
        if (!Macierz.isOk(macierz_y, macierz_y_teoretyczne) ||
                macierz_y.getLengthX() != macierz_y_teoretyczne.getLengthX() ||
                macierz_y.getLengthX() != 1) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Liczba result = null;
        Macierz licznik = (Macierz) Znak.odejmijElementy(macierz_y, macierz_y_teoretyczne);
        licznik = PodstawoweStatystyki.suma((Macierz) Znak.pomnozSkalarnieElementy(licznik, licznik));
        Macierz mianownik = (Macierz) Znak.odejmijSkalarnieElementy(macierz_y, PodstawoweStatystyki.srednia(macierz_y));
        mianownik = PodstawoweStatystyki.suma((Macierz) Znak.pomnozSkalarnieElementy(mianownik, mianownik));
        result = ((Macierz) Znak.podzielSkalarnieElementy(licznik, mianownik)).getValueAt(0, 0);
        return result;
    }

    /**
     * Oblicza współczynnik determinacji
     * @param macierz_y
     * @param macierz_y_teoretyczne
     * @return liczba z przedziału [0, 1]. 0 oznacza brak zależności liniowej.
     */
    public static Liczba wspolczynnikDeterminacjiR2(Macierz macierz_y, Macierz macierz_y_teoretyczne) {
        return (Liczba) Znak.odejmijElementy(new Liczba(1), wspolczynnikZbieznosciFi2(macierz_y, macierz_y_teoretyczne));
    }

    /**
     * Oblicza wariancję składnika resztowego.
     * @param macierz_y
     * @param macierz_y_teoretyczne
     * @param k liczba szacowanych parametrów równania regresji (dla liniowej y=ax+b wynosi 2).
     * @return
     */
    public static Liczba wariancjaSkladnikaResztowego(Macierz macierz_y, Macierz macierz_y_teoretyczne, Liczba k) {
        if (!Macierz.isOk(macierz_y, macierz_y_teoretyczne) ||
                macierz_y.getLengthX() != macierz_y_teoretyczne.getLengthX() ||
                macierz_y.getLengthX() != 1) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
        Liczba result = null;
        Macierz licznik = (Macierz) Znak.odejmijElementy(macierz_y, macierz_y_teoretyczne);
        licznik = PodstawoweStatystyki.suma((Macierz) Znak.pomnozSkalarnieElementy(licznik, licznik));
        Liczba mianownik = (Liczba) Znak.odejmijElementy(new Liczba(macierz_y.getLengthY()), k);
        result = ((Macierz) Znak.podzielElementy(licznik, mianownik)).getValueAt(0, 0);
        return result;
    }

    /**
     * Oblicza odchylenie standardowe reszt.
     * @param macierz_y
     * @param macierz_y_teoretyczne
     * @param k liczba szacowanych parametrów równania regresji (dla liniowej y=ax+b wynosi 2).
     * @return pierwiastek wariancji składnika resztowego.
     */
    public static Liczba odchylenieStandardoweReszt(Macierz macierz_y, Macierz macierz_y_teoretyczne, Liczba k) {
        return (Liczba) (new Funkcja(Funkcja.Functions.SQRT)).getValue(wariancjaSkladnikaResztowego(macierz_y, macierz_y_teoretyczne, k));
    }

    /**
     * Oblicza ocenę wariancji składnika losowego modelu (<b>se^2</b>).
     * @param macierz_y
     * @param macierz_y_teoretyczne
     * @param m liczba szacowanych parametrów. Jeśli jest szacowany model z wyrazem wolnym,
     * to:<br /><code>m = macierz_x_1.getLengthX()-1;</code><br />i<br />
     * <code>m = kalkulator.Regresja.parametryAlfa(macierz_x, macierz_y, true).getLengthY()-1;</code><br />
     * @return ocena wariancji składnika losowego.
     */
    public static Liczba wariancjaSkladnikaLosowego(Macierz macierz_y, Macierz macierz_y_teoretyczne, int m) {
        Macierz reszty = (Macierz) Znak.odejmijElementy(macierz_y, macierz_y_teoretyczne);
        Macierz reszty2 = (Macierz) Znak.pomnozSkalarnieElementy(reszty, reszty);
        Liczba reszty2suma = kalkulator.PodstawoweStatystyki.suma(reszty2).getValueAt(0, 0);
        Liczba result;
        //System.out.println(""+reszty2suma + "/("+macierz_y.getLengthY()+"-"+m+"-1)");
        result = (Liczba) Znak.podzielElementy(reszty2suma, new Liczba(macierz_y.getLengthY() - m - 1));
        return result;
    }

    //
    /**
     * Wyznacza estymator macierzy wariancji i kowariancji ocen parametrów strukturalnych modelu (<b>D^2(a)</b>).
     * @param se2 ocena wariancji składnika losowego
     * @param macierz_x
     * @return estymator macierzy wariancji i kowariancji ocen parametrów strukturalnych modelu.
     */
    public static Macierz macierzWariancjiKowariancji(Liczba se2, Macierz macierz_x) {
        Macierz result;
        System.out.println("" + Znak.pomnozElementy(Macierz.transpozycja(macierz_x), macierz_x));
        result = (Macierz) Znak.podzielElementy(se2, Znak.pomnozElementy(Macierz.transpozycja(macierz_x), macierz_x));
        return result;
    }

    /**
     * Wyznacza standardowe błędy ocen parametrów strukturalnych modelu
     * na podstawie estymatora macierzy wariancji i kowariancji parametrów.
     * @param macierz_wariancji_kowariancji
     * @return wektor (pionowy) zbudowany z pierwiastków wyciągniętych
     * z elementów głównej przekątnej podanej macierzy.
     */
    public static Macierz standardoweBledyOcen(Macierz macierz_wariancji_kowariancji) {
        Macierz result = new Macierz(1, macierz_wariancji_kowariancji.getLengthY());
        Funkcja Sqrt = new Funkcja(Functions.SQRT);
        for (int i = 0; i < result.getLengthY(); i++) {
            result.set(0, i, (Liczba) Sqrt.getValue(macierz_wariancji_kowariancji.getValueAt(i, i)));
        }
        return result;
    }

    /**
     * Liczy sprawdzaiany hipotezy zerowej, że współczynnik korelacji
     * między zmiennymi jest istotnie różny od zera dla każdej pary zmiennych.
     * @param macierz_R rozszerzona macierz korelacji
     * @param n liczba obserwacji
     * @return macierz o wymiarach takich samych jak <code>macierz_R</code>,
     * elementami są sprawdziany hipotezy.
     */
    public static Macierz testWspolczynnikaKorelacji_sprawdzianHipotezy(Macierz macierz_R, int n) {
        Macierz result = new Macierz(macierz_R.getLengthX(), macierz_R.getLengthY());
        Liczba nn = new Liczba(n);
        Liczba dwa = new Liczba(2);
        for (int i = 0; i < result.getLengthX(); i++) {
            for (int j = i + 1; j < result.getLengthY(); j++) {
                Liczba el = macierz_R.getValueAt(i, j);
                Liczba l1 = (Liczba) kalkulator.Funkcja.getValue(Functions.ABS, el);
                Liczba l2 = (Liczba) kalkulator.Funkcja.getValue(Functions.SQRT, Znak.odejmijElementy(Liczba.JEDEN, Znak.pomnozElementy(el, el)));
                Liczba l3 = (Liczba) kalkulator.Funkcja.getValue(Functions.SQRT, Znak.odejmijElementy(nn, dwa));
                Liczba r = (Liczba) Znak.podzielElementy(Znak.pomnozElementy(l1, l3), l2);
                result.set(i, j, r);
                result.set(j, i, r);
            }
        }
        return result;
    }

    /**
     * Oblicza wartość krytyczną dla testu istotności współczynnika
     * korelacji (odczytuje wartość z tablic studenta).
     * @param n ilość obserwacji
     * @param alfa poziom istotności
     * @return
     */
    public static Liczba testWspolczynnikaKorelacji_wartoscKrytyczna(int n, double alfa) {
        return (Liczba) Funkcja.getValue(Functions.STUDENTATAIL, new Liczba(n - 2), new Liczba(alfa));
    }

    /**
     * Testuje hipotezę o nieistotności współczynnika korelacji.
     * @param sprawdzian wynik funkcji <code>testWspolczynnikaKorelacji_sprawdzianHipotezy</code>
     * @param wartoscKrytyczna wynik funkcji <code>testWspolczynnikaKorelacji_wartoscKrytyczna</code>
     * @return Macierz, element 0 - nie ma podstaw do odrzucenia hipotezy,
     * że zmienne nie są istotnie skorelowane; 1 - są skorelowane.
     */
    public static Macierz testWspolczynnikaKorelacji_testuj(Macierz sprawdzian, Liczba wartoscKrytyczna) {
        Macierz result = new Macierz(sprawdzian.getLengthX(), sprawdzian.getLengthY());
        for (int i = 0; i < sprawdzian.getLengthX(); i++) {
            for (int j = 0; j < sprawdzian.getLengthY(); j++) {
                Liczba e = sprawdzian.getValueAt(i, j);
                if (e != null) {
                    result.set(i, j, wartoscKrytyczna.compareTo(e) > 0 ? new Liczba("0") : new Liczba("1"));
                } else {
                //result.set(i, j, null);
                }
            }
        }
        return result;
    }

	/**
	 * Oblicza statystykę testową dla testu Durbina Watsona.
	 * @param reszty macierz reszt
	 * @return statystykę testową.
	 */
	public static Liczba testDurbinaWatsona_statystyka(Macierz reszty) {
        if (!Macierz.isOk(reszty) ||
                reszty.getLengthX() !=  1) {
            throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
        }
		Macierz resztykwadrat = (Macierz) Znak.pomnozSkalarnieElementy(reszty, reszty);
		for (int i = reszty.getLengthY() - 1; i > 0; i--) {
			reszty.set(0, i, (Liczba) Znak.odejmijElementy(reszty.getValueAt(0, i), reszty.getValueAt(0, i-1)));
		}
		reszty.set(0, 0, Liczba.ZERO);
		Macierz licznik = (Macierz) Znak.pomnozSkalarnieElementy(reszty, reszty);
		Liczba l_licznik = kalkulator.PodstawoweStatystyki.suma(licznik).getValueAt(0, 0);
		Liczba l_mianownik = kalkulator.PodstawoweStatystyki.suma(resztykwadrat).getValueAt(0, 0);
		System.out.println("testDurbinaWatsona_statystyka "+l_licznik+"/"+l_mianownik + "  " +licznik+"  "+reszty);
		return (Liczba) Znak.podzielElementy(l_licznik, l_mianownik);
	}

    /**
     * Dobór zmiennych objaśniających do modelu metodą analizy współczynników korelacji.
     * @param n ilość obserwacji
     * @param alfa poziom istotności
     * @return wartość krytyczna.
     */
    public static Liczba doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_wartoscKrytyczna(int n, double alfa) {
        Liczba t = (Liczba) Funkcja.getValue(Functions.STUDENTATAIL, new Liczba(n - 2), new Liczba(alfa));
        Liczba t2 = (Liczba) Znak.pomnozElementy(t, t);
        Liczba nminus2 = new Liczba(n - 2);
        Liczba l1 = (Liczba) Znak.podzielElementy(t2, Znak.dodajElementy(t2, nminus2));
        return (Liczba) Funkcja.getValue(Functions.SQRT, l1);
    }

    /**
     * Dobór zmiennych objaśniających do modelu metodą analizy współczynników korelacji.
     * @param macierz_R rozszerzona macierz korelacji
     * @param wartoscKrytyczna wynik funkcji <code>doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_wartoscKrytyczna</code>
     * @return wektor indeksów zmiennych wybranych do modelu.
     */
    public static Macierz doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_dobierz(Macierz macierz_R, Liczba wartoscKrytyczna) {
		Macierz m = (Macierz) Funkcja.getValue(Funkcja.Functions.ABS, macierz_R);
        Boolean[] wModelu = new Boolean[m.getLengthX() - 1];
        for (Boolean elem : wModelu) {
            elem = null;
        }
        for (int i = 1; i < m.getLengthX(); i++) {
            wModelu[i - 1] = (m.getValueAt(i, 0).compareTo(wartoscKrytyczna) > 0) ? null : false;
        }
        boolean ok = false;
        do {
            Liczba max = null;
            int max_i = -1;
            for (int i = 1; i < m.getLengthX(); i++) {
                Liczba el = m.getValueAt(i, 0); // korelacja z Y
                if ((wModelu[i - 1] == null) && (max == null || max.compareTo(el) < 0)) {
                    max = el;
                    max_i = i;
                }
            }
            if (max == null) {
                ok = true;
                break;
            }
            wModelu[max_i - 1] = true;
            System.out.println("doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_dobierz - przyjęcie " + (max_i - 1));
            for (int i = 1; i < m.getLengthX(); i++) {
                if (i != max_i) { // pomijamy element z głównej przekątnej
                    Liczba el = m.getValueAt(i, max_i); // korelacja każdej zmiennej ze zmienną najmocniej skorelowaną z Y
                    if ((wModelu[i - 1] == null) && el.compareTo(wartoscKrytyczna) > 0) {
                        wModelu[i - 1] = false;
                        System.out.println("doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_dobierz - odrzucenie " + (i - 1));
                    }
                }
            }
        } while (!ok);

        int counter = 0;
        for (int i = 0; i < wModelu.length; i++) {
            Boolean b = wModelu[i];
            if (b) {
                counter++;
            }
        }

        Liczba[] results = new Liczba[counter];
        counter = 0;
        for (int i = 0; i < wModelu.length; i++) {
            Boolean b = wModelu[i];
            if (b) {
                results[counter++] = new Liczba(i);
            }
        }
        return (counter > 0) ? Macierz.createX(results) : null;
    }

    private static int newton(int n, int k) {
        int i1 = 1;
        for (int i = 2; i <= n; i++) {
            i1 *= i;
        }
        int i2 = 1;
        for (int i = 2; i <= k; i++) {
            i2 *= i;
        }
        int i3 = 1;
        for (int i = 2; i <= n - k; i++) {
            i3 *= i;
        }
        return i1 / (i2 * i3);
    }

    /**
     * Dobór zmiennych objaśniających do modelu metodą wskaźników
     * pojemności informacji.
     * @param macierz_R rozszerzona macierz korelacji
     * @return wektor indeksów zmiennych wybranych do modelu.
     */
    public static Macierz doborZmiennychMetodaWskaznikowPojemnosciInformacji_dobierz(Macierz macierz_R) {
        int m = (macierz_R.getLengthX() - 1);
        int ilosckombinacji = (2 << (m - 1)) - 1; // 2^m-1
        int[] dlugosciSerii = new int[m]; // ilosci kombinacji o tej samej liczbie elementów
        int[] dlugosciSeriiSkum = new int[m]; //skumulowana dlugosciSerii

        for (int i = 0; i < m; i++) {
            dlugosciSerii[i] = newton(m, i + 1);
        }

        dlugosciSeriiSkum[0] = dlugosciSerii[0];
        for (int i = 1; i < m; i++) {
            dlugosciSeriiSkum[i] = dlugosciSeriiSkum[i - 1] + dlugosciSerii[i];
        }

        System.out.println("ilosc kombinacji " + ilosckombinacji);
		if (ilosckombinacji > 100000) {
			System.err.println("Zbyt dużo kombinacji dla przeprowadzenia doboru zmiennych metodą wskaźników pojemności informacji");
			return null;
		}
        ArrayList<ArrayList<ArrayList<Integer>>> lista = new ArrayList<ArrayList<ArrayList<Integer>>>(ilosckombinacji);
        ArrayList<ArrayList<Integer>> kombinacje = null;
        for (int i = 0; i < m; i++) {
            ArrayList<ArrayList<Integer>> kombinacjepoprzednie = kombinacje;
            kombinacje = new ArrayList<ArrayList<Integer>>();
            lista.add(kombinacje);
            ArrayList<Integer> para;
            if (i == 0) {
                for (int j = 0; j < m; j++) {
                    para = new ArrayList<Integer>();
                    para.add(j);
                    kombinacje.add(para);
                }
            } else {
                for (int j = 0; j < m - i; j++) {
                    int k = kombinacjepoprzednie.size();
                    while (true) {
                        para = new ArrayList<Integer>();
                        para.add(j);
                        ArrayList<Integer> p = kombinacjepoprzednie.get(--k);
                        if (p.get(0) > j) {
                            for (Integer integer : p) {
                                para.add(integer);
                            }
                        } else {
                            break;
                        }
                        kombinacje.add(para);
                    }

                }

            }
            System.out.println((i+1) + " " + kombinacje.toString());
        }
        Liczba max_value = null;
        ArrayList<Integer> max_kombinacja = null;
		Funkcja abs = new Funkcja(Funkcja.Functions.ABS);
        for (ArrayList<ArrayList<Integer>> arrayList : lista) {
            for (ArrayList<Integer> k : arrayList) {
                Liczba suma = Liczba.ZERO;
					StringBuilder s = new StringBuilder();
                for (Integer zmienna : k) {
					if (s.length()>0) s.append("   +   ");
                    Liczba korelacja_z_y = macierz_R.getValueAt(0, 1 + zmienna);
                    Liczba mianownik = Liczba.JEDEN;
                    Liczba licznik = (Liczba) Znak.pomnozElementy(korelacja_z_y, korelacja_z_y);
					s.append(licznik.toString());
					s.append("/(1");
                    for (Integer innazmienna : k) {
                        if (zmienna != innazmienna) {
							Liczba l = (Liczba) abs.getValue(macierz_R.getValueAt(1 + zmienna, 1 + innazmienna));
                            mianownik = (Liczba) Znak.dodajElementy(mianownik, l);
							s.append("+");
							s.append(l.toString());
                        }
                    }
                    Liczba value = (Liczba) Znak.podzielElementy(licznik, mianownik);
					s.append(")");
                    suma = (Liczba) Znak.dodajElementy(suma, value);
                }
                if (max_value == null || suma.compareTo(max_value) > 0) {
                    max_value = suma;
                    max_kombinacja = k;
                }
                System.out.println("" + k.toString() + ", H=" + suma.toString() + " = "+s.toString());
            }
        }
        Liczba[] results = new Liczba[max_kombinacja.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = new Liczba(max_kombinacja.get(i));
        }
        System.out.println("koniec " + Arrays.asList(results).toString());
        return Macierz.createX(results);
    }

    /**
     * Oblicza statystykę testową dla testu istotności parametrów strukturalnych.
     * @param n liczba obserwacji
     * @param m liczba szacowanych parametrów
     * @param wspR2 współczynnik determinacji
     * @return wartość statystyki
     */
    public static Liczba istotnoscParametrowStrukturalnych_statystka(int n, int m, Liczba wspR2) {
        Liczba result = (Liczba) Znak.pomnozElementy(new Liczba((n - m - 1) / m), Znak.podzielElementy(wspR2, Znak.odejmijElementy(Liczba.JEDEN, wspR2)));
        return result;
    }

    /**
     * Oblicza wartość krytyczną dla testu istotności parametrów strukturalnych.
     * @param n liczba obserwacji
     * @param m liczba szacowanych parametrów
     * @param alfa poziom istotności
     * @return wartość odczytana z tablic rozkładu F-Snedecora.
     */
    public static Liczba istotnoscParametrowStrukturalnych_wartoscKrytyczna(int n, int m, double alfa) {
        return (Liczba) Funkcja.getValue(Functions.SNEDECORATAIL, new Liczba(m), new Liczba(n - m - 1), new Liczba(alfa));
    }

    /**
     * Testuje hipotezę testu istotności parametrów strukturalnych.
     * @param statystyka wynik funkcji <code>istotnoscParametrowStrukturalnych_statystka</code>
     * @param krytyczna wynik funkcji <code>istotnoscParametrowStrukturalnych_wartoscKrytyczna</code>
     * @return <b>true</b> jeśli odrzucamy hipotezę zerową o nieistotności
     * parametrów strukturalnych (czyli parametry istotnie różnią się od zera).
     */
    public static Boolean istotnoscParametrowStrukturalnych_testuj(Liczba statystyka, Liczba krytyczna) {
        return statystyka.compareTo(krytyczna) > 0;
    }

    /**
     * Oblicza statystyki testowe dla testu istotności poszczególnych
     * parametrów strukturalnych dla modelu KMNK.
     * @param parametry_alfa ocena wektora parametrów alfa, wynik
     * funkcji <code>parametryAlfa</code>
     * @param bledy standardowe błędy ocen parametrów strukturalnych
     * modelu, wynik funkcji <code>standardoweBledyOcen</code>
     * @return pionowy wektor, którego elementami są ilorazy wartości
     * parametrów i odchylenia standardowego.
     */
    public static Macierz istotnoscPoszczegolnychParametrowStrukturalnych_statystyka(Macierz parametry_alfa, Macierz bledy) {
        return (Macierz) Znak.podzielSkalarnieElementy(parametry_alfa, bledy);
    }

    /**
     * Oblicza wartość krytyczną dla testu istotności poszczególnych
     * parametrów strukturalnych.
     * @param n liczba obserwacji
     * @param m liczba szacowanych parametrów
     * @param alfa poziom istotności
     * @return wartość odczytana z tablic rozkładu Studenta.
     */
    public static Liczba istotnoscPoszczegolnychParametrowStrukturalnych_wartoscKrytyczna(int n, int m, double alfa) {
        return (Liczba) Funkcja.getValue(Functions.STUDENTATAIL, new Liczba(n - m - 1), new Liczba(alfa));
    }

    /**
     * Testuje hipotezę o nieistotności poszczególnych parametrów strukturalnych.
     * @param sprawdzian wynik funkcji <code>istotnoscPoszczegolnychParametrowStrukturalnych_statystyka</code>
     * @param wartoscKrytyczna wynik funkcji <code>istotnoscPoszczegolnychParametrowStrukturalnych_wartoscKrytyczna</code>
     * @return Macierz, element 0 - nie ma podstaw do odrzucenia hipotezy,
     * że parametr jest nieistotny; 1 - parametr istotny.
     */
    public static Macierz istotnoscPoszczegolnychParametrowStrukturalnych_testuj(Macierz sprawdzian, Liczba wartoscKrytyczna) {
        Macierz result = new Macierz(sprawdzian.getLengthX(), sprawdzian.getLengthY());
        Funkcja abs = new Funkcja(Functions.ABS);
        for (int i = 0; i < sprawdzian.getLengthX(); i++) {
            for (int j = 0; j < sprawdzian.getLengthY(); j++) {
                Liczba e = (Liczba) abs.getValue(sprawdzian.getValueAt(i, j));
                result.set(i, j, wartoscKrytyczna.compareTo(e) >= 0 ? new Liczba("0") : new Liczba("1"));
            }
        }
        return result;
    }

    /**
     * Obliczba statystykę dla testu istotności kombinacji liniowej
     * wektora parametrów.
     * @param w wektor (pionowy) kombinacji liniowej. Wektor dobrany zgodnie
     * postulowaną zależnością liniową między parametrami.
     * @param w0 określona liczba (prawa strona hipotezy zerowej)
     * @param macierz_kowariancji <pre>s^2 * (transpozycja(X) * X)^-1</pre>
     * @param parametry_alfa parametry alfa modelu (wektor pionowy)
     * @return statystyka
     */
    public static Liczba istotnoscWektoraParametrow_statystyka(Macierz w, Liczba w0, Macierz macierz_kowariancji, Macierz parametry_alfa) {
        Liczba result;
        Macierz w_transponowane = Macierz.transpozycja(w);
        Liczba licznik = ((Macierz) Znak.odejmijElementy(Znak.pomnozElementy(w_transponowane, parametry_alfa), w0)).getValueAt(0, 0);
        ElementWyrazenia m = Znak.pomnozElementy(w_transponowane, macierz_kowariancji);
        m = Znak.pomnozElementy(m, w);
        Liczba mianownik = (Liczba) Funkcja.getValue(Funkcja.Functions.SQRT, ((Macierz)m).getValueAt(0, 0));
        result = (Liczba) Znak.podzielElementy(licznik, mianownik);
        return result;
    }

    /**
     * Oblicza wartość krytyczną dla testu istotności kombinacji liniowej
     * wektora parametrów.
     * @param n liczba obserwacji
     * @param m liczba szacowanych parametrów
     * @param alfa poziom istotności
     * @return wartość odczytana z tablic rozkładu Studenta.
     */
    public static Liczba istotnoscWektoraParametrow_wartoscKrytyczna(int n, int m, double alfa) {
        return (Liczba) Funkcja.getValue(Functions.STUDENTATAIL, new Liczba(n - m - 1), new Liczba(alfa));
    }

    /**
     * Testuje hipotezę o nieistotności poszczególnych parametrów strukturalnych.
     * @param statystyka wynik funkcji <code>istotnoscWektoraParametrow_statystyka</code>
     * @param wartoscKrytyczna wynik funkcji <code>istotnoscWektoraParametrow_wartoscKrytyczna</code>
     * @return false - nie ma podstaw do odrzucenia hipotezy,
     * że kombinacja liniowa wektora parametrów jest równa podanej liczbie;
     * true - istotnie różna.
     */
    public static Boolean istotnoscWektoraParametrow_testuj(Liczba statystyka, Liczba wartoscKrytyczna) {
        Funkcja abs = new Funkcja(Functions.ABS);
        return ! (((Liczba) abs.getValue(statystyka)).compareTo(wartoscKrytyczna) < 0);
    }
}
