/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author erhannis
 */
public class ParallelRender {
    /**
     * Things to swap out:
     * 
     * Color
     * latticeDims
     * dims
     * orientation
     * 
     * SimplePhoton
     * NVector
     * NSurface
     * NCell
     * Matrix
     * 
     */
    
    public void makeEverythingIntoArrays() {
        //TODO
    }

    public static class LatchedThread extends Thread {
        public LatchedThread(Runnable runnable) {
            super(runnable);
        }
        
        // Count down at the end of runnable.
        public CountDownLatch latch = null;
    }
    
    public static Tensor<Integer> aRender(int dims, int latticeDims, int[] division, NVector[] orientation, NVector pos, NCell cell, int cameraMode, double dtl, double fov, int... picDims) {
        // So, the idea is to sweep from corner to corner of the aperture and ping pixels.
        Tensor<Integer> result = Tensor.getIntTensor(picDims);
        
        // Have to spawn Product(division) threads
        int[] blockIndex = new int[picDims.length];
        
        //TODO Sweep like the wind!
        HashSet<LatchedThread> threadz = new HashSet<LatchedThread>();
        aRenderSpawn(threadz, blockIndex, 0, picDims, dims, latticeDims, division, orientation, pos, cell, result, cameraMode, dtl, fov);
        CountDownLatch allFinished = new CountDownLatch(threadz.size());
        for (LatchedThread t : threadz) {
            t.latch = allFinished;
            t.start();
        }
        try {
            allFinished.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(ParallelRender.class.getName()).log(Level.SEVERE, null, ex);
        }
//        while (!threadz.isEmpty()) {
//            HashSet<Thread> toDelete = new HashSet<Thread>();
//            for (Thread t : threadz) {
//                if (!t.isAlive()) {
//                    toDelete.add(t);
//                }
//            }
//            threadz.removeAll(toDelete);
//        }
        return result;
    }

