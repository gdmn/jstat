/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import com.thoughtworks.xstream.XStream;
import java.io.*;

/**
 * @author Damian
 *
 */
public abstract class BeanSaverLoader {

    public static void saveBean(String fileName, Object bean) {
        try {
            BufferedWriter outputStream = new BufferedWriter(new FileWriter(fileName));
            XStream xstream = new XStream();
            xstream.toXML(bean, outputStream);
            outputStream.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public static Object loadBean(String fileName) {
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(fileName));
            XStream xstream = new XStream();
            Object o = xstream.fromXML(inputStream);
            inputStream.close();
            return o;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            //ex.printStackTrace();
        }
        return null;
    }


        /*
    public static void saveBean(String fileName, Object bean) {
        XMLEncoder out = null;
        try {
            out = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileName)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        out.writeObject(bean);
        out.close();
    }

    public static void saveBean2(String fileName, Object bean) {
        ObjectOutputStream out = null;
        try {
            out = (new ObjectOutputStream(new FileOutputStream(fileName)));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            out.writeObject(bean);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Deprecated
    public static void saveBean(Object bean) {
        saveBean(bean.getClass().getName() + ".txt", bean);
    }

    public static Object loadBean(String fileName) {
        XMLDecoder in = null;
        Object result = null;
        try {
            in = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName)));
            try {
                result = in.readObject();
            } finally {
                in.close();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
         */


}
