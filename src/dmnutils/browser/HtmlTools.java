/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.browser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author dmn
 */
public class HtmlTools {
    public static String readLink(URL url) throws IOException {
        StringBuilder result = new StringBuilder();
        URLConnection connection = url.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                connection.getInputStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
//            System.out.println(inputLine);
            result.append(inputLine+"\n");
        }
        in.close();
        return result.toString();
    }
}
