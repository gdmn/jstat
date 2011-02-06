/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.document;

import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;

/**
 * Klasa reprezentująca plik programu. Dziedziczy z <code>LinkedList</code> i
 * zawiera listę wszystkich podstron dokumentu.
 *
 * @author Damian
 */
public class Document extends LinkedList<AbstractDocumentPage> {// implements
    // Externalizable
    // {
    /**
     * Aktualnie zaznaczona, czyli otwarta strona dokumentu. Informacja
     * aktualna, o ile <code>MyInternalFrame</code> wykonuje update.
     */
    transient private int selected;
    /** nazwa pliku */
    transient private String fileName = null;
    /**
     * Czy zmieniono zawartość dokumentu?
     */
    transient private boolean changed = false;

    /** Creates a new instance of Document */
    public Document() {
        constructorHelper();
    }

    /**
     * TESTOWA PROCEDURA <br>
     * zapisuje obiekt do pliku (kompresja gzip)
     */
    public final void serializeMe(String fn) throws FileNotFoundException {
        /*
         * XMLEncoder out = new XMLEncoder(new BufferedOutputStream(new
         * FileOutputStream(fn))); out.writeObject(this); out.close();
         */
        System.out.println("Zapisywanie "+fn);
        beanutils.BeanSaverLoader.saveBean(fn, this);
        setChanged(false);
        setFileName(fn);
    }

    /**
     * TESTOWA PROCEDURA <br>
     * wczytuje obiekt z pliku (kompresja gzip)
     */
    public static final Document unSerializeMe(String fn) throws FileNotFoundException {
        /*
         * XMLDecoder in = null; Document b = null; //in = new
         * ObjectInputStream(new GZIPInputStream(new FileInputStream(fn))); in =
         * new XMLDecoder(new BufferedInputStream(new FileInputStream(fn))); try {
         * b = (Document) in.readObject(); } finally { in.close(); } return b;
         */
        System.out.println("Wczytywanie "+fn);
        Document d = (Document) beanutils.BeanSaverLoader.loadBean(fn);
        d.setFileName(fn);
        d.constructorHelper();
        return d;
    }

    /*
     * public void writeExternal(ObjectOutput out) throws IOException {
     * //super.writeExternal(out); out.writeObject((LinkedList<AbstractDocumentPage>)this); }
     *
     * public void readExternal(ObjectInput in) throws IOException,
     * ClassNotFoundException { this.addAll((LinkedList<AbstractDocumentPage>)
     * in.readObject()); constructorHelper(); }
     */

    // kontrola serializacji:
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        constructorHelper();
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz w
     * konstruktorze, wspólna ich część jest w tutaj
     */
    private void constructorHelper() {
        // System.out.println("dmnutils.document.Document.constructorHelper()");
        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
        for (AbstractDocumentPage e : this) {
            e.setParentDocument(this);
        }
        setChanged(false);
    }

    @Override
    public boolean add(AbstractDocumentPage e) {
        boolean result = super.add(e);
        e.setParentDocument(this);
        setChanged(true);
        notifyCountChange(size());
        return result;
    }

    @Override
    public void add(int index, AbstractDocumentPage element) {
        super.add(index, element);
        element.setParentDocument(this);
        setChanged(true);
        notifyCountChange(size());
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        //setSelected((index < size()-1) ? index+1 : index-1);
        setSelected(-1);
        boolean result = super.remove(o);
        ((AbstractDocumentPage) o).setParentDocument(null);
        setChanged(true);
        notifyCountChange(size());
        setSelected(Math.min(size()-1, index));
        return result;
    }

    @Override
    public AbstractDocumentPage remove(int index) {
        //setSelected((index < size()-1) ? index+1 :index-1);
        setSelected(-1);
        AbstractDocumentPage result = super.remove(index);
        result.setParentDocument(null);
        setChanged(true);
        notifyCountChange(size());
        setSelected(Math.min(size()-1, index));
        return result;
    }

    /**
     * Aktualnie zaznaczona, czyli otwarta strona dokumentu. Informacja
     * aktualna, o ile <code>MyInternalFrame</code> wykonuje update.
     */
    public int getSelected() {
        return selected;
    }

    /**
     * Aktualnie zaznaczona, czyli otwarta strona dokumentu. Informacja
     * aktualna, o ile <code>MyInternalFrame</code> wykonuje update.
     */
    public void setSelected(int selected) {
        int oldSelected = this.selected;
        this.selected = selected;
        if (propertyChangeSupport != null && selected != oldSelected) {
            // jak usuwa stronę, to zmienia się ilość podstron w dokumencie,
            // ale nie zmienia się index zaznaczonego.
            // dlatego nie można sprawdzić selected != oldSelected
            // bo wtedy nie odświeża strony
            propertyChangeSupport.firePropertyChange("selected", new Integer(oldSelected), new Integer(selected));
        }
    }

    /**
     * Aktualnie zaznaczona, czyli otwarta strona dokumentu. Informacja
     * aktualna, o ile <code>MyInternalFrame</code> wykonuje update.
     */
    public void notifyCountChange(int count) {
        int old = this.size();
        if (propertyChangeSupport != null /*&& count != old*/) {
            propertyChangeSupport.firePropertyChange("count", new Integer(-1), new Integer(count));
        }
    }


    /**
     * Sprawdza czy dokument wymaga zapisania.
     *
     * @return <code>true</code> jeśli zmieniony.
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Ustawia, czy dokument wymaga zapisania.
     *
     * @param changed czy dokument został zmieniony.
     */
    public void setChanged(boolean changed) {
        boolean oldChanged = this.changed;
        this.changed = changed;
        if (propertyChangeSupport != null && changed != oldChanged) {
            //System.out.println("Document.setChanged(" + changed + ")");
            propertyChangeSupport.firePropertyChange("changed", new Boolean(oldChanged), new Boolean(changed));
        }
    }

    /**
     * Wywołuje <code>setChanged(true)</code>.
     */
    public void changed() {
        setChanged(true);
    }

    public String getFileName() {
        return fileName;
    }

    protected void setFileName(String fileName) {
        String oldFileName = this.fileName;
        this.fileName = fileName;
        if (propertyChangeSupport != null && !fileName.equals(oldFileName)) {
            propertyChangeSupport.firePropertyChange("filename", oldFileName, fileName);
        }
    }
    /**
     * Utility field used by bound properties.
     */
    transient private java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

    // transient private java.beans.PropertyChangeSupport propertyChangeSupport
    // = null;
    /**
     * Adds a PropertyChangeListener to the listener list.
     * <h4>Właściwości możliwe do monitorowania:</h4>
     * <ul>
     * <li>filename (String) - nazwa pliku</li>
     * <li>selected (Integer) - wybrana strona dokumentu</li>
     * <li>changed (Boolean) - czy wymaga zapisania</li>
     * </ul>
     *
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        /*
         * obiekt jest Serializable a nie Externalizable, ponieważ dziedziczy z
         * LinkedList, więc przy deserializacji nie jest wywoływany konstruktor.
         */
        if (this.propertyChangeSupport == null) {
            System.err.println("propertyChangeSupport = null");
            propertyChangeSupport = new PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     *
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        if (this.propertyChangeSupport != null) {
            propertyChangeSupport.removePropertyChangeListener(l);
        }
    }
}
