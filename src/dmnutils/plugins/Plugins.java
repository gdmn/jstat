/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.plugins;

import bsh.EvalError;
import dmnutils.OSTools;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author dmn
 */
public class Plugins {
	static class Empty {
	}
	/** ciąg znaków "plugins/" z odpowiednim separatorem ścieżki na końcu */
	static final String plugins = "plugins" + OSTools.FILESEPARATOR;
	private static ArrayList<String> directories;
	private static ArrayList<Plugin> scripts;
	/**
	 * Rozszerzenie skryptów (koniecznie małe litery).
	 */
	public static final String scriptExtension = ".bsh";

	private static void searchIn(String d) {
		File f = new File(d);
		try {
			if (f.isDirectory()) {
				for (File e : f.listFiles()) {
					if (e.isDirectory()) {
						searchIn(e.getCanonicalPath());
					} else {
						String fn = e.getCanonicalPath();
						if (fn.toLowerCase().endsWith(scriptExtension)) {
							System.out.println("Wczytywanie " + fn + "...");
							try {
								scripts.add(new Plugin(fn));
							} catch (EvalError ex) {
								//Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
								System.err.println("Błąd EvalError ładowania wtyczki " + fn);
							} catch (FileNotFoundException ex) {
								System.err.println("Błąd FileNotFoundException ładowania wtyczki " + fn);
							} catch (IOException ex) {
								System.err.println("Błąd IOException ładowania wtyczki " + fn);
							}
						}
					}
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void search() {
		directories = new ArrayList<String>(3);
		scripts = new ArrayList<Plugin>(30);
		try {
			//String os = System.getProperty("os.name");
			directories.add(dmnutils.Settings.getSettingsDirectory() + plugins);
			//String s = jstat.gui.Main.getMain().getClass().getProtectionDomain().getCodeSource().getLocation().toString();
			URL u = (new Empty()).getClass().getProtectionDomain().getCodeSource().getLocation();
			if (u.getProtocol().equals("file")) {
				String s = u.toString().substring(5);
				File f = new File(s);
				// pod windowsem dostaje się coś w rodzaju file:/P:/jakis/folder/jstat.jar
				String os = System.getProperty("os.name");
				String separator = System.getProperty("file.separator");
				if (separator.equals("\\")) {
					separator = "\\\\";
				}
				if (os.toLowerCase().indexOf("windows") > -1) {
					s = s.substring(1);
					s = s.replaceAll("/", separator);
					System.out.println("s=" + s);
					//s = f.getParent();
					f = new File(s);
				}
				if (f.isFile()) {
					s = f.getParent();
					s = OSTools.includeTrailingFileSeparator(s) + plugins;
					directories.add(s);
				}
			}
			//directories.add(includeTrailingFileSeparator(new File("..").getCanonicalPath()) );
			directories.add(OSTools.includeTrailingFileSeparator(new File(".").getCanonicalPath()) + plugins);
			//directories.add(System.getProperty(""));


			for (String k : directories) {
				//System.out.println(string);
				System.out.println("Szukanie wtyczek w " + k);
				searchIn(k);
			}

		} catch (IOException ex) {
			Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Tworzy menu wtyczek.
	 * @return menu
	 */
	public static JMenu getJMenu() {
		if (scripts == null) {
			search();
		}
		if (scripts == null) {
			return null;
		}
		JMenu result = new JMenu("Wtyczki");
		JMenuItem menuItem;
		for (Plugin s : scripts) {
			int ptype = s.pluginType();
			if (ptype < 0) {
				System.err.println("Wtyczka " + s.getPath() + " nie dostarcza informacji o swoim typie");
			}
			if ((ptype & 1) > 0 || ptype < 0) {
				String shortName = s.pluginName();
				menuItem = new JMenuItem(shortName);
				menuItem.addActionListener(s);
				result.add(menuItem);
			}
		}
		return result;
	}

	/**
	 * Wczytuje podane wtyczki
	 * @param baseClass klasa służąca do pobrania zasobów (<code>baseClass.getResource(location)</code>)
	 * @param names wtyczki do wczytania
	 */
	public static void loadThesePlugins(Class baseClass, String... names) {
		//String location;
		java.net.URL u;
		for (String location : names) {
			// location = "../projectplugins/first.bsh";
			u = baseClass.getResource(location);
			if (u == null) {
				System.err.println("Script not found: " + location);
			} else {
				System.out.println("Wczytywanie " + u + "...");
				try {
					scripts.add(new Plugin(u));
				} catch (EvalError ex) {
					//Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
					System.err.println("Błąd EvalError ładowania wtyczki " + location);
				} catch (FileNotFoundException ex) {
					System.err.println("Błąd FileNotFoundException ładowania wtyczki " + location);
				} catch (IOException ex) {
					System.err.println("Błąd IOException ładowania wtyczki " + location);
				} catch (Exception ex) {
					Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
	}

	public static String getInfoAboutPlugins() {
		StringBuilder result = new StringBuilder();
		for (Plugin p : scripts) {
			try {
				result.append("Nazwa: " + p.pluginName() + "\n");
				result.append("Ścieżka: " + p.getPath() + "\n");
				result.append("Autor: " + p.pluginAuthor() + "\n");
				result.append("Wersja: " + p.pluginVersion() + "\n");
				StringBuilder s = new StringBuilder();
				int ptype = p.pluginType();
				if (ptype < 0) {
					s.append("nieokreślony");
				} else {
					if ((ptype & 1) > 0) {
						s.append("wpis w menu, ");
					}
					if ((ptype & 2) > 0) {
						s.append("automatyczne uruchomienie, ");
					}
					s.delete(s.length()-2, s.length());
				}
				result.append("Typ: " + s + " (" + ptype + ")\n");
				result.append("Opis: " + p.pluginInfo() + "\n\n");
			} catch (EvalError ex) {
				Logger.getLogger(Plugins.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return result.toString();
	}

	public static void main(String[] args) {
		search();
	}
}







