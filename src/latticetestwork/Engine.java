/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mewer12
 */
public class Engine {

    public double sqr(double a) {
        return a * a;
    }
    public static Random r = new Random();
    public int dims = 0;
    public LatticeTestworkView parent = null;
    public ArrayList<NLatticeBone> skeleton = null;

    public Engine(int dims, LatticeTestworkView parent) {
        this.dims = dims;
        this.parent = parent;
    }
    public Lattice lattice = null;
    public int mode = 0;

    public void init(int latticeDims, int mode) {
        lattice = new Lattice(dims, latticeDims);
        this.mode = mode;
        chosens = new ArrayList<NPoint>();
        highlighteds = new ArrayList<NPoint>();
        highlightedCells = new ArrayList<NCell>();
        permacenter = null;
        rot = new double[dims * (dims - 1) / 2];
        rotCoords = new int[dims * (dims - 1) / 2][2];
        int index = 0;
        for (int i = 0; i < dims - 1; i++) {
            for (int j = i + 1; j < dims; j++) {
                rotCoords[index][0] = i;
                rotCoords[index][1] = j;
                rot[index++] = 0;
            }
        }
        skeleton = new ArrayList<NLatticeBone>();
    }

    public void addPoints(int numPoints) { // Point coords are from -1,1 in each dim.
        for (int i = 0; i < numPoints; i++) {
            NPoint bucket = new NPoint(dims);
            for (int j = 0; j < dims; j++) {
                bucket.pos.coords[j] = (r.nextDouble() * 2) - 1;
            }
            lattice.addPoint(bucket);
        }
    }
//    public double xRot = 0;
//    public double yRot = 0;
    public double[] rot = null;
    public int[][] rotCoords = null;
    public boolean stereo = true;
    public double stereoDegrees = 0.1;
    public double stereoDelta = 2.5;
    public static boolean stereo4 = false;
    public double stereo4Alpha = 0.1;
    public boolean hideImmune = false;
    public boolean hideComplete = false;
    public boolean hideIncomplete = false;
    public boolean hidePoints = false;

    public NVector viewRotate(NVector i, double[] rot, int[][] rotCoords) throws Exception {
//        NVector basisA = new NVector(dims);
//        basisA.coords[0] = 1;
//        for (int j = 1; j < dims; j++) {
//            basisA.coords[j] = 0;
//        }

        NVector current = i;
        int index = 0;
        for (int j = 0; j < dims - 1; j++) {
            for (int l = j + 1; l < dims; l++) {
                if (index == 1) {
                    // Going to skip this one for now; it's the stereo one and we want it to be applied last so that it's always in line witht the screen.
                    index++;
                    continue;
                }
                NVector basisA = new NVector(dims);
                NVector basisB = new NVector(dims);
                for (int k = 0; k < dims; k++) {
                    if (rotCoords[index][0] == k) {
                        basisA.coords[k] = 1;
                    } else {
                        basisA.coords[k] = 0;
                    }
                    if (rotCoords[index][1] == k) {
                        basisB.coords[k] = 1;
                    } else {
                        basisB.coords[k] = 0;
                    }
                }
                current = NVector.rotateOrthBasis(basisA, basisB, current, rot[index]);
                index++;
            }
        }
        if (rot.length >= 2) {
            index = 1;
            NVector basisA = new NVector(dims);
            NVector basisB = new NVector(dims);
            for (int k = 0; k < dims; k++) {
                if (rotCoords[index][0] == k) {
                    basisA.coords[k] = 1;
                } else {
                    basisA.coords[k] = 0;
                }
                if (rotCoords[index][1] == k) {
                    basisB.coords[k] = 1;
                } else {
                    basisB.coords[k] = 0;
                }
            }
            current = NVector.rotateOrthBasis(basisA, basisB, current, rot[index]);
        }
        return current;
    }
    public static final int RMODE_MODEL = 0;
    public static final int RMODE_CAMERA = 1;
    public boolean trueRendered = true;

    public int lastWidth = 1;
    public int lastHeight = 1;
    public double lastTransX = 1;
    public double lastTransY = 1;
    public double lastScaleX = 1;
    public double lastScaleY = 1;
    public boolean sticksChanged = false;
    public boolean parallelRendering = false;

    public class Stick {

        public NPoint pointA;
        public NPoint pointB;

        public Stick() {
        }

        public Stick(NPoint pointA, NPoint pointB) {
            this.pointA = pointA;
            this.pointB = pointB;
        }

        public boolean equivalent(Stick s) {
            if (((this.pointA == s.pointA) && (this.pointB == s.pointB)) || ((this.pointB == s.pointA) && (this.pointA == s.pointB))) {
                return true;
            }
            return false;
        }

        public boolean equivalent(NPoint pointA, NPoint pointB) {
            if (((this.pointA == pointA) && (this.pointB == pointB)) || ((this.pointB == pointA) && (this.pointA == pointB))) {
                return true;
            }
            return false;
        }
    }
    
    public ArrayList<Stick> completeSticks = new ArrayList<Stick>();
    public ArrayList<Stick> incompleteSticks = new ArrayList<Stick>();

