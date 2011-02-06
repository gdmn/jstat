/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.podstawowestatystyki;

import dmnutils.document.DocumentPageFactory;
import jstat.obliczenia.*;

/**
 *
 * @author dmn
 */
public class PodstawoweStatystyki extends ObliczeniaAbstract {

    public PodstawoweStatystyki() {
        super();
        setCaption("Podstawowe statystyki");
        constructorHelper();
    }

    protected void constructorHelper() {
    }

    public static DocumentPageFactory<PageNotepadForObliczenia> factory = new DocumentPageFactory_PodstawoweStatystyki();

}
