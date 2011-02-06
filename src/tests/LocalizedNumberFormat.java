/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 *
 * @author dmn
 */
public class LocalizedNumberFormat {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        double k = 0.1d;
        for (int i = 0; i < 1000; i++) {
            k += 0.1d;
        }
        System.out.println("" + k);


        Locale loc = Locale.getDefault();
        NumberFormat f = NumberFormat.getInstance(loc);
        if (f instanceof DecimalFormat) {
            System.out.println("ssssss");
            ((DecimalFormat) f).setDecimalSeparatorAlwaysShown(true);
            DecimalFormatSymbols dfs = ((DecimalFormat) f).getDecimalFormatSymbols();
            dfs.setDecimalSeparator(',');
            ((DecimalFormat) f).setDecimalFormatSymbols(dfs);
            //Locale.setDefault(loc);

            Double a = 6.7d;
            Double b = Double.parseDouble("3.1");
            System.out.println("liczba " + a + " # " + b + " # locale: " + NumberFormat.getNumberInstance().format(a));

        }

        BigDecimal value = null;
        String valuestr = "3,2";
        try {
            try {
                //value = BigDecimal.valueOf( Double.valueOf(str));
                value = new BigDecimal(valuestr.replace(',', '.'));
            } catch (NumberFormatException ex) {
                throw new NumberFormatException(valuestr);
            }
        } finally {
            System.out.println("bigdecimal= " + value);
        }
    }
}
