/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import java.io.*;

/**
 *
 * @author Damian
 */
public class Log {

    private static ColoredTextPane log = null;
    private static TestStream myOut;
    private static TestStream myErr;

    public Log(ColoredTextPane log, boolean captureOutputs) {
        if (Log.log != null) {
        //throw new RuntimeException("Próba utworzenia drugiego Log");
        } else {
            Log.log = log;
            // Discover the name of the class this
            // object was created within:
            // String className = new Throwable().getStackTrace()[1].getClassName();
            // testStream = new TestStream(className);
            myOut = new TestStream() {

                private PrintStream old;

                public void afterCreated() {
                    old = System.out;
                }

                public void print(String s) {
                    //old.print(s);
                    Log.printNoCR(s, ColoredTextPane.REGULAR);
                }
            };
            if (captureOutputs) {
                try {
                    System.setOut(myOut);
                } catch (SecurityException w) {
                }
            }

            myErr = new TestStream() {

                private PrintStream old;

                public void afterCreated() {
                    old = System.err;
                }

                public void print(String s) {
                    //old.print(s);
                    Log.printNoCR(s, ColoredTextPane.ERROR);
                }
            };
            if (captureOutputs) {
                try {
                    System.setErr(myErr);
                } catch (SecurityException w) {
                }
            }
        }
    }

    public static PrintStream getOut() {
        return myOut;
    }

    public static PrintStream getErr() {
        return myErr;
    }

    /**
     * @param line
     * @see jstat.ColoredTextPane#print(java.lang.String)
     */
    public static void print(String line) {
        log.print(line);
    }

    /**
     * @param line
     * @see jstat.ColoredTextPane#printerr(java.lang.String)
     */
    public static void printerr(String line) {
        log.printerr(line);
    }

    /**
     * @param line
     * @param style
     * @see jstat.ColoredTextPane#printNoCR(java.lang.String, java.lang.String)
     */
    public static void printNoCR(String line, String style) {
        log.printNoCR(line, style);
    }

    /**
     * @param line
     * @param style
     * @see jstat.ColoredTextPane#print(java.lang.String, java.lang.String)
     */
    public static void print(String line, String style) {
        log.print(line, style);
    }

    public static void clear() {
        log.setText("");
    }
}
