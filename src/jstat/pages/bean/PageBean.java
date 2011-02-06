/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.pages.bean;

import dmnutils.document.DocumentPageFactory;
import dmnutils.document.AbstractDocumentPage;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.*;
import java.io.Externalizable;
import java.io.IOException;

import javax.swing.*;

import beanutils.*;
import dmnutils.document.Document;
import java.io.File;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
//import tests.FileChooserDemo;


/**
 * @author Damian
 *
 */
public class PageBean extends AbstractDocumentPage<PageBean> implements Externalizable {
    private BeanEditorTable beanProperties;
    private JPanel beanPropertiesPanel;
    private JTextField filterText ;

    /**
     *
     */
    public PageBean() {
        constructorHelper();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        constructorHelper();
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    private void constructorHelper() {
        setCaption("jb");
        JPanel p = new JPanel();
        //p.setLayout(new FlowLayout());
        JButton b1 = new JButton("Otwórz"), b2 = new JButton("Zapisz"), b3 = new JButton("wykres.Settings"), b4 = new JButton("Nowy");
        p.add(b1); p.add(b2); p.add(b4); p.add(b3);
        {
            b1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageBean.this.openClick(e);
                }
            });
            b2.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageBean.this.saveClick(e);
                }
            });
            b3.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageBean.this.b3click(e);
                }
            });
            b4.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PageBean.this.nowyClick(e);
                }
            });
        }
        //p.add(query);
        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, p);
        //add(new JScrollPane(results));
        beanPropertiesPanel = new JPanel(new BorderLayout());
        add(BorderLayout.CENTER, beanPropertiesPanel);
        //Dumper dmpr = new Dumper();
        //query.addActionListener(dmpr);
        //query.setText("wykres.Settings");
        //dmpr.actionPerformed(new ActionEvent(dmpr, 0, ""));

        fc = new JFileChooser();
    }

    class FilterTextDocumentListener implements DocumentListener {
            public void changedUpdate(DocumentEvent e) {
                PageBean.this.beanProperties.newFilter(filterText.getText());
            }
            public void insertUpdate(DocumentEvent e) {
                PageBean.this.beanProperties.newFilter(filterText.getText());
            }
            public void removeUpdate(DocumentEvent e) {
                PageBean.this.beanProperties.newFilter(filterText.getText());
            }
    }


    JFileChooser fc;

    private void openClick(ActionEvent e) {
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String fn = null;
            try {
                fn = file.getCanonicalFile().toString();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            boolean ok = false;
            try {
                System.out.println("Opening: " + fn + ".");
                Object bean = null;
                bean = beanutils.BeanSaverLoader.loadBean(fn);
                editBean(bean);
                ok = true;
            } finally {
                System.out.println(ok ? "Loaded." : "Failed.");
            }
        } else {
            System.out.println("Open command cancelled by user.");
        }
    }

    private void saveClick(ActionEvent e) {
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                //This is where a real application would save the file.
                System.out.println("Saving: " + file.getCanonicalFile() + ".");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                beanutils.BeanSaverLoader.saveBean(file.getCanonicalFile().toString(), beanProperties.getBean());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Saved.");
        } else {
            System.out.println("Save command cancelled by user.");
        }
    }

    private void nowyClick(ActionEvent e) {
        String name = JOptionPane.showInputDialog(this, "Jaki obiekt utworzyć?\nWprowadź pełną nazwę.");
        //JOptionPane.showMessageDialog(this, name);
        Class<?> c = null;
        try {
            c = Class.forName(name);
            editBean(c.newInstance());
        } catch (InstantiationException ex) {
            System.err.println(ex.getClass().getSimpleName() + " dla "+ name);
        } catch (IllegalAccessException ex) {
            System.err.println(ex.getClass().getSimpleName() + " dla "+ name);
        } catch (ClassNotFoundException ex) {
            System.err.println(ex.getClass().getSimpleName() + " dla "+ name);
            //System.err.println("Nie można znaleźć " + name);
        }
    }

    public void editBean(Object bean) {
        System.out.println(bean.toString());
        beanPropertiesPanel.removeAll();
        try {
            beanProperties = new BeanEditorTable(bean, null);
            //beanProperties.newFilter("oo");
            beanPropertiesPanel.add(BorderLayout.CENTER, new JScrollPane(beanProperties));
        //Create a separate form for filterText and statusText
        JPanel form = new JPanel(new BorderLayout());//(new SpringLayout());
        JLabel l1 = new JLabel("Filtr:  ", SwingConstants.TRAILING);
        form.add(l1, BorderLayout.LINE_START);
        filterText = new JTextField();
        //filterText.setText("jjjjjoooooooooooooo");
        //filterText.setMinimumSize(new Dimension(500,12));
        //filterText.setPreferredSize(filterText.getMinimumSize());
        //Whenever filterText changes, invoke newFilter.
        filterText.getDocument().addDocumentListener(
                new FilterTextDocumentListener());
        l1.setLabelFor(filterText);
        form.add(filterText, BorderLayout.CENTER);
        beanPropertiesPanel.add(BorderLayout.NORTH, form);
            beanPropertiesPanel.validate();
            beanPropertiesPanel.repaint();
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
        }
    }

    private void b3click(ActionEvent e) {
        String name = "wykres.Settings";
        Class<?> c = null;
        try {
            c = Class.forName(name);
        } catch (ClassNotFoundException e1) {
            System.err.println("Nie można znaleźć " + name);
            return;
        }
        try {
            editBean(c.newInstance());
            //beanProperties = new BeanEditorTable(c.newInstance());
        } catch (InstantiationException e1) {
            System.err.println("InstantiationException dla " + name);
            e1.printStackTrace();
            return;
        } catch (IllegalAccessException e1) {
            System.err.println("IllegalAccessException dla " + name);
            e1.printStackTrace();
            return;
        }
    }

    public static DocumentPageFactory<PageBean> factory = new DocumentPageFactory<PageBean>() {
        public PageBean create(Document doc) {
            return new PageBean();
        }
    };

}
