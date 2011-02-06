/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.browser;

import dmnutils.SwingUtils;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

/**
 *
 * @author dmn
 */
public class Toolbar extends JToolBar {
    // Przyciski do poruszania się po liście stron.
    private JButton backButton,  forwardButton,  goButton, sysButton;

    public Toolbar() {
        backButton = new JButton("< Wstecz");
        backButton.setActionCommand(DisplayPane.ACTION_BACK);
        backButton.setEnabled(false);
        add(backButton);
        forwardButton = new JButton("Do przodu >");
        forwardButton.setActionCommand(DisplayPane.ACTION_FORWARD);
        forwardButton.setEnabled(false);
        add(forwardButton);
        sysButton = new JButton("System.");
        sysButton.setActionCommand(DisplayPane.ACTION_SYSTEMBROWSE);
        add(sysButton);
        goButton = new JButton("Idź");
        goButton.setActionCommand(DisplayPane.ACTION_GO);
        locationTextField = new JTextField(35);
        locationTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    //actionGo();
                    goButton.doClick();
                }
            }
        });
        add(locationTextField);
        add(goButton);
        if (jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.ICONSONLY ||
                jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.BOTH ||
                jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.BOTH_TEXTRIGHT) {
            backButton.setIcon(SwingUtils.createNavigationIcon("go-previous"));
            forwardButton.setIcon(SwingUtils.createNavigationIcon("go-next"));
            goButton.setIcon(SwingUtils.createNavigationIcon("go-jump"));
            sysButton.setIcon(SwingUtils.createNavigationIcon("system-run"));
        }
        for (JButton b : new JButton[]{backButton, forwardButton, goButton, sysButton}) {
            b.setFocusable(false);
            b.setBorderPainted(jstat.Settings.get().getToolbarButtonBorderPainted());
            b.setOpaque(jstat.Settings.get().getToolbarButtonBorderPainted());
            if (jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.BOTH) {
                b.setVerticalTextPosition(AbstractButton.BOTTOM);
                b.setHorizontalTextPosition(AbstractButton.CENTER);
            }
            if (jstat.Settings.get().getToolbarStyle() == jstat.Settings.ToolbarStyle.ICONSONLY) {
                b.setText("");
            }
        }
        setFocusable(false);
        setFloatable(false);
        setBorderPainted(jstat.Settings.get().getToolbarBorderPainted());
    }

    public void addActionListener(ActionListener l) {
        for (AbstractButton c : new AbstractButton[]{forwardButton, backButton, goButton, sysButton}) {
            c.addActionListener(l);
        }
    }

    public void removeActionListener(ActionListener l) {
        for (AbstractButton c : new AbstractButton[]{forwardButton, backButton, goButton, sysButton}) {
            c.removeActionListener(l);
        }
    }
    private JTextField locationTextField;

    public JTextField getLocationTextField() {
        return locationTextField;
    }

    public void setLocationTextField(JTextField locationTextField) {
        this.locationTextField = locationTextField;
    }

    public void setLocationText(String s) {
        locationTextField.setText(s);
    }

    public String getLocationText() {
        return locationTextField.getText();
    }

    public void updateButtons(History h, URL currentUrl) {
        if (h.size() < 2) {
            backButton.setEnabled(false);
            forwardButton.setEnabled(false);
        } else {
            int pageIndex = h.indexOf(currentUrl.toString());
            backButton.setEnabled(pageIndex > 0);
            forwardButton.setEnabled(
                    pageIndex < (h.size() - 1));
        }

    }
}
