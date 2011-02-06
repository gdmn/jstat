/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import java.util.*;

/**
 *
 * @author Damian
 */
public class JavaProperties {

    public static String getSystemProperties() {
        StringBuilder result = new StringBuilder(1000);
        Map<String, String> env = System.getenv();
        TreeMap<String, String> str2 = new TreeMap<String, String>(env);
        String envName = null;
        for (Iterator<String> it = str2.keySet().iterator(); it.hasNext(); envName = it.next()) {
            if (envName != null) {
                result.append(String.format("%s = \"%s\"%n", envName, env.get(envName)));
            }
        }
        return result.toString();
    }

    public static String getJVMProperties() {
        StringBuilder result = new StringBuilder(1000);
        Properties p = System.getProperties();
        SortedSet<String> str = new TreeSet<String>(p.stringPropertyNames());
        for (String s : str) {
            result.append(String.format("%s = \"%s\"%n", s, System.getProperty(s)));
        }

        return result.toString();
    }

    public static String getJavaProperties() {
        return getSystemProperties() + getJVMProperties();
    }

    public static void main(String[] args) {
        System.out.println(getJavaProperties());
    }
}
