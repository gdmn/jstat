/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.pages.bsh;

import bsh.Interpreter;
import dmnutils.*;
import dmnutils.document.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jstat.gui.MyAction;

/**
 *
 * @author dmn
 */
public class PageBeanShell extends AbstractDocumentPage<PageBeanShell> implements ActionListener, Externalizable {

    protected JTextArea textInput;
    protected ColoredTextPane textOutput;

    /** Creates a new instance of PageCalculator */
    public PageBeanShell() {
        super();
        setCaption("Skrypt");
        constructorHelper();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(textInput.getText());
        out.writeObject(textOutput.getText());
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        constructorHelper();
        textInput.setText((String) in.readObject());
        textOutput.setText((String) in.readObject());
    }

    /**
     * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
     * w konstruktorze, wspólna ich część jest w tutaj
     */
    private void constructorHelper() {
        textInput = new JTextArea();
        JScrollPane scrollPaneTextInput = new JScrollPane(textInput);
        //textInput.setFont(new JTextField().getFont());
        textInput.setFont(new Font(Font.MONOSPACED, Font.PLAIN, new JTextField().getFont().getSize()));
        scrollPaneTextInput.setMinimumSize(new Dimension(1, 50));
        textInput.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                PageBeanShell.this.changed();
            }

            public void removeUpdate(DocumentEvent e) {
                insertUpdate(e);
            }

            public void changedUpdate(DocumentEvent e) {
                insertUpdate(e);
            }
        });

        textOutput = new ColoredTextPane();
        textOutput.setEditable(false);
        JScrollPane scrollPaneTextOutput = new JScrollPane(textOutput);
        scrollPaneTextOutput.setMinimumSize(new Dimension(1, 50));
        scrollPaneTextOutput.setPreferredSize(new Dimension(1, 100));

        textOutput.setFont((new JTextField()).getFont()); //przy wyglądzie windows jest zmniejszona czcionka w logTextArea

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPaneTextInput, scrollPaneTextOutput);
        splitPane.setResizeWeight(1.0); // przy zmianie rozmiaru dolny panel pozostaje bez zmian
        splitPane.setOneTouchExpandable(true);
        splitPane.setContinuousLayout(true); // przesuwanie "na żywo"
        //splitPane.setDividerLocation(100);

        setLayout(new BorderLayout());
        splitPane.setBorder(new EmptyBorder(5, 0, 5, 5));
        add(splitPane, BorderLayout.CENTER);

        createActions();
        add(createToolBar(), BorderLayout.NORTH);

        FocusListener foc = new FocusListener() {

            public void focusGained(FocusEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
                textInput.requestFocusInWindow();
            }

            public void focusLost(FocusEvent e) {
            }
        };

        for (Component o : Arrays.asList(this, scrollPaneTextInput, scrollPaneTextOutput)) {
            o.addFocusListener(foc);
        }

        JPopupMenu popup = new JPopupMenu();
        JMenuItem menuItem;
        menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_RUN));
        //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        KeyStroke ksRun = KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0, false);
        menuItem.setAccelerator(ksRun);
        popup.add(menuItem);
        menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_CLEAR));
        menuItem.addActionListener(this);
        popup.add(menuItem);
        //Add listener to components that can bring up popup menus.
        for (JComponent o : Arrays.asList(this, scrollPaneTextInput, scrollPaneTextOutput, textInput, textOutput)) {
            o.addMouseListener(new dmnutils.PopupListener(popup));
            o.registerKeyboardAction(this, ACTION_RUN, ksRun, JComponent.WHEN_FOCUSED);
        }
    }
    private static final String ACTION_RUN = MyAction.createNewId();
    private static final String ACTION_CLEAR = MyAction.createNewId();
    private MyAction[] actions;

    private void createActions() {
        actions = new MyAction[] {new MyAction(this, ACTION_RUN, "Uruchom", null, "Wykonuje skrypt", SwingUtils.createNavigationIcon("system-run")),
                                  new MyAction(this, ACTION_CLEAR, "Wyczyść konsolę", null, "Czyści konsolę wyjściową skryptu", SwingUtils.createNavigationIcon("edit-clear")),
        };
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
            JButton button = MyAction.createToolBarButton(a);
            toolBar.add(button);
        }
        return toolBar;
    }

    public void actionPerformed(ActionEvent evt) {
        final String ac = evt.getActionCommand();
        if (ac.equals(ACTION_RUN)) {
            runScript();
        } else if (ac.equals(ACTION_CLEAR)) {
            changed();
            textOutput.setText("");
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public void runScript() {
        final PrintStream old = System.out;
        PrintStream myOut = new dmnutils.TestStream() {

            public void afterCreated() {
            }

            public void print(String s) {
                //old.print(s);
                textOutput.printNoCR(s, ColoredTextPane.REGULAR);
            }
            };
        PrintStream myErr = new dmnutils.TestStream() {

            public void afterCreated() {
            }

            public void print(String s) {
                //old.print(s);
                textOutput.printNoCR(s, ColoredTextPane.ERROR);
            }
            };
        try {
            textOutput.setText("");
            changed();
            Interpreter interpreter = new Interpreter();
            try {
                System.setOut(myOut);
            } catch (SecurityException w) {
            }
            String eval_auto;
            eval_auto = jstat.plugins.wrappers.One.get();
            //textOutput.print(eval_auto, ColoredTextPane.ITALIC);
            interpreter.eval(eval_auto);
            //interpreter.set("plugin", this);
            interpreter.set("document", this.getParentDocument());
            interpreter.set("tools", new jstat.plugins.wrappers.Tools());
            interpreter.setOut(myOut);
            interpreter.setErr(myErr);
            interpreter.eval(textInput.getText());
        } catch (Exception e) {
            textOutput.printerr(e.getMessage());
            textOutput.printerr(Arrays.asList(e.getStackTrace()).toString());
        } finally {
            try {
                System.setOut(old);
            } catch (SecurityException w) {
            }
        }
    }

    public JTextArea getTextInput() {
        return textInput;
    }

    public static DocumentPageFactory<PageBeanShell> factory = new DocumentPageFactory<PageBeanShell>() {

        public PageBeanShell create(Document doc) {
            PageBeanShell result;
            result = new PageBeanShell();
            return result;
        }
    };
}
