/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.pages.notepad;

import dmnutils.*;
import dmnutils.document.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Strona z informacjami i wynikami obliczeń.
 * @author dmn
 */
public class PageNotepad extends AbstractDocumentPage<PageNotepad> implements Externalizable {

    protected ColoredTextPane textArea;

    public PageNotepad() {
        super();
        setCaption("Notatnik");
        constructorHelper();
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    protected void constructorHelper() {
        textArea = new ColoredTextPane();
        textArea.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                PageNotepad.this.changed();
            }

            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }

            public void changedUpdate(DocumentEvent e) {
                insertUpdate(e);
            }
        });
        textArea.setPreferredSize(new Dimension(500, 350));
        JScrollPane scrollPane = new JScrollPane(textArea);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 0, 5, 5));
        add(scrollPane, BorderLayout.CENTER);


        FocusListener foc = new FocusListener() {

            public void focusGained(FocusEvent e) {
                textArea.requestFocusInWindow();
            }

            public void focusLost(FocusEvent e) {
            }
        };

        for (Component o : Arrays.asList(this, textArea)) {
            o.addFocusListener(foc);
        }

    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(textArea.getText());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        constructorHelper();
        textArea.setText((String) in.readObject());
    }

    /**
     * Wypisuje wiadomość w podanym stylu
     * (delegacja do ColoredTextPane)
     */
    public void print(String line, String style) {
        textArea.print(line, style);
    }

    /**
     * Wypisuje wiadomość w podanym stylu bez znaku nowej lini
     * (delegacja do ColoredTextPane)
     */
    public void printNoCR(String line, String style) {
        textArea.printNoCR(line, style);
    }

    /**
     * Wypisuje zwykłą wiadomość
     * (delegacja do ColoredTextPane)
     */
    public void print(String line) {
        textArea.print(line);
    }

    /**
     * Wypisuje wiadomość o błędzie
     * (delegacja do ColoredTextPane)
     */
    public void printerr(String line) {
        textArea.printerr(line);
    }

    public ColoredTextPane getTextArea() {
        return textArea;
    }

    public static DocumentPageFactory<PageNotepad> factory = new DocumentPageFactory<PageNotepad>() {

        @Override
        public PageNotepad create(Document doc) {
            PageNotepad result;
            result = new PageNotepad();
            return result;
        }
    };
}
