/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.beans.*;
import java.lang.reflect.*;

/**
 * Klasa ułatwiająca niektóre operacje na obiektach JavaBean:
 * <ul>
 * <li>ustawianie właściwości,
 * <li>pobieranie właściwości.
 * </ul>
 * Zawiera jedynie statyczne metody.
 * @author Damian
 */
public abstract class BeanSetterGetter {

    /**
     * Ustawia właściwość <code>property</code> obiektu <code>bean</code> na <code>value</code>.
     * @param bean obiekt, którego właściwość ma być zmieniona
     * @param property String określający nazwę właściwości
     * @param value wartość do ustawienia
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void setBeanProperty(Object bean, String property, Object value) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        BeanInfo bi;
        bi = Introspector.getBeanInfo(bean.getClass(), null);
        PropertyDescriptor[] pds = bi.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getName().equals(property)) {
                setBeanProperty(bean, pd, value);
                return;
            }
        }
    }

    /**
     * Ustawia właściwość <code>property</code> obiektu <code>bean</code> na <code>value</code>.
     * @param bean obiekt, którego właściwość ma być zmieniona
     * @param property właściwość
     * @param value wartość do ustawienia
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void setBeanProperty(Object bean, PropertyDescriptor property, Object value) throws IllegalAccessException, InvocationTargetException {
        Method writeMethod = property.getWriteMethod();
        writeMethod.invoke(bean, value);
    }

    /**
     * Wywołuje <code>setBeanProperty()</code> i przechwytuje wszystkie wyjątki.<br>
     * Uwaga: wypisuje błędy na konsolę.
     * @return <code>true</code> jeśli bez błędów
     */
    public static boolean setBeanPropertyEasy(Object bean, String property, Object value) {
        try {
            setBeanProperty(bean, property, value);
            return true;
        } catch (IllegalAccessException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        } catch (InvocationTargetException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        } catch (IntrospectionException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        }
        return false;
    }

    /**
     * Wywołuje <code>setBeanProperty()</code> i przechwytuje wszystkie wyjątki.<br>
     * Uwaga: wypisuje błędy na konsolę.
     * @return <code>true</code> jeśli bez błędów
     */
    public static boolean setBeanPropertyEasy(Object bean, PropertyDescriptor property, Object value) {
        try {
            setBeanProperty(bean, property, value);
            return true;
        } catch (IllegalAccessException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        } catch (InvocationTargetException ex) {
            if (ex.getCause() != null) { //java.beans.PropertyVetoException
                System.err.println(ex.getCause().getClass().getSimpleName()+": "+ex.getCause().getMessage());
            } else {
                System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
            }
        }
        return false;
    }

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

    /**
     * Pobiera właściwość <code>property</code> obiektu <code>bean</code>.
     * @param bean obiekt, którego właściwość ma być odczytana
     * @param property String określający nazwę właściwości
     * @throws IntrospectionException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @return wartość właściwości
     */
    public static Object getBeanProperty(Object bean, String property) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        BeanInfo bi;
        bi = Introspector.getBeanInfo(bean.getClass(), null);
        PropertyDescriptor[] pds = bi.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            if (pd.getName().equals(property)) {
                return getBeanProperty(bean, pd);
            }
        }
        throw new IntrospectionException("Próba odczytania nieistniejącej właściwości");
    }

    /**
     * Pobiera właściwość <code>property</code> obiektu <code>bean</code>.
     * @param bean obiekt, którego właściwość ma być odczytana
     * @param property właściwość
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @return wartość właściwości
     */
    public static Object getBeanProperty(Object bean, PropertyDescriptor property) throws IllegalAccessException, InvocationTargetException {
        Method readMethod = property.getReadMethod();
        return readMethod.invoke(bean);
    }

    /**
     * Wywołuje <code>getBeanProperty()</code> i przechwytuje wszystkie wyjątki.<br>
     * Uwaga: wypisuje błędy na konsolę.
     * @return w razie błędu zwraca <code>null</code>
     */
    public static Object getBeanPropertyEasy(Object bean, String property) {
        try {
            return getBeanProperty(bean, property);
        } catch (IllegalAccessException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        } catch (InvocationTargetException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        } catch (IntrospectionException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        }
        return null;
    }

    /**
     * Wywołuje <code>getBeanProperty()</code> i przechwytuje wszystkie wyjątki.<br>
     * Uwaga: wypisuje błędy na konsolę.
     * @return w razie błędu zwraca <code>null</code>
     */
    public static Object getBeanPropertyEasy(Object bean, PropertyDescriptor property) {
        try {
            return getBeanProperty(bean, property);
        } catch (IllegalAccessException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        } catch (InvocationTargetException ex) {
            System.err.println(ex.toString()+" - "+bean.getClass().getCanonicalName()+"."+property);
        }
        return null;
    }

}
