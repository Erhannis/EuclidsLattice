/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author CORNELL-COLLEGE\mewer12
 */
public class CrossProducts {

    private HashMap<Integer, ArrayList<Integer>> productsArray;

    public int crossSign(int edgeLength, int index) {
        if (!productsArray.containsKey(edgeLength)) {
            computeProducts(edgeLength);
        }
        return productsArray.get(edgeLength).get(index) * ((2 * (edgeLength % 2)) - 1);
    }

    public int detSign(int edgeLength, int index) {
        if (!productsArray.containsKey(edgeLength)) {
            computeProducts(edgeLength);
        }
        return productsArray.get(edgeLength).get(index);
    }

    public int factorial(int numIn) {
        int result = 1;
        for (int i = 2; i <= numIn; i++) {
            result *= i;
        }
        return result;
    }

    private void computeProducts(int edgeLength) {
        int max = factorial(edgeLength);
        ArrayList<Integer> bucket = new ArrayList<Integer>(max);
        for (int i = 0; i < max; i++) {
            bucket.add(0);
        }
        productsArray.put(edgeLength, bucket);
        /*for (int i = 0; i < max; i++) {

        }*/
        //double[] result = new double[p.length + 1];
        boolean[] taken = new boolean[edgeLength];     // This one is whether a row has been taken.
        int[] takenSpot = new int[edgeLength];     // This one is which row a column took.
        for (int i = 0; i < edgeLength; i++) {
            taken[i] = false;
            takenSpot[i] = -1;
        }
        int[] state = {0};
        int[] position = {0};
        crossRecurse(taken, takenSpot, 0, state, edgeLength, new int[1], position);
    }

/*    public void cross(double[][] p) throws Exception {
        // REMEMBER TO DEAL WITH THE MATRIX ON ITS SIDE!
        // Should work to swap x and y.

        double[] result = new double[p.length + 1];
        boolean[] taken = new boolean[p.length + 1];     // This one is whether a row has been taken.
        int[] takenSpot = new int[p.length + 1];     // This one is which row a column took.
        for (int i = 0; i < p.length; i++) {
            if (p[i].length != (p.length + 1)) {
                throw new Exception("Must give n-1 vectors of n length!");
            }
        }
        for (int i = 0; i < (p.length + 1); i++) {
            taken[i] = false;
            takenSpot[i] = -1;
        }
        int[] state = {0};
        int[] position = {0};
        crossRecurse(taken, takenSpot, 0, state, p.length + 1, new int[1], position);
//        return result;
    }
*/
    private void crossRecurse(boolean[] taken, int[] takenSpot, int level, int[] state, int edgeLength, int[] curComponent, int[] position) {
        //prod take next available row, incmod state, if level == edgeLength +/- product, untake, return
        for (int y = 0; y < edgeLength; y++) {
            if (!taken[y]) {
                if (level == (edgeLength - 1)) {
                    takenSpot[level] = y;
                    // Check flips and store result
                    productsArray.get(edgeLength).set(position[0], getPermutationSign(edgeLength, takenSpot));
                    position[0]++;
                    break;
                } else {
                    taken[y] = true;
                    takenSpot[level] = y;
                    crossRecurse(taken, takenSpot, level + 1, state, edgeLength, curComponent, position);
                    taken[y] = false;
                }
            }
        }
//        return total.value;
    }

    private int getPermutationSign(int edgeLength, int[] takenSpotIn) {
        int[] takenSpot = Arrays.copyOf(takenSpotIn, edgeLength);
        int result = 1;
        boolean sorted = false;
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < (edgeLength - 1); i++) {
                if (takenSpot[i] > takenSpot[i + 1]) {
                    sorted = false;
                    int bucket = takenSpot[i];
                    takenSpot[i] = takenSpot[i + 1];
                    takenSpot[i + 1] = bucket;
                    result *= -1;
                }
            }
        }
        return result;
    }

    public CrossProducts() {
        productsArray = new HashMap<Integer, ArrayList<Integer>>();
    }
}
