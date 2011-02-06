/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package dmnutils.document;

import java.io.Serializable;

/**
 *
 * @author Damian
 */
public interface DocumentPageFactory <T extends AbstractDocumentPage> extends Serializable {

    /**
     * Procedura tworząca stronę. Powinna uruchomić np. kreator i zwrócić utworzoną stronę
     * @param doc dokument, do którego strona zostanie dodana, zmienna nie może
     * być zmodyfikowana przez tą procedurę.
     */
    T create(Document doc);

}
