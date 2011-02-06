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
public class Tools {

    /** Creates a new instance of Tools */
    public Tools() {
    }

    public static double getMin(double... args) {
        //if (args.length == 0) return Double.NaN;
        double result = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] < result) {
                result = args[i];
            }
        }
        return result;
    }

    public static int getMin(int... args) {
        //if (args.length == 0) throw new ArrayIndexOutOfBoundsException
        int result = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] < result) {
                result = args[i];
            }
        }
        return result;
    }

    public static double getMax(double... args) {
        //if (args.length == 0) return Double.NaN;
        double result = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] > result) {
                result = args[i];
            }
        }
        return result;
    }

    public static int getMax(int... args) {
        //if (args.length == 0) throw new ArrayIndexOutOfBoundsException
        int result = args[0];
        for (int i = 1; i < args.length; i++) {
            if (args[i] > result) {
                result = args[i];
            }
        }
        return result;
    }

}
