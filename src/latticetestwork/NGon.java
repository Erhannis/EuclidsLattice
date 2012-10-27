/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author mewer12
 */
public class NGon {
    public ArrayList<NPoint> points = null;
    public HashMap<NPoint, HashSet<NPoint>> connectsTo = null;
    public int pointsForCell = 0;
    public int dims = 0;
    
    public NGon(int dims, int pointsForCell) {
        this.dims = dims;
        this.pointsForCell = pointsForCell;
        this.points = new ArrayList<NPoint>();
        this.connectsTo = new HashMap<NPoint, HashSet<NPoint>>();
    }
    
    public void collectPointsFromConnections() {
        points.clear();
        for (NPoint i : connectsTo.keySet()) {
            points.add(i);
        }
    }
}
