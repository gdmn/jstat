/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package tests;

/**
 *
 * @author dmn
 */
public class Porownanie {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String a = "aaa";
        String b = "aaar";
        b = "aa" +"a";
        System.out.println("" + a.equals(b));
        System.out.println("" + (a == b));
    }
}
