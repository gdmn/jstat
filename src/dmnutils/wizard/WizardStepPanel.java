/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.wizard;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;

/**
 *
 * @author Damian
 */
public class WizardStepPanel extends JPanel {

    /**
     * Czy przycisk "Dalej" jest aktywny
     */
    public static final String PROPERTYCHANGE_FORWARDENABLED = "forwardEnabled";

    /**
     * Tytuł strony kreatora
     */
    public static final String PROPERTYCHANGE_TITLE = "title";

    /** Creates a new instance of WizardStepPanel */
    public WizardStepPanel(String title) {
        setForwardEnabled(true);
        setTitle(title);
        addFocusListener(new MyFocusListener());
        prepareStep();
    }

    private class MyFocusListener implements FocusListener {
        /**
         * Gdy wyświetlana jest strona określana przez tą klasę
         */
        public void focusGained(FocusEvent e) {
            JComponent a = WizardStepPanel.this.getFirstFocused();
            if (a != null) {
                a.requestFocusInWindow();
                //a.requestFocus();
            } else {
                for (java.awt.Component k : WizardStepPanel.this.getComponents()) {
                    if (k.isFocusable() && (!(k instanceof JLabel))) {
//                        System.out.println("znalazlem do aktywowania "+ k.getClass().toString() + " - "+ k.toString());
                        //k.requestFocusInWindow();
                        k.requestFocus();
                        break;
                    }
                }
            }
        }

        /**
         * Gdy strona traci focus, ogólnie można pominąć, jedynie wymóg implementowanego interfejsu
         */
        public void focusLost(FocusEvent e) {
        }
    }

    /**
     * Procedura wywoływana po konstruktorze, służy do przygotowania strony
     */
    public void prepareStep() {

    }

    /**
     * Holds value of property forwardEnabled.
     */
    private boolean forwardEnabled;

    /**
     * Utility field used by bound properties.
     */
    private java.beans.PropertyChangeSupport propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);

    /**
     * Adds a PropertyChangeListener to the listener list.
     * @param l The listener to add.
     */
    public void addPropertyChangeListener(java.beans.PropertyChangeListener l) {
        if (propertyChangeSupport == null) {
            // przy wyglądzie gtk konieczne, nie wiem dlaczego
            System.err.println("WizardStepPanel.propertyChangeSupport == null, tworzenie");
            propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener l) {
        if (propertyChangeSupport == null) {
            // przy wyglądzie gtk konieczne, nie wiem dlaczego
            System.err.println("WizardStepPanel.propertyChangeSupport == null, tworzenie");
            propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(propertyName, l);
    }

    /**
     * Removes a PropertyChangeListener from the listener list.
     * @param l The listener to remove.
     */
    public void removePropertyChangeListener(java.beans.PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    /**
     * Sprawdza, czy przycisk "Dalej" jest aktywny.
     * @return Value of property forwardEnabled.
     */
    public boolean isForwardEnabled() {
        return this.forwardEnabled;
    }

    /**
     * Ustawia, czy przycisk "Dalej" jest aktywny.
     * @param forwardEnabled New value of property forwardEnabled.
     */
    public void setForwardEnabled(boolean forwardEnabled) {
        boolean oldForwardEnabled = this.forwardEnabled;
        this.forwardEnabled = forwardEnabled;
        propertyChangeSupport.firePropertyChange(PROPERTYCHANGE_FORWARDENABLED, new Boolean(oldForwardEnabled), new Boolean(forwardEnabled));
    }

    /**
     * Holds value of property title.
     */
    private String title;

    /**
     * Pobiera tytuł strony kreatora, wyświetlany u góry i w panelu ze spisem.
     * @return Value of property title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Ustawia tytuł strony kreatora, wyświetlany u góry i w panelu ze spisem.
     * @param title New value of property title.
     */
    public void setTitle(String title) {
        String oldTitle = this.title;
        this.title = title;
        propertyChangeSupport.firePropertyChange(PROPERTYCHANGE_TITLE, oldTitle, title);
    }

    /**
     * Holds value of property firstFocused.
     */
    private JComponent firstFocused;

    /**
     * Który komponent ma dostać focus po zaaktywowaniu strony.
     * @return Value of property firstFocused.
     */
    public JComponent getFirstFocused() {
        return this.firstFocused;
    }

    /**
     * Ustawia, który komponent ma dostać focus po zaaktywowaniu strony.
     * @param firstFocused New value of property firstFocused.
     */
    public void setFirstFocused(JComponent firstFocused) {
        this.firstFocused = firstFocused;
    }

}
