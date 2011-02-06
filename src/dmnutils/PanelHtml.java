/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.*;
import org.lobobrowser.html.gui.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *
 * @author dmn
 */
public class PanelHtml extends JPanel {
    private HtmlPanel panel = new HtmlPanel();

    public PanelHtml() {
        super(new BorderLayout());
        panel = new HtmlPanel();
        this.add(panel, BorderLayout.CENTER);
    }

    public PanelHtml(String uri) throws MalformedURLException, SAXException, IOException {
        this();
        render(uri);
    }

    public PanelHtml(InputStream is, String uri) throws MalformedURLException, SAXException, IOException {
        this();
        render(is, uri, guessEncoding(null, is));
    }

    public PanelHtml(Reader reader, String uri) throws MalformedURLException, SAXException, IOException {
        this();
        render(reader, uri);
    }

    public void render(URL url) throws IOException, SAXException {
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();
        try {
            render(in, url.toString(), guessEncoding(connection, in));
        } finally {
            in.close();
        }
    }

    public void render(String uri) throws MalformedURLException, IOException, SAXException {
        //String uri = "http://google.com";
        URL url = new URL(uri);
        render(uri);
    }

    private String guessEncoding(URLConnection connection, InputStream in) {
        String encoding = null;
        try {
            if (connection != null) {
                encoding = connection.getContentEncoding();
            }
            if (encoding == null && in != null) {
// take a peek in the file then
                byte[] buff = new byte[512];
                int rc = 0;
                try {
                    in.mark(Integer.MAX_VALUE);
                    rc = in.read(buff);
                    in.reset();
                } catch (IOException ex) {
                    Logger.getLogger(PanelHtml.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (rc > 0) {
// fish out charset from meta tag
                    String s = new String(buff);
                    Pattern p = Pattern.compile("<meta\\shttp-equiv.*?Content-Type.*?charset=(.*?)\\\">",
                            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE | Pattern.DOTALL);
                    Matcher m = p.matcher(s);
                    if (m.find()) {
                        if (m.groupCount() > 1) {
                            encoding = m.group(1);
                        }
                    }
                }
            }
        } finally {
            if (encoding == null) {
                // no idea?
                System.out.println("No idea about stream encoding...");
                encoding = "UTF-8";
//                encoding = "windows-1250";
            }
            return encoding;
        }
    }

    public void render(InputStream in, String uri, String charset) throws SAXException, IOException {
// This example does not perform incremental rendering. //document = builder.parse(is);
        // A Reader should be created with the correct charset,
        // which may be obtained from the Content-Type header
        // of an HTTP response.
        System.out.println("Stream encoding: "+charset);
        Reader reader = new InputStreamReader(in, charset);
        render(reader, uri);
    }

    public void render(Reader reader, String uri) throws SAXException, IOException {
        // A Reader should be created with the correct charset,
        // which may be obtained from the Content-Type header
        // of an HTTP response.
        SimpleUserAgentContext ucontext = new SimpleUserAgentContext();
        SimpleHtmlRendererContext rcontext = new SimpleHtmlRendererContext(panel, ucontext);
        // Note that document builder should receive both contexts.
        DocumentBuilderImpl dbi = new DocumentBuilderImpl(ucontext, rcontext);
        // A documentURI should be provided to resolve relative URIs.
        InputSourceImpl is = new InputSourceImpl(reader, uri);
        is.setEncoding("UTF-8");
        System.out.println("Reader charset is: "+is.getEncoding());
        Document document = dbi.parse(is);
        // Now set document in panel. This is what causes the document to render.
        panel.setDocument(document, rcontext);
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() throws MalformedURLException, IOException, SAXException {
        //Create and set up the window.
        JFrame frame = new JFrame("FrameDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(20, 100, 400, 150);

        //frame.getContentPane().add(new PanelHtml("http://google.com"), BorderLayout.CENTER);
        StringReader sr = new StringReader("<b>to</b> jest test<br />:)");
        frame.getContentPane().add(new PanelHtml(sr, null), BorderLayout.CENTER);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGUI();
                } catch (MalformedURLException ex) {
                    Logger.getLogger(PanelHtml.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(PanelHtml.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SAXException ex) {
                    Logger.getLogger(PanelHtml.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
