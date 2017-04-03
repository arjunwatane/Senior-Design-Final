package com.itobuz.android.easybmicalculator;


/**
 * Created by Arjun on 1/11/17.
 */

///////////////////////////////////////////////////////////////////////////
//                                                                       //
// Program file name: Deriv.java                                          //
//                                                                       //
// © Tao Pang 2006                                                       //
//                                                                       //
// Last modified: January 18, 2006                                       //
//                                                                       //
// (1) This Java program is part of the book, "An Introduction to        //
//     Computational Physics, 2nd Edition," written by Tao Pang and      //
//     published by Cambridge University Press on January 19, 2006.      //
//                                                                       //
// (2) No warranties, express or implied, are made for this program.     //
//                                                                       //
///////////////////////////////////////////////////////////////////////////

// An example of evaluating the derivatives with the
// 3-point formulas for f(x)=sin(x).

public class Derive {
    // static final int n = 100, m = 5;
 /*   public static void main(String argv[]) {
        double[] x = new double[n+1];
        double[] f = new double[n+1];
        double[] f1 = new double[n+1];
        double[] f2 = new double[n+1];

        // Assign constants, data points, and function
        int k = 2;
        double h = Math.PI/(2*n);
        for (int i=0; i<=n; ++i) {
            x[i]  = h*i;
            f[i]  = Math.sin(x[i]);
        }

        // Calculate 1st-order and 2nd-order derivatives
        f1 = firstOrderDerivative(h, f, k);
        f2 = secondOrderDerivative(h, f, k);

        // Output the result in every m data points
        for (int i=0; i<=n; i+=m) {
            double df1 = f1[i]-Math.cos(x[i]);
            double df2 = f2[i]+Math.sin(x[i]);
            System.out.println("x = " + x[i]);
            System.out.println("f'(x) = " + f1[i]);
            System.out.println("Error in f'(x): " + df1);
            System.out.println("f''(x) = " + f2[i]);
            System.out.println("Error in f''(x): " + df2);
            System.out.println();
        }
    }*/

// Method for the 1st-order derivative with the 3-point
// formula.  Extrapolations are made at the boundaries.

    public static double[] firstOrderDerivative(double h,
                                                double f[], int k) {
        int n = f.length-1;
        double[] y = new double[n+1];
        double[] xl = new double[k+1];
        double[] fl = new double[k+1];
        double[] fr = new double[k+1];

// Evaluate the derivative at nonboundary points
        for (int i=1; i<n; ++i)
            y[i] = (f[i+1]-f[i-1])/(2*h);

        // Lagrange-extrapolate the boundary points
        for (int i=1; i<=(k+1); ++i) {
            xl[i-1] = h*i;
            fl[i-1] = y[i];
            fr[i-1] = y[n-i];
        }
        y[0] = aitken(0, xl, fl);
        y[n] = aitken(0, xl, fr);
        return y;
    }

// Method for the 2nd-order derivative with the 3-point
// formula.  Extrapolations are made at the boundaries.

    public static double[] secondOrderDerivative(double h,
                                                 double[] f, int k) {
        int n = f.length-1;
        double[] y = new double[n+1];
        double[] xl = new double[k+1];
        double[] fl = new double[k+1];
        double[] fr = new double[k+1];

// Evaluate the derivative at nonboundary points
        for (int i=1; i<n; ++i) {
            y[i] = (f[i+1]-2*f[i]+f[i-1])/(h*h);
        }

        // Lagrange-extrapolate the boundary points
        for (int i=1; i<=(k+1); ++i) {
            xl[i-1] = h*i;
            fl[i-1] = y[i];
            fr[i-1] = y[n-i];
        }
        y[0] = aitken(0, xl, fl);
        y[n] = aitken(0, xl, fr);
        return y;
    }

// The Aitken method for the Lagrange interpolation.

    public static double aitken(double x, double xi[],
                                double fi[]) {
        int n = xi.length-1;
        double ft[] = (double[]) fi.clone();
        for (int i=0; i<n; ++i) {
            for (int j=0; j<n-i; ++j) {
                ft[j] = (x-xi[j])/(xi[i+j+1]-xi[j])*ft[j+1]
                        +(x-xi[i+j+1])/(xi[j]-xi[i+j+1])*ft[j];
            }
        }
        return ft[0];
    }
}
