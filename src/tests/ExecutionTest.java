/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package tests;

import java.io.*;
import javax.swing.*;

/**
 *
 * @author Damian
 */
public class ExecutionTest {

    /** Creates a new instance of ExecutionTest */
    public ExecutionTest() {
    }

    public static void main(String[] args) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("cmd /c notepad.exe");
//            try {
//                if(process.waitFor() == 0) {
//                //By placing this in a condition, the java program is forced to wait
//                } else
//                    throw new InterruptedException("Process failed to terminated correctly");
//            } catch(InterruptedException ee){
//                System.out.println(ee.getMessage());
//            }
            BufferedWriter buffOut =
                    new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader buffIn =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            buffOut.write("//Some String");
            buffOut.flush(); //Ensure that the output reaches the process
            //            if((s=buffIn.readLine())!= null) System.out.println(s);
            buffOut.close();
            buffIn.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "koniec");
    }

}
