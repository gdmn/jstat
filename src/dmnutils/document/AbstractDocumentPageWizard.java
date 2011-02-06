/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.document;

import dmnutils.SwingUtils;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Damian
 */
public class AbstractDocumentPageWizard extends JDialog {

    /** Creates a new instance of AbstractDocumentPageWizard */
    public AbstractDocumentPageWizard() {
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
    }

    public static void main(String[] args) {
        JFrame o = new JFrame ("o") {
            public Dimension getPreferredSize() {
                return new Dimension(400,300);
            }
        };
        o.setContentPane(new JPanel(new FlowLayout()));
        //JOptionPane.showMessageDialog(this, "o");
        {
            JButton b = new JButton("pokaż dialog");
            o.getContentPane().add(b);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    AbstractDocumentPageWizard a = new AbstractDocumentPageWizard();
                    a.setVisible(true);
                }
            });
        }
        SwingUtils.run(o);
    }
}
