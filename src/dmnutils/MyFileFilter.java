/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import java.io.File;
import java.io.IOException;

/**
 * Filtr do otwierania/zapisywania dokumentów
 * @author dmn
 */
public class MyFileFilter extends javax.swing.filechooser.FileFilter {

    private String[] extensions;
    private String description;

    public MyFileFilter(String... acceptableExtensions) {
        this.extensions = acceptableExtensions;
        StringBuilder b = new StringBuilder("Dokumenty ");
        for (int i = 0; i < extensions.length; i++) {
            if (i > 0) {
                b.append(", ");
            }
            String s = extensions[i];
            extensions[i] = s.toLowerCase();
            s = s.toUpperCase();
            b.append(s);
        }
        description = b.toString();
    }

    //Accept all directories and all gif, jpg, tiff, or png files.
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = getExtension(f);
        if (extension != null) {
            for (String s : extensions) {
                if (s.toLowerCase().equals(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    //The description of this filter
    public String getDescription() {
        return description;
    }

    /**
     * Get the extension of a file.
     */
    public static String getExtension(File f) {
        if (f == null) {
            return null;
        }
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public String setDefaultExtension(File f) {
        String ext = getExtension(f);
        String s = null;
        try {
            s = f.getCanonicalPath().toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //System.out.println("plik bez rozszerzenia: "+s);
        if (extensions != null && extensions.length > 0) {
            if ((ext == null) || (!accept(f))) {
                return s + "." + extensions[0];
            }
        }
        return s;
    }
}
