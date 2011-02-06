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
 * @author Damian
 *
 */
public class Tekst extends ElementWyrazenia {

    public Tekst(String str) {
        super("\""+str+"\"");
    }

    /* (nie jest to dokumentacja Javadoc)
     * @see kalkulator.ElementWyrazenia#getArgumentsCount()
     */
    public int getArgumentsCount() {
        return 0;
    }

    public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
        throw new InvalidArgumentException("Metoda ElementWyrazenia getValue(ElementWyrazenia[] args) jest niepoprawna dla klasy "+this.getClass().getName());
    }
}
