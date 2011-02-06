/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.regresja;

import dmnutils.WorkingDialog;
import java.beans.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import jstat.obliczenia.podstawowestatystyki.zbiorowosci.*;
import dmnutils.document.*;
import dmnutils.wizard.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import jstat.gui.MyInternalFrame;
import jstat.obliczenia.*;
import jstat.obliczenia.steps.*;
import jstat.pages.html.PageHtmlOutput;
import jstat.pages.sheet.PageSheet;
import kalkulator.*;
import walidacja.Walidator;

/**
 *
 * @author dmn
 */
public class DocumentPageFactory_Regresja implements DocumentPageFactory<PageNotepadForObliczenia> {
	/**
	 * Sprawdza, czy zaznaczono odpowiednią stronę w dokumencie typu
	 * <code>PageSheet</code> i przekazuje sterowanie do <code>create2(Document doc)</code>
	 * @param doc
	 * @return
	 */
	public PageNotepadForObliczenia create(Document doc) {
		PageNotepadForObliczenia result;
		PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
		result = ps == null ? null : (PageNotepadForObliczenia) create2(doc);
		return result;
	}

	public AbstractDocumentPage<?> create2(final Document doc) {
		/** kolumna ze zmienną xi */
		//final int KOL_XI = 0;
		//AbstractDocumentPage<?> result = null;
		final WizardStepPanel[] steps = new WizardStepPanel[4];
		WizardStepPanel step = null;

		steps[0] = new WizardStepPanel_ZmienneComboBoxes(doc, "y (objaśniana)");
		steps[0].setTitle("Wybierz \"y\"");

		steps[1] = new WizardStepPanel_ZmienneCheckBoxes(doc);
		steps[1].setTitle("Wybierz objaśniające (\"xi\")");

		steps[1].addFocusListener(new FocusAdapter() {
			/** unieaktywnia JCheckBox dla zmiennej, która została wybrana w poprzednim kroku */
			public void focusGained(FocusEvent e) {
				WizardStepPanel_ZmienneComboBoxes prev = (WizardStepPanel_ZmienneComboBoxes) steps[0];
				WizardStepPanel_ZmienneCheckBoxes now = (WizardStepPanel_ZmienneCheckBoxes) steps[1];
				Integer[] map = (prev).getOdwzorowanie();
				for (int i = 0; i < now.getCheckboxes().size(); i++) {
					JCheckBox j = now.getCheckboxes().get(i);
					if (map == null || map.length < 1) {
						j.setEnabled(true);
					} else {
						for (int k = 0; k < map.length; k++) {
							if (map[k] != i) {
								j.setEnabled(true);
							} else {
								j.setEnabled(false);
								j.setSelected(false);
								break;
							}
						}
					}
				}
			}
		});
		steps[2] = new MyWizardStepPanel3(doc);

		steps[3] = new MyWizardStepPanel4(doc);
		steps[3].addFocusListener(new FocusAdapter() {
			/** wymagany rozmiar wektora parametrów */
			@Override
			public void focusGained(FocusEvent e) {
				WizardStepPanel_ZmienneCheckBoxes now = (WizardStepPanel_ZmienneCheckBoxes) steps[1];
				((MyWizardStepPanel4) steps[3]).updateDefaultKombinacjaLiniowa(now.getNazwyZmiennych());
			}
		});

		ArrayList<WizardStepPanel> arrayOfSteps = new ArrayList<WizardStepPanel>(Arrays.asList(steps));
		WizardMainPanel wizard = new WizardMainPanel(arrayOfSteps);
		WizardDialog dlg = new WizardDialog(null, "Regresja", wizard);
		if (dlg.execute().equals(WizardMainPanel.ACTION_OK)) {
			Task t = new Task((PageSheet) MyInternalFrame.checkActiveDocumentPage(false, null, doc, PageSheet.class), (WizardStepPanel_ZmienneComboBoxes) steps[0], (WizardStepPanel_ZmienneCheckBoxes) steps[1], (MyWizardStepPanel3) steps[2], (MyWizardStepPanel4) steps[3]);
			t.execute();
			return null;
		} else {
			return null;
		}
	}

