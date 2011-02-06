/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.awt.Graphics;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;

public class ColorRenderer extends JPanel
        implements TableCellRenderer {
    Border unselectedBorder = null;
    Border selectedBorder = null;
    Color value;
    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public ColorRenderer(boolean isBordered) {
    }

    public static void paintColor(JComponent component, Graphics g, Color value) {
        Color oldColor = g.getColor();
        g.setColor(value);
        Insets insets = component.getInsets();
        int squareWidth = component.getHeight() - insets.bottom-insets.top-1;
        g.fillRect(insets.left, insets.top, squareWidth, squareWidth);
        g.setColor(component.getForeground());
        g.drawRect(insets.left, insets.top, squareWidth, squareWidth);
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int textHeight = metrics.getHeight();
        int height = component.getHeight()-insets.top-insets.bottom;
        g.drawString("0x" + Integer.toHexString(value.getRGB()).substring(2).toUpperCase(),
                insets.left*2+squareWidth, (int)(height*0.5+textHeight*0.5-1));
        g.setColor(oldColor);
    }

    public void paint(Graphics g) {
        //JLabel
        super.paint(g);
        paintColor(this, g, value);
    }

    public Component getTableCellRendererComponent(
            JTable table, Object color,
            boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (!(color instanceof Color)) {
            throw new RuntimeException("!(color instanceof Color)");
        }
        value = (Color)color;

        setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
        } else {
            setBorder(noFocusBorder);
        }

        setToolTipText("RGB: " + value.getRed() + ", "
                + value.getGreen() + ", "
                + value.getBlue());
        return this;
    }
}
