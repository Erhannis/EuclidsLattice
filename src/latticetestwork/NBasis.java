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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewer12
 */
public class NBasis implements Streamable {

    public int dims = 0;
    public int internalDims = 0;
    public NVector[] bases = null;
    public NVector origin = null; // Not sure about this one.
    public Matrix basis = null; // I don't know if I want or need this one.
    /** Ok, so I think the projection is to an nplane THROUGH THE ORIGIN. */
    public Matrix projection = null;
    public boolean orthogonal = false; // And also normalized.
    public int snapDigits = 14;

    public NBasis(int dims, int internalDims) {
        this.dims = dims;
        this.internalDims = internalDims;
        this.bases = new NVector[internalDims];
        this.origin = new NVector(dims);
        //this.basis = new Matrix(internalDims, dims);
    }

    /**
     * Uses Gram-Schmidt orthogonalization to orthogonalize the bases (in place)
     * and construct a basis matrix out of them.
     */
    public void orthogonalize() {
        basis = new Matrix(bases.length, dims);//MTXOFT*
        // Gram-Schmidt orthogonalization
        for (int i = 0; i < bases.length; i++) {
            for (int j = 0; j < i; j++) {
                bases[i] = bases[i].minusB(NVector.lrProj(bases[j], bases[i]));
            }//System.out.println(bases);
            bases[i].ipNormalize();
            for (int j = 0; j < dims; j++) {
                basis.val[i][j] = bases[i].coords[j];
            }
        }
        orthogonal = true;
    }//System.out.println(basis);

    /**
     * Uses Gram-Schmidt orthogonalization to orthogonalize the bases (in place)
     * and construct a basis matrix out of them.
     */
    public void orthogonalizeWCache() {
        basis = Matrix.getCachedMatrix(bases.length, dims, false);//MTXOFT
        // Gram-Schmidt orthogonalization
        for (int i = 0; i < bases.length; i++) {
            for (int j = 0; j < i; j++) {
                bases[i] = bases[i].minusB(NVector.lrProj(bases[j], bases[i]));
            }//System.out.println(bases);
            bases[i].ipNormalize();
            for (int j = 0; j < dims; j++) {
                basis.val[i][j] = bases[i].coords[j];
            }
        }
        orthogonal = true;
    }//System.out.println(basis);
    
    /**
     * Constructs a basis matrix straight out of the bases.
     */
    public void straightToBasis() {
        basis = new Matrix(bases.length, dims);
        for (int i = 0; i < bases.length; i++) {
            for (int j = 0; j < dims; j++) {
                basis.val[i][j] = bases[i].coords[j];
            }
        }
    }

    /**
     * Constructs a projection matrix out of the basis matrix.
     * @throws Exception 
     */
    public void calcProjection() throws Exception {//System.out.println(Matrix.lrMult(basis, Matrix.transpose(basis)));
        //TODO You know, maybe I should catch the error here - since, it's like definitionally fine.
        Matrix bt = Matrix.transposeWCache(basis);
        projection = Matrix.lrMult(basis, bt);//MTXOFT*
        bt.doneWithMatrix();
    }

    /**
     * Constructs a projection matrix out of the basis matrix.
     * @throws Exception 
     */
    public void calcProjectionWCache() throws Exception {//System.out.println(Matrix.lrMult(basis, Matrix.transpose(basis)));
        //TODO You know, maybe I should catch the error here - since, it's like definitionally fine.
        Matrix bt = Matrix.transposeWCache(basis);
        projection = Matrix.lrMultWCache(basis, bt);//MTXOFT*
        bt.doneWithMatrix();
    }
    
    /**
     * Turns the points into a basis.
     * Note that the first point is used as origin.
     * @param points
     * @return 
     */
    public static NBasis pointsToBases(ArrayList<NPoint> points) {
        int internalDims = points.size() - 1;
        int dims;
        if (points.size() > 0) {
            dims = points.get(0).dims;
        } else {
            return new NBasis(0, 0); // Pretty much useless.
        }
        NBasis result = new NBasis(dims, internalDims);
        result.origin = points.get(0).pos;
        for (int i = 1; i < internalDims + 1; i++) {
            result.bases[i - 1] = points.get(i).pos.minusB(result.origin);
        }
        return result;
    }

