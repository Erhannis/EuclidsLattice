/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewer12
 */
public class Camera {
    public int dims = 0;
    public int latticeDims = 0;
    public Lattice lattice = null;
    public NVector[] aperture = null; // Oh, man, I have SUCH an urge to make some kind of Aperture Science joke.
    public NVector[] orientation = null; // First vector is the direction of "front".
                                         // I'm not sure how to keep this from warping over time.
                                         // I don't really have stand-alone rotation matrices, so I can't just keep a rolling tally.
                                         // Come to think of it, might that not introduce errors, as well?
    public NVector pos = null;
    public NVector velocity = null; // Not sure whether to keep this in dims or latticeDims.  Maybe I'll keep both, for now.
    public NVector latticeVelocity = null; // Just until I figure out which is more useful.
    public NCell cell = null;
    public int cellId = 0;
    public CameraForm camForm = null;
    public int id = -2;
    
    public Camera(int dims, int latticeDims, Lattice lattice) {
        this.dims = dims;
        this.latticeDims = latticeDims;
        this.lattice = lattice;
        this.zero = new NVector(dims);
        aperture = new NVector[1 << (latticeDims - 1)]; // Now, I think each vector in the aperture has to have latticeDims.
                                                  //   We'll multiply them by each vector in the orientation to get their actual direction.
        // Ok, so this confused me when I came back to it later.
        // "orientation" is the set of axes relative to the camera, for preserving left and right. etc.
        orientation = new NVector[latticeDims]; // Do I even need an aperture, given an orientation?  Dunno.
        pos = new NVector(dims);
        velocity = new NVector(dims);
        latticeVelocity = new NVector(latticeDims);
        init(0.5 * Math.PI);
    }

    private class IntHolder {
        int value;

        public IntHolder(int value) {
            this.value = value;
        }
    }

    public void constructAperture(double apertureAngle, int level, NVector current, IntHolder index) {
        //TODO I really ought to pay attention to the aperture angle.
        if (level < latticeDims - 1) {
            current.coords[level] = -1;
            constructAperture(apertureAngle, level + 1, current, index);
            current.coords[level] = 1;
            constructAperture(apertureAngle, level + 1, current, index);
        } else {
            current.coords[level] = -1;
            aperture[index.value++] = current.copy();
            current.coords[level] = 1;
            aperture[index.value++] = current.copy();
        }
    }
    
    public void init(double apertureAngle) {
        for (int i = 0; i < latticeDims; i++) {
            orientation[i] = new NVector(dims);
            for (int j = 0; j < dims; j++) {
                if (i == j) {
                    orientation[i].coords[j] = 1;
                } else {
                    orientation[i].coords[j] = 0;
                }
            }
        }
        NVector ap = new NVector(latticeDims);
        ap.coords[0] = 1;
        IntHolder index = new IntHolder(0);
        if (latticeDims > 1) {
            constructAperture(apertureAngle, 1, ap, index);
        }
        for (int i = 0; i < dims; i++) {
            pos.coords[i] = 0;
            velocity.coords[i] = 0;
        }
        for (int i = 0; i < latticeDims; i++) {
            latticeVelocity.coords[i] = 0;
        }
    }
    
    public void checkOrth() {
        for (int i = 0; i < latticeDims; i++) {
            System.out.println(orientation[i]);
        }
        for (int i = 0; i < latticeDims - 1; i++) {
            for (int j = i + 1; j < latticeDims; j++) {
                System.out.println(NVector.lrDot(orientation[i], orientation[j]));
            }
        }
    }
    
    public NVector zero;
    
