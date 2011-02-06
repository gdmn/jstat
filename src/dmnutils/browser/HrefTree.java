/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.browser;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.ArrayList;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.*;

/**
 *
 * @author dmn
 */
public class HrefTree extends JPanel {
	protected DefaultMutableTreeNode rootNode;
	protected DefaultTreeModel treeModel;
	protected JTree tree;

	public HrefTree() {
		super(new GridLayout(1, 0));

		rootNode = new DefaultMutableTreeNode("Root Node");
		treeModel = new DefaultTreeModel(rootNode);

		tree = new JTree(treeModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(!true);
		tree.addTreeSelectionListener(new MySelectionListener());

		JScrollPane scrollPane = new JScrollPane(tree);
		add(scrollPane);
	}

	public DefaultMutableTreeNode getRootNode() {
		return rootNode;
	}

	/**
	 * Dodaje gałąź do drzewa
	 * @param parent rodzic, może być null
	 * @param child <code>userObject</code> gałęzi
	 * @return dodana gałąź
	 */
	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
			Object child) {
		DefaultMutableTreeNode childNode =
				new DefaultMutableTreeNode(child);

		if (parent == null) {
			parent = rootNode;
		}

		//It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
		treeModel.insertNodeInto(childNode, parent,
				parent.getChildCount());

		return childNode;
	}

