/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */


package tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.*;

/**
 *
 * @author Damian
 */
class MyClassLoader extends ClassLoader {

    public Class findClass(String name) {
        byte[] b = loadClassData(name);
        return defineClass("wykres."+
                name, b, 0, b.length);
    }

    private byte[] loadClassData(String name) {
        File f = new File("./plugins/"+name+".class");
        FileInputStream in;
        byte[] buf;
        try {
            in = new FileInputStream(f);
            buf = new byte[(int) f.length()];
            in.read(buf);
        } catch (FileNotFoundException ex) { return null; } catch (IOException ex) { return null; }
        return buf;
    }

    public synchronized Class<?> loadClass(String name, boolean resolve) {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

public class LoaderTest {

    public static void linia() {
        System.out.println("--------------------------------------------------------------");
    }

    public static void printPackage(Class loadedClass) {
        //throw new UnsupportedOperationException("Not yet implemented");
        System.out.println(loadedClass.getCanonicalName());
        for (Class<?> i : loadedClass.getClasses()) {
            System.out.println("  "+ i);
        }
        for (Type i : loadedClass.getGenericInterfaces()) {
            System.out.println("  "+ i);
        }
    }

    static void showMethods(Class c) {
        //Class c = o.getClass();
        System.out.println("showMethods");
        Method[] theMethods = c.getDeclaredMethods();
        for (int i = 0; i < theMethods.length; i++) {
            String methodString = theMethods[i].getName();
            System.out.println("Name: " + methodString);
            String returnString =
                    theMethods[i].getReturnType().getName();
            System.out.println("   Return Type: " + returnString);
            Class[] parameterTypes = theMethods[i].getParameterTypes();
            System.out.print("   Parameter Types:");
            for (int k = 0; k < parameterTypes.length; k ++) {
                String parameterString = parameterTypes[k].getName();
                System.out.print(" " + parameterString);
            }
            System.out.println();
        }
    }

    public static void printModifiers(Class c) {
        //Class c = o.getClass();
        System.out.println("printModifiers");
        int m = c.getModifiers();
        if (Modifier.isPublic(m))
            System.out.println("public");
        if (Modifier.isAbstract(m))
            System.out.println("abstract");
        if (Modifier.isFinal(m))
            System.out.println("final");
    }

    static void printSuperclasses(Class subclass) {
        System.out.println("printSuperclasses");
        Class superclass = subclass.getSuperclass();
        while (superclass != null) {
            String className = superclass.getName();
            System.out.println(className);
            subclass = superclass;
            superclass = subclass.getSuperclass();
        }
    }

    static void printInterfaceNames(Class c) {
        //Class c = o.getClass();
        System.out.println("printInterfaceNames");
        Class[] theInterfaces = c.getInterfaces();
        for (int i = 0; i < theInterfaces.length; i++) {
            String interfaceName = theInterfaces[i].getName();
            System.out.println(interfaceName);
        }
    }

    static void verifyInterface(Class c) {
        System.out.println("verifyInterface");
        String name = c.getName();
        if (c.isInterface()) {
            System.out.println(name + " is an interface.");
        } else {
            System.out.println(name + " is a class.");
        }
    }

    static void printFieldNames(Class c) {
        System.out.println("printFieldNames");
        //Class c = o.getClass();
        Field[] publicFields = c.getFields();
        for (int i = 0; i < publicFields.length; i++) {
            String fieldName = publicFields[i].getName();
            Class typeClass = publicFields[i].getType();
            String fieldType = typeClass.getName();
            System.out.println("Name: " + fieldName +
                    ", Type: " + fieldType);
        }
    }

    static void showConstructors(Class c) {
        System.out.println("showConstructors");
        Constructor[] theConstructors = c.getConstructors();
        for (int i = 0; i < theConstructors.length; i++) {
            System.out.print("( ");
            Class[] parameterTypes =
                    theConstructors[i].getParameterTypes();
            for (int k = 0; k < parameterTypes.length; k ++) {
                String parameterString = parameterTypes[k].getName();
                System.out.print(parameterString + " ");
            }
            System.out.println(")");
        }
    }

    public static void runMain(Class c) {
        Class[] parameterTypes = new Class[] {String[].class};
        Method mainMethod;
        Object[] arguments = new Object[] {null};
        try {
            //Object o = c.newInstance();
            mainMethod = c.getMethod("main", parameterTypes);
            //result = (String)
            mainMethod.invoke(null, arguments);
        } catch (NoSuchMethodException e) {
            System.err.println("###### NoSuchMethodException: "+c.getName());
        } catch (IllegalAccessException e) {
            System.err.println("###### IllegalAccessException: "+c.getName());
        } catch (InvocationTargetException e) {
            System.err.println("###### InvocationTargetException: "+c.getName());
        }/* catch (InstantiationException ex) {
            System.out.println("######4   FAILED RUNNING: "+c.getName());
        }*/
        return;
    }

    public static void zbadajKlase(Class loadedClass) {
        linia();
        printPackage(loadedClass);
        linia();
        showMethods(loadedClass);
        linia();
        printModifiers(loadedClass);
        linia();
        printSuperclasses(loadedClass);
        linia();
        printInterfaceNames(loadedClass);
        linia();
        verifyInterface(loadedClass);
        linia();
        printFieldNames(loadedClass);
        linia();
        showConstructors(loadedClass);
    }

    public static void pr(File[] fs) {
        for (File f: fs) {
            String fn = null;
            fn = f.getName().toString();
            //System.out.println(fn);
            if (fn.indexOf("$") <= 0 && fn.endsWith(".class")) {
                try {
                    //fn = fn.substring(2);
                    fn = fn.replaceAll(".class", "");
                    System.out.println(fn);
                    //Class c = com.sun.org.apache.bcel.internal.util.ClassLoader.getSystemClassLoader().loadClass(fn);
                    MyClassLoader loader = new MyClassLoader();
                    Class loadedClass = loader.loadClass(//"wykres."+
                            fn, true);
                    //Object loadedObject = loadedClass.newInstance();
                    //Class c = Class.getClassLoader().loadClass(fn);
                    //System.out.println(""+c);
                    zbadajKlase(loadedClass);
                    linia();
                    runMain(loadedClass);
                } finally {}
            }
        }
    }

    public static void pr2(File[] fs, String pref) {
        for (File f: fs) {
            System.out.println(pref+f+"");
            if (f.isDirectory()) {
                pr2(f.listFiles(), pref+"  ");
            }
        }
    }

    /** Creates a new instance of LoaderTest */
    public LoaderTest() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        pr(File.listRoots());
//        pr((new File("C:\\")).listFiles());
//        pr2((new File(".")).listFiles(), "|");
        pr((new File("./plugins")).listFiles());
        //zbadajKlase(LoaderTest.class);
        //JavaFileManager
        //pr((new File("JStat.jar")).listFiles());
    }

}