    public void realignOrientation(NCell cell) throws Exception {
        //TODO Do something with this.
        // Probably re-orthogonalize the orientation and align the aperture to that.
        // OKOKOKOKOK - I'm gonna have to think out loud here.
        // So...what we want is to kinda project the front vector onto the cell, but rotate the rest to match.
        // Repeat the process with te rest of the vectors until you've matched the internal dimensions of the cell.
        // Then, to correct errors, reorthogonalize the vectors...I think.  I'm wondering if that'll work right, or mess up the angles.
        for (int i = 0; i < cell.cellDims; i++) {//source.length()//System.out.println(cell.basis.projection);
            //TODO Maybe check for perpendicular to cell...shouldn't happen, but JIC.
            NVector source = orientation[i];//lattice.cells.contains(cell)//System.out.println(cell.basis.projection);
            NVector target = Matrix.lrvMult(cell.basis.projection, source);//target.length()
            if (MeMath.prettyZero(target.lengthSqr(), 17)) {
                // The current orientation axis is perpendicular to the cell...problem.
                // Let's see...we want, now, an arbitrary direction to rotate the orientation
                //   structure, but one that is perpendicular to the already rotated axes.
                // AAAUGH, to heck with it!  Just scramble the thing and try again!
                //TODO Maybe at some point, we can see what the Matrix.cross product will give us with insufficient vectors.
                source = NVector.random(dims, 1, true);
                target = NVector.random(dims, 1, true);
                for (int j = 0; j < latticeDims; j++) {//NVector.angle(orientation[0], orientation[1]);
                    //orientation[j] = NVector.rotate(pos, source, target, orientation[j], NVector.angle(source, target));
                    orientation[j] = NVector.rotate(zero, source, target, orientation[j], NVector.angle(source, target));
                }
                //THINK This has the potential to stack overflow with weird parameters.
                realignOrientation(cell);
                return;
            }
//            orientation[i] = target;
            //source = source.minusB(pos);
            //target = target.minusB(pos);
            
            //TODO If this thing actually works, we could start at the next base.
            //TODO In which case, I would here set base = target.
            //checkOrth();
            for (int j = 0; j < latticeDims; j++) {//NVector.angle(orientation[0], orientation[1]);
                //orientation[j] = NVector.rotate(pos, source, target, orientation[j], NVector.angle(source, target));
                orientation[j] = NVector.rotate(zero, source, target, orientation[j], NVector.angle(source, target));
            }
        }
        //this.pos = Matrix.lrvMult(cell.basis.projection, pos.minusB(cell.basis.origin)).plusB(cell.basis.origin);
        this.cell = cell;
    }
    
    public int projectionType = PROJ_MERCATOR;
    public static final int PROJ_AZIMUTHAL = 1;
    public static final int PROJ_MERCATOR = 2;
    
    public void renderCamera(Graphics2D g, int cameraMode, int width, int height, double dtl, double fov, int graininess) {
        //Cuda?
        switch (cameraMode) {
            case PROJ_AZIMUTHAL:
                break;
            case PROJ_MERCATOR:
                //TODO So, this isn't actually general, like, for multiple dimensions.
                int dWidth = width / graininess;
                int dHeight = height / graininess;
                int[] picDims = new int[]{dWidth, dHeight};
                Tensor<Color> result = aRender(dtl, fov, picDims);
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                for (int x = 0; x < dWidth; x++) {
                    for (int y = 0; y < dHeight; y++) {
                        int rgb = result.get(x, y).getRGB();
                        for (int xi = 0; xi < graininess; xi++) {
                            for (int yi = 0; yi < graininess; yi++) {
                                image.setRGB((x * graininess) + xi, (y * graininess) + yi, rgb);
                            }
                        }
                    }
                }
                g.drawImage(image, new AffineTransform(), null);
                break;
            default:
        }
    }

    public Tensor<Color> aRender(double dtl, double fov, int... picDims) {
        // So, the idea is to sweep from corner to corner of the aperture and ping pixels.
        Tensor<Color> result = Tensor.getColorTensor(picDims);
        //TODO Sweep like the wind!
        int[] coord = new int[picDims.length];
        aRenderRecurse(picDims, coord, 0, result, dtl, fov);
        return result;
    }