    public void render(Graphics2D g, int renderMode, int width, int height, double transX, double transY, double scaleX, double scaleY, boolean clickRender) {
        lastWidth = width;
        lastHeight = height;
        lastTransX = transX;
        lastTransY = transY;
        lastScaleX = scaleX;
        lastScaleY = scaleY;
        if (clickRender) {
            //    g.getTransform().
        }
//        g.setColor(Color.red);
//        g.fillRect(-50, -50, 100, 100);
        g.setStroke(new BasicStroke(0.001f));
        g.translate((width / 2.0) + transX, (height / 2.0) + transY);
        g.scale(scaleX, scaleY);
//        java.awt.geom.Ellipse2D.Double test = new java.awt.geom.Ellipse2D.Double();
//        test.x = -1;
//        test.y = -1;
//        test.height = 2;
//        test.width = 2;
//        g.draw(test);
        switch (renderMode) {
            case RMODE_MODEL:
                switch (mode) {
                    case 0: // 2-2 Triangulation
                        if (lattice != null) {
                            for (NPoint i : lattice.points) {
                                g.setColor(Color.black);
                                Ellipse2D.Double bucket = new Ellipse2D.Double();
                                bucket.x = i.pos.coords[0] - 0.01;
                                bucket.y = i.pos.coords[1] - 0.01;
                                bucket.width = 0.02;
                                bucket.height = 0.02;
                                i.displayed = true;
                                i.displayPoint.setLocation(bucket.x, bucket.y);
                                g.draw(bucket);
                            }
                            for (NCell i : lattice.cells) {
                                g.setColor(Color.blue);
                                for (int j = 0; j < i.faces.length; j++) {
                                    Line2D.Double line = new Line2D.Double();
                                    line.x1 = i.faces[j].points[0].pos.coords[0];
                                    line.y1 = i.faces[j].points[0].pos.coords[1];
                                    line.x2 = i.faces[j].points[1].pos.coords[0];
                                    line.y2 = i.faces[j].points[1].pos.coords[1];
                                    //bucket.width = 0.02;
                                    //bucket.height = 0.02;
                                    g.draw(line);
                                }
                            }
                            if (permacenter != null) {
                                g.setColor(Color.black);
                                Ellipse2D.Double circle = new Ellipse2D.Double();
                                circle.x = permacenter.pos.coords[0] - permaradius;
                                circle.y = permacenter.pos.coords[1] - permaradius;
                                circle.height = 2 * permaradius;
                                circle.width = 2 * permaradius;
                                g.setColor(Color.red);
                                g.draw(circle);
                            }
                            switch (chosens.size()) {
                                case 1:
                                    g.setColor(Color.red);
                                    Ellipse2D.Double bucket = new Ellipse2D.Double();
                                    bucket.x = chosens.get(0).pos.coords[0] - 0.01;
                                    bucket.y = chosens.get(0).pos.coords[1] - 0.01;
                                    bucket.width = 0.02;
                                    bucket.height = 0.02;
                                    g.draw(bucket);
                                    break;
                                case 2:
                                    g.setColor(Color.red);
                                    Line2D.Double line = new Line2D.Double();
                                    line.x1 = chosens.get(0).pos.coords[0];
                                    line.y1 = chosens.get(0).pos.coords[1];
                                    line.x2 = chosens.get(1).pos.coords[0];
                                    line.y2 = chosens.get(1).pos.coords[1];
                                    //bucket.width = 0.02;
                                    //bucket.height = 0.02;
                                    g.draw(line);
                                    break;
                                case 3:
                                    Matrix augmented = null;
                                    try {
                                        double[][] crossA = new double[chosens.size() - 2][dims];
                                        for (int i = 1; i < chosens.size() - 1; i++) {
                                            NVector bucket2 = chosens.get(i).pos.minusB(chosens.get(0).pos);
                                            for (int j = 0; j < dims; j++) {
                                                crossA[i - 1][j] = bucket2.coords[j];
                                            }
                                        }
                                        double[] cA = new double[dims];
                                        for (int i = 0; i < dims; i++) {
                                            cA[i] = 0;
                                        }
                                        for (int i = 0; i < chosens.size() - 1; i++) {
                                            for (int j = 0; j < dims; j++) {
                                                cA[j] += chosens.get(i).pos.coords[j];
                                            }
                                        }
                                        for (int i = 0; i < dims; i++) {
                                            cA[i] /= chosens.size() - 1;
                                        }

                                        double[][] crossB = new double[chosens.size() - 2][dims];
                                        for (int i = 2; i < chosens.size(); i++) {
                                            NVector bucket2 = chosens.get(i).pos.minusB(chosens.get(1).pos);
                                            for (int j = 0; j < dims; j++) {
                                                crossB[i - 2][j] = bucket2.coords[j];
                                            }
                                        }
                                        double[] cB = new double[dims];
                                        for (int i = 0; i < dims; i++) {
                                            cB[i] = 0;
                                        }
                                        for (int i = 1; i < chosens.size(); i++) {
                                            for (int j = 0; j < dims; j++) {
                                                cB[j] += chosens.get(i).pos.coords[j];
                                            }
                                        }
                                        for (int i = 0; i < dims; i++) {
                                            cB[i] /= chosens.size() - 1;
                                        }
                                        double[] aNorm = Matrix.cross(crossA);
                                        double[] bNorm = Matrix.cross(crossB);

                                        boolean aNotZero = false;
                                        boolean bNotZero = false;
                                        augmented = Matrix.maybeGetCachedMatrix(3, dims, false);
                                        for (int i = 0; i < dims; i++) {
                                            augmented.val[0][i] = aNorm[i];
                                            if (aNorm[i] != 0) {
                                                aNotZero = true;
                                            }
                                            augmented.val[1][i] = -bNorm[i];
                                            if (bNorm[i] != 0) {
                                                bNotZero = true;
                                            }
                                            augmented.val[2][i] = cB[i] - cA[i];
                                            if (aNorm[i] == 0 && bNorm[i] == 0) {
                                                throw new Exception("A coord of the norms is 0!");
                                            }
                                        }
                                        if (!(aNotZero && bNotZero)) {
                                            throw new Exception("A norm is 0!");
                                        }
                                        IntersectionResult solution = augmented.solveIntersect();
                                        augmented.doneWithMatrix(); augmented = null;
                                        NPoint center = new NPoint(dims);
                                        if (solution.q12 == 1) {
                                            for (int i = 0; i < dims; i++) {
                                                center.pos.coords[i] = (aNorm[i] * solution.value) + cA[i];
                                            }
                                        } else if (solution.q12 == 2) {
                                            for (int i = 0; i < dims; i++) {
                                                center.pos.coords[i] = (bNorm[i] * solution.value) + cB[i];
                                            }
                                        } else {
                                            throw new Exception("Argh, neither q1 nor q2.");
                                        }
                                        double radius = 0;
                                        for (int i = 0; i < dims; i++) {
                                            radius += sqr(center.pos.coords[i] - chosens.get(0).pos.coords[i]);
                                        }
                                        radius = Math.sqrt(radius);

                                        // Calculating the center of the circle made by the three points
//                            double ax = chosens.get(0).pos.coords[0];
//                            double ay = chosens.get(0).pos.coords[1];
//                            double bx = chosens.get(1).pos.coords[0];
//                            double by = chosens.get(1).pos.coords[1];
//                            double cx = chosens.get(2).pos.coords[0];
//                            double cy = chosens.get(2).pos.coords[1];
//                            double centerX = ((by*cx*cx)-(bx*bx*cy)-(by*by*cy)+(by*cy*cy)+(ax*ax*(cy-by))+(ay*ay*(cy-by))+(ay*((bx*bx)+(by*by)-(cx*cx)-(cy*cy))))
//                                                /
//                                             (2*((ay*(bx-cx))+(by*cx)-(bx*cy)+(ax*(cy-by))));
//                            double centerY = ((ax*ax*(bx-cx))+(ay*ay*(bx-cx))+(cx*((bx*bx)+(by*by)-(bx*cx)))-(bx*cy*cy)+(ax*((cx*cx)+(cy*cy)-(bx*bx)-(by*by))))
//                                                /
//                                             (2*((ay*(bx-cx))+(by*cx)-(bx*cy)+(ax*(cy-by))));
//                            double radius = Math.sqrt(sqr(centerX - ax) + sqr(centerY - ay));
                                        Ellipse2D.Double circle = new Ellipse2D.Double();
//                            circle.x = centerX - radius;
//                            circle.y = centerY - radius;
                                        circle.x = center.pos.coords[0] - radius;
                                        circle.y = center.pos.coords[1] - radius;
                                        circle.height = 2 * radius;
                                        circle.width = 2 * radius;
                                        g.setColor(Color.red);
                                        g.draw(circle);
                                    } catch (Exception e) {
                                        Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, e);
                                    }
                                    if (augmented != null) augmented.doneWithMatrix();
                                    break;
                            }
                        }
                        break;
                    case 100:
                    case 3:
                    case 2:
                    case 1:
                        if (sticksChanged) {
                            if (!parallelRendering) {
                                updateSticks();
                            } else {
                                updateSticksParallel();
                            }
                        }
                        //TODO HOLY GARBAGE, I'm suddenly realizing how much this could be cleaned up.
                        if (lattice != null) {
                            for (int hasStereo = 0; hasStereo <= 1; hasStereo++) {
                                double stereoCurDelta = 0;
                                double stereo4CurAlpha = 0;
                                if (hasStereo == 1) {
                                    if (stereo && rot.length > 1) {
                                        stereoCurDelta = 0.5 * stereoDelta;
                                        rot[1] -= stereoDegrees;
                                        if (stereo4) {
                                            stereo4CurAlpha = -stereo4Alpha;
                                        }
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (stereo && rot.length > 1) {
                                        stereoCurDelta = -0.5 * stereoDelta;
                                        if (stereo4) {
                                            stereo4CurAlpha = stereo4Alpha;
                                        }
                                    }
                                }
                                //TODO I should probably be able to rotate this thing.
                                g.setColor(Color.black);
                                if (!hidePoints) {
                                    for (NPoint i : lattice.points) {
                                        try {
                                            if (hideImmune && i.immune) {
                                                continue;
                                            }
                                            g.setColor(i.color);
                                            Ellipse2D.Double bucket = new Ellipse2D.Double();

                                            NVector current = viewRotate(i.pos, rot, rotCoords);

//                            first.coords[0] = (i.pos.coords[0] * Math.cos(yRot)) + (i.pos.coords[2] * Math.sin(yRot));
//                            first.coords[1] = i.pos.coords[1];
//                            first.coords[2] = (i.pos.coords[2] * Math.cos(yRot)) - (i.pos.coords[0] * Math.sin(yRot));
//                            NVector second = new NVector(dims);
//                            second.coords[0] = first.coords[0];
//                            second.coords[1] = (first.coords[1] * Math.cos(xRot)) - (first.coords[2] * Math.sin(xRot));
//                            second.coords[2] = (first.coords[1] * Math.sin(xRot)) + (first.coords[2] * Math.cos(xRot));
//                            bucket.x = second.coords[0] - 0.01;
//                            bucket.y = second.coords[1] - 0.01;
//                            bucket.width = (0.02 * (second.coords[2] + 1)) + 0.01;
//                            bucket.height = (0.02 * (second.coords[2] + 1)) + 0.01;
                                            //TODO Nest and combine these and all such ifs here.
                                            if (current.coords.length > 0) {
                                                bucket.x = current.coords[0] - 0.01 + stereoCurDelta;
                                            } else {
                                                bucket.x = 0;
                                            }
                                            if (current.coords.length > 1) {
                                                bucket.y = current.coords[1] - 0.01;
                                            } else {
                                                bucket.y = 0;
                                            }
                                            if (current.coords.length > 2) {
                                                bucket.width = (0.02 * (current.coords[2] + 1)) + 0.01;
                                                bucket.height = (0.02 * (current.coords[2] + 1)) + 0.01;
                                            } else {
                                                bucket.width = 0.01;
                                                bucket.height = 0.01;
                                            }
                                            i.displayed = true;
                                            if (hasStereo == 0) {
                                                i.displayPoint.setLocation(bucket.x, bucket.y);
                                            } else {
                                                i.displayPointStereo.setLocation(bucket.x, bucket.y);
                                            }
                                            if (stereo4 && current.coords.length > 3) {
                                                bucket.y = bucket.y + (stereo4CurAlpha * current.coords[3]);
                                            }
                                            g.draw(bucket);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (permacenter != null) {
                                    try {
                                        g.setColor(Color.green);
                                        Ellipse2D.Double bucket = new Ellipse2D.Double();
                                        NVector second = viewRotate(permacenter.pos, rot, rotCoords);
                                        bucket.x = second.coords[0] - permaradius + stereoCurDelta;
                                        bucket.y = second.coords[1] - permaradius;
//                        bucket.width = (0.02 * (second.coords[2] + 1)) + 0.01;
//                        bucket.height = (0.02 * (second.coords[2] + 1)) + 0.01;
                                        bucket.width = permaradius * 2;
                                        bucket.height = permaradius * 2;
                                        g.draw(bucket);
                                        if (second.coords.length > 0) {
                                            bucket.x = second.coords[0] - 0.01 + stereoCurDelta;
                                        } else {
                                            bucket.x = 0;
                                        }
                                        if (second.coords.length > 1) {
                                            bucket.y = second.coords[1] - 0.01;
                                        } else {
                                            bucket.y = 0;
                                        }
                                        if (second.coords.length > 2) {
                                            bucket.width = (0.02 * (second.coords[2] + 1)) + 0.01;
                                            bucket.height = (0.02 * (second.coords[2] + 1)) + 0.01;
                                        } else {
                                            bucket.width = 0.01;
                                            bucket.height = 0.01;
                                        }
                                        if (stereo4 && second.coords.length > 3) {
                                            bucket.y = bucket.y + (stereo4CurAlpha * second.coords[3]);
                                        }
                                        g.setColor(Color.magenta);
                                        g.draw(bucket);
                                    } catch (Exception e) {
                                    }
                                }
                                if (!hideComplete) {
                                    g.setColor(Color.blue);
                                    for (Stick s : completeSticks) {
                                        if (hideImmune && (s.pointA.immune || s.pointB.immune)) {
                                            continue;
                                        }
                                        try {
                                            Line2D.Double line = new Line2D.Double();
                                            NVector start = s.pointA.pos;
                                            NVector second = viewRotate(start, rot, rotCoords);
                                            start = s.pointB.pos;
                                            NVector fourth = viewRotate(start, rot, rotCoords);
                                            if (second.coords.length > 0) {
                                                line.x1 = second.coords[0] + stereoCurDelta;
                                            } else {
                                                line.x1 = 0;
                                            }
                                            if (second.coords.length > 1) {
                                                line.y1 = second.coords[1];
                                            } else {
                                                line.y1 = 0;
                                            }
                                            if (stereo4 && second.coords.length > 3) {
                                                line.y1 = line.y1 + (stereo4CurAlpha * second.coords[3]);
                                            }
                                            if (fourth.coords.length > 0) {
                                                line.x2 = fourth.coords[0] + stereoCurDelta;
                                            } else {
                                                line.x2 = 0;
                                            }
                                            if (fourth.coords.length > 1) {
                                                line.y2 = fourth.coords[1];
                                            } else {
                                                line.y2 = 0;
                                            }
                                            if (stereo4 && fourth.coords.length > 3) {
                                                line.y2 = line.y2 + (stereo4CurAlpha * fourth.coords[3]);
                                            }
                                            //bucket.width = 0.02;
                                            //bucket.height = 0.02;
                                            g.draw(line);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (!hideIncomplete) {
                                    g.setColor(Color.red);
                                    for (Stick s : incompleteSticks) {
                                        if (hideImmune && (s.pointA.immune || s.pointB.immune)) {
                                            continue;
                                        }
                                        try {
                                            Line2D.Double line = new Line2D.Double();
                                            NVector start = s.pointA.pos;
                                            NVector second = viewRotate(start, rot, rotCoords);
                                            start = s.pointB.pos;
                                            NVector fourth = viewRotate(start, rot, rotCoords);
                                            if (second.coords.length > 0) {
                                                line.x1 = second.coords[0] + stereoCurDelta;
                                            } else {
                                                line.x1 = 0;
                                            }
                                            if (second.coords.length > 1) {
                                                line.y1 = second.coords[1];
                                            } else {
                                                line.y1 = 0;
                                            }
                                            if (stereo4 && second.coords.length > 3) {
                                                line.y1 = line.y1 + (stereo4CurAlpha * second.coords[3]);
                                            }
                                            if (fourth.coords.length > 0) {
                                                line.x2 = fourth.coords[0] + stereoCurDelta;
                                            } else {
                                                line.x2 = 0;
                                            }
                                            if (fourth.coords.length > 1) {
                                                line.y2 = fourth.coords[1];
                                            } else {
                                                line.y2 = 0;
                                            }
                                            if (stereo4 && fourth.coords.length > 3) {
                                                line.y2 = line.y2 + (stereo4CurAlpha * fourth.coords[3]);
                                            }
                                            //bucket.width = 0.02;
                                            //bucket.height = 0.02;
                                            g.draw(line);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
//                                for (int m = 0; m < lattice.cells.size(); m++) {
//                                    NCell i = lattice.cells.get(m);
//                                    //for (NCell i : lattice.cells) {
//                                    for (int j = 0; j < i.faces.length; j++) {
//                                        if (i.faces[j].points.length != 1) {
//                                            //THINK Would it be more efficient to call i.faces[j].complete()?
//                                            if (lattice.incompleteFaces.contains(i.faces[j])) {
//                                                continue;
////                                                if (hideIncomplete) {
////                                                    continue;
////                                                }
//                                            } else {
//                                                if (hideComplete) {
//                                                    continue;
//                                                }
//                                            }
//                                            for (int k = 0; k < i.faces[j].points.length - 1; k++) {
//                                                if (hideImmune && i.faces[j].points[k].immune) {
//                                                    continue;
//                                                }
//                                                for (int l = k + 1; l < i.faces[j].points.length; l++) {
//                                                    try {
//                                                        if (hideImmune && i.faces[j].points[l].immune) {
//                                                            continue;
//                                                        }
//                                                        Line2D.Double line = new Line2D.Double();
//                                                        NVector start = i.faces[j].points[k].pos;
//                                                        NVector second = viewRotate(start, rot, rotCoords);
//                                                        start = i.faces[j].points[l].pos;
//                                                        NVector fourth = viewRotate(start, rot, rotCoords);
//                                                        if (second.coords.length > 0) {
//                                                            line.x1 = second.coords[0] + stereoCurDelta;
//                                                        } else {
//                                                            line.x1 = 0;
//                                                        }
//                                                        if (second.coords.length > 1) {
//                                                            line.y1 = second.coords[1];
//                                                        } else {
//                                                            line.y1 = 0;
//                                                        }
//                                                        if (fourth.coords.length > 0) {
//                                                            line.x2 = fourth.coords[0] + stereoCurDelta;
//                                                        } else {
//                                                            line.x2 = 0;
//                                                        }
//                                                        if (second.coords.length > 1) {
//                                                            line.y2 = fourth.coords[1];
//                                                        } else {
//                                                            line.y2 = 0;
//                                                        }
//                                                        //bucket.width = 0.02;
//                                                        //bucket.height = 0.02;
//                                                        g.draw(line);
//                                                    } catch (Exception e) {
//                                                    }
//                                                }
//                                            }
//                                        } else {
//                                            try {
//                                                if (hideImmune && (i.faces[j].points[0].immune || i.faces[j + 1 % i.faces.length].points[0].immune)) {
//                                                    continue;
//                                                }
//                                                Line2D.Double line = new Line2D.Double();
//                                                NVector start = i.faces[j].points[0].pos;
//                                                NVector second = viewRotate(start, rot, rotCoords);
//                                                start = i.faces[j + 1 % i.faces.length].points[0].pos;
//                                                NVector fourth = viewRotate(start, rot, rotCoords);
//                                                if (second.coords.length > 0) {
//                                                    line.x1 = second.coords[0] + stereoCurDelta;
//                                                } else {
//                                                    line.x1 = 0;
//                                                }
//                                                if (second.coords.length > 1) {
//                                                    line.y1 = second.coords[1];
//                                                } else {
//                                                    line.y1 = 0;
//                                                }
//                                                if (fourth.coords.length > 0) {
//                                                    line.x2 = fourth.coords[0] + stereoCurDelta;
//                                                } else {
//                                                    line.x2 = 0;
//                                                }
//                                                if (second.coords.length > 1) {
//                                                    line.y2 = fourth.coords[1];
//                                                } else {
//                                                    line.y2 = 0;
//                                                }
//                                                //bucket.width = 0.02;
//                                                //bucket.height = 0.02;
//                                                g.draw(line);
//                                            } catch (Exception e) {
//                                                //e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                }
//                                if (!hideIncomplete) {
//                                    g.setColor(Color.red);
//                                    for (int j = 0; j < lattice.incompleteFaces.size(); j++) {
//                                        for (int k = 0; k < lattice.incompleteFaces.get(j).points.length - 1; k++) {
//                                            if (hideImmune && lattice.incompleteFaces.get(j).points[k].immune) {
//                                                continue;
//                                            }
//                                            for (int l = k + 1; l < lattice.incompleteFaces.get(j).points.length; l++) {
//                                                try {
//                                                    if (hideImmune && lattice.incompleteFaces.get(j).points[l].immune) {
//                                                        continue;
//                                                    }
//                                                    Line2D.Double line = new Line2D.Double();
//                                                    NVector start = lattice.incompleteFaces.get(j).points[k].pos;
//                                                    NVector second = viewRotate(start, rot, rotCoords);
//                                                    start = lattice.incompleteFaces.get(j).points[l].pos;
//                                                    NVector fourth = viewRotate(start, rot, rotCoords);
//                                                    if (second.coords.length > 0) {
//                                                        line.x1 = second.coords[0] + stereoCurDelta;
//                                                    } else {
//                                                        line.x1 = 0;
//                                                    }
//                                                    if (second.coords.length > 1) {
//                                                        line.y1 = second.coords[1];
//                                                    } else {
//                                                        line.y1 = 0;
//                                                    }
//                                                    if (fourth.coords.length > 0) {
//                                                        line.x2 = fourth.coords[0] + stereoCurDelta;
//                                                    } else {
//                                                        line.x2 = 0;
//                                                    }
//                                                    if (second.coords.length > 1) {
//                                                        line.y2 = fourth.coords[1];
//                                                    } else {
//                                                        line.y2 = 0;
//                                                    }
//                                                    //bucket.width = 0.02;
//                                                    //bucket.height = 0.02;
//                                                    g.draw(line);
//                                                } catch (Exception e) {
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
                                g.setColor(Color.ORANGE);
                                for (Camera c : lattice.cameras) {
                                    try {//c.pos.length()
                                        Ellipse2D.Double bucket = new Ellipse2D.Double();

                                        NVector current = viewRotate(c.pos, rot, rotCoords);

                                        if (current.coords.length > 0) {
                                            bucket.x = current.coords[0] - 0.01 + stereoCurDelta;
                                        } else {
                                            bucket.x = 0;
                                        }
                                        if (current.coords.length > 1) {
                                            bucket.y = current.coords[1] - 0.01;
                                        } else {
                                            bucket.y = 0;
                                        }
                                        if (current.coords.length > 2) {
                                            bucket.width = (0.02 * (current.coords[2] + 1)) + 0.01;
                                            bucket.height = (0.02 * (current.coords[2] + 1)) + 0.01;
                                        } else {
                                            bucket.width = 0.01;
                                            bucket.height = 0.01;
                                        }
                                        g.draw(bucket);

                                        for (NVector v : c.orientation) {
                                            Line2D.Double line = new Line2D.Double();
                                            NVector start = c.pos;
                                            NVector second = viewRotate(start, rot, rotCoords);
                                            start = c.pos.plusB(v);
                                            NVector fourth = viewRotate(start, rot, rotCoords);
                                            if (second.coords.length > 0) {
                                                line.x1 = second.coords[0] + stereoCurDelta;
                                            } else {
                                                line.x1 = 0;
                                            }
                                            if (second.coords.length > 1) {
                                                line.y1 = second.coords[1];
                                            } else {
                                                line.y1 = 0;
                                            }
                                            if (stereo4 && second.coords.length > 3) {
                                                line.y1 = line.y1 + (stereo4CurAlpha * second.coords[3]);
                                            }
                                            if (fourth.coords.length > 0) {
                                                line.x2 = fourth.coords[0] + stereoCurDelta;
                                            } else {
                                                line.x2 = 0;
                                            }
                                            if (fourth.coords.length > 1) {
                                                line.y2 = fourth.coords[1];
                                            } else {
                                                line.y2 = 0;
                                            }
                                            if (stereo4 && fourth.coords.length > 3) {
                                                line.y2 = line.y2 + (stereo4CurAlpha * fourth.coords[3]);
                                            }
                                            //bucket.width = 0.02;
                                            //bucket.height = 0.02;
                                            g.draw(line);
                                        }
                                    } catch (Exception e) {
                                    }
                                }
                                int clrInt = 0x000000;

                                for (ArrayList<NVector> t : lattice.tracers) {
                                    Color clr = new Color(clrInt);
                                    clrInt += 0x04;
                                    g.setColor(clr);
                                    if (t.size() > 0) {
                                        for (int i = 1; i < t.size(); i++) {
                                            try {
                                                Line2D.Double line = new Line2D.Double();
                                                NVector start = t.get(i - 1);
                                                NVector second = viewRotate(start, rot, rotCoords);
                                                start = t.get(i);
                                                NVector fourth = viewRotate(start, rot, rotCoords);
                                                if (second.coords.length > 0) {
                                                    line.x1 = second.coords[0] + stereoCurDelta;
                                                } else {
                                                    line.x1 = 0;
                                                }
                                                if (second.coords.length > 1) {
                                                    line.y1 = second.coords[1];
                                                } else {
                                                    line.y1 = 0;
                                                }
                                                if (stereo4 && second.coords.length > 3) {
                                                    line.y1 = line.y1 + (stereo4CurAlpha * second.coords[3]);
                                                }
                                                if (fourth.coords.length > 0) {
                                                    line.x2 = fourth.coords[0] + stereoCurDelta;
                                                } else {
                                                    line.x2 = 0;
                                                }
                                                if (fourth.coords.length > 1) {
                                                    line.y2 = fourth.coords[1];
                                                } else {
                                                    line.y2 = 0;
                                                }
                                                if (stereo4 && fourth.coords.length > 3) {
                                                    line.y2 = line.y2 + (stereo4CurAlpha * fourth.coords[3]);
                                                }
                                                //bucket.width = 0.02;
                                                //bucket.height = 0.02;
                                                g.draw(line);
                                            } catch (Exception ex) {
                                                Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                    }
                                }
                                if (!highlighteds.isEmpty()) {
                                    g.setColor(Color.ORANGE);
                                    for (NPoint i : highlighteds) {
                                        try {
                                            if (hideImmune && i.immune) {
                                                continue;
                                            }
                                            Ellipse2D.Double bucket = new Ellipse2D.Double();

                                            NVector current = viewRotate(i.pos, rot, rotCoords);

                                            if (current.coords.length > 0) {
                                                bucket.x = current.coords[0] - 0.01 + stereoCurDelta;
                                            } else {
                                                bucket.x = 0;
                                            }
                                            if (current.coords.length > 1) {
                                                bucket.y = current.coords[1] - 0.01;
                                            } else {
                                                bucket.y = 0;
                                            }
                                            if (current.coords.length > 2) {
                                                bucket.width = (0.02 * (current.coords[2] + 1)) + 0.01;
                                                bucket.height = (0.02 * (current.coords[2] + 1)) + 0.01;
                                            } else {
                                                bucket.width = 0.01;
                                                bucket.height = 0.01;
                                            }
                                            if (stereo4 && current.coords.length > 3) {
                                                bucket.y = bucket.y + (stereo4CurAlpha * current.coords[3]);
                                            }
                                            g.draw(bucket);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (!highlightedCells.isEmpty()) {
                                    g.setColor(Color.DARK_GRAY);
                                    for (int m = 0; m < highlightedCells.size(); m++) {
                                        NCell i = highlightedCells.get(m);
                                        //for (NCell i : lattice.cells) {
                                        for (int j = 0; j < i.faces.length; j++) {
                                            if (i.faces[j].points.length != 1) {
                                                //THINK Would it be more efficient to call i.faces[j].complete()?
//                                                if (lattice.incompleteFaces.contains(i.faces[j])) {
//                                                    continue;
////                                                if (hideIncomplete) {
////                                                    continue;
////                                                }
//                                                } else {
//                                                    if (hideComplete) {
//                                                        continue;
//                                                    }
//                                                }
                                                for (int k = 0; k < i.faces[j].points.length - 1; k++) {
//                                                    if (hideImmune && i.faces[j].points[k].immune) {
//                                                        continue;
//                                                    }
                                                    for (int l = k + 1; l < i.faces[j].points.length; l++) {
                                                        try {
//                                                            if (hideImmune && i.faces[j].points[l].immune) {
//                                                                continue;
//                                                            }
                                                            Line2D.Double line = new Line2D.Double();
                                                            NVector start = i.faces[j].points[k].pos;
                                                            NVector second = viewRotate(start, rot, rotCoords);
                                                            start = i.faces[j].points[l].pos;
                                                            NVector fourth = viewRotate(start, rot, rotCoords);
                                                            if (second.coords.length > 0) {
                                                                line.x1 = second.coords[0] + stereoCurDelta;
                                                            } else {
                                                                line.x1 = 0;
                                                            }
                                                            if (second.coords.length > 1) {
                                                                line.y1 = second.coords[1];
                                                            } else {
                                                                line.y1 = 0;
                                                            }
                                                            if (stereo4 && second.coords.length > 3) {
                                                                line.y1 = line.y1 + (stereo4CurAlpha * second.coords[3]);
                                                            }
                                                            if (fourth.coords.length > 0) {
                                                                line.x2 = fourth.coords[0] + stereoCurDelta;
                                                            } else {
                                                                line.x2 = 0;
                                                            }
                                                            if (fourth.coords.length > 1) {
                                                                line.y2 = fourth.coords[1];
                                                            } else {
                                                                line.y2 = 0;
                                                            }
                                                            if (stereo4 && fourth.coords.length > 3) {
                                                                line.y2 = line.y2 + (stereo4CurAlpha * fourth.coords[3]);
                                                            }
                                                            //bucket.width = 0.02;
                                                            //bucket.height = 0.02;
                                                            g.draw(line);
                                                        } catch (Exception e) {
                                                        }
                                                    }
                                                }
                                            } else {
                                                try {
//                                                    if (hideImmune && (i.faces[j].points[0].immune || i.faces[j + 1 % i.faces.length].points[0].immune)) {
//                                                        continue;
//                                                    }
                                                    Line2D.Double line = new Line2D.Double();
                                                    NVector start = i.faces[j].points[0].pos;
                                                    NVector second = viewRotate(start, rot, rotCoords);
                                                    start = i.faces[j + 1 % i.faces.length].points[0].pos;
                                                    NVector fourth = viewRotate(start, rot, rotCoords);
                                                    if (second.coords.length > 0) {
                                                        line.x1 = second.coords[0] + stereoCurDelta;
                                                    } else {
                                                        line.x1 = 0;
                                                    }
                                                    if (second.coords.length > 1) {
                                                        line.y1 = second.coords[1];
                                                    } else {
                                                        line.y1 = 0;
                                                    }
                                                    if (stereo4 && second.coords.length > 3) {
                                                        line.y1 = line.y1 + (stereo4CurAlpha * second.coords[3]);
                                                    }
                                                    if (fourth.coords.length > 0) {
                                                        line.x2 = fourth.coords[0] + stereoCurDelta;
                                                    } else {
                                                        line.x2 = 0;
                                                    }
                                                    if (fourth.coords.length > 1) {
                                                        line.y2 = fourth.coords[1];
                                                    } else {
                                                        line.y2 = 0;
                                                    }
                                                    if (stereo4 && fourth.coords.length > 3) {
                                                        line.y2 = line.y2 + (stereo4CurAlpha * fourth.coords[3]);
                                                    }
                                                    //bucket.width = 0.02;
                                                    //bucket.height = 0.02;
                                                    g.draw(line);
                                                } catch (Exception e) {
                                                    //e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!chosens.isEmpty()) {
                                    g.setColor(Color.CYAN);
                                    for (NPoint i : chosens) {
                                        if (i == null) {
                                            continue;
                                        }
                                        try {
                                            if (hideImmune && i.immune) {
                                                continue;
                                            }
                                            Ellipse2D.Double bucket = new Ellipse2D.Double();

                                            NVector current = viewRotate(i.pos, rot, rotCoords);

                                            if (current.coords.length > 0) {
                                                bucket.x = current.coords[0] - 0.01 + stereoCurDelta;
                                            } else {
                                                bucket.x = 0;
                                            }
                                            if (current.coords.length > 1) {
                                                bucket.y = current.coords[1] - 0.01;
                                            } else {
                                                bucket.y = 0;
                                            }
                                            if (current.coords.length > 2) {
                                                bucket.width = (0.02 * (current.coords[2] + 1)) + 0.01;
                                                bucket.height = (0.02 * (current.coords[2] + 1)) + 0.01;
                                            } else {
                                                bucket.width = 0.01;
                                                bucket.height = 0.01;
                                            }
                                            if (stereo4 && current.coords.length > 3) {
                                                bucket.y = bucket.y + (stereo4CurAlpha * current.coords[3]);
                                            }
                                            g.draw(bucket);
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                                if (hasStereo == 1) {
                                    rot[1] += stereoDegrees;
                                }
                            }
                        }
                        break;
                    default:
                }
            case RMODE_CAMERA:
                break;
            default:
        }
        //g.scale(0.001, 0.001);
        //g.translate(-250, -250);
    }
    public double repulsionCutoff = -1;
    public int repulsionMode = 0;

    public void repel(double forceFactor) {
//        switch (mode) {
//            case 0: // 2-2 Triangulation
        if (lattice != null) {
            switch (repulsionMode) {
                case 0:
                    for (NPoint i : lattice.points) {
                        NVector force = new NVector(dims);
                        for (int j = 0; j < dims; j++) {
                            force.coords[j] = 0;
                        }
//                        force.coords[0] = 0;
//                        force.coords[1] = 0;
                        for (NPoint j : lattice.points) {
                            // Add j force upon i.
                            if (i != j) {
                                double[] dif = new double[dims];
                                double dist2 = 0;
                                double dtot = 0; // Stands for distance total.  It's like Manhattan distance.
                                for (int k = 0; k < dims; k++) {
                                    dif[k] = i.pos.coords[k] - j.pos.coords[k];
                                    dist2 += sqr(dif[k]);
                                    dtot += Math.abs(dif[k]);
                                }
                                if (repulsionCutoff >= 0 && dtot < repulsionCutoff) {
                                    //TODO This check could be put in the outer loop.
                                    continue;
                                }
//                                double xdif = i.pos.coords[0] - j.pos.coords[0];
//                                double ydif = i.pos.coords[1] - j.pos.coords[1];
//                                double dist2 = sqr(xdif) + sqr(ydif);
//                                double dtot = (Math.abs(xdif) + Math.abs(ydif));
                                if (dtot != 0) {
                                    double[] perc = new double[dims];
                                    for (int k = 0; k < dims; k++) {
                                        perc[k] = dif[k] / dtot;
                                    }
//                                    double xperc = xdif / dtot;
//                                    double yperc = ydif / dtot;

                                    // The following math is highly suspect.  Check it.
                                    //if (((dist2 * Math.abs(ydif)) != 0) && ((dist2 * Math.abs(xdif)) != 0)) {

                                    for (int k = 0; k < dims; k++) {
                                        force.coords[k] += perc[k] / dist2;
                                    }
//                                    force.coords[0] += xperc / dist2;
//                                    force.coords[1] += yperc / dist2;
                                }
                            }
                        }
                        for (int k = 0; k < dims; k++) {
                            force.coords[k] *= forceFactor;
                        }
//                        force.coords[0] /= 10000;
//                        force.coords[1] /= 10000;
                        for (int k = 0; k < dims; k++) {
                            i.pos.coords[k] += force.coords[k];
                        }
//                        i.pos.coords[0] += force.coords[0];
//                        i.pos.coords[1] += force.coords[1];

//                        if (i.pos.coords[0] > 1) {
//                            i.pos.coords[0] = 1;
//                        } else if (i.pos.coords[0] < -1) {
//                            i.pos.coords[0] = -1;
//                        }
//                        if (i.pos.coords[1] > 1) {
//                            i.pos.coords[1] = 1;
//                        } else if (i.pos.coords[1] < -1) {
//                            i.pos.coords[1] = -1;
//                        }
                    }
                    break;
                default:
            }
        }
//                break;
//            default:
//        }
    }
    public ArrayList<NPoint> chosens = new ArrayList<NPoint>();
    public ArrayList<NPoint> highlighteds = new ArrayList<NPoint>();
    public ArrayList<NCell> highlightedCells = new ArrayList<NCell>();

    public void clickPoint(Point mousePoint, boolean shift, boolean ctrl, boolean alt, int button) {
        try {
            AffineTransform t = new AffineTransform();

            t.translate((lastWidth / 2.0) + lastTransX, (lastHeight / 2.0) + lastTransY);
            t.scale(lastScaleX, lastScaleY);

            Point2D p = t.inverseTransform(mousePoint, null);

            switch (mode) {
//                case 0: // 2-2 Triangulation
                default:
                    if (lattice != null) {
                        if (alt) {
                            chosens.clear();
                            break;
                        }
                        if (!ctrl) {
                            chosens.clear();
                            //break;
                        }
                        NPoint closest = null;
                        double dist = 0;
                        for (NPoint i : lattice.points) {
                            if (!i.displayed) {
                                continue;
                            }
                            double newdist = 10;
                            if (stereo) {
                                newdist = Math.min(i.displayPoint.distance(p), i.displayPointStereo.distance(p));
                            } else {
                                newdist = i.displayPoint.distance(p);
                            }
                            //double newdist = sqr(i.pos.coords[0] - p.getX()) + sqr(i.pos.coords[1] - p.getY());
                            if ((closest == null) || newdist < dist) {
                                closest = i;
                                dist = newdist;
                            }
                        }
                        if (closest == null) {
                            break;
                        }
                        if (chosens.contains(closest)) {
                            chosens.remove(closest);
                        } else {
                            chosens.add(closest);
                        }
                    }
                    break;
            }
        } catch (NoninvertibleTransformException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTriangle() {
        if (chosens.size() == 3) { // This will only work for 2 in 2 dims
            NCell newCell = new NCell(dims, lattice.internalDims);
            newCell.points[0] = chosens.get(0);
            newCell.points[1] = chosens.get(1);
            newCell.points[2] = chosens.get(2);
            NFace face0 = new NFace(dims, lattice.internalDims);
            face0.points[0] = newCell.points[0];
            face0.points[0].faces.add(face0);
            face0.points[1] = newCell.points[1];
            face0.points[1].faces.add(face0);
            face0.cellA = newCell;
            newCell.faces[0] = face0;
            lattice.incompleteFaces.add(face0);
            lattice.faces.add(face0);
            NFace face1 = new NFace(dims, lattice.internalDims);
            face1.points[0] = newCell.points[0];
            face1.points[0].faces.add(face1);
            face1.points[1] = newCell.points[2];
            face1.points[1].faces.add(face1);
            face1.cellA = newCell;
            newCell.faces[1] = face1;
            lattice.incompleteFaces.add(face1);
            lattice.faces.add(face1);
            NFace face2 = new NFace(dims, lattice.internalDims);
            face2.points[0] = newCell.points[1];
            face2.points[0].faces.add(face2);
            face2.points[1] = newCell.points[2];
            face2.points[1].faces.add(face2);
            face2.cellA = newCell;
            newCell.faces[2] = face2;
            lattice.incompleteFaces.add(face2);
            lattice.faces.add(face2);
            lattice.cells.add(newCell);
        }
    }

    public boolean seed() {
        if (radiusNearestAverageCount == -1) {
            radiusNearestAverageCount = 0;
            double radiusNearestTotal = 0;
            for (NPoint p : lattice.points) {
                double nearestDist = -1;
                NPoint b = null;
                for (NPoint i : lattice.points) {
                    if (((nearestDist == -1) || (p.distSqr(i) < nearestDist)) && (p != i)) {
                        b = i;
                        nearestDist = p.distSqr(i);
                    }
                }
                radiusNearestTotal += Math.sqrt(nearestDist);
                radiusNearestAverageCount++;
            }
            radiusNearestAverage = radiusNearestTotal / radiusNearestAverageCount;
        }        
        //NFace leaf = lattice.incompleteFaces.get(faceNum);
        NPoint a = lattice.points.get(r.nextInt(lattice.points.size()));
        double nearestDist = -1;
        NPoint b = null;
        if (!candidateSystem) {
            for (NPoint i : lattice.points) {
                if (((nearestDist == -1) || (a.distSqr(i) < nearestDist)) && (a != i)) {
                    b = i;
                    nearestDist = a.distSqr(i);
                }
            }
        } else {
            for (NPoint i : a.candidates) {
                if (((nearestDist == -1) || (a.distSqr(i) < nearestDist)) && (a != i)) {
                    b = i;
                    nearestDist = a.distSqr(i);
                }
            }
        }
        NFace leaf = new NFace(dims, lattice.internalDims);
        if (leaf.points.length > 0) {
            leaf.points[0] = a;
        }
        if (leaf.points.length > 1) {
            leaf.points[1] = b;
        }


//        double radius = radiusSize * leaf.points[0].dist(leaf.points[1]);
        double radius;
        if (radiusLimitAverage) {
            radiusAverage = leaf.points[0].dist(b);
            radiusCount = 1;
            radius = radiusSize * radiusAverage;
        } else {
            radius = radiusSize * leaf.points[0].dist(b);
        }
        if (radiusLimitNearestAverage) {
            radius = Math.min(radius, radiusSize * radiusNearestAverage);
        }
        if (maxLength > -1) {
            radius = Math.min(radius, maxLength);
        }
        ArrayList<NPoint> nearest = new ArrayList<NPoint>();
        if (!candidateSystem) {
            if (radiusSize != -1) {
                for (NPoint i : lattice.points) {
                    if ((leaf.points[0].dist(i) <= radius) && (!leaf.isaPoint(i)) && (!i.immune)) {
                        nearest.add(i);
                    }
                }
            } else {
                for (NPoint i : lattice.points) {
                    if ((!leaf.isaPoint(i)) && (!i.immune)) {
                        nearest.add(i);
                    }
                }
            }
        } else {
            nearest.addAll(leaf.points[0].candidates);
            for (NPoint p : leaf.points) {
                // It's like set intersection.
                if (p != null) {
                    nearest.retainAll(p.candidates);
                }
            }
        }
        ArrayList<NPoint> anchors = new ArrayList<NPoint>();
        for (NPoint i : leaf.points) {
            if (i != null) {
                anchors.add(i);
            }
        }
        return seedRecurse(nearest, anchors, leaf, leaf.points.length + 1, anchors.size());
    }

    public boolean seedRecurse(ArrayList<NPoint> nearest, ArrayList<NPoint> anchors, NFace leaf, int finalDepth, int currentDepth) {
        if (currentDepth < finalDepth - 1) {
            ArrayList<NPoint> subNearest = (ArrayList<NPoint>) nearest.clone();
            if (!candidateSystem) {
                for (NPoint i : nearest) {
                    leaf.points[currentDepth] = i;
                    anchors.add(i);
                    subNearest.remove(i);
                    if (seedRecurse(subNearest, anchors, leaf, finalDepth, currentDepth + 1)) {
                        return true;
                    }
                    anchors.remove(i);
                    subNearest.add(i);
                }
            } else {
                for (NPoint i : nearest) {
                    leaf.points[currentDepth] = i;
                    anchors.add(i);
                    subNearest.remove(i);
                    ArrayList<NPoint> removed = (ArrayList<NPoint>) subNearest.clone();
                    removed.removeAll(i.candidates);
                    subNearest.retainAll(i.candidates);
                    if (seedRecurse(subNearest, anchors, leaf, finalDepth, currentDepth + 1)) {
                        return true;
                    }
                    anchors.remove(i);
                    subNearest.addAll(removed);
                    subNearest.add(i);
                }
            }
        } else {
            for (NPoint i : nearest) {
                anchors.add(i);
                //TODO I really ought to change this to just pick one in withinCircle and go from there, but hang on.
                if (((maxThinness == -1) || checkThinness(anchors, maxThinness)) && ((minAngle == -1) || checkMinAngle(anchors, minAngle)) && checkMinMaxVolume(anchors, minVolume, maxVolume) && checkMinMaxLength(anchors, minLength, maxLength)) {
                    ArrayList<NPoint> withinCircle = findCircleContentsALT(anchors, true, false);
                    if (withinCircle.isEmpty()) {
                        NCell newCell = new NCell(dims, lattice.internalDims);
                        for (int j = 0; j < newCell.points.length; j++) {
                            newCell.points[j] = anchors.get(j);
                        }
                        newCell.faces[0] = leaf;
                        leaf.cellA = newCell;
                        for (int j = 1; j < newCell.faces.length; j++) {
                            NFace newFace = new NFace(dims, lattice.internalDims);
                            int index = 0;
                            for (int k = newCell.points.length - 1; k >= 0; k--) {
                                if (((newCell.points.length - 1) - k) == j) { //Uggh.  I think this should work.  Each face should skip a different point.
                                    continue;
                                }
                                newFace.points[index] = newCell.points[k];
                                //newFace.points[index].faces.add(newFace);
                                index++;
                            }
                            boolean matched = false;
                            NFace match = null;
//                                for (NFace f : lattice.faces) {
//                                    if (f.equivalent(newFace)) {
//                                        matched = true;
//                                        match = f;
//                                        break;
//                                    }
//                                }
                            if (matched) {
//                                    match.cellB = newCell;
//                                    newCell.faces[j] = match;
//                                    lattice.incompleteFaces.remove(match); //TODO Maybe double-check?
                            } else {
                                for (NPoint p : newFace.points) {
                                    p.faces.add(newFace);
                                }
                                newFace.cellA = newCell;
                                newCell.faces[j] = newFace;
                                lattice.incompleteFaces.add(newFace);
                                lattice.faces.add(newFace);
                            }
                        }
                        for (NPoint p : leaf.points) {
                            p.faces.add(leaf);
                        }
                        lastCell = newCell;
                        lattice.cells.add(newCell);
                        lattice.incompleteFaces.add(leaf);
                        lattice.faces.add(leaf);
                        sticksChanged = true;
                        
                        if (radiusLimitAverage) {
                            radiusAverage = 0;
                            radiusCount = 0;
                            for (int j = 0; j < newCell.points.length - 1; j++) {
                                for (int k = j + 1; k < newCell.points.length; k++) {
                                    radiusAverage += newCell.points[j].dist(newCell.points[k]);
                                    radiusCount++;
                                }
                            }
                            // I'm going to assume cells have at least one point in them.
                            radiusAverage /= radiusCount;
                        }
                        return true;
                    }
                }
                anchors.remove(i);
            }
        }
        return false;
    }
    public double permaradius = 0;
    public NPoint permacenter = null;
    public NCell lastCell = null;

    public ArrayList<NPoint> findCircleContents(ArrayList<NPoint> anchors) {
        Matrix augmented = null;
        try {
            //TODO I need to figure out how to do this in arbitrary dimensions.
            // Two dimensions, for now:
            double[][] crossA = new double[anchors.size() - 2][dims];
            for (int i = 1; i < anchors.size() - 1; i++) {
                NVector bucket = anchors.get(i).pos.minusB(anchors.get(0).pos);
                for (int j = 0; j < dims; j++) {
                    crossA[i - 1][j] = bucket.coords[j];
                }
            }
            double[] cA = new double[dims];
            for (int i = 0; i < dims; i++) {
                cA[i] = 0;
            }
            for (int i = 0; i < anchors.size() - 1; i++) {
                for (int j = 0; j < dims; j++) {
                    cA[j] += anchors.get(i).pos.coords[j];
                }
            }
            for (int i = 0; i < dims; i++) {
                cA[i] /= anchors.size() - 1;
            }

            double[][] crossB = new double[anchors.size() - 2][dims];
            for (int i = 2; i < anchors.size(); i++) {
                NVector bucket = anchors.get(i).pos.minusB(anchors.get(1).pos);
                for (int j = 0; j < dims; j++) {
                    crossB[i - 2][j] = bucket.coords[j];
                }
            }
            double[] cB = new double[dims];
            for (int i = 0; i < dims; i++) {
                cB[i] = 0;
            }
            for (int i = 1; i < anchors.size(); i++) {
                for (int j = 0; j < dims; j++) {
                    cB[j] += anchors.get(i).pos.coords[j];
                }
            }
            for (int i = 0; i < dims; i++) {
                cB[i] /= anchors.size() - 1;
            }
            if (crossA.length == dims - 2) {
                double[][] newCross = new double[dims - 1][dims];
                for (int i = 0; i < dims - 1; i++) {
                    for (int j = 0; j < dims; j++) {
                        newCross[i][j] = anchors.get(i + 1).pos.coords[j] - anchors.get(0).pos.coords[j];
                    }
                }
                double[] extraNorm = Matrix.cross(newCross);
                for (int i = 0; i < dims - 2; i++) {
                    for (int j = 0; j < dims; j++) {
                        newCross[i][j] = crossA[i][j];
                    }
                }
                for (int j = 0; j < dims; j++) {
                    newCross[dims - 2][j] = extraNorm[j];
                }
                crossA = newCross;
                newCross = new double[dims - 1][dims];
                for (int i = 0; i < dims - 2; i++) {
                    for (int j = 0; j < dims; j++) {
                        newCross[i][j] = crossB[i][j];
                    }
                }
                for (int j = 0; j < dims; j++) {
                    newCross[dims - 2][j] = extraNorm[j];
                }
                crossB = newCross;
            }
            double[] aNorm = Matrix.cross(crossA);
            double[] bNorm = Matrix.cross(crossB);

            boolean aNotZero = false;
            boolean bNotZero = false;
            augmented = Matrix.maybeGetCachedMatrix(3, dims, false);
            for (int i = 0; i < dims; i++) {
                augmented.val[0][i] = aNorm[i];
                if (aNorm[i] != 0) {
                    aNotZero = true;
                }
                augmented.val[1][i] = -bNorm[i];
                if (bNorm[i] != 0) {
                    bNotZero = true;
                }
                augmented.val[2][i] = cB[i] - cA[i];
                if (aNorm[i] == 0 && bNorm[i] == 0) {
                    throw new Exception("A coord of the norms is 0!");
                }
            }
            if (!(aNotZero && bNotZero)) {
                throw new Exception("A norm is 0!");
            }
            for (int i = 0; i < anchors.size(); i++) {
                for (int j = 0; j < anchors.size(); j++) {
                    if (i != j) {
                        boolean same = true;
                        for (int k = 0; k < dims; k++) {
                            if (anchors.get(i).pos.coords[k] != anchors.get(j).pos.coords[k]) {
                                same = false;
                                break;
                            }
                        }
                        if (same) {
                            throw new Exception("Two anchors coincide.");
                        }
                    }
                }
            }
            IntersectionResult solution = augmented.solveIntersect();
            augmented.doneWithMatrix(); augmented = null;
            NPoint center = new NPoint(dims);
            if (solution.q12 == 1) {
                for (int i = 0; i < dims; i++) {
                    center.pos.coords[i] = (aNorm[i] * solution.value) + cA[i];
                }
            } else if (solution.q12 == 2) {
                for (int i = 0; i < dims; i++) {
                    center.pos.coords[i] = (bNorm[i] * solution.value) + cB[i];
                }
            } else {
                throw new Exception("Argh, neither q1 nor q2.");
            }
            double radius = 0;
            for (int i = 0; i < dims; i++) {
                radius += sqr(center.pos.coords[i] - anchors.get(0).pos.coords[i]);
            }
            ArrayList<NPoint> result = new ArrayList<NPoint>();
            for (NPoint i : lattice.points) { //TODO This seems to be the slowest point ever.
                if ((center.distSqr(i) <= radius) && (!anchors.contains(i))) {
                    result.add(i);
                }
            }
            if (result.size() == 0) {
                permacenter = center;
                permaradius = Math.sqrt(radius);
            }
            return result;

//            double ax = anchors.get(0).pos.coords[0];
//            double ay = anchors.get(0).pos.coords[1];
//            double bx = anchors.get(1).pos.coords[0];
//            double by = anchors.get(1).pos.coords[1];
//            double cx = anchors.get(2).pos.coords[0];
//            double cy = anchors.get(2).pos.coords[1];
//            double centerX = ((by*cx*cx)-(bx*bx*cy)-(by*by*cy)+(by*cy*cy)+(ax*ax*(cy-by))+(ay*ay*(cy-by))+(ay*((bx*bx)+(by*by)-(cx*cx)-(cy*cy))))
//                                /
//                             (2*((ay*(bx-cx))+(by*cx)-(bx*cy)+(ax*(cy-by))));
//            double centerY = ((ax*ax*(bx-cx))+(ay*ay*(bx-cx))+(cx*((bx*bx)+(by*by)-(bx*cx)))-(bx*cy*cy)+(ax*((cx*cx)+(cy*cy)-(bx*bx)-(by*by))))
//                                /
//                             (2*((ay*(bx-cx))+(by*cx)-(bx*cy)+(ax*(cy-by))));
//            double radius = sqr(centerX - ax) + sqr(centerY - ay);
//            NPoint center = new NPoint(2);
//            center.pos.coords[0] = centerX;
//            center.pos.coords[1] = centerY;
//            ArrayList<NPoint> result = new ArrayList<NPoint>();
//            for (NPoint i : lattice.points) { //TODO This seems to be the slowest point ever.
//                if ((center.distSqr(i) <= radius) && (!anchors.contains(i))) {
//                    result.add(i);
//                }
//            }
//            return result;
        } catch (Exception ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
            //System.err.println(ex.getMessage());
            ArrayList<NPoint> scratch = new ArrayList<NPoint>();
            scratch.add(new NPoint(dims));
            return scratch;
        } finally {
            if (augmented != null) augmented.doneWithMatrix();            
        }
    }

    public ArrayList<NPoint> findCircleContentsALT(ArrayList<NPoint> anchors, boolean stopAtOne, boolean alwaysSetPermashapes) {

        //TODO IMPORTANT: There's something wrong here, note 6x3 salt structure failure.
        try {
            // Summary:
            // (Ok, so this has kinda changed.  The idea's more or less the same.)
            // If less than n+1 points:
            //// Get orthogonal basis containing all points in the m-plane all in the first coords.
            ////// Gram-Schmidt schtuff happens here
            ////// Mathemagic happens
            //// NOPE Get inverse of that matrix, in order to:
            //// Coordinate transform into said basis, and get rid of the extra coords.
            // NOPE Make vectors out of pi - p0.
            // Lay the vectors out and make them into a matrix.
            // Get the determinant and multiply it by 2 ^ (number of columns).  (Call it denom, for denominator.)
            // For each vector, sum up the square of its coordinates and make a vector out of the resulting numbers.  Call this b.
            // For each coordinate of the center, replace that number column of the matrix with b and get the determinant and divide the result by denom.
            // If coordinate transformed:
            //// Multiply by the original basis matrix to get back into normal coordinates.
            //// Do any translation necessary.  I'll have to see if it's needed.
            // Congrats!  You now have the center of whatever n-sphere is appropriate for the number of points you have!

            // Actually doing it:
            // If less than n+1 points:
            ArrayList<NVector> bases = null;
            Matrix basis = null;
            //Matrix basisInv = null;
            ArrayList<NVector> newAnchors = new ArrayList<NVector>();
            if (anchors.size() < dims + 1) {
                //// Get orthogonal basis containing all points in the m-plane all in the first coords.
                //basis = new Matrix(dims, dims);
                bases = new ArrayList<NVector>();
                ArrayList<NVector> points = new ArrayList<NVector>();
                NVector blank = new NVector(dims);
//                for (int i = 0; i < dims; i++) {
//                    blank.coords[i] = 0;
//                }
                points.add(blank);
                for (int i = 1; i < anchors.size(); i++) {
//                    NVector bucket = new NVector(dims);
//                    NVector bucket2 = new NVector(dims);
//                    for (int j = 0; j < dims; j++) {
//                        bucket.coords[j] = anchors.get(i).pos.coords[j] - anchors.get(0).pos.coords[j];
//                        bucket2.coords[j] = anchors.get(i).pos.coords[j] - anchors.get(0).pos.coords[j];
//                    }
                    bases.add(anchors.get(i).pos.minusB(anchors.get(0).pos));
                    points.add(anchors.get(i).pos.minusB(anchors.get(0).pos));
                }
                basis = Matrix.maybeGetCachedMatrix(bases.size(), dims, false);//MTXOFT*
                // Gram-Schmidt orthogonalization
                for (int i = 0; i < bases.size(); i++) {
                    for (int j = 0; j < i; j++) {
                        bases.set(i, bases.get(i).minusB(NVector.lrProj(bases.get(j), bases.get(i))));
                    }//System.out.println(bases);
                    bases.get(i).ipNormalize();
                    for (int j = 0; j < dims; j++) {
                        basis.val[i][j] = bases.get(i).coords[j];
                    }//System.out.println(basis);//System.out.println(points);
                }//NVector.angle(bases.get(0), bases.get(1))
                // Get the coordinates of the vectors 
                NVector[] result = Matrix.ipTransformCoords(bases, points);
                for (int i = 0; i < result.length; i++) {
                    newAnchors.add(result[i]);
                }
//                // Stand the vectors up on end and 
//                //// Get inverse of that matrix
//                basisInv = Matrix.invert(basis);
//                //// Coordinate transform into said basis, and get rid of the extra coords.
//                for (int i = 0; i < anchors.size(); i++) {
//                    NVector bucket = Matrix.lrvMult(basis, anchors.get(i).pos);
//                    NVector reduced = new NVector(anchors.size() - 1);
//                    for (int j = 0; j < anchors.size() - 1; j++) {
//                        reduced.coords[j] = bucket.coords[j];
//                    }
//                    newAnchors.add(bucket);
//                }
            } else {
                for (int i = 0; i < anchors.size(); i++) {
                    //NVector bucket = new NVector(dims);
                    // It might be dangerous to add them directly like this, at least if they get changed later.
                    newAnchors.add(anchors.get(i).pos);
                }
            }
            // NOPE Make vectors out of pi - p0.
            // Lay the vectors out and make them into a matrix.  We're using Cramer's Rule.
            Matrix cramer = Matrix.maybeGetCachedMatrix(newAnchors.get(0).dims + 1, newAnchors.size(), false);
            for (int row = 0; row < newAnchors.size(); row++) {
                for (int col = 0; col < newAnchors.get(0).dims; col++) {
                    cramer.val[col][row] = 2 * newAnchors.get(row).coords[col];
                }
                cramer.val[newAnchors.get(0).dims][row] = 1;
            }
//            if (0 == 1) {
//            Matrix test = new Matrix(newAnchors.get(0).dims, newAnchors.size());
//            for (int row = 0; row < newAnchors.size(); row++) {
//                //THINK Er...maybe use arraycopy?
//                for (int col = 0; col < newAnchors.get(0).dims; col++) {
//                    test.val[col][row] = newAnchors.get(row).coords[col];
//                }
//            }
//                System.out.println(test);
//                System.out.println(cramer);
//            }
            // Get the determinant and multiply it by 2 ^ (number of columns).  (Call it denom, for denominator.)
            double denom = cramer.det();// * ((int)Math.pow(2, cramer.cols - 1));
//            System.out.println(denom);
            // For each vector, sum up the square of its coordinates and make a vector out of the resulting numbers.  Call this b.
            NVector b = new NVector(newAnchors.size());
            for (int i = 0; i < newAnchors.size(); i++) {
                b.coords[i] = NVector.lrDot(newAnchors.get(i), newAnchors.get(i));
            }
            // For each coordinate of the center, replace that number column of the matrix with b and get the determinant and divide the result by denom.
            NVector centerV = new NVector(newAnchors.get(0).dims);
            for (int col = 0; col < newAnchors.get(0).dims; col++) {
                Matrix temp = cramer.copyWCache();//MTXOFT*
                System.arraycopy(b.coords, 0, temp.val[col], 0, temp.rows);
//                for (int row = 0; row < temp.rows; row++) {
//                    temp.val[col][row] = b.coords[row];
//                }//System.out.println(temp);
                centerV.coords[col] = temp.det() / denom;
                temp.doneWithMatrix();
//                System.out.println(temp.det());
            }
            cramer.doneWithMatrix();
//            System.out.println(centerV);

            double radius = 0;
//            for (int j = 0; j < newAnchors.size(); j++) {
//                radius = 0;
            for (int i = 0; i < centerV.dims; i++) {
                radius += sqr(centerV.coords[i] - newAnchors.get(0).coords[i]);
            }//System.out.println(Math.sqrt(radius));
//                System.out.println("flatrad" + j + "=" +Math.sqrt(radius));
//            }

            // If coordinate transformed:
            NPoint center = new NPoint(dims);
            if (basis != null) {//System.out.println(basis)
                //// Multiply by the original basis matrix to get back into normal coordinates.
                //System.out.println(Matrix.lrvMult(basis, newAnchors.get(1)))
                //System.out.println(anchors.get(0).pos)
                //System.out.println(anchors.get(1).pos)
                center.pos = Matrix.lrvMult(basis, centerV);
                //// Do any translation necessary.  I'll have to see if it's needed.
                center.pos = center.pos.plusB(anchors.get(0).pos);

//                System.out.println(bases);
                for (int i = 0; i < anchors.size(); i++) {
                    NVector bucket = Matrix.lrvMult(basis, newAnchors.get(i));
                    //// Do any translation necessary.  I'll have to see if it's needed.
                    bucket = bucket.plusB(anchors.get(0).pos);
//                    System.out.println("travec" + i + "=" + bucket.toString());
//                    System.out.println("vector" + i + "=" + anchors.get(i).pos.toString());
                }
                basis.doneWithMatrix();
            } else {
                center.pos = centerV;
            }
            // Congrats!  You now have the center of whatever n-sphere is appropriate for the number of points you have!

            radius = 0;
//            for (int j = 0; j < anchors.size(); j++) {
//                radius = 0;
            for (int i = 0; i < dims; i++) {
                radius += sqr(center.pos.coords[i] - anchors.get(0).pos.coords[i]);
            }//System.out.println(Math.sqrt(radius));
//                System.out.println("rad" + j + "=" +Math.sqrt(radius));
//            }
            ArrayList<NPoint> result = new ArrayList<NPoint>();
            for (int i = 0; i < center.pos.coords.length; i++) {
                if (Double.isNaN(center.pos.coords[i]) || Double.isInfinite(center.pos.coords[i])) {
                    throw new Exception("Straight line sphere");
                }
            }
            if (containmentFudgeValue != -1) {
                double sqrtRad = Math.sqrt(radius) - containmentFudgeValue;
                double newRadius = sqr(sqrtRad) * Math.signum(sqrtRad);
                if (stopAtOne) {
                    for (NPoint i : lattice.points) { //TODO This seems to be the slowest point ever.
                        if ((center.distSqr(i) < newRadius) && (!anchors.contains(i)) && (!i.immune)) {
                            result.add(i);
                            break;
                        }
                    }
                } else {
                    for (NPoint i : lattice.points) { //TODO This seems to be the slowest point ever.
                        if ((center.distSqr(i) < newRadius) && (!anchors.contains(i)) && (!i.immune)) {
                            result.add(i);
                        }
                    }
                }
            } else {
                if (stopAtOne) {
                    for (NPoint i : lattice.points) { //TODO This seems to be the slowest point ever.
                        if ((center.distSqr(i) <= radius) && (!anchors.contains(i)) && (!i.immune)) {
                            result.add(i);
                            break;
                        }
                    }
                } else {
                    for (NPoint i : lattice.points) { //TODO This seems to be the slowest point ever.
                        if ((center.distSqr(i) <= radius) && (!anchors.contains(i)) && (!i.immune)) {
                            result.add(i);
                        }
                    }
                }
            }
            if (result.isEmpty() || alwaysSetPermashapes) {
                permacenter = center;
                permaradius = Math.sqrt(radius);
            }
            return result;
        } catch (Exception ex) {
            //Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
            //System.err.println(ex.getMessage());
            ArrayList<NPoint> scratch = new ArrayList<NPoint>();
            scratch.add(new NPoint(dims));
            return scratch;
        }
    }
    public double radiusSize = 3;
    public boolean allowSkipHardFaces = true;
    public double maxThinness = -1;
    public double containmentFudgeValue = -1;
    public double minAngle = -1;
    public double minVolume = -1;
    public double maxVolume = -1;
    public double minLength = -1;
    public double maxLength = -1;
    public int faceBottom = 0;
    public NCell taggedCell = null;
    public NFace taggedFace = null;
    public double maxRefractionAngle = Math.PI / 2;
    public double radiusAverage = 0;
    public int radiusCount = 0;
    public boolean radiusLimitAverage = true;
    public boolean radiusLimitNearestAverage = false;
    public double radiusNearestAverage = 0;
    public int radiusNearestAverageCount = -1;

    public int lastCompleteCount = 0;
    
    public boolean crystallize(int runTag) {
        if (lattice != null) {//refreshCompletePoints();
            if (radiusNearestAverageCount == -1) {
                radiusNearestAverageCount = 0;
                double radiusNearestTotal = 0;
                for (NPoint p : lattice.points) {
                    double nearestDist = -1;
                    NPoint b = null;
                    for (NPoint i : lattice.points) {
                        if (((nearestDist == -1) || (p.distSqr(i) < nearestDist)) && (p != i)) {
                            b = i;
                            nearestDist = p.distSqr(i);
                        }
                    }
                    radiusNearestTotal += Math.sqrt(nearestDist);
                    radiusNearestAverageCount++;
                }
                radiusNearestAverage = radiusNearestTotal / radiusNearestAverageCount;
            }
            if (!lattice.incompleteFaces.isEmpty()) {//parent.dp.paintImmediately(parent.dp.getBounds());
                int top = 0;
                if (!allowSkipHardFaces) {
                    faceBottom = 0;
                    top = 0;
                } else {
                    top = lattice.incompleteFaces.size() - 1;
                }
                for (int faceNumBase = 0; faceNumBase <= top; faceNumBase++) {
                    int faceNum = (faceNumBase + faceBottom) % (top + 1);
                    NFace leaf = lattice.incompleteFaces.get(faceNum);
                    if (leaf.runTag == runTag) {
                        continue;
                    }
                    if (leaf.complete()) {
                        continue;
                    }
                    if (taggedFace != null && (taggedFace.equivalent(leaf))) {
                        System.out.println("Tagged face processed.");
                    }
                    double radius;
                    if (radiusLimitAverage) {
                        // Should I include stats from the current leaf?
                        //   It shouldn't matter, after a few leaves get added.
                        radius = radiusSize * radiusAverage;
                    } else {
                        if (leaf.points.length > 1) {
                            radius = radiusSize * leaf.points[0].dist(leaf.points[1]);
                        } else {
                            double nearestDist = -1;
                            NPoint b = null;
                            for (NPoint i : lattice.points) {
                                if (((nearestDist == -1) || (leaf.points[0].distSqr(i) < nearestDist)) && (leaf.points[0] != i)) {
                                    b = i;
                                    nearestDist = leaf.points[0].distSqr(i);
                                }
                            }
                            radius = radiusSize * Math.sqrt(nearestDist);
                        }
                    }
                    if (radiusLimitNearestAverage) {
                        radius = Math.min(radius, radiusSize * radiusNearestAverage);
                    }
                    if (maxLength > -1) {
                        radius = Math.min(radius, maxLength);
                    }
                    ArrayList<NPoint> nearest = new ArrayList<NPoint>();
                    if (!candidateSystem) {
                        if (radiusSize != -1) {
                            for (NPoint i : lattice.points) {
                                if (!i.complete) {
                                    i.calcComplete();
                                    if (!i.complete) {
                                        if ((leaf.points[0].dist(i) <= radius) && (!leaf.cellA.isaPoint(i)) && (!i.immune)) {
                                            nearest.add(i);
                                        }
                                    }
                                }
                            }
                        } else {
                            for (NPoint i : lattice.points) {
                                if (!leaf.cellA.isaPoint(i) && (!i.immune)) {
                                    nearest.add(i);
                                }
                            }
                        }
                    } else {
                        nearest.addAll(leaf.points[0].candidates);
                        for (NPoint p : leaf.points) {
                            // It's like set intersection.
                            nearest.retainAll(p.candidates);
                        }
                    }
                    ArrayList<NPoint> anchors = new ArrayList<NPoint>();
                    for (NPoint i : leaf.points) {
                        anchors.add(i);
                    }
                    int failCauseWithin = 0;
                    int failCauseThinness = 0;
                    int failCauseAngle = 0;
                    int failCauseVolume = 0;
                    int failCauseLength = 0;
                    int failCauseUndo = 0;
                    for (NPoint i : nearest) {
                        anchors.add(i);
                        if (taggedCell != null && (taggedCell.equivalent(anchors))) {
                            System.out.println("Tagged cell processed.");
                        }//calcThinness2(taggedCell.points);//calcThinness2(anchors.toArray(new NPoint[0]));
                        //TODO I really ought to change this to just pick one in withinCircle and go from there, but hang on.
                        if (((maxThinness == -1) || checkThinness(anchors, maxThinness)) && ((minAngle == -1) || checkMinAngle(anchors, minAngle)) && checkMinMaxVolume(anchors, minVolume, maxVolume) && checkMinMaxLength(anchors, minLength, maxLength)) {
                            // If it's already an existing structure, formalize it.
                            NCell bucket = new NCell(lattice.dims, lattice.internalDims);
                            for (int j = 0; j < bucket.points.length; j++) {
                                bucket.points[j] = anchors.get(j);
                            }
                            bucket.soloMakeFaces();
                            boolean cellStructureExists = true;
                            for (NFace f : bucket.faces) {
                                if (f.points.length > 0) {
                                    boolean found = false;
                                    for (NFace pf : f.points[0].faces) {
                                        if (pf.equivalent(f)) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        cellStructureExists = false;
                                        break;
                                    }
                                }
//                                } else {
//                                    // Umm, else what?  I dunno; I'm leaving it.
//                                }
                            }
                            ArrayList<NPoint> withinCircle = null;
                            if (!cellStructureExists) {
                                withinCircle = findCircleContentsALT(anchors, true, false);
                            }
                            if (cellStructureExists || withinCircle.isEmpty()) {
//                                ArrayList
                                NCell newCell = new NCell(dims, lattice.internalDims);
                                lastCell = newCell;
                                for (int j = 0; j < newCell.points.length; j++) {
                                    newCell.points[j] = anchors.get(j);
                                }
                                newCell.faces[0] = leaf;
                                if (leaf.cellB != null) {
                                    System.err.println("Leaf complete!");
                                }
                                leaf.cellBBackup = null;
                                leaf.cellB = newCell;

                                boolean undo = false;

                                for (int j = 1; j < newCell.faces.length; j++) {
                                    NFace newFace = new NFace(dims, lattice.internalDims);
                                    int index = 0;
                                    for (int k = newCell.points.length - 1; k >= 0; k--) {
                                        if (((newCell.points.length - 1) - k) == j) { //Uggh.  I think this should work.  Each face should skip a different point.
                                            continue;
                                        }
                                        newFace.points[index] = newCell.points[k];
                                        //newFace.points[index].faces.add(newFace);
                                        index++;
                                    }
                                    boolean matched = false;
                                    NFace match = null;
//                                for (NFace f : lattice.faces) {
                                    for (NFace f : newFace.points[0].faces) {
                                        if (f.equivalent(newFace)) {
                                            matched = true;
                                            match = f;
                                            break;
                                        }
                                    }
                                    if (matched) {
                                        if (match.cellB != null) {
                                            //System.err.println("Overwriting a cellB!");
//                                            highlighteds.clear();
//                                            for (NPoint fp : newCell.points) {
//                                                highlighteds.add(fp);
//                                            }
//                                            chosens.clear();
//                                            for (NPoint fp : leaf.points) {
//                                                chosens.add(fp);
//                                            }
//                                            //highlightedCells.clear();
//                                            //highlightedCells.add(newCell);
//                                            throw new RuntimeException("Full halt!");
                                            undo = true;
                                        }
                                        match.cellBBackup = match.cellB;
                                        match.cellB = newCell;
                                        newCell.faces[j] = match;
                                        lattice.incompleteFaces.remove(match); //TODO Maybe double-check?
                                    } else {
                                        for (NPoint p : newFace.points) {
                                            p.faces.add(newFace);
                                        }
                                        newFace.cellA = newCell;
                                        newCell.faces[j] = newFace;
                                        lattice.incompleteFaces.add(newFace);
                                        lattice.faces.add(newFace);
                                    }
                                }

                                if (!undo) {
                                    for (int j = 0; j < newCell.faces.length; j++) {
                                        if (newCell.faces[j].complete()) {
                                            if (newCell.faces[j].basis == null) {
                                                newCell.faces[j].calcBasis();
                                            }
                                            if (!newCell.faces[j].checkAngle(maxRefractionAngle, false)) {
                                                undo = true;
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (undo) {
                                    // Undo what was done.

                                    leaf.cellB = null;
                                    for (int j = 1; j < newCell.faces.length; j++) {
                                        if (newCell.faces[j].complete()) {
                                            newCell.faces[j].cellB = newCell.faces[j].cellBBackup;
                                            newCell.faces[j].cellBBackup = null;
                                            if (!newCell.faces[j].complete()) {
                                                lattice.incompleteFaces.add(newCell.faces[j]);
                                            }
                                        } else {
                                            for (NPoint p : newCell.faces[j].points) {
                                                p.faces.remove(newCell.faces[j]);
                                            }
                                            lattice.incompleteFaces.remove(newCell.faces[j]);
                                            lattice.faces.remove(newCell.faces[j]);
                                        }
                                    }
                                    anchors.remove(i);
                                    //getMisclassifiedFaceCount();
                                    //THINK I never figured out why the complete point count kept going down that time.
//                                    int newCompCount = refreshCompletePoints();
//                                    if (newCompCount < lastCompleteCount) {
//                                        System.err.println("Decreased count!");
//                                    }
                                    failCauseUndo++;
                                    continue;
                                }

                                lattice.cells.add(newCell);
                                lattice.incompleteFaces.remove(leaf);

                                faceBottom = faceNum;
                                //System.out.println("debugID " + i.debugID);
                                System.out.println("Found face at " + faceNum);
                                sticksChanged = true;

                                //THINK Maybe this should still be checked?  Isn't finding any, though, and REALLY slow.
//                                for (int j = 0; j < newCell.faces.length; j++) {
//                                    checkDuplicates(newCell.faces[j]);
//                                }

                                //TODO Check for pre-formed cells here

                                //getMisclassifiedFaceCount();
                                //THINK I never figured out why the complete point count kept going down that time.
//                                int newCompCount = refreshCompletePoints();
//                                if (newCompCount < lastCompleteCount) {
//                                    System.err.println("Decreased count!");
//                                }
                                leaf.runTag = runTag;
                                
                                // Update average radius
                                if (radiusLimitAverage) {
                                    double additionalRadius = 0;
                                    int additionalRadiusCount = 0;
                                    for (NPoint p : newCell.points) {
                                        if (p != i) {
                                            additionalRadius += p.dist(i);
                                            additionalRadiusCount++;
                                        }
                                    }
                                    //THINK This could cause inaccuracy.  Not sure how to circumvent that.
                                    radiusAverage = (((radiusAverage * radiusCount) + additionalRadius) / (radiusCount + additionalRadiusCount));
                                    radiusCount += additionalRadiusCount;
                                }
                                return true;
                            } else { //faceNum
                                failCauseWithin++;
                            }
                        } else {
                            if (!((maxThinness == -1) || checkThinness(anchors, maxThinness))) {
                                failCauseThinness++;
                            }
                            if (!((minAngle == -1) || checkMinAngle(anchors, minAngle))) {
                                failCauseAngle++;
                            }
                            if (!checkMinMaxVolume(anchors, minVolume, maxVolume)) {
                                failCauseVolume++;
                            }
                            if (!checkMinMaxLength(anchors, minLength, maxLength)) {
                                failCauseLength++;
                            }
                        }
                        anchors.remove(i);
                    }
                    //System.out.println("Passed face " + faceNum);
                    if (nearest.size() == 0) {
                        System.out.println("Passed face RANGE  " + faceNum);
                    } else {
                        if (failCauseAngle >= failCauseUndo) {
                            if (failCauseAngle >= failCauseLength) {
                                if (failCauseAngle >= failCauseThinness) {
                                    if (failCauseAngle >= failCauseVolume) {
                                        if (failCauseAngle >= failCauseWithin) {
                                            if (failCauseAngle >= 1) {
                                                System.out.println("Passed face ANGLE  " + faceNum);
                                            } else {
                                                System.out.println("Passed face NONE   " + faceNum);
                                            }
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    } else {
                                        if (failCauseVolume >= failCauseWithin) {
                                            System.out.println("Passed face VOLUME " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    }
                                } else {
                                    if (failCauseThinness >= failCauseVolume) {
                                        if (failCauseThinness >= failCauseWithin) {
                                            System.out.println("Passed face THINNS " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    } else {
                                        if (failCauseVolume >= failCauseWithin) {
                                            System.out.println("Passed face VOLUME " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    }
                                }
                            } else {
                                if (failCauseLength >= failCauseThinness) {
                                    if (failCauseLength >= failCauseVolume) {
                                        if (failCauseLength >= failCauseWithin) {
                                            System.out.println("Passed face LENGTH " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    } else {
                                        if (failCauseVolume >= failCauseWithin) {
                                            System.out.println("Passed face VOLUME " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    }
                                } else {
                                    if (failCauseThinness >= failCauseVolume) {
                                        if (failCauseThinness >= failCauseWithin) {
                                            System.out.println("Passed face THINNS  " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    } else {
                                        if (failCauseVolume >= failCauseWithin) {
                                            System.out.println("Passed face VOLUME " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    }
                                }
                            }
                        } else {
                            if (failCauseUndo >= failCauseLength) {
                                if (failCauseUndo >= failCauseThinness) {
                                    if (failCauseUndo >= failCauseVolume) {
                                        if (failCauseUndo >= failCauseWithin) {
                                            System.out.println("Passed face UNDO   " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    } else {
                                        if (failCauseVolume >= failCauseWithin) {
                                            System.out.println("Passed face VOLUME " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    }
                                } else {
                                    if (failCauseThinness >= failCauseVolume) {
                                        if (failCauseThinness >= failCauseWithin) {
                                            System.out.println("Passed face THINNS " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    } else {
                                        if (failCauseVolume >= failCauseWithin) {
                                            System.out.println("Passed face VOLUME " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    }
                                }
                            } else {
                                if (failCauseLength >= failCauseThinness) {
                                    if (failCauseLength >= failCauseVolume) {
                                        if (failCauseLength >= failCauseWithin) {
                                            System.out.println("Passed face LENGTH " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    } else {
                                        if (failCauseVolume >= failCauseWithin) {
                                            System.out.println("Passed face VOLUME " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    }
                                } else {
                                    if (failCauseThinness >= failCauseVolume) {
                                        if (failCauseThinness >= failCauseWithin) {
                                            System.out.println("Passed face THINNS  " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    } else {
                                        if (failCauseVolume >= failCauseWithin) {
                                            System.out.println("Passed face VOLUME " + faceNum);
                                        } else {
                                            System.out.println("Passed face WITHIN " + faceNum);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    leaf.runTag = runTag;
                }
            }
        }
        faceBottom = 0;
        return false;
    }

    public static double calcThinness1(ArrayList<NPoint> frame) {
        double min = -1;
        double max = -1;
        for (int i = 0; i < frame.size() - 1; i++) {
            for (int j = i + 1; j < frame.size(); j++) {
                double bucket = frame.get(i).dist(frame.get(j));
//                System.out.println(i + "-" + j + ": " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            }
        }
        return max / min;
    }

    public static double calcThinness2(ArrayList<NPoint> frame) {
        double min = -1;
        double max = -1;
        for (int i = 0; i < frame.size() - 1; i++) {
            for (int j = i + 1; j < frame.size(); j++) {
                double bucket = frame.get(i).dist(frame.get(j));
//                System.out.println(i + "-" + j + ": " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            }
        }

        NBasis[] basises = new NBasis[frame.size()];
        {
            basises[0] = new NBasis(frame.get(0).dims, frame.size() - 2);
            int index = 0;
            for (int j = 2; j < frame.size(); j++) {
                basises[0].bases[index++] = frame.get(j).pos.minusB(frame.get(1).pos);
            }
            basises[0].orthogonalize();
            try {
                basises[0].calcProjection();// System.out.println(basises[i - 1].projection);
                double bucket = Matrix.lrvMult(basises[0].projection, frame.get(0).pos.minusB(frame.get(1).pos)).dist(frame.get(0).pos.minusB(frame.get(1).pos));
//                System.out.println("0: " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            } catch (Exception ex) {
                //Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                return Double.NaN;
            }
        }
        for (int i = 1; i < basises.length; i++) {
            basises[i] = new NBasis(frame.get(0).dims, frame.size() - 2);
            int index = 0;
            for (int j = 1; j < frame.size(); j++) {
                if (i != j) {
                    basises[i].bases[index++] = frame.get(j).pos.minusB(frame.get(0).pos);
                }
            }
            basises[i].orthogonalize();
            try {
                basises[i].calcProjection();// System.out.println(basises[i - 1].projection);
                double bucket = Matrix.lrvMult(basises[i].projection, frame.get(i).pos.minusB(frame.get(0).pos)).dist(frame.get(i).pos.minusB(frame.get(0).pos));
//                System.out.println(i + ": " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            } catch (Exception ex) {
                //Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                return Double.NaN;
            }
        }

        return max / min;
    }

    public static double calcThinness1(NPoint[] frame) {
        double min = -1;
        double max = -1;
        for (int i = 0; i < frame.length - 1; i++) {
            for (int j = i + 1; j < frame.length; j++) {
                double bucket = frame[i].dist(frame[j]);
//                System.out.println(i + "-" + j + ": " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            }
        }
        return max / min;
    }

    public static double calcThinness2(NPoint[] frame) {
        double min = -1;
        double max = -1;
        for (int i = 0; i < frame.length - 1; i++) {
            for (int j = i + 1; j < frame.length; j++) {
                double bucket = frame[i].dist(frame[j]);
//                System.out.println(i + "-" + j + ": " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            }
        }

        NBasis[] basises = new NBasis[frame.length];
        {
            basises[0] = new NBasis(frame[0].dims, frame.length - 2);
            int index = 0;
            for (int j = 2; j < frame.length; j++) {
                basises[0].bases[index++] = frame[j].pos.minusB(frame[1].pos);
            }
            basises[0].orthogonalize();
            try {
                basises[0].calcProjection();// System.out.println(basises[i - 1].projection);
                double bucket = Matrix.lrvMult(basises[0].projection, frame[0].pos.minusB(frame[1].pos)).dist(frame[0].pos.minusB(frame[1].pos));
//                System.out.println("0: " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            } catch (Exception ex) {
                //Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                return Double.NaN;
            }
        }
        for (int i = 1; i < basises.length; i++) {
            basises[i] = new NBasis(frame[0].dims, frame.length - 2);
            int index = 0;
            for (int j = 1; j < frame.length; j++) {
                if (i != j) {
                    basises[i].bases[index++] = frame[j].pos.minusB(frame[0].pos);
                }
            }
            basises[i].orthogonalize();
            try {
                basises[i].calcProjection();// System.out.println(basises[i - 1].projection);
                double bucket = Matrix.lrvMult(basises[i].projection, frame[i].pos.minusB(frame[0].pos)).dist(frame[i].pos.minusB(frame[0].pos));
//                System.out.println(i + ": " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            } catch (Exception ex) {
                //Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                return Double.NaN;
            }
        }

//        ArrayList<NPoint> scumbucket = new ArrayList<NPoint>();
//        for (int i = 0; i < frame.length; i++) {
//            scumbucket.add(frame[i]);
//        }
//        checkThinness(scumbucket, 5);

        return max / min;
    }

    public static double calcMinAngle(NPoint[] frame) {
        double min = -1;
        //double max = -1;
        for (int i = 0; i < frame.length; i++) {
            for (int j = 0; j < frame.length; j++) {
                for (int k = 0; k < frame.length; k++) {
                    if (i != j && i != k && j != k) {
                        double bucket = NVector.angle(frame[j].pos.minusB(frame[i].pos), frame[k].pos.minusB(frame[i].pos));
                        if (min == -1 || bucket < min) {
                            min = bucket;
                        }
                    }
                }
            }
        }
        return min;
    }

    public static boolean checkMinAngle(ArrayList<NPoint> frame, double thinness) {
        double min = -1;
        //double max = -1;
        for (int i = 0; i < frame.size(); i++) {
            for (int j = 0; j < frame.size(); j++) {
                for (int k = 0; k < frame.size(); k++) {
                    if (i != j && i != k && j != k) {
                        double bucket = NVector.angle(frame.get(j).pos.minusB(frame.get(i).pos), frame.get(k).pos.minusB(frame.get(i).pos));
                        if (min == -1 || bucket < min) {
                            min = bucket;
                        }
                    }
                }
            }
        }
        if ((min < thinness) || Double.isInfinite(min) || Double.isNaN(min)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean checkMinMaxVolume(ArrayList<NPoint> frame, double minVolume, double maxVolume) {
        if (minVolume == -1 && maxVolume == -1) {
            return true;
        }
        if (frame.size() <= 0) {
            return false; // Eh, maybe I should throw an error or something.
        }
        int dims = frame.get(0).dims;
        int simplexDims = frame.size() - 1;
        NBasis volBasis = new NBasis(dims, simplexDims);
        ArrayList<NPoint> ptList = new ArrayList<NPoint>();
        volBasis.bases[0] = NVector.zero(dims);
        for (int i = 0; i < volBasis.bases.length; i++) {
            volBasis.bases[i] = frame.get(i + 1).pos.minusB(frame.get(0).pos);
        }
        volBasis.orthogonalize();
        try {
            volBasis.calcProjection();
        } catch (Exception ex) {
            Logger.getLogger(NCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        NVector[] ptVectors = new NVector[frame.size()];
        for (int i = 0; i < frame.size(); i++) {
            ptVectors[i] = frame.get(i).pos;
        }
        NVector[] result = Matrix.ipTransformCoords(volBasis.bases, ptVectors);
        //TODO Could maybe be cached
        Matrix m = new Matrix(simplexDims, simplexDims);
        for (int i = 1; i < result.length; i++) {
            NVector diff = result[i].minusB(result[0]);
            for (int j = 0; j < result[i].dims; j++) {
                m.val[i - 1][j] = diff.coords[j];
            }
        }
        try {
            double volume = ((1.0 / MeMath.factorial(simplexDims)) * Math.abs(m.det()));
            //System.out.println("Volume: " + volume);
            if (minVolume != -1) {
                if ((volume < minVolume) || Double.isInfinite(volume) || Double.isNaN(volume)) {
                    return false;
                } else {
                    if (maxVolume != -1) {
                        if (volume > maxVolume) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            } else {
                if (volume > maxVolume) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(NCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static boolean checkThinness(ArrayList<NPoint> frame, double thinness) {
        // This could actually be done in series with the second part.
        double min = -1;//calcThinness1(frame);
        double max = -1;//calcThinness2(frame);
        for (int i = 0; i < frame.size() - 1; i++) {
            for (int j = i + 1; j < frame.size(); j++) {
                double bucket = frame.get(i).dist(frame.get(j));
//                System.out.println(i + "-" + j + ": " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            }
        }
        double result = max / min;
//        System.out.println("max " + max + "; min " + min + "; result " + result);
        if ((result > thinness) || Double.isInfinite(result) || Double.isNaN(result)) {
            return false;
        }

//        min = -1;
//        max = -1;
        NBasis[] basises = new NBasis[frame.size()];
        {
            basises[0] = new NBasis(frame.get(0).dims, frame.size() - 2);
            int index = 0;
            for (int j = 2; j < frame.size(); j++) {
                basises[0].bases[index++] = frame.get(j).pos.minusB(frame.get(1).pos);
            }
            basises[0].orthogonalizeWCache();
            try {
                basises[0].calcProjectionWCache();// System.out.println(basises[i - 1].projection);//MTXOFT
                double bucket = Matrix.lrvMult(basises[0].projection, frame.get(0).pos.minusB(frame.get(1).pos)).dist(frame.get(0).pos.minusB(frame.get(1).pos));
//                System.out.println("0: " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            } catch (Exception ex) {
                basises[0].basis.doneWithMatrix();
                basises[0].projection.doneWithMatrix();
                //Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            basises[0].basis.doneWithMatrix();
            basises[0].projection.doneWithMatrix();
        }
        for (int i = 1; i < basises.length; i++) {
            basises[i] = new NBasis(frame.get(0).dims, frame.size() - 2);
            int index = 0;
            for (int j = 1; j < frame.size(); j++) {
                if (i != j) {
                    basises[i].bases[index++] = frame.get(j).pos.minusB(frame.get(0).pos);
                }
            }
            basises[i].orthogonalizeWCache();//MTXOFT*
            try {
                basises[i].calcProjectionWCache();// System.out.println(basises[i - 1].projection);
                double bucket = Matrix.lrvMult(basises[i].projection, frame.get(i).pos.minusB(frame.get(0).pos)).dist(frame.get(i).pos.minusB(frame.get(0).pos));                
//                System.out.println(i + ": " + bucket);
                if (min == -1) {
                    min = bucket;
                    max = min;
                } else if (bucket < min) {
                    min = bucket;
                } else if (bucket > max) {
                    max = bucket;
                }
            } catch (Exception ex) {
                basises[i].basis.doneWithMatrix();
                basises[i].projection.doneWithMatrix();
                //Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            basises[i].basis.doneWithMatrix();
            basises[i].projection.doneWithMatrix();
        }

        result = max / min;
//        System.out.println("max " + max + "; min " + min + "; result " + result);
        if ((result > thinness) || Double.isInfinite(result) || Double.isNaN(result)) {
            return false;
        }
        return true;
    }

    public void bind() {
        switch (mode) {
            case 3: // 3-3 Bind to a -1,1 box
            case 0: // 2-2 Bind to a -1,1 box
                for (NPoint i : lattice.points) {
                    for (int j = 0; j < dims; j++) {
                        if (i.pos.coords[j] > 1) {
                            i.pos.coords[j] = 1;
                        } else if (i.pos.coords[j] < -1) {
                            i.pos.coords[j] = -1;
                        }
                    }
                }
                break;
            case 1: // 3-2 Bind to a radius 1 sphere: normalize vectors
                bindSphere();
                break;
            case 2: // 3-2 Bind to a width 2 box
                for (NPoint i : lattice.points) {
                    double length = 0;
                    for (int j = 0; j < dims; j++) {
                        length += sqr(i.pos.coords[j]);
                    }
                    length = Math.sqrt(length);
                    if (length == 0) {
                        for (int j = 0; j < dims; j++) {
                            i.pos.coords[j] = (r.nextDouble() * 2) - 1;
                        }
                        for (int j = 0; j < dims; j++) {
                            length += sqr(i.pos.coords[j]);
                        }
                        length = Math.sqrt(length);
                        // It had better not be 0 now.
                    }
                    for (int j = 0; j < dims; j++) {
                        i.pos.coords[j] /= length;
                        i.pos.coords[j] *= 2;
                    }

                    if (i.pos.coords[0] > 1) {
                        i.pos.coords[0] = 1;
                    } else if (i.pos.coords[0] < -1) {
                        i.pos.coords[0] = -1;
                    }
                    if (i.pos.coords[1] > 1) {
                        i.pos.coords[1] = 1;
                    } else if (i.pos.coords[1] < -1) {
                        i.pos.coords[1] = -1;
                    }
                    if (i.pos.coords[2] > 1) {
                        i.pos.coords[2] = 1;
                    } else if (i.pos.coords[2] < -1) {
                        i.pos.coords[2] = -1;
                    }
                }
                break;
            default:
                break;
        }
    }

    public void bindSphere() {
        for (NPoint i : lattice.points) {
            double length = 0;
            for (int j = 0; j < dims; j++) {
                length += sqr(i.pos.coords[j]);
            }
            length = Math.sqrt(length);
            if (length == 0) {
                for (int j = 0; j < dims; j++) {
                    i.pos.coords[j] = (r.nextDouble() * 2) - 1;
                }
                for (int j = 0; j < dims; j++) {
                    length += sqr(i.pos.coords[j]);
                }
                length = Math.sqrt(length);
                // It had better not be 0 now.
            }
            for (int j = 0; j < dims; j++) {
                i.pos.coords[j] /= length;
            }
        }
    }
    public boolean triangulateNCube = false;

    public void placeNCube() {
        NPoint[] corners = new NPoint[1 << dims];
        placeNCubeRecurse(new NVector(dims), 0, corners);
        if (triangulateNCube) {
            if (lattice.internalDims == dims) {
                // Going to try to add corner simplices as cells.
                int[] cornersTally = new int[1 << dims];
                for (int i = 0; i < cornersTally.length; i++) {
                    cornersTally[i] = 0;
                }
                boolean foundEmpty = true;
                while (foundEmpty) {
                    foundEmpty = false;
                    for (int i = 0; i < cornersTally.length; i++) {
                        if (cornersTally[i] == 0) {// System.out.println(cornersTally);
                            // Add corner cell
                            foundEmpty = true;
                            cornersTally[i] = -1;
                            NCell cell = new NCell(dims, lattice.internalDims);
                            cell.points[0] = corners[i];
                            corners[i].immune = true;
                            int index = 1;
                            for (int bit = 0; bit < dims; bit++) {
                                int bucket = i ^ (1 << bit);
                                cell.points[index++] = corners[bucket];
                                cornersTally[bucket]++;
                            }

                            // Add faces to cell
                            for (int j = 0; j < cell.points.length; j++) {
                                // Form the face made by omitting the current point
                                NFace face = new NFace(dims, lattice.internalDims);
                                index = 0;
                                for (int k = 0; k < cell.points.length; k++) {
                                    if (j != k) {
                                        face.points[index++] = cell.points[k];
                                        cell.points[k].faces.add(face);
                                    }
                                }
                                face.cellA = cell;
                                cell.faces[j] = face;
                                // Let all outer faces be complete.
                                if (j == 0) {
                                    lattice.incompleteFaces.add(face);
                                }
                                lattice.faces.add(face);
                            }
                            lattice.cells.add(cell);
                        }
                    }
                }
            }
        }
    }

    private void placeNCubeRecurse(NVector point, int i, NPoint[] corners) {
        if (i < dims) {
            point.coords[i] = 1;
//            point.coords[i] = i + 1;
            placeNCubeRecurse(point, i + 1, corners);
            point.coords[i] = -1;
//            point.coords[i] = -1 * (i + 1);
            placeNCubeRecurse(point, i + 1, corners);
        } else {
            NPoint p = new NPoint(dims);
            p.pos = point.copy();
            lattice.addPoint(p);
            int index = 0;
            for (int j = 0; j < dims; j++) {
                index <<= 1;
                if (point.coords[j] == 1) {
                    index += 1;
                }
            }
            corners[index] = p;
        }
    }

    public void calcProperties() {
        if (lattice != null) {
        }
    }

    public void placeCamera() {
        placeCamera(true);
    }

    public void placeCamera(boolean orient) {
        if (lattice != null) {
            if (orient) {
                Camera newCam = new Camera(dims, lattice.internalDims, lattice);
                NCell cell = lattice.cells.get(r.nextInt(lattice.cells.size()));
                NVector center = new NVector(dims);
                for (NPoint i : cell.points) {
                    center = center.plusB(i.pos);
                }
                center = center.multS(1.0 / cell.points.length);
                newCam.pos = center;
                try {
                    newCam.realignOrientation(cell);
                } catch (Exception ex) {
                    Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                }
                newCam.camForm = new CameraForm(this, newCam, parent);
                lattice.cameras.add(newCam);
                newCam.camForm.show();
            } else {
                Camera newCam = new Camera(dims, lattice.internalDims, lattice);
                NCell cell = lattice.cells.get(r.nextInt(lattice.cells.size()));
                newCam.cell = cell;
                NVector center = new NVector(dims);
                for (NPoint i : cell.points) {
                    center = center.plusB(i.pos);
                }
                center = center.multS(1.0 / cell.points.length);
                //newCam.pos = center;
                permacenter.pos = center;
                permaradius = cell.points[0].pos.minusB(center).length();
//                try {
//                    newCam.realignOrientation(cell);
//                } catch (Exception ex) {
//                    Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
//                }
                newCam.camForm = new CameraForm(this, newCam, parent);
                newCam.camForm.render = false;
                newCam.camForm.boxRenderMain.setSelected(true);
                lattice.cameras.add(newCam);
                newCam.camForm.show();
            }
            parent.dp.repaint();
        }
    }
    
    public void placeFunctionTest() throws Exception {
        if (lattice != null) {
            if (dims != 4 || lattice.internalDims != 3) {
                throw new Exception("Required: Dims = 4, LDims = 3");
            }
            double[] lower = new double[] {-4, -4, -4};
            double[] upper = new double[] {4, 4, 4};
            double[] step  = new double[] {0.5, 0.5, 0.5};
            for (double u = lower[0]; u <= upper[0]; u += step[0]) {
                for (double v = lower[1]; v <= upper[1]; v += step[1]) {
                    for (double w = lower[2]; w <= upper[2]; w += step[2]) {
                        double a = u + 0.5*v + 0.5*w;
                        double b = v + 0.5*w;
                        double c = w;
                        double d = 1.0 / (Math.pow(a, 4) + Math.pow(b, 4) + Math.pow(c, 4) + 1);
                        NPoint bucket = new NPoint(new double[]{a, b, c, d});
                        lattice.addPoint(bucket);
                    }
                }
            }
        }
    }
    public double donutCircumfrence = 30;

    public void placeNDonut() throws Exception {
        if (lattice != null) {
            if ((dims / 2) >= lattice.internalDims) {
                NVector cursor = new NVector(lattice.internalDims);
                for (int i = 0; i < cursor.dims; i++) {
                    cursor.coords[i] = 0;
                }
                NVector shiftCursor = new NVector(lattice.internalDims);
                for (int i = 0; i < shiftCursor.dims; i++) {
                    shiftCursor.coords[i] = 0;
                }
                NVector intCursor = new NVector(lattice.internalDims);
                for (int i = 0; i < intCursor.dims; i++) {
                    intCursor.coords[i] = 0;
                }
//                double inc = Math.PI / 20;
//                int circ = (int) (Math.PI * 2 / inc);
                int circ = (int) donutCircumfrence;
                double inc = (Math.PI * 2) / donutCircumfrence;
                NPoint[] donutMesh = new NPoint[(int) Math.pow(circ, intCursor.dims)];
                placeNDonutRecurse(cursor, shiftCursor, 0, inc, intCursor, donutMesh, circ);
                // Going to try to triangulate this sucker right here.  Won't be deLaunay, but that's ok.

            } else {
                // Well, can't really make one of those non-curved donuts, so...go fish.
                throw new Exception("Total dims must be at least 2x lattice dims.");
            }
        }
    }
    
    public double randomRange = -1;

    public void placeNDonutRecurse(NVector cursor, NVector shiftCursor, int dim, double inc, NVector intCursor, NPoint[] donutMesh, int circ) {
        if (dim < cursor.dims) {
            double d = 0;
            //for (double d = 0; d < Math.PI * 2 * 0.25; d += inc) {
            for (int i = 0; i < circ; i++) {
                if (dim < cursor.dims - 1) {
                    for (int j = dim + 1; j < cursor.dims; j++) {
//                        if (shiftCursor.coords[j] > 0) {
//                            shiftCursor.coords[j] = 0;
//                        } else {
                        shiftCursor.coords[j] += 0.5 * inc;
//                        }
                    }
                }
                if (randomRange > 0) {
                    cursor.coords[dim] = d + shiftCursor.coords[dim] + (((r.nextDouble() * 2) - 1) * randomRange);
                } else {
                    cursor.coords[dim] = d + shiftCursor.coords[dim];
                }
                intCursor.coords[dim] = i;
                placeNDonutRecurse(cursor, shiftCursor, dim + 1, inc, intCursor, donutMesh, circ);
                d += inc;
            }
        } else {
            NPoint bucket = new NPoint(dims);
            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[i * 2] = Math.sin(cursor.coords[i] + (inc * 0.25 * (r.nextDouble() - 0.5)));
                bucket.pos.coords[i * 2] = Math.sin(cursor.coords[i]);
                //bucket.pos.coords[i * 2] = cursor.coords[i];
            }
            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[(i * 2) + 1] = Math.cos(cursor.coords[i] + (inc * 0.25 * (r.nextDouble() - 0.5)));
                bucket.pos.coords[(i * 2) + 1] = Math.cos(cursor.coords[i]);
                //bucket.pos.coords[(i * 2) + 1] = 0;
            }
            lattice.addPoint(bucket);
            donutMesh[calcLinearNArrayIndex(intCursor, circ)] = bucket;
        }
    }

    public void placeNSaltLattice() throws Exception {
        if (lattice != null) {
            if ((dims / 2) >= lattice.internalDims) {
                NVector cursor = new NVector(lattice.internalDims);
                for (int i = 0; i < cursor.dims; i++) {
                    cursor.coords[i] = 0;
                }
                NVector shiftCursor = new NVector(lattice.internalDims);
                for (int i = 0; i < shiftCursor.dims; i++) {
                    shiftCursor.coords[i] = 0;
                }
                NVector intCursor = new NVector(lattice.internalDims);
                for (int i = 0; i < intCursor.dims; i++) {
                    intCursor.coords[i] = 0;
                }
//                double inc = Math.PI / 20;
//                int circ = (int) (Math.PI * 2 / inc);
                int circ = (int) donutCircumfrence;
                double inc = (Math.PI * 2) / donutCircumfrence;
                NPoint[] donutMesh = new NPoint[(int) Math.pow(circ, intCursor.dims)];
                placeNSaltLatticeRecurse(cursor, shiftCursor, 0, inc, intCursor, donutMesh, circ);
                // Going to try to triangulate this sucker right here.  Won't be deLaunay, but that's ok.

            } else {
                // Well, can't really make one of those non-curved donuts, so...go fish.
                throw new Exception("Total dims must be at least 2x lattice dims.");
            }
        }
    }

    public void placeNSaltLatticeRecurse(NVector cursor, NVector shiftCursor, int dim, double inc, NVector intCursor, NPoint[] donutMesh, int circ) {
        if (dim < cursor.dims) {
            int i = 0;
            for (double d = 0; d < Math.PI * 0.4; d += inc) {
                if (dim < cursor.dims - 1) {
                    //TODO Fix this; add to all previous (upcoming?) coords
//                    if (shiftCursor.coords[dim + 1] > 0) {
//                        shiftCursor.coords[dim + 1] = 0;
//                    } else {
//                        shiftCursor.coords[dim + 1] += 0.5 * inc;
//                    }
                    for (int j = dim + 1; j < cursor.dims; j++) {
                        if (shiftCursor.coords[j] > 0) {
                            shiftCursor.coords[j] = 0;
                        } else {
                            shiftCursor.coords[j] += 0.5 * inc;
                        }
                    }
                }
                cursor.coords[dim] = d + shiftCursor.coords[dim];
                intCursor.coords[dim] = i++;
                placeNSaltLatticeRecurse(cursor, shiftCursor, dim + 1, inc, intCursor, donutMesh, circ);
            }
        } else {
            NPoint bucket = new NPoint(dims);
            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[i * 2] = Math.sin(cursor.coords[i] + (inc * 0.25 * (r.nextDouble() - 0.5)));
                //bucket.pos.coords[i * 2] = Math.sin(cursor.coords[i]);
                bucket.pos.coords[i * 2] = cursor.coords[i];
            }
            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[(i * 2) + 1] = Math.cos(cursor.coords[i] + (inc * 0.25 * (r.nextDouble() - 0.5)));
                //bucket.pos.coords[(i * 2) + 1] = Math.cos(cursor.coords[i]);
                bucket.pos.coords[(i * 2) + 1] = 0;
            }
            lattice.addPoint(bucket);
            donutMesh[calcLinearNArrayIndex(intCursor, circ)] = bucket;
        }
    }
    
    /**
     * Rather than search lattice.points, each point is placed and assigned candidate
     * points by calculation - the list of the only points to which it can be connected.
     */
    public boolean candidateSystem = false;

    public void placeNMobiusDonut() throws Exception {
        if (lattice != null) {
            if ((dims == 7) && (lattice.internalDims == 3)) {
                NVector cursor = new NVector(lattice.internalDims);
                for (int i = 0; i < cursor.dims; i++) {
                    cursor.coords[i] = 0;
                }
                NVector shiftCursor = new NVector(lattice.internalDims);
                for (int i = 0; i < shiftCursor.dims; i++) {
                    shiftCursor.coords[i] = 0;
                }
                NVector intCursor = new NVector(lattice.internalDims);
                for (int i = 0; i < intCursor.dims; i++) {
                    intCursor.coords[i] = 0;
                }
//                double inc = Math.PI / 20;
//                int circ = (int) (Math.PI * 2 / inc);
                int circ = (int) donutCircumfrence;
                double inc = (Math.PI * 2) / donutCircumfrence; //THINK Ummm, maybe I should use circ
                NPoint[] donutMesh = new NPoint[(int) Math.pow(circ + 2, intCursor.dims)];
                placeNSomething(cursor, shiftCursor, 0, inc, intCursor, donutMesh, circ);
                
                {
                    int ui = -1;
                    int vi = 0;
                    int wi = 0;
                    for (vi = -1; vi < circ + 1; vi++) {
                        for (wi = -1; wi < circ + 1; wi++) {
                            NPoint overlapPoint = donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)];
                            NPoint truePoint = null;
                            for (NPoint p : lattice.points) {
                                if (p.pos.approximatelyEquivalent(overlapPoint.pos, 30)) {
                                    truePoint = p;
                                    break;
                                }
                            }
                            if (truePoint == null) {
                                System.err.println("Overlap not found for (" + ui + ", " + vi + ", " + wi + ")");
                            }
                            donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)] = truePoint;
                        }
                    }
                    vi = -1;
                    for (ui = -1; ui < circ + 1; ui++) {
                        for (wi = -1; wi < circ + 1; wi++) {
                            NPoint overlapPoint = donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)];
                            NPoint truePoint = null;
                            for (NPoint p : lattice.points) {
                                if (p.pos.approximatelyEquivalent(overlapPoint.pos, 30)) {
                                    truePoint = p;
                                    break;
                                }
                            }
                            if (truePoint == null) {
                                System.err.println("Overlap not found for (" + ui + ", " + vi + ", " + wi + ")");
                            }
                            donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)] = truePoint;
                        }
                    }
                    wi = -1;
                    for (ui = -1; ui < circ + 1; ui++) {
                        for (vi = -1; vi < circ + 1; vi++) {
                            NPoint overlapPoint = donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)];
                            NPoint truePoint = null;
                            for (NPoint p : lattice.points) {
                                if (p.pos.approximatelyEquivalent(overlapPoint.pos, 30)) {
                                    truePoint = p;
                                    break;
                                }
                            }
                            if (truePoint == null) {
                                System.err.println("Overlap not found for (" + ui + ", " + vi + ", " + wi + ")");
                            }
                            donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)] = truePoint;
                        }
                    }
                    ui = circ;
                    for (vi = -1; vi < circ + 1; vi++) {
                        for (wi = -1; wi < circ + 1; wi++) {
                            NPoint overlapPoint = donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)];
                            NPoint truePoint = null;
                            for (NPoint p : lattice.points) {
                                if (p.pos.approximatelyEquivalent(overlapPoint.pos, 30)) {
                                    truePoint = p;
                                    break;
                                }
                            }
                            if (truePoint == null) {
                                System.err.println("Overlap not found for (" + ui + ", " + vi + ", " + wi + ")");
                            }
                            donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)] = truePoint;
                        }
                    }
                    vi = circ;
                    for (ui = -1; ui < circ + 1; ui++) {
                        for (wi = -1; wi < circ + 1; wi++) {
                            NPoint overlapPoint = donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)];
                            NPoint truePoint = null;
                            for (NPoint p : lattice.points) {
                                if (p.pos.approximatelyEquivalent(overlapPoint.pos, 30)) {
                                    truePoint = p;
                                    break;
                                }
                            }
                            if (truePoint == null) {
                                System.err.println("Overlap not found for (" + ui + ", " + vi + ", " + wi + ")");
                            }
                            donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)] = truePoint;
                        }
                    }
                    wi = circ;
                    for (ui = -1; ui < circ + 1; ui++) {
                        for (vi = -1; vi < circ + 1; vi++) {
                            NPoint overlapPoint = donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)];
                            NPoint truePoint = null;
                            for (NPoint p : lattice.points) {
                                if (p.pos.approximatelyEquivalent(overlapPoint.pos, 30)) {
                                    truePoint = p;
                                    break;
                                }
                            }
                            if (truePoint == null) {
                                System.err.println("Overlap not found for (" + ui + ", " + vi + ", " + wi + ")");
                            }
                            donutMesh[calcLinearNArrayIndex(new NVector(new double[]{ui + 1, vi + 1, wi + 1}), circ + 2)] = truePoint;
                        }
                    }
                }
                
                for (int ui = 0; ui < circ; ui++) {
                    for (int vi = 0; vi < circ; vi++) {
                        for (int wi = 0; wi < circ; wi++) {
                            // Huh, why did I decide to index the things with NVectors?
                            NPoint p = donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui + 1, vi + 1, wi + 1}), circ + 2)];
                            p.candidates = new ArrayList<NPoint>();
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+0, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+0, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+0, wi+2}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+1, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+1, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+1, wi+2}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+2, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+2, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+0, vi+2, wi+2}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+0, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+0, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+0, wi+2}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+1, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+1, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+1, wi+2}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+2, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+2, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+1, vi+2, wi+2}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+0, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+0, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+0, wi+2}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+1, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+1, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+1, wi+2}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+2, wi+0}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+2, wi+1}), circ + 2)]);
                            p.candidates.add(donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui+2, vi+2, wi+2}), circ + 2)]);
                        }
                    }
                }
                candidateSystem = true;
                //placeNMobiusDonutRecurse(cursor, shiftCursor, 0, inc, intCursor, donutMesh, circ);

                // Going to try to triangulate this sucker right here.  Won't be deLaunay, but that's ok.

            } else {
                // Well, can't really make one of those non-curved klein bottles, so...go fish.
                //throw new Exception("Total dims must be at least 3x lattice dims.");
                throw new Exception("Required: Dims = 7, LDims = 3");
            }
        }
    }

    public void placeNMobiusDonutRecurse(NVector cursor, NVector shiftCursor, int dim, double inc, NVector intCursor, NPoint[] donutMesh, int circ) {
        if (dim < cursor.dims) {
            double d = 0;
            //for (double d = 0; d < Math.PI * 2 * 0.25; d += inc) {
            //for (int i = 0; i < (dim == 0 ? circ / 2 : circ); i++) {
            for (int i = 0; i < circ + tweakValue; i++) {
                if (dim < cursor.dims - 1) {
                    for (int j = dim + 1; j < cursor.dims; j++) {
                        if (shiftCursor.coords[j] > 0) {
                            shiftCursor.coords[j] = 0;
                        } else {
                            shiftCursor.coords[j] += 0.5 * inc;
                        }
                    }
                }
                if (randomRange > 0) {
                    cursor.coords[dim] = d + shiftCursor.coords[dim] + (((r.nextDouble() * 2) - 1) * randomRange);
                } else {
                    cursor.coords[dim] = d + shiftCursor.coords[dim];
                }
                intCursor.coords[dim] = i;
                placeNMobiusDonutRecurse(cursor, shiftCursor, dim + 1, inc, intCursor, donutMesh, circ);
                d += inc;
            }
        } else {
            NPoint bucket = new NPoint(dims);
            if (cursor.dims == 2) {
                bucket.pos.coords[0] = Math.sin(cursor.coords[0]);
                bucket.pos.coords[1] = Math.cos(cursor.coords[0]) * Math.cos(cursor.coords[1] * 0.5);
                bucket.pos.coords[2] = Math.cos(cursor.coords[0]) * Math.sin(cursor.coords[1] * 0.5);
                bucket.pos.coords[3] = Math.sin(cursor.coords[1]);
                bucket.pos.coords[4] = Math.cos(cursor.coords[1]) * Math.cos(cursor.coords[0] * 0.5);
                bucket.pos.coords[5] = Math.cos(cursor.coords[1]) * Math.sin(cursor.coords[0] * 0.5);
                for (NPoint p : lattice.points) {
                    if (p.pos.exactlyEquivalent(bucket.pos)) {
                        System.err.println("Equal at " + intCursor.toString());
                    }//System.out.println(bucket.pos);System.out.println(p.pos);
                    if (p.pos.approximatelyEquivalent(bucket.pos, 15)) {
                        System.err.println("About equal at " + intCursor.toString());
                        chosens.add(bucket);
                    }
                }
            } else {
                for (int i = 0; i < cursor.dims; i++) {
                    bucket.pos.coords[(i * 3) + 0] = Math.sin(cursor.coords[i]);
                    bucket.pos.coords[(i * 3) + 1] = Math.cos(cursor.coords[i]) * Math.cos(cursor.coords[(i + 1) % cursor.dims] * 0.5);
                    bucket.pos.coords[(i * 3) + 2] = Math.cos(cursor.coords[i]) * Math.sin(cursor.coords[(i + 1) % cursor.dims] * 0.5);
                }
            }
//            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[i * 3] = Math.sin(cursor.coords[i]);
//            }
//            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[(i * 3) + 1] = Math.cos(cursor.coords[i]);
//            }
//            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[(i * 3) + 2] = Math.cos(cursor.coords[i]);
//            }
            lattice.addPoint(bucket);
            donutMesh[calcLinearNArrayIndex(intCursor, circ)] = bucket;
        }
    }

    public double sin(double x) {
        return Math.sin(x);
    }

    public double cos(double x) {
        return Math.cos(x);
    }
    public int tweakValue = 0;

    public void placeNSomething(NVector cursor, NVector shiftCursor, int dim, double inc, NVector intCursor, NPoint[] donutMesh, int circ) {
        randomRange = 1.5 / circ;
        //randomRange = 0;
        double u = -inc;
        for (int ui = -1; ui < circ + 1; ui++) {
            double v = -inc;
            for (int vi = -1; vi < circ + 1; vi++) {
                double w = -inc;
                for (int wi = -1; wi < circ + 1; wi++) {
                    NPoint bucket = new NPoint(dims);
                    double ur = (((r.nextDouble() * 2) - 1) * randomRange);
                    double vr = (((r.nextDouble() * 2) - 1) * randomRange);
                    double wr = (((r.nextDouble() * 2) - 1) * randomRange);
                    ur = 0;
                    vr = 0;
                    wr = 0;
                    /*
                     * 0 x   = Sin(u) Cos(v/2)
                     * 1 x'  = Cos(u)
                     * 2 x'' = Sin(u) Sin(v/2)
                     * 3 y   = Sin(v)
                     * 4 y'  = Cos(v)
                     * 5 y'' = 
                     */
                
//                    bucket.pos.coords[0] = sin(u + ur) * cos((v + vr) / 2);
//                    bucket.pos.coords[1] = cos(u + ur);
//                    bucket.pos.coords[2] = sin(u + ur) * sin((v + vr) / 2);
//                    bucket.pos.coords[3] = sin(v + vr);
//                    bucket.pos.coords[4] = cos(v + vr);
//                    //bucket.pos.coords[5] = Math.cos(cursor.coords[1]) * Math.sin(cursor.coords[0] * 0.5);

                    /*
                     * 0 x   = Sin(u) Cos(v/2)
                     * 1 x'  = Cos(u)
                     * 2 x'' = Sin(u) Sin(v/2)
                     * 3 y   = Sin(v) Cos(u/2)
                     * 4 y'  = Cos(v)
                     * 5 y'' = Sin(v) Sin(u/2)
                     */
                
//                    bucket.pos.coords[0] = sin(u + ur) * cos((v + vr) / 2);
//                    bucket.pos.coords[1] = cos(u + ur);
//                    bucket.pos.coords[2] = sin(u + ur) * sin((v + vr) / 2);
//                    bucket.pos.coords[3] = sin(v + vr) * cos((u + ur) / 2);
//                    bucket.pos.coords[4] = cos(v + vr);
//                    bucket.pos.coords[5] = sin(v + vr) * sin((u + ur) / 2);

//                    bucket.pos.coords[0] = sin(u + ur);
//                    bucket.pos.coords[1] = cos(u + ur);
//                    bucket.pos.coords[2] = 0;
//                    bucket.pos.coords[3] = sin(v + vr) * cos((u + ur) / 2);
//                    bucket.pos.coords[4] = cos(v + vr);
//                    bucket.pos.coords[5] = sin(v + vr) * sin((u + ur) / 2);

                    bucket.pos.coords[0] = sin(u + ur) * cos((v + vr) / 2);
                    bucket.pos.coords[1] = cos(u + ur);
                    bucket.pos.coords[2] = sin(u + ur) * sin((v + vr) / 2);
                    bucket.pos.coords[3] = sin(v + vr);
                    bucket.pos.coords[4] = cos(v + vr);
                    bucket.pos.coords[5] = sin(w + wr);
                    bucket.pos.coords[6] = cos(w + wr);

                    if (ui > -1 && ui < circ && vi > -1 && vi < circ && wi > -1 && wi < circ) {
                        // The extra units are for overlap, for matching edges up.
                        lattice.addPoint(bucket);
                        
                    }
                    donutMesh[calcLinearNArrayIndex(new NVector(new double[] {ui + 1, vi + 1, wi + 1}), circ + 2)] = bucket;
                    w += inc;
                }
                v += inc;
            }
            u += inc;
        }
    }

    public void placeNMobiusPipe() throws Exception {
        if (lattice != null) {
            if ((dims / 2) >= lattice.internalDims) {
                NVector cursor = new NVector(lattice.internalDims);
                for (int i = 0; i < cursor.dims; i++) {
                    cursor.coords[i] = 0;
                }
                NVector shiftCursor = new NVector(lattice.internalDims);
                for (int i = 0; i < shiftCursor.dims; i++) {
                    shiftCursor.coords[i] = 0;
                }
                NVector intCursor = new NVector(lattice.internalDims);
                for (int i = 0; i < intCursor.dims; i++) {
                    intCursor.coords[i] = 0;
                }
//                double inc = Math.PI / 20;
//                int circ = (int) (Math.PI * 2 / inc);
                int circ = (int) donutCircumfrence;
                double inc = (Math.PI * 2) / donutCircumfrence;
                NPoint[] donutMesh = new NPoint[(int) Math.pow(circ, intCursor.dims)];
                placeNMobiusPipeRecurse(cursor, shiftCursor, 0, inc, intCursor, donutMesh, circ);
                // Going to try to triangulate this sucker right here.  Won't be deLaunay, but that's ok.

            } else {
                // Well, can't really make one of those non-curved klein bottles, so...go fish.
                throw new Exception("Total dims must be at least 2x lattice dims.");
            }
        }
    }

    public void placeNMobiusPipeRecurse(NVector cursor, NVector shiftCursor, int dim, double inc, NVector intCursor, NPoint[] donutMesh, int circ) {
        if (dim < cursor.dims) {
            double d = 0;
            //for (double d = 0; d < Math.PI * 2 * 0.25; d += inc) {
            for (int i = 0; i < circ; i++) {
                if (dim < cursor.dims - 1) {
                    for (int j = dim + 1; j < cursor.dims; j++) {
//                        if (shiftCursor.coords[j] > 0) {
//                            shiftCursor.coords[j] = 0;
//                        } else {
                        shiftCursor.coords[j] += 0.5 * inc;
//                        }
                    }
                }
                if (randomRange > 0) {
                    cursor.coords[dim] = d + shiftCursor.coords[dim] + (((r.nextDouble() * 2) - 1) * randomRange);
                } else {
                    cursor.coords[dim] = d + shiftCursor.coords[dim];
                }
                cursor.coords[dim] = d + shiftCursor.coords[dim];
                intCursor.coords[dim] = i;
                placeNMobiusPipeRecurse(cursor, shiftCursor, dim + 1, inc, intCursor, donutMesh, circ);
                d += inc;
            }
        } else {
            NPoint bucket = new NPoint(dims);
            if (cursor.dims == 2) {
                bucket.pos.coords[0] = cursor.coords[0];
                bucket.pos.coords[1] = Math.sin(cursor.coords[1]);
                bucket.pos.coords[2] = Math.cos(cursor.coords[1]) * Math.cos(cursor.coords[0] * 0.5);
                bucket.pos.coords[3] = Math.cos(cursor.coords[1]) * Math.sin(cursor.coords[0] * 0.5);
            } else {
                for (int i = 0; i < cursor.dims; i++) {
                    bucket.pos.coords[(i * 3) + 0] = Math.sin(cursor.coords[i]);
                    bucket.pos.coords[(i * 3) + 1] = Math.cos(cursor.coords[i]) * Math.cos(cursor.coords[(i + 1) % cursor.dims] * 0.5);
                    bucket.pos.coords[(i * 3) + 2] = Math.cos(cursor.coords[i]) * Math.sin(cursor.coords[(i + 1) % cursor.dims] * 0.5);
                }
            }
//            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[i * 3] = Math.sin(cursor.coords[i]);
//            }
//            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[(i * 3) + 1] = Math.cos(cursor.coords[i]);
//            }
//            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[(i * 3) + 2] = Math.cos(cursor.coords[i]);
//            }
            lattice.addPoint(bucket);
            donutMesh[calcLinearNArrayIndex(intCursor, circ)] = bucket;
        }
    }

    /**
     * Oh, right; now I remember.  This function will try to turn the donut into a mesh without
     * the whole deLaunay triangulation process.
     * @param cursor
     * @param dim
     * @param inc
     * @param intCursor
     * @param donutMesh
     * @param circ 
     */
    public void meshNDonutRecurse(NVector cursor, int dim, double inc, NVector intCursor, NPoint[] donutMesh, int circ) {
        //TODO WAY not done yet.
        if (dim < cursor.dims) {
            int i = 0;
            for (double d = 0; d < Math.PI * 2; d += inc) {
                cursor.coords[dim] = d;
                intCursor.coords[dim] = i++;
                meshNDonutRecurse(cursor, dim + 1, inc, intCursor, donutMesh, circ);
            }
        } else {
            //donutMesh[calcLinearNArrayIndex(intCursor, circ)];
        }
    }

    public int calcLinearNArrayIndex(NVector address, int base) {
        int index = 0;
        for (int i = 0; i < address.dims; i++) {
            index += (int) (address.coords[i] * Math.pow(base, i));
        }
        return index;
    }

    public void shear1() {
        if (lattice != null) {
            Matrix shear = Matrix.identity(dims);
            for (int i = 1; i < dims; i++) {
                shear.val[i][i - 1] = 0.01;
            }
            for (NPoint i : lattice.points) {
                try {
                    i.pos = Matrix.lrvMult(shear, i.pos);


                } catch (Exception ex) {
                    Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void shearN() {
        if (lattice != null) {
            Matrix shear = Matrix.identity(dims);
            for (int i = 1; i < dims; i++) {
                shear.val[i][i - 1] = 0.01 * i;
            }
            for (NPoint i : lattice.points) {
                try {
                    i.pos = Matrix.lrvMult(shear, i.pos);


                } catch (Exception ex) {
                    Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void swapHands() {
        if (lattice != null) {
            for (NPoint i : lattice.points) {
                i.swapHands();
            }
        }
    }

    public void popCopyHands() {
        if (lattice != null) {
            for (NPoint i : lattice.points) {
                i.swapHands();
            }
        }
    }

    public void pushCopyHands() {
        if (lattice != null) {
            for (NPoint i : lattice.points) {
                i.swapHands();
            }
        }
    }
    //TODO Allow this to be controlled.
    public double joggleScale = 0.1;

    public void joggle() {
        if (lattice != null) {
            for (NPoint i : lattice.points) {
                for (int j = 0; j < i.dims; j++) {
                    i.pos.coords[j] += (r.nextDouble() - 0.5) * joggleScale;
                }
            }
        }
    }

    public int polarity(double x) {
        if (x < 0) {
            return -1;
        } else if (x > 0) {
            return 1;
        }
        return 0;
    }

    /**
     * This assumes that we're dealing with an n-cube.
     * Finds all the faces on the outside of the n-cube and takes them off the incomplete list.
     */
    public void clearOuterFaces() {
        if (lattice != null) {
            ArrayList<NFace> removal = new ArrayList<NFace>();
            for (NFace i : lattice.incompleteFaces) {
                for (int j = 0; j < dims; j++) {
                    double polarity = 0;
                    boolean outer = true;
                    for (NPoint p : i.points) {
                        if (polarity == 0) {
                            polarity = polarity(p.pos.coords[j]);
                        } else {
                            if (polarity != polarity(p.pos.coords[j])) {
                                outer = false;
                                break;
                            }
                        }
                    }
                    if (outer) {
                        removal.add(i);
                        break;
                    }
                }
            }
            for (NFace i : removal) {
                lattice.incompleteFaces.remove(i);
            }
        }
    }

    public int refreshCompletePoints() {
        int pointsComplete = 0;
        if (lattice != null) {
            for (NPoint q : lattice.points) {
                if (q.calcComplete()) {
                    pointsComplete++;
                }
            }
            System.out.println(pointsComplete + "/" + lattice.points.size() + " : " + ((100 * pointsComplete) / ((double) lattice.points.size())) + "%");
        }
        return pointsComplete;
    }

    public int fetchPointsComplete() {
        int pointsComplete = 0;
        if (lattice != null) {
            for (NPoint i : lattice.points) {
                if (i.complete) {
                    pointsComplete++;
                }
            }
        }
        System.out.println(pointsComplete);
        return pointsComplete;
    }

    /**
     * Assumes that the lattice describes the exterior of a figure.
     * Intended for subdividing an n-cube, after Auto-crystallize corners.
     */
    public void truncate() {
        if (lattice != null) {
            // Find all the corners by figuring out which points connect to which.
            // First, make a blank list (set) for each point.
            HashMap<NPoint, HashSet<NPoint>> connectsTo = new HashMap<NPoint, HashSet<NPoint>>();
            for (NFace i : lattice.incompleteFaces) {
                for (NPoint p : i.points) {
                    if (!connectsTo.containsKey(p)) {
                        connectsTo.put(p, new HashSet<NPoint>());
                    }
                }
            }

            // Now, for each point, add all connecting ones to the set.
            for (NFace i : lattice.incompleteFaces) {
                for (NPoint p : i.points) {
                    for (NPoint q : i.points) {
                        if (p != q) {
                            connectsTo.get(p).add(q);
                        }
                    }
                }
            }

            /* For the first point of the lot (it Breaks after the first), make a new
             * face from the points not the corner point.  Make the whole block into
             * a cell.  Remove the faces that include said corner point from the
             * list of incomplete faces, since they no longer are.
             */
            int index = 0;
            for (NPoint p : connectsTo.keySet()) {
                index = 0;
                // New cell.
                NCell cell = new NCell(dims, lattice.internalDims);
                Object[] stupid = connectsTo.get(p).toArray();
                NPoint[] connections = new NPoint[stupid.length];
                for (int i = 0; i < stupid.length; i++) {
                    connections[i] = (NPoint) stupid[i];
                }
                ArrayList<NPoint> facePts = null;
                // Finish all the old faces.
                boolean debug = true;
                if (debug) {
                    for (NPoint i : lattice.points) {
                        i.immune = true;
                    }
                }

                for (int i = 0; i < connections.length; i++) {
                    // This loop is being temporarily subverted.
                    connections[i].immune = false;
//                    facePts = new ArrayList<NPoint>();
//                    facePts.add(p);
//                    for (int j = 0; j < connections.length; j++) {
//                        if (i != j) {
//                            facePts.add(connections[j]);
//                        }
//                    }
//                    //TODO Check if p was properly given faces!
//                    cell.faces[index++] = fetchFace(facePts, cell, p.faces); // You know, maybe I could just make all the [faces
//                    //                                                     //   joined to p] complete.
                }
                if (debug) {
                    break;
                }
                // Now add the internal face.
                facePts = new ArrayList<NPoint>();
                for (int i = 0; i < connections.length; i++) {
                    facePts.add(connections[i]);
                }
                cell.faces[index] = fetchFace(facePts, cell);
                lattice.cells.add(cell);
                p.immune = true;
                // Just do one corner at a time.
                break;
            }
        }
    }

    /**
     * So...here's the idea here.
     * You have an NGon, defined by the connections between its points.
     * Like, a square, or a d8.  Or something crazier.
     * So, you chop off a corner, through all the points to which it is connected.
     * I'm going to assume that this is possible for all the cases we'll meet; n-cubes
     * and their truncations and their truncated faces.
     * This leaves you with some kind of NGon, but one dimension less.
     * If it has the right number of points to be a cell in its dimension,
     * make it a cell, and attach it to the corner, making a higher cell.
     * Continue on up the chain.
     */
    public void recursiveTruncate() {
        if (lattice != null) {
            NGon figure = new NGon(dims, dims + 1);
            // First, make a blank list (set) for each point.
            for (NFace i : lattice.incompleteFaces) {
                for (NPoint p : i.points) {
                    if (!figure.connectsTo.containsKey(p)) {
                        figure.connectsTo.put(p, new HashSet<NPoint>());
                    }
                }
            }

            // Now, for each point, add all connecting ones to the set.
            for (NFace i : lattice.incompleteFaces) {
                for (NPoint p : i.points) {
                    for (NPoint q : i.points) {
                        if (p != q) {
                            figure.connectsTo.get(p).add(q);
                        }
                    }
                }
            }

            figure.collectPointsFromConnections();
            try {
                ArrayList<NCell> result = truncateRecurse(figure);


            } catch (RockBottom ex) {
                Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public int counter = 0;

    public class RockBottom extends Exception {
    }

    public ArrayList<NCell> truncateRecurse(NGon figure) throws RockBottom {
        counter++;
        if (counter > 20) {
            System.out.println("Something's up.");
        }
        ArrayList<NCell> result = new ArrayList<NCell>();

//        if (figure.points.size() == figure.pointsForCell) {
//            // Turn it into a cell.
//            NCell bucket = new NCell(figure.dims, figure.pointsForCell - 1);
//            for (int i = 0; i < bucket.points.length; i++) {
//                bucket.points[i] = figure.points.get(i);
//            }
//            bucket.soloMakeFaces();
//            result.add(bucket);
//            return result;
//        } else {
        if (figure.points.size() == 0) {
            throw new RockBottom();
        }
        while (figure.points.size() > figure.pointsForCell) {
            // Pick a point.
            NPoint p = null;
            boolean succeeded = false;
            for (int pt = 0; pt < figure.points.size(); pt++) {
                p = figure.points.get(0);

                // Make connected pts into NGon.
                NGon newCutNGon = new NGon(figure.dims, figure.pointsForCell - 1);
                ArrayList<NPoint> connected = new ArrayList<NPoint>();
                for (NPoint i : figure.connectsTo.get(p)) {
                    HashSet<NPoint> subconnections = new HashSet<NPoint>();
                    newCutNGon.connectsTo.put(i, subconnections);
                    for (NPoint j : figure.connectsTo.get(p)) {
                        // If the one cut point IS connected to one of the others, add it.
                        if ((i != j) && (figure.connectsTo.get(i).contains(j))) {
                            subconnections.add(j);
                        }
                    }
                }
                newCutNGon.collectPointsFromConnections();

                // truncateRecurse said NGon.
                ArrayList<NCell> newFaces;
                try {
                    newFaces = truncateRecurse(newCutNGon);
                } catch (RockBottom e) {
                    continue;
                }


                // Take the resulting subcells and connect them to the original point, to make cells.
                for (NCell c : newFaces) {
                    NCell supercell = new NCell(dims, c.cellDims + 1);
                    supercell.points[0] = p;
                    for (int i = 0; i < c.points.length; i++) {
                        supercell.points[i + 1] = c.points[i];

                        // Connect things here to reflect truncation
                        for (int j = 0; j < c.points.length; j++) {
                            if (j != i) {
                                figure.connectsTo.get(c.points[i]).add(c.points[j]);
                            }
                        }
                    }
                    supercell.soloMakeFaces();

                    // Add the cells to the list.
                    result.add(supercell);
                }

                // Now remove the corner point from consideration
                figure.points.remove(p);
                figure.connectsTo.remove(p);
                for (HashSet<NPoint> h : figure.connectsTo.values()) {
                    h.remove(p);
                }
                succeeded = true;
                break;
            }
            if (!succeeded) {
                throw new RockBottom();
            }
        }
//        }
        // Turn it into a cell.
        NCell bucket = new NCell(figure.dims, figure.pointsForCell - 1);
        for (int i = 0; i < bucket.points.length; i++) {
            bucket.points[i] = figure.points.get(i);
        }
        bucket.soloMakeFaces();
        result.add(bucket);
        counter--;
        return result;
    }

    public NFace fetchFace(ArrayList<NPoint> points, NCell cell) {
        NFace bucket = new NFace(dims, points.size());
        for (int i = 0; i < bucket.points.length; i++) {
            bucket.points[i] = points.get(i);
        }
        for (NFace i : lattice.faces) {
            if (i.equivalent(bucket)) {
                if (i.cellA != null) {
                    i.cellB = cell;
                    lattice.incompleteFaces.remove(i);
                } else {
                    i.cellA = cell; // This usually shouldn't happen.
                }
                return i;
            }
        }
        bucket.cellA = cell;
        bucket.informPoints();
        lattice.incompleteFaces.add(bucket);
        lattice.faces.add(bucket);
        return bucket;
    }

    public NFace fetchFace(NFace tryFace, NCell cell) {
        for (NFace i : lattice.faces) {
            if (i.equivalent(tryFace)) {
                if (i.cellA != null) {
                    i.cellB = cell;
                    lattice.incompleteFaces.remove(i);
                } else {
                    i.cellA = cell; // This usually shouldn't happen.
                }
                return i;
            }
        }
        tryFace.cellA = cell;
        tryFace.informPoints();
        lattice.incompleteFaces.add(tryFace);
        lattice.faces.add(tryFace);
        return tryFace;
    }

    public NFace fetchFace(ArrayList<NPoint> points, NCell cell, HashSet<NFace> faces) {
        NFace bucket = new NFace(dims, points.size());
        for (int i = 0; i < bucket.points.length; i++) {
            bucket.points[i] = points.get(i);
        }
        for (NFace i : faces) {
            if (i.equivalent(bucket)) {
                if (i.cellA != null) {
                    i.cellB = cell;
                    lattice.incompleteFaces.remove(i);
                } else {
                    i.cellA = cell; // This usually shouldn't happen.
                }
                return i;
            }
        }
        bucket.cellA = cell;
        bucket.informPoints();
        lattice.incompleteFaces.add(bucket);
        lattice.faces.add(bucket);
        return bucket;
    }

    public void newNCubeWithTruncate() {
        if (lattice != null) {
            NPoint[] corners = new NPoint[1 << dims];
            placeNCubeRecurse(new NVector(dims), 0, corners);

            for (int i = 0; i < lattice.points.size(); i++) {
                System.out.print(lattice.points.get(i));
            }
            System.out.println();

            // It appears that I need a figure whose faces are all simplices.
            // I THINK that auto-truncating the corners should do it.
            if (lattice.internalDims == dims) {
                // Going to try to add corner simplices as cells.
                int[] cornersTally = new int[1 << dims];
                for (int i = 0; i < cornersTally.length; i++) {
                    cornersTally[i] = 0;
                }
                boolean foundEmpty = true;
                while (foundEmpty) {
                    foundEmpty = false;
                    for (int i = 0; i < cornersTally.length; i++) {
                        if (cornersTally[i] == 0) {// System.out.println(cornersTally);
                            // Add corner cell
                            foundEmpty = true;
                            cornersTally[i] = -1;
                            NCell cell = new NCell(dims, lattice.internalDims);
                            cell.points[0] = corners[i];
                            corners[i].immune = true;
                            int index = 1;
                            for (int bit = 0; bit < dims; bit++) {
                                int bucket = i ^ (1 << bit);
                                cell.points[index++] = corners[bucket];
                                cornersTally[bucket]++;
                            }

                            // Add faces to cell
                            for (int j = 0; j < cell.points.length; j++) {
                                // Form the face made by omitting the current point
                                NFace face = new NFace(dims, lattice.internalDims);
                                index = 0;
                                for (int k = 0; k < cell.points.length; k++) {
                                    if (j != k) {
                                        face.points[index++] = cell.points[k];
                                        cell.points[k].faces.add(face);
                                    }
                                }
                                face.cellA = cell;
                                cell.faces[j] = face;
                                // Let all outer faces be complete.
                                if (j == 0) {
                                    lattice.incompleteFaces.add(face);
                                }
                                lattice.faces.add(face);
                            }
                            lattice.cells.add(cell);
                        }
                    }
                }
            }


            NGon nCube = new NGon(dims, dims + 1);

//            // Connect all the corners, in the proper fashion.
//            for (int i = 0; i < corners.length; i++) {
//                nCube.connectsTo.put(corners[i], new HashSet<NPoint>());
//                int index = 1;
//                for (int bit = 0; bit < dims; bit++) {
//                    int bucket = i ^ (1 << bit);
//                    nCube.connectsTo.get(corners[i]).add(corners[bucket]);
//                }
//            }
//            nCube.collectPointsFromConnections();
            for (int i = 0; i < lattice.incompleteFaces.size(); i++) {
                NFace f = lattice.incompleteFaces.get(i);
                for (int j = 0; j < f.points.length; j++) {
                    NPoint p = f.points[j];
                    if (!nCube.connectsTo.containsKey(p)) {
                        nCube.connectsTo.put(p, new HashSet<NPoint>());
                    }
                    for (int k = 0; k < f.points.length; k++) {
                        NPoint q = f.points[k];
                        if (p != q) {
                            nCube.connectsTo.get(p).add(q);
                        }
                    }
                }
            }
            nCube.collectPointsFromConnections();

//            if (parent.was != null) {
//                for (int i = 0; i < nCube.points.size(); i++) {
//                    for (int j = 0; j < nCube.points.get(i).dims; j++) {
//                        if (nCube.points.get(i).pos.coords[j] != parent.was.points.get(i).pos.coords[j]) {
//                            System.err.println(i + ", " + j + " is different");
//                        }
//                    }
//                }
//            } else {
//                parent.was = nCube;
//            }            
//            for (int i = 0; i < nCube.points.size(); i++) {
//               System.out.print(nCube.points.get(i)); 
//            }
//            for (NPoint p : nCube.connectsTo.keySet()) {
//                System.out.print(p.toString() + "->");
//                for (NPoint q : nCube.connectsTo.get(p)) {
//                    System.out.print(q);
//                }
//                System.out.print(";");
//            }
//            System.out.println();
            ArrayList<NCell> result = null;
            try {
                result = truncateRecurse(nCube);
                //parent.was = nCube;


            } catch (RockBottom ex) {
                Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Now try to integrate the results into a lattice.
            lattice.cells.addAll(result);
            for (NCell c : result) {
                for (int i = 0; i < c.faces.length; i++) {
                    c.faces[i] = fetchFace(c.faces[i], c);
                }
            }
        }
    }

    public int checkIncomplete() {
        if (lattice != null) {
            HashSet<NFace> remove = new HashSet<NFace>();
            for (NFace i : lattice.incompleteFaces) {
                if ((i.cellA != null) && (i.cellB != null)) {
                    remove.add(i);
                }
            }
            lattice.incompleteFaces.removeAll(remove);
            return remove.size();
        }
        return 0;
    }

    public void checkDuplicates() {
        if (lattice != null) {
            HashSet<NFace> remove = new HashSet<NFace>();
            for (int i = 0; i < lattice.faces.size() - 1; i++) {
                for (int j = i + 1; j < lattice.faces.size(); j++) {
                    if (lattice.faces.get(i).equivalent(lattice.faces.get(j))) {
                        remove.add(lattice.faces.get(i));
                    }
                }
            }
            lattice.faces.removeAll(remove);
            lattice.incompleteFaces.removeAll(remove);
        }
    }

    public boolean checkDuplicates(NFace f) {
        if (lattice != null) {
            HashSet<NFace> remove = new HashSet<NFace>();
            for (int i = 0; i < lattice.faces.size(); i++) {
                if (f != lattice.faces.get(i) && lattice.faces.get(i).equivalent(f)) {
                    remove.add(lattice.faces.get(i));
                }
            }
            if (remove.isEmpty()) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Tries to triangulate an n-cube by chopping it into bits.
     * Huh, seems to work for 4 dims, but not 5+.  Not sure why.
     */
    public void katanaMath() {
        if (lattice != null) {
            NPoint[] corners = new NPoint[1 << dims];
            placeNCubeRecurse(new NVector(dims), 0, corners);

            NSolid start = new NSolid(dims, dims);
            start.points = lattice.points;
            ArrayList<NSolid> bits = new ArrayList<NSolid>();
            ArrayList<NSolid> finalBits = new ArrayList<NSolid>();
            bits.add(start);
            boolean done = false;
            while (!done) {
                done = true;
                ArrayList<NSolid> smallerBits = new ArrayList<NSolid>();
                for (int i = 0; i < bits.size(); i++) {
                    if (bits.get(i).points.size() != dims + 1) {
                        try {
                            NSolid[] halves = bits.get(i).divide();
                            smallerBits.add(halves[0]);
                            smallerBits.add(halves[1]);
                            done = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        finalBits.add(bits.get(i));
                    }
                }
                bits = smallerBits;
            }

            for (int i = 0; i < finalBits.size(); i++) {
                NCell bucket = new NCell(dims, dims);
                for (int j = 0; j < bucket.points.length; j++) {
                    bucket.points[j] = finalBits.get(i).points.get(j);
                }
                bucket.soloMakeFaces();
                for (int j = 0; j < bucket.faces.length; j++) {
                    bucket.faces[j] = fetchFace(bucket.faces[j], bucket);
                }
                lattice.cells.add(bucket);
            }
        }
    }

    public double cellVolumes() {
        double sum = 0;
        if (lattice != null) {
            for (NCell c : lattice.cells) {
                sum += c.getVolume();
            }
        }
        return sum;
    }

    public void bindToSkeletonHard() {
        if (lattice != null) {
            for (NPoint p : lattice.points) {
                NLatticeBone closestBone = null;
                double closestDist = -1;
                boolean snapped = false;
                for (NLatticeBone b : skeleton) {
                    NVector close = b.closestPoint(p.pos);
                    NVector diff = p.pos.minusB(close);
                    double len = diff.length();
                    boolean setMin = false;
                    if ((closestBone == null) || (len < closestDist)) {
                        closestBone = b;
                        closestDist = len;
                        snapped = false;
                        setMin = true;
                    }
                    if (len < b.radius) { //TODO This section could probably be optimized; it'll get run a lot.
                        // Move it back.
                        p.pos = close.plusB(diff.ipNormalize().multS(b.radius));
                        if (setMin) {
                            snapped = true;
                            closestDist = b.radius;
                        }
                    }
                }
                if (!snapped) {
                    NVector close = closestBone.closestPoint(p.pos);
                    NVector diff = p.pos.minusB(close);
                    p.pos = close.plusB(diff.ipNormalize().multS(closestBone.radius));
                }
            }
        }
    }

    public void bindToSkeletonElastic() {
        if (lattice != null) {
            //TODO Need to actually write this bit.
//            for (NPoint p : lattice.points) {
//                NLatticeBone closestBone = null;
//                double closestDist = -1;
//                boolean snapped = false;
//                for (NLatticeBone b : skeleton) {
//                    NVector close = b.closestPoint(p.pos);
//                    NVector diff = p.pos.minusB(close);
//                    double len = diff.length();
//                    boolean setMin = false;
//                    if ((closestBone == null) || (len < closestDist)) {
//                        closestBone = b;
//                        closestDist = len;
//                        snapped = false;
//                        setMin = true;
//                    }
//                    if (len < b.radius) { //TODO This section could probably be optimized; it'll get run a lot.
//                        // Move it back.
//                        p.pos = close.plusB(diff.ipNormalize().multS(b.radius));
//                        if (setMin) {
//                            snapped = true;
//                            closestDist = b.radius;
//                        }
//                    }
//                }
//                if (!snapped) {
//                    NVector close = closestBone.closestPoint(p.pos);
//                    NVector diff = p.pos.minusB(close);
//                    p.pos = close.plusB(diff.ipNormalize().multS(closestBone.radius));
//                }
//            }
        }
    }

    /**
     * Checks a cell against its permaradius.  Maybe I'll explain more
     * when I actually do this function.
     * @return 
     */
    public boolean checkCellRadius() {
        //TODO Do this.
        return false;
    }

    public static double getSimplexNVolume(NVector[] points) {
        if (points.length <= 0) {
            return 1000; // Eh, maybe I should throw an error or something.
        }
        int dims = points[0].dims;
        int simplexDims = points.length - 1;
        NBasis volBasis = new NBasis(dims, simplexDims);
        ArrayList<NPoint> ptList = new ArrayList<NPoint>();
        volBasis.bases[0] = NVector.zero(dims);
        for (int i = 0; i < volBasis.bases.length; i++) {
            volBasis.bases[i] = points[i + 1].minusB(points[0]);
        }
        volBasis.orthogonalize();
        try {
            volBasis.calcProjection();


        } catch (Exception ex) {
            Logger.getLogger(NCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        NVector[] ptVectors = new NVector[points.length];
        for (int i = 0; i < points.length; i++) {
            ptVectors[i] = points[i];
        }
        NVector[] result = Matrix.ipTransformCoords(volBasis.bases, ptVectors);
        //TODO Could maybe be cached
        Matrix m = new Matrix(simplexDims, simplexDims);
        for (int i = 1; i < result.length; i++) {
            NVector diff = result[i].minusB(result[0]);
            for (int j = 0; j < result[i].dims; j++) {
                m.val[i - 1][j] = diff.coords[j];
            }
        }
        try {
            return ((1.0 / MeMath.factorial(simplexDims)) * Math.abs(m.det()));


        } catch (Exception ex) {
            Logger.getLogger(NCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1000; // Yeah, yeah, yeah.
    }

    public static double getSimplexNVolume(NPoint[] points) {
        if (points.length <= 0) {
            return 1000; // Eh, maybe I should throw an error or something.
        }
        int dims = points[0].dims;
        int simplexDims = points.length - 1;
        NBasis volBasis = new NBasis(dims, simplexDims);
        ArrayList<NPoint> ptList = new ArrayList<NPoint>();
        volBasis.bases[0] = NVector.zero(dims);
        for (int i = 0; i < volBasis.bases.length; i++) {
            volBasis.bases[i] = points[i + 1].pos.minusB(points[0].pos);
        }
        volBasis.orthogonalize();
        try {
            volBasis.calcProjection();


        } catch (Exception ex) {
            Logger.getLogger(NCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        NVector[] ptVectors = new NVector[points.length];
        ptVectors[0] = new NVector(dims);
        for (int i = 1; i < points.length; i++) {
            ptVectors[i] = points[i].pos.minusB(points[0].pos);
            //ptVectors[i] = points[i].pos;
        }
        NVector[] result = Matrix.ipTransformCoords(volBasis.bases, ptVectors);
        //TODO Could maybe be cached
        Matrix m = new Matrix(simplexDims, simplexDims);
        for (int i = 1; i < result.length; i++) {
            NVector diff = result[i].minusB(result[0]);
            for (int j = 0; j < result[i].dims; j++) {
                m.val[i - 1][j] = diff.coords[j];
            }
        }
        try {
            return ((1.0 / MeMath.factorial(simplexDims)) * Math.abs(m.det()));


        } catch (Exception ex) {
            Logger.getLogger(NCell.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1000; // Yeah, yeah, yeah.
    }

    public void updateSticks() {
        completeSticks.clear();
        incompleteSticks.clear();
        if (lattice.internalDims > 1) {
            int facesDone = 0;
            for (NFace f : lattice.faces) {
                if (f.complete()) {
                    if (!hideComplete) {
                        for (int i = 0; i < f.points.length; i++) {
                            for (int j = i + 1; j < f.points.length; j++) {
                                boolean add = true;
                                for (Stick s : completeSticks) {
                                    if (s.equivalent(f.points[i], f.points[j])) {
                                        add = false;
                                        break;
                                    }
                                }
                                if (add) {
                                    completeSticks.add(new Stick(f.points[i], f.points[j]));
                                }
                            }
                        }
                    }
                } else {
                    if (!hideIncomplete) {
                        for (int i = 0; i < f.points.length; i++) {
                            for (int j = i + 1; j < f.points.length; j++) {
                                boolean add = true;
                                for (Stick s : incompleteSticks) {
                                    if (s.equivalent(f.points[i], f.points[j])) {
                                        add = false;
                                        break;
                                    }
                                }
                                if (add) {
                                    incompleteSticks.add(new Stick(f.points[i], f.points[j]));
                                }
                            }
                        }
                    }
                }
                facesDone++;
            }
        } else if (lattice.internalDims == 1) {
            //THINK This might not be quite right, at the edges.
            for (NCell c : lattice.cells) {
                for (int i = 0; i < c.points.length; i++) {
                    for (int j = i + 1; j < c.points.length; j++) {
                        boolean add = true;
                        for (Stick s : completeSticks) {
                            if (s.equivalent(c.points[i], c.points[j])) {
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            completeSticks.add(new Stick(c.points[i], c.points[j]));
                        }
                    }
                }
            }
        }
        sticksChanged = false;
    }

    public void updateSticksParallel() {
        completeSticks.clear();
        incompleteSticks.clear();
        if (lattice.internalDims > 1) {
            for (int k = 0; k < lattice.faces.size(); k++) {
                NFace f = lattice.faces.get(k);
                if (f.complete()) {
                    for (int i = 0; i < f.points.length; i++) {
                        for (int j = i + 1; j < f.points.length; j++) {
                            boolean add = true;
                            for (Stick s : completeSticks) {
                                if (s.equivalent(f.points[i], f.points[j])) {
                                    add = false;
                                    break;
                                }
                            }
                            if (add) {
                                completeSticks.add(new Stick(f.points[i], f.points[j]));
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < f.points.length; i++) {
                        for (int j = i + 1; j < f.points.length; j++) {
                            boolean add = true;
                            for (Stick s : incompleteSticks) {
                                if (s.equivalent(f.points[i], f.points[j])) {
                                    add = false;
                                    break;
                                }
                            }
                            if (add) {
                                incompleteSticks.add(new Stick(f.points[i], f.points[j]));
                            }
                        }
                    }
                }
            }
        } else if (lattice.internalDims == 1) {
            //THINK This might not be quite right, at the edges.
            for (int k = 0; k < lattice.cells.size(); k++) {
                NCell c = lattice.cells.get(k);
                for (int i = 0; i < c.points.length; i++) {
                    for (int j = i + 1; j < c.points.length; j++) {
                        boolean add = true;
                        for (Stick s : completeSticks) {
                            if (s.equivalent(c.points[i], c.points[j])) {
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            completeSticks.add(new Stick(c.points[i], c.points[j]));
                        }
                    }
                }
            }
        }
        sticksChanged = false;
    }

    /**
     * Looks for cells formed of incomplete faces, yet are not counted as
     * actual cells, presumably due to violation of one or more restrictions.
     * Might as well make them actual cells, though.
     * Returns the number of cells formed in this way.
     * @return 
     */
    public int joinPreCells() {
        ArrayList<NCell> converted = new ArrayList<NCell>();
        HashSet<NFace> convertedFaces = new HashSet<NFace>();
        for (NFace f : lattice.incompleteFaces) {
            // Brutish method:
            // List all connected faces.
            //// List all faces connected to points of face.
            //// Check them for connection to this face.
            // Discount the ones that are part of the complete cell connected to this face.
            // If some subset of lDim+1 (including target face) are connected, they form a cell.

            // Hey, so this is based on the assumption that any given incomplete face
            //     has a cell for cellA and null for cellB.
            if (convertedFaces.contains(f)) {
                continue;
            }
            HashSet<NFace> pointConnected = new HashSet<NFace>();
            pointConnected.add(f);
            for (NPoint p : f.points) {
                for (NFace g : p.faces) {
                    if (f.isConnected(g) && (f.cellA != g.cellA) && (g.cellB == null)) {
                        pointConnected.add(g);
                    }
                }
            }
            ArrayList<NFace> connected = new ArrayList<NFace>(pointConnected);
            int[] indices = new int[lattice.internalDims + 1];
            ArrayList<NFace> result = formCellRecurse(connected, indices, 0, lattice.internalDims, 0);
            if (result != null) {
                NCell newCell = new NCell(lattice.dims, lattice.internalDims);
                for (int i = 0; i < newCell.faces.length; i++) {
                    newCell.faces[i] = result.get(i);
                }
                HashSet<NPoint> points = new HashSet<NPoint>();
                for (NFace g : newCell.faces) {
                    for (NPoint p : g.points) {
                        points.add(p);
                        if (points.size() == newCell.points.length) {
                            break;
                        }
                    }
                    if (points.size() == newCell.points.length) {
                        break;
                    }
                }
                // Assuming we have enough points.
                int i = 0;
                for (NPoint p : points) {
                    newCell.points[i++] = p;
                }
                converted.add(newCell);
                convertedFaces.addAll(result);
            }
        }
        for (NCell c : converted) {
            for (NFace f : c.faces) {
                f.cellB = c;
                lattice.incompleteFaces.remove(f);
            }
            lattice.cells.add(c);
            sticksChanged = true;
        }
        return converted.size();
    }

    public ArrayList<NFace> formCellRecurse(ArrayList<NFace> connected, int[] indices, int curLevel, int maxLevel, int curIndex) {
        if (curLevel <= maxLevel) {
            for (int i = curIndex; i < connected.size() - maxLevel + curLevel; i++) {
                indices[curLevel] = i;
                ArrayList<NFace> result = formCellRecurse(connected, indices, curLevel + 1, maxLevel, i + 1);
                if (result != null) {
                    return result;
                }
            }
            return null;
        } else {
            boolean allConnected = true;
            for (int i = 0; i < indices.length - 1; i++) {
                for (int j = i + 1; j < indices.length; j++) {
                    if (!connected.get(indices[i]).isConnected(connected.get(indices[j]))) {
                        allConnected = false;
                        break;
                    }
                }
                if (!allConnected) {
                    break;
                }
            }
            if (allConnected) {
                ArrayList<NFace> result = new ArrayList<NFace>();
                for (int i = 0; i < indices.length; i++) {
                    result.add(connected.get(indices[i]));
                }
                return result;
            }
            return null;
        }
    }
    public static final int GROUND_COLORFUL = 1;

    public void addGround(double elevation, int type) {
        switch (type) {
            case GROUND_COLORFUL:
                NSurface ground = new NSurface(lattice.dims, lattice.dims - 1, new Color(r.nextInt(), true));
                ground.points[0] = new NPoint(dims);
                ground.points[0].pos.coords[0] = elevation;
                for (int i = 1; i < dims; i++) {
                    ground.points[0].pos.coords[i] = -8;
                }
                for (int i = 1; i < ground.points.length; i++) {
                    ground.points[i] = new NPoint(dims);
                    ground.points[i].pos.coords[0] = elevation;
                    for (int j = 1; j < dims; j++) {
                        if (i == j) {
                            ground.points[i].pos.coords[j] = 8 * dims;
                        } else {
                            ground.points[i].pos.coords[j] = -8;
                        }
                    }
                }
                ground.makeBasis();
                for (NCell c : lattice.cells) {
                    boolean hasAbove = false;
                    for (int i = 0; i < c.points.length; i++) {
                        if (c.points[i].pos.coords[0] >= elevation) {
                            hasAbove = true;
                            break;
                        }
                    }
                    if (!hasAbove) {
                        continue;
                    }
                    boolean hasBelow = false;
                    for (int i = 0; i < c.points.length; i++) {
                        if (c.points[i].pos.coords[0] <= elevation) {
                            hasBelow = true;
                            break;
                        }
                    }
                    if (!hasBelow) {
                        continue;
                    }
                    c.surfaces.add(ground);
                }
                break;
            default:
        }
    }

    public boolean checkMinMaxLength(ArrayList<NPoint> frame, double minLength, double maxLength) {
        if (minLength == -1 && maxLength == -1) {
            return true;
        }
        if (frame.size() <= 0) {
            return false; // Eh, maybe I should throw an error or something.
        }

        if (minLength != -1) {
            if (maxLength != -1) {
                // Check both min and maxLength
                for (int i = 0; i < frame.size() - 1; i++) {
                    for (int j = i + 1; j < frame.size(); j++) {
                        double dist = frame.get(i).pos.dist(frame.get(j).pos);
                        if (dist < minLength || dist > maxLength) {
                            return false;
                        }
                    }
                }
            } else {
                // Just check minLength
                for (int i = 0; i < frame.size() - 1; i++) {
                    for (int j = i + 1; j < frame.size(); j++) {
                        if (frame.get(i).pos.dist(frame.get(j).pos) < minLength) {
                            return false;
                        }
                    }
                }
            }
        } else {
            // Just check maxLength
            for (int i = 0; i < frame.size() - 1; i++) {
                for (int j = i + 1; j < frame.size(); j++) {
                    if (frame.get(i).pos.dist(frame.get(j).pos) > maxLength) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public int getMisclassifiedFaceCount() {
        int result = 0;
        for (NFace f : lattice.incompleteFaces) {
            if (f.complete()) {
                result++;
            }
        }
        //System.out.println("Faces misclassified: " + result);
        return result;
    }

    /**
     * Creates and returns a camera without adding it to the lattice's list of
     * cameras, and without alerting whatever form you assign it to.  I think
     * that currently you can safely pass in null for cameraForm.
     * @param orient
     * @param cameraForm
     * @return 
     */
    public Camera createIndependentCamera(boolean orient, CameraForm cameraForm) {
        Camera newCam = null;
        if (lattice != null) {
            if (orient) {
                newCam = new Camera(dims, lattice.internalDims, lattice);
                NCell cell = lattice.cells.get(r.nextInt(lattice.cells.size()));
                NVector center = new NVector(dims);
                for (NPoint i : cell.points) {
                    center = center.plusB(i.pos);
                }
                center = center.multS(1.0 / cell.points.length);
                newCam.pos = center;
                try {
                    newCam.realignOrientation(cell);
                } catch (Exception ex) {
                    Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                }
                newCam.camForm = cameraForm;
            } else {
                newCam = new Camera(dims, lattice.internalDims, lattice);
                NCell cell = lattice.cells.get(r.nextInt(lattice.cells.size()));
                newCam.cell = cell;
                NVector center = new NVector(dims);
                for (NPoint i : cell.points) {
                    center = center.plusB(i.pos);
                }
                center = center.multS(1.0 / cell.points.length);
                //newCam.pos = center;
                permacenter.pos = center;
                permaradius = cell.points[0].pos.minusB(center).length();
//                try {
//                    newCam.realignOrientation(cell);
//                } catch (Exception ex) {
//                    Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
//                }
                newCam.camForm = cameraForm;
            }
        }
        return newCam;
    }
    
    public void clearEdges() {
        if (lattice != null) {
            lattice.cells.clear();
            lattice.faces.clear();
            lattice.incompleteFaces.clear();
            lattice.cameras.clear();
            completeSticks.clear();
            incompleteSticks.clear();
            sticksChanged = true;
            for (NPoint p : lattice.points) {
                p.faces.clear();
                p.complete = false;
                p.immune = false;
                if (p.candidates != null) {
                    p.candidates.clear();
                }
                if (p.faceIDs != null) {
                    p.faceIDs.clear();
                }
            }
        }
    }
    
    
    // New
    
    public void add3dTriPoints() {
        if (dims != 3) throw new InvalidParameterException("Dims must be 3");

        double scale = 100;
        
        //TODO I could probably make this a more equilateral tetrahedron.
        
        NPoint bucket = new NPoint(dims);
        bucket.pos.coords[0] = 0;
        bucket.pos.coords[1] = 0;
        bucket.pos.coords[2] = 1 * scale;
        lattice.addPoint(bucket);

        bucket = new NPoint(dims);
        bucket.pos.coords[0] = -0.5 * scale;
        bucket.pos.coords[1] = -0.5 * scale;
        bucket.pos.coords[2] = -0.5 * scale;
        lattice.addPoint(bucket);

        bucket = new NPoint(dims);
        bucket.pos.coords[0] = 0.5 * scale;
        bucket.pos.coords[1] = -0.5 * scale;
        bucket.pos.coords[2] = -0.5 * scale;
        lattice.addPoint(bucket);

        bucket = new NPoint(dims);
        bucket.pos.coords[0] = 0;
        bucket.pos.coords[1] = 0.5 * scale;
        bucket.pos.coords[2] = -0.5 * scale;
        lattice.addPoint(bucket);
    }

    public void placeCameraAtOrigin(boolean orient) {
        if (lattice != null) {
            if (orient) {
                Camera newCam = new Camera(dims, lattice.internalDims, lattice);
                NCell cell = lattice.cells.get(r.nextInt(lattice.cells.size()));
                NVector center = new NVector(dims);
                for (NPoint i : cell.points) {
                    center = center.plusB(i.pos);
                }
                center = center.multS(1.0 / cell.points.length);
                newCam.pos = center;
                try {
                    newCam.realignOrientation(cell);
                } catch (Exception ex) {
                    Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                }
                newCam.camForm = new CameraForm(this, newCam, parent);
                lattice.cameras.add(newCam);
                newCam.camForm.show();
            } else {
                Camera newCam = new Camera(dims, lattice.internalDims, lattice);
                NCell cell = lattice.cells.get(r.nextInt(lattice.cells.size()));
                newCam.cell = cell;
                NVector center = new NVector(dims);
                for (NPoint i : cell.points) {
                    center = center.plusB(i.pos);
                }
                center = center.multS(1.0 / cell.points.length);
                //newCam.pos = center;
                permacenter.pos = center;
                permaradius = cell.points[0].pos.minusB(center).length();
                newCam.camForm = new CameraForm(this, newCam, parent);
//                newCam.camForm.render = false; //NEW
                lattice.cameras.add(newCam);
                newCam.camForm.show();
            }
//            parent.dp.repaint(); //NEW
        }
    }
    
    // NOTE: I've only written this to deal with one large cell.  Otherwise, fix it.
    public void addSurface(double[][] coords) {
        NSurface ground = new NSurface(lattice.dims, lattice.dims - 1, new Color(r.nextInt(), true));
        ground.points[0] = new NPoint(dims);
        ground.points[0].pos.coords[0] = coords[0][0];
        ground.points[0].pos.coords[1] = coords[0][1];
        ground.points[0].pos.coords[2] = coords[0][2];
        ground.points[1] = new NPoint(dims);
        ground.points[1].pos.coords[0] = coords[1][0];
        ground.points[1].pos.coords[1] = coords[1][1];
        ground.points[1].pos.coords[2] = coords[1][2];
        ground.points[2] = new NPoint(dims);
        ground.points[2].pos.coords[0] = coords[2][0];
        ground.points[2].pos.coords[1] = coords[2][1];
        ground.points[2].pos.coords[2] = coords[2][2];
        ground.makeBasis();
        for (NCell c : lattice.cells) {
            c.surfaces.add(ground);
        }
    }
    
    /**
     * Note that this assumes that all references are as they should be, and we
     * don't, like, have a cell with this point, but no reference chain the other way.
     * Also, it doesn't currently remove points from Sticks.
     * @param point 
     */
    public void deleteMeshPoint(NPoint point) {
      if (lattice != null && point != null) {
        System.out.println("deleting point " + point);
        lattice.points.remove(point);
        chosens.remove(point);
        highlighteds.remove(point);
        
        if (point.candidates != null) {
          for (NPoint neighbor : point.candidates) {
            neighbor.candidates.remove(point);
          }
        }
        
        ArrayList<NFace> faces = new ArrayList<NFace>(point.faces);
        for (NFace face : faces) {
          deleteMeshFace(face); // This should also take care of relevant cells.
        }
      }
    }

    private int cellId = 0;
    
    public void deleteMeshFace(NFace face) {
      if (lattice != null && face != null) {
        System.out.println("deleting face " + face);
        lattice.faces.remove(face);
        
        if (taggedFace == face) {
          taggedFace = null;
        }
        
        for (NPoint point : face.points) {
          point.faces.remove(face);
          point.complete = false;
        }
        
        deleteMeshCell(face.cellB);
        deleteMeshCell(face.cellA);
        deleteMeshCell(face.cellBBackup);

        lattice.incompleteFaces.remove(face);
      }
    }

    /**
     * Doesn't check cameras' `cell`.
     * @param cell 
     */
    public void deleteMeshCell(NCell cell) {
      if (lattice != null && cell != null) {
        cell.id = cellId++;
        System.out.println("deleting cell " + cell.id  + " " + cell);
        highlightedCells.remove(cell);
        
        if (lastCell == cell) {
          lastCell = null;
        }
        
        if (taggedCell == cell) {
          taggedCell = null;
        }
        
        lattice.cells.remove(cell);
        
        for (NPoint point : cell.points) {
          point.complete = false;
        }
        
        for (NFace face : cell.faces) {
          if (face.cellB == cell) {
            face.cellB = null;
          }
          if (face.cellA == cell) {
            face.cellA = face.cellB;
            face.cellB = null;
          }
          if (face.cellBBackup == cell) {
            face.cellBBackup = null;
          }
          if (!lattice.incompleteFaces.contains(face)) { //TODO Could be slow
            lattice.incompleteFaces.add(face);
          }
        }
      }
    }
}
