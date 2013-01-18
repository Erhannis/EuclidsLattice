/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author mewer12
 */
public class NPoint implements Streamable {

    public int dims = 0;
    public NVector pos = null;
    public NVector posOffhand = null;
    public HashSet<NFace> faces = null;
    public boolean complete = false;
    public boolean immune = false;

    // This just stores where the point was last shown to allow one to click on it.
    public Point2D displayPoint = new Point2D.Double();
    public Point2D displayPointStereo = new Point2D.Double();
    
    public Color color;

    public ArrayList<NPoint> candidates = null;
    
    public NPoint(int dims) {
        this.dims = dims;
        pos = new NVector(dims);
        faces = new HashSet<NFace>();
        
        if (Engine.stereo4) { 
            this.color = new Color(Engine.r.nextInt(0x1000000));
        } else {
            color = Color.BLACK;
        }
    }

    public double distSqr(NPoint p) {
        double dist = 0;
        for (int i = 0; i < dims; i++) {
            dist += MeMath.sqr(this.pos.coords[i] - p.pos.coords[i]);
        }
        return dist;
    }

    public double dist(NPoint p) {
        double dist = 0;
        for (int i = 0; i < dims; i++) {
            dist += MeMath.sqr(this.pos.coords[i] - p.pos.coords[i]);
        }
        return Math.sqrt(dist);
    }

    public boolean calcComplete() {
        if (faces.size() > 0) {
            boolean allComplete = true;
            for (NFace i : faces) {
                if (!i.complete()) {
                    allComplete = false;
                    break;
                }
            }
            if (allComplete) {
                complete = true;
                return true;
            }
        }
        complete = false;
        return false;
    }

    public void swapHands() {
        if (posOffhand == null) {
            posOffhand = pos.copy();
        } else {
            NVector bucket = pos;
            pos = posOffhand;
            posOffhand = bucket;
        }
    }

    public void popCopyHands() {
        if (posOffhand == null) {
            posOffhand = pos.copy();
        } else {
            pos = posOffhand.copy();
        }
    }

    public void pushCopyHands() {
        if (posOffhand == null) {
            posOffhand = pos.copy();
        } else {
            posOffhand = pos.copy();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (int i = 0; i < pos.coords.length - 1; i++) {
            sb.append((((int) (pos.coords[i] * 1000)) / 1000.0) + ",");
        }
        sb.append((((int) (pos.coords[pos.coords.length - 1] * 1000)) / 1000.0) + "}");
        return sb.toString();
    }

    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(dims);
        dos.writeBoolean(complete);
        dos.writeInt(id);
        dos.writeInt(faces.size());
        for (NFace f : faces) {
            if (f != null) {
                dos.writeInt(f.id);
            } else {
                dos.writeInt(-1);
            }
        }
        dos.writeBoolean(immune);
        for (int i = 0; i < dims; i++) {
            dos.writeDouble(pos.coords[i]);
        }
        if (posOffhand != null) {
            for (int i = 0; i < dims; i++) {
                dos.writeDouble(posOffhand.coords[i]);
            }
        } else {
            for (int i = 0; i < dims; i++) {
                dos.writeDouble(pos.coords[i]);
            }
        }
    }
    public int id = -2;
    public ArrayList<Integer> faceIDs = null;

    public static Object fromBytes(DataInputStream dis) throws IOException {
        int dims = dis.readInt();
        NPoint result = new NPoint(dims);
        result.complete = dis.readBoolean();
        result.id = dis.readInt();
        result.faceIDs = new ArrayList<Integer>();
        int faceCount = dis.readInt();
        for (int i = 0; i < faceCount; i++) {
            result.faceIDs.add(dis.readInt());
        }
        result.immune = dis.readBoolean();
        result.pos = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            result.pos.coords[i] = dis.readDouble();
        }
        result.posOffhand = new NVector(dims);
        for (int i = 0; i < dims; i++) {
            result.posOffhand.coords[i] = dis.readDouble();
        }
        return result;
    }
}
