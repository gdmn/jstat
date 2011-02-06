/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.plugins;

import bsh.*;
import dmnutils.Log;
import dmnutils.WorkingDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import javax.swing.SwingWorker;

/**
 *
 * @author dmn
 */
public class Plugin implements ActionListener {
	public Plugin(String path) throws EvalError, FileNotFoundException, IOException {
		this.path = path;
		interpreter.setOut(Log.getOut());
		interpreter.setErr(Log.getErr());
		String eval_auto;
		eval_auto = jstat.plugins.wrappers.One.get();
		//textOutput.print(eval_auto, ColoredTextPane.ITALIC);
		interpreter.eval(eval_auto);
		//interpreter.eval(jstat.plugins.wrappers.One.get());
		interpreter.set("plugin", this);
		interpreter.set("tools", new jstat.plugins.wrappers.Tools());
		interpreter.source(path);
		int ptype = pluginType();
		if (ptype > 0 && (ptype & 2) > 0) {
			if (pluginInit()) {
				System.out.println("Automatyczne uruchamianie "+getPath());
				pluginStart();
			}
		}
	}

	public Plugin(java.net.URL u) throws EvalError, FileNotFoundException, IOException, Exception {
		this.path = u.getFile();
		interpreter.setOut(Log.getOut());
		interpreter.setErr(Log.getErr());
		String eval_auto;
		eval_auto = jstat.plugins.wrappers.One.get();
		//textOutput.print(eval_auto, ColoredTextPane.ITALIC);
		interpreter.eval(eval_auto);
		interpreter.set("plugin", this);
		interpreter.set("tools", new jstat.plugins.wrappers.Tools());
		interpreter.eval(readURL(u));
		int ptype = pluginType();
		if (ptype > 0 && (ptype & 2) > 0) {
			if (pluginInit()) {
				System.out.println("Automatyczne uruchamianie "+getPath());
				pluginStart();
			}
		}
	}

	private static String readURL(java.net.URL u) throws Exception {
		URLConnection yc = u.openConnection();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
				yc.getInputStream()));
		String inputLine;
		StringBuilder result = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			result.append(inputLine + "\n");
		}
		in.close();
		return result.toString();
	}
	private Interpreter interpreter = new Interpreter();

	/**
	 * Pobiera interpreter bsh
	 */
	public Interpreter getInterpreter() {
		return interpreter;
	}
	private String path;

	/** Zwraca ścieżkę źródłową skryptu */
	public String getPath() {
		return this.path;
	}

	/** Wykonuje instrukcje w interpreterze bsh przypisanym do wtyczki */
	public Object eval(String s) throws EvalError {
		return interpreter.eval(s);
	}

	/** Nazwa pliku bez ścieżki powyżej "plugins/" i bez rozszerzenia. */
	public String getShortFilename() {
		String s = path;
		String shortName = s.substring(s.indexOf(Plugins.plugins) + Plugins.plugins.length());
		shortName = shortName.substring(0, shortName.length() - Plugins.scriptExtension.length());
		return shortName;
	}

	public void actionPerformed(ActionEvent e) {
		new PluginTask().execute();
	}

	/** Nazwa wtyczki */
	public String pluginName() {
		try {
			return (String) eval("getName()");
		} catch (EvalError ex) {
			//Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/** Typ wtyczki:<br />
	 * <ul><li><b>1</b> - powinna się pojawić w menu "Wtyczki"</li>
	 * </ul>
	 * @return typ lub -1 w przypadku, gdy wtyczka nie dostarcza informacji
	 * o swoim typie.
	 */
	public int pluginType() {
		try {
			return (Integer) eval("getType()");
		} catch (EvalError ex) {
			//Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
		}
		return -1;
	}

	/** Wersja wtyczki */
	public int pluginVersion() throws EvalError {
		return (Integer) eval("getVersion()");
	}

	/** Autor wtyczki */
	public String pluginAuthor() throws EvalError {
		return (String) eval("getAuthor()");
	}

	/** Informacje o wtyczce */
	public String pluginInfo() throws EvalError {
		return (String) eval("getInfo()");
	}

	/** Inicjowanie wtyczki, metoda musi zwrócić <code>true</code>,
	inaczej wtyczka nie jest uruchamiana */
	public boolean pluginInit() throws EvalError {
		return (Boolean) eval("init()");
	}

	/** Uruchomienie wtyczki */
	public void pluginStart() throws EvalError {
		eval("start()");
	}

	/** Procedura wywoływana po zakończeniu działania wtyczki */
	public void pluginStop() throws EvalError {
		eval("stop()");
	}

	private class PluginTask extends SwingWorker<Void, Void> {
		@Override
		protected Void doInBackground() throws Exception {
			try {
				if (!pluginInit()) {
					System.err.println("brak gotowości skryptu");
					return null;
				}
			} catch (EvalError ev) {
				ev.printStackTrace();
				System.err.println(ev.getScriptStackTrace());
			}
			WorkingDialog.inc();
			try {
				pluginStart();
			} catch (EvalError ev) {
				ev.printStackTrace();
				System.err.println(ev.getScriptStackTrace());
			} finally {
				WorkingDialog.dec();
				return null;
			}
		}

		@Override
		protected void done() {
			try {
				pluginStop();
			} catch (EvalError ev) {
				ev.printStackTrace();
				System.err.println("" + ev.getScriptStackTrace());
			}
		}
	}
}













