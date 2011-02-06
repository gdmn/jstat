/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author Damian
 */
public class Settings extends dmnutils.Settings<jstat.Settings> {

    static Settings kalkulatorSettings;

    static { kalkulatorSettings = new Settings(); kalkulatorSettings.load("kalkulator.xml"); }

    public static Settings get() {
        return kalkulatorSettings;
    }

    public static MathContext divideContext() {
        return new MathContext(Settings.get().getDokladnoscDzielenia()); //40
    }

    public static final MathContext printContext() {
        return new MathContext(Settings.get().getDokladnoscDruku(), RoundingMode.HALF_UP); //25
    }

    public Settings() {
        addVetoableChangeListener(new VetoableChangeListener() {

            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                //throw new UnsupportedOperationException("Not supported yet.");
                if (evt.getPropertyName().equals(PROP_DOKLADNOSCDRUKU)) {
                    Integer nowy = (Integer) evt.getNewValue();
                    if (nowy<0) {
                        throw new PropertyVetoException("dokładność druku nie może być ujemna", evt);
                    }
                } else
                if (evt.getPropertyName().equals(PROP_DOKLADNOSCDZIELENIA)) {
                    Integer nowy = (Integer) evt.getNewValue();
                    if (nowy<0) {
                        throw new PropertyVetoException("dokładność dzielenia nie może być ujemna", evt);
                    }
                }
            }
        });
    }

    private int dokladnoscDzielenia = 40;

    public static final String PROP_DOKLADNOSCDZIELENIA = "dokladnoscDzielenia";

    /**
     * Get the value of dokladnoscDzielenia
     *
     * @return the value of dokladnoscDzielenia
     */
    public int getDokladnoscDzielenia() {
        return this.dokladnoscDzielenia;
    }

    /**
     * Set the value of dokladnoscDzielenia
     *
     * @param newdokladnoscDzielenia new value of dokladnoscDzielenia
     * @throws java.beans.PropertyVetoException
     */
    public void setDokladnoscDzielenia(int newdokladnoscDzielenia) throws java.beans.PropertyVetoException {
        int olddokladnoscDzielenia = dokladnoscDzielenia;
        vetoableChangeSupport.fireVetoableChange(PROP_DOKLADNOSCDZIELENIA, olddokladnoscDzielenia, newdokladnoscDzielenia);
        this.dokladnoscDzielenia = newdokladnoscDzielenia;
        propertyChangeSupport.firePropertyChange(PROP_DOKLADNOSCDZIELENIA, olddokladnoscDzielenia, newdokladnoscDzielenia);
    }

    private int dokladnoscDruku = 25;

    public static final String PROP_DOKLADNOSCDRUKU = "dokladnoscDruku";

    /**
     * Get the value of dokladnoscDruku
     *
     * @return the value of dokladnoscDruku
     */
    public int getDokladnoscDruku() {
        return this.dokladnoscDruku;
    }

    /**
     * Set the value of dokladnoscDruku
     *
     * @param newdokladnoscDruku new value of dokladnoscDruku
     * @throws java.beans.PropertyVetoException
     */
    public void setDokladnoscDruku(int newdokladnoscDruku) throws java.beans.PropertyVetoException {
        int olddokladnoscDruku = dokladnoscDruku;
        vetoableChangeSupport.fireVetoableChange(PROP_DOKLADNOSCDRUKU, olddokladnoscDruku, newdokladnoscDruku);
        this.dokladnoscDruku = newdokladnoscDruku;
        propertyChangeSupport.firePropertyChange(PROP_DOKLADNOSCDRUKU, olddokladnoscDruku, newdokladnoscDruku);
    }

}
