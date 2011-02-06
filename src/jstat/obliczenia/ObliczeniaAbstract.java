/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia;

import dmnutils.document.*;
import jstat.pages.notepad.*;

/**
 *
 * @author dmn
 */
public abstract class ObliczeniaAbstract extends AbstractDocumentPage<PageNotepad> {

    public ObliczeniaAbstract() {
        super();
    }


    public static DocumentPageFactory<PageNotepadForObliczenia> factory = new DocumentPageFactory_ObliczeniaAbstract();

}