	/**
	 * Reaguje na zaznaczenia gałęzi drzewa
	 */
	class MySelectionListener implements TreeSelectionListener {
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node;
			node = (DefaultMutableTreeNode) (e.getPath().getLastPathComponent());
			Object userObject = node.getUserObject();
			if (userObject instanceof TreeBookmark) {
//				System.out.println("wybrano " + node + " " + ((TreeBookmark) userObject).bookURL + "");
				URL u = ((TreeBookmark) userObject).bookURL;
				notifyListeners(u == null ? null : u.toString());
			}
		}
	}

	/**
	 * Parsuje podany plik i dodaje znalezione nagłówki
	 * (tj. h1, h2, itd.) do drzewa.
	 * @param adres plik html, lokalny lub z internetu
	 */
	public void parse(String adres) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// przetworzenie pliku
			MyHandler handler = new MyHandler(adres);
			SAXParser saxParser = factory.newSAXParser();
			if (adres.indexOf(":/") < 1) {
				//jeśli nie jest adresem internetowym
				saxParser.parse(new File(adres), handler);
			} else {
				//w przypadku gdy http://....
				URL uadres = new URL(adres);
				URLConnection connection = uadres.openConnection();
				saxParser.parse(connection.getInputStream(), handler);
			}
		} catch (SAXParseException spe) {
			System.out.println("** Parsing error" + ", line " + spe.getLineNumber() + ", uri " + spe.getSystemId());
			System.out.println("   " + spe.getMessage());
			Exception x = spe;
			if (spe.getException() != null) {
				x = spe.getException();
			}
			x.printStackTrace();
		} catch (SAXException sxe) {
			Exception x = sxe;
			if (sxe.getException() != null) {
				x = sxe.getException();
			}
			x.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Wczytuje podany plik, wyszukuje wszystkie odnośniki:
	 * <code>href="costam"</code> i parsuje wszystkie
	 * znalezione <code>costam</code>
	 */
	public void parseAllHrefs(String adres) {
		try {
			URL url = new URL(adres);
			rootNode.setUserObject(new TreeBookmark("Spis treści", "h", url));
			URLConnection yc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;
			String adresbezostatniego = adres.substring(0, adres.lastIndexOf("/") + 1);

			while ((inputLine = in.readLine()) != null) {
				int phref = inputLine.toLowerCase().indexOf("href=\"");
				int ptag = inputLine.toLowerCase().indexOf("<a ");
				if (phref > -1 && ptag > -1) {
					//System.out.println(inputLine);
					String s = inputLine.substring(phref + 6);
					int pclose = s.indexOf("\"");
					if (pclose > -1) {
						s = s.substring(0, pclose);
						int pslash = s.indexOf("/");
						int pdots = s.indexOf(":");
						// nie może być <code>mailto:janek@dom.pl</code>, ale może być <code>http://janek.dom.pl</code>
						if (pdots == -1 || pdots + 1 == pslash) {
							// jeśli jest relatywny, to uzupełniamy
							if (pdots == -1) {
								s = adresbezostatniego + s;
							}
							//System.out.println(s);
							parse(s);
						}
					}
				}
			}
			in.close();
		} catch (MalformedURLException ex) {
			Logger.getLogger(HrefTree.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(HrefTree.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Sprawia, że widoczny jest pierwszy poziom dzieci poniżej gałęzi root.
	 */
	public void expandFirstLevel() {
		if (getRootNode().getChildCount() > 0) {
			tree.scrollPathToVisible(new TreePath(((DefaultMutableTreeNode) getRootNode().getFirstChild()).getPath()));
			tree.scrollPathToVisible(new TreePath(( getRootNode()).getPath()));
		}
	}

	/////////////////////////////////////////////////////////////
	// SAX
	private class MyHandler extends DefaultHandler {
		/** ostatnio znaleziony tekst */
		StringBuilder charactersBuffer = new StringBuilder();
		/** ostatnio znaleziony tag z atrybutami */
		String peekLast;
		/** bieżąca gałąź drzewa */
		DefaultMutableTreeNode now = null;
		boolean otwartytagnaglowka = false;
		String adres;

		/**
		 * Czyści bufor
		 */
		private void clearBuffer() {
//			if (hasAnyAttributes || charactersBuffer.length() > 0) {
//				System.out.println(l.toString() +
//						(charactersBuffer.length() > 0 ? " " + charactersBuffer.toString().trim()
//						: ""));
//			}
			charactersBuffer.delete(0, charactersBuffer.length());
		}

		public MyHandler(String adres) {
			this.adres = adres;
		}

		public void setDocumentLocator(Locator l) {
		}

		public void startDocument() throws SAXException {
		}

		public void endDocument() throws SAXException {
		}

		/**
		 * Znaleziony tag rozpoczynający
		 */
		public void startElement(String namespaceURI, String lName,
				String qName, Attributes attrs) throws SAXException {
			clearBuffer();
			String eName = lName;
			if ("".equals(eName)) {
				eName = qName;
			}
			if (attrs != null) {
				//jeśli są atrybuty, również je umieszczamy w zmiennej l
				LinkedList<String> attributes = new LinkedList();
				for (int i = 0; i < attrs.getLength(); i++) {
					String aName = attrs.getLocalName(i);
					if ("".equals(aName)) {
						aName = attrs.getQName(i);
					}
					attributes.add(aName + "=" + attrs.getValue(i));
				}
				if (attributes.size() > 0) {
					peekLast = (eName + " " + attributes.toString());
				} else {
					peekLast = (eName);
				}
			} else {
				peekLast = (eName);
			}


			// jeśli jest h1, h2, h3, h4, h5, h6
			if ((eName.length() == 2 && eName.indexOf("h") == 0 && "123456".indexOf(eName.substring(1, 2)) > -1)) {
				//System.out.println("znaleziono nagłówek " + eName);
				otwartytagnaglowka = true;
				if (now == null) {
					//System.out.println("poprzedni nagłówek to null");
				} else {
					String poprzednitag = ((TreeBookmark) now.getUserObject()).tag;
					//System.out.println("poprzedni nagłówek to " + poprzednitag);
					// jeśli poprzedni był wyższy lub równy (tzn. np. było h2, a znaleziono h1), to trzeba wskoczyć na poziom wyżej w hierarchii
					if (Integer.parseInt(eName.substring(1, 2)) <= Integer.parseInt(((TreeBookmark) now.getUserObject()).tag.substring(1, 2))) {
						int roznica = Integer.parseInt(((TreeBookmark) now.getUserObject()).tag.substring(1, 2)) - Integer.parseInt(eName.substring(1, 2));
						//System.out.println("poprzedni nagłówek jest wyższego lub takiego samego poziomu jak znaleziony, różnica " + roznica);
						if (roznica == 0) {
							// ten sam poziom
							now = (DefaultMutableTreeNode) now.getParent();
						} else if (eName.toLowerCase().equals("h1")) {
							// poziom "najgłówniejszy"
							now = null;
						} else {
							// cofamy się, aż rodzic będzie większy od dziecka
							int teraz = Integer.parseInt(eName.substring(1, 2));
							int rodzic;
							do {
								now = (DefaultMutableTreeNode) now.getParent();
								rodzic = Integer.parseInt(((TreeBookmark) now.getUserObject()).tag.substring(1, 2));
							} while (now != null &&
									rodzic>=teraz
									);
						}
					}
				}
				DefaultMutableTreeNode newnode = addObject(now, new TreeBookmark(eName, eName, (URL) null)); // url zostanie dodany później, w <code>endElement</code>

				now = newnode;
			}
		}

		/**
		 * Znaleziony tag zamykający
		 */
		public void endElement(String namespaceURI, String sName, String qName)
				throws SAXException {
			// jeśli jest h1, h2, h3, h4, h5, h6
			if ((qName.length() == 2 && qName.indexOf("h") == 0 && "123456".indexOf(qName.substring(1, 2)) > -1)) {
				otwartytagnaglowka = false;
				//System.out.println("znaleziono zamykający tag " + qName + ", bufor tekstu: " + charactersBuffer.toString().trim());
				// znaleziono tekst odpowiadający za nagłówek, trzeba go ustawić
				TreeBookmark book = ((TreeBookmark) now.getUserObject());
				if (book.bookURL == null) {
					book.bookName = "? " + charactersBuffer.toString().trim();
				} else {
					book.bookName = charactersBuffer.toString().trim();
				}
//				System.out.println();
			}
			// znaleziono tag "a" w otwartym nagłówku. analiza i ustawienie odnośnika:
			if (otwartytagnaglowka && qName.equals("a")) {
				String ostatni = peekLast;
//				System.out.println("otwartytagnaglowka && znaleziono zamykający tag " + qName + ", ostatni element: " + ostatni);
				if (ostatni.toLowerCase().indexOf("a [name=") == 0) {
					String href = ostatni.substring(8, ostatni.length() - 1);
					href = adres + "#" + href;
//					System.out.println("href=" + href);
					try {
						((TreeBookmark) now.getUserObject()).bookURL = new URL(href); // ustawienie odnośnika

					} catch (MalformedURLException ex) {
						Logger.getLogger(HrefTree.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			clearBuffer();
		}

		/**
		 * Tekst między tagami
		 */
		public void characters(char buf[], int offset, int len)
				throws SAXException {
			String s = new String(buf, offset, len);
			if (!s.trim().equals("")) {
				String k = peekLast;
				//k = k.substring(1, k.length() - 1);
				charactersBuffer.append(s);
			}
		}

		public void error(SAXParseException e)
				throws SAXParseException {
			throw e;
		}

		public void warning(SAXParseException err)
				throws SAXParseException {
			System.out.println("** Warning" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			System.out.println("   " + err.getMessage());
		}
	};
	// SAX - koniec
	/////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// TreeBookmark
	/**
	 * Liść drzewka, zawiera nagłówek, odnośnik do pliku,
	 * dla celów przetwarzania również tag (h1, h2, itd.)
	 */
	private class TreeBookmark {
		public String bookName;
		public URL bookURL;
		public String tag;

		public TreeBookmark(String caption, String tag, URL bookURL) {
			bookName = caption;
			this.bookURL = bookURL;
			this.tag = tag;
//			System.out.println("dodano " + caption + " - " + bookURL);
		}

		public String toString() {
			return bookName;
		}
	}
	// TreeBookmark - koniec
	/////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////
	// Listenery, TIJ4, s. 1148
	private ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

	public synchronized void addActionListener(ActionListener l) {
		actionListeners.add(l);
	}

	public synchronized void removeActionListener(ActionListener l) {
		actionListeners.remove(l);
	}

	public void notifyListeners(String command) {
		ActionEvent a = new ActionEvent(this, 0, command);
		ArrayList<ActionListener> lv = null;
		synchronized (this) {
			lv = new ArrayList<ActionListener>(actionListeners);
		}
		for (ActionListener actionListener : lv) {
			actionListener.actionPerformed(a);
		}
	}
	// Listenery - koniec
	/////////////////////////////////////////////////////////////

	public static void main(String argv[]) throws FileNotFoundException {
		JFrame frame = new JFrame();
		//frame.getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel(new BorderLayout());
		frame.setContentPane(panel);


		HrefTree htree = new HrefTree();
		htree.parse(jstat.gui.Help.getAddr("testtagow.html").toString());

		JScrollPane treeView = new JScrollPane(htree);
		panel.add(treeView);
		panel.setMinimumSize(new Dimension(200, 400));
		panel.setPreferredSize(panel.getMinimumSize());

		frame.pack();
		htree.expandFirstLevel();
		dmnutils.SwingUtils.run(frame);
	}
}

