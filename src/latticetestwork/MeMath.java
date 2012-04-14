/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.util.ArrayList;

/**
 *
 * @author mewer12
 */
public class MeMath {
    public static double sqr(double a) {
        return a * a;
    }
    
    /**
     * Checks to see if a and b are pretty equal - to "digits" binary "decimal" places, for simplicity.
     * My simplicity, anyway.
     * @param a
     * @param b
     * @param digits
     * @return 
     */
    public static boolean prettyEqual(double a, double b, int binDigits) {
        return (((int) (a * (1 << binDigits))) == ((int) (b * (1 << binDigits))));
    }

    /**
     * Checks to see if a is pretty much equal to 0 - to "digits" binary "decimal" places, for simplicity.
     * My simplicity, anyway.
     * @param a
     * @param digits
     * @return 
     */
    public static boolean prettyZero(double a, int binDigits) {
        return (((int) (a * (1 << binDigits))) == 0);
    }
    
    public static long factorial(int a) {
        long product = a;
        for (int i = a - 1; i > 1; i--) {
            product *= i;
        }
        return product;
    }
    
    public int mod(int x, int n) {
        return ((x % n) + n) % n;
    }

    public double mod(double x, double n) {
        return ((x % n) + n) % n;
    }
}
