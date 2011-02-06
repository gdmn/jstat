/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.pages.html;

import dmnutils.SwingUtils;
import dmnutils.document.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import jstat.gui.MyAction;
import jstat.pages.notepad.PageNotepad;
import kalkulator.Macierz;

/**
 * Strona z informacjami i wynikami obliczeń, wersja Html.
 * @author dmn
 */
public class PageHtmlOutput extends AbstractDocumentPage<PageNotepad> implements Externalizable, ActionListener {
	private dmnutils.PanelHtmlBuffered panelhtml = new dmnutils.PanelHtmlBuffered();
	private static String css = "<style type=\"text/css\">" + "\n" +
			"/* <![CDATA[ */" + "\n" +
			"* { font-family: Verdana, sans-serif; }" +
			"p { padding:0px; border: 1px dashed #A0A0F3; font-size: 12; background: #E4E4F9; margin: 0px; }" + "\n" +
			"h1 { text-align: center; padding:0px; }" + "\n" +
			//"h6 { color: red }" + "\n" +
			//"h2 { padding: 1px 1px 1px 1px; margin: 1px; color: yellow; text-align: center; line-height: 12 cm; }" + "\n" +
			"#blad { border: 1px dashed #F3A0A0; background: #F9E4E4; }" + "\n" +
			"#naglowektabeli { border: 1px solid #A0A0F3; background: #E4E4F9; background-color: #E4E4F9; font-weight: bold; text-align: center; width: 100%; height: 2em; }" + "\n" +
			"#macierz { background: #FFFFCC; border-collapse: collapse; border: 1px ridge black; }" + "\n" +
			//"table { table-layout: auto; border-collapse: collapse; border: 1px; width: 100%; cellspacing: 1px; }"              +"\n"+
			"table { table-layout: auto; font-size: 12; border-spacing: 0px  5px; width: 100%; }" + "\n" + //border-collapse: separate; }" + "\n" +
			"/* ]]> */" + "\n" +
			"</style>";
	private static boolean showtestcss = false;
	private final String testcss =
			"<h1>elo 1</h1><p>x</p>" +
			"<h2>elo 2</h2><p>x</p>" +
			"<h3>elo 3</h3><p>x</p>" +
			"<h4>elo 4</h4><p>x</p>" +
			"<h5>elo 5</h5><p>x</p>" +
			"<h6>elo 6</h6><p>x</p>" +
			"<h2>aaaaa bbbbb cccccc aaaaa bbbbb cccccc aaaaa bbbbb cccccc aaaaa bbbbb cccccc aaaaa bbbbb cccccc </h2><p>paragraf</p>" +
			"<p><b>line</b></p><p>sss<br />aaaaa</p><hr />" +
			"<p style=\"color: red\">To jest jakiś tekst</p>" +
			"<div id=\"blad\">aaaaaaaaaaaaaaa</div>" +
			"<p title=\"t\">paragraf z title=t</p>" +
			"" +
			//"<table border=\"5\" cellspacing=\"1\">" +
			"<table>" +
			"<tr>" +
			"<td>komórka1</td>	<td>komórka2</td>" +
			"</tr>" +
			"<td colspan=\"2\"><span id=\"naglowektabeli\">komórki1,2 4444444444444444444444444444444444444</span></td>" +
			"<tr>" +
			"<td>komórka3</td>	<td>komórka4</td>" +
			"</tr>" +
			"</table>";

	public PageHtmlOutput() {
		super();
		setCaption("Html");
		constructorHelper();
		clear();
	}

	/**
	 * procedura wywołana w <code>readExternal(ObjectInput in)</code> oraz
	 * w konstruktorze, wspólna ich część jest w tutaj
	 */
	protected void constructorHelper() {
		setLayout(new BorderLayout());
		//setBorder(new EmptyBorder(5, 5, 5, 5));
		setPreferredSize(new Dimension(800, 600));
		add(panelhtml, BorderLayout.CENTER);
		createActions();
		add(createToolBar(), BorderLayout.NORTH);
	}
	private static final String ACTION_EXPORT = MyAction.createNewId();
	private MyAction[] actions;

	private void createActions() {
		actions = new MyAction[]{new MyAction(this, ACTION_EXPORT, "Eksportuj", null, "Eksportuje wyniki obliczeń do pliku HTML", SwingUtils.createNavigationIcon("text-html")),};
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

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeObject(panelhtml);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		panelhtml = (dmnutils.PanelHtmlBuffered) in.readObject();
		constructorHelper();
	}

	/**
	 * Ustawia arkusz stylów dodawany każdorazowo na początku wyjścia
	 * w przypadku wywołania metody <code>clear()</code>.
	 * @param css arkusz do ustawienia.
	 */
	public void setCSS(String css) {
		this.css = css;
	}

