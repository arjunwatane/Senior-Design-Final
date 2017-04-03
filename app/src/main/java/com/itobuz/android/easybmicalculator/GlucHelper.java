package com.itobuz.android.easybmicalculator;

import java.text.DecimalFormat;

/**
 * Created by Debasis on 21-10-2016.
 */

public class GlucHelper {
    /**
     *
     * @param value double that is formatted
     * @return double that has 1 decimal place
     */
    private double format ( double value) {
        if ( value != 0){
            DecimalFormat df = new DecimalFormat("###.#");
            //if language change
            String my_str = String.valueOf(df.format(value));
            String my_new_str = my_str.replaceAll(",", ".");

            return Double.valueOf(my_new_str);
        } else {
            return -1;
        }
    }

    /**
     *
     * @param lb - pounds
     * @return kg rounded to 1 decimal place
     */
    public double lbToKgConverter(double lb) {
        return format(lb * 0.45359237 );
    }

    /**
     *
     * @param kg - kilograms
     * @return lb rounded to 1 decimal place
     */
    public double kgToLbConverter(double kg) {
        return format(kg * 2.20462262);
    }

    /**
     *
     * @param cm - centimeters
     * @return feet rounded to 1 decimal place
     */
    public double cmToFeetConverter(double cm) {
        return format(cm * 0.032808399 );
    }

    /**
     *
     * @param feet - feet
     * @return centimeters rounded to 1 decimal place
     */
    public double feetToCmConverter(double feet) {
        return format(feet * 30.48 );
    }

    /**
     *
     * @param feet - feet
     * @param inch - inch
     * @return centimeters rounded to 1 decimal place
     */
    public double feetInchToCmConverter(double feet, double inch) {
        return format( (feet * 30.48)+(inch*2.54) );
    }

    /**
     *
     * @param glu (Glucose)
     * @return Glucose classification based on the glu number
     */
    public String getGlucClassification (double glu) {
        if (glu <= 0) return "Unknown";
        String classification;

        if (glu < 70.0) {
            classification = "Low";
        } else if (glu < 110) {
            classification = "Normal";
        } else if (glu < 130) {
            classification = "High";
        } else {
            classification = "Dangerously High";
        }

        return classification;
    }
}