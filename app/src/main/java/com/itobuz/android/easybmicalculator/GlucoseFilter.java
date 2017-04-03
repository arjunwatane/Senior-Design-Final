package com.itobuz.android.easybmicalculator;

/**
 * Created by Arjun on 1/30/17.
 */

public class GlucoseFilter
{

    /** Function to calculate first derivative of the spectrum **/
    public double[] firstDerivative(double glucose_data[]){
        Derive derivation = new Derive();
        double[] result =  derivation.firstOrderDerivative(1.0, glucose_data, 2);
        //Log.d("Derived Result: ", Arrays.toString(result));
        return result;
    }

    public double[][] firstDerivative(double glucose_data[][]){
        double results[][] = new double[glucose_data.length][glucose_data[0].length];
        for(int i = 0; i < glucose_data.length; i ++) {
            results[i] = firstDerivative(glucose_data[i]);
        }
        return results;
    }


    /** Function to smooth the spectrum by using a moving window **/
    public double[] smooth(double glucose_data[]){
        int denominator = 3;
        double sum;
        double average[] = new double[glucose_data.length-denominator+1];

        for(int i = 0; i < average.length; i ++) {
            sum = 0;

            for(int k = i; k < denominator+i; k ++)
                sum += glucose_data[k];
            average[i] = sum/denominator;
        }


        return average;
    }

    public double[][] smooth(double glucose_data[][]){
        double results[][] = new double[glucose_data.length][glucose_data[0].length];
        for(int i = 0; i  < glucose_data.length; i ++){
            results[i] = smooth(glucose_data[i]);
        }
        return results;
    }

    public double [] polynomialFit(double glucose_data[][], double gluc_value_arr[]){

        PolynomialFit polyfit = new PolynomialFit(2);
        int sum;
        double average;
        double[] results= new double[glucose_data.length];
        for(int i=0; i < glucose_data.length; i++)
        {
            sum = 0;

            for(int j = 0; j < glucose_data[i].length; j ++){
                sum += glucose_data[i][j];
            }
            average = sum / glucose_data[i].length;
            results[i] = average;
        }

        /*
        double x_index[] = new double[results.length];
        for(int i=0; i < results.length; i++)
            x_index[i] = (double)i; */

        polyfit.fit(gluc_value_arr,results);
        polyfit.removeWorstFit();
        double coefficients[] = polyfit.getCoef();

        return coefficients;

    }


    public double polynomialFitSkin(double skin_data[][], double gluc_value_arr[]){

        PolynomialFit polyfit = new PolynomialFit(1);
        int sum;
        double average;
        double[] results= new double[skin_data.length];
        for(int i=0; i < skin_data.length; i++)
        {
            sum = 0;

            for(int j = 0; j < skin_data[i].length; j ++){
                sum += skin_data[i][j];
            }
            average = sum / skin_data[i].length;
            results[i] = average;
        }

        /*
        double x_index[] = new double[results.length];
        for(int i=0; i < results.length; i++)
            x_index[i] = (double)i; */

        polyfit.fit(gluc_value_arr,results);
        polyfit.removeWorstFit();
        double coefficients[] = polyfit.getCoef();

        return coefficients[0];

    }



}
