/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

/**
 *
 * @author mewer12
 */
public class NLine {
    public int dims = 0;
    public NPoint a = null;
    public NPoint b = null;
    
    public NLine(int dims) {
        this.dims = dims;
        a = new NPoint(dims);
        b = new NPoint(dims);
    }
}