	class TylkoLiczby implements DocumentListener {
		Liczba min, max;
		boolean ulamki;
		JTextField field;
		Color bgColor;
		WizardStepPanel step;

		public TylkoLiczby(WizardStepPanel step, JTextField field, Liczba min, Liczba max, boolean ulamki) {
			this.step = step;
			this.min = min;
			this.max = max;
			this.ulamki = ulamki;
			this.field = field;
			bgColor = field.getBackground();
		}

		public TylkoLiczby(WizardStepPanel step, JTextField field) {
			this(step, field, null, null, true);
		}

		private void zleDane() {
			step.setForwardEnabled(false);
			field.setBackground(Color.RED);
		}

		public void insertUpdate(DocumentEvent e) {
			String s = null;
			try {
				s = e.getDocument().getText(0, e.getDocument().getLength());
			} catch (BadLocationException ex) {
				zleDane();
				return;
			//Logger.getLogger(DocumentPageFactory_Regresja.class.getName()).log(Level.SEVERE, null, ex);
			}
			Liczba r = null;
			try {
				r = new Liczba(s);
			} catch (NumberFormatException ex) {
				zleDane();
				return;
			}
			if (max != null && max.compareTo(r) < 0) {
				zleDane();
				return;
			}
			if (min != null && min.compareTo(r) > 0) {
				zleDane();
				return;
			}
			if (!ulamki && r.isFraction()) {
				zleDane();
				return;
			}
			step.setForwardEnabled(true);
			field.setBackground(bgColor);
		//System.out.println("insertUpdate " + e.getType());
		}

		public void removeUpdate(DocumentEvent e) {
			insertUpdate(e);
		}

		public void changedUpdate(DocumentEvent e) {
			insertUpdate(e);
		}
	}

	private class MyWizardStepPanel4 extends WizardStepPanel {
		private Document doc;
		private JTextField[] textFields = new JTextField[3];
		Integer ilosczmiennych = null;
		private JLabel wektor = new JLabel();

		public MyWizardStepPanel4(Document doc) {
			super("Test kombinacji liniowej wektora parametrów");
			this.doc = doc;
			setLayout(new BorderLayout());
			add(utworzStrone(), BorderLayout.CENTER);
		}

		public void updateDefaultKombinacjaLiniowa(Collection odwzorowanie) {
			ilosczmiennych = odwzorowanie.size() + 1;
//            ArrayList<Integer> a = new ArrayList<Integer>(odwzorowanie.length);
//            for (int i : odwzorowanie) {
//                a.add(i);
//            }
			//System.out.println("odwzorowanie: "+a.toString());
			LinkedList n = new LinkedList(odwzorowanie);
			n.add(new String("wolny"));
			wektor.setText(n.toString());
			//if (textFields[1].getText().equals("")) {
			StringBuilder result = new StringBuilder(4 + 2 * n.size() + 15);
			result.append("{{");
			for (int i = 0; i < n.size(); i++) {
				if (i > 0) {
					result.append("; ");
				}
				result.append("1");
			}
			result.append("}}");
			textFields[2].setText(result.toString());
		//}
		}

		public JPanel utworzStrone() {
			JPanel result = new JPanel(new SpringLayout());
			PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
			JComponent prev = null;
			JLabel label;
			JTextField field;
			label = new JLabel();
			field = new JTextField();
			textFields[0] = field;
			field.setText("0,05");
			label.setText("Poziom istotności:");
			field.getDocument().addDocumentListener(new TylkoLiczby(this, field, new Liczba("0.00001"), new Liczba("0.5"), true));
			result.add(label);
			result.add(field);
			dmnutils.SwingUtils.springSetAsRow(prev, 5, label, 0, field, 100);
			prev = field;

			label = new JLabel();
			field = new JTextField();
			textFields[1] = field;
			field.setText("0,0");
			label.setText("Wyraz wolny:");
			field.getDocument().addDocumentListener(new TylkoLiczby(this, field, null, null, true));
			result.add(label);
			result.add(field);
			dmnutils.SwingUtils.springSetAsRow(prev, 5, label, 0, field, 100);
			prev = field;

			label = new JLabel();
			label.setText("Wybrane zmienne:");
			result.add(label);
			result.add(wektor);
			dmnutils.SwingUtils.springSetAsRow(prev, 15, label, 150, wektor, 0);
			prev = label;

			label = new JLabel();
			field = new JTextField();
			textFields[2] = field;
			field.setText("");
			label.setText("Kombinacja liniowa:");
			field.getDocument().addDocumentListener(new TylkoWektory(this, field, ilosczmiennych));
			result.add(label);
			result.add(field);
			dmnutils.SwingUtils.springSetAsRow(prev, 5, label, 150, field, 0);
			prev = field;

			return result;
		}

