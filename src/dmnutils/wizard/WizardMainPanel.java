/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.wizard;

import dmnutils.SwingUtils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.*;
//import jstat.gui.MyAction;

/**
 * Klasa tworzy główny panel dla kreatorów. Zawiera panel po lewej stronie
 * ze "spisem treści", przyciski na dole oraz centralną część z kolejnymi
 * stronami kreatora (<code>WizardStepPanel</code>).
 * @author Damian
 */
public class WizardMainPanel extends JPanel implements ActionListener {
    private ArrayList<WizardStepPanel> arrayOfSteps;
    private JPanel pagePanel;
    private JLabel titleLabel;
    private int stepIndex = -1;
    private PagesIndexPanel leftPanel;
    /** określa wciśnięty przycisk */
    private String value;


    private JButton backButton, nextButton, okButton, cancelButton;

    private static final String ACTION_BACK   = "WizardMainPanel.ACTION_BACK";
    private static final String ACTION_NEXT   = "WizardMainPanel.ACTION_NEXT";
    /** Identyfikuje przycisk "OK" w kreatorze */
    public  static final String ACTION_OK     = "WizardMainPanel.ACTION_OK";
    /** Identyfikuje przycisk "Anuluj" w kreatorze (lub przycisk zamknięcia "X" w dialogu) */
    public  static final String ACTION_CANCEL = "WizardMainPanel.ACTION_CANCEL";

