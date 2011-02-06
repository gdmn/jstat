/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author Damian
 */
public class MyAction extends AbstractAction implements Serializable {

    private final ActionListener listener;
    private static int myActionCounter = 0;

    /**
     * Tworzy nowy obiekt MyAction, dziedziczący z AbstractAction.
     * @param listener do niego będą przekierowywane zdarzenia
     * @param id - identyfikator zdarzenia. Po wywołaniu actionPerformed
     * dostępny jako <code>getActionCommand()</code>.
     * Najlepiej użyć statycznej metody <code>MyAction.createNewId()</code>
     * do wygenerowania niepowtarzalnego identyfikatora
     * @param text etykieta
     * @param mnemonic "gorący" klawisz
     * @param desc opis do podpowiedzi (tooltip)
     * @param icon wiadomo
     */
    public MyAction(ActionListener listener, String id, String text, Integer mnemonic, String desc, ImageIcon icon) {
        super(text, icon);
        putValue("MyActionID", id);
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);
        this.listener = listener;
    }
    protected static final String ACTION_PREFIX = "ACTION_#";
    protected static final String ACTION_POSTFIX = "#";

    /**
     * Zwraca niepowtarzalny identyfikator dla ActionCommand. Nadaje się do tworzenia stałych w klasie, np.<br>
     * <code>
     * private static final String ACTION_ABOUT = MyAction.createNewId();
     * </code>
     */
    public static String createNewId() {
        return ACTION_PREFIX + (myActionCounter++) + ACTION_POSTFIX;
    }

    public void actionPerformed(ActionEvent e) {
        ActionEvent e2 = new ActionEvent(e.getSource(), e.getID(), getActionCommand(), e.getWhen(), e.getModifiers());
        System.out.println("" + e2.getActionCommand() + " " + getValue(NAME) + " (" + getValue(SHORT_DESCRIPTION) + ")");
        listener.actionPerformed(e2);
    }

    public String getActionCommand() {
        return getValue("MyActionID").toString();
    }

    public static MyAction findAction(MyAction[] where, String id) {
        for (MyAction a : where) {
            if (a != null && a.getActionCommand().equals(id)) {
                return a;
            }
        }
        return null;
    }

    public static JButton createToolBarButton(Action a) {
        JButton button = new JButton(a);
        button.setFocusable(false);
        button.setBorderPainted(jstat.Settings.get().getToolbarButtonBorderPainted());
        button.setOpaque(jstat.Settings.get().getToolbarButtonBorderPainted());
        if (button.getIcon() != null) {
            if (jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.ICONSONLY) {
                button.setText(""); // an icon-only button
            } else if (jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.TEXTONLY) {
                button.setIcon(null);
            } else if (jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.BOTH) {
                button.setVerticalTextPosition(AbstractButton.BOTTOM);
                button.setHorizontalTextPosition(AbstractButton.CENTER);
            } else if (jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.BOTH_TEXTRIGHT) {
            }
        }
        return button;
    }
}

