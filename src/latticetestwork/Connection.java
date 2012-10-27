/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

/**
 *
 * @author mewer12
 */
public class Connection {
    public NPoint a = null;
    public NPoint b = null;
    
    public Connection(NPoint a, NPoint b) {
        if (a != null) {
            this.a = a;
        } else {
            this.a = new NPoint(0);
        }
        if (b != null) {
            this.b = b;
        } else {
            this.b = new NPoint(0);
        }
    }
    
    public int hashCode() {
        return a.hashCode() + b.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Connection)) {
            return false;
        }
        return (((((Connection)obj).a == a) && (((Connection)obj).b == b)) || ((((Connection)obj).a == b) && (((Connection)obj).b == a)));
    }
}
