/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.gui;

import beanutils.BeanEditorTable;
import dmnutils.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.IntrospectionException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author dmn
 */
public class SettingsEditor extends JPanel implements ActionListener {

//    private JPanel[] beanPropertiesPanels = new JPanel[2];
//    private BeanEditorTable[] beanProperties = new BeanEditorTable[beanPropertiesPanels.length];
    //private JTextField[] filterTexts = new JTextField[beanPropertiesPanels.length];
    private dmnutils.Settings jstatTempSettings, kalkulatorTempSettings;
    private JButton btn_ok;
    private JButton btn_anuluj;

    public SettingsEditor() {
        //setModal(true);
        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(5, 5, 5, 5));
//        beanPropertiesPanels[0] = new JPanel(new BorderLayout());
//        add(beanPropertiesPanel[0], BorderLayout.CENTER);


        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btn_ok = new JButton("OK");
        btn_anuluj = new JButton("Anuluj");
        SwingUtils.classForImages = jstat.EmptyClass.class;
        btn_ok.setIcon(SwingUtils.createNavigationIcon("dialog-ok"));
        btn_anuluj.setIcon(SwingUtils.createNavigationIcon("dialog-cancel"));
        navigation.add(btn_ok);
        navigation.add(btn_anuluj);
        btn_ok.addActionListener(this);
        btn_anuluj.addActionListener(this);
        add(navigation, BorderLayout.SOUTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFocusable(false);
        add(tabs, BorderLayout.CENTER);

        jstatTempSettings = new jstat.Settings();
        jstatTempSettings.assignProperties(jstat.Settings.get());
        tabs.addTab("Główne", createTabPage(jstatTempSettings));

        kalkulatorTempSettings = new kalkulator.Settings();
        kalkulatorTempSettings.assignProperties(kalkulator.Settings.get());
        tabs.addTab("Obliczenia", createTabPage(kalkulatorTempSettings));
//        editBean(tempSettings, beanPropertiesPanel[0]);

    //jstat.Settings.assignProperties(new jstat.Settings());
    }

    private JPanel createTabPage(Object bean) {
        JPanel result = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new BorderLayout());
        form.setBorder(new EmptyBorder(0, 0, 5, 0));
        JLabel l1 = new JLabel("Filtr:  ", SwingConstants.TRAILING);
        form.add(l1, BorderLayout.LINE_START);
        JPanel centerPanel = new JPanel(new BorderLayout());
        final BeanEditorTable editor = editBean(bean, centerPanel);
        JTextField filterText = new JTextField();
        filterText.getDocument().addDocumentListener(
                new FilterTextDocumentListener(editor, filterText));
        l1.setLabelFor(filterText);
        form.add(filterText, BorderLayout.CENTER);
		JCheckBox cb1 = new JCheckBox("Tryb eksperta");
		cb1.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				editor.setAllColumnVisible(e.getStateChange() == ItemEvent.SELECTED);
			}
		});
        form.add(cb1, BorderLayout.LINE_END);
        result.add(form, BorderLayout.NORTH);
        result.add(centerPanel, BorderLayout.CENTER);
        result.setBorder(new EmptyBorder(5, 5, 5, 5));
        return result;
    }

    public BeanEditorTable editBean(Object bean, JPanel beanPropertiesPanel) {
        //System.out.println(bean.toString());
        beanPropertiesPanel.removeAll();
        try {
            BeanEditorTable beanProperties = new BeanEditorTable(bean, Object.class);
            beanProperties.setAllColumnVisible(false);
            beanProperties.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            //beanProperties.newFilter("oo");
            beanPropertiesPanel.add(BorderLayout.CENTER, new JScrollPane(beanProperties));
            //Create a separate form for filterText and statusText
            //beanPropertiesPanel.add(BorderLayout.NORTH, form);
            beanPropertiesPanel.validate();
            beanPropertiesPanel.repaint();
            return beanProperties;
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    class FilterTextDocumentListener implements DocumentListener {

        BeanEditorTable beanProperties;
        JTextField filterText;

        public FilterTextDocumentListener(BeanEditorTable beanProperties, JTextField filterText) {
            this.beanProperties = beanProperties;
            this.filterText = filterText;
        }

        public void changedUpdate(DocumentEvent e) {
            beanProperties.newFilter(filterText.getText());
        }

        public void insertUpdate(DocumentEvent e) {
            beanProperties.newFilter(filterText.getText());
        }

        public void removeUpdate(DocumentEvent e) {
            beanProperties.newFilter(filterText.getText());
        }
    }

    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame();
        frame.add(new SettingsEditor());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showDialog(java.awt.Frame parent) {
        JDialog frame = new JDialog(parent, "Ustawienia");
        frame.setModal(true);
        final SettingsEditor se = new SettingsEditor();
        frame.add(se);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent we) {
                se.actionPerformed(new ActionEvent(se.btn_anuluj, 0, null));
            }
        });
        Dimension size = new Dimension(550, 300);
        frame.setMinimumSize(size);
        frame.setPreferredSize(size);
        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_ok) {
            jstat.Settings.get().assignProperties(jstatTempSettings);
            jstat.Settings.get().save();
            kalkulator.Settings.get().assignProperties(kalkulatorTempSettings);
            kalkulator.Settings.get().save();
        }
        if (e.getSource() == btn_ok || e.getSource() == btn_anuluj) {
            Container c = SwingUtils.findParentWithGivenClass(this, JDialog.class);
            if (c == null) {
                c = SwingUtils.findParentWithGivenClass(this, JFrame.class);
            }
            c.setVisible(false);
            int exitop = -1;
            if (c instanceof JFrame) {
                JFrame fc = (JFrame) c;
                exitop = fc.getDefaultCloseOperation();
            } else if (c instanceof JDialog) {
                JDialog dc = (JDialog) c;
                exitop = dc.getDefaultCloseOperation();
            }
            if (exitop == JFrame.EXIT_ON_CLOSE) {
                System.exit(0);
            }
        }
    }
}
