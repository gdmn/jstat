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
import java.util.Arrays;
import wykres.Settings;

/**
 * @author Damian
 *
 */
public abstract class BeanAbout {

    public static String dumpFull(Class<?> bean) {
        final String L = "====";
        final String CR = "\n";
        StringBuilder result = new StringBuilder(200);
        BeanInfo bi = null;
        try {
            // bi = Introspector.getBeanInfo(bean, Object.class);
            bi = Introspector.getBeanInfo(bean, bean.getSuperclass());
        } catch (IntrospectionException e) {
            result.append("Nie można podejrzeć " + bean.getName() + CR);
            return result.toString();
        }
        for (PropertyDescriptor d : bi.getPropertyDescriptors()) {
            Class<?> p = d.getPropertyType();
            if (p == null) continue;
            result.append("Klasa właściwości:  " + p.getName() + ", Nazwa właściwości: " + d.getName() + CR);
            Method readMethod = d.getReadMethod();
            if (readMethod != null) result.append("Metoda odczytująca: " + readMethod+CR);
            Method writeMethod = d.getWriteMethod();
            if (writeMethod != null) result.append("Metoda ustawiająca: " + writeMethod+CR);
            if (p.getSuperclass() != null) {
                result.append("Superklasa: " + p.getSuperclass().getName()+CR);
                if (Enum.class.isAssignableFrom(p)) {
                    try {
                        Method m_values = p.getMethod("values");
                        result.append(Arrays.toString((Object[]) m_values.invoke(null))+CR);
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    } catch (SecurityException ex) {
                        ex.printStackTrace();
                    } catch (NoSuchMethodException ex) {
                        ex.printStackTrace();
                    }

                }
            }
            result.append(L+CR);
        }
        result.append("Metody publiczne:"+CR);
        MethodDescriptor mScramble = null;
        for (MethodDescriptor m : bi.getMethodDescriptors()) {
            result.append(m.getMethod().toString()+CR);
            if (m.getMethod().toString().toLowerCase().indexOf("scramble") > 0) {
                mScramble = m;
            }
        }
        result.append(L+CR);
        if (mScramble != null) {
            result.append("Znaleziono scramble !"+CR);
            Method me = mScramble.getMethod();
            try {
                Object binstance = bean.newInstance();
                me.invoke(binstance);
                //BeanSaverLoader.saveBean(binstance);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            }
            result.append(L+CR);
        }
        result.append("Obsługa zdarzeń:"+CR);
        for (EventSetDescriptor e : bi.getEventSetDescriptors()) {
            result.append("Typ odbiornika: " + e.getListenerType().getName()+CR);
            for (Method lm : e.getListenerMethods())
                result.append("Metoda odbiornika: " + lm.getName()+CR);
            for (MethodDescriptor lmd : e.getListenerMethodDescriptors())
                result.append("Deskryptor metody: " + lmd.getMethod()+CR);
            Method addListener = e.getAddListenerMethod();
            result.append("Metoda dodająca odbiornik: " + addListener+CR);
            Method remListener = e.getRemoveListenerMethod();
            result.append("Metoda usuwająca odbiornik: " + remListener+CR);
            result.append(L+CR);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(dumpFull(Settings.class));
    }

}
