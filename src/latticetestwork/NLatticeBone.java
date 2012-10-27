/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewer12
 */
public class NLatticeBone {
    public int dims = 0;
    public NVector a = null;
    public NVector b = null;
    public double radius = 0;
    public Matrix projection = null;
    public boolean calculated = false;
    
    public NLatticeBone(int dims) {
        this.dims = dims;
    }

    public NLatticeBone(int dims, NVector a, NVector b, double radius) {
        this.dims = dims;
        this.a = a;
        this.b = b;
        this.radius = radius;
    }
    
    public void calcProjection() {
        NVector bucket = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            bucket.coords[i] = a.coords[i] - b.coords[i];
        }
        bucket.ipNormalize();
        Matrix left = new Matrix(1, dims);
        for (int i = 0; i < dims; i++) {
//            left.val[0][i] = a.coords[i] - b.coords[i];
            left.val[0][i] = bucket.coords[i];
        }
        
        try {
            projection = Matrix.lrMult(left, Matrix.transpose(left));
        } catch (Exception ex) {
            Logger.getLogger(NLatticeBone.class.getName()).log(Level.SEVERE, null, ex);
            projection = Matrix.identity(dims);
        }
        calculated = true;
    }
    
    public NVector closestPoint(NVector p1) {
        if (!calculated) {
            calcProjection();
        }
        
        NVector p = null;
        try {
            p = Matrix.lrvMult(projection, p1.minusB(a)).plusB(a);
        } catch (Exception ex) {
            Logger.getLogger(NLatticeBone.class.getName()).log(Level.SEVERE, null, ex);
        }
        boolean inBetween = true;
        for (int i = 0; i < dims; i++) {
            if (!((Math.min(a.coords[i], b.coords[i]) <= p.coords[i]) && (p.coords[i] <= Math.max(a.coords[i], b.coords[i])))) {
                inBetween = false;
                break;
            }
        }
        if (inBetween)
            return p;
        if (a.minusB(p).lengthSqr() <= b.minusB(p).lengthSqr())
            return a;
        return b;
    }
}
