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
 * Jeśli nawiasy są nierozwijalne, to znaczy, że zawartość ich nie ulega
 * automatycznemu obliczeniu przed przekazaniem argumentów do funkcji.
 * Dzięki temu można obliczyć całkę.
 * @author dmn
 */
public class NawiasyNierozwijalne extends Nawiasy {

    /** Creates a new instance of NawiasyNierozwijalne */
    public NawiasyNierozwijalne(String str) {
        super(str);
    }

}
