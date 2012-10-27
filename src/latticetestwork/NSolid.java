package latticetestwork;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NSolid {

    public int dims = 0;
    public int actualDims = 0;
    public ArrayList<NPoint> points = null;
    /**
     * How many digits checked for equality.
     * It comes into play when checking if a point is on the cutting plane.
     */
    public int snapDigits = 10;

    public NSolid(int dims, int actualDims) {
        this.dims = dims;
        this.actualDims = actualDims;
        this.points = new ArrayList<NPoint>();
    }
    public HashSet<Connection> outerFrame = null;

    public NSolid[] divide() throws Exception {
        if (this.points.size() <= dims + 1) {
            throw new Exception("Cannot be divided.");
        }

        ArrayList<NPoint> nPlane = new ArrayList<NPoint>();
        outerFrame = getOuterFrame(nPlane, 0);
        nPlane = new ArrayList<NPoint>();

        NSolid[] result = new NSolid[2];
        if (divideRecurse(nPlane, 0)) {
            result[0] = new NSolid(dims, actualDims);
            result[0].points.addAll(left);
            result[0].points.addAll(middle);
            result[1] = new NSolid(dims, actualDims);
            result[1].points.addAll(right);
            result[1].points.addAll(middle);
            return result;
        } else {
            throw new Exception("Cannot be divided.");
        }
    }

    public boolean divideRecurse(ArrayList<NPoint> nPlane, int index) {
        if (nPlane.size() < dims) {
            for (int i = index; i < points.size(); i++) {
                if (!nPlane.contains(points.get(i))) {
                    nPlane.add(points.get(i));
                    if (divideRecurse(nPlane, i + 1)) {
                        return true;
                    }
                    nPlane.remove(points.get(i));
                }
            }
            return false;
        } else if (nPlane.size() == dims) {
            // Check for proper division
            return checkDivision(nPlane);
        }
        return false;
    }
    public ArrayList<NPoint> left = null;
    public ArrayList<NPoint> middle = null;
    public ArrayList<NPoint> right = null;

    public boolean checkDivision(ArrayList<NPoint> nPlane) {
        // Check that plane points are independent.
        NBasis basis = NBasis.pointsToBases(nPlane);
        if (!basis.checkIndependent()) {
            return false;
        }
        basis.orthogonalize();
        try {
            basis.calcProjection();
        } catch (Exception ex) {
            Logger.getLogger(NSolid.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Sort points into 3 groups: left, middle, right.  Need some of each.
        left = new ArrayList<NPoint>();
        middle = new ArrayList<NPoint>();
        right = new ArrayList<NPoint>();
        int refCoord = -1;
        for (int i = 0; i < points.size(); i++) {//System.out.println(basis.projection);
            try {
                NVector difference = points.get(i).pos.minusB(Matrix.lrvMult(basis.projection, points.get(i).pos.minusB(basis.origin)).plusB(basis.origin));
                boolean zero = true;
                if (refCoord == -1) {
                    for (int j = 0; j < actualDims; j++) {
                        if (!MeMath.prettyZero(difference.coords[j], snapDigits)) {
                            refCoord = j;
                            zero = false;
                            break;
                        }
                    }
                } else {
                    if (!MeMath.prettyZero(difference.coords[refCoord], snapDigits)) {
                        zero = false;
                    }
                }
                if (zero) {
                    middle.add(points.get(i));
                } else if (difference.coords[refCoord] < 0) {
                    left.add(points.get(i));
                } else {
                    right.add(points.get(i));
                }
            } catch (Exception ex) {
                Logger.getLogger(NSolid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        if (middle.size() != dims)
//            return false;
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        } else {
            for (Connection c : outerFrame) {
                if ((left.contains(c.a) && right.contains(c.b)) || (left.contains(c.b) && right.contains(c.a))) {
                    return false;
                }
            }
            return true;
        }
    }

    public HashSet<Connection> getOuterFrame(ArrayList<NPoint> nPlane, int index) {
        HashSet<Connection> result = new HashSet<Connection>();
        if (nPlane.size() < dims) {
            for (int i = index; i < points.size(); i++) {
                if (!nPlane.contains(points.get(i))) {
                    nPlane.add(points.get(i));
                    result.addAll(getOuterFrame(nPlane, i + 1));

//                    // For debugging.
//                    for (int j = 0; j < result.size() - 1; j++) {
//                        for (int k = j + 1; k < result.size(); k++) {
//                            if (((result.get(j)[0] == result.get(k)[0]) && (result.get(j)[1] == result.get(k)[1]))
//                             || ((result.get(j)[0] == result.get(k)[1]) && (result.get(j)[1] == result.get(k)[0]))) {
//                                System.err.println("Duplicate!");
//                            }
//                        }
//                    }

                    nPlane.remove(points.get(i));
                }
            }
            return result;
        } else if (nPlane.size() == dims) {
            // Check for proper division
            return checkDivisionFrame(nPlane);
        }
        return result;
    }
    
//    public HashSet<ArrayList<Hyperplane>> previousPlanes = null;
    public HashSet<NBasis> previousPlanes = null;
//    public HashSet<NVector> previousPlanesCommonOrigins = null;
//    public NVector commonOrigin = null;

    public HashSet<Connection> checkDivisionFrame(ArrayList<NPoint> nPlane) {
        HashSet<Connection> result = new HashSet<Connection>();
        // Check that plane points are independent.
        NBasis basis = NBasis.pointsToBases(nPlane);
        if (!basis.checkIndependent()) {
            return result;
        }
        basis.orthogonalize();
        try {
            basis.calcProjection();
        } catch (Exception ex) {
            Logger.getLogger(NSolid.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (previousPlanes != null) {
            NBasis standard = basis.makeStandardized();
            if (previousPlanes.contains(standard)) {
                return result;
            }
            previousPlanes.add(standard);
        } else {
            previousPlanes = new HashSet<NBasis>();
            previousPlanes.add(basis.makeStandardized());
        }
        
        // Sort points into 3 groups: left, middle, right.  Need some of each.
        left = new ArrayList<NPoint>();
        middle = new ArrayList<NPoint>();
        right = new ArrayList<NPoint>();
        int refCoord = -1;
        for (int i = 0; i < points.size(); i++) {//System.out.println(basis.projection);
            try {
                NVector difference = points.get(i).pos.minusB(Matrix.lrvMult(basis.projection, points.get(i).pos.minusB(basis.origin)).plusB(basis.origin));
                boolean zero = true;
                if (refCoord == -1) {
                    for (int j = 0; j < actualDims; j++) {
                        if (!MeMath.prettyZero(difference.coords[j], snapDigits)) {
                            refCoord = j;
                            zero = false;
                            break;
                        }
                    }
                } else {
                    if (!MeMath.prettyZero(difference.coords[refCoord], snapDigits)) {
                        zero = false;
                    }
                }
                if (zero) {
                    middle.add(points.get(i));
                } else if (difference.coords[refCoord] < 0) {
                    left.add(points.get(i));
                } else {
                    right.add(points.get(i));
                }
            } catch (Exception ex) {
                Logger.getLogger(NSolid.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if ((!left.isEmpty()) && (!right.isEmpty())) {
            // Not an outer face.
            return new HashSet<Connection>();
        } else {
            // Is an outer face
            if (middle.size() == dims) {
                // It's a simplex - just connect them all and send it back.
                for (int i = 0; i < middle.size() - 1; i++) {
                    for (int j = i + 1; j < middle.size(); j++) {
                        result.add(new Connection(middle.get(i), middle.get(j)));
                    }
                }
                return result;
            } else {
                // Oh, great.  It (should) have extra points.  Recurse to fix.
                NSolid subLevel = new NSolid(dims - 1, actualDims);
                subLevel.points = middle;  //NOTE I think this is safe...right?  Probably not for parallel.
                ArrayList<NPoint> nSubPlane = new ArrayList<NPoint>();
                result.addAll(subLevel.getOuterFrame(nSubPlane, 0));
                return result;
            }
        }
    }
}