		class TylkoWektory implements DocumentListener {
			Integer wymiar;
			JTextField field;
			Color bgColor;
			WizardStepPanel step;

			public TylkoWektory(WizardStepPanel step, JTextField field, Integer wymiar) {
				this.step = step;
				this.wymiar = wymiar;
				this.field = field;
				bgColor = field.getBackground();
			}

			public TylkoWektory(WizardStepPanel step, JTextField field) {
				this(step, field, null);
			}

			private void zleDane() {
				step.setForwardEnabled(false);
				field.setBackground(Color.RED);
			}

			public void insertUpdate(DocumentEvent e) {
				String s = null;
				try {
					s = e.getDocument().getText(0, e.getDocument().getLength());
				} catch (BadLocationException ex) {
					zleDane();
					return;
				}
				try {
					int[] wymiar_s = Walidator.wymiarMacierzyStr(s);
//                    System.out.println("wymiar = " + ilosczmiennych + ", jest = " + wymiar_s[0] + "x" + wymiar_s[1]);
					if (wymiar_s[1] != 1 || wymiar_s[0] != ilosczmiennych) {
						zleDane();
					} else {
						Macierz temp = Macierz.valueOf(s, null);
						step.setForwardEnabled(true);
						field.setBackground(bgColor);
					}
				} catch (Exception exc) {
					zleDane();
				}
			//System.out.println("insertUpdate " + e.getType());
			}

			public void removeUpdate(DocumentEvent e) {
				insertUpdate(e);
			}

			public void changedUpdate(DocumentEvent e) {
				insertUpdate(e);
			}
		}

		public double getAlfa() {
			return (new Liczba(textFields[0].getText())).getValueAsDouble();
		}

		public Liczba getWolny() {
			return new Liczba(textFields[1].getText());
		}

		public Macierz getKombinacja() {
			return Macierz.valueOf(textFields[2].getText(), null);
		}
	}

	public static enum Testy {
		WSPKORELACJI_ALFA, DOBORMETWSPKOR_ALFA, ISTOTNOSC_ALFA, ISTOTNOSC2_ALFA
	}

	private class MyWizardStepPanel3 extends WizardStepPanel {
		private Document doc;
		private JTextField[] textFields = new JTextField[Testy.values().length];

		public MyWizardStepPanel3(Document doc) {
			super("Testy");
			this.doc = doc;
			setLayout(new BorderLayout());
			add(utworzStrone(), BorderLayout.CENTER);
		}

		public JPanel utworzStrone() {
			JPanel result = new JPanel(new SpringLayout());
			PageSheet ps = (PageSheet) MyInternalFrame.checkActiveDocumentPage(true, null, doc, PageSheet.class);
			JComponent prev = null;
			for (Testy t : Testy.values()) {
				JLabel label = new JLabel();
				JTextField field = new JTextField();
				textFields[t.ordinal()] = field;
				String l;
				String f;
				switch (t) {
					case WSPKORELACJI_ALFA:
						f = "0,05";
						l = "Test współczynnika korelacja - alfa:";
						field.getDocument().addDocumentListener(new TylkoLiczby(this, field, new Liczba("0.00001"), new Liczba("0.5"), true));
						break;
					case DOBORMETWSPKOR_ALFA:
						f = "0,05";
						l = "Dobór zmiennych objaśniających do modelu metodą analizy współczynników korelacji  - alfa:";
						field.getDocument().addDocumentListener(new TylkoLiczby(this, field, new Liczba("0.00001"), new Liczba("0.5"), true));
						break;
					case ISTOTNOSC_ALFA:
						f = "0,05";
						l = "Istotność parametrów strukturalnych - alfa:";
						field.getDocument().addDocumentListener(new TylkoLiczby(this, field, new Liczba("0.00001"), new Liczba("0.5"), true));
						break;
					case ISTOTNOSC2_ALFA:
						f = "0,05";
						l = "Istotność poszczególnych parametrów strukturalnych - alfa:";
						field.getDocument().addDocumentListener(new TylkoLiczby(this, field, new Liczba("0.00001"), new Liczba("0.5"), true));
						break;
					default:
						f = "0,00";
						l = "nieznany test";
						field.getDocument().addDocumentListener(new TylkoLiczby(this, field));
				}
				label.setText(l);
				field.setText(f);
				result.add(label);
				result.add(field);
				dmnutils.SwingUtils.springSetAsRow(prev, 5, label, 0, field, 100);
				prev = field;
			}
			return result;
		}

