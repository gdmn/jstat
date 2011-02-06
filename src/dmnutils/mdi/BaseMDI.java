/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.mdi;

import java.beans.*;
import java.io.*;
import java.util.zip.GZIPOutputStream;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.zip.GZIPInputStream;
import jstat.gui.*;

/**
 * Bazowa klasa dla aplikacji MDI. Zawiera metody, które mogą (powinny) być przesłonięte:<br>
 * <li><code>JMenuBar createMenuBar()</code> - tworzy pasek menu, który automatycznie jest dodawany przez konstruktor do okna<br>
 * <li><code>JToolBar createToolBar()</code> - tworzy pasek narzędzi, który automatycznie jest dodawany przez konstruktor do okna<br>
 * <li><code>void quit()</code> - zamyka aplikację<br>
 *
 * @author Damian
 */
public class BaseMDI<T extends BaseInternalFrame> extends JFrame implements Serializable { // implements ActionListener, InternalFrameListener {
    private Class<T>       internalFrameType;
    private JMenu          windowMenu;

    protected JDesktopPane desktop;
    protected JMenuBar     menuBar;
    protected JToolBar     toolBar;

    /**
     * Tworzy nową instancję klasy BaseMDI. Tworzone okno jest niewidoczne i wyśrodkowane na ekranie. Klasa automatycznie dodaje nowo tworzone okna (<code>createFrame()</code>)
     * do menu, które zostało przypisane za pomocą <code>setWindowMenu(JMenu windowMenu)</code>. Również zajmuje się usuwaniem z menu zamkniętych
     * okien oraz automatycznie aktywuje wybrane okno w przypadku kliknięcia przez użytkownika w odpowiednią pozycję menu.<br>
     * Aby zmienić domyślne menu, należy przesłonić <code>createMenuBar()</code>.<br>
     * Aby dodać pasek narzędzi, należy przesłonić <code>createToolBar()</code>.<br>
     *
     * @param internalFrameType klasa okien jakie mają być obsługiwane
     * @param title tytuł okna
     */
    public BaseMDI(Class<T> internalFrameType, String title) {
        super(title);

        // JPanel contentPane = new JPanel();

        this.internalFrameType = internalFrameType;

        int inset = 100;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        setPreferredSize(new Dimension(screenSize.width - inset * 2, screenSize.height - inset * 2));

        // Set up the GUI.
        desktop = new JDesktopPane(); // a specialized layered pane
        // desktop.setForeground(this.getForeground());
        desktop.setOpaque(false);
        // setContentPane(new JScrollPane(desktop, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS));
        getContentPane().add(desktop, BorderLayout.CENTER);

        createActions();

        setJMenuBar(menuBar = createMenuBar());

        // Create the toolbar.
        getContentPane().add(toolBar = createToolBar(), BorderLayout.PAGE_START);
        toolBar.setRollover(true);

        // add(contentPane, BorderLayout.CENTER);
        // setContentPane(contentPane);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ((JFrame) e.getSource()).setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                quit();
            }

