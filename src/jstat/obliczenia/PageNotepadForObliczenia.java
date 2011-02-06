/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia;

import jstat.pages.html.PageHtmlOutput;

/**
 * PageNotepad w trybie tylko do odczytu
 * @author dmn
 */
public class PageNotepadForObliczenia extends PageHtmlOutput {

    public PageNotepadForObliczenia() {
        setCaption("Obliczenia");
    }
    @Override
    protected void constructorHelper() {
        super.constructorHelper();
    }
}
