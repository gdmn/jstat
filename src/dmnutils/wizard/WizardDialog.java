/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */
package dmnutils.wizard;

import javax.swing.*;
import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class WizardDialog extends JDialog
                   implements ActionListener,
                              PropertyChangeListener {

    private WizardMainPanel wizardPanel;

    public WizardDialog(Frame parent, String dialogTitle, ArrayList<WizardStepPanel> arrayOfSteps) {
        this(parent, dialogTitle, new WizardMainPanel(arrayOfSteps));
    }

    /** Creates the reusable dialog. */
    public WizardDialog(Frame parent, String dialogTitle, final WizardMainPanel wizardPanel) {
        super(parent, true);
        this.wizardPanel = wizardPanel;
        setTitle(dialogTitle);

        //Make this dialog display it.
        setContentPane(wizardPanel);
        pack();
        setLocationRelativeTo(parent);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
                public void windowClosing(WindowEvent we) {
                    wizardPanel.setValue(WizardMainPanel.ACTION_CANCEL);
            }
        });

        wizardPanel.addPropertyChangeListener(this);
    }

    /** This method handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
        //optionPane.setValue(btnString1);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
         && (e.getSource() == wizardPanel)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = e.getNewValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            if (WizardMainPanel.ACTION_OK.equals(value)) {
                    //we're done; clear and dismiss the dialog
                    clearAndHide();
            } else { //user closed dialog or clicked cancel
                    clearAndHide();
            }
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        setVisible(false);
    }

    /**
     * Zwraca jaki przycisk został wciśnięty
     */
    public String getValue() {
        return wizardPanel.getValue();
    }

    /**
     * Pokazuje dialog.
     * @return Wybrany przycisk (<code>WizardMainPanel.ACTION_CANCEL</code> lub <code>WizardMainPanel.ACTION_OK</code>)
     */
    public String execute() {
        setVisible(true);
        return getValue();
    }


}