    public static void aRenderSpawn(HashSet<LatchedThread> threadz, int[] blockIndex, int coordIndex, final int[] picDims, final int dims, final int latticeDims, final int[] division, final NVector[] orientation, final NVector pos, final NCell cell, final Tensor<Integer> result, final int cameraMode, final double dtl, final double fov) {
        if (coordIndex < blockIndex.length) {
            for (int i = 0; i < division[coordIndex]; i++) {
                blockIndex[coordIndex] = i;
                aRenderSpawn(threadz, blockIndex, coordIndex + 1, picDims, dims, latticeDims, division, orientation, pos, cell, result, cameraMode, dtl, fov);
            }
        } else {
            final int[] blockIdx = blockIndex.clone();
            LatchedThread t = new LatchedThread(new Runnable() {
                public void run() {
                    int[] coord = new int[picDims.length];
                    int[] fromCoords = new int[picDims.length];
                    int[] toCoords = new int[picDims.length];
                    for (int i = 0; i < blockIdx.length; i++) {
                        fromCoords[i] = ((picDims[i] / division[i]) + 1) * blockIdx[i];
                        toCoords[i] = Math.min(((picDims[i] / division[i]) + 1) * (blockIdx[i] + 1), picDims[i]);
                    }
                    System.out.println("start block " + Arrays.toString(blockIdx) + "; " + Arrays.toString(fromCoords) + "; " + Arrays.toString(toCoords));
                    aRenderRecurse(dims, latticeDims, picDims, fromCoords, toCoords, coord, 0, orientation, pos, cell, result, cameraMode, dtl, fov);
                    System.out.println("end block " + Arrays.toString(blockIdx) + "; " + Arrays.toString(fromCoords) + "; " + Arrays.toString(toCoords));
                    
                    Thread t = Thread.currentThread();
                    if (t instanceof LatchedThread) {
                        if (((LatchedThread)t).latch != null) {
                            ((LatchedThread)t).latch.countDown();
                        }
                    }
                }
            });
            threadz.add(t);
        }
    }
    
/**/
    public static void aRenderRecurse(int dims, int latticeDims, int[] picDims, int[] fromCoords, int[] toCoords, int[] coord, int index, NVector[] orientation, NVector pos, NCell cell, Tensor<Integer> result, int cameraMode, double dtl, double fov) {
        if (index < picDims.length) {
            if (latticeDims == 2 && picDims.length == 2 && index == 1) {
                coord[index] = 0;
                aRenderRecurse(dims, latticeDims, picDims, fromCoords, toCoords, coord, index + 1, orientation, pos, cell, result, cameraMode, dtl, fov);
                int colr = result.get(coord[0], 0);
                for (int y = fromCoords[1]; y < toCoords[1]; y++) {
                    coord[1] = y;
                    result.put(colr, coord);
                }
            } else {
                //THINK For future reference, make sure that toCoords doesn't go beyond picDims.
                for (int i = fromCoords[index]; i < toCoords[index]; i++) {
                    coord[index] = i;
                    aRenderRecurse(dims, latticeDims, picDims, fromCoords, toCoords, coord, index + 1, orientation, pos, cell, result, cameraMode, dtl, fov);
                }
            }
        } else {//System.out.println(orientation[2]);
            NVector dir = new NVector(dims);
            //NVector apDir = aperture[0];
            //TODO Fix this horrible patched mess here.  (Should have aperture.)  I just want to see the pretty pictures!
            // Actually, this works pretty well.  I might leave it.
            if (cameraMode == Camera.PROJ_FULL_360) {
                dir = NVector.unitVector(dims, 0);
                for (int i = 1; i < dims; i++) {
                    double angle = (((double)coord[i - 1]) / picDims[i - 1]) * 2 * Math.PI;
                    try {
                        dir = NVector.rotate(NVector.unitVector(dims, 0), NVector.unitVector(dims, i), dir, angle);
                    } catch (Exception ex) {
                        //Logger.getLogger(ParallelRender.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                dir = dir.plusB(orientation[0].multS(0.5 / fov));
                for (int i = 1; i < latticeDims; i++) { //THINK ARGH, is this even the right bound?
                    dir = dir.plusB(orientation[i].multS((((double)coord[i - 1]) / picDims[i - 1]) - 0.5)); //THINK Ugh.  Way jury-rigged.
                }
            }
            //SimplePhoton photon = new SimplePhoton(dims, pos, dir, Color.BLACK, cell, dtl);            
            //result.put(Color.blue, coord);
            result.put(proceed(pos, dir, cell, dtl), coord);
        }
    }

    public static int proceed(NVector pos, NVector dir, NCell cell, double dtl) {
        // So, want to continue through the cell.
        // If there's nothing in the cell, go straight to the other side.
        // If there is, check for collisions.
        pos = pos.copyToCache();
        double percentOfIncidence = 1;
        NFace crossedFace = null;
        while (dtl > 0) {
            if (cell.surfaces.size() > 0) {
                //TODO DO SOMETHING
                // Ok, for now, I'm gonna just straight up say "HEY THRS SUMTHIN HERE!"
                if (cell.surfaces.get(0) == null) {
                    pos.doneWithNVector();
                    return (((int) (cell.color.getRed() * percentOfIncidence)) * 0x010000) + (((int) (cell.color.getGreen() * percentOfIncidence)) * 0x000100) + ((int) (cell.color.getBlue() * percentOfIncidence));
                } else {
                    //TODO Actually deal with the surfaces.
                    // Check which side you hit
                    NSurface hitSurface = null;
                    boolean outsideSurface = true;
                    boolean outsideCell = true;
                    NVector trueHit = null;
                    for (NSurface s : cell.surfaces) {
                        outsideSurface = true;
                        outsideCell = true;
                        //TODO Maybe replace this with f.containsPoint?  May be slightly less efficient, though.
//                        if (f == crossedFace) {
//                            continue;
//                        }
                        if (s == null) {
                            continue;
                        }
                        hitSurface = s;
                        NVector hit = Matrix.lineNPlaneIntersect(pos, dir, s.basis.origin, true, s.basis.bases);
                        NVector hMp = hit.minusB(pos, true);
                        if (!NVector.sameQuadrant(dir, hMp)) {
                            hit.doneWithNVector();
                            hMp.doneWithNVector();
                            continue;
                        }
                        hMp.doneWithNVector();

                        // Check if inside surface bounds
                        NVector[] ptVectors = new NVector[s.points.length + 1];
                        ptVectors[0] = hit;
                        for (int i = 0; i < s.points.length; i++) {
                            ptVectors[i + 1] = s.points[i].pos;
                        }
                        NVector[] flatVectors = Matrix.ipTransformCoords(s.basis.bases, ptVectors, true);
                        Matrix baryM = Matrix.maybeGetCachedMatrix(flatVectors.length, (flatVectors.length > 0 ? flatVectors[0].coords.length : 0), false);
                        for (int x = 0; x < baryM.cols - 1; x++) {
                            for (int y = 0; y < baryM.rows; y++) {
                                baryM.val[x][y] = flatVectors[x + 1].coords[y] - flatVectors[flatVectors.length - 1].coords[y];
                            }
                        }
                        int x = baryM.cols - 1;
                        for (int y = 0; y < baryM.rows; y++) {
                            baryM.val[x][y] = flatVectors[0].coords[y] - flatVectors[flatVectors.length - 1].coords[y];
                        }
                        
                        for (NVector nv : flatVectors) {
                            nv.doneWithNVector();
                        }
                        flatVectors = null;
                        
                        baryM.ipRRowForm();
                        x = baryM.cols - 1;
                        double[] baryCoords = new double[baryM.rows + 1];
                        baryCoords[baryM.rows] = 1;
                        outsideSurface = false;
                        for (int y = 0; y < baryM.rows; y++) {
                            baryCoords[y] = baryM.val[x][y];
                            if (baryCoords[y] < 0 || baryCoords[y] > 1) {
                                outsideSurface = true;
                                break;
                            }
                            baryCoords[baryM.rows] -= baryCoords[y];
                        }
                        if (baryCoords[baryM.rows] < 0 || baryCoords[baryM.rows] > 1) {
                            outsideSurface = true;
                        }
                        baryM.doneWithMatrix();
                        if (outsideSurface) {
                            hit.doneWithNVector();
                            continue;
                        }

                        // Check if inside cell bounds
                        ptVectors = new NVector[cell.points.length + 1];
                        ptVectors[0] = hit;
                        for (int i = 0; i < cell.points.length; i++) {
                            ptVectors[i + 1] = cell.points[i].pos;
                        }
                        flatVectors = Matrix.ipTransformCoords(cell.basis.bases, ptVectors, true);
                        baryM = Matrix.maybeGetCachedMatrix(flatVectors.length, (flatVectors.length > 0 ? flatVectors[0].coords.length : 0), false);
                        for (x = 0; x < baryM.cols - 1; x++) {
                            for (int y = 0; y < baryM.rows; y++) {
                                baryM.val[x][y] = flatVectors[x + 1].coords[y] - flatVectors[flatVectors.length - 1].coords[y];
                            }
                        }
                        x = baryM.cols - 1;
                        for (int y = 0; y < baryM.rows; y++) {
                            baryM.val[x][y] = flatVectors[0].coords[y] - flatVectors[flatVectors.length - 1].coords[y];
                        }
                        
                        for (NVector nv : flatVectors) {
                            nv.doneWithNVector();
                        }
                        flatVectors = null;
                        
                        baryM.ipRRowForm();
                        x = baryM.cols - 1;
                        baryCoords = new double[baryM.rows + 1];
                        baryCoords[baryM.rows] = 1;
                        outsideCell = false;
                        for (int y = 0; y < baryM.rows; y++) {
                            baryCoords[y] = baryM.val[x][y];
                            if (baryCoords[y] < 0 || baryCoords[y] > 1) {
                                outsideCell = true;
                                break;
                            }
                            baryCoords[baryM.rows] -= baryCoords[y];
                        }
                        if (baryCoords[baryM.rows] < 0 || baryCoords[baryM.rows] > 1) {
                            outsideCell = true;
                        }
                        baryM.doneWithMatrix();

                        if (!outsideSurface && !outsideCell) {
                            trueHit = hit;
                            break;
                        }
                        hit.doneWithNVector();                        
                    }
                    // Theoretically, now hitFace has what face we hit, and hit has where we ended up.
                    // Go there, and subtract from dtl.
                    //dtl -= hit.minusB(pos).length();
                    if (!outsideSurface && !outsideCell) {
                        NVector pMh = null;
                        try {
                            pMh = pos.minusB(trueHit, true);
                            percentOfIncidence = 2 * NVector.angle(Matrix.lrvMult(hitSurface.basis.projection, pMh), pMh) / (Math.PI);
                            pMh.doneWithNVector();
                        } catch (Exception ex) {
                            if (pMh != null) pMh.doneWithNVector();
                            Logger.getLogger(SimplePhoton.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        //TODO This could be cached...I guess....
                        pos.doneWithNVector();
                        pos = trueHit;
//                    dir = hitSurface.crossCornerVector(cell, dir);
//                    if (hitSurface.crossCornerCell(cell) != null) {
//                        cell = hitFace.crossCornerCell(cell);
//                    } else {
//                        return cell.color;
//                    }
//                    crossedFace = hitFace;
                        pos.doneWithNVector();
                        if (hitSurface.color != null && hitSurface.color.getRGB() != 0) {
//                            return new Color((int) (hitSurface.color.getRed() * percentOfIncidence), (int) (hitSurface.color.getGreen() * percentOfIncidence), (int) (hitSurface.color.getBlue() * percentOfIncidence));
                            return (((int) (hitSurface.color.getRed() * percentOfIncidence)) * 0x010000) + (((int) (hitSurface.color.getGreen() * percentOfIncidence)) * 0x000100) + ((int) (hitSurface.color.getBlue() * percentOfIncidence));
                        } else {
                            // Jury-rigged to be ground.
//                            return new Color((int) (cell.color.getRed() * percentOfIncidence), (int) (cell.color.getGreen() * percentOfIncidence), (int) (cell.color.getBlue() * percentOfIncidence));
                            return (((int) (cell.color.getRed() * percentOfIncidence)) * 0x010000) + (((int) (cell.color.getGreen() * percentOfIncidence)) * 0x000100) + ((int) (cell.color.getBlue() * percentOfIncidence));
                        }
                    }
                }
//                return cell.color;
                //return cell.surfaces.get(0).render.renderPix(null, null, null);
//            } else {
            }
                // Check which side you hit
                NFace hitFace = null;
                NVector trueHit = null;
                for (NFace f : cell.faces) {
                    //TODO Maybe replace this with f.containsPoint?  May be slightly less efficient, though.
                    if (f == crossedFace) {
                        continue;
                    }
                    hitFace = f;
                    NVector hit = Matrix.lineNPlaneIntersect(pos, dir, f.basis.origin, true, f.basis.bases);
                    NVector hMp = hit.minusB(pos, true);
                    if (!NVector.sameQuadrant(dir, hMp)) {
                        hit.doneWithNVector();
                        hMp.doneWithNVector();
                        continue;
                    }
                    hMp.doneWithNVector();
                    NVector[] ptVectors = new NVector[f.points.length + 1];
                    ptVectors[0] = hit;
                    for (int i = 0; i < f.points.length; i++) {
                        ptVectors[i + 1] = f.points[i].pos;
                    }
                    NVector[] flatVectors = Matrix.ipTransformCoords(f.basis.bases, ptVectors, true);
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
                    
                    for (NVector nv : flatVectors) {
                        nv.doneWithNVector();
                    }
                    flatVectors = null;
                    
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
                        trueHit = hit;
                        break;
                    }
                    hit.doneWithNVector();
                }
                // Theoretically, now hitFace has what face we hit, and hit has where we ended up.
                // Go there, and subtract from dtl.
                NVector hMp = trueHit.minusB(pos, true);
                dtl -= hMp.length();
                hMp.doneWithNVector();
                NVector pMh = null;
                try {
                    pMh = pos.minusB(trueHit, true);
                    if (hitFace == null) System.err.println("hitFace null");
                    if (hitFace.basis == null) System.err.println("hitFace.basis null");
                    if (hitFace.basis.projection == null) System.err.println("hitFace.basis.projection null");
                    if (pMh == null) System.err.println("pMh null");
                    percentOfIncidence = 2 * NVector.angle(Matrix.lrvMult(hitFace.basis.projection, pMh), pMh) / (Math.PI);
                    pMh.doneWithNVector();
                } catch (Exception ex) {
                    if (pMh != null) pMh.doneWithNVector();
                    Logger.getLogger(SimplePhoton.class.getName()).log(Level.SEVERE, null, ex);
                }
                pos.doneWithNVector();
                pos = trueHit;
                if (hitFace.crossCornerCell(cell) != null) {
                    dir = hitFace.crossCornerVector(cell, dir);
                    cell = hitFace.crossCornerCell(cell);
                } else {
                    pos.doneWithNVector();
                    return cell.color.getRGB();
                }
                crossedFace = hitFace;
//            }
        }
        //return new Color((int)(cell.color.getRed() * percentOfIncidence), (int)(cell.color.getGreen() * percentOfIncidence), (int)(cell.color.getBlue() * percentOfIncidence));
        pos.doneWithNVector();
        return 0x00000000;
//        return cell.color;
    }
/**/
}
