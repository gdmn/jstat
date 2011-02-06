/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author dmn
 */
public class OSTools {

    /** separator ścieżki */
    static public final String FILESEPARATOR = System.getProperty("file.separator");

    static public String includeTrailingFileSeparator(String s) {
        String l = s.substring(s.length() - 1, s.length());
        if (l.equals(FILESEPARATOR)) {
            return s;
        }
        return s + FILESEPARATOR;
    }

    static public String excludeTrailingFileSeparator(String s) {
        String l = s.substring(s.length() - 1, s.length());
        if (l.equals(FILESEPARATOR)) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static void command(String... command) {
        boolean err = false;
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader results = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String s;
            while ((s = results.readLine()) != null) {
                System.out.println(s);
            }
            BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((s = errors.readLine()) != null) {
                System.err.println(s);
                err = true;
            }
        } catch (Exception e) {
            if (!command[0].equalsIgnoreCase("cmd")) {
                command("cmd", "/C", command[0]);
            } else {
                throw new RuntimeException(e);
            }
        }
        if (err) {
            throw new OSExecuteException("Błąd przy uruchamianiu " + command);
        }
    }

    public static boolean existsURL(URL u) {
        InputStream c;
        try {
            c = u.openStream();
            if (c != null) {
                c.close();
                return true;
            }
        } catch (IOException ex) {
            return false;
        }
        return false;
    }

    public static void main(String[] args) {
        command("sh", "-c", "ls; df -h");
    }
}

class OSExecuteException extends RuntimeException {

    public OSExecuteException(String why) {
        super(why);
    }
}
