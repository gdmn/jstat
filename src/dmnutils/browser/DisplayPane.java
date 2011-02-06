/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.browser;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URL;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 *
 * @author dmn
 */
public class DisplayPane extends JEditorPane implements HyperlinkListener {
    private History pageList;
    private URL mainURL;

    public static final String ACTION_BACK = "ACTION_BACK";
    public static final String ACTION_FORWARD = "ACTION_FORWARD";
    public static final String ACTION_GO = "ACTION_GO";
    public static final String ACTION_SYSTEMBROWSE = "ACTION_SYSTEMBROWSE";
    /**
     * Get the value of mainURL
     *
     * @return the value of mainURL
     */
    public URL getMainURL() {
        return mainURL;
    }

    /**
     * Set the value of mainURL
     *
     * @param mainURL new value of mainURL
     */
    public void setMainURL(URL mainURL) {
        this.mainURL = mainURL;
    }

    public DisplayPane() {
        Dimension d = new Dimension(680, 480);
        //setMinimumSize(d);
        setPreferredSize(d);
        setContentType("text/html");
        setEditable(false);
        addHyperlinkListener(this);
        setBorder(new EmptyBorder(0,0,0,0));
        setForeground(java.awt.Color.BLACK);
        setBackground(java.awt.Color.WHITE);
        pageList = new History();
    }

    /* Wyświetlenie strony i dodanie jej do listy
    odwiedzonych stron. */
    private void showPage(URL pageUrl, boolean addToList) {
        System.out.println("Renderowanie "+pageUrl.toString());
        // Wyświetlenie kursora klepsydry w trakcie wczytywania strony.
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            // Pobranie adresu aktualnie wyświetlanej strony.
            URL currentUrl = getPage();
            // Wczytanie i wyświetlenie danej strony.
            setPage(pageUrl);
            // Pobranie adresu nowej strony.
            URL newUrl = getPage();
            mainURL = newUrl;
            // Dodanie strony do listy.
            if (addToList) {
                int listSize = pageList.size();
                if (listSize > 0) {
                    int pageIndex =
                            pageList.indexOf(currentUrl.toString());
                    if (pageIndex < listSize - 1) {
                        for (int i = listSize - 1; i > pageIndex; i--) {
                            pageList.remove(i);
                        }
                    }
                }
                pageList.add(newUrl.toString());
            }
            if (toolbar != null) {
                // Aktualizacja pola tekstowego adresu URL.
                toolbar.setLocationText(newUrl.toString());
                // Aktualizacja przycisków w zależności od strony.
                toolbar.updateButtons(pageList, newUrl);
            }
        } catch (Exception e) {
            // Wyświetlenie błędu.
            JOptionPane.showMessageDialog(this, "Błąd wczytywania strony",
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Zwrócenie domyślnego kursora.
            setCursor(Cursor.getDefaultCursor());
        }
    }
    private Toolbar toolbar;

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar(Toolbar t) {
        if (toolbar != null) {
            toolbar.removeActionListener(toolbarActionListener);
        }
        t.addActionListener(toolbarActionListener);
        this.toolbar = t;
    }
    private ActionListener toolbarActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(ACTION_BACK)) {
                actionBack();
            } else if (e.getActionCommand().equals(ACTION_FORWARD)) {
                actionForward();
            } else if (e.getActionCommand().equals(ACTION_GO)) {
                navigate(toolbar.getLocationText());
            } else if (e.getActionCommand().equals(ACTION_SYSTEMBROWSE)) {
                showInSystemBrowser();
            }
        }
    };

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e) {
        HyperlinkEvent.EventType eventType = e.getEventType();
        if (eventType == HyperlinkEvent.EventType.ACTIVATED) {
            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent linkEvent = (HTMLFrameHyperlinkEvent) e;
                HTMLDocument document = (HTMLDocument) getDocument();
                System.out.println("go to " + linkEvent.getDescription() + " " + linkEvent.getTarget());
                document.processHTMLFrameHyperlinkEvent(linkEvent);
            } else {
                showPage(e.getURL(), true);
            }
        }
    }

    // Przejście do poprzednio przeglądanej strony.
    public void actionBack() {
        URL currentUrl = getPage();
        int pageIndex = pageList.indexOf(currentUrl.toString());
        try {
            showPage(
                    new URL((String) pageList.get(pageIndex - 1)), false);
        } catch (Exception e) {
        }
    }

    // Przejście w przód oglądanej aktualnie strony.
    public void actionForward() {
        URL currentUrl = getPage();
        int pageIndex = pageList.indexOf(currentUrl.toString());
        try {
            showPage(
                    new URL((String) pageList.get(pageIndex + 1)), false);
        } catch (Exception e) {
        }
    }

    private URL verifyUrl(String url) {
        // Tylko adresy HTTP.
        //if (!url.toLowerCase().startsWith("http://"))
        //return null;

        // Weryfikacja URL.
        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(url);
        } catch (Exception e) {
            return null;
        }

        return verifiedUrl;
    }

    /**
     * Wyświetla przeglądaną stronę w systemowej przeglądarce.
     */
    public void showInSystemBrowser() {
        if (pageList.size()>0) {
            SystemBrowser.browse((pageList.get(pageList.size()-1).toString()));
        }
    }

    /** Wczytanie i wyświetlenie podanej strony. */
    public void navigate(String s) {
        URL verifiedUrl = verifyUrl(s);//locationTextField.getText());

        if (verifiedUrl != null) {
            navigate(verifiedUrl);
        } else {
            JOptionPane.showMessageDialog(this, "Błędny adres URLy",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Wczytanie i wyświetlenie podanej strony. */
    public void navigate(URL addr) {
        showPage(addr, true);
    }
}