    /**
     * Tworzy WizardMainPanel.
     * @param arrayOfSteps lista wszystkich stron kreatora
     */
    public WizardMainPanel(ArrayList<WizardStepPanel> arrayOfSteps) {
        this.arrayOfSteps = arrayOfSteps;

        setLayout(new BorderLayout());

        final int SPACE = 5;

        //-- leftPanel
        leftPanel = new PagesIndexPanel();
        emptyBorder(leftPanel, SPACE, SPACE, SPACE, 0);
        add(leftPanel, BorderLayout.WEST);
        updateLeftPanel();
        //leftPanel.setVisible(false);

        //-- buttonPanel
        JPanel buttonPanel = new JPanel();
        emptyBorder(buttonPanel, 0, 0, 0, 0);
        backButton = new JButton("Wstecz");
        nextButton = new JButton("Dalej");
        okButton = new JButton("OK");
        cancelButton = new JButton("Anuluj");
        if (SwingUtils.classForImages != null) {
            backButton.setIcon(SwingUtils.createNavigationIcon("go-previous"));
            nextButton.setIcon(SwingUtils.createNavigationIcon("go-next"));
            okButton.setIcon(SwingUtils.createNavigationIcon("dialog-ok"));
            cancelButton.setIcon(SwingUtils.createNavigationIcon("dialog-cancel"));
        }
        backButton.setMnemonic(KeyEvent.VK_W);
        nextButton.setMnemonic(KeyEvent.VK_D);
        okButton.setMnemonic(KeyEvent.VK_O);
        cancelButton.setMnemonic(KeyEvent.VK_A);
        for (JButton b : Arrays.asList(backButton, nextButton, okButton, cancelButton)) {
            Dimension d = b.getMinimumSize();
            d.width = 120;
            b.setMinimumSize(d);
            b.setPreferredSize(d);
        }
        backButton.setActionCommand(ACTION_BACK);
        backButton.addActionListener(this);
        buttonPanel.add(backButton);
        nextButton.setActionCommand(ACTION_NEXT);
        nextButton.addActionListener(this);
        buttonPanel.add(nextButton);
        okButton.setActionCommand(ACTION_OK);
        okButton.addActionListener(this);
        buttonPanel.add(okButton);
        cancelButton.setActionCommand(ACTION_CANCEL);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        //-- bottomPanel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        emptyBorder(bottomPanel, 0, 0, 0, 0);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        bottomPanel.add(new JSeparator(), BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        //-- titlePanel
        JPanel titlePanel = new JPanel(new BorderLayout()) { };
        titlePanel.setOpaque(false);
        titlePanel.add(new JSeparator(), BorderLayout.SOUTH);
        emptyBorder(titlePanel, 0, 0, SPACE, 0);
        //SlickPanel slick = new SlickPanel(Color.WHITE, titlePanel.getBackground());
        Font f = new JLabel().getFont();
        SlickPanel slick = new SlickPanel(Color.WHITE, titlePanel.getBackground());
        slick.setForeground(Color.BLACK);
        //SlickPanel slick = new SlickPanel(titlePanel.getBackground(),Color.WHITE);
        slick.setLayout(new BorderLayout());
        titleLabel = new JLabel();
        slick.add(titleLabel, BorderLayout.CENTER);
        slick.add(Box.createVerticalStrut(3), BorderLayout.NORTH);
        slick.add(Box.createVerticalStrut(3), BorderLayout.SOUTH);
        slick.add(Box.createHorizontalStrut(3), BorderLayout.EAST);
        slick.add(Box.createHorizontalStrut(3), BorderLayout.WEST);
        titlePanel.add(slick, BorderLayout.CENTER);

        //-- pagePanel
        pagePanel = new JPanel(new BorderLayout());
        emptyBorder(pagePanel, 0, 0, 0, 0);
        if (arrayOfSteps != null && arrayOfSteps.size() > 0) {
            setStepIndex(0);
        }

        //-- centerPanel
        JPanel centerPanel = new JPanel(new BorderLayout()) {
            public Dimension getPreferredSize() {
                return new Dimension(400, 250);
            }
        };
        emptyBorder(centerPanel, SPACE, SPACE/*0*/, SPACE, SPACE);
        centerPanel.add(titlePanel, BorderLayout.NORTH);
        centerPanel.add(pagePanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        //-- obsługa przycisków itp.
        for (WizardStepPanel step : arrayOfSteps) {
            step.addPropertyChangeListener(WizardStepPanel.PROPERTYCHANGE_FORWARDENABLED, this.forwardEnabledPropertyChangeListener);
            step.addPropertyChangeListener(WizardStepPanel.PROPERTYCHANGE_TITLE, this.titlePropertyChangeListener);
        }

        /* ustawienie focus na panelu, przy pierwszym "złapaniu" w klasie
         MyFocusListener jest to usuwane po uprzednim zaaktywowaniu aktywnej strony */
        setFocusable(true);
        addFocusListener(new MyFocusListener());
        requestFocusInWindow();
    }

    private class MyFocusListener implements FocusListener {
        public void focusGained(FocusEvent e) {
            if (arrayOfSteps != null && arrayOfSteps.size() > 0) {
                arrayOfSteps.get(stepIndex).requestFocusInWindow();
                //System.out.println("arrayOfSteps.get(stepIndex).requestFocusInWindow();");
                WizardMainPanel.this.removeFocusListener(WizardMainPanel.this.getFocusListeners()[0]);
                setFocusable(false);
            }
        }
        public void focusLost(FocusEvent e) {
        }
    }

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
            System.err.println("WizardMainPanel.propertyChangeSupport == null, tworzenie");
            propertyChangeSupport =  new java.beans.PropertyChangeSupport(this);
        }
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener l) {
        if (propertyChangeSupport == null) {
            // przy wyglądzie gtk konieczne, nie wiem dlaczego
            System.err.println("WizardMainPanel.propertyChangeSupport == null, tworzenie");
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
     * Wywołuje obsługę zdarzenia po kliknięciu w podany przycisk
     */
    public void setValue(String action) {
        value = action;
        propertyChangeSupport.firePropertyChange(JOptionPane.VALUE_PROPERTY, JOptionPane.UNINITIALIZED_VALUE, action);
    }

    /**
     * Zwraca jaki przycisk został wciśnięty
     */
    public String getValue() {
        return value;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(ACTION_BACK)) {
            //quit();
            if (stepIndex > 0) setStepIndex(stepIndex - 1);
        } else if (e.getActionCommand().equals(ACTION_NEXT)) {
            //quit();
            if (stepIndex >= 0 && stepIndex+1 < arrayOfSteps.size()) setStepIndex(stepIndex + 1);
        } else if (e.getActionCommand().equals(ACTION_OK)) {
            //quit();
            setValue(ACTION_OK);
        } else if (e.getActionCommand().equals(ACTION_CANCEL)) {
            //quit();
            //??JOptionPane.UNINITIALIZED_VALUE
            setValue(ACTION_CANCEL);
        }
    }

    /** Wywoływany w przypadku zmiany stanu przycisku "Dalej" z nie- na aktywny lub odwrotnie */
    private PropertyChangeListener forwardEnabledPropertyChangeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            //JOptionPane.showMessageDialog(null, "forwardEnabledPropertyChangeListener - "+ evt.getPropertyName()+": "+evt.getOldValue().toString()+" -> "+evt.getNewValue().toString());
            WizardStepPanel step = arrayOfSteps.get(stepIndex);
            okButton.setEnabled(step.isForwardEnabled() && stepIndex == arrayOfSteps.size()-1);
            nextButton.setEnabled(step.isForwardEnabled() && stepIndex < arrayOfSteps.size()-1);
        }
    };

    /** Wywoływany w przypadku zmiany tytułu strony kreatora */
    private PropertyChangeListener titlePropertyChangeListener = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            //JOptionPane.showMessageDialog(null, "titlePropertyChangeListener - "+ evt.getPropertyName()+": "+evt.getOldValue().toString()+" -> "+evt.getNewValue().toString());
            WizardStepPanel step = arrayOfSteps.get(stepIndex);
            titleLabel.setText(step.getTitle());
            updateLeftPanel();
        }
    };

    /**
     * Wyświetla stronę kreatora. Zmienia tytuł, odświeża indeks stron z lewej strony,
     * oraz przekazuje focus do aktywnej strony.
     * @param id numer strony do wyświetlenia (z podanych w kreatorze)
     */
    private void setStepIndex(int id) {
        pagePanel.removeAll();
        WizardStepPanel step = arrayOfSteps.get(id);
        pagePanel.add(step);
        titleLabel.setText(step.getTitle());
        pagePanel.validate();
        pagePanel.repaint();
        stepIndex = id;
        okButton.setEnabled(step.isForwardEnabled() && id == arrayOfSteps.size()-1);
        nextButton.setEnabled(step.isForwardEnabled() && id < arrayOfSteps.size()-1);
        backButton.setEnabled(id > 0);
        updateLeftPanel();
        step.requestFocus();
    }

    /** Klasa obsługująca "spis treści" z lewej strony */
    private class PagesIndexPanel extends JPanel {
        public PagesIndexPanel() {
            super(new BorderLayout());
        }

        public Dimension getPreferredSize() {
            return new Dimension(150, 30);
        }

        /** Rysuje ramkę */
        public void paint(Graphics g) {
            super.paint(g);
            Insets ins = this.getInsets();
            int width = getWidth() - ins.left - ins.right -1;
            int height = getHeight() - ins.top - ins.bottom -1;
            //g.drawRoundRect(ins.left, ins.top, width, height, 30,30);
            g.drawRect(ins.left, ins.top, width, height);
        }

        /** Odświeża listę stron. Bieżącą stronę zaznacza innym kolorem */
        public void updateIndex() {
            leftPanel.removeAll();
            JPanel topPanel = new JPanel(new GridLayout(0,1));
            int i = 0;
            for (WizardStepPanel p : arrayOfSteps) {
                JLabel b = new JLabel((++i) + ". " + p.getTitle());
                if (i == stepIndex+1) {
                    b.setForeground(Color.BLUE);
                }
                //b.setHorizontalAlignment(SwingConstants.CENTER);
                topPanel.add(b);
            }
            JPanel opa = new JPanel(new BorderLayout());
            opa.setBorder(new EmptyBorder(1,1+5,1,1));
            opa.setOpaque(false);
            leftPanel.add(opa, BorderLayout.CENTER);
            opa.add(topPanel, BorderLayout.NORTH);
        }
    }

    /** Odświeżenie listy stron */
    private void updateLeftPanel() {
        leftPanel.updateIndex();
    }

    static java.util.Random randomForColor = new java.util.Random(10002);
    public static Color randomColor() {
        return new Color(randomForColor.nextFloat(), randomForColor.nextFloat(), randomForColor.nextFloat());
    }
    /*public static void randomBorder(JComponent c) {
        final int THICK = 8;
        c.setBorder(new MatteBorder(THICK, THICK, THICK, THICK, randomColor()));
    }*/


    public static void emptyBorder(JComponent c, int top, int left, int bottom, int right) {
        c.setBorder(new EmptyBorder(top, left, bottom, right));
        //c.setBorder(new MatteBorder(top+1, left+1, bottom+1, right+1, randomColor()));
    }


    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    public static WizardMainPanel przykladowy() {
        WizardStepPanel p0 = new WizardStepPanel("strona 1") {
            public void focusGained(FocusEvent e) {
            }
        };

        WizardStepPanel p1 = new WizardStepPanel("strona 2") {

            public void focusGained(FocusEvent e) {
            }
        };

        WizardStepPanel p2 = new WizardStepPanel("strona 3") {

            public void focusGained(FocusEvent e) {
            }
        };
        ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(
                Arrays.asList(p0, p1, p2)
                );

        final WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);

        return wizard;
    }

    public static void main(String[] args) {
        WizardStepPanel p0 = new WizardStepPanel("zerowy");
        p0.setForwardEnabled(true);

        WizardStepPanel p1 = new WizardStepPanel("p1") {
            public void prepareStep() {
            }
        };
        p1.setForwardEnabled(true);

        final WizardStepPanel p2 = new WizardStepPanel("p222");
        p2.setForwardEnabled(false);
        JCheckBox box = new JCheckBox("druga");
        p2.add(box);
        JButton dialogBtn = new JButton("dlg");
        p2.add(dialogBtn);
        p2.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                //JOptionPane.showMessageDialog(p2, evt.getPropertyName()+": "+evt.getOldValue().toString()+" -> "+evt.getNewValue().toString());
            }
        });
        p2.addPropertyChangeListener(WizardStepPanel.PROPERTYCHANGE_TITLE, new PropertyChangeListener() {
            //p2.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                //JOptionPane.showMessageDialog(p2, "TITLE: "+evt.getPropertyName()+": "+evt.getOldValue().toString()+" -> "+evt.getNewValue().toString());
            }
        });
        box.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                p2.setTitle(((JCheckBox)e.getSource()).getText());
                p2.setForwardEnabled(((JCheckBox)e.getSource()).isSelected());
            }
        });
        dialogBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                WizardDialog wd = new WizardDialog(null, "Test WizardDialog", przykladowy());
                wd.setVisible(true);
                JOptionPane.showMessageDialog(null, "WizardDialog: "+wd.getValue());
            }
        });


        ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(
                Arrays.asList(p0, p1, p2)
                );
        final WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);
        wizard.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                JOptionPane.showMessageDialog(null, "WizardStepPanel.PropertyChangeListener: "+evt.getPropertyName()+": "+evt.getOldValue().toString()+" -> "+evt.getNewValue().toString());
            }
        });

        dmnutils.SwingUtils.run(wizard);
    }

}
