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

/**
 *
 * @author dmn
 */
public class ZlotyPodzial {

    private Liczba zmiennaXval;
    private FunkcjaInterface zmiennaX = new FunkcjaInterface() {

        public String toString() {
            return "X";
        }

        public int getArgumentsCount() {
            return 0;
        }

        public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
            return zmiennaXval;
        }
    };

    public static Liczba znajdzX(Liczba y0, Liczba low, Liczba high, Liczba eps, Liczba correction, WyrazeniePostfix f) {
        return (new ZlotyPodzial()).znajdz(y0, low, high, eps, correction, f);
    }

    public static Liczba znajdzX(Liczba y0, Liczba low, Liczba high, Liczba eps, Liczba correction, Funkcja f, int indeksZmienianego, ElementWyrazenia... args) {
        return (new ZlotyPodzial()).znajdz(y0, low, high, eps, correction, f, indeksZmienianego, args);
    }

    public Liczba znajdz(Liczba y0, Liczba low, Liczba high, Liczba eps, Liczba correction, Funkcja ff, int indeksZmienianego, ElementWyrazenia... args) {
        Liczba mid = null;
        //Liczba eps = new Liczba("0,0000000000001");
        Liczba result;
        zmiennaXval = low;
        args[indeksZmienianego] = low;
        Liczba value_low = (Liczba) ff.getValue(args);
        args[indeksZmienianego] = high;
        Liczba value_high = (Liczba) ff.getValue(args);
        int kroki = 0;
        boolean found = false;
        do {
			kroki++;
            //System.out.println((kroki) + ". low=" + low + " high=" + high);
            if (value_low.compareTo(y0) <= 0 && value_high.compareTo(y0) >= 0) {
				//Liczba roznica = (Liczba) Znak.odejmijElementy(value_high, value_low);
				Liczba roznica = (Liczba) Znak.odejmijElementy(high, low);
//                                        mid = (Liczba) Znak.pomnozElementy(Liczba.POL, Znak.dodajElementy(high, low));
                mid = (Liczba) Znak.dodajElementy(low, Znak.pomnozElementy(Liczba.ZLOTA, roznica));
//                                        mid = (Liczba) Znak.dodajElementy(low, Znak.pomnozElementy(Znak.odejmijElementy(Liczba.JEDEN, Liczba.ZLOTA), Znak.odejmijElementy(high, low)));

				if (roznica.compareTo(eps) <= 0) {
                    found = true;
                    break;
                } else {
                    args[indeksZmienianego] = mid;
                    Liczba value_mid = (Liczba) ff.getValue(args);
                    //System.out.println("x=" + mid + " f=" + value_mid);
                    if (value_mid.compareTo(y0) < 0) {
                        low = mid;
                        value_low = value_mid;
                    } else {
                        high = mid;
                        value_high = value_mid;
                    }
                }
            } else {
                if (correction == null || correction.compareTo(Liczba.ZERO) <= 0 || kroki > 2000) {
                    throw new InvalidArgumentException("Złe warunki startowe");
                }
				// rozszerzenie przedziału poszukiwań:
                if (value_high.compareTo(y0) < 0) {
                    high = (Liczba) Znak.dodajElementy(high, correction);
                    args[indeksZmienianego] = high;
                    value_high = (Liczba) ff.getValue(args);
                } else {
                    low = (Liczba) Znak.odejmijElementy(low, correction);
                    args[indeksZmienianego] = low;
                    value_low = (Liczba) ff.getValue(args);
                }
            }
        } while (!found);
        result = mid;
        return result;
    }

    public Liczba znajdz(Liczba y0, Liczba low, Liczba high, Liczba eps, Liczba correction, WyrazeniePostfix f) {
        FunkcjaInterface[] oldFunkcjaInterface = f.getUserArrayOfFunctions();
        f.setUserArrayOfFunctions(new FunkcjaInterface[] {
            zmiennaX
        });
        Funkcja zmiennaXfunkcja = new Funkcja("X", f.getUserArrayOfFunctions());
        ArrayList<ElementWyrazenia> oldMyElements = new ArrayList<ElementWyrazenia>(f.myElements.size());
        for (int i = 0; i < f.myElements.size(); i++) {
            oldMyElements.add(f.myElements.get(i));
            if (f.myElements.get(i).toString().equals("X")) {
                //f.myElements.add(i, (ElementWyrazenia)zmiennaX);
                f.myElements.set(i, zmiennaXfunkcja);
            } else {
            }
        }
        Liczba mid = null;
        //Liczba eps = new Liczba("0,0000000000001");
        Liczba result;
        zmiennaXval = low;
        Liczba value_low = (Liczba) f.getValue();
        zmiennaXval = high;
        Liczba value_high = (Liczba) f.getValue();
        int kroki = 0;
        boolean found = false;
        do {
//            System.out.println((++kroki) + ". low=" + low + " high=" + high);
            if (value_low.compareTo(y0) <= 0 && value_high.compareTo(y0) >= 0) {
//                                        mid = (Liczba) Znak.pomnozElementy(Liczba.POL, Znak.dodajElementy(high, low));
                mid = (Liczba) Znak.dodajElementy(low, Znak.pomnozElementy(Liczba.ZLOTA, Znak.odejmijElementy(high, low)));
//                                        mid = (Liczba) Znak.dodajElementy(low, Znak.pomnozElementy(Znak.odejmijElementy(Liczba.JEDEN, Liczba.ZLOTA), Znak.odejmijElementy(high, low)));
                //Liczba roznica = (Liczba) Znak.odejmijElementy(value_high, value_low);
				Liczba roznica = (Liczba) Znak.odejmijElementy(high, low);
				if (roznica.compareTo(eps) <= 0) {
                    found = true;
                    break;
                } else {
                    zmiennaXval = mid;
                    Liczba value_mid = (Liczba) f.getValue();
                    //System.out.println("x=" + mid + " f=" + value_mid);
                    if (value_mid.compareTo(y0) < 0) {
                        low = mid;
                        value_low = value_mid;
                    } else {
                        high = mid;
                        value_high = value_mid;
                    }
                }
            } else {
                if (correction == null || correction.compareTo(Liczba.ZERO) <= 0) {
                    throw new InvalidArgumentException("Złe warunki startowe");
                }
                if (value_high.compareTo(y0) < 0) {
                    high = (Liczba) Znak.dodajElementy(high, correction);
                    zmiennaXval = high;
                    value_high = (Liczba) f.getValue();
                } else {
                    low = (Liczba) Znak.odejmijElementy(low, correction);
                    zmiennaXval = low;
                    value_low = (Liczba) f.getValue();
                }
            }
        } while (!found);
        result = mid;
        f.myElements = oldMyElements;
        f.setUserArrayOfFunctions(oldFunkcjaInterface);
        return result;
    }
}
