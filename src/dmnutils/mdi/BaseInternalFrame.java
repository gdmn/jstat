/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.mdi;

import java.io.Serializable;
import javax.swing.*;

/**
 * Bazowa klasa dla okien w aplikacji MDI
 * @author Damian
 */
public class BaseInternalFrame extends JInternalFrame implements Serializable {

    static int openFrameCount = 0;
    private static int positionCounter = 0;
    static final int xOffset = 20,  yOffset = 20;
    private JMenuItem menuItem;
    //private static ArrayList<BaseInternalFrame> framesArray = new ArrayList<BaseInternalFrame>();
    public BaseInternalFrame() {
        super("Dokument #" + (++openFrameCount),
                true, //resizable
                true, //closable
                true, //maximizableDocumentPageInterface
                true);//iconifiable

            //iconifiable
            //...Create the GUI and put it in the window...
            //...Create the GUI and put it in the window...
            //...Then set the window size or call pack...
            setSize(400, 200);

            setLocation(xOffset * (positionCounter), yOffset * (positionCounter));
            positionCounter = (positionCounter < 9) ? positionCounter + 1 : 0;
            setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Wywoływana przez BaseMDI gdy nie ma żadnych okien. Układanie można zacząć od początku.
     */
    static void resetPositionCounter() {
        positionCounter = 0;
    }

    public JMenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(JMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public boolean canClose() {
        int n = JOptionPane.YES_OPTION;
        Object[] options = {"Tak", "Nie"};
        n = JOptionPane.showOptionDialog(this,
                "Zamknąć dokument?",
                this.getTitle(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        return n == JOptionPane.YES_OPTION;
    }
}
