/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

/**
 * @author Damian
 *
 * Ma na celu ustawianie wartości komórki w tabeli ze sprawdzeniem, czy można ustawić podaną wartość
 */
public interface TableValueSetterInterface {
    /**
     * Sprawdza, czy można ustawić wartość w podanej komórce.<br>
     * Jeśli tak, zmienia wartość.
     * @param value wartość do ustawienia.
     * @param row numer wiersza tabeli. Automatycznie konwertowane na numer wiersza w <code>TableModel</code>
     * @param col numer kolumny tabeli. Automatycznie konwertowane na numer kolumny w <code>TableModel</code>
     * @return <code>true</code> jeśli wartość została zmieniona, <code>false</code> w przeciwnym wypadku
     */
    public boolean tryToSetValueAt(Object value, int row, int col);

    /**
     * Zmienia wartość w podanej komórce.<br>Domyślnie jest to metoda
     * delegowana z <code>TableModel</code> w <code>JTable</code>.
     * @param value wartość do ustawienia.
     * @param row numer wiersza tabeli. Automatycznie konwertowane na numer wiersza w <code>TableModel</code>
     * @param col numer kolumny tabeli. Automatycznie konwertowane na numer kolumny w <code>TableModel</code>
     */
    public void setValueAt(Object value, int row, int col);
}
