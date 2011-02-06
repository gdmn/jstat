/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.gui;

import dmnutils.plugins.Plugins;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dmn
 */
public class AboutProgram {
	public static void aboutProgram() {
		String[] info = new String[4];
		info[0] = "";
		JApplet applet = Main.getMain().getAppletContext();
		if (applet != null) {
			applet.showStatus("help");
			info[0] = info[0] + "aplikacja uruchomiona jako aplet\n";
			info[0] = info[0] + "  getCodeBase: " + applet.getCodeBase().toString() + "\n";
			info[0] = info[0] + "  getDocumentBase: " + applet.getDocumentBase().toString() + "\n";
			info[0] = info[0] + "  getLocale: " + applet.getLocale().toString() + "\n";
		//s = s +  + "\n";
		} else {
			info[0] = info[0] + "aplikacja uruchomiona w zwykłym trybie\n";
		}
		info[0] = info[0] + "folder ustawień: " + dmnutils.Settings.getSettingsDirectory();
		File f = new File(".");
		try {
			info[0] = info[0] + "\nbieżąca ścieżka: " + f.getCanonicalPath();
		} catch (IOException ex) {
		}
		info[0] = info[0] + "\ngłówna ścieżka programu: " + Main.getMain().getClass().getProtectionDomain().getCodeSource().getLocation().toString();
		info[1] = dmnutils.JavaProperties.getSystemProperties();
		info[2] = dmnutils.JavaProperties.getJVMProperties();
		info[3] = Plugins.getInfoAboutPlugins();
		//JOptionPane.showMessageDialog(null, jstat.Version.NAME_VER + "\n" + jstat.Version.AUTHOR_LONG + "\n\n" + s);
		javax.swing.JDialog help = new javax.swing.JDialog();
		help.setTitle("O programie");
		help.setPreferredSize(new Dimension(500, 300));
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFocusable(false);
		Border emptyborder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		tabbedPane.setBorder(emptyborder);

		JPanel informacje = new JPanel();
		informacje.setLayout(new BoxLayout(informacje, BoxLayout.Y_AXIS));
		{
			//button.setAlignmentX(Component.CENTER_ALIGNMENT);
			JLabel lMail = new JLabel("mail: " + jstat.Version.MAIL);
			JLabel lWww = new JLabel("strona domowa: " + jstat.Version.WWW);
			JLabel lXmpp = new JLabel("jabber: " + jstat.Version.XMPP);
			MouseAdapter podswietlenie = new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					((JLabel) e.getComponent()).setForeground(Color.BLUE);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					((JLabel) e.getComponent()).setForeground(new JLabel().getForeground());
				}
			};
			for (JLabel l : Arrays.asList(lMail, lWww, lXmpp)) {
				l.addMouseListener(podswietlenie);
				l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
			lWww.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					dmnutils.browser.SystemBrowser.browse(jstat.Version.WWW);
				}
			});
			lMail.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					dmnutils.browser.SystemBrowser.mailto(jstat.Version.MAIL);
				}
			});
			lXmpp.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					dmnutils.browser.SystemBrowser.browse("xmpp:" + jstat.Version.XMPP);
				}
			});
			JLabel[] labels = new JLabel[]{
				new JLabel("<html><strong><big>" + jstat.Version.NAME_VER + ""),
				new JLabel("\n\n"),
				new JLabel(jstat.Version.NAME),
				lMail, lXmpp, lWww
			//new JLabel("<html>" + jstat.Version.AUTHOR_LONG.replaceAll("\n", "<br />")),
			};
			for (JLabel l : labels) {
				informacje.add(l);
			}
		}
		informacje.setBorder(emptyborder);
		tabbedPane.addTab("Informacje", informacje);

		for (int i = 0; i < info.length; i++) {
			JTextArea textArea = new JTextArea();
			textArea.setFont(new JTextField().getFont());
			JScrollPane scrollPane = new JScrollPane(textArea);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(scrollPane, BorderLayout.CENTER);
			panel.setBorder(new EmptyBorder(3, 3, 3, 3));
			//scrollPane.setBorder(emptyborder);
			textArea.setEditable(false);
			textArea.setText(info[i]);
			String title = null;
			switch (i) {
				case 0:
					title = "Ścieżki";
					break;
				case 1:
					title = "System";
					break;
				case 2:
					title = "Java";
					break;
				case 3:
					title = "Wtyczki";
					break;
			}
			tabbedPane.addTab(title, panel);
		}

		help.add(tabbedPane, BorderLayout.CENTER);
		JLabel label = new JLabel("" + jstat.Version.NAME_VER);
		help.add(label, BorderLayout.PAGE_END);

		label.setBorder(emptyborder);
		help.pack();
		help.setLocationRelativeTo(null);
		help.setModal(true);
		help.setVisible(true);
	}
}
