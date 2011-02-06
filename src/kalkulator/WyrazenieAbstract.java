/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

import java.util.*;

/**
 *
 * @author Damian
 */
//public abstract class WyrazenieAbstract implements java.io.Serializable  {
public abstract class WyrazenieAbstract extends ElementWyrazenia  {


    /** lista wszystkich elementów wyrażenia */
    protected ArrayList<ElementWyrazenia>  myElements;

    /** lista fcji użytkownika */
    protected FunkcjaInterface[] userArrayOfFunctions;

    public WyrazenieAbstract(ElementWyrazenia[] e, FunkcjaInterface[] userArrayOfFunctions) {
        //TODO: tekstowa reprezentacja
        super("WyrazenieAbstract");
        myElements = new ArrayList<ElementWyrazenia>(Arrays.asList(e));
        this.userArrayOfFunctions = userArrayOfFunctions;
        /*
         * myElements = new ArrayList<ElementWyrazenia>(e.length); for (ElementWyrazenia ele : e) { myElements.add(ele); }
         */
    }

    public WyrazenieAbstract(Collection<? extends ElementWyrazenia> e, FunkcjaInterface[] userArrayOfFunctions) {
        //TODO: tekstowa reprezentacja
        super("WyrazenieAbstract");
        myElements = new ArrayList<ElementWyrazenia>(e);
        this.userArrayOfFunctions = userArrayOfFunctions;
    }

    /**
     * @return ilość elementów
     */
    public int getSize() {
        return (myElements == null) ? 0 : myElements.size();
    }

    /**
     * @return element o podanym indeksie
     */
    public ElementWyrazenia getItem(int index) throws IndexOutOfBoundsException {
        return (myElements == null) ? null : myElements.get(index);
    }

    /**
     * @return tablica elementów
     */
    protected ArrayList<ElementWyrazenia> getAll() {
        return myElements;
    }

    /**
     * @return tekstowa reprezentaca obiektu
     */
    public String toString() {
        if ((myElements == null) || (myElements.size() == 0)) return null;
        StringBuilder result = new StringBuilder();
        if (myElements.size() > 0) {
            for (ElementWyrazenia ele : myElements) {
                // result.append(", "+ele.getClass().getName()+" "+ele.toString());
                // result.append(", "+ele.getClass().getSimpleName()+" "+ele.toString());
                result.append(", " + ele.toString());
            } // for
            result.delete(0, 2);
        } // size>0
        return "["+result.toString()+"]";
    }

    /**
     * @return wartość userArrayOfFunctions
     */
    public FunkcjaInterface[] getUserArrayOfFunctions() {
        return userArrayOfFunctions;
    }

    /**
     * Używać z rozwagą. <b>DODAJE</b> wartości podane w parametrze. Przydaje się, jeśli trzeba narysować wykres,
     * a jakieś funkcje użytkownika są dodane.
     * @param userArrayOfFunctions wartość userArrayOfFunctions do ustawienia
     */
    public void setUserArrayOfFunctions(FunkcjaInterface[] userArrayOfFunctions) {
        FunkcjaInterface[] tmp = new FunkcjaInterface[this.userArrayOfFunctions.length+userArrayOfFunctions.length];
        int counter = 0;
        for (FunkcjaInterface f : userArrayOfFunctions) {
            tmp[counter++] = f;
        }
        for (FunkcjaInterface f : this.userArrayOfFunctions) {
            tmp[counter++] = f;
        }
        this.userArrayOfFunctions = tmp;
    }

    public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
        throw new InvalidArgumentException("Nie można wywołać ElementWyrazenia.getValue()");
    }

    public int getArgumentsCount() {
        return 0;
    }
}
