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
 * @author Damian
 */
public abstract class ElementWyrazenia implements java.io.Serializable {
    transient protected String stringRepresentation;

    /** Creates a new instance of ElementWyrazenia */
    public ElementWyrazenia(String str) {
        this.stringRepresentation = str;
    }

    public String toString() {
        return stringRepresentation;
    }

    public static String getArgumentsInfo(ElementWyrazenia... args) {
        if (args == null) return null;
        StringBuilder result = new StringBuilder(5*args.length);
        for (ElementWyrazenia k : args)
            result.append(", "+k.getClass().getSimpleName());
        return result.delete(0, 2).append(')').insert(0, '(').toString();
    }

    /**
     * Priorytet elementu przy przekształcaniu infix -> postfix.
     * @return Priorytet.
     */
    public int getPriority() { return 0; };

    /**
     * Liczba argumentów do obliczenia wartości.
     * @return Dla funkcji z jednym argumentem
     * zwraca 1, dla funkcji z dwoma - 2, itd. Dla liczby zwraca 0.
     */
    abstract public int getArgumentsCount();

    /**
     * Oblicza wynik w oparciu o args.
     * @return Wynik obliczeń.
     * @param args Tablica z parametrami.
     */
    abstract public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException;

}
