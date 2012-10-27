/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mewer12
 */
public class NCubeTriangulation {
    public static void main(String[] args) {
        int dims = 4;
        int points = 1 << dims;
        //ArrayList<Integer> wereCorners = new ArrayList<Integer>();
        int[] wereCorners = new int[points];
        for (int i = 0; i < points; i++) {
            //wereCorners.add(0);
            wereCorners[i] = 0;
        }
        // First, get all corners.
        boolean foundEmpty = true;
        while (foundEmpty) {
            foundEmpty = false;
            for (int i = 0; i < points; i++) {
                if (wereCorners[i] == 0) {
                    wereCorners[i] = -1;
                }
            }
        }
    }
}
