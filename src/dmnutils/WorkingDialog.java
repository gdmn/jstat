/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author dmn
 */
public class WorkingDialog extends JDialog {

    private JLabel label;
    private static WorkingDialog working;
    private static int counter = 0;
    private static LinkedList<String> progressTexts = new LinkedList<String>();

    WorkingDialog() {
        setTitle("Czekaj...");
        JPanel p = new JPanel(new GridLayout(0,1));
        p.setBorder(new EmptyBorder(20, 50, 20, 50));
        label = new JLabel("?");
        this.setMinimumSize(new Dimension(350,50));
        p.add(label);
        ((GridLayout)p.getLayout()).setVgap(10);
        JProgressBar pb = new JProgressBar();
        pb.setIndeterminate(true);
        p.add(pb);
        p.setOpaque(true);
        this.setContentPane(p);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setModal(false);
        this.setAlwaysOnTop(true);
        pack();
    }

    private void setProgressText(String text) {
        label.setText(text == null ? "..." : text);
        pack();
        setLocationRelativeTo(null);
    }

    public static void inc() {
        inc(null);
    }

    public static synchronized void inc(String progressText) {
//        System.out.println("inc");
        if (progressTexts == null || progressTexts.isEmpty()) {
            working = new WorkingDialog();
        }

        progressTexts.addFirst(progressText);
        working.setProgressText(progressText);
        working.repaint(); working.validate();
        working.setVisible(true);
    }

    public static synchronized void setTempProgressText(String progressText) {
        if (progressTexts == null || progressTexts.isEmpty()) {
            return;
        }
        working.setProgressText(progressText);
        working.repaint(); working.validate();
    }

    public static synchronized void dec() {
//        System.out.println("dec");
        progressTexts.removeFirst();
        if (progressTexts.isEmpty()) {
            working.setVisible(false);
            working.dispose();
        } else {
            working.setProgressText(progressTexts.peekFirst());
        }
    }

    private static void createAndShowGUI() {
        try {
            WorkingDialog.inc("i");
            Thread.yield();
            Thread.sleep(3000);
            Thread.yield();
            WorkingDialog.dec();
        //Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(WorkingDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                createAndShowGUI();
            }
        });
    }
}
