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
 * Wyjątek wyrzucany, gdy na stosie występuje nieodpowiedni argument lub zła ich ilość.
 * @author Damian
 */
public class InvalidArgumentException extends java.lang.ArithmeticException { //java.lang.Exception {

    /** Creates a new instance of InvalidArgumentException */
    public InvalidArgumentException() {
        super();
    }

    public InvalidArgumentException(String msg) {
        super(msg);
    }
}
