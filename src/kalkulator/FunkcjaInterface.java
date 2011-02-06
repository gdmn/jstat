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
 * Interfejs z metodami: <br>
 * getArgumentsCount<br>
 * getValue<br>
 *
 * @author Damian
 */
public interface FunkcjaInterface extends java.io.Serializable {
    public int getArgumentsCount();
    public ElementWyrazenia getValue(ElementWyrazenia... args) throws InvalidArgumentException;
}