            // inaczej jeśli uruchamiany jest program, nie jest aktywne automatycznie utworzone okno
            public void windowActivated(WindowEvent e) {
                if (desktop.getAllFrames().length > 0) {
                    activateInternalFrame(desktop.getAllFrames()[0]);
                    // System.out.println("zaaktywowano internal frame "+ desktop.getAllFrames()[0].getTitle());
                } else {
                    // System.out.println("NIE zaaktywowano internal frame");
                }
            }
        });

        pack();
        setLocationRelativeTo(null); //wyśrodkowanie
    }

    private static final String ACTION_NEW                     = MyAction.createNewId();
    private static final String ACTION_QUIT                    = MyAction.createNewId();
    private static final String ACTION_ABOUT                   = MyAction.createNewId();
    private static final String ACTION_ACTIVATEINTERNAL_PREFIX = MyAction.createNewId() + "##";
    private MyAction[]          actions;

    private void createActions() {
        actions = new MyAction[] { new MyAction(myActionListener, ACTION_NEW, "Nowy", new Integer(KeyEvent.VK_N), "Nowe okno", null),
        new MyAction(myActionListener, ACTION_QUIT, "Zakończ", new Integer(KeyEvent.VK_K), "Kończy program", null),
        new MyAction(myActionListener, ACTION_ABOUT, "O programie...", new Integer(KeyEvent.VK_P), null, null) };
    }

    /**
     * Tworzy menu. Należy przysłonić, aby zmienić domyślne.
     *
     * @return domyślne menu
     */
    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("Plik");
        menu.setMnemonic(KeyEvent.VK_P);
        menuBar.add(menu);

        // Set up the first menu item.
        JMenuItem menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_NEW));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
        menu.add(menuItem);

        // Set up the second menu item.
        menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_QUIT));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
        menu.add(menuItem);

        menu = new JMenu("Okno");
        menu.setMnemonic(KeyEvent.VK_O);
        menuBar.add(menu);
        setWindowMenu(menu);

        menuBar.add(Box.createHorizontalGlue());
        menu = new JMenu("Pomoc");
        menu.setMnemonic(KeyEvent.VK_P);
        menuBar.add(menu);
        menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_ABOUT));
        menuItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        menu.add(menuItem);

        return menuBar;
    }

    /**
     * Tworzy pasek narzędzi. Należy przysłonić, aby zmienić domyślne.
     *
     * @return domyślny pasek narzędzi
     */
    protected JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        JButton b = null;

        b = new JButton(MyAction.findAction(actions, ACTION_NEW));
        b.setFocusable(false);
        toolBar.add(b);

        b = new JButton(MyAction.findAction(actions, ACTION_ABOUT));
        b.setFocusable(false);
        toolBar.add(b);

        toolBar.addSeparator();
        b = new JButton(MyAction.findAction(actions, ACTION_QUIT));
        b.setFocusable(false);
        toolBar.add(b);

        return toolBar;
    }

    /**
     * Tworzy nowe wewnętrzne okno. Jeśli <code>windowMenu</code> jest różne od <code>null</code>, tworzy również nową pozycję w menu.
     */
    protected T createFrame() {
        try {
            T frame = internalFrameType.newInstance();
            return createFrame(frame);
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Tworzy nowe wewnętrzne okno. Jeśli <code>windowMenu</code> jest różne od <code>null</code>, tworzy również nową pozycję w menu.
     * @param frame ramka do dodania
     */
    protected T createFrame(T frame) {
        frame.setVisible(true); // necessary as of 1.3
        frame.addInternalFrameListener(myInternalFrameListener);
        desktop.add(frame);
        addInternalFrameToWindowMenu(frame);
        activateInternalFrame(frame);
        return frame;
    }

    /**
     * Uaktywnia wewnętrzne okno
     */
    public static void activateInternalFrame(JInternalFrame frame) {
        try {
            frame.setIcon(false);
            frame.setSelected(true);
        } catch (PropertyVetoException ex) {
        }
    }

    /**
     * Tworzy nową pozycję w menu.
     */
    private void addInternalFrameToWindowMenu(T f) {
        // for (Component i : menuBar.getComponents()) {
        // if (i instanceof JMenu && ((JMenu)i).getText().equals("Okno")) {
        // JMenu menu = ((JMenu)i);
        if (windowMenu == null) return;
        JMenuItem menuItem = new JMenuItem(f.getTitle());
        f.setMenuItem(menuItem);
        String fToString = f.toString();
        menuItem.setToolTipText(fToString);
        menuItem.setActionCommand(ACTION_ACTIVATEINTERNAL_PREFIX + MyAction.createNewId() + "##");
        menuItem.addActionListener(myActionListener);
        windowMenu.add(menuItem);
    }

    /**
     * Usuwa pozycję z menu. Wywoływane przy zamykaniu wewnętrznego okna.
     */
    private void removeInternalFrameFromWindowMenu(T f) {
        if (windowMenu == null) return;
        windowMenu.remove(f.getMenuItem());
    }

    /**
     * Zamyka aplikację. Najpierw wywołuje metodę <code>canClose()</code> dla każdego z okien wewnętrznych. Kończy program, jeśli każde z nich
     * zwróci <code>true</code> lub jeśli nie ma żadnych okien wewnętrznych
     */
    protected void quit() {
        // Object[] options = {"Tak", "Nie"};
        JInternalFrame[] frames = desktop.getAllFrames();
        int count = frames.length;
        int n = JOptionPane.YES_OPTION;
        if (n == JOptionPane.YES_OPTION) {
            for (int i = frames.length - 1; i >= 0; i--) {
                @SuppressWarnings("unchecked")
                T te = (T) frames[i];
                activateInternalFrame(te);
                if (te.canClose()) {
                    // frames[i].setClosed(true);
                    te.dispose();
                } else {
                    break;
                }
            }
            if (desktop.getAllFrames().length == 0) {
                System.exit(0);
            }
            // JOptionPane.showMessageDialog(this, "Nie udało się zamknąć: "+desktop.getAllFrames().length);
        }
    }

    /**
     * okienko z pomocą
     */
    protected void aboutProgram() {
        JOptionPane.showMessageDialog(null, "BaseMDI by Damian G.");
    }

    /**
     * TESTOWA PROCEDURA <br>
     * zapisuje obiekt do pliku (kompresja gzip)
     */
    public final void serializeMe(String fn) {
        try {
            //XMLEncoder out = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fn)));
            ObjectOutputStream out = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(fn)));
            out.writeObject(this);
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * TESTOWA PROCEDURA <br>
     * wczytuje obiekt z pliku (kompresja gzip)
     */
    public static final BaseMDI unSerializeMe(String fn) {
        //XMLDecoder in = null;
        ObjectInputStream in = null;
        BaseMDI b = null;
        try {
            in = new ObjectInputStream(new GZIPInputStream(new FileInputStream(fn)));
            //in = new XMLDecoder(new BufferedInputStream(new FileInputStream(fn)));
            try {
                b = (BaseMDI) in.readObject();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } finally {
                in.close();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return b;
    }

    private transient MyActionListener myActionListener = new MyActionListener();

    private class MyActionListener implements ActionListener, Serializable {
        // React to menu selections.
        public void actionPerformed(ActionEvent e) {
            if (ACTION_NEW.equals(e.getActionCommand())) { // new
                createFrame();
            } else if (ACTION_QUIT.equals(e.getActionCommand())) {
                quit();
            } else if (ACTION_ABOUT.equals(e.getActionCommand())) {
                aboutProgram();
            } else if (e.getActionCommand().startsWith(ACTION_ACTIVATEINTERNAL_PREFIX)) {
                JInternalFrame[] frames = desktop.getAllFrames();
                for (int i = 0; i < frames.length; i++) {
                    if (((BaseInternalFrame) frames[i]).getMenuItem() == e.getSource()) {
                        JInternalFrame frame = frames[i];
                        activateInternalFrame(frame);
                        break;
                    }
                }
            }
        }
        // private void writeObject(ObjectOutputStream stream) throws IOException { }
        // private void readObject(ObjectOutputStream stream) throws IOException, ClassNotFoundException { }
    }

    private transient MyInternalFrameListener myInternalFrameListener = new MyInternalFrameListener();

    private class MyInternalFrameListener implements InternalFrameListener, Serializable {
        public void internalFrameClosing(InternalFrameEvent e) {
            // displayMessage("Internal frame closing", e);
            @SuppressWarnings("unchecked")
            T te = (T) e.getSource();
            activateInternalFrame(te);
            if (!te.canClose()) {
                te.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            } else {
                te.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            }
        }

        public void internalFrameClosed(InternalFrameEvent e) {
            // displayMessage("Internal frame closed", e);
            @SuppressWarnings("unchecked")
            T te = (T) e.getSource();
            removeInternalFrameFromWindowMenu(te);
            if (desktop.getAllFrames().length == 0) {
                T.resetPositionCounter();
            }
        }

        public void internalFrameOpened(InternalFrameEvent e) {
            // displayMessage("Internal frame opened", e);
        }

        public void internalFrameIconified(InternalFrameEvent e) {
            // displayMessage("Internal frame iconified", e);
        }

        public void internalFrameDeiconified(InternalFrameEvent e) {
            // displayMessage("Internal frame deiconified", e);
        }

        public void internalFrameActivated(InternalFrameEvent e) {
            // displayMessage("Internal frame activated", e);
        }

        public void internalFrameDeactivated(InternalFrameEvent e) {
            // displayMessage("Internal frame deactivated", e);
        }
    }

    public JMenu getWindowMenu() {
        return windowMenu;
    }

    public void setWindowMenu(JMenu windowMenu) {
        this.windowMenu = windowMenu;
    }

    public static void main(String[] args) {
        dmnutils.SwingUtils.run(new BaseMDI<BaseInternalFrame>(BaseInternalFrame.class, "MDI Test"));
    }

}