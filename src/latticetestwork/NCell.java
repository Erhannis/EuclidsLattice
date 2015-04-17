/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Color;
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
public class NCell implements Streamable {

    public int dims = 0;
    public int cellDims = 0;
    public NPoint[] points = null;
    public NFace[] faces = null;
    public NBasis basis = null;
    public ArrayList<NSurface> surfaces = null;
    public Color color = null;

    public NCell(int dims, int cellDims) {
        this.dims = dims;
        this.cellDims = cellDims;
        points = new NPoint[cellDims + 1];
        faces = new NFace[cellDims + 1];
        basis = new NBasis(dims, cellDims);
        surfaces = new ArrayList<NSurface>();
        Random r = new Random();
        color = new Color(r.nextInt(0x100), r.nextInt(0x100), r.nextInt(0x100), 0x00);
    }

    public boolean isaPoint(NPoint p) {
        for (NPoint i : points) {
            if (i == p) {
                return true;
            }
        }
        return false;
    }

    /**
     * Warning: because of why I made this function, the cell does not add itself
     * to the faces' lists, nor does it add the faces to any other list.
     */
    public void soloMakeFaces() {
        for (int i = 0; i < points.length; i++) {
            NPoint[] facePoints = new NPoint[points.length - 1];
            int index = 0;
            for (int j = 0; j < points.length; j++) {
                if (j != i) {
                    facePoints[index++] = points[j];
                }
            }

            this.faces[i] = NFace.faceFromPoints(facePoints);
        }
    }

    public static NCell fromPointAndSubface(NPoint p, NFace f) {
        NCell result = null;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{C");
        for (int i = 0; i < points.length; i++) {
            sb.append(points[i].toString());
        }
        sb.append("}");
        return sb.toString();
    }

    public double getVolume() {
        NBasis volBasis = new NBasis(dims, cellDims);
        ArrayList<NPoint> ptList = new ArrayList<NPoint>();
        volBasis.bases[0] = NVector.zero(dims);
        for (int i = 0; i < volBasis.bases.length; i++) {
            volBasis.bases[i] = points[i + 1].pos.minusB(points[0].pos);
        }
        volBasis.orthogonalize();
        try {
            volBasis.calcProjection();
        } catch (Exception ex) {
            Logger.getLogger(NCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        NVector[] ptVectors = new NVector[points.length];
//        for (int i = 0; i < points.length; i++) {
//            ptVectors[i] = points[i].pos;
//        }
        ptVectors[0] = new NVector(dims);
        for (int i = 1; i < points.length; i++) {
            ptVectors[i] = points[i].pos.minusB(points[0].pos);
        }
        NVector[] result = Matrix.ipTransformCoords(volBasis.bases, ptVectors);
        Matrix m = new Matrix(cellDims, cellDims);
        for (int i = 1; i < result.length; i++) {
            NVector diff = result[i].minusB(result[0]);
            for (int j = 0; j < result[i].dims; j++) {
                m.val[i - 1][j] = diff.coords[j];
            }
        }
        try {
            return ((1.0 / MeMath.factorial(cellDims)) * Math.abs(m.det()));
        } catch (Exception ex) {
            Logger.getLogger(NCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1000; // Yeah, yeah, yeah.
    }

    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(dims);
        dos.writeInt(cellDims);
        if (basis != null)
            dos.writeInt(basis.id);
        else 
            dos.writeInt(-1);
        dos.writeInt(id);
        dos.writeInt(faces.length);
        for (int i = 0; i < faces.length; i++) {
            if (faces[i] != null)
                dos.writeInt(faces[i].id);
            else 
                dos.writeInt(-1);
        }
        dos.writeInt(points.length);
        for (int i = 0; i < points.length; i++) {
            if (points[i] != null)
                dos.writeInt(points[i].id);
            else 
                dos.writeInt(-1);
        }
        dos.writeInt(surfaces.size());
        for (int i = 0; i < surfaces.size(); i++) {
            if (surfaces.get(i) != null)
                dos.writeInt(surfaces.get(i).id);
            else 
                dos.writeInt(-1);
        }
        if (color != null) {
            dos.writeByte(color.getRed());
            dos.writeByte(color.getGreen());
            dos.writeByte(color.getBlue());
            dos.writeByte(color.getAlpha());
        } else {
            dos.writeByte(0x00);
            dos.writeByte(0x00);
            dos.writeByte(0x00);
            dos.writeByte(0x00);
        }
    }

    public int id = -2;
    public ArrayList<Integer> faceIDs = null;
    public ArrayList<Integer> pointIDs = null;
    public ArrayList<Integer> surfaceIDs = null;
    public int basisId = -1;
    
    public static Object fromBytes(DataInputStream dis) throws IOException {
        int dims = dis.readInt();
        int latticeDims = dis.readInt();
        NCell result = new NCell(dims, latticeDims);
        result.basisId = dis.readInt();
        result.id = dis.readInt();
        result.faceIDs = new ArrayList<Integer>();
        int faceCount = dis.readInt();
        for (int i = 0; i < faceCount; i++) {
            result.faceIDs.add(dis.readInt());
        }
        result.pointIDs = new ArrayList<Integer>();
        int pointCount = dis.readInt();
        for (int i = 0; i < pointCount; i++) {
            result.pointIDs.add(dis.readInt());
        }
        result.surfaceIDs = new ArrayList<Integer>();
        int surfaceCount = dis.readInt();
        for (int i = 0; i < surfaceCount; i++) {
            result.surfaceIDs.add(dis.readInt());
        }
        int r = dis.readUnsignedByte();
        int g = dis.readUnsignedByte();
        int b = dis.readUnsignedByte();
        int a = dis.readUnsignedByte();
        result.color = new Color(r,g,b,a);
        return result;
    }
    
    public void makeBasis() {
        try {
        ArrayList<NPoint> bucket = new ArrayList<NPoint>(points.length);
        for (int i = 0; i < points.length; i++) {
            bucket.add(points[i]);
        }//System.out.println(bucket);
        basis = NBasis.pointsToBases(bucket);
        basis.makeStandardized();
        } catch (NullPointerException e) {
            boolean manualRetry = false;
            if (manualRetry) {
                makeBasis();
            }
        }
    }
    
    public boolean equivalent(NCell c) {
        // Technically, checking a cell with a superset of this's points would also return true.
        for (int i = 0; i < this.points.length; i++) {
            boolean found = false;
            for (int j = 0; j < c.points.length; j++) {
                if (this.points[i] == c.points[j]) {
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
        // Technically, checking a cell with a superset of this's points would also return true.
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
}
