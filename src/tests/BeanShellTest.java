/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package tests;

import bsh.EvalError;
import bsh.Interpreter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jstat.plugins.wrappers.Tools.*;

/**
 *
 * @author dmn
 */
public class BeanShellTest {

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        assert 2<1;
        try {
            Interpreter interpreter = new Interpreter();
            BeanShellTest bean = new BeanShellTest();
            bean.setValue(2);
            interpreter.set("exampleBean", bean);
            Integer value = (Integer) interpreter.eval("10/exampleBean.getValue()");
            System.out.println(value);
            System.out.println("in "+calc("5/4"));
            System.out.println("" + interpreter.eval("calc(String input) { return kalkulator.Calc.calc(input).toString(); };\n calc(\"3+33\")"));
            String value2 = interpreter.source("example.bsh").toString();
            System.out.println("example.bsh: "+value2);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BeanShellTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BeanShellTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (EvalError ex) {
            Logger.getLogger(BeanShellTest.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("beanshellerror: " + ex.getErrorSourceFile() +": " + ex.getMessage() );
        }
    }
}