	/**
	 * Dodaje tekst do bufora
	 */
	public void printRAW(String line) {
		//panelhtml.print("<p>"+line+"</p>");
		panelhtml.print(line);
	}

	/**
	 * Dodaje paragraf (w znacznikach <p> i </p>).
	 * @param p tekst do dodania
	 */
	public void println(String p) {
		panelhtml.print("<p>" + p + "</p>");
	}

	/**
	 * Dodaje parametry jako komórki wiersza.
	 * @param cells komórki wiersza
	 */
	public void printTableRow(String... cells) {
		panelhtml.print("<tr>");
		if (cells != null) {
			boolean first = true;
			for (String s : cells) {
				if (first) { //align=\"right\"
					panelhtml.print("<td valign=\"top\" >" + s + "</td>");
				} else {
					panelhtml.print("<td>" + s + "</td>");
				}
				first = false;
			}
		}
		panelhtml.print("</tr>");
	}

	/**
	 * Dodaje nagłówek tabeli, opcjonalnie łącząc kilka komórek.
	 * @param head nagłówek
	 * @param colspan ilość komórek do połączenia
	 */
	public void printTableRowHead(String head, int colspan) {
		panelhtml.print("<tr height=\"2\" bgcolor=\"#000000\" >" +
				((colspan > 1) ? "<td colspan=\"" + colspan + "\" >" : "<td>") +
				"</td>" +
				"</tr>");
		panelhtml.print("<tr>" + //height=\"50\" valign=\"bottom\"
				((colspan > 1) ? "<td colspan=\"" + colspan + "\" >" : "<td>") +
				"<span id=\"naglowektabeli\">" + head + "</span></td>" +
				"</tr>");
	}

	/**
	 * Konwertuje macierz do tablicy.
	 * @param m Macierz do przekształcenia
	 * @return Tablica w html, tzn. w znacznikach <code>table</code>.
	 */
	public static String macierzToHtmlTable(Macierz m) {
		String s = m.toString();
		s = s.replaceAll("\\{\\{", "<tr><td>");
		s = s.replaceAll("\\}\\}", "</td></tr>");
		s = s.replaceAll("\\}\\{", "</td></tr><tr><td>");
		s = s.replaceAll(";", "</td><td>");
		s = "<table id=\"macierz\" border=\"1\" rules=\"all\" >" + s + "</table>";
		return s;
	}

	public void clear() {
		panelhtml.clear();
		panelhtml.beginUpdate();
		panelhtml.print(css);
		if (showtestcss) {
			panelhtml.print(testcss);
			printerr("printerr");
		}
		panelhtml.endUpdate();
	}

	/**
	 * Rozpoczyna zapisywanie wiadomości do bufora.
	 */
	public void beginUpdate() {
		panelhtml.beginUpdate();
		changed();
	}

	/**
	 * Kończy zapisywanie wiadomości do bufora i odrysowuje panel.
	 */
	public void endUpdate() {
		panelhtml.endUpdate();
	}

	/**
	 * Wypisuje wiadomość o błędzie
	 */
	public void printerr(String line) {
		printRAW("<p id=\"blad\">" + line + "</p>");
	}
	public static DocumentPageFactory<PageHtmlOutput> factory = new DocumentPageFactory<PageHtmlOutput>() {
		@Override
		public PageHtmlOutput create(Document doc) {
			PageHtmlOutput result;
			result = new PageHtmlOutput();
			return result;
		}
	};

	public static void main(String[] args) {
		//(new jstat.SwingUtils(false, false)).setLookFeel(null);
		SwingUtils.classForImages = jstat.EmptyClass.class;

		System.out.println("" + Locale.getDefault());
		showtestcss = true;
		PageHtmlOutput sheet = PageHtmlOutput.factory.create(null);

		if (sheet != null) {
			dmnutils.SwingUtils.run(sheet);
		} else {
			System.exit(1);
		}
	}

	public void actionPerformed(ActionEvent e) {
		//throw new UnsupportedOperationException("Not supported yet.");
		if (e.getActionCommand().equals(ACTION_EXPORT)) {
			saveDocumentAs();
		}
	}

	/**
	 * Wyświetla dialog do wyboru nazwy pliku i zapisuje aktywny dokument
	 */
	public void saveDocumentAs() {
		final JFileChooser fc = new JFileChooser();
		fc.setMultiSelectionEnabled(false);
		dmnutils.MyFileFilter filter = new dmnutils.MyFileFilter("html", "htm");
		fc.addChoosableFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		int returnVal = fc.showSaveDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			PrintWriter outputStream = null;
			try {
				String fn = filter.setDefaultExtension(fc.getSelectedFile());
				outputStream = new PrintWriter(new FileWriter(fn));
				outputStream.print(panelhtml.getBuffer().toString());
			} catch (IOException ex) {
				Logger.getLogger(PageHtmlOutput.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				outputStream.close();
			}
		}
	}
}
