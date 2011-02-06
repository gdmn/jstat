/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.gui;

import dmnutils.OSTools;
import dmnutils.PanelHtml;
import dmnutils.SwingUtils;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import jstat.pages.html.PageHtmlOutput;
import org.xml.sax.SAXException;

/**
 *
 * @author dmn
 */
public class Help extends PanelHtml {
    public static String getHelpDirectory() {
        //String result = getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        String result = (new jstat.EmptyClass()).getClass().getProtectionDomain().getCodeSource().getLocation().toString();
        if (result.substring(result.length() - 14).equals("build/classes/")) {
            result = result.substring(0, result.length() - 14);
        } else if (result.substring(result.length() - 9).toLowerCase().equals("jstat.jar")) {
            result = result.substring(0, result.length() - 9);
        }
        return result;
    }

    public static URL getAddrInJar(String htmlfile) {
        String location = "help/" + htmlfile;
        java.net.URL u;
        if (htmlfile.indexOf("#") == -1) {
//            System.out.println(location);
            u = jstat.EmptyClass.class.getResource(location);
        } else {
            location = location.substring(0, location.indexOf("#"));
//            System.out.print(location + " ");
            u = jstat.EmptyClass.class.getResource(location);
            try {
                u = new URL(u.toString() + htmlfile.substring(htmlfile.indexOf("#"), htmlfile.length()));
//                System.out.println(u.toString());
            } catch (MalformedURLException ex) {
                Logger.getLogger(Help.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (u == null) {
            //System.err.println("Help not found: " + location);
        } else {
        }
        return u;
    }

    /**
     * Pobiera adres pomocy, czyli pliku index.html.
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static URL getAddr() throws FileNotFoundException {
        return getAddr("index.html");
    }

    /**
     * Pobiera adres pomocy.
     * @param htmlfile np. "funkcje.html"
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static URL getAddr(String htmlfile) throws FileNotFoundException {
        try {
            final String plik = "help/" + htmlfile;//index.html";

            boolean ok = false;
            String d = getHelpDirectory();
            String d2 = OSTools.includeTrailingFileSeparator((new File(d)).getParent());
            URL[] urls;
            if (jstat.Settings.get().getPreferOnlineHelp()) {
                urls = new URL[]{new URL("http://gdamian.ovh.org/jstat/help/index.html"), new URL(d + plik), new URL(d2 + plik), getAddrInJar(htmlfile),};
            } else {
                urls = new URL[]{new URL(d + plik), new URL(d2 + plik), getAddrInJar(htmlfile), new URL("http://gdamian.ovh.org/jstat/help/index.html"),};
            }
            for (URL url : urls) {
                if (url != null && OSTools.existsURL(url)) {
                    return url;
                }
            }
            StringBuilder errorString = new StringBuilder();
            errorString.append("Nie można znaleźć pliku pomocy, szukano w:");
            for (URL url : urls) {
                errorString.append("\n- " + url.toString());
            }
            throw new FileNotFoundException(errorString.toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(Help.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private URL getAddrJavadoc() {
        try {
            return new URL(getHelpDirectory() + "javadoc/index.html");
        } catch (MalformedURLException ex) {
            Logger.getLogger(Help.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Help() throws FileNotFoundException {
        int inset = 150;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(screenSize.width - inset * 2, screenSize.height - inset * 2));
        java.net.URL u = getAddr();
        // location = "../projectplugins/first.bsh";
        if (u != null) {
            try {
                System.out.println("Renderowanie " + u);
                render(u);
            } catch (IOException ex) {
                Logger.getLogger(PageHtmlOutput.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(PageHtmlOutput.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        try {
            Help h = new Help();
            SwingUtils.classForImages = jstat.EmptyClass.class;
            if (h != null) {
                dmnutils.SwingUtils.run(h);
            } else {
                System.exit(1);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Help.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
