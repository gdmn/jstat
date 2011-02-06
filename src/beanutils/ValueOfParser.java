/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package beanutils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Klasa, która przekształca String na dowolny typ ogólny, jeśli tylko ten typ posiada statyczną metodę valueOf(String).
 * @author Damian
 */
public class ValueOfParser<T> {
    private Class<?> kind;
    private Class<?> kind2; //jeśli kind jest typem podstawowym, kind2 jest typem go opakowującym, inaczej kind2 = kind;

    public static Class<?> bazowyDoOpakowujacego(Class<?> base) {
        if (base == byte.class) return Byte.class;
        if (base == long.class) return Long.class;
        if (base == short.class) return Short.class;
        if (base == int.class) return Integer.class;
        if (base == double.class) return Double.class;
        if (base == float.class) return Float.class;
        if (base == char.class) return Character.class;
        if (base == boolean.class) return Boolean.class;
        return base;
    }

    /**
     *
     * @param kind klasa obiektu wynikowego
     */
    public ValueOfParser(Class<?> kind) {
        this.kind = kind;
        this.kind2 = bazowyDoOpakowujacego(kind);
    }

    public T valueOf(String s) throws NullPointerException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        if (kind == String.class) {
            @SuppressWarnings("unchecked")
            T result = (T)s;
            return result;
        }
        Method[] methods = kind2.getDeclaredMethods();
        Method valueOf = null;
        for (Method m : methods) {
            if (m.getName().equals("valueOf") &&
                    (m.getReturnType().equals(kind2)) &&
                    (m.getParameterTypes().length == 1) &&
                    (m.getParameterTypes()[0].equals(String.class))) {
                valueOf = m;
                break;
            }
        }
        @SuppressWarnings("unchecked")
        T result = (T) valueOf.invoke(null, s);
        return result;
    }

    /**
     * Przekształca String na dowolny typ ogólny, jeśli tylko ten typ posiada
     * statyczną metodę <code>valueOf(String)</code>.
     * <p>
     * W wypadku gdy <code>kind</code> jest typem bazowym (np. int, byte),
     * przeszukuje klasy opakowujące (tutaj: Integer, Byte).
     * <p>
     * W przypadku, gdy <code>kind == String.class</code>, zwracany jest <code>s</code>.
     * <p>
     * Przykład wywołania:
     * <li><code>int i = ValueOfParser.<Integer>valueOf("3414", Integer.class);</code>
     * @param s String do przekształcenia
     * @param kind klasa obiektu wynikowego
     * @return Obiekt typu <code>T</code> z wartością <code>s</code>.
     * @throws NullPointerException kiedy nie znaleziono metody <code>valueOf(String)</code>
     * @throws IllegalArgumentException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static <T> T valueOf(String s, Class<?> kind) throws NullPointerException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        ValueOfParser<T> v = new ValueOfParser<T>(kind);
        return v.valueOf(s);
    }
}

