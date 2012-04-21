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

/**
 *
 * @author mewer12
 */
public class NSurface {
    public int dims = 0;
    public int internalDims = 0;
    public NPoint[] points = null;
    public NObject parent = null;
    public RenderPixelFunction render = null;
    public Color color = null;
    public NBasis basis = null;
    
    public class RenderPixelFunction {
        public Color renderPix(NVector[] strikePoint, NVector strikeDir, NSurface strikeSurface) {
            return color;
        }
    }
    
    public NSurface(int dims, int internalDims, Color color) {
        this.dims = dims;
        this.internalDims = internalDims;
        points = new NPoint[internalDims + 1];
        render = new RenderPixelFunction();
        basis = new NBasis(dims, internalDims);
        this.color = color;
    }

    public int basisId = -1;
    public int id = -2;
    public ArrayList<Integer> pointIDs = null;
    
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(dims);
        dos.writeInt(internalDims);
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
        if (basis != null)
            dos.writeInt(basis.id);
        else 
            dos.writeInt(-1);
        dos.writeInt(id);
        dos.writeInt(points.length);
        for (int i = 0; i < points.length; i++) {
            if (points[i] != null)
                dos.writeInt(points[i].id);
            else 
                dos.writeInt(-1);
        }
    }    
    
    public static Object fromBytes(DataInputStream dis) throws IOException {
        int dims = dis.readInt();
        int internalDims = dis.readInt();
        int r = dis.readUnsignedByte();
        int g = dis.readUnsignedByte();
        int b = dis.readUnsignedByte();
        int a = dis.readUnsignedByte();
        NSurface result = new NSurface(dims, internalDims, new Color(r,g,b,a));
        result.basisId = dis.readInt();
        result.id = dis.readInt();
        result.pointIDs = new ArrayList<Integer>();
        int pointCount = dis.readInt();
        for (int i = 0; i < pointCount; i++) {
            result.pointIDs.add(dis.readInt());
        }
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

}
