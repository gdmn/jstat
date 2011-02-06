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
public class Nawiasy extends ElementWyrazenia {
    private boolean openBracket;

    /** Creates a new instance of Nawiasy */
    public Nawiasy(String str) {
        super(str);
        openBracket = str.equals("(");
    }

    /**
     * Zwraca true jeśli nawias otwierający, false jeśli zamykający.
     */
    public boolean isOpening() {
       return openBracket;
    }

    public int getPriority() { return 0; }

    public int getArgumentsCount() {
        return 0;
    }

    public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
        throw new InvalidArgumentException("Metoda ElementWyrazenia getValue(ElementWyrazenia[] args) jest niepoprawna dla klasy "+this.getClass().getName());
    }

}
