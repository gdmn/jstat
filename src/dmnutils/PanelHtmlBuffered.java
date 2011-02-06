/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import jstat.pages.html.PageHtmlOutput;
import org.xml.sax.SAXException;

/**
 *
 * @author dmn
 */
public class PanelHtmlBuffered extends PanelHtml implements Externalizable {

    private StringBuffer buffer;
    private boolean updating;

    public PanelHtmlBuffered() {
        super();
        constructorHelper();
    }

    public StringBuffer getBuffer() {
        return buffer;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(buffer);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        constructorHelper();
        buffer = (StringBuffer) in.readObject();
//        System.out.println("wczytano:"+buffer.toString());
        updating = false;
        bufferChanged();
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    protected void constructorHelper() {
        clear();
    }

    /**
     * Rozpoczyna zapisywanie wiadomości do bufora.
     */
    public void beginUpdate() {
        updating = true;
    }

    /**
     * Kończy zapisywanie wiadomości do bufora i odrysowuje panel.
     */
    public void endUpdate() {
        updating = false;
        bufferChanged();
    }

    public void checkUpdating() {
        if (!updating) {
            throw new RuntimeException("Najpierw trzeba wywołać metodę PanelHtmlBuffered.beginUpdate()");
        }
    }

    /**
     * Odrysowuje panel
     */
    private void bufferChanged() {
        try {
            render(new StringReader(buffer.toString()), null);
        } catch (SAXException ex) {
            Logger.getLogger(PageHtmlOutput.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PageHtmlOutput.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Czyści panel z tekstu.
     */
    public void clear() {
        buffer = new StringBuffer();
        //buffer.append("<p style=\"color: red\">To jest jakiś tekst</p>");
        bufferChanged();
    }

    /**
     * Dodaje tekst do bufora
     */
    public void print(String line) {
        checkUpdating();
        buffer.append(line);
    }


}
