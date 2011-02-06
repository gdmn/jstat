/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.browser;

import java.net.*;
import java.awt.Desktop;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmn
 */
public class SystemBrowser {
    public static boolean browse(URI uri) {
        // Try using the Desktop api first
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(uri);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean browse(String uri) {
        try {
            browse(new URI(uri));
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

	public static boolean mailto(URI uri) {
        // Try using the Desktop api first
        try {
            Desktop desktop = Desktop.getDesktop();
            desktop.mail(uri);
            return true;
        } catch (Exception e) {
        }
        return false;
	}
	public static boolean mailto(String uri) {
        try {
            mailto(new URI(uri.indexOf("mailto:") == 0 ? uri : "mailto:"+uri));
            return true;
        } catch (Exception ex) {
        }
        return false;
	}


}
