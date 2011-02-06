/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat;

/**
 * Aktualna wersja programu
 * @author dmn
 */
public abstract class Version {
    public static int MAJOR = 0;
    public static int MINOR = 25; //*0.01
    public static String VER = Integer.toString(MAJOR)+"."+Integer.toString(MINOR);
    public static String NAME = "JStat";
    public static String NAME_VER = NAME+" "+VER;
    public static String AUTHOR = "Damian Gorlach";
	public static String XMPP =  "dmn@jabster.pl";
	public static String MAIL =  "dmn@jabster.pl";
	public static String WWW = "http://gdamian.ovh.org/jstat";
    public static String AUTHOR_LONG = AUTHOR+"\nmail: "+MAIL+"\n"+WWW;
}
