/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

/**
 *
 * @author mewer12
 */
public class NBone {
    public int dims = 0;
    public NPoint a = null;
    public NPoint b = null;
    public NObject parent = null;
    public double rigidity = 1;
    
    public NBone(int dims) {
        this.dims = dims;
        a = new NPoint(dims);
        b = new NPoint(dims);
    }
}
