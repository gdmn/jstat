/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package tests;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dmn
 */
public class Permutacje {

    private static int newton(int n, int k) {
        int i1 = 1;
        for (int i = 2; i <= n; i++) {
            i1 *= i;
        }
        int i2 = 1;
        for (int i = 2; i <= k; i++) {
            i2 *= i;
        }
        int i3 = 1;
        for (int i = 2; i <= n - k; i++) {
            i3 *= i;
        }
        return i1 / (i2 * i3);
    }
    public static void permutation(int k, List s) {
        int factorial = 1;
        for (int j = 2; j <= s.size(); j++) {
            factorial = factorial * (j - 1);        // factorial= (j-1)!
            int l1 = j - ((k / factorial) % j) -1;
            int l2 = j -1;
            Object t;
            t = s.get(l1);
            s.set(l1, s.get(l2));
            s.set(l2, t);
        }
    }
    public static void permutation(int k, int z, List s) {
        int factorial = 1;
        k = k*z/s.size();
        permutation(k, s);
        if (false)
        for (int j = 2; j <= s.size(); j++) {
            factorial = factorial * (j - 1);        // factorial= (j-1)!
            int l1 = j - ((k / factorial) % j) -1;
            int l2 = j -1;
            Object t;
            t = s.get(l1);
            s.set(l1, s.get(l2));
            s.set(l2, t);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<Integer> array = new ArrayList(6);
        for (int i = 0; i < 6; i++) {
            array.add(i+1);
        }
        for (int k = 0; k < 120+1; k++) {
            ArrayList t = (ArrayList)array.clone();
            permutation(k, 4, t);
            System.out.println(""+t.toString()+" " + array);
        }


    }
}
