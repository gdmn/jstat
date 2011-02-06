/*
 * Ten plik jest częścią programu JStat.
 * Autorem jest Damian Gorlach.
 * Plik źródłowy nie może być zmieniany ani rozpowszechniany bez zgody autora.
 * Wszelkie pytania należy kierować na adres mailowy: dmn@jabster.pl
 * Strona domowa programu: http://gdamian.ovh.org/jstat
 * Ostatnia modyfikacja: 25 maja 2008.
 */

package kalkulator;

/**
 * na podstawie http://www.alglib.net/specialfunctions/incompletebeta.php
 * @author dmn
 */
public class Beta {
static public final double machineepsilon = 5E-16;
static public final double maxrealnumber  = 1E300;
static public final double minrealnumber  = 1E-300;

    /*************************************************************************
Incomplete beta integral

Returns incomplete beta integral of the arguments, evaluated
from zero to x.  The function is defined as

                 x
    -            -
   | (a+b)      | |  a-1     b-1
 -----------    |   t   (1-t)   dt.
  -     -     | |
 | (a) | (b)   -
                0

The domain of definition is 0 <= x <= 1.  In this
implementation a and b are restricted to positive values.
The integral from x to 1 may be obtained by the symmetry
relation

   1 - incbet( a, b, x )  =  incbet( b, a, 1-x ).

The integral is evaluated by a continued fraction expansion
or, when b*x is small, by a power series.

ACCURACY:

Tested at uniformly distributed random points (a,b,x) with a and b
in "domain" and x between 0 and 1.
                                       Relative error
arithmetic   domain     # trials      peak         rms
   IEEE      0,5         10000       6.9e-15     4.5e-16
   IEEE      0,85       250000       2.2e-13     1.7e-14
   IEEE      0,1000      30000       5.3e-12     6.3e-13
   IEEE      0,10000    250000       9.3e-11     7.1e-12
   IEEE      0,100000    10000       8.7e-10     4.8e-11
Outputs smaller than the IEEE gradual underflow threshold
were excluded from these statistics.

Cephes Math Library, Release 2.8:  June, 2000
Copyright 1984, 1995, 2000 by Stephen L. Moshier
*************************************************************************/
static public double incompletebeta(double a, double b, double x)
{
    double result;
    double t;
    double xc;
    double w;
    double y;
    int flag;
    double sg=0;
    double big;
    double biginv;
    double maxgam;
    double minlog;
    double maxlog;

    big = 4.503599627370496e15;
    biginv = 2.22044604925031308085e-16;
    maxgam = 171.624376956302725;
    minlog = Math.log(minrealnumber);
    maxlog = Math.log(maxrealnumber);
    assert (a>0&&b>0);
//    ap_error::make_assertion(a>0&&b>0);    ap_error::make_assertion(x>=0&&x<=1);
    if( x==0 )
    {
        result = 0;
        return result;
    }
    if( x==1 )
    {
        result = 1;
        return result;
    }
    flag = 0;
    if( b*x<=1.0&&x<=0.95 )
    {
        result = incompletebetaps(a, b, x, maxgam);
        return result;
    }
    w = 1.0-x;
    if( x>a/(a+b) )
    {
        flag = 1;
        t = a;
        a = b;
        b = t;
        xc = x;
        x = w;
    }
    else
    {
        xc = w;
    }
    if( flag==1&&b*x<=1.0&&x<=0.95 )
    {
        t = incompletebetaps(a, b, x, maxgam);
        if( t<=machineepsilon )
        {
            result = 1.0-machineepsilon;
        }
        else
        {
            result = 1.0-t;
        }
        return result;
    }
    y = x*(a+b-2.0)-(a-1.0);
    if( y<0.0 )
    {
        w = incompletebetafe(a, b, x, big, biginv);
    }
    else
    {
        w = incompletebetafe2(a, b, x, big, biginv)/xc;
    }
    y = a*Math.log(x);
    t = b*Math.log(xc);
    if( a+b<maxgam&&Math.abs(y)<maxlog&&Math.abs(t)<maxlog )
    {
        t = Math.pow(xc, b);
        t = t*Math.pow(x, a);
        t = t/a;
        t = t*w;
        t = t*(gamma(a+b)/(gamma(a)*gamma(b)));
        if( flag==1 )
        {
            if( t<=machineepsilon )
            {
                result = 1.0-machineepsilon;
            }
            else
            {
                result = 1.0-t;
            }
        }
        else
        {
            result = t;
        }
        return result;
    }
    y = y+t+lngamma(a+b, sg)-lngamma(a, sg)-lngamma(b, sg);
    y = y+Math.log(w/a);
    if( y<minlog )
    {
        t = 0.0;
    }
    else
    {
        t = Math.exp(y);
    }
    if( flag==1 )
    {
        if( t<=machineepsilon )
        {
            t = 1.0-machineepsilon;
        }
        else
        {
            t = 1.0-t;
        }
    }
    result = t;
    return result;
}


/*************************************************************************
Inverse of imcomplete beta integral

Given y, the function finds x such that

 incbet( a, b, x ) = y .

The routine performs interval halving or Newton iterations to find the
root of incbet(a,b,x) - y = 0.


ACCURACY:

                     Relative error:
               x     a,b
arithmetic   domain  domain  # trials    peak       rms
   IEEE      0,1    .5,10000   50000    5.8e-12   1.3e-13
   IEEE      0,1   .25,100    100000    1.8e-13   3.9e-15
   IEEE      0,1     0,5       50000    1.1e-12   5.5e-15
With a and b constrained to half-integer or integer values:
   IEEE      0,1    .5,10000   50000    5.8e-12   1.1e-13
   IEEE      0,1    .5,100    100000    1.7e-14   7.9e-16
With a = .5, b constrained to half-integer or integer values:
   IEEE      0,1    .5,10000   10000    8.3e-11   1.0e-11

Cephes Math Library Release 2.8:  June, 2000
Copyright 1984, 1996, 2000 by Stephen L. Moshier
*************************************************************************/
static public double invincompletebeta(double a, double b, double y)
{
    double result;
    double aaa=0;
    double bbb=0;
    double y0=0;
    double d;
    double yyy=0;
    double x=0;
    double x0;
    double x1;
    double lgm=0;
    double yp;
    double di=0;
    double dithresh=0;
    double yl;
    double yh;
    double xt;
    int i;
    int rflg=0;
    int dir=0;
    int nflg;
    double s=0;
    int mainlooppos;
    int ihalve;
    int ihalvecycle;
    int newt;
    int newtcycle;
    int breaknewtcycle;
    int breakihalvecycle;

    i = 0;
    //ap_error::make_assertion(y>=0&&y<=1);
    if( y==0 )
    {
        result = 0;
        return result;
    }
    if( y==1.0 )
    {
        result = 1;
        return result;
    }
    x0 = 0.0;
    yl = 0.0;
    x1 = 1.0;
    yh = 1.0;
    nflg = 0;
    mainlooppos = 0;
    ihalve = 1;
    ihalvecycle = 2;
    newt = 3;
    newtcycle = 4;
    breaknewtcycle = 5;
    breakihalvecycle = 6;
    while(true)
    {

        //
        // start
        //
        if( mainlooppos==0 )
        {
            if( a<=1.0||b<=1.0 )
            {
                dithresh = 1.0e-6;
                rflg = 0;
                aaa = a;
                bbb = b;
                y0 = y;
                x = aaa/(aaa+bbb);
                yyy = incompletebeta(aaa, bbb, x);
                mainlooppos = ihalve;
                continue;
            }
            else
            {
                dithresh = 1.0e-4;
            }
            yp = -invnormaldistribution(y);
            if( y>0.5 )
            {
                rflg = 1;
                aaa = b;
                bbb = a;
                y0 = 1.0-y;
                yp = -yp;
            }
            else
            {
                rflg = 0;
                aaa = a;
                bbb = b;
                y0 = y;
            }
            lgm = (yp*yp-3.0)/6.0;
            x = 2.0/(1.0/(2.0*aaa-1.0)+1.0/(2.0*bbb-1.0));
            d = yp*Math.sqrt(x+lgm)/x-(1.0/(2.0*bbb-1.0)-1.0/(2.0*aaa-1.0))*(lgm+5.0/6.0-2.0/(3.0*x));
            d = 2.0*d;
            if( d<Math.log(minrealnumber) )
            {
                x = 0;
                break;
            }
            x = aaa/(aaa+bbb*Math.exp(d));
            yyy = incompletebeta(aaa, bbb, x);
            yp = (yyy-y0)/y0;
            if( Math.abs(yp)<0.2 )
            {
                mainlooppos = newt;
                continue;
            }
            mainlooppos = ihalve;
            continue;
        }

        //
        // ihalve
        //
        if( mainlooppos==ihalve )
        {
            dir = 0;
            di = 0.5;
            i = 0;
            mainlooppos = ihalvecycle;
            continue;
        }

        //
        // ihalvecycle
        //
        if( mainlooppos==ihalvecycle )
        {
            if( i<=99 )
            {
                if( i!=0 )
                {
                    x = x0+di*(x1-x0);
                    if( x==1.0 )
                    {
                        x = 1.0-machineepsilon;
                    }
                    if( x==0.0 )
                    {
                        di = 0.5;
                        x = x0+di*(x1-x0);
                        if( x==0.0 )
                        {
                            break;
                        }
                    }
                    yyy = incompletebeta(aaa, bbb, x);
                    yp = (x1-x0)/(x1+x0);
                    if( Math.abs(yp)<dithresh )
                    {
                        mainlooppos = newt;
                        continue;
                    }
                    yp = (yyy-y0)/y0;
                    if( Math.abs(yp)<dithresh )
                    {
                        mainlooppos = newt;
                        continue;
                    }
                }
                if( yyy<y0 )
                {
                    x0 = x;
                    yl = yyy;
                    if( dir<0 )
                    {
                        dir = 0;
                        di = 0.5;
                    }
                    else
                    {
                        if( dir>3 )
                        {
                            di = 1.0-(1.0-di)*(1.0-di);
                        }
                        else
                        {
                            if( dir>1 )
                            {
                                di = 0.5*di+0.5;
                            }
                            else
                            {
                                di = (y0-yyy)/(yh-yl);
                            }
                        }
                    }
                    dir = dir+1;
                    if( x0>0.75 )
                    {
                        if( rflg==1 )
                        {
                            rflg = 0;
                            aaa = a;
                            bbb = b;
                            y0 = y;
                        }
                        else
                        {
                            rflg = 1;
                            aaa = b;
                            bbb = a;
                            y0 = 1.0-y;
                        }
                        x = 1.0-x;
                        yyy = incompletebeta(aaa, bbb, x);
                        x0 = 0.0;
                        yl = 0.0;
                        x1 = 1.0;
                        yh = 1.0;
                        mainlooppos = ihalve;
                        continue;
                    }
                }
                else
                {
                    x1 = x;
                    if( rflg==1&&x1<machineepsilon )
                    {
                        x = 0.0;
                        break;
                    }
                    yh = yyy;
                    if( dir>0 )
                    {
                        dir = 0;
                        di = 0.5;
                    }
                    else
                    {
                        if( dir<-3 )
                        {
                            di = di*di;
                        }
                        else
                        {
                            if( dir<-1 )
                            {
                                di = 0.5*di;
                            }
                            else
                            {
                                di = (yyy-y0)/(yh-yl);
                            }
                        }
                    }
                    dir = dir-1;
                }
                i = i+1;
                mainlooppos = ihalvecycle;
                continue;
            }
            else
            {
                mainlooppos = breakihalvecycle;
                continue;
            }
        }

        //
        // breakihalvecycle
        //
        if( mainlooppos==breakihalvecycle )
        {
            if( x0>=1.0 )
            {
                x = 1.0-machineepsilon;
                break;
            }
            if( x<=0.0 )
            {
                x = 0.0;
                break;
            }
            mainlooppos = newt;
            continue;
        }

        //
        // newt
        //
        if( mainlooppos==newt )
        {
            if( nflg!=0 )
            {
                break;
            }
            nflg = 1;
            lgm = lngamma(aaa+bbb, s)-lngamma(aaa, s)-lngamma(bbb, s);
            i = 0;
            mainlooppos = newtcycle;
            continue;
        }

        //
        // newtcycle
        //
        if( mainlooppos==newtcycle )
        {
            if( i<=7 )
            {
                if( i!=0 )
                {
                    yyy = incompletebeta(aaa, bbb, x);
                }
                if( yyy<yl )
                {
                    x = x0;
                    yyy = yl;
                }
                else
                {
                    if( yyy>yh )
                    {
                        x = x1;
                        yyy = yh;
                    }
                    else
                    {
                        if( yyy<y0 )
                        {
                            x0 = x;
                            yl = yyy;
                        }
                        else
                        {
                            x1 = x;
                            yh = yyy;
                        }
                    }
                }
                if( x==1.0||x==0.0 )
                {
                    mainlooppos = breaknewtcycle;
                    continue;
                }
                d = (aaa-1.0)*Math.log(x)+(bbb-1.0)*Math.log(1.0-x)+lgm;
                if( d<Math.log(minrealnumber) )
                {
                    break;
                }
                if( d>Math.log(maxrealnumber) )
                {
                    mainlooppos = breaknewtcycle;
                    continue;
                }
                d = Math.exp(d);
                d = (yyy-y0)/d;
                xt = x-d;
                if( xt<=x0 )
                {
                    yyy = (x-x0)/(x1-x0);
                    xt = x0+0.5*yyy*(x-x0);
                    if( xt<=0.0 )
                    {
                        mainlooppos = breaknewtcycle;
                        continue;
                    }
                }
                if( xt>=x1 )
                {
                    yyy = (x1-x)/(x1-x0);
                    xt = x1-0.5*yyy*(x1-x);
                    if( xt>=1.0 )
                    {
                        mainlooppos = breaknewtcycle;
                        continue;
                    }
                }
                x = xt;
                if( Math.abs(d/x)<128.0*machineepsilon )
                {
                    break;
                }
                i = i+1;
                mainlooppos = newtcycle;
                continue;
            }
            else
            {
                mainlooppos = breaknewtcycle;
                continue;
            }
        }

        //
        // breaknewtcycle
        //
        if( mainlooppos==breaknewtcycle )
        {
            dithresh = 256.0*machineepsilon;
            mainlooppos = ihalve;
            continue;
        }
    }

    //
    // done
    //
    if( rflg!=0 )
    {
        if( x<=machineepsilon )
        {
            x = 1.0-machineepsilon;
        }
        else
        {
            x = 1.0-x;
        }
    }
    result = x;
    return result;
}


/*************************************************************************
Continued fraction expansion #1 for incomplete beta integral

Cephes Math Library, Release 2.8:  June, 2000
Copyright 1984, 1995, 2000 by Stephen L. Moshier
*************************************************************************/
static public double incompletebetafe(double a,
     double b,
     double x,
     double big,
     double biginv)
{
    double result;
    double xk;
    double pk;
    double pkm1;
    double pkm2;
    double qk;
    double qkm1;
    double qkm2;
    double k1;
    double k2;
    double k3;
    double k4;
    double k5;
    double k6;
    double k7;
    double k8;
    double r;
    double t;
    double ans;
    double thresh;
    int n;

    k1 = a;
    k2 = a+b;
    k3 = a;
    k4 = a+1.0;
    k5 = 1.0;
    k6 = b-1.0;
    k7 = k4;
    k8 = a+2.0;
    pkm2 = 0.0;
    qkm2 = 1.0;
    pkm1 = 1.0;
    qkm1 = 1.0;
    ans = 1.0;
    r = 1.0;
    n = 0;
    thresh = 3.0*machineepsilon;
    do
    {
        xk = -x*k1*k2/(k3*k4);
        pk = pkm1+pkm2*xk;
        qk = qkm1+qkm2*xk;
        pkm2 = pkm1;
        pkm1 = pk;
        qkm2 = qkm1;
        qkm1 = qk;
        xk = x*k5*k6/(k7*k8);
        pk = pkm1+pkm2*xk;
        qk = qkm1+qkm2*xk;
        pkm2 = pkm1;
        pkm1 = pk;
        qkm2 = qkm1;
        qkm1 = qk;
        if( qk!=0 )
        {
            r = pk/qk;
        }
        if( r!=0 )
        {
            t = Math.abs((ans-r)/r);
            ans = r;
        }
        else
        {
            t = 1.0;
        }
        if( t<thresh )
        {
            break;
        }
        k1 = k1+1.0;
        k2 = k2+1.0;
        k3 = k3+2.0;
        k4 = k4+2.0;
        k5 = k5+1.0;
        k6 = k6-1.0;
        k7 = k7+2.0;
        k8 = k8+2.0;
        if( Math.abs(qk)+Math.abs(pk)>big )
        {
            pkm2 = pkm2*biginv;
            pkm1 = pkm1*biginv;
            qkm2 = qkm2*biginv;
            qkm1 = qkm1*biginv;
        }
        if( Math.abs(qk)<biginv||Math.abs(pk)<biginv )
        {
            pkm2 = pkm2*big;
            pkm1 = pkm1*big;
            qkm2 = qkm2*big;
            qkm1 = qkm1*big;
        }
        n = n+1;
    }
    while(n!=300);
    result = ans;
    return result;
}


/*************************************************************************
Continued fraction expansion #2
for incomplete beta integral

Cephes Math Library, Release 2.8:  June, 2000
Copyright 1984, 1995, 2000 by Stephen L. Moshier
*************************************************************************/
static public double incompletebetafe2(double a,
     double b,
     double x,
     double big,
     double biginv)
{
    double result;
    double xk;
    double pk;
    double pkm1;
    double pkm2;
    double qk;
    double qkm1;
    double qkm2;
    double k1;
    double k2;
    double k3;
    double k4;
    double k5;
    double k6;
    double k7;
    double k8;
    double r;
    double t;
    double ans;
    double z;
    double thresh;
    int n;

    k1 = a;
    k2 = b-1.0;
    k3 = a;
    k4 = a+1.0;
    k5 = 1.0;
    k6 = a+b;
    k7 = a+1.0;
    k8 = a+2.0;
    pkm2 = 0.0;
    qkm2 = 1.0;
    pkm1 = 1.0;
    qkm1 = 1.0;
    z = x/(1.0-x);
    ans = 1.0;
    r = 1.0;
    n = 0;
    thresh = 3.0*machineepsilon;
    do
    {
        xk = -z*k1*k2/(k3*k4);
        pk = pkm1+pkm2*xk;
        qk = qkm1+qkm2*xk;
        pkm2 = pkm1;
        pkm1 = pk;
        qkm2 = qkm1;
        qkm1 = qk;
        xk = z*k5*k6/(k7*k8);
        pk = pkm1+pkm2*xk;
        qk = qkm1+qkm2*xk;
        pkm2 = pkm1;
        pkm1 = pk;
        qkm2 = qkm1;
        qkm1 = qk;
        if( qk!=0 )
        {
            r = pk/qk;
        }
        if( r!=0 )
        {
            t = Math.abs((ans-r)/r);
            ans = r;
        }
        else
        {
            t = 1.0;
        }
        if( t<thresh )
        {
            break;
        }
        k1 = k1+1.0;
        k2 = k2-1.0;
        k3 = k3+2.0;
        k4 = k4+2.0;
        k5 = k5+1.0;
        k6 = k6+1.0;
        k7 = k7+2.0;
        k8 = k8+2.0;
        if( Math.abs(qk)+Math.abs(pk)>big )
        {
            pkm2 = pkm2*biginv;
            pkm1 = pkm1*biginv;
            qkm2 = qkm2*biginv;
            qkm1 = qkm1*biginv;
        }
        if( Math.abs(qk)<biginv||Math.abs(pk)<biginv )
        {
            pkm2 = pkm2*big;
            pkm1 = pkm1*big;
            qkm2 = qkm2*big;
            qkm1 = qkm1*big;
        }
        n = n+1;
    }
    while(n!=300);
    result = ans;
    return result;
}


/*************************************************************************
Power series for incomplete beta integral.
Use when b*x is small and x not too close to 1.

Cephes Math Library, Release 2.8:  June, 2000
Copyright 1984, 1995, 2000 by Stephen L. Moshier
*************************************************************************/
static public double incompletebetaps(double a, double b, double x, double maxgam)
{
    double result;
    double s;
    double t;
    double u;
    double v;
    double n;
    double t1;
    double z;
    double ai;
    double sg=0;

    ai = 1.0/a;
    u = (1.0-b)*x;
    v = u/(a+1.0);
    t1 = v;
    t = u;
    n = 2.0;
    s = 0.0;
    z = machineepsilon*ai;
    while(Math.abs(v)>z)
    {
        u = (n-b)*x/n;
        t = t*u;
        v = t/(a+n);
        s = s+v;
        n = n+1.0;
    }
    s = s+t1;
    s = s+ai;
    u = a*Math.log(x);
    if( a+b<maxgam&&Math.abs(u)<Math.log(maxrealnumber) )
    {
        t = gamma(a+b)/(gamma(a)*gamma(b));
        s = s*t*Math.pow(x, a);
    }
    else
    {
        t = lngamma(a+b, sg)-lngamma(a, sg)-lngamma(b, sg)+u+Math.log(s);
        if( t<Math.log(minrealnumber) )
        {
            s = 0.0;
        }
        else
        {
            s = Math.exp(t);
        }
    }
    result = s;
    return result;
}


/*************************************************************************
Gamma function

Input parameters:
    X   -   argument

Domain:
    0 < X < 171.6
    -170 < X < 0, X is not an integer.

Relative error:
 arithmetic   domain     # trials      peak         rms
    IEEE    -170,-33      20000       2.3e-15     3.3e-16
    IEEE     -33,  33     20000       9.4e-16     2.2e-16
    IEEE      33, 171.6   20000       2.3e-15     3.2e-16

Cephes Math Library Release 2.8:  June, 2000
Original copyright 1984, 1987, 1989, 1992, 2000 by Stephen L. Moshier
Translated to AlgoPascal by Bochkanov Sergey (2005, 2006, 2007).
*************************************************************************/
static public double gamma(double x)
{
    double result;
    double p;
    double pp;
    double q;
    double qq;
    double z;
    int i;
    double sgngam;

    sgngam = 1;
    q = Math.abs(x);
    if( q>33.0 )
    {
        if( x<0.0 )
        {
            p = (int) Math.floor(q);
            i = (int) Math.round(p);
            if( i%2==0 )
            {
                sgngam = -1;
            }
            z = q-p;
            if( z>0.5 )
            {
                p = p+1;
                z = q-p;
            }
            z = q*Math.sin(Math.PI*z);
            z = Math.abs(z);
            z = Math.PI/(z*gammastirf(q));
        }
        else
        {
            z = gammastirf(x);
        }
        result = sgngam*z;
        return result;
    }
    z = 1;
    while(x>=3)
    {
        x = x-1;
        z = z*x;
    }
    while(x<0)
    {
        if( x>-0.000000001 )
        {
            result = z/((1+0.5772156649015329*x)*x);
            return result;
        }
        z = z/x;
        x = x+1;
    }
    while(x<2)
    {
        if( x<0.000000001 )
        {
            result = z/((1+0.5772156649015329*x)*x);
            return result;
        }
        z = z/x;
        x = x+1.0;
    }
    if( x==2 )
    {
        result = z;
        return result;
    }
    x = x-2.0;
    pp = 1.60119522476751861407E-4;
    pp = 1.19135147006586384913E-3+x*pp;
    pp = 1.04213797561761569935E-2+x*pp;
    pp = 4.76367800457137231464E-2+x*pp;
    pp = 2.07448227648435975150E-1+x*pp;
    pp = 4.94214826801497100753E-1+x*pp;
    pp = 9.99999999999999996796E-1+x*pp;
    qq = -2.31581873324120129819E-5;
    qq = 5.39605580493303397842E-4+x*qq;
    qq = -4.45641913851797240494E-3+x*qq;
    qq = 1.18139785222060435552E-2+x*qq;
    qq = 3.58236398605498653373E-2+x*qq;
    qq = -2.34591795718243348568E-1+x*qq;
    qq = 7.14304917030273074085E-2+x*qq;
    qq = 1.00000000000000000320+x*qq;
    result = z*pp/qq;
    return result;
//    return result;
}


/*************************************************************************
Natural logarithm of gamma function

Input parameters:
    X       -   argument

Result:
    logarithm of the absolute value of the Gamma(X).

Output parameters:
    SgnGam  -   sign(Gamma(X))

Domain:
    0 < X < 2.55e305
    -2.55e305 < X < 0, X is not an integer.

ACCURACY:
arithmetic      domain        # trials     peak         rms
   IEEE    0, 3                 28000     5.4e-16     1.1e-16
   IEEE    2.718, 2.556e305     40000     3.5e-16     8.3e-17
The error criterion was relative when the function magnitude
was greater than one but absolute when it was less than one.

The following test used the relative error criterion, though
at certain points the relative error could be much higher than
indicated.
   IEEE    -200, -4             10000     4.8e-16     1.3e-16

Cephes Math Library Release 2.8:  June, 2000
Copyright 1984, 1987, 1989, 1992, 2000 by Stephen L. Moshier
Translated to AlgoPascal by Bochkanov Sergey (2005, 2006, 2007).
*************************************************************************/
static public double lngamma(double x, double sgngam)
{
    double result;
    double a;
    double b;
    double c;
    double p;
    double q;
    double u;
    double w;
    double z;
    int i;
    double logpi;
    double ls2pi;
    double tmp=0;

    sgngam = 1;
    logpi = 1.14472988584940017414;
    ls2pi = 0.91893853320467274178;
    if( x<-34.0 )
    {
        q = -x;
        w = lngamma(q, tmp);
        p = (int) Math.floor(q);
        i = (int) Math.round(p);
        if( i%2==0 )
        {
            sgngam = -1;
        }
        else
        {
            sgngam = 1;
        }
        z = q-p;
        if( z>0.5 )
        {
            p = p+1;
            z = p-q;
        }
        z = q*Math.sin(Math.PI*z);
        result = logpi-Math.log(z)-w;
        return result;
    }
    if( x<13 )
    {
        z = 1;
        p = 0;
        u = x;
        while(u>=3)
        {
            p = p-1;
            u = x+p;
            z = z*u;
        }
        while(u<2)
        {
            z = z/u;
            p = p+1;
            u = x+p;
        }
        if( z<0 )
        {
            sgngam = -1;
            z = -z;
        }
        else
        {
            sgngam = 1;
        }
        if( u==2 )
        {
            result = Math.log(z);
            return result;
        }
        p = p-2;
        x = x+p;
        b = -1378.25152569120859100;
        b = -38801.6315134637840924+x*b;
        b = -331612.992738871184744+x*b;
        b = -1162370.97492762307383+x*b;
        b = -1721737.00820839662146+x*b;
        b = -853555.664245765465627+x*b;
        c = 1;
        c = -351.815701436523470549+x*c;
        c = -17064.2106651881159223+x*c;
        c = -220528.590553854454839+x*c;
        c = -1139334.44367982507207+x*c;
        c = -2532523.07177582951285+x*c;
        c = -2018891.41433532773231+x*c;
        p = x*b/c;
        result = Math.log(z)+p;
        return result;
    }
    q = (x-0.5)*Math.log(x)-x+ls2pi;
    if( x>100000000 )
    {
        result = q;
        return result;
    }
    p = 1/(x*x);
    if( x>=1000.0 )
    {
        q = q+((7.9365079365079365079365*0.0001*p-2.7777777777777777777778*0.001)*p+0.0833333333333333333333)/x;
    }
    else
    {
        a = 8.11614167470508450300*0.0001;
        a = -5.95061904284301438324*0.0001+p*a;
        a = 7.93650340457716943945*0.0001+p*a;
        a = -2.77777777730099687205*0.001+p*a;
        a = 8.33333333333331927722*0.01+p*a;
        q = q+a/x;
    }
    result = q;
    return result;
}


static public double gammastirf(double x)
{
    double result;
    double y;
    double w;
    double v;
    double stir;

    w = 1/x;
    stir = 7.87311395793093628397E-4;
    stir = -2.29549961613378126380E-4+w*stir;
    stir = -2.68132617805781232825E-3+w*stir;
    stir = 3.47222221605458667310E-3+w*stir;
    stir = 8.33333333333482257126E-2+w*stir;
    w = 1+w*stir;
    y = Math.exp(x);
    if( x>143.01608 )
    {
        v = Math.pow(x, 0.5*x-0.25);
        y = v*(v/y);
    }
    else
    {
        y = Math.pow(x, x-0.5)/y;
    }
    result = 2.50662827463100050242*y*w;
    return result;
}

/*************************************************************************
Error function

The integral is

                          x
                           -
                2         | |          2
  erf(x)  =  --------     |    Math.exp( - t  ) dt.
             Math.sqrt(pi)   | |
                         -
                          0

For 0 <= |x| < 1, erf(x) = x * P4(x**2)/Q5(x**2); otherwise
erf(x) = 1 - erfc(x).


ACCURACY:

                     Relative error:
arithmetic   domain     # trials      peak         rms
   IEEE      0,1         30000       3.7e-16     1.0e-16

Cephes Math Library Release 2.8:  June, 2000
Copyright 1984, 1987, 1988, 1992, 2000 by Stephen L. Moshier
*************************************************************************/
static public double erf(double x)
{
    double result;
    double xsq;
    double s;
    double p;
    double q;

    s = Math.signum(x);
    x = Math.abs(x);
    if( x<0.5 )
    {
        xsq = x*x;
        p = 0.007547728033418631287834;
        p = 0.288805137207594084924010+xsq*p;
        p = 14.3383842191748205576712+xsq*p;
        p = 38.0140318123903008244444+xsq*p;
        p = 3017.82788536507577809226+xsq*p;
        p = 7404.07142710151470082064+xsq*p;
        p = 80437.3630960840172832162+xsq*p;
        q = 0.0;
        q = 1.00000000000000000000000+xsq*q;
        q = 38.0190713951939403753468+xsq*q;
        q = 658.070155459240506326937+xsq*q;
        q = 6379.60017324428279487120+xsq*q;
        q = 34216.5257924628539769006+xsq*q;
        q = 80437.3630960840172826266+xsq*q;
        result = s*1.1283791670955125738961589031*x*p/q;
        return result;
    }
    if( x>=10 )
    {
        result = s;
        return result;
    }
    result = s*(1-erfc(x));
    return result;
}


/*************************************************************************
Complementary error function

 1 - erf(x) =

                          inf.
                            -
                 2         | |          2
  erfc(x)  =  --------     |    Math.exp( - t  ) dt
              Math.sqrt(pi)   | |
                          -
                           x


For small x, erfc(x) = 1 - erf(x); otherwise rational
approximations are computed.


ACCURACY:

                     Relative error:
arithmetic   domain     # trials      peak         rms
   IEEE      0,26.6417   30000       5.7e-14     1.5e-14

Cephes Math Library Release 2.8:  June, 2000
Copyright 1984, 1987, 1988, 1992, 2000 by Stephen L. Moshier
*************************************************************************/
static public double erfc(double x)
{
    double result;
    double p;
    double q;

    if( x<0 )
    {
        result = 2-erfc(-x);
        return result;
    }
    if( x<0.5 )
    {
        result = 1.0-erf(x);
        return result;
    }
    if( x>=10 )
    {
        result = 0;
        return result;
    }
    p = 0.0;
    p = 0.5641877825507397413087057563+x*p;
    p = 9.675807882987265400604202961+x*p;
    p = 77.08161730368428609781633646+x*p;
    p = 368.5196154710010637133875746+x*p;
    p = 1143.262070703886173606073338+x*p;
    p = 2320.439590251635247384768711+x*p;
    p = 2898.0293292167655611275846+x*p;
    p = 1826.3348842295112592168999+x*p;
    q = 1.0;
    q = 17.14980943627607849376131193+x*q;
    q = 137.1255960500622202878443578+x*q;
    q = 661.7361207107653469211984771+x*q;
    q = 2094.384367789539593790281779+x*q;
    q = 4429.612803883682726711528526+x*q;
    q = 6089.5424232724435504633068+x*q;
    q = 4958.82756472114071495438422+x*q;
    q = 1826.3348842295112595576438+x*q;
    result = Math.exp(-x*x)*p/q;
    return result;
}


/*************************************************************************
Normal distribution function

Returns the area under the Gaussian probability density
function, integrated from minus infinity to x:

                           x
                            -
                  1        | |          2
   ndtr(x)  = ---------    |    Math.exp( - t /2 ) dt
              Math.sqrt(2pi)  | |
                          -
                         -inf.

            =  ( 1 + erf(z) ) / 2
            =  erfc(z) / 2

where z = x/Math.sqrt(2). Computation is via the functions
erf and erfc.


ACCURACY:

                     Relative error:
arithmetic   domain     # trials      peak         rms
   IEEE     -13,0        30000       3.4e-14     6.7e-15

Cephes Math Library Release 2.8:  June, 2000
Copyright 1984, 1987, 1988, 1992, 2000 by Stephen L. Moshier
*************************************************************************/
static public double normaldistribution(double x)
{
    double result;

    result = 0.5*(erf(x/1.41421356237309504880)+1);
    return result;
}


/*************************************************************************
Inverse of the error function

Cephes Math Library Release 2.8:  June, 2000
Copyright 1984, 1987, 1988, 1992, 2000 by Stephen L. Moshier
*************************************************************************/
static public double inverf(double e)
{
    double result;

    result = invnormaldistribution(0.5*(e+1))/Math.sqrt(2d);
    return result;
}


/*************************************************************************
Inverse of Normal distribution function

Returns the argument, x, for which the area under the
Gaussian probability density function (integrated from
minus infinity to x) is equal to y.


For small arguments 0 < y < Math.exp(-2), the program computes
z = Math.sqrt( -2.0 * Math.log(y) );  then the approximation is
x = z - Math.log(z)/z  - (1/z) P(1/z) / Q(1/z).
There are two rational functions P/Q, one for 0 < y < Math.exp(-32)
and the other for y up to Math.exp(-2).  For larger arguments,
w = y - 0.5, and  x/Math.sqrt(2pi) = w + w**3 R(w**2)/S(w**2)).

ACCURACY:

                     Relative error:
arithmetic   domain        # trials      peak         rms
   IEEE     0.125, 1        20000       7.2e-16     1.3e-16
   IEEE     3e-308, 0.135   50000       4.6e-16     9.8e-17

Cephes Math Library Release 2.8:  June, 2000
Copyright 1984, 1987, 1988, 1992, 2000 by Stephen L. Moshier
*************************************************************************/
static public double invnormaldistribution(double y0)
{
    double result;
    double expm2;
    double s2pi;
    double x;
    double y;
    double z;
    double y2;
    double x0;
    double x1;
    int code;
    double p0;
    double q0;
    double p1;
    double q1;
    double p2;
    double q2;

    expm2 = 0.13533528323661269189;
    s2pi = 2.50662827463100050242;
    if( y0<=0 )
    {
        result = -maxrealnumber;
        return result;
    }
    if( y0>=1 )
    {
        result = maxrealnumber;
        return result;
    }
    code = 1;
    y = y0;
    if( y>1.0-expm2 )
    {
        y = 1.0-y;
        code = 0;
    }
    if( y>expm2 )
    {
        y = y-0.5;
        y2 = y*y;
        p0 = -59.9633501014107895267;
        p0 = 98.0010754185999661536+y2*p0;
        p0 = -56.6762857469070293439+y2*p0;
        p0 = 13.9312609387279679503+y2*p0;
        p0 = -1.23916583867381258016+y2*p0;
        q0 = 1;
        q0 = 1.95448858338141759834+y2*q0;
        q0 = 4.67627912898881538453+y2*q0;
        q0 = 86.3602421390890590575+y2*q0;
        q0 = -225.462687854119370527+y2*q0;
        q0 = 200.260212380060660359+y2*q0;
        q0 = -82.0372256168333339912+y2*q0;
        q0 = 15.9056225126211695515+y2*q0;
        q0 = -1.18331621121330003142+y2*q0;
        x = y+y*y2*p0/q0;
        x = x*s2pi;
        result = x;
        return result;
    }
    x = Math.sqrt(-2.0*Math.log(y));
    x0 = x-Math.log(x)/x;
    z = 1.0/x;
    if( x<8.0 )
    {
        p1 = 4.05544892305962419923;
        p1 = 31.5251094599893866154+z*p1;
        p1 = 57.1628192246421288162+z*p1;
        p1 = 44.0805073893200834700+z*p1;
        p1 = 14.6849561928858024014+z*p1;
        p1 = 2.18663306850790267539+z*p1;
        p1 = -1.40256079171354495875*0.1+z*p1;
        p1 = -3.50424626827848203418*0.01+z*p1;
        p1 = -8.57456785154685413611*0.0001+z*p1;
        q1 = 1;
        q1 = 15.7799883256466749731+z*q1;
        q1 = 45.3907635128879210584+z*q1;
        q1 = 41.3172038254672030440+z*q1;
        q1 = 15.0425385692907503408+z*q1;
        q1 = 2.50464946208309415979+z*q1;
        q1 = -1.42182922854787788574*0.1+z*q1;
        q1 = -3.80806407691578277194*0.01+z*q1;
        q1 = -9.33259480895457427372*0.0001+z*q1;
        x1 = z*p1/q1;
    }
    else
    {
        p2 = 3.23774891776946035970;
        p2 = 6.91522889068984211695+z*p2;
        p2 = 3.93881025292474443415+z*p2;
        p2 = 1.33303460815807542389+z*p2;
        p2 = 2.01485389549179081538*0.1+z*p2;
        p2 = 1.23716634817820021358*0.01+z*p2;
        p2 = 3.01581553508235416007*0.0001+z*p2;
        p2 = 2.65806974686737550832*0.000001+z*p2;
        p2 = 6.23974539184983293730*0.000000001+z*p2;
        q2 = 1;
        q2 = 6.02427039364742014255+z*q2;
        q2 = 3.67983563856160859403+z*q2;
        q2 = 1.37702099489081330271+z*q2;
        q2 = 2.16236993594496635890*0.1+z*q2;
        q2 = 1.34204006088543189037*0.01+z*q2;
        q2 = 3.28014464682127739104*0.0001+z*q2;
        q2 = 2.89247864745380683936*0.000001+z*q2;
        q2 = 6.79019408009981274425*0.000000001+z*q2;
        x1 = z*p2/q2;
    }
    x = x0-x1;
    if( code!=0 )
    {
        x = -x;
    }
    result = x;
    return result;
}


}
