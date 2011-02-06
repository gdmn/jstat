/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.document;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import javax.swing.JPanel;

/**
 * Identyfikuje wszystkie podstrony, jakie można dodać do dokumentu
 * @author Damian
 */
public abstract class AbstractDocumentPage<T extends AbstractDocumentPage> extends JPanel implements Externalizable {

    /**
     * Panel zawierający stronę
     */
    final public JPanel getPanel() {
        return this;
    }
	
    private Document parentDocument;

    /**
     * Ustawia dokument będący właścicielem strony reprezentowanej przez tą klasę.
     * @param parent
     */
    void setParentDocument(Document parent) {
        this.parentDocument = parent;
    }

    /**
     * Pobiera dokument, do którego należy bieżąca strona.
     * @return klasa typu <code>Document</code>.
     */
    public Document getParentDocument() {
        return parentDocument;
    }

    /**
     * Zaznacza dokument jako zmieniony.
     */
    public void changed() {
        if (parentDocument != null) {
            parentDocument.changed();
        } else {
            new RuntimeException("parentDocument == null");
        }
    }
    private String caption = "AbstractDocumentPage";
    public static final String PROP_CAPTION = "caption";

    /**
     * Get the value of caption
     *
     * @return the value of caption
     */
    public String getCaption() {
        return this.caption;
    }

    /**
     * Set the value of caption
     *
     * @param newcaption new value of caption
     * @throws java.beans.PropertyVetoException
     */
    public void setCaption(String newcaption) {
        String oldcaption = caption;
        this.caption = newcaption;
        firePropertyChange(PROP_CAPTION, oldcaption, newcaption);
        changed();
    }

    @Override
    final public String toString() {
        return getCaption();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(caption);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //constructorHelper();
        caption = (String) in.readObject();
    }
}
