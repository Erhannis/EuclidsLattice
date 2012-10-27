/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

/**
 * A mathematical hyperplane, given by a normal vector and a displacement scalar.
 * @author mewer12
 */
public class Hyperplane {

    public int dims = 0;
    public NVector normal = null;
    public double displacement = 0;
    public int nonZeroCoord = 0;
    public int snapDigits = 10;

    public Hyperplane(int dims) {
        this.dims = dims;
        normal = new NVector(dims);
    }

    public void calcNonZeroCoord() {
        for (int i = 0; i < dims; i++) {
            if (!MeMath.prettyZero(normal.coords[i], snapDigits)) {
                nonZeroCoord = i;
                break;
            }
        }
    }

    /**
     * Scales to make the displacement 1, if non-zero (to snapDigits binary digits).
     * Makes comparisons a lot easier.
     */
    public void normalize() {
        if (!MeMath.prettyZero(displacement, snapDigits)) {
            normal = normal.multS(1 / displacement);
            displacement = 1;
        } else {
            displacement = 0;
        }
    }

    public boolean equivalent(Hyperplane b) {
        if (displacement == 0) {
            if (b.displacement == 0) {
                if (dims != b.dims) {
                    return false;
                }
                for (int i = 0; i < dims; i++) {
                    if (!MeMath.prettyEqual(normal.coords[i], b.normal.coords[i], snapDigits)) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            if (b.displacement == 0) {
                return false;
            } else {
                if (dims != b.dims) {
                    return false;
                }
                for (int i = 0; i < dims; i++) {
                    if (!MeMath.prettyEqual(normal.coords[i], b.normal.coords[i], snapDigits)) {
                        return false;
                    } else {
                        return true;
                    }
                }
                return true;
            }
        }
    }
}
