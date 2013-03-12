/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author mewer12
 */
public class NVector {

    public int dims = 0;
    public double[] coords = null;

    public NVector(int dims) {
        this.dims = dims;
        coords = new double[dims];
    }

    public NVector(double[] coords) {
        this.dims = coords.length;
        this.coords = coords;
    }

    public NVector(double[] coords, boolean clone) {
        this.dims = coords.length;
        if (clone) {
            this.coords = coords.clone();
        } else {
            this.coords = coords;
        }
    }

    public NVector plusB(NVector b) {
        NVector result = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            result.coords[i] = this.coords[i] + b.coords[i];
        }
        return result;
    }

    public NVector minusB(NVector b) {
        NVector result = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            result.coords[i] = this.coords[i] - b.coords[i];
        }
        return result;
    }

    public NVector multS(double s) {
        NVector result = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            result.coords[i] = this.coords[i] * s;
        }
        return result;
    }

    public NVector plusBIP(NVector b) {
        for (int i = 0; i < dims; i++) {
            coords[i] += b.coords[i];
        }
        return this;
    }

    public NVector minusBIP(NVector b) {
        for (int i = 0; i < dims; i++) {
            coords[i] -= b.coords[i];
        }
        return this;
    }

    public NVector multSIP(double s) {
        for (int i = 0; i < dims; i++) {
            coords[i] *= s;
        }
        return this;
    }
    
    public static double lrDot(NVector l, NVector r) {
        double result = 0;
        for (int i = 0; i < l.dims; i++) {
            result += l.coords[i] * r.coords[i];
        }
        return result;
    }

    public static NVector lrProj(NVector onto, NVector v) {
        return onto.multS(NVector.lrDot(onto, v) / NVector.lrDot(onto, onto));
    }

    public NVector ipNormalize() {
        double length = 0;
        for (int i = 0; i < dims; i++) {
            length += coords[i] * coords[i];
        }
        if (length == 0) {
            //THINK This might be important to note, when it happens.
            return this;
        }
        length = Math.sqrt(length);
        for (int i = 0; i < dims; i++) {
            coords[i] /= length;
        }
        return this;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append(coords[0]);
        for (int i = 1; i < dims; i++) {
            result.append("," + coords[i]);
        }
        result.append("}");
        return result.toString();
    }

    public NVector copy() {
        NVector result = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            result.coords[i] = this.coords[i];
        }
        return result;
    }

    /**
     * Returns a rotation of vector v from AB through AC by angle degrees.
     * @param basisPtA
     * @param basisPtB
     * @param basisPtC
     * @param v
     * @param angle
     * @return
     * @throws Exception 
     */
    public static NVector rotate(NVector basisPtA, NVector basisPtB, NVector basisPtC, NVector v, double angle) throws Exception {
        int dims = v.dims;

        ArrayList<NVector> bases = new ArrayList<NVector>();
        Matrix basis = null;

        NVector bucket = new NVector(dims);
        for (int j = 0; j < dims; j++) {
            bucket.coords[j] = basisPtB.coords[j] - basisPtA.coords[j];
        }
        bases.add(bucket);
        bucket = new NVector(dims);
        for (int j = 0; j < dims; j++) {
            bucket.coords[j] = basisPtC.coords[j] - basisPtA.coords[j];
        }
        bases.add(bucket);
        basis = new Matrix(bases.size(), dims);
        for (int i = 0; i < bases.size(); i++) {
            for (int j = 0; j < i; j++) {
                bases.set(i, bases.get(i).minusB(NVector.lrProj(bases.get(j), bases.get(i))));
            }//System.out.println(bases);
            bases.get(i).ipNormalize();
            for (int j = 0; j < dims; j++) {
                basis.val[i][j] = bases.get(i).coords[j];
            }
        }
        Matrix projection;
        projection = Matrix.lrMult(basis, Matrix.transpose(basis));
        ArrayList<NVector> points = new ArrayList<NVector>();
        NVector translated = v.minusB(basisPtA);
        NVector projected = Matrix.lrvMult(projection, translated);
        NVector difference = translated.minusB(projected);
        points.add(projected);
        // Does it matter?

        // Get the coordinates of the vectors 
        NVector[] transPoints = Matrix.ipTransformCoords(bases, points);
        NVector flatV = transPoints[0];
        Matrix rotation = new Matrix(2, 2);
        rotation.val[0][0] = Math.cos(angle);
        rotation.val[1][1] = rotation.val[0][0];
        rotation.val[0][1] = Math.sin(angle);
        rotation.val[1][0] = -rotation.val[0][1];
        NVector rotV = Matrix.lrvMult(rotation, flatV);
        NVector newV = Matrix.lrvMult(basis, rotV);
        newV = newV.plusB(difference);
        newV = newV.plusB(basisPtA);

        return newV;
    }

    /**
     * Returns a rotation of vector v from OB through OC by angle degrees.
     * @param basisPtA
     * @param basisPtB
     * @param basisPtC
     * @param v
     * @param angle
     * @return
     * @throws Exception 
     */
    public static NVector rotate(NVector basisPtB, NVector basisPtC, NVector v, double angle) throws Exception {
        int dims = v.dims;

        ArrayList<NVector> bases = new ArrayList<NVector>();
        Matrix basis = new Matrix(2, dims);

//        NVector basB = basisPtB.copy().ipNormalize();
//        basis.val[0] = basB.coords;
//        NVector basC = basisPtC.minusB(NVector.lrProj(basisPtB, basisPtC)).ipNormalize();
//        basis.val[1] = basC.coords;
        
//        NVector basB = basisPtB.copy().ipNormalize();
//        NVector basC = basisPtC.copy().ipNormalize();
//        
//        bases.add(basB);
//        bases.add(basC);
//        
//        bases.get(0).ipNormalize();
//        basis.val[0] = bases.get(0).copy().coords;
//        bases.set(0, basisPtC.minusB(NVector.lrProj(basisPtB, basisPtC)).ipNormalize());
//        bases.get(1).ipNormalize();
//        basis.val[1] = bases.get(1).copy().coords;
        
        
        NVector bucket = new NVector(dims);
        for (int j = 0; j < dims; j++) {
            bucket.coords[j] = basisPtB.coords[j];
        }
        bases.add(bucket);
        bucket = new NVector(dims);
        for (int j = 0; j < dims; j++) {
            bucket.coords[j] = basisPtC.coords[j];
        }
        bases.add(bucket);
        basis = new Matrix(bases.size(), dims);
        for (int i = 0; i < bases.size(); i++) {
            for (int j = 0; j < i; j++) {
                bases.set(i, bases.get(i).minusB(NVector.lrProj(bases.get(j), bases.get(i))));
            }//System.out.println(bases);
            bases.get(i).ipNormalize();
            for (int j = 0; j < dims; j++) {
                basis.val[i][j] = bases.get(i).coords[j];
            }
        }
        
        Matrix projection;
        projection = Matrix.lrMult(basis, Matrix.transpose(basis));
        ArrayList<NVector> points = new ArrayList<NVector>();
        NVector projected = Matrix.lrvMult(projection, v);
        NVector difference = v.minusB(projected);
        points.add(projected);
        // Does it matter?

        // Get the coordinates of the vectors 
        NVector[] transPoints = Matrix.ipTransformCoords(bases, points);
        NVector flatV = transPoints[0];
        Matrix rotation = new Matrix(2, 2);
        rotation.val[0][0] = Math.cos(angle);
        rotation.val[1][1] = rotation.val[0][0];
        rotation.val[0][1] = Math.sin(angle);
        rotation.val[1][0] = -rotation.val[0][1];
        NVector rotV = Matrix.lrvMult(rotation, flatV);
        NVector newV = Matrix.lrvMult(basis, rotV);
        newV = newV.plusB(difference);

        return newV;
    }
    
    public static NVector rotateOrthBasis(NVector basisVecA, NVector basisVecB, NVector v, double angle) throws Exception {
        int dims = v.dims;
        //NVector result = new NVector(dims);

        ArrayList<NVector> bases = new ArrayList<NVector>();
        Matrix basis = null;

        bases.add(basisVecA);
        bases.add(basisVecB);
        basis = new Matrix(bases.size(), dims);
        // Gram-Schmidt orthogonalization - except we're assuming we've been given an orthogonal basis
        for (int i = 0; i < bases.size(); i++) {
            for (int j = 0; j < dims; j++) {
                basis.val[i][j] = bases.get(i).coords[j];
            }
        }
        Matrix projection;
        projection = Matrix.lrMult(basis, Matrix.transpose(basis));
        ArrayList<NVector> points = new ArrayList<NVector>();
        NVector projected = Matrix.lrvMult(projection, v);
        NVector difference = v.minusB(projected);
        points.add(projected);
        // Does it matter?

        // Get the coordinates of the vectors 
        NVector[] transPoints = Matrix.ipTransformCoords(bases, points);
        NVector flatV = transPoints[0];
        Matrix rotation = new Matrix(2, 2);
        rotation.val[0][0] = Math.cos(angle);
        rotation.val[1][1] = rotation.val[0][0];
        rotation.val[0][1] = Math.sin(angle);
        rotation.val[1][0] = -rotation.val[0][1];
        NVector rotV = Matrix.lrvMult(rotation, flatV);
        NVector newV = Matrix.lrvMult(basis, rotV);
        newV = newV.plusB(difference);
//        newV = newV.plusB(basisPtA);

        return newV;
    }

    /**
     * This assumes a basis of two vectors, forming a plane of rotation.
     * 
     * @param basis
     * @param v
     * @param angle
     * @return
     * @throws Exception 
     */
    public static NVector rotate(NBasis basis, NVector v, double angle) throws Exception {
        int dims = v.dims;
        //NVector result = new NVector(dims);

//        } catch (Exception e) {
//            // Not sure what to do, actually.
//            projection = Matrix.identity(dims);
//        }
        ArrayList<NVector> points = new ArrayList<NVector>();
//        NVector difference = v.minusB(Matrix.lrvMult(projection, v));
//        points.add(Matrix.lrvMult(projection, v).minusB(basisPtA));
        // Or is it
        NVector translated = v.minusB(basis.origin);
        NVector projected = Matrix.lrvMult(basis.projection, translated);
        NVector difference = translated.minusB(projected);
        points.add(projected);
        // Does it matter?

        // Get the coordinates of the vectors 
        ArrayList<NVector> twoBases = new ArrayList<NVector>();
        twoBases.add(basis.bases[0]);
        twoBases.add(basis.bases[1]);
        NVector[] transPoints = Matrix.ipTransformCoords(twoBases, points);
        NVector flatV = transPoints[0];
        Matrix rotation = new Matrix(2, 2);
        rotation.val[0][0] = Math.cos(angle);
        rotation.val[1][1] = rotation.val[0][0];
        rotation.val[0][1] = Math.sin(angle);
        rotation.val[1][0] = -rotation.val[0][1];
        NVector rotV = Matrix.lrvMult(rotation, flatV);
        NVector newV = Matrix.lrvMult(basis.basis, rotV);
        newV = newV.plusB(difference);
        newV = newV.plusB(basis.origin);

        return newV;
    }

    public double distSqr(NVector v) {
        double dist = 0;
        for (int i = 0; i < dims; i++) {
            dist += MeMath.sqr(this.coords[i] - v.coords[i]);
        }
        return dist;
    }

    public double dist(NVector v) {
        double dist = 0;
        for (int i = 0; i < dims; i++) {
            dist += MeMath.sqr(this.coords[i] - v.coords[i]);
        }
        return Math.sqrt(dist);
    }

    public double lengthSqr() {
        double length = 0;
        for (int i = 0; i < dims; i++) {
            length += this.coords[i] * this.coords[i];
        }
        return length;
    }

    public double length() {
        double length = 0;
        for (int i = 0; i < dims; i++) {
            length += this.coords[i] * this.coords[i];
        }
        return Math.sqrt(length);
    }

    public static NVector zero(int dims) {
        NVector result = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            result.coords[i] = 0;
        }
        return result;
    }

    /**
     * Returns the angle between the two vectors.  Order does not matter, as
     * it is computed with acos{a.b / |a||b|)
     * @param a
     * @param b
     * @return 
     */
    public static double angle(NVector a, NVector b) {
        double dot = lrDot(a, b);
        double lens = (a.length() * b.length());
        if (!MeMath.prettyEqual(Math.abs(dot), lens, 24)) {
            return Math.acos(dot / lens);
        } else {
            if (dot >= 0) {
                return 0;
            } else {
                return Math.PI;
            }
        }
    }
    
    /**
     * Returns whether two vectors are in the same "quadrant" (however many there may be.)
     * This is useful for figuring out if an intersection is in front or behind of a point.
     * (like, sameQuadrant(dir, hit.minusB(pos)).)
     * @param a
     * @param b
     * @return 
     */
    public static boolean sameQuadrant(NVector a, NVector b) {
        for (int i = 0; i < a.coords.length; i++) {
            if ((a.coords[i] > 0 && b.coords[i] < 0) || (a.coords[i] < 0 && b.coords[i] > 0)) {
                return false;
            }
        }
        return true;
    }
    
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(dims);
        for (int i = 0; i < dims; i++) {
            dos.writeDouble(coords[i]);
        }
    }

    public static NVector fromBytes(DataInputStream dis) throws IOException {
        int dims = dis.readInt();
        NVector result = new NVector(dims);        
        for (int i = 0; i < dims; i++) {
            result.coords[i] = dis.readDouble();
        }
        return result;
    }
    
    public static Random r = new Random();
    
    public static NVector random(int dims, double range, boolean normalize) {
        NVector bucket = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            bucket.coords[i] = ((r.nextDouble() * 2) - 1) * range;
        }
        if (normalize) {
            bucket.ipNormalize();
        }
        return bucket;
    }
    
    public boolean exactlyEquivalent(NVector v) {
        if (this.dims != v.dims)
            return false;
        for (int i = 0; i < dims; i++) {
            if (this.coords[i] != v.coords[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean approximatelyEquivalent(NVector v, int binDigits) {
        if (this.dims != v.dims)
            return false;
        for (int i = 0; i < dims; i++) {
            if (!MeMath.prettyEqual(this.coords[i], v.coords[i], binDigits)) {
                return false;
            }
        }
        return true;
    }
}