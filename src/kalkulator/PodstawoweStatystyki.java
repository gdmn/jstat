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
import java.util.HashMap;
import kalkulator.Funkcja.Functions;

/**
 *
 * @author dmn
 */
public abstract class PodstawoweStatystyki {
	static final int TYP_SZCZEGOLOWY = 0;
	static final int TYP_ROZDZIELCZY = 1;
	static final int TYP_ROZDZIELCZY_Z_PRZEDZIALAMI = 2;

	/**
	 * Oblicza wektor średnich po kolumnach macierzy
	 * @param macierz macierz do obliczeń
	 * @return wektor średnich
	 */
	public static Macierz srednia(Macierz macierz) {
		if ((macierz == null) || (macierz.getLengthY() < 1) || (macierz.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz result = new Macierz(new WyrazenieInfix[macierz.getLengthX()][1]);
		for (int xi = 0; xi < macierz.getLengthX(); xi++) {
			BigDecimal b = new BigDecimal(0);
			for (int yi = 0; yi < macierz.getLengthY(); yi++) {
				b = b.add(macierz.getValueAt(xi, yi).getValueAsBigDecimal());
			}
			b = b.divide(new BigDecimal(macierz.getLengthY()), Settings.divideContext());
			result.set(xi, 0, new Liczba(b));
		}
		return result;
	}

	/**
	 * Oblicza wektor sumy po kolumnach macierzy
	 * @param macierz macierz do obliczeń
	 * @return poziomy wektor sum
	 */
	public static Macierz suma(Macierz macierz) {
		if (!Macierz.isOk(macierz)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz result = new Macierz(macierz.getLengthX(), 1);
		for (int xi = 0; xi < macierz.getLengthX(); xi++) {
			BigDecimal b = new BigDecimal(0);
			for (int yi = 0; yi < macierz.getLengthY(); yi++) {
				b = b.add(macierz.getValueAt(xi, yi).getValueAsBigDecimal());
			}
			result.set(xi, 0, new Liczba(b));
		}
		return result;
	}

	/**
	 * Oblicza wektor sumy po wierszach macierzy
	 * @param macierz macierz do obliczeń
	 * @return pionowy wektor sum
	 */
	public static Macierz sumaPoWierszach(Macierz macierz) {
		if (!Macierz.isOk(macierz)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz result = new Macierz(1, macierz.getLengthY());
		for (int yi = 0; yi < macierz.getLengthY(); yi++) {
			BigDecimal b = new BigDecimal(0);
			for (int xi = 0; xi < macierz.getLengthX(); xi++) {
				b = b.add(macierz.getValueAt(xi, yi).getValueAsBigDecimal());
			}
			result.set(0, yi, new Liczba(b));
		}
		return result;
	}

	/**
	 * Oblicza wektor iloczynu po kolumnach macierzy
	 * @param macierz macierz do obliczeń
	 * @return poziomy wektor iloczynów
	 */
	public static Macierz iloczyn(Macierz macierz) {
		if (!Macierz.isOk(macierz)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz result = new Macierz(new WyrazenieInfix[macierz.getLengthX()][1]);
		for (int xi = 0; xi < macierz.getLengthX(); xi++) {
			BigDecimal b = new BigDecimal(1);
			for (int yi = 0; yi < macierz.getLengthY(); yi++) {
				b = b.multiply(macierz.getValueAt(xi, yi).getValueAsBigDecimal());
			}
			result.set(xi, 0, new Liczba(b));
		}
		return result;
	}

	/**
	 * Znajduje wektor elementów maksymalnych po kolumnach macierzy
	 * @param macierz macierz do obliczeń
	 * @return wektor elementów maksymalnych
	 */
	public static Macierz max(Macierz macierz) {
		if ((macierz == null) || (macierz.getLengthY() < 1) || (macierz.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz result = new Macierz(new WyrazenieInfix[macierz.getLengthX()][1]);
		for (int xi = 0; xi < macierz.getLengthX(); xi++) {
			Liczba max = macierz.getValueAt(xi, 0);
			for (int yi = 1; yi < macierz.getLengthY(); yi++) {
				Liczba k = macierz.getValueAt(xi, yi);
				max = max.getValueAsBigDecimal().compareTo(k.getValueAsBigDecimal()) > 0 ? max : k;
			}
			result.set(xi, 0, max);
		}
		return result;
	}

	/**
	 * Znajduje wektor elementów minimalnych po kolumnach macierzy
	 * @param macierz macierz do obliczeń
	 * @return wektor elementów minimalnych
	 */
	public static Macierz min(Macierz macierz) {
		if ((macierz == null) || (macierz.getLengthY() < 1) || (macierz.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz result = new Macierz(new WyrazenieInfix[macierz.getLengthX()][1]);
		for (int xi = 0; xi < macierz.getLengthX(); xi++) {
			Liczba min = macierz.getValueAt(xi, 0);
			for (int yi = 1; yi < macierz.getLengthY(); yi++) {
				Liczba k = macierz.getValueAt(xi, yi);
				min = min.getValueAsBigDecimal().compareTo(k.getValueAsBigDecimal()) < 0 ? min : k;
			}
			result.set(xi, 0, min);
		}
		return result;
	}

	/**
	 * Oblicza macierz powstałą przez zsumowanie wyrazów z wszystkich wierszy powyżej i danej kolumny.
	 * @param macierz macierz do obliczeń
	 * @return macierz wartości skumulowanych
	 */
	public static Macierz kumuluj(Macierz macierz) {
		if ((macierz == null) || (macierz.getLengthY() < 1) || (macierz.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz result = new Macierz(macierz.getLengthX(), macierz.getLengthY());
		for (int xi = 0; xi < macierz.getLengthX(); xi++) {
			BigDecimal b = new BigDecimal(0);
			for (int yi = 0; yi < macierz.getLengthY(); yi++) {
				b = b.add(macierz.getValueAt(xi, yi).getValueAsBigDecimal());
				result.set(xi, yi, new Liczba(b));
			}
		}
		return result;
	}

	/**
	 * Oblicza średnie arytmetyczne.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni
	 * @return wektor średnich
	 */
	public static Macierz sredniaArytmetyczna(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		//obliczenie średniej
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		Funkcja fodwr = new Funkcja(Functions.INV);
		Macierz macierz_sredniearytmetyczne = null;

		if (typszeregu > 0) {
			Macierz macierz_xini = (Macierz) Znak.pomnozSkalarnieElementy(macierz_xi, macierz_ni);
			Macierz macierz_ni_sumy = PodstawoweStatystyki.suma(macierz_ni);
			Macierz macierz_xini_sumy = PodstawoweStatystyki.suma(macierz_xini);
			macierz_sredniearytmetyczne = (Macierz) Znak.pomnozSkalarnieElementy(Macierz.getValueApplyFunction1(macierz_ni_sumy, fodwr), macierz_xini_sumy);
		} else {
			macierz_sredniearytmetyczne = macierz_xi_srednie;
		}
		return macierz_sredniearytmetyczne;
	}

	/**
	 * Oblicza średnie geotmetryczne.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni
	 * @return wektor średnich
	 */
	public static Macierz sredniaGeometryczna(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		//obliczenie średniej
		Macierz macierz_xini = (Macierz) Znak.pomnozSkalarnieElementy(macierz_xi, macierz_ni);
		Macierz macierz_sredniegeometryczne_ilorazy = new Macierz(macierz_xini.getLengthX(), macierz_xini.getLengthY() - 1);
		for (int xi = 0; xi < macierz_xini.getLengthX(); xi++) {
			for (int yi = 0; yi < macierz_xini.getLengthY() - 1; yi++) {
				macierz_sredniegeometryczne_ilorazy.set(xi, yi, (Liczba) Znak.podzielElementy(macierz_xini.getValueAt(xi, yi + 1), macierz_xini.getValueAt(xi, yi)));
			}
		}
		Macierz macierz_sredniegeometryczne = PodstawoweStatystyki.iloczyn(macierz_sredniegeometryczne_ilorazy);
		final BigDecimal stopienpierwiastka = (new BigDecimal(1d)).divide(new BigDecimal(macierz_sredniegeometryczne_ilorazy.getLengthY()), Settings.divideContext());
		FunkcjaInterface fpierwiastek = new FunkcjaInterface() {
			@Override
			public int getArgumentsCount() {
				return 1;
			}

			@Override
			public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException {
				return new Liczba(((Liczba) args[0]).pow(stopienpierwiastka));
			}
		};
//            result2.print("średnie macierz_sredniegeometryczne_ilorazy: " + macierz_sredniegeometryczne_ilorazy.toString() + "   pierw: " + stopienpierwiastka.toString());
		macierz_sredniegeometryczne = Macierz.getValueApplyFunction1(macierz_sredniegeometryczne, fpierwiastek);
		return macierz_sredniegeometryczne;
	}

	/**
	 * Oblicza częstości.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni
	 * @return wektor częstości
	 */
	public static Macierz czestosci(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_wi = null;
		if (typszeregu > 0) {
			Macierz macierz_ni_sumy = PodstawoweStatystyki.suma(macierz_ni);
			macierz_wi = (Macierz) Znak.podzielSkalarnieElementy(macierz_ni, macierz_ni_sumy);
		} else {
			Macierz macierz_xini = (Macierz) Znak.pomnozSkalarnieElementy(macierz_xi, macierz_ni);
			Macierz macierz_xini_sumy = PodstawoweStatystyki.suma(macierz_xini);
			macierz_wi = (Macierz) Znak.podzielSkalarnieElementy(macierz_xi, macierz_xini_sumy); //powinno być macierz_xi_sumy, ale takiej nie liczyliśmy. macierz_xini_sumy = macierz_xi_sumy

		}
		return macierz_wi;
	}

	/**
	 * Oblicza odchylenie standardowe na podstawie wariancji (liczy pierwiastek każdego elementu macierzy).
	 * @param wariancja
	 * @return wektor odchyleń
	 */
	public static Macierz odchylenieStandardowe(Macierz wariancja) {
		if ((wariancja == null) || (wariancja.getLengthY() < 1) || (wariancja.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		//Macierz macierz_s2 = wariancja(typszeregu, macierz_xi, macierz_ni);
		Macierz macierz_s = Macierz.getValueApplyFunction1(wariancja, new Funkcja(Funkcja.Functions.SQRT));
		return macierz_s;
	}

	/**
	 * Oblicza odchylenie standardowe.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni może być <code>null</code>, jeśli <code>typszeregu = TYP_SZCZEGOLOWY</code>
	 * @return wektor odchyleń
	 */
	public static Macierz odchylenieStandardowe(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				((typszeregu > 0) && ((macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)))) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_s2 = wariancja(typszeregu, macierz_xi, macierz_ni);
		Macierz macierz_s = Macierz.getValueApplyFunction1(macierz_s2, new Funkcja(Funkcja.Functions.SQRT));
		return macierz_s;
	}

	/**
	 * Oblicza wariancję.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni może być <code>null</code>, jeśli <code>typszeregu = TYP_SZCZEGOLOWY</code>
	 * @return wariancja
	 */
	public static Macierz wariancja(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				((typszeregu > 0) && ((macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)))) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		Macierz ximinusxsr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_xi, macierz_xi_srednie);
		Macierz ximinusxsrkwadrat = (Macierz) Znak.pomnozSkalarnieElementy(ximinusxsr, ximinusxsr);
		Macierz macierz_s2 = null;
		if (typszeregu == TYP_SZCZEGOLOWY) {
			macierz_s2 = (Macierz) Znak.podzielElementy(PodstawoweStatystyki.suma(ximinusxsrkwadrat), new Liczba(macierz_xi.getLengthY()));
		} else {
			Macierz ximinusxsrkwadratni = (Macierz) Znak.pomnozSkalarnieElementy(macierz_ni, ximinusxsrkwadrat);
			Macierz macierz_ni_sumy = PodstawoweStatystyki.suma(macierz_ni);
			macierz_s2 = (Macierz) Znak.podzielSkalarnieElementy(PodstawoweStatystyki.suma(ximinusxsrkwadratni), macierz_ni_sumy);
		}
		return macierz_s2;
	}

	/**
	 * Oblicza odchylenie przeciętne.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni
	 * @return wektor odchyleń
	 */
	public static Macierz odchyleniePrzecietne(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		Macierz absximinuxxsrni = (Macierz) Znak.pomnozSkalarnieElementy(macierz_ni,
				Macierz.getValueApplyFunction1(
				(Macierz) Znak.odejmijElementy(macierz_xi,
				macierz_xi_srednie.getValueAt(0, 0)), new Funkcja(Funkcja.Functions.ABS)));
		Macierz macierz_ni_sumy = PodstawoweStatystyki.suma(macierz_ni);
		Macierz macierz_d = (Macierz) Znak.podzielSkalarnieElementy(PodstawoweStatystyki.suma(absximinuxxsrni), macierz_ni_sumy);
		return macierz_d;
	}

	/**
	 * Oblicza współczynnik zmienności Vs.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni
	 * @return wektor współczynników
	 */
	public static Macierz wspolczynnikZmiennosciVs(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_s = odchylenieStandardowe(wariancja(typszeregu, macierz_xi, macierz_ni));
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		return (Macierz) Znak.podzielSkalarnieElementy(macierz_s, macierz_xi_srednie);
	}

	/**
	 * Oblicza współczynnik zmienności Vd.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni
	 * @return wektor współczynników
	 */
	public static Macierz wspolczynnikZmiennosciVd(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_d = odchyleniePrzecietne(typszeregu, macierz_xi, macierz_ni);
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		return (Macierz) Znak.podzielSkalarnieElementy(macierz_d, macierz_xi_srednie);
	}

	/**
	 * Oblicza modalną (dominantę)
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_xi_poczatek pionowy wektor początów przedziałów klasowych
	 * @param macierz_xi_koniec pionowy wektor początów końców klasowych
	 * @param macierz_ni
	 * @return wektor modalnych
	 */
	public static Macierz modalna(int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(typszeregu == TYP_ROZDZIELCZY_Z_PRZEDZIALAMI) && ((macierz_xi_poczatek == null) || (macierz_xi_poczatek.getLengthY() < 1) || (macierz_xi_poczatek.getLengthX() < 1) ||
				(macierz_xi_koniec == null) || (macierz_xi_koniec.getLengthY() < 1) || (macierz_xi_koniec.getLengthX() < 1)) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_max = typszeregu > 0 ? PodstawoweStatystyki.max(macierz_ni) : PodstawoweStatystyki.max(macierz_xi);
		Macierz macierz_mo = null;
		if (typszeregu == 1) {
			macierz_mo = PodstawoweStatystyki.max(macierz_ni);
			for (int xi = 0; xi < macierz_mo.getLengthX(); xi++) {
				macierz_mo.set(xi, 0, macierz_xi.getValueAt(0, Macierz.znajdzWartosc(Macierz.pobierzKolumny(macierz_ni, xi), macierz_mo.getValueAt(xi, 0)).y));
			}
		} else if (typszeregu == 2) { //z przedziałami klasowymi

			macierz_mo = PodstawoweStatystyki.max(macierz_ni);
			for (int xi = 0; xi < macierz_mo.getLengthX(); xi++) {
				int nrprzedzialu = Macierz.znajdzWartosc(Macierz.pobierzKolumny(macierz_ni, xi), macierz_mo.getValueAt(xi, 0)).y;
				BigDecimal dolnagranica = macierz_xi_poczatek.getValueAt(0, nrprzedzialu).getValueAsBigDecimal();
				BigDecimal rozpietosc = macierz_xi_koniec.getValueAt(0, nrprzedzialu).getValueAsBigDecimal().subtract(dolnagranica);
				BigDecimal liczebnosc = macierz_ni.getValueAt(xi, nrprzedzialu).getValueAsBigDecimal();
				BigDecimal liczebnoscpoprz = nrprzedzialu - 1 >= 0 ? macierz_ni.getValueAt(xi, nrprzedzialu - 1).getValueAsBigDecimal() : new BigDecimal(0);
				BigDecimal liczebnoscnast = nrprzedzialu + 1 <= macierz_ni.getLengthY() ? macierz_ni.getValueAt(xi, nrprzedzialu + 1).getValueAsBigDecimal() : new BigDecimal(0);
				BigDecimal modalna = dolnagranica.add(
						rozpietosc.multiply(
						liczebnosc.subtract(liczebnoscpoprz).divide(
						liczebnosc.subtract(liczebnoscpoprz).add(liczebnosc.subtract(liczebnoscnast)), Settings.divideContext())));
				macierz_mo.set(xi, 0, new Liczba(modalna));
			}
		} else if (typszeregu == 0) { // szczegółowy

			macierz_mo = new Macierz(macierz_max.getLengthX(), 1);
			for (int x = 0; x < macierz_xi.getLengthX(); x++) {
				HashMap<BigDecimal, Integer> ilosc = new HashMap<BigDecimal, Integer>();
				for (int y = 0; y < macierz_xi.getLengthY(); y++) {
					BigDecimal l = macierz_xi.getValueAt(x, y).getValueAsBigDecimal();
					Integer g = ilosc.get(l);
					int gg = (g == null ? 0 : g);
					ilosc.put(l, gg + 1);
				}
				BigDecimal max = null;
				int maxCount = 0;
				for (BigDecimal i : ilosc.keySet()) {
					if (maxCount == 0 || ilosc.get(i) > maxCount) {
						max = i;
						maxCount = ilosc.get(i);
					}
				}
				macierz_mo.set(x, 0, new Liczba(max));
			}
		}
		return macierz_mo;
	}

	private static int[] modalnaPrzedzialy(int count) {
		int[] poz = new int[2];
		boolean parzyste = count % 2 == 0;
		if (!parzyste) {
			poz[0] = (count + 1) / 2 - 1; // -1 bo od zera numerowane

		} else {
			poz[0] = count / 2 - 1; // -1 bo od zera numerowane

			poz[1] = poz[0] + 1;
		}
		return poz;
	}

	private static int kwartylePrzedzialy(int kwartyl, int count) {
		int p = (int) Math.round(0.25 * (count + 1));
		if (kwartyl == 3) {
			p = count + 1 - p;
		}
		p = p - 1;
		return p;
	}

	/**
	 * Oblicza medianę
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_xi_poczatek pionowy wektor początów przedziałów klasowych
	 * @param macierz_xi_koniec pionowy wektor początów końców klasowych
	 * @param macierz_ni
	 * @return wektor modalnych
	 */
	public static Macierz mediana(int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(typszeregu == TYP_ROZDZIELCZY_Z_PRZEDZIALAMI) && ((macierz_xi_poczatek == null) || (macierz_xi_poczatek.getLengthY() < 1) || (macierz_xi_poczatek.getLengthX() < 1) ||
				(macierz_xi_koniec == null) || (macierz_xi_koniec.getLengthY() < 1) || (macierz_xi_koniec.getLengthX() < 1)) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_me = null;
		if (typszeregu == 0) { //szczegółowy

			macierz_me = new Macierz(macierz_xi.getLengthX(), 1);
			Liczba me = null;
			int count = macierz_xi.getLengthY();
			int[] poz = modalnaPrzedzialy(count);
			boolean parzyste = count % 2 == 0;
			for (int x = 0; x < macierz_xi.getLengthX(); x++) {
				//me = !parzyste ? macierz_xi.getValueAt(x, poz[0]) : (Liczba) Znak.podzielElementy(Znak.dodajElementy(macierz_xi.getValueAt(x, poz[0]), macierz_xi.getValueAt(x, poz[1])), new Liczba(new BigDecimal(2)));
				Macierz kolumnaposortowana = Macierz.sortuj(macierz_xi, x);
				//me = !parzyste ? macierz_xi.getValueAt(x, poz[0]) : (Liczba) Znak.podzielElementy(Znak.dodajElementy(macierz_xi.getValueAt(x, poz[0]), macierz_xi.getValueAt(x, poz[1])), new Liczba(new BigDecimal(2)));
				me = !parzyste ? kolumnaposortowana.getValueAt(0, poz[0]) : (Liczba) Znak.podzielElementy(Znak.dodajElementy(kolumnaposortowana.getValueAt(0, poz[0]), kolumnaposortowana.getValueAt(0, poz[1])), new Liczba(new BigDecimal(2)));
				macierz_me.set(x, 0, me);
			}
		} else if (typszeregu <= 2) {
			macierz_me = new Macierz(macierz_ni.getLengthX(), 1);
			Macierz macierz_ni_skumulowany = PodstawoweStatystyki.kumuluj(macierz_ni);
			for (int x = 0; x < macierz_ni_skumulowany.getLengthX(); x++) {
				Liczba me = null;
				int count = macierz_ni_skumulowany.getValueAt(x, macierz_ni_skumulowany.getLengthY() - 1).getValueAsBigDecimal().intValue();
				int[] poz = modalnaPrzedzialy(count);
				boolean parzyste = count % 2 == 0;
				int y1 = -1, y2 = -1;
				//znajdz klasy przedziałów odpowiadające pozycjom
				for (int y = 0; y < macierz_ni_skumulowany.getLengthY(); y++) {
					int k = macierz_ni_skumulowany.getValueAt(x, y).getValueAsBigDecimal().intValue();
					if (y1 < 0 && k >= poz[0]) {
						y1 = y;
						if (!parzyste) {
							break;
						}
					}
					if (parzyste && y2 < 0 && k >= poz[1]) {
						y2 = y;
						break;
					}
				}
				if (typszeregu == 1) {
					me = !parzyste ? macierz_xi.getValueAt(0, y1) : (Liczba) Znak.podzielElementy(Znak.dodajElementy(macierz_xi.getValueAt(0, y1), macierz_xi.getValueAt(0, y2)), new Liczba(new BigDecimal(2)));
				} else if (typszeregu == 2) {
					double p = (parzyste ? poz[0] : -0.5d + poz[0]) + 1; //zgodnie ze wzorem Nme = n/2, czyli skorygujemy to co wcześniej policzone. dodajemy jeden, bo pozycje liczone były od "0"

					int nrprzedzialu = macierz_ni_skumulowany.getValueAt(x, y1).getValueAsBigDecimal().intValue() >= p ? y1 : y2;
					if (nrprzedzialu < 0) {
						throw new InvalidArgumentException("Mediana - czy dane są posortowane?");
					}
					BigDecimal dolnagranica = macierz_xi_poczatek.getValueAt(0, nrprzedzialu).getValueAsBigDecimal();
					BigDecimal rozpietosc = macierz_xi_koniec.getValueAt(0, nrprzedzialu).getValueAsBigDecimal().subtract(dolnagranica);
					BigDecimal liczebnosc = macierz_ni.getValueAt(x, nrprzedzialu).getValueAsBigDecimal();
					BigDecimal sumapoprzedzajacych = nrprzedzialu - 1 >= 0 ? macierz_ni_skumulowany.getValueAt(x, nrprzedzialu - 1).getValueAsBigDecimal() : new BigDecimal(0);
					me = new Liczba(
							dolnagranica.add(rozpietosc.multiply((new BigDecimal(p)).subtract(sumapoprzedzajacych)).divide(liczebnosc, Settings.divideContext())));
				}
				macierz_me.set(x, 0, me);
			}
		}
		return macierz_me;
	}

	/**
	 * Oblicza kwartyle
	 * @param kwartyl 1 lub 2 lub 3
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_xi_poczatek pionowy wektor początów przedziałów klasowych
	 * @param macierz_xi_koniec pionowy wektor początów końców klasowych
	 * @param macierz_ni
	 * @return wektor modalnych
	 */
	public static Macierz kwartyl(int kwartyl, int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni) {
		if (kwartyl == 2) {
			return mediana(typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
		}
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				kwartyl > 3 || kwartyl < 1 ||
				((typszeregu == TYP_ROZDZIELCZY_Z_PRZEDZIALAMI) && ((macierz_xi_poczatek == null) || (macierz_xi_poczatek.getLengthY() < 1) || (macierz_xi_poczatek.getLengthX() < 1) ||
				(macierz_xi_koniec == null) || (macierz_xi_koniec.getLengthY() < 1) || (macierz_xi_koniec.getLengthX() < 1))) ||
				((typszeregu == TYP_ROZDZIELCZY) && ((macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)))) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_me = null;
		if (typszeregu == 0) { //szczegółowy

			macierz_me = new Macierz(macierz_xi.getLengthX(), 1);
			Liczba me = null;
			int count = macierz_xi.getLengthY();
			int poz = kwartylePrzedzialy(kwartyl, count);
			for (int x = 0; x < macierz_xi.getLengthX(); x++) {
				Macierz kolumnaposortowana = Macierz.sortuj(macierz_xi, x);
				me = kolumnaposortowana.getValueAt(0, poz);
				macierz_me.set(x, 0, me);
			}
		} else if (typszeregu <= 2) {
			macierz_me = new Macierz(macierz_ni.getLengthX(), 1);
			Macierz macierz_ni_skumulowany = PodstawoweStatystyki.kumuluj(macierz_ni);
			for (int x = 0; x < macierz_ni_skumulowany.getLengthX(); x++) {
				Liczba me = null;
				int count = macierz_ni_skumulowany.getValueAt(x, macierz_ni_skumulowany.getLengthY() - 1).getValueAsBigDecimal().intValue();
				int poz = kwartylePrzedzialy(kwartyl, count);
				int y1 = -1, y2 = -1;
				//znajdz klasy przedziałów odpowiadające pozycjom
				for (int y = 0; y < macierz_ni_skumulowany.getLengthY(); y++) {
					int k = macierz_ni_skumulowany.getValueAt(x, y).getValueAsBigDecimal().intValue();
					if (y1 < 0 && k >= poz) {
						y1 = y;
						break;
					}
				}
				if (typszeregu == 1) {
					me = macierz_xi.getValueAt(0, y1);
				} else if (typszeregu == 2) {
					int nrprzedzialu = y1;
					if (nrprzedzialu < 0) {
						throw new InvalidArgumentException("Kwartyle - czy dane są posortowane?");
					}
					BigDecimal dolnagranica = macierz_xi_poczatek.getValueAt(0, nrprzedzialu).getValueAsBigDecimal();
					BigDecimal rozpietosc = macierz_xi_koniec.getValueAt(0, nrprzedzialu).getValueAsBigDecimal().subtract(dolnagranica);
					BigDecimal liczebnosc = macierz_ni.getValueAt(x, nrprzedzialu).getValueAsBigDecimal();
					BigDecimal sumapoprzedzajacych = nrprzedzialu - 1 >= 0 ? macierz_ni_skumulowany.getValueAt(x, nrprzedzialu - 1).getValueAsBigDecimal() : new BigDecimal(0);
					me = new Liczba(
							dolnagranica.add(rozpietosc.multiply((new BigDecimal(poz)).subtract(sumapoprzedzajacych)).divide(liczebnosc, Settings.divideContext())));
				}
				macierz_me.set(x, 0, me);
			}
		}
		return macierz_me;
	}

	/**
	 * Oblicza współczynnik skosnosci Ad
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_xi_poczatek pionowy wektor początów przedziałów klasowych
	 * @param macierz_xi_koniec pionowy wektor początów końców klasowych
	 * @param macierz_ni
	 * @return wektor modalnych
	 */
	public static Macierz wspolczynnikSkosnosciAs(int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(typszeregu == TYP_ROZDZIELCZY_Z_PRZEDZIALAMI) && ((macierz_xi_poczatek == null) || (macierz_xi_poczatek.getLengthY() < 1) || (macierz_xi_poczatek.getLengthX() < 1) ||
				(macierz_xi_koniec == null) || (macierz_xi_koniec.getLengthY() < 1) || (macierz_xi_koniec.getLengthX() < 1)) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_as = null;
		Macierz macierz_mo = modalna(typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		Macierz macierz_s = odchylenieStandardowe(wariancja(typszeregu, macierz_xi, macierz_ni));
		if (typszeregu == 0) {
			macierz_as = (Macierz) Znak.podzielSkalarnieElementy(Znak.odejmijElementy(macierz_xi_srednie, macierz_mo), macierz_s);
		} else if (typszeregu <= 2) {
			macierz_as = (Macierz) Znak.podzielSkalarnieElementy(Znak.odejmijElementy(macierz_xi_srednie.getValueAt(0, 0), macierz_mo), macierz_s);
		}
		return macierz_as;
	}

	/**
	 * Oblicza współczynnik skosnosci Ad
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_xi_poczatek pionowy wektor początów przedziałów klasowych
	 * @param macierz_xi_koniec pionowy wektor początów końców klasowych
	 * @param macierz_ni
	 * @return wektor współczynników
	 */
	public static Macierz wspolczynnikSkosnosciAd(int typszeregu, Macierz macierz_xi, Macierz macierz_xi_poczatek, Macierz macierz_xi_koniec, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(typszeregu == TYP_ROZDZIELCZY_Z_PRZEDZIALAMI) && ((macierz_xi_poczatek == null) || (macierz_xi_poczatek.getLengthY() < 1) || (macierz_xi_poczatek.getLengthX() < 1) ||
				(macierz_xi_koniec == null) || (macierz_xi_koniec.getLengthY() < 1) || (macierz_xi_koniec.getLengthX() < 1)) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		Macierz macierz_ad = null;
		Macierz macierz_mo = modalna(typszeregu, macierz_xi, macierz_xi_poczatek, macierz_xi_koniec, macierz_ni);
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		Macierz macierz_d = PodstawoweStatystyki.odchyleniePrzecietne(typszeregu, macierz_xi, macierz_ni);
		if (typszeregu == 0) {
			macierz_ad = (Macierz) Znak.podzielSkalarnieElementy(Znak.odejmijElementy(macierz_xi_srednie, macierz_mo), macierz_d);
		} else if (typszeregu <= 2) {
			macierz_ad = (Macierz) Znak.podzielSkalarnieElementy(Znak.odejmijElementy(macierz_xi_srednie.getValueAt(0, 0), macierz_mo), macierz_d);
		}
		return macierz_ad;
	}

	/**
	 * Oblicza współczynnik asymetrii A.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni
	 * @return wektor współczynników
	 */
	public static Macierz wspolczynnikAsymetriiA(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		//Macierz macierz_sredniearytmetyczne = sredniaArytmetyczna(typszeregu, macierz_xi, macierz_ni);
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		Macierz ximinusxsr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_xi, macierz_xi_srednie);
		Macierz ximinusxsrszescian = (Macierz) Znak.pomnozSkalarnieElementy(Znak.pomnozSkalarnieElementy(ximinusxsr, ximinusxsr), ximinusxsr);
		Macierz ximinusxsrszescianni = (Macierz) Znak.pomnozSkalarnieElementy(macierz_ni, ximinusxsrszescian);
		Macierz macierz_m3 = (Macierz) Znak.podzielSkalarnieElementy(PodstawoweStatystyki.suma(ximinusxsrszescianni), PodstawoweStatystyki.suma(macierz_ni));
		Macierz macierz_s = odchylenieStandardowe(wariancja(typszeregu, macierz_xi, macierz_ni));
		Macierz macierz_s3 = (Macierz) Znak.pomnozSkalarnieElementy(macierz_s, Znak.pomnozSkalarnieElementy(macierz_s, macierz_s));
		//result2.print("m3: " + macierz_m3.toString());
		//result2.print("s^3: " + macierz_s3.toString());
		Macierz macierz_a = (Macierz) Znak.podzielSkalarnieElementy(macierz_m3, macierz_s3);
		return macierz_a;
	}

	/**
	 * Oblicza współczynnik skupienia A.
	 * @param typszeregu
	 * @param macierz_xi xi
	 * @param macierz_ni
	 * @return wektor współczynników
	 */
	public static Macierz wspolczynnikSkupieniaK(int typszeregu, Macierz macierz_xi, Macierz macierz_ni) {
		if ((macierz_xi == null) || (macierz_xi.getLengthY() < 1) || (macierz_xi.getLengthX() < 1) ||
				(macierz_ni == null) || (macierz_ni.getLengthY() < 1) || (macierz_ni.getLengthX() < 1)) {
			throw new InvalidArgumentException("Nieprawidłowy wymiar macierzy");
		}
		//Macierz macierz_sredniearytmetyczne = sredniaArytmetyczna(typszeregu, macierz_xi, macierz_ni);
		Macierz macierz_xi_srednie = PodstawoweStatystyki.srednia(macierz_xi);
		Macierz ximinusxsr = (Macierz) Znak.odejmijSkalarnieElementy(macierz_xi, macierz_xi_srednie);
		Macierz ximinusxsr4 = (Macierz) Znak.pomnozSkalarnieElementy(Znak.pomnozSkalarnieElementy(Znak.pomnozSkalarnieElementy(ximinusxsr, ximinusxsr), ximinusxsr), ximinusxsr);
		Macierz ximinusxsr4ni = (Macierz) Znak.pomnozSkalarnieElementy(macierz_ni, ximinusxsr4);
		Macierz macierz_m4 = (Macierz) Znak.podzielSkalarnieElementy(PodstawoweStatystyki.suma(ximinusxsr4ni), PodstawoweStatystyki.suma(macierz_ni));
		Macierz macierz_s = odchylenieStandardowe(wariancja(typszeregu, macierz_xi, macierz_ni));
		Macierz macierz_s4 = (Macierz) Znak.pomnozSkalarnieElementy(macierz_s, Znak.pomnozSkalarnieElementy(Znak.pomnozSkalarnieElementy(macierz_s, macierz_s), macierz_s));
		Macierz macierz_k = (Macierz) Znak.podzielSkalarnieElementy(macierz_m4, macierz_s4);
		return macierz_k;
	}
}
