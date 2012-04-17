/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matthew Ewer <Ewer.Matthew@gmail.com>
 */
public class SimplePhoton {

    public int dims = 0;
    public NVector pos = null; // Maybe a point?
    public NVector dir = null;
    public Color color = null;
    public NCell cell = null;
    public NFace crossedFace = null;
    public double dtl = 0; // Distance to live

    /**
     * Ok, so this is a photon.  Well, a reverse photon, I guess, since it goes out from the eyes.
     * Huh, maybe it should start at black and accumulate color along the way....  We'll see.
     * Dtl is distance-to-live.  Color is the starting color (? Testing.).  The others should be fairly clear.
     */
    public SimplePhoton(int dims, NVector pos, NVector dir, Color color, NCell cell, double dtl) {
        this.dims = dims;
        this.pos = pos;
        this.dir = dir;
        this.color = color;
        this.cell = cell;
        this.dtl = dtl;
    }

    public Color proceed() {
        // So, want to continue through the cell.
        // If there's nothing in the cell, go straight to the other side.
        // If there is, check for collisions.
        double percentOfIncidence = 1;
        while (dtl > 0) {
            if (cell.surfaces.size() > 0) {
                //TODO DO SOMETHING
                // Ok, for now, I'm gonna just straight up say "HEY THRS SUMTHIN HERE!"
                if (cell.surfaces.get(0) == null) {
                    return new Color((int) (cell.color.getRed() * percentOfIncidence), (int) (cell.color.getGreen() * percentOfIncidence), (int) (cell.color.getBlue() * percentOfIncidence));
                } else {
                    //TODO Actually deal with the surfaces.
                    // Check which side you hit
                    NSurface hitSurface = null;
                    NVector hit = null;
                    for (NSurface s : cell.surfaces) {
                        //TODO Maybe replace this with f.containsPoint?  May be slightly less efficient, though.
//                        if (f == crossedFace) {
//                            continue;
//                        }
                        if (s == null) {
                            continue;
                        }
                        hitSurface = s;
                        hit = Matrix.lineNPlaneIntersect(pos, dir, s.basis.origin, s.basis.bases);
                        if (!NVector.sameQuadrant(dir, hit.minusB(pos))) {
                            continue;
                        }
                        NVector[] ptVectors = new NVector[s.points.length + 1];
                        ptVectors[0] = hit;
                        for (int i = 0; i < s.points.length; i++) {
                            ptVectors[i + 1] = s.points[i].pos;
                        }
                        NVector[] flatVectors = Matrix.ipTransformCoords(s.basis.bases, ptVectors);
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
                        boolean outside = false;
                        for (int y = 0; y < baryM.rows; y++) {
                            baryCoords[y] = baryM.val[x][y];
                            if (baryCoords[y] < 0 || baryCoords[y] > 1) {
                                outside = true;
                                break;
                            }
                            baryCoords[baryM.rows] -= baryCoords[y];
                        }
                        if (baryCoords[baryM.rows] < 0 || baryCoords[baryM.rows] > 1) {
                            outside = true;
                        }
                        if (!outside) {
                            break;
                        }
                    }
                    // Theoretically, now hitFace has what face we hit, and hit has where we ended up.
                    // Go there, and subtract from dtl.
                    //dtl -= hit.minusB(pos).length();
                    try {
                        percentOfIncidence = 2 * NVector.angle(Matrix.lrvMult(hitSurface.basis.projection, pos.minusB(hit)), pos.minusB(hit)) / (Math.PI);
                    } catch (Exception ex) {
                        Logger.getLogger(SimplePhoton.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    pos = hit;
//                    dir = hitSurface.crossCornerVector(cell, dir);
//                    if (hitSurface.crossCornerCell(cell) != null) {
//                        cell = hitFace.crossCornerCell(cell);
//                    } else {
//                        return cell.color;
//                    }
//                    crossedFace = hitFace;
                    return new Color((int) (hitSurface.color.getRed() * percentOfIncidence), (int) (hitSurface.color.getGreen() * percentOfIncidence), (int) (hitSurface.color.getBlue() * percentOfIncidence));
                }
//                return cell.color;
                //return cell.surfaces.get(0).render.renderPix(null, null, null);
            } else {
                // Check which side you hit
                NFace hitFace = null;
                NVector hit = null;
                for (NFace f : cell.faces) {
                    //TODO Maybe replace this with f.containsPoint?  May be slightly less efficient, though.
                    if (f == crossedFace) {
                        continue;
                    }
                    hitFace = f;
                    hit = Matrix.lineNPlaneIntersect(pos, dir, f.basis.origin, f.basis.bases);
                    if (!NVector.sameQuadrant(dir, hit.minusB(pos))) {
                        continue;
                    }
                    NVector[] ptVectors = new NVector[f.points.length + 1];
                    ptVectors[0] = hit;
                    for (int i = 0; i < f.points.length; i++) {
                        ptVectors[i + 1] = f.points[i].pos;
                    }
                    NVector[] flatVectors = Matrix.ipTransformCoords(f.basis.bases, ptVectors);
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
                    boolean outside = false;
                    for (int y = 0; y < baryM.rows; y++) {
                        baryCoords[y] = baryM.val[x][y];
                        if (baryCoords[y] < 0 || baryCoords[y] > 1) {
                            outside = true;
                            break;
                        }
                        baryCoords[baryM.rows] -= baryCoords[y];
                    }
                    if (baryCoords[baryM.rows] < 0 || baryCoords[baryM.rows] > 1) {
                        outside = true;
                    }
                    if (!outside) {
                        break;
                    }
                }
                // Theoretically, now hitFace has what face we hit, and hit has where we ended up.
                // Go there, and subtract from dtl.
                dtl -= hit.minusB(pos).length();
                try {
                    percentOfIncidence = 2 * NVector.angle(Matrix.lrvMult(hitFace.basis.projection, pos.minusB(hit)), pos.minusB(hit)) / (Math.PI);
                } catch (Exception ex) {
                    Logger.getLogger(SimplePhoton.class.getName()).log(Level.SEVERE, null, ex);
                }
                pos = hit;
                if (hitFace.crossCornerCell(cell) != null) {
                    dir = hitFace.crossCornerVector(cell, dir);
                    cell = hitFace.crossCornerCell(cell);
                } else {
                    return cell.color;
                }
                crossedFace = hitFace;
            }
        }
        //return new Color((int)(cell.color.getRed() * percentOfIncidence), (int)(cell.color.getGreen() * percentOfIncidence), (int)(cell.color.getBlue() * percentOfIncidence));
        return Color.BLACK;
//        return cell.color;
    }

    public Color proceedTracer(ArrayList<NVector> tracer) {
        // So, want to continue through the cell.
        // If there's nothing in the cell, go straight to the other side.
        // If there is, check for collisions.
        double percentOfIncidence = 1;
        while (dtl > 0) {
            tracer.add(pos);
            if (cell.surfaces.size() > 0) {
                //TODO DO SOMETHING
                // Ok, for now, I'm gonna just straight up say "HEY THRS SUMTHIN HERE!"
                if (cell.surfaces.get(0) == null) {
                    return new Color((int) (cell.color.getRed() * percentOfIncidence), (int) (cell.color.getGreen() * percentOfIncidence), (int) (cell.color.getBlue() * percentOfIncidence));
                } else {
                    //TODO Actually deal with the surfaces.
                    return new Color((int) (cell.color.getRed() * percentOfIncidence), (int) (cell.color.getGreen() * percentOfIncidence), (int) (cell.color.getBlue() * percentOfIncidence));
                }
                //return cell.surfaces.get(0).render.renderPix(null, null, null);
            } else {
                // Check which side you hit
                NFace hitFace = null;
                NVector hit = null;
                for (NFace f : cell.faces) {
                    //TODO Maybe replace this with f.containsPoint?  May be slightly less efficient, though.
                    if (f == crossedFace) {
                        continue;
                    }
                    hitFace = f;//tracer.add(f.points[1].pos)
                    hit = Matrix.lineNPlaneIntersect(pos, dir, f.basis.origin, f.basis.bases);
                    if (!NVector.sameQuadrant(dir, hit.minusB(pos))) {
                        continue;
                    }
                    NVector[] ptVectors = new NVector[f.points.length + 1];
                    ptVectors[0] = hit;
                    for (int i = 0; i < f.points.length; i++) {
                        ptVectors[i + 1] = f.points[i].pos;
                    }
                    NVector[] flatVectors = Matrix.ipTransformCoords(f.basis.bases, ptVectors);
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
                    boolean outside = false;
                    for (int y = 0; y < baryM.rows; y++) {
                        baryCoords[y] = baryM.val[x][y];
                        if (baryCoords[y] < 0 || baryCoords[y] > 1) {
                            outside = true;
                            break;
                        }
                        baryCoords[baryM.rows] -= baryCoords[y];
                    }
                    if (baryCoords[baryM.rows] < 0 || baryCoords[baryM.rows] > 1) {
                        outside = true;
                    }
                    if (!outside) {
                        break;
                    }
                }
                // Theoretically, now hitFace has what face we hit, and hit has where we ended up.
                // Go there, and subtract from dtl.
                dtl -= hit.minusB(pos).length();
                try {
                    percentOfIncidence = 2 * NVector.angle(Matrix.lrvMult(hitFace.basis.projection, pos.minusB(hit)), pos.minusB(hit)) / (Math.PI);
                } catch (Exception ex) {
                    Logger.getLogger(SimplePhoton.class.getName()).log(Level.SEVERE, null, ex);
                }
                pos = hit;
                if (hitFace.crossCornerCell(cell) != null) {
                    dir = hitFace.crossCornerVector(cell, dir);
                    cell = hitFace.crossCornerCell(cell);
                } else {
                    return cell.color;
                }
                crossedFace = hitFace;
            }
        }
        tracer.add(pos);
        return new Color((int) (cell.color.getRed() * percentOfIncidence), (int) (cell.color.getGreen() * percentOfIncidence), (int) (cell.color.getBlue() * percentOfIncidence));
    }
}
