/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * Wyświetla menu prawoklikowe podane w parametrze konstruktora.
 * @author dmn
 */
public class PopupListener extends MouseAdapter {

    private JPopupMenu popup;

    /**
     * Listener odpowiedzialny za wyświetlenie menu kontekstowego
     * po naciśnięciu prawego przycisku myszki.
     * @param popup menu do wyświetlenia.
     */

    public PopupListener(JPopupMenu popup) {
        super();
        this.popup = popup;
    }

    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popup.show(e.getComponent(),
                    e.getX(), e.getY());
        }
    }
    }