		public double getParametrTestu(Testy t) {
			//return Double.parseDouble(textFields[t.ordinal()].getText());
			return (new Liczba(textFields[t.ordinal()].getText())).getValueAsDouble();
		}
	}

	private class Task extends SwingWorker<PageNotepadForObliczenia, Void> {
		PageSheet ps;
		WizardStepPanel_ZmienneComboBoxes step0;
		WizardStepPanel_ZmienneCheckBoxes step1;
		MyWizardStepPanel3 step2;
		MyWizardStepPanel4 step3;
		PageNotepadForObliczenia results;

		public Task(PageSheet ps, WizardStepPanel_ZmienneComboBoxes step0, WizardStepPanel_ZmienneCheckBoxes step1, MyWizardStepPanel3 step2, MyWizardStepPanel4 step3) {
			this.ps = ps;
			this.step0 = step0;
			this.step1 = step1;
			this.step2 = step2;
			this.step3 = step3;
		}

		@Override
		protected void done() {
//            System.out.println("done");
			jstat.gui.Main.getMain().insertPage(new DocumentPageFactory<PageNotepadForObliczenia>() {
				public PageNotepadForObliczenia create(Document doc) {
					try {
						return get();
					} catch (InterruptedException ex) {
						Logger.getLogger(DocumentPageFactory_Regresja.class.getName()).log(Level.SEVERE, null, ex);
					} catch (ExecutionException ex) {
						Logger.getLogger(DocumentPageFactory_Regresja.class.getName()).log(Level.SEVERE, null, ex);
					}
					return null;
				}
			});
		}

