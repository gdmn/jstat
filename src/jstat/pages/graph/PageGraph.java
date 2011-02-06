/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.pages.graph;

import dmnutils.SwingUtils;
import dmnutils.document.Document;
import dmnutils.document.DocumentPageFactory;
import dmnutils.document.AbstractDocumentPage;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import jstat.gui.MyAction;
import jstat.pages.calculator.PageCalculator;

import jstat.plugins.wrappers.Tools;
import wykres.*;

/**
 *
 * @author Damian
 */
public class PageGraph extends AbstractDocumentPage<PageCalculator> implements ActionListener, Externalizable {

    protected JTextField textInput;
    WykresPanel wykres;

    /** Creates a new instance of PageGraph */
    public PageGraph() {
        wykres = new WykresPanel(-10, 10, -10, 10, "0");
        setCaption("Wykres");
        constructorHelper();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(wykres);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        wykres = (WykresPanel) in.readObject();
        constructorHelper();
        textInput.setText(wykres.getWyrazenieString());
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    private void constructorHelper() {
        setLayout(new BorderLayout());
        JPanel panelCenter = new JPanel(new BorderLayout());
        add(panelCenter, BorderLayout.CENTER);
        panelCenter.setBorder(new EmptyBorder(5, 0, 5, 5));

        StringBuilder caption = new StringBuilder();
        //caption.append("dla x \u0454 <"+wykres.xMin+"; "+wykres.xMax+"> i  y \u0454 <"+wykres.yMin+"; "+wykres.yMax+">");

        //wykres.setOpaque(true);
        JScrollPane sp = new JScrollPane(wykres);

        panelCenter.add(sp, BorderLayout.CENTER);
        JLabel l = new JLabel(caption + "");
        wykres.setAssignedLabel(l);
        panelCenter.add(l, BorderLayout.NORTH);

        textInput = new JTextField(20);
        textInput.addActionListener(this);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(textInput, BorderLayout.SOUTH);
        bottomPanel.setBorder(new EmptyBorder(5, 0, 0, 0));
        panelCenter.add(bottomPanel, BorderLayout.SOUTH);
        FocusListener foc = new FocusListener() {

            public void focusGained(FocusEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
                textInput.requestFocusInWindow();
                textInput.selectAll();
            }

            public void focusLost(FocusEvent e) {
            }
        };

        for (Component o : Arrays.asList(this, bottomPanel, sp, wykres)) {
            o.addFocusListener(foc);
        }
        createActions();
        add(createToolBar(), BorderLayout.NORTH);
    }
    private static final String ACTION_EXPORT = MyAction.createNewId();
    private MyAction[] actions;

    private void createActions() {
        actions = new MyAction[] {new MyAction(this, ACTION_EXPORT, "Eksport", null, "Eksportuje wyniki obliczeń do pliku PNG", SwingUtils.createNavigationIcon("text-html")),};
    }

    protected JToolBar createToolBar() {
        String prev = null;
        JToolBar toolBar = new JToolBar();
        // JToolBar toolBar = super.createToolBar(); toolBar.addSeparator(); toolBar.addSeparator();
        toolBar.setFocusable(false);
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(jstat.Settings.get().getToolbarBorderPainted());
        for (MyAction a : actions) {
            String c = a.getClass().getCanonicalName();
            if (prev != null && !c.equals(prev)) {
                toolBar.addSeparator();
            }
            prev = c;
            toolBar.add(MyAction.createToolBarButton(a));
        }
        return toolBar;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == textInput) {
            String fromUser = textInput.getText();
            wykres.setWyrazenie(fromUser);
            changed();
        } else if (e.getActionCommand() == ACTION_EXPORT) {
            saveGraphAs();
        }
    }

    /**
     * Wyświetla dialog do wyboru nazwy pliku i zapisuje wykres.
     */
    public void saveGraphAs() {
        Integer rozmiar = 600;
        do {
            rozmiar = (Integer) Tools.getObjectFromText("Podaj rozmiar obrazka. " +
                    "Zostanie utworzony kwadratowy obraz o wysokości\n" +
                    "i szerokości podanej niżej. Należy wprowadzić " +
                    "liczbę naturalną większą od 49.", rozmiar.toString(), Integer.class);
            if (rozmiar == null) break;
        } while (rozmiar < 50);
        if (rozmiar == null || rozmiar < 50 ) return;
        final JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        dmnutils.MyFileFilter filter = new dmnutils.MyFileFilter("png");
        fc.addChoosableFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        int returnVal = fc.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                File f = new File(filter.setDefaultExtension(fc.getSelectedFile()));
                wykres.getWykres().savePNG(f, rozmiar, rozmiar);
                System.out.println("Zapisano "+f.toString());
            } catch (IOException ex) {
                Logger.getLogger(PageGraph.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public JTextField getTextInput() {
        return textInput;
    }

    public static DocumentPageFactory<PageGraph> factory = new DocumentPageFactory<PageGraph>() {

        public PageGraph create(Document doc) {
            PageGraph result;
            result = new PageGraph();
            return result;
        }
    };
}
