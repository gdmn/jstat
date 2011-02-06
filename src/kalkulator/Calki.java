/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author dmn
 */
public class Calki {

    public static double simpsmpl(ElementWyrazenia... args) {
        double eps = args.length > 3 ? ((Liczba) ((WyrazeniePostfix) args[3]).getValue()).getValueAsDouble() : 0.000000001;
        int n = args.length > 4 ? ((Liczba) ((WyrazeniePostfix) args[4]).getValue()).getValueAsInt() : 8;
        return new Calki().simpsonsimple(
                ((Liczba) ((WyrazeniePostfix) args[0]).getValue()).getValueAsDouble(),
                ((Liczba) ((WyrazeniePostfix) args[1]).getValue()).getValueAsDouble(),
                (WyrazeniePostfix) args[2],
                eps,
                n,
                0);
    }

    public static double laguerre(ElementWyrazenia... args) {
        System.out.println("#DEBUG: laguerre(" + Arrays.asList(args).toString() + ")");
        return new Calki().gausslaguerre((WyrazeniePostfix) args[0]);
    }
	
    private double zmiennaXval;
    private FunkcjaInterface zmiennaX = new FunkcjaInterface() {

        public String toString() {
            return "X";
        }

        public int getArgumentsCount() {
            return 0;
        }

        public ElementWyrazenia getValue(ElementWyrazenia[] args) throws InvalidArgumentException {
            return new Liczba(zmiennaXval);
        }
    };

    public double simpsonsimple(double a, double b,
            WyrazeniePostfix f,
            double eps, int n, int st) {
        /*
        {---------------------------------------------------------------------------}
        {                                                                           }
        {  The function Simpsonsimple finds an approximate value of the integral    }
        {  from a simple integrand f(x) in a finite interval [a,b] by Simpson's     }
        {  method.                                                                  }
        {  Data:                                                                    }
        {    a,b - the ends of the integration interval,                            }
        {    f   - a Turbo Pascal function which for the given x calculates the     }
        {          value of integrand f(x).                                         }
        {    eps - relative error for calculating the integral,                     }
        {    n   - a positive integer which limits the calculations (the            }
        {          calculations are finished if the length of subintervals,         }
        {          evaluated in the method, are less than (b-a)/3^n).               }
        {          Notes: 1) The condition 1<=n<=9 should be fulfilled.             }
        {                 2) If the relative error is not achieved and the length   }
        {                    of subintervals are less than (b-a)/3^n, the function  }
        {                    Simpsonsimple yields the last approximation found.     }
        {  Result:                                                                  }
        {    Simpsonsimple(a,b,f,eps,n,st) - approximate value of the integral.     }
        {  Other parameters:                                                        }
        {    st - a variable which within the function Simpsonsimple is assigned    }
        {         the value of:                                                     }
        {           1, if the condition 1<=n<=9 is not fulfilled,                   }
        {           0, otherwise.                                                   }
        {         Note: If st=1, then Simpsonsimple(a,b,f,eps,n,st) is not          }
        {               calculated.                                                 }
        {  Unlocal identifier:                                                      }
        {    fx - a procedural-type identifier defined as follows                   }
        {           type fx = function (x : Extended) : Extended;                   }
        {  Note: A function passed as a parameter should be declared with a far     }
        {        directive or compiled in the $F+ state.                            }
        {                                                                           }
        {---------------------------------------------------------------------------}
         */
        FunkcjaInterface[] oldFunkcjaInterface = f.getUserArrayOfFunctions();
        f.setUserArrayOfFunctions(new FunkcjaInterface[] {
            zmiennaX
        });

        Funkcja zmiennaXfunkcja = new Funkcja("X", f.getUserArrayOfFunctions());

        ArrayList<ElementWyrazenia> oldMyElements = new ArrayList<ElementWyrazenia>(f.myElements.size());
        for (int i = 0; i < f.myElements.size(); i++) {
            oldMyElements.add(f.myElements.get(i));
            if (f.myElements.get(i).toString().equals("X")) {
                //f.myElements.add(i, (ElementWyrazenia)zmiennaX);
                f.myElements.set(i, zmiennaXfunkcja);
            } else {
            }
        }
        int k, k1, lev;
        double dx, sum, sum1, x0, x1, x2;
        {
            if ((n >= 1) && (n <= 9)) {
                st = 0;
                dx = b - a;
                sum = 0;
                lev = 0;
                k1 = 1;
                do {
                    lev = lev + 1;
                    sum1 = sum;
                    dx = dx / 3;
                    x0 = a;
                    sum = 0;
                    for (k = 1; k <= k1; k++) {
                        x1 = x0 + dx;
                        x2 = x1 + dx;
                        //x0 = f(x0)+4*f(x0+0.5*dx)+2*f(x1)+4*f(x1+0.5*dx);
                        //x1 = 2*f(x2)+4*f(x2+0.5*dx)+f(x2+dx);
                        double x0_copy = x0;
                        x0 = 0;
                        zmiennaXval = x0_copy;
                        x0 += ((Liczba) f.getValue()).getValueAsDouble();
                        zmiennaXval = x0_copy + 0.5 * dx;
                        x0 += 4 * ((Liczba) f.getValue()).getValueAsDouble();
                        zmiennaXval = x1;
                        x0 += 2 * ((Liczba) f.getValue()).getValueAsDouble();
                        zmiennaXval = x1 + 0.5 * dx;
                        x0 += 4 * ((Liczba) f.getValue()).getValueAsDouble();
                        x1 = 0;
                        zmiennaXval = x2;
                        x1 += 2 * ((Liczba) f.getValue()).getValueAsDouble();
                        zmiennaXval = x2 + 0.5 * dx;
                        x1 += 4 * ((Liczba) f.getValue()).getValueAsDouble();
                        zmiennaXval = x2 + dx;
                        x1 += ((Liczba) f.getValue()).getValueAsDouble();
                        //
                        sum = sum + dx * (x0 + x1) / 6;
                        x0 = x2 + dx;
                    }
                    k1 = 3 * k1;
                } while (!((lev > 1) && (Math.abs(sum - sum1) <= eps * sum) || (lev == n)));
                //result = sum;
                f.myElements = oldMyElements;
                f.setUserArrayOfFunctions(oldFunkcjaInterface);
                return sum;
            } else {
                st = 1;
            }
            f.myElements = oldMyElements;
            f.setUserArrayOfFunctions(oldFunkcjaInterface);
            return 0;
        }
    //TODO: dodaje na początek stare funkcje
    }