		@Override
		protected PageNotepadForObliczenia doInBackground() {
			WorkingDialog.inc("Obliczanie regresji...");
			try {
				//try { Thread.sleep(5000);} catch (InterruptedException e) {}
				Macierz m = ps.getDataAsMacierz();
				results = new PageNotepadForObliczenia();
				results.setCaption("Regresja");
				PageNotepadForObliczenia result2 = results;
				result2.beginUpdate();
				result2.printRAW("<table>");
				try {
					WorkingDialog.setTempProgressText("Pobieranie danych");
					Integer[] map = (step0).getOdwzorowanie();
					Macierz macierz_x = null,
							macierz_y = null,
							macierz_wszystko = null;

					macierz_x = (step1).getMacierz(m);
					macierz_y = Macierz.pobierzKolumny(m, map[0]);
					{
						Integer[] mapy = (step0).getOdwzorowanie();
						int[] mapx = (step1).getWybrane();
						int[] mapwszystkie = new int[mapx.length + 1];
						for (int i = 0; i < mapx.length; i++) {
							mapwszystkie[i + 1] = mapx[i];
						}
						mapwszystkie[0] = mapy[0];
						macierz_wszystko = Macierz.pobierzKolumny(m, mapwszystkie);
					}
					WorkingDialog.setTempProgressText("Korelacja");
					result2.printTableRowHead("Korelacja", 2);
					Macierz korelacja = kalkulator.Regresja.macierzKorelacji(macierz_x);
					result2.printTableRow("macierz korelacji", PageHtmlOutput.macierzToHtmlTable(korelacja) + "");
					Macierz wkorelacji = kalkulator.Regresja.wektorKorelacji(macierz_y, macierz_x);
					result2.printTableRow("wektor korelacji", PageHtmlOutput.macierzToHtmlTable(wkorelacji) + "");
					Macierz rkorelacji = kalkulator.Regresja.rozszerzonaMacierzKorelacji(macierz_y, macierz_x);
					result2.printTableRow("rozszerzona macierz korelacji", PageHtmlOutput.macierzToHtmlTable(rkorelacji) + "");
					Liczba wspolczynnikKorelacjiWielorakiej = kalkulator.Regresja.wspolczynnikKorelacjiWielorakiej(korelacja, rkorelacji);
					result2.printTableRow("współczynnik korelacji wielorakiej", wspolczynnikKorelacjiWielorakiej + "");

					WorkingDialog.setTempProgressText("Istotność współczynnika korelacji");
					result2.printTableRowHead("Istotność współczynnika korelacji", 2);
					Macierz testWspolczynnikaKorelacji_sprawdzianHipotezy = kalkulator.Regresja.testWspolczynnikaKorelacji_sprawdzianHipotezy(rkorelacji, macierz_x.getLengthY());
					result2.printTableRow("sprawdzian", PageHtmlOutput.macierzToHtmlTable(testWspolczynnikaKorelacji_sprawdzianHipotezy) + "");
					double testWspolczynnikaKorelacji_alfa = step2.getParametrTestu(Testy.WSPKORELACJI_ALFA);
					result2.printTableRow("poziom istotności", new Liczba(testWspolczynnikaKorelacji_alfa).toString());
					Liczba testWspolczynnikaKorelacji_krytyczna = kalkulator.Regresja.testWspolczynnikaKorelacji_wartoscKrytyczna(macierz_x.getLengthY(), testWspolczynnikaKorelacji_alfa);
					result2.printTableRow("wartość krytyczna", testWspolczynnikaKorelacji_krytyczna.toString());
					Macierz testWspolczynnikaKorelacji_test = kalkulator.Regresja.testWspolczynnikaKorelacji_testuj(testWspolczynnikaKorelacji_sprawdzianHipotezy, testWspolczynnikaKorelacji_krytyczna);
					result2.printTableRow("wynik testu", PageHtmlOutput.macierzToHtmlTable(testWspolczynnikaKorelacji_test));

					WorkingDialog.setTempProgressText("Dobór zmiennych do modelu");
					result2.printTableRowHead("Dobór zmiennych metodą analizy współczynników korelacji", 2);
					double dobor_alfa = step2.getParametrTestu(Testy.DOBORMETWSPKOR_ALFA);
					result2.printTableRow("poziom istotności", new Liczba(dobor_alfa).toString());
					Liczba dobor_krytyczna = kalkulator.Regresja.doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_wartoscKrytyczna(macierz_x.getLengthY(), dobor_alfa);
					result2.printTableRow("wartość krytyczna", dobor_krytyczna.toString());
					Macierz dobor_zmienne = kalkulator.Regresja.doborZmiennychMetodaAnalizyWspolczynnikowKorelacji_dobierz(rkorelacji, dobor_krytyczna);
					if (dobor_zmienne == null) {
						result2.printTableRow("wynik testu", "brak zmiennych nadających się do modelu");
					} else {
						String zm;
						{
							StringBuilder b = new StringBuilder();
							int[] mapx = (step1).getWybrane();
							for (int i = 0; i < mapx.length; i++) {
								if (Macierz.znajdzWartosc(dobor_zmienne, new Liczba(i)) != null) {
									if (!b.toString().equals("")) {
										b.append(", ");
									}
									int j = mapx[i];
									b.append(ps.getColumnNames()[j]);
								}
							}
							zm = b.toString();
						}
						result2.printTableRow("wynik testu", zm);
					}
					result2.printTableRowHead("Dobór zmiennych metodą wskaźników pojemności informacji", 2);
					Macierz h = kalkulator.Regresja.doborZmiennychMetodaWskaznikowPojemnosciInformacji_dobierz(rkorelacji);
					if (h == null) {
						result2.printTableRow("wynik testu", "brak zmiennych nadających się do modelu");
					} else {
						String zm;
						{
							StringBuilder b = new StringBuilder();
							int[] mapx = (step1).getWybrane();
							for (int i = 0; i < mapx.length; i++) {
								if (Macierz.znajdzWartosc(h, new Liczba(i)) != null) {
									if (!b.toString().equals("")) {
										b.append(", ");
									}
									int j = mapx[i];
									b.append(ps.getColumnNames()[j]);
								}
							}
							zm = b.toString();
						}
						result2.printTableRow("wynik testu", zm);
					}

					WorkingDialog.setTempProgressText("Współczynniki");
					result2.printTableRowHead("Współczynniki", 2);
					Macierz parametry = kalkulator.Regresja.parametryAlfa(macierz_y, macierz_x, true);
					result2.printTableRow("parametry alfa", parametry + "");
					Macierz macierz_x_1 = kalkulator.Regresja.dodajWyrazWolny(macierz_x);
					//result2.printTableRow("macierz_x_1", macierz_x_1 + "");
					Macierz macierz_teoretyczne = kalkulator.Regresja.obliczTeoretyczne(macierz_x_1, parametry);
					result2.printTableRow("teoretyczne", PageHtmlOutput.macierzToHtmlTable(macierz_teoretyczne) + "");
					Macierz reszty = (Macierz) Znak.odejmijElementy(macierz_y, macierz_teoretyczne);
					result2.printTableRow("reszty", PageHtmlOutput.macierzToHtmlTable(reszty) + "");
					Macierz kwadratsumreszt = kalkulator.Regresja.sumaKwadratow(reszty);
					result2.printTableRow("suma kwadratow reszt", (kwadratsumreszt.getValueAt(0, 0)) + "");
					Liczba wariancjaLosowego = kalkulator.Regresja.wariancjaSkladnikaLosowego(macierz_y, macierz_teoretyczne, macierz_x.getLengthX());
					result2.printTableRow("wariancja składnika losowego", wariancjaLosowego + "");
					Macierz macierz_wariancji_kowariancji = kalkulator.Regresja.macierzWariancjiKowariancji(wariancjaLosowego, macierz_x_1);
					result2.printTableRow("macierz wariancji i kowariancji", PageHtmlOutput.macierzToHtmlTable(macierz_wariancji_kowariancji) + "");
					Macierz standardowe_bledy_ocen = kalkulator.Regresja.standardoweBledyOcen(macierz_wariancji_kowariancji);
					result2.printTableRow("standardowe błędy ocen parametrów", PageHtmlOutput.macierzToHtmlTable(standardowe_bledy_ocen) + "");

					WorkingDialog.setTempProgressText("Model");
					String model;
					{
						StringBuilder b = new StringBuilder();
						b.append(ps.getColumnNames()[map[0]]);
						b.append(" = ");
						int[] mapx = (step1).getWybrane();
						for (int i = 0; i < mapx.length; i++) {
							int j = mapx[i];
							b.append(parametry.getValueAt(0, i).toString());
							b.append("*");
							b.append(ps.getColumnNames()[j]);
							if (i < mapx.length - 1) {
								b.append(" + ");
							}
						}
						if (parametry.getLengthY() > mapx.length) {
							b.append(" + ");
							b.append(parametry.getValueAt(0, mapx.length));
						}
						model = b.toString();
					}
					result2.printTableRow("model", model);

					WorkingDialog.setTempProgressText("Test Durbina-Watsona");
					result2.printTableRowHead("Test Durbina-Watsona", 2);
					Liczba dw = kalkulator.Regresja.testDurbinaWatsona_statystyka(reszty);
					result2.printTableRow("statystyka", dw + "");

					WorkingDialog.setTempProgressText("Istotność parametrów strukturalnych");
					result2.printTableRowHead("Istotność parametrów strukturalnych", 2);
					Liczba wspolczynnikDeterminacji = kalkulator.Regresja.wspolczynnikDeterminacjiR2(macierz_y, macierz_teoretyczne);
					result2.printTableRow("R^2", wspolczynnikDeterminacji + "");
					Liczba istotnosc_statystyka = kalkulator.Regresja.istotnoscParametrowStrukturalnych_statystka(macierz_x.getLengthY(), (step1).getWybrane().length, wspolczynnikDeterminacji);
					result2.printTableRow("statystyka", istotnosc_statystyka + "");
					double istotnosc_alfa = step2.getParametrTestu(Testy.ISTOTNOSC_ALFA);
					result2.printTableRow("poziom istotności", new Liczba(istotnosc_alfa).toString());
					Liczba istotnosc_krytyczna = kalkulator.Regresja.istotnoscParametrowStrukturalnych_wartoscKrytyczna(macierz_x.getLengthY(), (step1).getWybrane().length, istotnosc_alfa);
					result2.printTableRow("wartość krytyczna", istotnosc_krytyczna + "");
					result2.printTableRow("istotne?", kalkulator.Regresja.istotnoscParametrowStrukturalnych_testuj(istotnosc_statystyka, istotnosc_krytyczna) + "");

					WorkingDialog.setTempProgressText("Istotność poszczególnych parametrów strukturalnych");
					result2.printTableRowHead("Istotność poszczególnych parametrów strukturalnych", 2);
					Macierz istotnoscposzczegolnych_statystyka = kalkulator.Regresja.istotnoscPoszczegolnychParametrowStrukturalnych_statystyka(parametry, standardowe_bledy_ocen);
					result2.printTableRow("statystyka", PageHtmlOutput.macierzToHtmlTable(istotnoscposzczegolnych_statystyka) + "");
					double istotnoscposzczegolnych_alfa = step2.getParametrTestu(Testy.ISTOTNOSC2_ALFA);
					result2.printTableRow("poziom istotności", new Liczba(istotnoscposzczegolnych_alfa).toString());
					Liczba istotnoscposzczegolnych_krytyczna = kalkulator.Regresja.istotnoscPoszczegolnychParametrowStrukturalnych_wartoscKrytyczna(macierz_x.getLengthY(), (step1).getWybrane().length, istotnoscposzczegolnych_alfa);
					result2.printTableRow("wartość krytyczna", istotnoscposzczegolnych_krytyczna + "");
					result2.printTableRow("istotne?", kalkulator.Regresja.istotnoscPoszczegolnychParametrowStrukturalnych_testuj(istotnoscposzczegolnych_statystyka, istotnoscposzczegolnych_krytyczna) + "");

					WorkingDialog.setTempProgressText("Istotność wektora parametrów strukturalnych");
					result2.printTableRowHead("Istotność wektora parametrów strukturalnych", 2);
					Liczba istotnoscwektora_statystyka = kalkulator.Regresja.istotnoscWektoraParametrow_statystyka(Macierz.transpozycja(step3.getKombinacja()), step3.getWolny(), macierz_wariancji_kowariancji, parametry);
					result2.printTableRow("w0", step3.getWolny() + "");
					result2.printTableRow("kombinacja liniowa", step3.getKombinacja() + "");
					result2.printTableRow("statystyka", istotnoscwektora_statystyka + "");
					double istotnoscwektora_alfa = step2.getParametrTestu(Testy.ISTOTNOSC2_ALFA);
					result2.printTableRow("poziom istotności", new Liczba(istotnoscwektora_alfa).toString());
					Liczba istotnoscwektora_krytyczna = kalkulator.Regresja.istotnoscWektoraParametrow_wartoscKrytyczna(macierz_x.getLengthY(), (step1).getWybrane().length, istotnoscwektora_alfa);
					result2.printTableRow("wartość krytyczna", istotnoscwektora_krytyczna + "");
					result2.printTableRow("istotnie różne od w0?", kalkulator.Regresja.istotnoscWektoraParametrow_testuj(istotnoscwektora_statystyka, istotnoscwektora_krytyczna) + "");


					WorkingDialog.setTempProgressText(null);
				} catch (Exception e) {
					result2.printTableRow("błąd", e.toString());
					e.printStackTrace();
				} finally {
					result2.printRAW("</table>");
					result2.endUpdate();
					System.out.println("end of doinbackground");
				}
			} finally {
				WorkingDialog.dec();
				return results;
			}
		}
	}
}
