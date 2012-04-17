/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author mewer12
 */
public class Matrix implements Streamable {
    public int cols = 0;
    public int rows = 0;
    public double[][] val = null;
    
    public Matrix(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        this.val = new double[cols][rows];
    }
    
    public Matrix(Matrix source) {
        this.cols = source.cols;
        this.rows = source.rows;
        this.val = new double[cols][rows];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                this.val[x][y] = source.val[x][y];
            }
        }
    }
    
    public boolean debug = false;
    
    /**
     * This matrix is an augmented matrix representing two lines which theoretically intersect.
     * Upon that assumption, it will try to get values for the two variables using some form of row-echelon reduction.
     * @return
     * @throws Exception 
     */
    public IntersectionResult ipSolveIntersect() throws Exception {
        //THINK I replaced == 1 with prettyEqual.  It might not be necessary, however.
        double[] qValues = {0, 0};
        boolean done = false;
        boolean qt1 = false;
        boolean qt2 = false;
        int t1 = 0;
        int t2 = 0;
        boolean qr1 = false;
        boolean qr2 = false;
        int r1 = 0;
        int r2 = 0;
        while (!done) {
            for (int y = 0; y < rows; y++) {
                if (MeMath.prettyZero(val[0][y], 17)) {
                    if (MeMath.prettyZero(val[1][y], 17)) {
                        // Hmm.  Pity.
                    } else if (!qt2 && MeMath.prettyEqual(val[1][y], 1, 17)) {
                        qt2 = true;
                        t2 = y;
                        qValues[1] = val[2][y];
                    } else {
                        val[2][y] /= val[1][y];
                        val[1][y] = 1;//
                    }
                } else if (MeMath.prettyEqual(val[0][y], 1, 17)) {
                    if (MeMath.prettyZero(val[1][y], 17)) {
                        if (!qt1) {
                            if (!qr1) {
                                qr1 = true;
                                r1 = y;
                            }
                            qt1 = true;
                            t1 = y;
                            qValues[0] = val[2][y];
                        }
                    }/*else if (val[1][y] == 1) {
                    }*/else {
                        if (qt1) {
                            if (qt2) {
                                // Oh, well, we're already done.
                            } else {
                                val[0][y] = 0;
                                val[1][y] -= val[1][t1];
                                val[2][y] -= val[2][t1];
                            }
                        } else {
                            if (!qr1) {
                                qr1 = true;
                                r1 = y;
                            }
                            if (qt2) {
                                val[0][y] -= (val[0][t2] * val[1][y]);
                                val[2][y] -= (val[2][t2] * val[1][y]);
                                val[1][y] = 0;
                            } else {
                                if (qr1 && r1 != y) {
                                    val[0][y] = 0;
                                    val[1][y] -= val[1][r1];
                                    val[2][y] -= val[2][r1];
                                }
                            }
                        }
                    }
                } else {
                    val[1][y] /= val[0][y];
                    val[2][y] /= val[0][y];
                    val[0][y] = 1;
                }
            }
            
            
            // Check done.
            for (int y = 0; y < rows; y++) {
                if (!qt1 && MeMath.prettyEqual(val[0][y], 1, 17) && MeMath.prettyZero(val[1][y], 17)) {
                    qt1 = true;
                    t1 = y;
                    qValues[0] = val[2][y];
                } else if (!qt2 && MeMath.prettyZero(val[0][y], 17) && MeMath.prettyEqual(val[1][y], 1, 17)) {
                    qt2 = true;
                    t2 = y;
                    qValues[1] = val[2][y];
                }
            }
            if ((qt1 && (!MeMath.prettyZero(qValues[0], 17))) || (qt2 && (!MeMath.prettyZero(qValues[1], 17)))) {
                done = true;
            }
            if (debug) {
                System.out.println(this);
            }
            if (qt1 && MeMath.prettyZero(qValues[0], 17) && qt2 && MeMath.prettyZero(qValues[1], 17)) {
                throw new Exception("Dead end!");
            }
        }
        if (qt1 && (!MeMath.prettyZero(qValues[0], 17))) {
            return new IntersectionResult(1,qValues[0]);
        } else if (qt2 && (!MeMath.prettyZero(qValues[1], 17))) {
            return new IntersectionResult(2,qValues[1]);
        } else {
            return new IntersectionResult(0,0);
        }
//        return qValues;
    }
    
    public IntersectionResult solveIntersect() throws Exception {
        Matrix backup = new Matrix(this);
        IntersectionResult result = ipSolveIntersect();
        this.val = backup.val;
        return result;
    }
    
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int y = 0; y < rows; y++) {
            if (y == 0) {
                result.append("/");
            } else if (y == (rows - 1)) {
                result.append("\\");
            } else {
                result.append("|");
            }
            
            for (int x = 0; x < cols; x++) {
                result.append(String.format("%7.4f", val[x][y]) + "\t");
            }
            if (y == 0) {
                result.append("\\\n");
            } else if (y == (rows - 1)) {
                result.append("/\n");
            } else {
                result.append("|\n");
            }
        }
        return result.toString();
    }
    
    public static CrossProducts cp = new CrossProducts();
    public static double[] cross(double[][] p) throws Exception {

        double[] result = new double[p.length + 1];
        boolean[] taken = new boolean[p.length + 1];
        for (int i = 0; i < p.length; i++) {
            if (p[i].length != (p.length + 1)) {
                throw new Exception("Must give n-1 vectors of n length!");
            }
        }
        for (int i = 0; i < (p.length + 1); i++) {
            taken[i] = false;
        }
        int[] state = {0};
        crossRecurse(result, 1, taken, 0, state, p.length + 1, p, new int[1], cp);
        return result;
    }
    
    private static void crossRecurse(double[] total, double product, boolean[] taken, int level, int[] state, int edgeLength, double[][] p, int[] curComponent, CrossProducts crossProductsA) {
        //prod take next available row, incmod state, if level == edgeLength +/- product, untake, return
        for (int y = -1; y < (edgeLength - 1); y++) {
            if (!taken[y + 1]) {
                double wasVal = product;
                if (y == -1) {
                    curComponent[0] = level;
                } else {
                    product *= p[y][level];
                }
                if (level == (edgeLength - 1)) {
                    /*                  switch (state[0]) {
                    case 0:
                    total[curComponent[0]] -= product * (1 - (2 * (edgeLength % 2)));
                    break;
                    case 1:
                    total[curComponent[0]] += product * (1 - (2 * (edgeLength % 2)));
                    break;
                    case 2:
                    total[curComponent[0]] += product * (1 - (2 * (edgeLength % 2)));
                    break;
                    case 3:
                    total[curComponent[0]] -= product * (1 - (2 * (edgeLength % 2)));
                    break;
                    }
                    state[0] = (state[0] + 1) % 4;
                     */
                    total[curComponent[0]] += product * crossProductsA.crossSign(edgeLength, state[0]);
//                    System.out.println(product * crossProductsA.sign(edgeLength, state[0]));
                    state[0]++;
                    break;
                } else {
                    taken[y + 1] = true;
                    crossRecurse(total, product, taken, level + 1, state, edgeLength, p, curComponent, crossProductsA);
                    taken[y + 1] = false;
                }
                product = wasVal;
            }
        }
//        return total.value;
    }
    
    public void ipRRowForm() { // Kinda.  It does its best to turn the left side into the identity matrix.
if (debug) {
    System.out.println(this);
}
        for (int row = 0; row < rows; row++) {
            if (row >= cols) {
                // Probably don't do anything for now.
            } else {
                if (MeMath.prettyZero(val[row][row], 17)) {
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < rows; y++) {
                        if (!MeMath.prettyZero(val[row][y], 17)) {
                            for (int x = 0; x < cols; x++) {
                                val[x][row] += val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
if (debug) {
    System.out.println(this);
}
                    if (!found) {
                        // Maybe I should error here.  We'll leave it, for now.
                        System.err.println("Matrix can't be left-identity.");
                        System.err.println(this);
                        System.err.println();
                    }
                }
                for (int col = row + 1; col < cols; col++) {
                    val[col][row] /= val[row][row];
                }
                val[row][row] = 1;
if (debug) {
    System.out.println(this);
}
                // Apply this row to all other ones.
                for (int y = 0; y < rows; y++) {
                    if (y != row) {
                        double factor = val[row][y];
                        for (int i = row; i < cols; i++) {
//                            System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                            val[i][y] -= factor * val[i][row];
                        }
if (debug) {
    System.out.println(this);
}
                    }
                }
            }
        }
        
//        // Make identity.
//        int max = Math.min(rows, cols);
//        for (int row = 0; row < rows; row++) {
//            if (row >= cols) {
////                for (int x = 0; x < max; x++) {
////                    // Try to zero this spot in the row.
////                    for (int i = x; i < cols; i++) {
////                        val[i][row] -= val[x][row] * val[x][x];
////                    }
////if (debug) {
////    System.out.println(this);
////}
////                }
//            } else {
//                for (int x = row + 1; x < max; x++) {
//                    // Try to zero this spot in the row.
//                    for (int i = x; i < cols; i++) {
//                        val[i][row] -= val[x][row] * val[x][x];
//                    }
//if (debug) {
//    System.out.println(this);
//}
//                }
//            }
//        }
    }

    public void ipRRowFormMod() { // Kinda.  It does its best to turn the left side into the identity matrix.
if (debug) {
    System.out.println(this);
}
        for (int row = 0; (row < rows && row < cols - 1); row++) {
            if (row >= cols) {
                // Probably don't do anything for now.
            } else {
                if (MeMath.prettyZero(val[row][row], 17)) {////
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < rows; y++) {
                        if (!MeMath.prettyZero(val[row][y], 17)) {
                            for (int x = 0; x < cols; x++) {
                                val[x][row] += val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
if (debug) {
    System.out.println(this);
}
                    if (!found) {
                        // Maybe I should error here.  We'll leave it, for now.
                        System.err.println("Matrix can't be left-identity.");
                        System.err.println(this);
                        System.err.println();
                    }
                }
                for (int col = row + 1; col < cols; col++) {
                    val[col][row] /= val[row][row];
                }
                val[row][row] = 1;
if (debug) {
    System.out.println(this);
}
                // Apply this row to all other ones.
                for (int y = 0; y < rows; y++) {
                    if (y != row) {
                        double factor = val[row][y];
                        for (int i = row; i < cols; i++) {
//                            System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                            val[i][y] -= factor * val[i][row];
                        }
if (debug) {
    System.out.println(this);
}
                    }
                }
            }
        }
        
//        // Make identity.
//        int max = Math.min(rows, cols);
//        for (int row = 0; row < rows; row++) {
//            if (row >= cols) {
////                for (int x = 0; x < max; x++) {
////                    // Try to zero this spot in the row.
////                    for (int i = x; i < cols; i++) {
////                        val[i][row] -= val[x][row] * val[x][x];
////                    }
////if (debug) {
////    System.out.println(this);
////}
////                }
//            } else {
//                for (int x = row + 1; x < max; x++) {
//                    // Try to zero this spot in the row.
//                    for (int i = x; i < cols; i++) {
//                        val[i][row] -= val[x][row] * val[x][x];
//                    }
//if (debug) {
//    System.out.println(this);
//}
//                }
//            }
//        }
    }
    
    public static Matrix lrJoin(Matrix l, Matrix r) throws Exception {
        if (l.rows != r.rows) {
            throw new Exception("Matrices are different heights!");
        }
        if (l.cols != r.cols) {
            System.err.println("Matrices are diff widths.  You'll have a hard time separating them.");
        }
        Matrix result = new Matrix(l.cols + r.cols, l.rows);
        for (int x = 0; x < l.cols; x++) {
            for (int y = 0; y < l.rows; y++) {
                result.val[x][y] = l.val[x][y];
            }            
        }
        for (int x = 0; x < r.cols; x++) {
            for (int y = 0; y < r.rows; y++) {
                result.val[x + l.cols][y] = r.val[x][y];
            }            
        }
        return result;
    }

    public static Matrix[] lrSplit(Matrix lr) throws Exception {
        if ((lr.cols % 2) != 0) {
            throw new Exception("Matrix doesn't have an even width!");
        }
        Matrix l = new Matrix(lr.cols / 2, lr.rows);
        Matrix r = new Matrix(lr.cols / 2, lr.rows);
        for (int x = 0; x < l.cols; x++) {
            for (int y = 0; y < l.rows; y++) {
                l.val[x][y] = lr.val[x][y];
            }            
        }
        for (int x = 0; x < r.cols; x++) {
            for (int y = 0; y < r.rows; y++) {
                r.val[x][y] = lr.val[x + l.cols][y];
            }            
        }
        Matrix[] result = {l, r};
        return result;
    }
    
    public static Matrix lrMult(Matrix l, Matrix r) throws Exception {
        if (l.cols != r.rows) {
            throw new Exception("Dims don't match for multiplication!");
        }
        Matrix result = new Matrix(r.cols, l.rows);
        for (int col = 0; col < result.cols; col++) {
            for (int row = 0; row < result.rows; row++) {
                double sum = 0;
                for (int i = 0; i < r.rows; i++) {
                    sum += l.val[i][row] * r.val[col][i];
                }
                result.val[col][row] = sum;
            }
        }
        return result;
    }

    public static Matrix identity(int size) {
        Matrix m = new Matrix(size, size);
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (x == y) {
                    m.val[x][y] = 1;
                } else {
                    m.val[x][y] = 0;
                }
            }
        }
        return m;
    }
    
    public static Matrix invert(Matrix m) throws Exception {
        if (m.cols != m.rows) {
            throw new Exception("Non-square matrix has no inverse! (More or less.)");
        }
        int size = m.cols;
        Matrix left = m;
        Matrix right = Matrix.identity(size);
        Matrix joined = Matrix.lrJoin(left, right);
        joined.ipRRowForm();
        Matrix[] split = Matrix.lrSplit(joined);
        return split[1];
    }

    public static NVector lrvMult(Matrix l, NVector r) throws Exception {
        if (l.cols != r.dims) {
            throw new Exception("Dims don't match for multiplication!");
        }
        NVector result = new NVector(l.rows);
        for (int row = 0; row < result.dims; row++) {
            double sum = 0;
            for (int i = 0; i < r.dims; i++) {
                sum += l.val[i][row] * r.coords[i];
            }
            result.coords[row] = sum;
        }
        return result;
    }
    
    /**
     * This attempts to figure out what the coordinates of each vector are
     * in the provided basis.  It was specifically designed to handle subspaces,
     * provided that all the vectors are in line with the basis.
     * The vectors returned will have dimension = number of bases.
     * I'm not actually sure why it's ip (in place).
     * @param bases
     * @param vectors
     * @return 
     */
    public static NVector[] ipTransformCoords(ArrayList<NVector> bases, ArrayList<NVector> vectors) {
        // Set up the matrix
        Matrix m = new Matrix(bases.get(0).dims + bases.size(), bases.size() + vectors.size());
        {
        int row = 0;
        for (int i = 0; i < bases.size(); i++, row++) {
            int col = 0;
            for (int j = 0; j < bases.get(i).dims; j++, col++){
                m.val[col][row] = bases.get(i).coords[j];
            }
            for (int j = 0; j < bases.size(); j++, col++){
                if (j == i) {
                    m.val[col][row] = 1;
                } else {
                    m.val[col][row] = 0;
                }
            }
        }
        for (int i = 0; i < vectors.size(); i++, row++) {
            int col = 0;
            for (int j = 0; j < vectors.get(i).dims; j++, col++){
                m.val[col][row] = vectors.get(i).coords[j];
            }
            for (int j = 0; j < bases.size(); j++, col++){
                m.val[col][row] = 0;
            }
        }
        }
        
        // Now solve it.
        int skipped = 0;
        for (int row = 0; row < bases.size(); row++) {
            if (row + skipped >= m.cols - 1) {
                // Probably don't do anything for now.
            } else {
                //if (m.val[row + skipped][row] == 0) {
                if (MeMath.prettyZero(m.val[row + skipped][row], 17)) {
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < bases.size(); y++) {
                        //if (m.val[row + skipped][y] != 0) {//m.val[2][y-1]
                        if (!MeMath.prettyZero(m.val[row + skipped][y], 17)) {
                            for (int x = row + skipped; x < m.cols; x++) {
                                m.val[x][row] += m.val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //TODO This should check for...something.  Too many skips or something.  I don't care right now.
//                        if (row + skipped < ) I dunno.  Something should probably go here, but I'm going to leave it for now.
                        skipped++;
                        row--;
                        continue;
                        // Maybe I should error here.  We'll leave it, for now.
//                        System.err.println("Matrix can't be left-identity.");
//                        System.err.println(m);
//                        System.err.println();
                    }
                }
                // Div row by leading value
                for (int col = row + skipped + 1; col < m.cols; col++) {
                    m.val[col][row] /= m.val[row + skipped][row];
                }
                m.val[row + skipped][row] = 1;
                // Apply this row to all the following ones.
                for (int y = row + 1; y < m.rows; y++) {
                    double factor = m.val[row + skipped][y];
                    for (int i = row + skipped; i < m.cols; i++) {
//                        System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                        m.val[i][y] -= factor * m.val[i][row];
                    }
                }
            }
        }
        
        //System.out.println(m);
        
        // Extract the answer.
        NVector[] result = new NVector[vectors.size()];
        for (int i = 0; i < vectors.size(); i++) {
            NVector bucket = new NVector(bases.size());
            for (int j = 0; j < bucket.dims; j++) {
                bucket.coords[j] = -m.val[vectors.get(0).dims + j][bases.size() + i];
            }
            result[i] = bucket;
        }
        
        return result;
    }

    /**
     * This attempts to figure out what the coordinates of each vector are
     * in the provided basis.  It was specifically designed to handle subspaces,
     * provided that all the vectors are in line with the basis.
     * The vectors returned will have dimension = number of bases.
     * Ugh, had to copy the whole friggin' thing just to use NVector[] instead of ArrayList<NVector>...
     * @param bases
     * @param vectors
     * @return 
     */
    public static NVector[] ipTransformCoords(NVector[] bases, NVector[] vectors) {
        // Set up the matrix
        Matrix m = new Matrix(bases[0].dims + bases.length, bases.length + vectors.length);
        {
        int row = 0;
        for (int i = 0; i < bases.length; i++, row++) {
            int col = 0;
            for (int j = 0; j < bases[i].dims; j++, col++){
                m.val[col][row] = bases[i].coords[j];
            }
            for (int j = 0; j < bases.length; j++, col++){
                if (j == i) {
                    m.val[col][row] = 1;
                } else {
                    m.val[col][row] = 0;
                }
            }
        }
        for (int i = 0; i < vectors.length; i++, row++) {
            int col = 0;
            for (int j = 0; j < vectors[i].dims; j++, col++){
                m.val[col][row] = vectors[i].coords[j];
            }
            for (int j = 0; j < bases.length; j++, col++){
                m.val[col][row] = 0;
            }
        }
        }
        
        // Now solve it.
        int skipped = 0;
        for (int row = 0; row < bases.length; row++) {
            if (row + skipped >= m.cols - 1) {
                // Probably don't do anything for now.
            } else {
                //if (m.val[row + skipped][row] == 0) {
                if (MeMath.prettyZero(m.val[row + skipped][row], 17)) {
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < bases.length; y++) {
                        //if (m.val[row + skipped][y] != 0) {
                        if (!MeMath.prettyZero(m.val[row + skipped][y], 17)) {
                            for (int x = row + skipped; x < m.cols; x++) {
                                m.val[x][row] += m.val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        //TODO This should check for...something.  Too many skips or something.  I don't care right now.
//                        if (row + skipped < ) I dunno.  Something should probably go here, but I'm going to leave it for now.
                        skipped++;
                        row--;
                        continue;
                        // Maybe I should error here.  We'll leave it, for now.
//                        System.err.println("Matrix can't be left-identity.");
//                        System.err.println(m);
//                        System.err.println();
                    }
                }
                for (int col = row + skipped + 1; col < m.cols; col++) {
                    m.val[col][row] /= m.val[row + skipped][row];
                }
                m.val[row + skipped][row] = 1;
                // Apply this row to all the following ones.
                for (int y = row + 1; y < m.rows; y++) {
                    double factor = m.val[row + skipped][y];
                    for (int i = row + skipped; i < m.cols; i++) {
//                        System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                        m.val[i][y] -= factor * m.val[i][row];
                    }
                }
            }
        }
        
        //System.out.println(m);
        
        // Extract the answer.
        NVector[] result = new NVector[vectors.length];
        for (int i = 0; i < vectors.length; i++) {
            NVector bucket = new NVector(bases.length);
            for (int j = 0; j < bucket.dims; j++) {
                bucket.coords[j] = -m.val[vectors[0].dims + j][bases.length + i];
            }
            result[i] = bucket;
        }
        
        return result;
    }

    /**
     * -EXPERIMENTAL-
     * Calculates the determinant of the matrix.
     * 
     * @return double
     */
    public double det() throws Exception {
        if (cols != rows) {
            throw new Exception("Can't find determinant for non-square matrix!");
        }
        DoubleHolder result = new DoubleHolder(0);
        boolean[] taken = new boolean[cols];
        for (int i = 0; i < cols; i++) {
            taken[i] = false;
        }
        int[] state = {0};
        detRecurse(result, 1, taken, 0, state);
        return result.value;
    }

    private class DoubleHolder {

        double value;

        public DoubleHolder(double value) {
            this.value = value;
        }
    }

    private class IntHolder {

        int value;

        public IntHolder(int value) {
            this.value = value;
        }
    }

    private void detRecurse(DoubleHolder total, double product, boolean[] taken, int level, int[] state) {
        //prod take next available row, incmod state, if level == edgeLength +/- product, untake, return
        for (int y = 0; y < cols; y++) {
            if (!taken[y]) {
                double wasVal = product;
                product *= this.val[level][y];
                if (level == (cols - 1)) {
                    /*                    switch (state.value) {
                    case 0:
                    total.value += product;
                    break;
                    case 1:
                    total.value -= product;
                    break;
                    case 2:
                    total.value -= product;
                    break;
                    case 3:
                    total.value += product;
                    break;
                    }
                    state.value = (state.value + 1) % 4;*/
                    total.value += product * this.cp.detSign(cols, state[0]);
                    state[0]++;
                    break;
                } else {
                    taken[y] = true;
                    this.detRecurse(total, product, taken, level + 1, state);
                    taken[y] = false;
                }
                product = wasVal;
            }
        }
//        return total.value;
    }
    
    public Matrix copy() {
        Matrix result = new Matrix(cols, rows);
        for (int x = 0; x < cols; x++) {
            System.arraycopy(this.val[x], 0, result.val[x], 0, rows);
//            for (int y = 0; y < rows; y++) {
//                result.val[x][y] = this.val[x][y];
//            }
        }
        return result;
    }
    
    public static Matrix transpose(Matrix m) {
        Matrix result = new Matrix(m.rows, m.cols);
        for (int i = 0; i < m.cols; i++) {
            for (int j = 0; j < m.rows; j++) {
                result.val[j][i] = m.val[i][j];
            }
        }
        return result;
    }

    public void ipRRowFormError() throws Exception { // Kinda.  It does its best to turn the left side into the identity matrix.
if (debug) {
    System.out.println(this);
}
        for (int row = 0; row < rows; row++) {
            if (row >= cols) {
                // Probably don't do anything for now.
            } else {
                if (MeMath.prettyZero(val[row][row], 17)) {
                    // Add another row to this one to get not 0.
                    boolean found = false;
                    for (int y = row + 1; y < rows; y++) {
                        if (!MeMath.prettyZero(val[row][y], 17)) {
                            for (int x = 0; x < cols; x++) {
                                val[x][row] += val[x][y];
                            }
                            found = true;
                            break;
                        }
                    }
if (debug) {
    System.out.println(this);
}
                    if (!found) {
                        // Maybe I should error here.  We'll leave it, for now.
                        throw new Exception("Matrix can't be left-identity.\n" + this);
//                        System.err.println("Matrix can't be left-identity.");
//                        System.err.println(this);
//                        System.err.println();
                    }
                }
                for (int col = row + 1; col < cols; col++) {
                    val[col][row] /= val[row][row];
                }
                val[row][row] = 1;
if (debug) {
    System.out.println(this);
}
                // Apply this row to all other ones.
                for (int y = 0; y < rows; y++) {
                    if (y != row) {
                        double factor = val[row][y];
                        for (int i = row; i < cols; i++) {
//                            System.err.println("row=" + row + ";y=" + y + ";i=" + i);
                            val[i][y] -= factor * val[i][row];
                        }
if (debug) {
    System.out.println(this);
}
                    }
                }
            }
        }
    }

    public int id = -2;
    
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(cols);
        dos.writeInt(rows);
        dos.writeBoolean(debug);
        dos.writeInt(id);
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                dos.writeDouble(val[i][j]);
            }
        }
    }
    
    public static Object fromBytes(DataInputStream dis) throws IOException {
        int cols = dis.readInt();
        int rows = dis.readInt();
        Matrix result = new Matrix(cols, rows);
        result.debug = dis.readBoolean();
        result.id = dis.readInt();
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                result.val[i][j] = dis.readDouble();
            }
        }
        return result;
    }
    
    /**
     * This'll get an intersection of a line and an NPlane.  It works for fully determined systems,
     * but it also works for systems where technically the line might not quite intersect the nplane, actually.
     * However, they ought to be really really close, because it effectively just collapses a number of dimensions
     * equal to the extra - it reduces rows (Reduced Row Echelon format) until a solution is found, and then ignores the rest.
     * Good enough for now, anyway.
     * @param lPos
     * @param lDir
     * @param origin
     * @param bases
     * @return 
     */
    public static NVector lineNPlaneIntersect(NVector lPos, NVector lDir, NVector origin, NVector... bases) {
        Matrix solver = new Matrix(2 + bases.length, lPos.dims);
        for (int y = 0; y < solver.rows; y++) {
            solver.val[0][y] = lDir.coords[y];
        }
        for (int x = 1; x < bases.length + 1; x++) {
            for (int y = 0; y < solver.rows; y++) {
                solver.val[x][y] = -bases[x - 1].coords[y];
            }
        }
        int x = solver.cols - 1;
        for (int y = 0; y < solver.rows; y++) {//System.out.println(lPos.minusB(origin.plusB(bases[0])).length())
            solver.val[x][y] = origin.coords[y] - lPos.coords[y];
        }//System.out.println(solver);
        //TODO It might be a good idea to make this "closest points" for near-intersection.
        //TODO   Of course, this is probably faster, and maybe just as good for most things.
        //TODO   I'm already done with it, for sure.
        solver.ipRRowFormMod();
        double t = solver.val[solver.cols - 1][0];
        return lDir.multS(t).plusB(lPos);
    }
}