    public void aRenderRecurse(int[] picDims, int[] coord, int index, Tensor<Color> result, double dtl, double fov) {
        if (index < picDims.length) {
            if (latticeDims == 2 && picDims.length == 2 && index == 1) {
                coord[index] = 0;
                aRenderRecurse(picDims, coord, index + 1, result, dtl, fov);
                Color col = result.get(coord[0], 0);
                for (int y = 0; y < picDims[1]; y++) {
                    coord[1] = y;
                    result.put(col, coord);
                }
            } else {
                for (int i = 0; i < picDims[index]; i++) {
                    coord[index] = i;
                    aRenderRecurse(picDims, coord, index + 1, result, dtl, fov);
                }
            }
        } else {//System.out.println(orientation[2]);
            NVector dir = new NVector(dims);
            //NVector apDir = aperture[0];
            //TODO Fix this horrible patched mess here.  (Should have aperture.)  I just want to see the pretty pictures!
            // Actually, this works pretty well.  I might leave it.
            dir = dir.plusB(orientation[0].multS(0.5 / fov));
            for (int i = 1; i < latticeDims; i++) { //THINK ARGH, is this even the right bound?
                dir = dir.plusB(orientation[i].multS((((double)coord[i - 1]) / picDims[i - 1]) - 0.5)); //THINK Ugh.  Way jury-rigged.
            }
            SimplePhoton photon = new SimplePhoton(dims, pos, dir, Color.BLACK, cell, dtl);            
            //result.put(Color.blue, coord);
            result.put(photon.proceed(), coord);
        }
    }

