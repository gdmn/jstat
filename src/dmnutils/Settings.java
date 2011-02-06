/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import beanutils.BeanSaverLoader;
import beanutils.BeanSetterGetter;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmn
 */
public abstract class Settings<T extends Settings> implements Serializable {

    //private transient T settings = null;
    private transient String settingsFileName = null;

//    static {
//        load(Settings.class);
//    }

    @SuppressWarnings("unchecked")
    public void load(String settingsFileName) {
        //main = new Settings();
        this.settingsFileName = settingsFileName;
        T tmp = null;
        try {
            tmp = (T) BeanSaverLoader.loadBean(getSettingsDirectory() + settingsFileName);
        } finally {
            if (tmp != null) {
                /* trzeba odczytywać przez assignProperties, ponieważ
                 * jeśli wcześniej nie było w pliku jakiejś właściwości
                 * zapisanej, to ma ona wartość null. w funkcji
                 * assignProperties wszystkie, które mają wartość
                 * null są zastępowane przez domyślne wartości.
                 */
                this.assignProperties(tmp);
            } else {
                save();
            }
        }
    }

    public void save() {
        BeanSaverLoader.saveBean(getSettingsDirectory() + settingsFileName, this);
    }

//    public Settings get() {
//        return settings;
//    }

    public void assignProperties(Settings settings) {
        if (settings == null) {
            return;
        }
        try {
            //main = settings;
            BeanInfo bi_old = Introspector.getBeanInfo(this.getClass(), Object.class);
            PropertyDescriptor[] pd_old = bi_old.getPropertyDescriptors();
            BeanInfo bi_new = Introspector.getBeanInfo(settings.getClass(), Object.class);
            PropertyDescriptor[] pd_new = bi_new.getPropertyDescriptors();
            for (int i = 0; i < pd_new.length; i++) {
                PropertyDescriptor pd1 = pd_old[i];
                PropertyDescriptor pd2 = pd_new[i];
                Object value = BeanSetterGetter.getBeanPropertyEasy(settings, pd2);
//                System.out.println("" + pd1.getDisplayName() + ": " +
//                        BeanSetterGetter.getBeanPropertyEasy(this, pd1) + " -> " +
//                        value);
                if (value != null) {
                    BeanSetterGetter.setBeanProperty(this, pd1, value);
                }
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IntrospectionException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected transient java.beans.PropertyChangeSupport propertyChangeSupport = new java.beans.PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected transient java.beans.VetoableChangeSupport vetoableChangeSupport = new java.beans.VetoableChangeSupport(this);

    /**
     * Add VetoableChangeListener.
     *
     * @param listener
     */
    public void addVetoableChangeListener(java.beans.VetoableChangeListener listener) {
        vetoableChangeSupport.addVetoableChangeListener(listener);
    }

    /**
     * Remove VetoableChangeListener.
     *
     * @param listener
     */
    public void removeVetoableChangeListener(java.beans.VetoableChangeListener listener) {
        vetoableChangeSupport.removeVetoableChangeListener(listener);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        vetoableChangeSupport = new java.beans.VetoableChangeSupport(this);
        propertyChangeSupport = new java.beans.PropertyChangeSupport(this);
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Zwraca ścieżkę, gdzie powinny znajdować się ustawienia programów.
     * @return
     */
    public static String getSettingsDirectory() {
        String result = null;
        Properties p = System.getProperties();
        String os = p.getProperty("os.name");
        String separator = p.getProperty("file.separator");
        if (os.toLowerCase().indexOf("windows") > -1) {
            //Map<String, String> env = System.getenv();
            result = System.getenv("LOCALAPPDATA");
            if (result == null) {
                result = System.getenv("APPDATA");
            }
            result = result + separator + jstat.Version.NAME.toLowerCase() + separator;
        } else {
            result = p.getProperty("user.home") + separator + "." + jstat.Version.NAME.toLowerCase() + separator;
        }
        File f = (new File(result));
        try {
            if (!f.exists() && !f.mkdirs()) {
                return null;
            }
        } catch (SecurityException e) {
            return null;
        }
        return result;
    }

}
