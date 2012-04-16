/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewer12
 */
public class NFace implements Streamable {

    public int dims = 0;
    public int latticeDims = 0;
    public NPoint[] points = null;
    public NCell cellA = null;
    public NCell cellB = null;
    public NBasis basis = null;
    public NVector aDir = null;
    public NVector bDir = null;
    public double angle = 0;

    public NFace(int dims, int latticeDims) {
        this.dims = dims;
        this.latticeDims = latticeDims;
        points = new NPoint[latticeDims];
    }

    public boolean complete() {
        return ((cellA != null) && (cellB != null));
    }

    public boolean isaPoint(NPoint p) {
        for (NPoint i : points) {
            if (i == p) {
                return true;
            }
        }
        return false;
    }

    public boolean equivalent(NFace f) {
        // Technically, checking a face with a superset of this's points would also return true.
        for (int i = 0; i < this.points.length; i++) {
            boolean found = false;
            for (int j = 0; j < f.points.length; j++) {
                if (this.points[i] == f.points[j]) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public boolean equivalent(ArrayList<NPoint> points) {
        // Technically, checking a face with a superset of this's points would also return true.
        for (int i = 0; i < this.points.length; i++) {
            boolean found = false;
            for (int j = 0; j < points.size(); j++) {
                if (this.points[i] == points.get(j)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * "Informs" all the points of the face that they are now a part of it.
     */
    public void informPoints() {
        for (NPoint p : points) {
            p.faces.add(this);
        }
    }

    /**
     * Warning: because of why I made this function, it does not add itself to the
     * points' lists, or any other list.  It changes nothing.
     * @param points
     * @return 
     */
    public static NFace faceFromPoints(NPoint[] points) {
        NFace result = new NFace(points[0].dims, points.length);
        for (int i = 0; i < points.length; i++) {
            result.points[i] = points[i];
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{F");
        for (int i = 0; i < points.length; i++) {
            sb.append(points[i].toString());
        }
        sb.append("}");
        return sb.toString();
    }
    public int id = -2;
    public ArrayList<Integer> pointIDs = null;
    public int cellAid = 0;
    public int cellBid = 0;
    public int basisId = 0;

    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(dims);
        dos.writeInt(latticeDims);
        if (cellA != null) {
            dos.writeInt(cellA.id);
        } else {
            dos.writeInt(-1);
        }
        if (cellB != null) {
            dos.writeInt(cellB.id);
        } else {
            dos.writeInt(-1);
        }
        dos.writeInt(id);
        dos.writeInt(points.length);
        for (int i = 0; i < points.length; i++) {
            if (points[i] != null) {
                dos.writeInt(points[i].id);
            } else {
                dos.writeInt(-1);
            }
        }
        dos.writeInt(basis.id);
        if (aDir != null) {
            aDir.toBytes(dos);
        } else {
            new NVector(dims).toBytes(dos);
        }
        if (bDir != null) {
            bDir.toBytes(dos);
        } else {
            new NVector(dims).toBytes(dos);
        }
        dos.writeDouble(angle);
    }

    public static Object fromBytes(DataInputStream dis) throws IOException {
        int dims = dis.readInt();
        int latticeDims = dis.readInt();
        NFace result = new NFace(dims, latticeDims);
        result.cellAid = dis.readInt();
        result.cellBid = dis.readInt();
        result.id = dis.readInt();
        result.pointIDs = new ArrayList<Integer>();
        int pointCount = dis.readInt();
        for (int i = 0; i < pointCount; i++) {
            result.pointIDs.add(dis.readInt());
        }
        result.basisId = dis.readInt();
        result.aDir = NVector.fromBytes(dis);
        result.bDir = NVector.fromBytes(dis);
        result.angle = dis.readDouble();
        return result;
    }

    /**
     * Calculates the basis in the face.
     */
    public void calcBasis() {
        this.basis = NBasis.pointsToBases(points);
        basis.makeStandardized();
    }

    /**
     * When moving from one cell to another, there's a rotation that must take place as you cross the corner between.
     * Calculate it, moving from A to B.
     * Man, I could swear I already did this or something....
     */
    public void calcRotations() {
        NVector vA = null;
        for (NPoint a : cellA.points) {
            if (!isaPoint(a)) {
                vA = a.pos.minusB(points[0].pos);
                break;
            }
        }
        NVector vB = null;
        for (NPoint b : cellB.points) {
            if (!isaPoint(b)) {
                vB = b.pos.minusB(points[0].pos);
                break;
            }
        }
        try {//System.out.println(basis.projection);
            aDir = Matrix.lrvMult(basis.projection, vA).minusB(vA);
            bDir = vB.minusB(Matrix.lrvMult(basis.projection, vB));
            angle = NVector.angle(aDir, bDir);
        } catch (Exception ex) {
            Logger.getLogger(NFace.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public NVector crossCornerVector(NCell from, NVector dir) {
        if (from == cellA) {
            try {
                return NVector.rotate(aDir, bDir, dir, angle);
            } catch (Exception ex) {
                Logger.getLogger(NFace.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        } else { // Just gonna assume here they haven't given me something unrelated.
            // "Yeah, I'm gonna cross the Canadian border into Germany."
            try {
                return NVector.rotate(bDir, aDir, dir, angle);
            } catch (Exception ex) {
                Logger.getLogger(NFace.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    public NCell crossCornerCell(NCell from) {
        if (from == cellA) {
            return cellB;
        } else { // Just gonna assume here they haven't given me something unrelated.
            // "Yeah, I'm gonna cross the Canadian border into Germany."
            return cellA;
        }
    }

    /**
     * Makes final calculations, after face is set.
     */
    public void calcAll() {
        calcBasis();
        calcRotations();
    }

    public boolean containsPoint(NVector hit) {
        //NVector hit = Matrix.lineNPlaneIntersect(pos, dir, f.basis.origin, f.basis.bases);
        NVector[] ptVectors = new NVector[points.length + 1];
        ptVectors[0] = hit;
        for (int i = 0; i < points.length; i++) {
            ptVectors[i + 1] = points[i].pos;
        }
        NVector[] flatVectors = Matrix.ipTransformCoords(basis.bases, ptVectors);
        Matrix baryM = new Matrix(flatVectors.length, (flatVectors.length > 0 ? flatVectors[0].coords.length : 0));
        for (int x = 0; x < baryM.cols - 1; x++) {
            for (int y = 0; y < baryM.rows; y++) {
                baryM.val[x][y] = flatVectors[x + 1].coords[y] - flatVectors[flatVectors.length - 1].coords[y];
            }
        }
        int x = baryM.cols - 1;
        for (int y = 0; y < baryM.rows; y++) {
            baryM.val[x][y] = flatVectors[0].coords[y] - flatVectors[flatVectors.length - 1].coords[y];
        }
        baryM.ipRRowForm();
        x = baryM.cols - 1;
        double[] baryCoords = new double[baryM.rows + 1];
        baryCoords[baryM.rows] = 1;
        for (int y = 0; y < baryM.rows; y++) {
            baryCoords[y] = baryM.val[x][y];
            if (baryCoords[y] < 0 || baryCoords[y] > 1) {
                return false;
            }
            baryCoords[baryM.rows] -= baryCoords[y];
        }
        if (baryCoords[baryM.rows] < 0 || baryCoords[baryM.rows] > 1) {
            return false;
        }
        return true;
    }
    
    public boolean isConnected(NFace f) {
        int matched = points.length - 1;
        for (NPoint a : this.points) {
            for (NPoint b : this.points) {
                if (a == b) {
                    matched--;
                    break;
                }
            }
            if (matched <= 0) {
                break;
            }
        }
        if (matched <= 0) {
            return true;
        }
        return false;
    }
}