    /**
     * Turns the points into a basis.
     * Note that the first point is used as origin.
     * @param points
     * @return 
     */
    public static NBasis pointsToBases(NPoint[] points) {
        int internalDims = points.length - 1;
        int dims;
        if (points.length > 0) {
            dims = points[0].dims;
        } else {
            return new NBasis(0, 0); // Pretty much useless.
        }
        NBasis result = new NBasis(dims, internalDims);
        result.origin = points[0].pos;
        for (int i = 1; i < internalDims + 1; i++) {
            result.bases[i - 1] = points[i].pos.minusB(result.origin);
        }
        return result;
    }

    /**
     * Turns the vectors into a basis.
     * Note that the first vector is used as origin.
     * @param vectors
     * @return 
     */
    public static NBasis vectorsToBases(ArrayList<NVector> vectors) {
        int internalDims = vectors.size() - 1;
        int dims;
        if (vectors.size() > 0) {
            dims = vectors.get(0).dims;
        } else {
            return new NBasis(0, 0); // Pretty much useless.
        }
        NBasis result = new NBasis(dims, internalDims);
        result.origin = vectors.get(0);
        for (int i = 1; i < internalDims + 1; i++) {
            result.bases[i - 1] = vectors.get(i).minusB(result.origin);
        }
        return result;
    }

    /**
     * Checks to see if the bases are independent.
     * @return 
     */
    public boolean checkIndependent() {
        this.straightToBasis();
        Matrix m = basis.copy();
        try {
            m.ipRRowFormError();
        } catch (Exception e) {
            // Not independent.
            return false;
        }
        return true;
    }

    /**
     * Returns a hyperplane - it may have extra dimensions, as it is always n-1.
     * Uses the normal of the difference of the basis's origin and its projection onto the basis.
     * This thing is designed for use in subplanes, and you'll need to provide a common origin
     * located in the superplane in order to compare hyperplanes.
     * ...
     * Decipher that, eh.
     */
    public Hyperplane makeHyperplane(NVector commonOrigin) {
        Hyperplane result = new Hyperplane(dims);
        try {
            NVector newBasisOrigin = origin.minusB(commonOrigin);
            result.normal = Matrix.lrvMult(projection, newBasisOrigin).minusB(newBasisOrigin);
        } catch (Exception ex) {
            Logger.getLogger(NBasis.class.getName()).log(Level.SEVERE, null, ex);
        }
        result.displacement = 1;
        return result;
    }
//    /**
//     * See the other makeHyperplane; going to try it without a common origin in the superplane.
//     * Actually, maybe not.
//     * @return 
//     */
//    public Hyperplane makeHyperplane() {
//        Hyperplane result = new Hyperplane(dims);
//        try {
//            result.normal = Matrix.lrvMult(projection, origin).minusB(origin);
//        } catch (Exception ex) {
//            Logger.getLogger(NBasis.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        result.displacement = 1;
//        return result;
//    }
    public Random r = new Random();

