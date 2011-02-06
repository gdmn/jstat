/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package tests;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import kalkulator.ElementWyrazenia;
import kalkulator.Macierz;
import kalkulator.WyrazeniePostfix;

/**
 *
 * @author dmn
 */
public class MacierzTest {

    /** Creates a new instance of MacierzTest */
    public MacierzTest() {
    }

    public static void main(String[] args) {
        String[] inputstrings = new String[] {
            //"2/3",
            //"odwr(hilbert(7))",
            //"hilbert(7)^2",
            "{{2;3}{4;22}}^(-1)",
            "inv({{2;3}{4;22}})",
            "({{2;3}{4;22}}^(-1))^2",
            "{{2;3}{4;22}}^(-2)"
        };
        //LinkedList<String> input = new LinkedList<String>(Arrays.<String>asList(new String[] {"a"}));
        List<String> input = new LinkedList<String>(Arrays.<String>asList(inputstrings));
        for (int x = 50; x <= 60; x++) {
            input.add("normalny(0; 1; " + x + ")");
        }
        ElementWyrazenia output[] = new ElementWyrazenia[input.size()];
        long l1 = System.nanoTime();
        int j = 0;
        for (String s : input) {
            output[j++] = WyrazeniePostfix.valueOf(s).getValue();
        }
        long l2 = System.nanoTime();
        for (int i = 0; i < j; i++) {
            System.out.println(input.get(i) + " = " + output[i]);
        }
        System.out.println("czas: " + ((double) (l2 - l1) / 1000000000) + " sek.");

        Macierz dst = Macierz.valueOf("{{1;2;3;44}{4;5;6;45}{7;8;9;46}}", null),
                src = Macierz.valueOf("{{100;200}{300;400}}", null);
        Macierz wklejona = Macierz.wklej(dst, src, 0, 0);
        Macierz wycieta = Macierz.pobierz(dst, 1, 0, 3, 3);
        System.out.println("wklej: " + wklejona);
        System.out.println("pobierz: " + wycieta);
//2067909047925770649600000
    }
}
