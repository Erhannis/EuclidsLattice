/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewer12
 */
public class Lattice {
    
    public int dims = 0;
    public int internalDims = 0;
    public ArrayList<NPoint> points = null;
    public ArrayList<NFace> incompleteFaces = null;
    public ArrayList<NFace> faces = null;
    public ArrayList<NCell> cells = null;
    public ArrayList<NSurface> surfaces = null;
    public ArrayList<Camera> cameras = null;
    public ArrayList<ArrayList<NVector>> tracers = null;
    
    public Lattice(int dims, int internalDims) {
        this.dims = dims;
        this.internalDims = internalDims;
        this.points = new ArrayList<NPoint>();
        this.incompleteFaces = new ArrayList<NFace>();
        this.faces = new ArrayList<NFace>();
        this.cells = new ArrayList<NCell>();
        this.surfaces = new ArrayList<NSurface>();
        this.cameras = new ArrayList<Camera>();
        this.tracers = new ArrayList<ArrayList<NVector>>();
    }
    
    public void addPoint(NPoint point) {
        points.add(point);
    }
    
    public byte[] toByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LatticeForm.saveLattice(baos, this);
        //THINK This has the potential to use memory stupidly.
        return baos.toByteArray();
    }
    
    public static Lattice fromByteArray(byte[] lattice) {
        ByteArrayInputStream bais = new ByteArrayInputStream(lattice);
        Lattice l = LatticeForm.loadLattice(bais);
        try {
            bais.close();
        } catch (IOException ex) {
            Logger.getLogger(Lattice.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }
}
