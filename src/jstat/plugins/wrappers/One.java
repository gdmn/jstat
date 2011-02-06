/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.plugins.wrappers;

import kalkulator.Funkcja;
import kalkulator.Funkcja.Functions;

/**
 * Implementacja funkcji dla skryptów na "poziomie zerowym".
 * @author dmn
 */
public class One {

    //public static ElementWyrazenia snedecoratail(ElementWyrazenia arg0, ElementWyrazenia arg1, ElementWyrazenia arg2) {return Funkcja.getValue(Functions.SNEDECORATAIL, arg0, arg1, arg2); }
    /**
     * Podstawowe
     * @return
     */
    public static String get() {
        StringBuilder result = new StringBuilder();
        result.append("import kalkulator.*; import dmnutils.*; import dmnutils.browser.*; " +
                "import kalkulator.Funkcja.Functions; import jstat.plugins.wrappers.*; ");
        result.append("import wykres.*; ");
        result.append("import java.math.*; ");
        result.append("import jstat.pages.bean.PageBean; import jstat.pages.bsh.PageBeanShell; import jstat.pages.calculator.PageCalculator; import jstat.pages.html.PageHtmlOutput; import jstat.pages.notepad.PageNotepad; import jstat.pages.sheet.PageSheet;  ");
        result.append("public static ElementWyrazenia calc(String fromUser) { Tools.calc(fromUser); } ");
        //result.append("public static void addImports() { plugin.addAllImports(); } ");
        result.append("public static void print(String s) { if (s != null) System.out.println(s); else System.out.println(\"null\"); } "); // jak przekierowujemy interpreter.setOut() to nie działa normalnie
        result.append("public static ElementWyrazenia funkcjaGetValue(Functions funkcja, ElementWyrazenia[] args) throws InvalidArgumentException { " +
                "Funkcja f = new Funkcja(funkcja, null);  return f.getValue(args); }");
        for (Functions i : Functions.values()) {
            String f = i.toString();
            result.append("public static ElementWyrazenia " + f.toLowerCase() + "(");
            for (int j = 0; j < new Funkcja(i).getArgumentsCount(); j++) {
                if (j > 0) {
                    result.append(", ");
                }
                result.append("ElementWyrazenia arg" + j);
            }
            result.append(") { ");
            //public static ElementWyrazenia abs(ElementWyrazenia arg0) { return fgetValue(Functions.ABS, new ElementWyrazenia[] {arg0} ); }
            result.append("return funkcjaGetValue(Functions." + f + ", new ElementWyrazenia[] {");
            for (int j = 0; j < new Funkcja(i).getArgumentsCount(); j++) {
                if (j > 0) {
                    result.append(", ");
                }
                result.append("arg" + j);
            }
            //Funkcja.getValue(Functions.GAMMA, args)
            result.append("} ); }\n");
        }
        /* Znak.* */
        String[] t = new String[] {"dodaj", "dodajSkalarnie", "pomnoz", "pomnozSkalarnie",
                                   "odejmij", "odejmijSkalarnie", "podziel", "podzielSkalarnie",
                                   "poteguj"
        };
        for (String s : t) {
            result.append(znakdotfunkcja2(s));
        }
        t = new String[] {"przeciwny"
        };
        for (String s : t) {
            result.append(znakdotfunkcja1(s));
        }
        //System.out.println(result.toString()); // w jstat.pages.bsh.PageBeanShell.runScript()
        return result.toString();
    }

    private static String znakdotfunkcja1(String fcja) {
        StringBuilder result = new StringBuilder();
        final String EW = "ElementWyrazenia";
        final String ED = "double";
        String[][] t = new String[][] {{EW}, {ED}};
        for (String[] s : t) {
            result.append("public static ElementWyrazenia ");
            result.append(fcja + "(");
            result.append(s[0]);
            result.append(" args0) { return Znak.");
            result.append(fcja);
            result.append("Element(");
            for (int i = 0; i < s.length; i++) {
                if (i > 0) {
                    result.append(", ");
                }
                if (s[i] != EW) {
                    result.append("new Liczba(args" + i + ")");
                } else {
                    result.append("args" + i + "");
                }
            }
            result.append("); }\n");
        }
        return result.toString();
    }

    private static String znakdotfunkcja2(String fcja) {
        StringBuilder result = new StringBuilder();
        final String EW = "ElementWyrazenia";
        final String ED = "double";
        String[][] t = new String[][] {{EW, EW}, {ED, ED}, {EW, ED}, {ED, EW}};
        for (String[] s : t) {
            result.append("public static ElementWyrazenia ");
            result.append(fcja + "(");
            result.append(s[0]);
            result.append(" args0, ");
            result.append(s[1]);
            result.append(" args1) { return Znak.");
            result.append(fcja);
            result.append("Elementy(");
            for (int i = 0; i < s.length; i++) {
                if (i > 0) {
                    result.append(", ");
                }
                if (s[i] != EW) {
                    result.append("new Liczba(args" + i + ")");
                } else {
                    result.append("args" + i + "");
                }
            }
            result.append("); }\n");
        }
        return result.toString();
    }
}
