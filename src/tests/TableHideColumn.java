/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class TableHideColumn {
	public static void main(String[] args) {
		final JTable table = new JTable(10, 5);
		final TableColumnModel colModel = table.getColumnModel();
		JButton button = new JButton("Toggle Column 2");
		button.addActionListener(new ActionListener() {
			private TableColumn removed = null;
			public void actionPerformed(ActionEvent e) {
				if (removed == null) {
					removed = colModel.getColumn(2);
					colModel.removeColumn(removed);
				}
				else {
					colModel.addColumn(removed);
					colModel.moveColumn(colModel.getColumnCount()-1, 2);
					removed = null;
				}
			}
		});

		JPanel content = new JPanel(new BorderLayout(0, 10));
		content.add(button, BorderLayout.NORTH);
		content.add(new JScrollPane(table));

		JFrame f = new JFrame("Hide Column Test");
		f.setContentPane(content);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.setVisible(true);
	}
}