    /*                                                                           */
    /*  The function GaussLaguerre calculates an approximate value of the        */
    /*  integral from e^(-x)*f(x) in the interval [0,infinity) by the Gauss-     */
    /*  -Laguerre quadrature formula.                                            */
    /*  Data:                                                                    */
    /*    f - a Turbo Pascal function which for the given x calculates the value */
    /*        of f(x) occurring in the integrand,                                */
    /*    n - number of nodes minus 1 (the nodes are numbered from 0 to n),      */
    /*    x - an array containing roots of the Laguerre polynomial of the degree */
    /*        n+1 (the element x[k] should contain the value of the k-th root;   */
    /*        k=0,1,...,n).                                                      */
    /*  Result:                                                                  */
    /*    GaussLaguerre(f,n,x,st) - approximate value of the integral.           */
    /*  Other parameters:                                                        */
    /*    st - a variable which within the function GaussLaguerre is assigned    */
    /*         the value of:                                                     */
    /*           1, if n<1,                                                      */
    /*           0, otherwise.                                                   */
    /*         Note: If st=1, then GaussLaguerre(f,n,x,st) is not calculated.    */
    /*  Unlocal identifiers:                                                     */
    /*    vector - a type identifier of ext}ed array [q0..qn], where q0<=0 and */
    /*             qn>=n,                                                        */
    /*    fx     - a procedural-type identifier defined as follows               */
    /*               type fx = function (x : Ext}ed) : Ext}ed;               */
    /*  Note: A function passed as a parameter should be declared with the far   */
    /*        directive or compiled in the $F+ state.                            */
    /*                                                                           */
    /*---------------------------------------------------------------------------*/
    private double gausslaguerre(WyrazeniePostfix f) {
        double ar, l0, l1, l2=0, s, x1;
        int st;
        double[] x = new double[] {
            0.1523222277318082,
            0.807220022742256,
            2.005125155619347,
            3.783473973331233,
            6.204956777876613,
            9.372985251687576,
            13.46623691109209,
            18.83359778899170,
            26.37407189092738
        };
        int n = x.length;
        FunkcjaInterface[] oldFunkcjaInterface = f.getUserArrayOfFunctions();
        f.setUserArrayOfFunctions(new FunkcjaInterface[] {
            zmiennaX
        });

        Funkcja zmiennaXfunkcja = new Funkcja("X", f.getUserArrayOfFunctions());

        ArrayList<ElementWyrazenia> oldMyElements = new ArrayList<ElementWyrazenia>(f.myElements.size());
        for (int i = 0; i < f.myElements.size(); i++) {
            oldMyElements.add(f.myElements.get(i));
            if (f.myElements.get(i).toString().equals("X")) {
                //f.myElements.add(i, (ElementWyrazenia)zmiennaX);
                f.myElements.set(i, zmiennaXfunkcja);
            } else {
            }
        }
        st = 0;
        s = 0d;
        ar = 1d;
        for (int k = 0; k < n; k++) {
            l0 = 1d;
            x1 = x[k];
            l1 = 1d - x1;
            for (int m = 1; m < n - 1; m++) {
                l2 = (2d * m + 1d - x1) * l1 - m * m * l0;
                l0 = l1;
                l1 = l2;
            }
            zmiennaXval = x1;
            double x1_copy = x1;
            double r = ((Liczba) f.getValue()).getValueAsDouble();
            s = s + x1_copy * r / (l1 * l1);
            //System.out.println("k="+k+" l0="+l0+" l1="+l1+" l2="+l2+" r="+r+" x1="+x1+" s="+s);
        }
        for (int k = 2; k < n; k++) {
            ar = k * ar;
        }
        f.myElements = oldMyElements;
        f.setUserArrayOfFunctions(oldFunkcjaInterface);
        return ar * ar * s / ((n + 0) * (n + 0));
    }

    public static void main(String[] args) {

        //System.out.println("" + laguerre(WyrazeniePostfix.valueOf("1", Funkcja.funkcjaX))); // =1
        System.out.println("" + laguerre(WyrazeniePostfix.valueOf("sin(x)/(x*e^x)", Funkcja.funkcjaX))); // =0.463647609000806
        System.out.println("g "+(new DecimalFormat("###.############")).format( Funkcja.funkcjagamma_lanczos(0.02) ));
    }
}
