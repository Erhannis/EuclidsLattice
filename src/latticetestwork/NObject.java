/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.util.ArrayList;

/**
 *
 * @author mewer12
 */
public class NObject {
    public int dims = 0;
    public int internalDims = 0;
    public ArrayList<NPoint> points = null;
    public ArrayList<NBone> bones = null;
    
    public NObject(int dims, int internalDims) {
        this.dims = dims;
        this.internalDims = internalDims;
        this.points = new ArrayList<NPoint>();
        this.bones = new ArrayList<NBone>();
    }
}