    /**
     * Moves the camera by "dir", where dir is a vector in the lattice - dir is 3 dims in 3:4.
     * Calculated relative to the camera's orientation.
     * If "crossed" != null, ignores said face.
     * @param dir 
     */
    public void move(NVector dir, NFace crossed) {
        double moveLength = dir.length();
        // Check which side you hit
        NFace hitFace = null;
        NVector hit = null;
         for (NFace f : cell.faces) {
            //TODO Maybe replace this with f.containsPoint?  May be slightly less efficient, though.
            if (f == crossed) {
                continue;
            }
            hit = Matrix.lineNPlaneIntersect(pos, dir, f.basis.origin, f.basis.bases);
            if (hit.minusB(pos).length() > moveLength || !NVector.sameQuadrant(dir, hit.minusB(pos))) {
                continue;
            }
            hitFace = f; lattice.incompleteFaces.add(hitFace);
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
        if (hitFace == null) {
            // Didn't hit a face.
            pos = pos.plusB(dir);
        } else {
            if (hitFace.crossCornerCell(cell) != null) {
                moveLength -= hit.minusB(pos).length(); //THINK Maybe the move length could be changed a little more efficiently or accurately.
                pos = hit;
                dir = hitFace.crossCornerVector(cell, dir).ipNormalize().multS(moveLength);
                velocity = hitFace.crossCornerVector(cell, velocity);
                for (int i = 0; i < latticeDims; i++) {
                    orientation[i] = hitFace.crossCornerVector(cell, orientation[i]);
                }
                cell = hitFace.crossCornerCell(cell);
                move(dir, hitFace);//TODO Something went wrong here; stack overflow.  Fix it.
                //THINK Maybe straighten up vectors and stuff
            }
        }
    }
    
    public double checkFloating() {
        try {
            return pos.minusB(cell.basis.origin).minusB(Matrix.lrvMult(cell.basis.projection, pos.minusB(cell.basis.origin))).length();
        } catch (Exception ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 10;
    }
    
    public void reanchor() {
        try {
            pos = Matrix.lrvMult(cell.basis.projection, pos.minusB(cell.basis.origin)).plusB(cell.basis.origin);
        } catch (Exception ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeInt(dims);
        dos.writeInt(latticeDims);
        dos.writeInt(id);
        dos.writeInt(aperture.length);
        for (int i = 0; i < aperture.length; i++) {
            aperture[i].toBytes(dos);
        }
        //THINK Camera form?
        if (cell != null) {
            dos.writeInt(cell.id);
        } else {
            dos.writeInt(-1);
        }
        latticeVelocity.toBytes(dos);
        dos.writeInt(orientation.length);
        for (int i = 0; i < orientation.length; i++) {
            orientation[i].toBytes(dos);
        }
        pos.toBytes(dos);
        dos.writeInt(projectionType);
        velocity.toBytes(dos);
        zero.toBytes(dos);
    }

    public static Camera fromBytes(DataInputStream dis, Lattice lattice) throws IOException {
        int dims = dis.readInt();
        int latticeDims = dis.readInt();        
        Camera result = new Camera(dims, latticeDims, lattice);
        result.id = dis.readInt();
        result.aperture = new NVector[dis.readInt()];
        for (int i = 0; i < result.aperture.length; i++) {
            result.aperture[i] = NVector.fromBytes(dis);
        }
        //THINK Camera form?
        result.cellId = dis.readInt();
        result.latticeVelocity = NVector.fromBytes(dis);
        result.orientation = new NVector[dis.readInt()];
        for (int i = 0; i < result.orientation.length; i++) {
            result.orientation[i] = NVector.fromBytes(dis);
        }
        result.pos = NVector.fromBytes(dis);
        result.projectionType = dis.readInt();
        result.velocity = NVector.fromBytes(dis);
        result.zero = NVector.fromBytes(dis);
        return result;
    }    
    
    public Tensor<Color> aRenderTracer(double dtl, int... picDims) {
        // So, the idea is to sweep from corner to corner of the aperture and ping pixels.
        Tensor<Color> result = Tensor.getColorTensor(picDims);
        //TODO Sweep like the wind!
        int[] coord = new int[picDims.length];
        aRenderRecurseTracer(picDims, coord, 0, result, dtl);
        return result;
    }

    public void aRenderRecurseTracer(int[] picDims, int[] coord, int index, Tensor<Color> result, double dtl) {
        if (index < picDims.length) {
            for (int i = 0; i < picDims[index]; i++) {
                coord[index] = i;
                aRenderRecurseTracer(picDims, coord, index + 1, result, dtl);
            }
        } else {//System.out.println(orientation[2]);
            NVector dir = new NVector(dims);
            //NVector apDir = aperture[0];
            //TODO Fix this horrible patched mess here.  (Should have aperature.)  I just want to see the pretty pictures!
            dir = dir.plusB(orientation[0].multS(0.5));
            for (int i = 1; i < latticeDims; i++) { //THINK ARGH, is this even the right bound?
                dir = dir.plusB(orientation[i].multS((((double)coord[i - 1]) / picDims[i - 1]) - 0.5)); //THINK Ugh.  Way jury-rigged.
            }
            SimplePhoton photon = new SimplePhoton(dims, pos, dir, Color.BLACK, cell, dtl);            
            //result.put(Color.blue, coord);
            if (this.latticeDims > 2 || coord[1] == 0) {
                ArrayList<NVector> tracer = new ArrayList<NVector>();
                lattice.tracers.add(tracer);
                result.put(photon.proceedTracer(tracer), coord);
            }
        }
    }

    void renderCameraTracer(int cameraMode, int width, int height, double dtl) {
        lattice.tracers.clear();
        switch (cameraMode) {
            case PROJ_AZIMUTHAL:
                break;
            case PROJ_MERCATOR:
                //TODO So, this isn't actually general, like, for multiple dimensions.
                int[] picDims = new int[]{width, height};
                Tensor<Color> result = aRenderTracer(dtl, picDims);
//                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//                for (int x = 0; x < width; x++) {
//                    for (int y = 0; y < height; y++) {
//                        image.setRGB(x, y, result.get(x, y).getRGB());
//                    }
//                }
//                g.drawImage(image, new AffineTransform(), null);
                break;
            default:
        }
    }
    
    public void rotate(int axis1, int axis2, double angle) throws Exception {
        NVector ax1 = orientation[axis1].copy();
        NVector ax2 = orientation[axis2].copy();
        for (int i = 0; i < orientation.length; i++) {
            orientation[i] = NVector.rotate(ax1, ax2, orientation[i], angle);
        }
    }
}