    /**
     * Gives a random vector lying in the basis.
     * @return 
     */
    public NVector randomVector() {
        NVector random = new NVector(internalDims);
        for (int i = 0; i < internalDims; i++) {
            random.coords[i] = r.nextDouble() - 0.5;
        }
        try {
            return Matrix.lrvMult(basis, random);
        } catch (Exception ex) {
            Logger.getLogger(NBasis.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Creates a standardized basis - two n-planes that are parallel and coincide will produce
     * the same standardized basis.
     */
    public NBasis makeStandardized() {
        if (!orthogonal) {
            orthogonalize();
        }
        if (projection == null) {
            try {
                calcProjection();
            } catch (Exception ex) {
                Logger.getLogger(NBasis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        NVector[] mainBasis = new NVector[dims];
        for (int i = 0; i < dims; i++) {
            mainBasis[i] = new NVector(dims);
            for (int j = 0; j < dims; j++) {
                if (i == j) {
                    mainBasis[i].coords[j] = 1;
                } else {
                    mainBasis[i].coords[j] = 0;
                }
            }
        }

        //ArrayList<NVector> newBases = new ArrayList<NVector>();
        NBasis newBasis = new NBasis(dims, internalDims);
        int index = 0;
        for (int i = 0; i < dims; i++) {
            try {
                NVector proj = Matrix.lrvMult(projection, mainBasis[i]);
                //NVector diff = mainBasis[i].minusB(proj);
                if (((int) (proj.lengthSqr() * (1 << snapDigits))) != 0) {
                    newBasis.bases[index++] = proj;
                    if (index == internalDims) {
                        break;
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(NBasis.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        newBasis.orthogonalize();
        try {
            newBasis.calcProjection();
            newBasis.origin = origin.minusB(Matrix.lrvMult(projection, origin));
        } catch (Exception ex) {
            Logger.getLogger(NBasis.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newBasis;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        boolean changed = false;
        if (basis != null) {
            changed = true;
            for (int i = 0; i < basis.val.length; i++) {
                for (int j = 0; j < basis.val[i].length; j++) {
                    //hash += Double.doubleToLongBits(basis.val[i][j]);
                    hash += i * j * ((int) (basis.val[i][j] * (1 << snapDigits)));
                }
            }
        }
        if (origin != null) {
            changed = true;
            for (int i = 0; i < dims; i++) {
                //hash += Double.doubleToLongBits(origin.coords[i]);
                hash += i * ((int) (origin.coords[i] * (1 << snapDigits)));
            }
        }
        if (changed) {
            return hash;
        } else {
            return super.hashCode();
        }
    }

    @Override
    public boolean equals(Object obj) {
        NBasis b = ((NBasis) obj);
        if (this == b) {
            return true;
        }
        if (b == null) {
            return false;
        }
        if (basis != null) {
            if (b.basis == null) {
                return false;
            }
            if (origin != null) {
                if (b.origin == null) {
                    return false;
                }
                for (int i = 0; i < dims; i++) {
                    if (!MeMath.prettyEqual(origin.coords[i], b.origin.coords[i], snapDigits)) {
                        return false;
                    }
                }
            } else {
                if (b.origin != null) {
                    return false;
                }
            }
            for (int i = 0; i < basis.val.length; i++) {
                for (int j = 0; j < basis.val[i].length; j++) {
                    if (!MeMath.prettyEqual(basis.val[i][j], b.basis.val[i][j], snapDigits)) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            if (b.basis == null) {
                if (origin != null) {
                    if (b.origin == null) {
                        return false;
                    }
                    for (int i = 0; i < dims; i++) {
                        if (!MeMath.prettyEqual(origin.coords[i], b.origin.coords[i], snapDigits)) {
                            return false;
                        }
                    }
                    return true;
                } else {
                    if (b.origin == null) {
                        return true;
                    }
                    return false;
                }
            }
            return false;
        }
    }
    public int id = -2;
    //public ArrayList<Integer> basesIDs = null;
    public int basisId = 0;
    public int projectionId = 0;

    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(dims);
        dos.writeInt(internalDims);
        dos.writeInt(id);
        if (basis != null) {
            dos.writeInt(basis.id);
        } else {
            dos.writeInt(-1);
        }
        if (projection != null) {
            dos.writeInt(projection.id);
        } else {
            dos.writeInt(-1);
        }
        dos.writeInt(bases.length);
        for (int j = 0; j < bases.length; j++) {
            if (bases[j] != null) {
                for (int i = 0; i < dims; i++) {
                    dos.writeDouble(bases[j].coords[i]);
                }
            } else {
                for (int i = 0; i < dims; i++) {
                    dos.writeDouble(0);
                }
            }
        }
        if (origin != null) {
            for (int i = 0; i < dims; i++) {
                dos.writeDouble(origin.coords[i]);
            }
        } else {
            for (int i = 0; i < dims; i++) {
                dos.writeDouble(0);
            }
        }
        dos.writeBoolean(orthogonal);
        dos.writeInt(snapDigits);
    }

    public static Object fromBytes(DataInputStream dis) throws IOException {
        int dims = dis.readInt();
        int latticeDims = dis.readInt();
        NBasis result = new NBasis(dims, latticeDims);
        result.id = dis.readInt();
        result.basisId = dis.readInt();
        result.projectionId = dis.readInt();
        int baseCount = dis.readInt();
        for (int j = 0; j < baseCount; j++) {
            result.bases[j] = new NVector(dims);
            for (int i = 0; i < dims; i++) {
                result.bases[j].coords[i] = dis.readDouble();
            }
        }
        result.origin = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            result.origin.coords[i] = dis.readDouble();
        }
        result.orthogonal = dis.readBoolean();
        result.snapDigits = dis.readInt();
        return result;
    }

    /**
     * 
     * @param pos
     * @param dir
     * @return
     * @throws Exception 
     */
    public NVector orient(NVector pos, NVector dir) throws Exception {
        //THINK Wait, is the projection centered at the origin??  Maybe not.  Or...?
        //THINK Also, check this against the recently added code.
        return Matrix.lrvMult(projection, dir).ipNormalize().multS(dir.length());
    }
}
