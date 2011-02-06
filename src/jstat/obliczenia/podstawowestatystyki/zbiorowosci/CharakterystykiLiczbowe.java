/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package jstat.obliczenia.podstawowestatystyki.zbiorowosci;

import dmnutils.document.DocumentPageFactory;
import jstat.obliczenia.ObliczeniaAbstract;
import jstat.obliczenia.PageNotepadForObliczenia;

/**
 *
 * @author dmn
 */
public class CharakterystykiLiczbowe extends ObliczeniaAbstract {

    public CharakterystykiLiczbowe() {
        super();
        setCaption("Charakterystyki liczbowe struktury zbiorowości");
        constructorHelper();
    }

    protected void constructorHelper() {
    }

    public static DocumentPageFactory<PageNotepadForObliczenia> factory = new DocumentPageFactory_CharakterystykiLiczbowe();

}
