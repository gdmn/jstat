/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import javax.swing.*;
import javax.swing.text.*;

/**
 * JTextPane z metodami do automatycznego stosowania stylu, np. pokolorowany lub pogrubiony
 * @author Damian
 */
public class ColoredTextPane extends JTextPane {

    /**
     *
     */
    public ColoredTextPane() {
        addStylesToDocument(getStyledDocument());
    }

    /**
     * Styl zwykły
     */
    public static final String REGULAR = "regular";

    /**
     * Styl pochylony
     */
    public static final String ITALIC  = "italic";

    /**
     * Styl pogrubiony
     */
    public static final String BOLD    = "bold";

    /**
     * Styl błędu
     */
    public static final String ERROR   = "error";

    /**
     * Styl napisu wprowadzonego przez użytkownika
     */
    public static final String USER    = "user";

    protected static void addStylesToDocument(StyledDocument doc) {
        // Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle(REGULAR, def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle(ITALIC, regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle(BOLD, regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle(ERROR, regular);
        StyleConstants.setForeground(s, java.awt.Color.RED);

        s = doc.addStyle(USER, regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, java.awt.Color.BLUE);
    }

    /**
     * Wypisuje wiadomość w podanym stylu
     */
    public void print(String line, String style) {
        printNoCR(line + "\n", style);
    }

    /**
     * Wypisuje wiadomość w podanym stylu bez znaku nowej lini
     */
    public void printNoCR(String line, String style) {
            // log.append(line + "\n");
            StyledDocument doc = getStyledDocument();
            try {
                doc.insertString(doc.getLength(), line, doc.getStyle(style));
                // Make sure the new text is visible, even if there
                // was a selection in the text area.
                setCaretPosition(getDocument().getLength());
            } catch (BadLocationException ex) {
                // ex.printStackTrace();
            }
    }

    /**
     * Wypisuje zwykłą wiadomość
     */
    public void print(String line) {
        print(line, REGULAR);
    }

    /**
     * Wypisuje wiadomość o błędzie
     */
    public void printerr(String line) {
        print(line, ERROR);
    }
}
