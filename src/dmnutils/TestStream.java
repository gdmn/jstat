/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */
package dmnutils;

//: com:bruceeckel:simpletest:TestStream.java
//Simple utility for testing program output. Intercepts
//System.out to print both to the console and a buffer.
//From 'Thinking in Java, 3rd ed.' (c) Bruce Eckel 2002
//www.BruceEckel.com. See copyright notice in CopyRight.txt.

import java.io.*;

import javax.swing.JOptionPane;

public abstract class TestStream extends PrintStream {
    // private PrintStream console = System.out, err = System.err, fout;
    // private InputStream stdin;
    // To store lines sent to System.out or err

    public TestStream() {
        super(System.out, true); // Autoflush
    }

    /** po utworzeniu, jakby ciąg dalszy konstruktora */
    public abstract void afterCreated();
	
    public void dispose() { close() ;}

    public void close() {
        super.close();
    }

    /** może być z lub bez CR!!! */
    public abstract void print(String s);

    // Override all possible print/println methods to send
    // intercepted console output to both the console and
    // the Output.txt file:
    public void print(boolean x) {
        print(x + "");
    }

    public void println(boolean x) {
        print(x + "\n");
    }

    public void print(char x) {
        print(x + "");
    }

    public void println(char x) {
        print(x + "\n");
    }

    public void print(int x) {
        print(x + "");
    }

    public void println(int x) {
        print(x + "\n");
    }

    public void print(long x) {
        print(x + "");
    }

    public void println(long x) {
        print(x + "\n");
    }

    public void print(float x) {
        print(x + "");
    }

    public void println(float x) {
        print(x + "\n");
    }

    public void print(double x) {
        print(x + "");
    }

    public void println(double x) {
        print(x + "\n");
    }

    public void print(char[] x) {
        print(x.toString() + "");
    }

    public void println(char[] x) {
        print(x.toString() + "\n");
    }

    public void println(String x) {
        print(x + "\n");
    }

    public void print(Object x) {
        print(x + "");
    }

    public void println(Object x) {
        print(x + "\n");
    }

    public void println() {
        print("\n");
    }

    public void write(byte[] buffer, int offset, int length) {
        print(java.util.Arrays.copyOfRange(buffer, offset, offset + length).toString());
    }

    public void write(int b) {
        print(new Character((char) b) + "");
    }
} // /:~
