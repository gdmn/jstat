/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.gui;

import dmnutils.SwingUtils;
import dmnutils.document.*;
import dmnutils.mdi.*;
import java.awt.event.ActionEvent;
import java.beans.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Klasa reprezentująca okno z pojedynczym dokumentem.
 *
 * @author Damian
 */
public class MyInternalFrame extends BaseInternalFrame implements ActionListener {
	private JList leftList;
	private JPanel rightPanel;
	private Document document;
	private JPopupMenu listPopupMenu;
	private PropertyChangeListener documentPCL = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			String name = evt.getPropertyName();
			if (evt.getOldValue() != null && evt.getNewValue() != null) {
				System.out.println("documentPropertyChangeListener " + name + ": " + evt.getOldValue().toString() + " -> " + evt.getNewValue().toString());
			}
			if (name.equals("filename") || name.equals("changed")) {
				String now = MyInternalFrame.this.getTitle();
				String fn = MyInternalFrame.this.document.getFileName();
				if (fn == null && name.equals("changed") && !now.substring(0, 1).equals("*")) {
					MyInternalFrame.this.setTitle("* " + now);
				} else {
					MyInternalFrame.this.setTitle((MyInternalFrame.this.document.isChanged() ? "* " : "") + MyInternalFrame.this.document.getFileName());
				}
			} else if (name.equals("selected")) {
				showDocumentPage((Integer) evt.getNewValue());
			} else if (name.equals("count")) {
				//MyInternalFrame.this.firePageChanged();
			}
		}
	};

	public MyInternalFrame(Document doc) {
		super();
		JPanel p1 = new JPanel(new BorderLayout());
		JPanel p2 = new JPanel(new BorderLayout());
		this.document = doc;
		doc.addPropertyChangeListener(documentPCL);
		leftList = new JList(myListModel);
		leftList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leftList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					return;
				}
				showDocumentPage(leftList.getSelectedIndex());
			}
		});
		listPopupMenu = new JPopupMenu();
		leftList.addMouseListener(new dmnutils.PopupListener(listPopupMenu));
		createControls();
		JScrollPane listScrollPane = new JScrollPane(leftList);
		listScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		p1.add(listScrollPane, BorderLayout.CENTER);
		rightPanel = p2;
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, p1, p2);
		splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		p1.setPreferredSize(new Dimension(150, -1));
		leftList.setSelectedIndex(0);
		splitPane.setResizeWeight(0.0);
		splitPane.setOneTouchExpandable(!true);
		splitPane.setContinuousLayout(true); // przesuwanie "na żywo"

		setContentPane(splitPane);
		setSize(700, 300);
		String fn = doc.getFileName();
		if (fn != null) {
			this.setTitle(doc.getFileName());
		}
	}
	public static final String ACTION_DELETE = MyAction.createNewId();
	public static final String ACTION_UP = MyAction.createNewId();
	public static final String ACTION_DOWN = MyAction.createNewId();
	public static final String ACTION_PROPERTIES = MyAction.createNewId();
	public static final String ACTION_RENAME = MyAction.createNewId();
	private MyAction[] actions;

	private void createActions() {
		actions = new MyAction[]{
					new MyAction(this, ACTION_RENAME, "Zmień nazwę", null, "Zmienia nazwę podstrony", SwingUtils.createNavigationIcon("edit")),
					new MyAction(this, ACTION_DELETE, "Usuń", null, "Usuwa podstronę", SwingUtils.createNavigationIcon("edit-delete")),
					new MyAction(this, ACTION_UP, "Do góry", null, "Przesuwa podstronę w górę", SwingUtils.createNavigationIcon("go-up")),
					new MyAction(this, ACTION_DOWN, "W dół", null, "Przesuwa podstronę w dół", SwingUtils.createNavigationIcon("go-down")),
				//new MyAction(this, ACTION_PROPERTIES, "Właściwości", null, "Pokazuje właściwości podstrony", SwingUtils.createNavigationIcon("go-down")),
				};
	}

	private void createControls() {
		createActions();
		for (int i = 0; i < actions.length; i++) {
			MyAction a = actions[i];
			if (a == null && i < actions.length - 1 && actions[i + 1] == null) {
				break;
			}
			if (a == null) {
				listPopupMenu.addSeparator();
			} else {
				JMenuItem menuItem;
				menuItem = new JMenuItem(a);
				listPopupMenu.add(menuItem);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand() == ACTION_RENAME) {
			AbstractDocumentPage adp = getActiveDocumentPage(true, null);
			if (adp != null) {
				String nazwa = jstat.plugins.wrappers.Tools.getText("Nowa nazwa podstrony:", adp.getCaption());
				if (nazwa != null && nazwa.length() > 0) {
					adp.setCaption(nazwa);
					firePageChanged();
				} else {
					System.err.println("Nic nie wprowadzono");
				}
			}
		} else if (e.getActionCommand() == ACTION_DELETE) {
			AbstractDocumentPage adp = getActiveDocumentPage(true, null);
			if (adp != null) {
				boolean o = jstat.plugins.wrappers.Tools.getYesNo("Usunąć " + adp.getCaption() + "?");
				if (o) {
					adp.getParentDocument().remove(adp);
					firePageChanged();
				}
			}
		} else if (e.getActionCommand().equals(ACTION_UP) || e.getActionCommand().equals(ACTION_DOWN)) {
			AbstractDocumentPage adp = getActiveDocumentPage(true, null);
			if (adp != null) {
				Document d = adp.getParentDocument();
				int index = d.indexOf(adp);
				int index2 = -1;
				if (e.getActionCommand().equals(ACTION_UP) && index > 0) {
					index2 = index - 1;
				} else if (e.getActionCommand().equals(ACTION_DOWN) && index < d.size() - 1) {
					index2 = index + 1;
				}
				if (index2 > -1) {
					d.set(index, d.get(index2));
					d.set(index2, adp);
					firePageChanged();
					d.setSelected(index2);
				} else {
					System.err.println("Nie można przesunąć");
				}
			}
		} else {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public void setDocument(Document document) {
		this.document.removePropertyChangeListener(documentPCL);
		this.document = document;
		document.addPropertyChangeListener(documentPCL);
	}

	/** Creates a new instance of MyInternalFrame */
	public MyInternalFrame() {
		this(new Document());
	}

	/** Pobiera kontekstowe menu listy stron wyświetlanej po lewej stronie */
	public JPopupMenu getListPopupMenu() {
		return listPopupMenu;
	}

	@Override
	public boolean canClose() {
		if (!document.isChanged()) {
			return true;
		}
		int n = JOptionPane.YES_OPTION;
		Object[] options = {"Tak", "Nie", "Anuluj"};
		n = JOptionPane.showOptionDialog(this, "Zmieniono dokument.\n" + "Zapisać zmiany?", this.getTitle(), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, options[0]);
		boolean successfulSaved = false;
		if (n == JOptionPane.YES_OPTION) {
			successfulSaved = saveDocument();
		}
		return n == JOptionPane.NO_OPTION || (n == JOptionPane.YES_OPTION && successfulSaved);
	}

	public boolean saveDocument() {
		Container parent = dmnutils.SwingUtils.findParentWithGivenClass(this, JFrame.class);
		if (parent instanceof Main) {
			return ((Main) parent).saveDocument();
		} else {
			System.out.println("parent : " + this.getParent());
			JOptionPane.showMessageDialog(this, "save not yet implemented", getTitle(), JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	public Document getDocument() {
		return document;
	}
	MyListModel myListModel = new MyListModel();

	class MyListModel extends AbstractListModel {
		public int getSize() {
			return document.size();
		}

		public Object getElementAt(int index) {
			return document.get(index);
		}

		public void fireContentsChanged() {
			this.fireContentsChanged(this, 0, leftList.getModel().getSize());
		}
	}

	/**
	 * Pokazuje stronę dokumentu o podanym indeksie w panelu po prawej.
	 */
	public void showDocumentPage(int index) {
		if (leftList.getSelectedIndex() != index) {
			leftList.setSelectedIndex(index);
//            System.out.println("aaaaaaaaaaaaaaaaaaa "+index);
		}
		AbstractDocumentPage dpi = null;
		if (index >= 0) {
			dpi = document.get(index);
			document.setSelected(index);
		}
		// JOptionPane.showMessageDialog(this, "strona "+index);
		rightPanel.removeAll();
		if (index >= 0) {
			rightPanel.add(dpi.getPanel(), BorderLayout.CENTER);
		}
		rightPanel.validate();
		rightPanel.repaint();
		if (dpi != null) {
			dpi.getPanel().requestFocusInWindow();
		}
	}

	/**
	 * Pobiera zaznaczoną stronę dokumentu.
	 *
	 * @param withErrorMessage wyświetla okienko z informacją, gdy liczba stron
	 * jest równa 0 lub niezgodna z podaną klasą
	 * @param parentComponent komponent służący jako rodzic dla wyświetlanej
	 * wiadomości (może być null)
	 * @param document dokument, z którego pobieramy aktywną stronę
	 * @param c jeśli różne od <code>null</code>, sprawdza, czy zaznaczona
	 * strona jest danej klasy
	 * @return aktywna strona lub <code>null</code>
	 */
	public static AbstractDocumentPage checkActiveDocumentPage(boolean withErrorMessage, Component parentComponent, Document document, Class<? extends AbstractDocumentPage> c) {
		if (document.size() == 0) {
			if (withErrorMessage) {
				JOptionPane.showMessageDialog(parentComponent, "Dokument nie ma stron.", "Błąd", JOptionPane.ERROR_MESSAGE);
			}
			return null;
		}
		AbstractDocumentPage dpi = document.get(document.getSelected());
		if (c != null) {
			if (!c.isInstance(dpi)) {
				if (withErrorMessage) {
					JOptionPane.showMessageDialog(parentComponent, "Zaznaczona strona nie jest typu " + c.getSimpleName() + ".", "Błąd", JOptionPane.ERROR_MESSAGE);
				}
				return null;
			}
		}
		return dpi;
	}

	/**
	 * Pobiera zaznaczoną stronę dokumentu.
	 *
	 * @param withErrorMessage wyświetla okienko z informacją, gdy liczba stron
	 * jest równa 0 lub niezgodna z podaną klasą
	 * @param c jeśli różne od <code>null</code>, sprawdza, czy zaznaczona
	 * strona jest danej klasy
	 * @return aktywna strona lub <code>null</code>
	 */
	public AbstractDocumentPage getActiveDocumentPage(boolean withErrorMessage, Class c) {
		return checkActiveDocumentPage(withErrorMessage, this, document, c);
	}

	/**
	 * Odświeża listę stron dokumentu w liście po lewej stronie.
	 */
	public void firePageChanged() {
		((MyListModel) leftList.getModel()).fireContentsChanged();
	}
}
