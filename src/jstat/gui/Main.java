/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.gui;

import beanutils.*;
import dmnutils.*;
import dmnutils.document.*;
import dmnutils.mdi.*;
import java.io.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jstat.functioneditor.FunctionEditor;
import jstat.obliczenia.ObliczeniaTest;
import jstat.pages.bean.PageBean;
import jstat.pages.bsh.PageBeanShell;
import jstat.pages.calculator.PageCalculator;
import jstat.pages.graph.PageGraph;
import jstat.pages.notepad.PageNotepad;
import jstat.pages.sheet.PageSheet;

/**
 * Główna klasa programu. Tworzy okno aplikacji.
 *
 * @author Damian
 */
public class Main extends BaseMDI<MyInternalFrame> implements ActionListener {
	private static Main main;
	private static ColoredTextPane logText;

	public static Main getMain() {
		return main;
	}


	static {
		SwingUtils.classForImages = jstat.EmptyClass.class;
		logText = new ColoredTextPane();
		Log log = new Log(logText, jstat.Settings.get().getCaptureOutput());
	}

	/** Creates a new instance of Main */
	public Main() {
		super(MyInternalFrame.class, jstat.Version.NAME_VER);
		if (main != null) {
			throw new RuntimeException("Próba utworzenia dwóch instancji aplikacji");
		}
		main = this;
		appletContext = null;

		JPanel logPane = new JPanel(new BorderLayout());
		logText.setEditable(false);
		// logTextArea.setFont((new JTextField()).getFont()); //przy wyglądzie windows jest zmniejszona czcionka w logTextArea
		JScrollPane scrollPane = new JScrollPane(logText);
		logPane.add(scrollPane, BorderLayout.CENTER);
		logPane.setPreferredSize(new Dimension(200, 80));
		// logPane.setMaximumSize(logPane.getPreferredSize());

		getContentPane().remove(desktop);
		System.out.println("" + jstat.Settings.get().toString());
		System.out.println("" + jstat.Settings.get().getFancyBackground().toString());
		desktop = new JDesktopPane() {
			public void paintComponent(Graphics g) {

				setBackground((new JPanel()).getBackground());
				super.paintComponent(g);
				if (!jstat.Settings.get().getFancyBackground()) {
					return;
				}
				Rectangle bounds = getBounds();
				bounds.add(bounds.x - 1, bounds.y - 1);
				bounds.add(bounds.width + 2, bounds.height + 2);
				ImageIcon icon = SwingUtils.createNavigationIcon("background");
				if (icon == null) {
					return;
				}
				Image image = icon.getImage();
				for (int x = bounds.x; x < bounds.width; x += icon.getIconWidth()) {
					for (int y = bounds.x; y < bounds.height; y += icon.getIconHeight()) {
						g.drawImage(image, x, y, null);
					}
				}

				icon = SwingUtils.createNavigationIcon("logo");
				if (icon == null) {
					return;
				}
				image = icon.getImage();
				g.drawImage(image, bounds.width - icon.getIconWidth(), bounds.y/*bounds.height - icon.getIconHeight()*/, null);
			}
		};
		jstat.Settings.get().addPropertyChangeListener(new PropertyChangeListener() {
			// odrysowanie tła przy zmianie właściwości w ustawieniach
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(jstat.Settings.PROP_FANCYBACKGROUND)) {
					desktop.repaint();
				}
			}
		});
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, desktop, logPane);
		splitPane.setResizeWeight(1.0); // przy zmianie rozmiaru dolny panel pozostaje bez zmian

		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true); // przesuwanie "na żywo"

		getContentPane().add(splitPane, BorderLayout.CENTER);

		createFrame(); // create first "window"
		// The root logger's handlers default to INFO. We have to
		// crank them up. We could crank up only some of them
		// if we wanted, but we will turn them all up.
		Handler[] handlers =
				Logger.getLogger("").getHandlers();
		for (int index = 0; index < handlers.length; index++) {
			handlers[index].setLevel(Level.SEVERE);
		}
	// pack();
	}

	@Override
	protected MyInternalFrame createFrame(MyInternalFrame frame) {
		MyInternalFrame f = super.createFrame(frame);
		if (jstat.Settings.get().getMaximizeNewInternalFrame()) {
			try {
				f.setMaximum(true);
			} catch (PropertyVetoException ex) {
				//Logger.getLogger(BaseMDI.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return f;
	}
	private JApplet appletContext;

	public Main(JApplet appletContext) {
		this();
		this.appletContext = appletContext;
	}

	public JApplet getAppletContext() {
		return appletContext;
	}



	@Override
	protected JToolBar createToolBar() {
		JToolBar tb = new JToolBar();
		// JToolBar toolBar = super.createToolBar(); toolBar.addSeparator(); toolBar.addSeparator();
		tb.setFocusable(false);
		tb.setFloatable(false);
		tb.setBorderPainted(jstat.Settings.get().getToolbarBorderPainted());
		for (int i = 0; i < actions.length; i++) {
			MyAction a = actions[i];
			if (a == null && i < actions.length - 1 && actions[i + 1] == null) {
				break;
			}
			if (a == null) {
				tb.addSeparator();
			} else if (!(a instanceof MyObliczeniaAction)) {
				tb.add(MyAction.createToolBarButton(a));
			}
		}
		return tb;
	}

	protected JMenuBar createMenuBar() {
		JMenuBar mb = new JMenuBar();
		createActions();

		JMenu menu = new JMenu("Plik");
		menu.setMnemonic(KeyEvent.VK_P);
		mb.add(menu);

		// Set up the first menu item.
		JMenuItem menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_NEW));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_OPEN));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_SAVE));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_SAVEAS));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
		menu.add(menuItem);

		// Set up the second menu item.
		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_QUIT));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		menu = new JMenu("Wstaw");
		menu.setMnemonic(KeyEvent.VK_W);
		mb.add(menu);

		for (MyAction a : actions) {
			if (a instanceof MyInsertAction) {
				menuItem = new JMenuItem(a);
				// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
				menu.add(menuItem);
			}
		}

		menu = new JMenu("Okno");
		menu.setMnemonic(KeyEvent.VK_O);
		mb.add(menu);
		setWindowMenu(menu);

		menu = new JMenu("Narzędzia");
		menu.setMnemonic(KeyEvent.VK_N);
		mb.add(menu);
		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_FUNCTIONEDITOR));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
		menu.add(menuItem);
		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_CLEARLOG));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_PREFERENCES));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		menu = new JMenu("Obliczenia");
		menu.setMnemonic(KeyEvent.VK_L);
		mb.add(menu);

		for (MyAction a : actions) {
			if (a instanceof MyObliczeniaAction) {
				menuItem = new JMenuItem(a);
				// menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
				menu.add(menuItem);
			}
		}

		if (jstat.Settings.get().getPluginsEnabled()) {
			dmnutils.plugins.Plugins.search();
			dmnutils.plugins.Plugins.loadThesePlugins(jstat.EmptyClass.class,
					"projectplugins/homepage.bsh",
					"projectplugins/helpinbrowser.bsh",
					"projectplugins/first.bsh");
			menu = dmnutils.plugins.Plugins.getJMenu();
			//menu.setMnemonic(KeyEvent.VK_Y);
			mb.add(menu);
		}

		mb.add(Box.createHorizontalGlue());
		menu = new JMenu("Pomoc");
		menu.setMnemonic(KeyEvent.VK_C);
		mb.add(menu);
		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_HELP));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem(MyAction.findAction(actions, ACTION_ABOUT));
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, ActionEvent.CTRL_MASK));
		menu.add(menuItem);

		return mb;
	}
	private static final String ACTION_ABOUT = MyAction.createNewId();
	private static final String ACTION_HELP = MyAction.createNewId();
	private static final String ACTION_NEW = MyAction.createNewId();
	private static final String ACTION_OPEN = MyAction.createNewId();
	private static final String ACTION_SAVE = MyAction.createNewId();
	private static final String ACTION_SAVEAS = MyAction.createNewId();
	private static final String ACTION_QUIT = MyAction.createNewId();
	private static final String ACTION_HALT = MyAction.createNewId();
	private static final String ACTION_CLEARLOG = MyAction.createNewId();
	private static final String ACTION_PREFERENCES = MyAction.createNewId();
	private static final String ACTION_FUNCTIONEDITOR = MyAction.createNewId();
	private static final String ACTION_INSERTSHEET = MyAction.createNewId();
	private static final String ACTION_INSERTCALCULATOR = MyAction.createNewId();
	private static final String ACTION_INSERTGRAPH = MyAction.createNewId();
	private static final String ACTION_INSERTBEAN = MyAction.createNewId();
	private static final String ACTION_INSERTNOTEPAD = MyAction.createNewId();
	private static final String ACTION_INSERTBEANSHELL = MyAction.createNewId();
	private static final String ACTION_INSERTOBLICZENIA = MyAction.createNewId();
	private static final String ACTION_INSERTPODSTAWOWESTATYSTYKI = MyAction.createNewId();
	private static final String ACTION_INSERTCHARAKTERYSTYKILICZBOWE = MyAction.createNewId();
	private static final String ACTION_INSERTREGRESJA = MyAction.createNewId();
	private static final String ACTION_INSERTDYNAMIKA = MyAction.createNewId();
	private static final String ACTION_SERIALIZE = MyAction.createNewId();
	private static final String ACTION_2 = MyAction.createNewId();
	private static final String ACTION_UNSERIALIZE = MyAction.createNewId();
	private MyAction[] actions;

	private interface SerializableActionListener extends ActionListener, Serializable {
	}

	private class MyInsertAction extends MyAction implements Serializable {
		public MyInsertAction(final DocumentPageFactory factory, String id, String text, Integer mnemonic, String desc, ImageIcon icon) {
			super(new SerializableActionListener() {
				public void actionPerformed(ActionEvent e) {
					insertPage(factory);
				}
			}, id, text, mnemonic, desc, icon);
		}
	}

	private class MyObliczeniaAction extends MyAction implements Serializable {
		public MyObliczeniaAction(final DocumentPageFactory factory, String id, String text, Integer mnemonic, String desc, ImageIcon icon) {
			super(new SerializableActionListener() {
				public void actionPerformed(ActionEvent e) {
					insertPage(factory);
				}
			}, id, text, mnemonic, desc, icon);
		}
	}

	private void createActions() {
		actions = new MyAction[]{
					new MyAction(this, ACTION_NEW, "Nowy", new Integer(KeyEvent.VK_N), "Nowy dokument", SwingUtils.createNavigationIcon("document-new")),
					new MyAction(this, ACTION_OPEN, "Otwórz...", new Integer(KeyEvent.VK_O), "Otwiera dokument", SwingUtils.createNavigationIcon("document-open")),
					new MyAction(this, ACTION_SAVE, "Zapisz", new Integer(KeyEvent.VK_Z), "Zapisuje dokument", SwingUtils.createNavigationIcon("document-save")),
					null,
					new MyAction(this, ACTION_PREFERENCES, "Ustawienia", new Integer(KeyEvent.VK_U), "Opcje programu", SwingUtils.createNavigationIcon("preferences-system")),
					new MyAction(this, ACTION_CLEARLOG, "Wyczyść log", new Integer(KeyEvent.VK_G), "Czyści dziennik", SwingUtils.createNavigationIcon("edit-clear")),
					null,
					//new MyObliczeniaAction(ObliczeniaTest.factory, ACTION_INSERTOBLICZENIA, "OblTst", null, "Dodaje obliczenia", null),
					new MyObliczeniaAction(jstat.obliczenia.podstawowestatystyki.PodstawoweStatystyki.factory, ACTION_INSERTPODSTAWOWESTATYSTYKI, "Podstawowe statystyki", null,
					"Oblicza podstawowe statystyki", null),
					new MyObliczeniaAction(jstat.obliczenia.podstawowestatystyki.zbiorowosci.CharakterystykiLiczbowe.factory, ACTION_INSERTCHARAKTERYSTYKILICZBOWE, "Charakterystyki liczbowe", null,
					"Oblicza charakterystyki liczbowe struktury zbiorowości", null),
					new MyObliczeniaAction(jstat.obliczenia.regresja.Regresja.factory, ACTION_INSERTREGRESJA, "Regresja", null,
					"Oblicza regresję liniową", null),
					new MyObliczeniaAction(jstat.obliczenia.analizadynamikizjawisk.AnalizaDynamiki.factory, ACTION_INSERTDYNAMIKA, "Analiza dynamiki", null,
					"Dokonuje analizy dynamiki zjawisk", null),
					new MyInsertAction(PageSheet.factory, ACTION_INSERTSHEET, "Arkusz", new Integer(KeyEvent.VK_A),
					"Dodaje arkusz do bieżącego dokumentu", SwingUtils.createNavigationIcon("x-office-spreadsheet")),
					new MyInsertAction(PageCalculator.factory, ACTION_INSERTCALCULATOR, "Kalkulator", new Integer(KeyEvent.VK_K),
					"Dodaje kalkulator do bieżącego dokumentu", SwingUtils.createNavigationIcon("accessories-calculator")),
					new MyInsertAction(PageGraph.factory, ACTION_INSERTGRAPH, "Wykres", new Integer(KeyEvent.VK_W),
					"Dodaje wykres do bieżącego dokumentu", SwingUtils.createNavigationIcon("x-office-presentation")),
					// TODO: zamienić na PageNotepad w końcowej wersji
					///PageHtmlOutput
					new MyInsertAction(PageNotepad.factory, ACTION_INSERTNOTEPAD, "Notatnik", new Integer(KeyEvent.VK_N),
					"Dodaje notatnik", SwingUtils.createNavigationIcon("accessories-text-editor")),
					new MyInsertAction(PageBeanShell.factory, ACTION_INSERTBEANSHELL, "Skrypt", new Integer(KeyEvent.VK_B),
					"Dodaje skrypt", SwingUtils.createNavigationIcon("text-x-script")),
					null,
					new MyAction(this, ACTION_QUIT, "Zakończ", new Integer(KeyEvent.VK_K), "Kończy program", SwingUtils.createNavigationIcon("system-log-out")),
					//new MyAction(this, ACTION_HALT, "Halt", null, "Halt", SwingUtils.createNavigationIcon("system-shutdown")),
					null,
					new MyAction(this, ACTION_HELP, "Pomoc", new Integer(KeyEvent.VK_P), null, SwingUtils.createNavigationIcon("help-browser")),
					new MyAction(this, ACTION_ABOUT, "O programie...", new Integer(KeyEvent.VK_O), null, SwingUtils.createNavigationIcon("help-about")),
					null, null,
					new MyAction(this, ACTION_FUNCTIONEDITOR, "Edytor funkcji", new Integer(KeyEvent.VK_F), "Otwiera edytor funkcji (z pomocą)", SwingUtils.createNavigationIcon("edit")),
					new MyInsertAction(PageBean.factory, ACTION_INSERTBEAN, "Bean", null,
					"Dodaje JavaBean", SwingUtils.createNavigationIcon("text-x-script")),
					new MyAction(this, ACTION_SAVEAS, "Zapisz jako...", new Integer(KeyEvent.VK_J), "Zapisuje dokument pod nową nazwą", SwingUtils.createNavigationIcon("document-save-as")),
				};
	}

	/**
	 * Dodaje stronę do aktywnego dokumentu.
	 * @param factory fabryka tworząca stronę.
	 * @return utworzona strona.
	 */
	public AbstractDocumentPage insertPage(DocumentPageFactory factory) {
		MyInternalFrame mif = getActiveFrame(true);
		if (mif == null) {
			return null;
		}
		AbstractDocumentPage page = factory.create(mif.getDocument());
		if (page != null) {
			mif.getDocument().add(page);
			mif.showDocumentPage(mif.getDocument().size() - 1);
			mif.firePageChanged();
			return page;
		}
		return null;
	}

	/**
	 * Wstawia nową pustą stronę i zwraca główny panel tej strony.
	 * @param caption etykieta pojawiająca się po lewej stronie.
	 * @return nowo utworzony panel wypełniający stronę.
	 */
	public JPanel insertJPanel(String caption) {
		MyInternalFrame mif = getActiveFrame(true);
		if (mif == null) {
			return null;
		}
		AbstractDocumentPage page = new AbstractDocumentPage() {
			@Override
			public void writeExternal(ObjectOutput out) throws IOException {
				throw new RuntimeException("Nie można zapisać elementów utworzonych przez Main.insertJPanel");
			}
		};
		page.setCaption(caption);
		if (page != null) {
			mif.getDocument().add(page);
			mif.showDocumentPage(mif.getDocument().size() - 1);
			mif.firePageChanged();
			return page.getPanel();
		}
		return null;
	}

	/**
	 * Sprawdza, czy istnieje jakiś otwarty dokument.
	 * @param withErrorMessage wyświetla okienko z informacją, gdy nie ma aktywnego dokumentu
	 * @return aktywny dokument lub <code>null</code>
	 */
	//TODO: przenieść do klasy nadrzędnej - może save/load też?
	public MyInternalFrame getActiveFrame(boolean withErrorMessage) {
		MyInternalFrame mif = (MyInternalFrame) desktop.getSelectedFrame();
		if (mif == null) {
			if (withErrorMessage) {
				JOptionPane.showMessageDialog(this, "Żadne okno z dokumentem nie jest aktywne.", "Błąd", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}
		try {
			mif.setIcon(false);
		} catch (PropertyVetoException ex) {
		}
		return mif;
	}

	/**
	 * Wyświetla dialog do wyboru nazwy pliku i zapisuje aktywny dokument
	 */
	public boolean saveDocumentAs() {
		MyInternalFrame mif = getActiveFrame(true);
		if (mif == null) {
			return false;
		}
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		fc.addChoosableFileFilter(new JStatFileFilter());
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selected = fc.getSelectedFile();
			if (selected.exists()) {
				int n = JOptionPane.showOptionDialog(this, "Plik \"" + selected.toString() + "\" istnieje.\nZastąpić?", "Plik istnieje",
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
						null, null, null);
				if (n == JOptionPane.OK_OPTION) {
					return saveDocument(fc.getSelectedFile());
				} else {
					return false;
				}
			} else {
				return saveDocument(fc.getSelectedFile());
			}
		} else {
			return false;
		}
	}

	/**
	 * Zapisuje zmiany w bieżącym dokumencie, jeśli nie jest
	 * jeszcze nadana nazwa pliku, wyświetla dialog do wyboru
	 * pliku do zapisania.
	 * @return true jeśli zapisano, false jeśli anulowano.
	 */
	public boolean saveDocument() {
		MyInternalFrame mif = getActiveFrame(true);
		if (mif == null) {
			return false;
		}
		String fn = mif.getDocument().getFileName();
		if (fn != null) {
			return saveDocument(new File(fn));
		} else {
			return saveDocumentAs();
		}
	}

	/**
	 * Zapisuje aktywny dokument pod wskazaną nazwą
	 */
	public boolean saveDocument(File filename) {
		MyInternalFrame mif = getActiveFrame(true);
		if (mif == null) {
			return false;
		}
		try {
			mif.getDocument().serializeMe(JStatFileFilter.setJstatExtension(filename));
			System.out.println("Zapisano jako " + filename.toString());
			return true;
		} catch (HeadlessException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Wyświetla dialog do wyboru pliku (lub plików) i wczytuje wskazane
	 */
	public void loadDocuments() {
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(true);
		fc.addChoosableFileFilter(new JStatFileFilter());
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loadDocuments(fc.getSelectedFiles());
		}
	}

	/**
	 * Otwiera wskazane pliki
	 * @param filenames tablica plików do otworzenia
	 */
	public void loadDocuments(File... filenames) {
		for (File f : filenames) {
			try {
				Document d = Document.unSerializeMe(f.getCanonicalPath());
				createFrame(new MyInternalFrame(d));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		final String ac = e.getActionCommand();
		if (ac.equals(ACTION_NEW)) {
			createFrame();
		} else if (ac.equals(ACTION_QUIT)) {
			quit();
		} else if (ac.equals(ACTION_HALT)) {
			System.exit(1);
		} else if (ac.equals(ACTION_ABOUT)) {
			aboutProgram();
		} else if (ac.equals(ACTION_HELP)) {
			//help();
			try {
				HelpWithTree.help().setVisible(true);
			} catch (FileNotFoundException ee) {
			}
		} else if (ac.equals(ACTION_CLEARLOG)) {
			Log.clear();
		} else if (ac.equals(ACTION_SERIALIZE)) {
			throw new UnsupportedOperationException(MyAction.findAction(actions, ACTION_SERIALIZE).getValue(Action.NAME).toString());
		} else if (ac.equals(ACTION_UNSERIALIZE)) {
			throw new UnsupportedOperationException(MyAction.findAction(actions, ACTION_UNSERIALIZE).getValue(Action.NAME).toString());
		} else if (ac.equals(ACTION_2)) {
			MyAction a2 = MyAction.findAction(actions, ACTION_QUIT);
			MyAction a1 = MyAction.findAction(actions, ACTION_SERIALIZE);
			a1.setEnabled(!a1.isEnabled());
			a2.setEnabled(a1.isEnabled());
			MyAction.findAction(actions, ACTION_UNSERIALIZE).setEnabled(a1.isEnabled());
		} else if (ac.equals(ACTION_OPEN)) {
			this.loadDocuments();
		} else if (ac.equals(ACTION_SAVEAS)) {
			this.saveDocumentAs();
		} else if (ac.equals(ACTION_SAVE)) {
			this.saveDocument();
		} else if (ac.equals(ACTION_PREFERENCES)) {
			SettingsEditor.showDialog(this);
		} else if (ac.equals(ACTION_FUNCTIONEDITOR)) {
			FunctionEditor.showDialog(this);
		} else {
			throw new UnsupportedOperationException("Not supported yet: " + e.toString());
		}
	}

	@Override
	protected void aboutProgram() {
		AboutProgram.aboutProgram();
	}

	private void help() {
		try {
			JFrame h = new JFrame("Pomoc " + jstat.Version.NAME);
			h.setContentPane(new Help());
			h.pack();
			h.setLocationRelativeTo(null);
			h.setVisible(true);
		} catch (FileNotFoundException ex) {
			jstat.plugins.wrappers.Tools.showErr(ex.getMessage());
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void main(String[] args) {
		SwingUtils su = new dmnutils.SwingUtils(false, false);
		if (jstat.Settings.get().getLookAndFeel() == jstat.Settings.LookAndFeel.OCEAN) {
			SwingUtils.setLookFeel(SwingUtils.metal);
		} else {
				SwingUtils.setLookFeel(null);
		}
		if (args.length > 0) {
			for (String arg : args) {
				if (arg.startsWith("lf=")) {
					String s = arg.substring(3);
					String lf = (String) BeanSetterGetter.getBeanPropertyEasy(su, "" + s);
					if (lf == null) {
						System.err.println("Niepoprawny identyfikator LookAndFeel: " + s + ". Zostanie użyty domyślny dla systemu.");
					}
					//SwingUtils.setDefaultLookAndFeelDecorated(true, true);
					SwingUtils.setDefaultLookAndFeelDecorated(!true, !true);
					if (SwingUtils.setLookFeel(lf)) {
						System.out.println("Ustawiono L&F: " + lf);
					} else {
						System.err.println("Nie można ustawić LookAndFeel: " + s + ". Zostanie użyty domyślny dla systemu.");
					}
				}
			}
		}
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dmnutils.SwingUtils.run(new Main());
			}
		});
	}
}
