/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.lang.reflect.Array;
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
    public Random r = new Random();
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
    public boolean hideImmune = false;
    public boolean hideComplete = false;

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

    public void render(Graphics2D g, int renderMode, int width, int height, double transX, double transY, double scaleX, double scaleY) {
//        g.setColor(Color.red);
//        g.fillRect(-50, -50, 100, 100);
        g.setStroke(new BasicStroke(0.01f));
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
                                        Matrix augmented = new Matrix(3, dims);
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
                                    break;
                            }
                        }
                        break;
                    case 100:
                    case 3:
                    case 2:
                    case 1:
                        if (lattice != null) {
                            for (int hasStereo = 0; hasStereo <= 1; hasStereo++) {
                                double stereoCurDelta = 0;
                                if (hasStereo == 1) {
                                    if (stereo && rot.length > 1) {
                                        stereoCurDelta = 0.5 * stereoDelta;
                                        rot[1] -= stereoDegrees;
                                    } else {
                                        break;
                                    }
                                } else {
                                    if (stereo && rot.length > 1) {
                                        stereoCurDelta = -0.5 * stereoDelta;
                                    }
                                }
                                //TODO I should probably be able to rotate this thing.
                                for (NPoint i : lattice.points) {
                                    try {
                                        if (hideImmune && i.immune) {
                                            continue;
                                        }
                                        g.setColor(Color.black);
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
                                    } catch (Exception e) {
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
                                        g.setColor(Color.magenta);
                                        g.draw(bucket);
                                    } catch (Exception e) {
                                    }
                                }
                                for (NCell i : lattice.cells) {
                                    g.setColor(Color.blue);
                                    for (int j = 0; j < i.faces.length; j++) {
                                        if (i.faces[j].points.length != 1) {
                                            if (hideComplete && (!lattice.incompleteFaces.contains(i.faces[j]))) {
                                                continue;
                                            }
                                            for (int k = 0; k < i.faces[j].points.length - 1; k++) {
                                                if (hideImmune && i.faces[j].points[k].immune) {
                                                    continue;
                                                }
                                                for (int l = k + 1; l < i.faces[j].points.length; l++) {
                                                    try {
                                                        if (hideImmune && i.faces[j].points[l].immune) {
                                                            continue;
                                                        }
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
                                                        if (fourth.coords.length > 0) {
                                                            line.x2 = fourth.coords[0] + stereoCurDelta;
                                                        } else {
                                                            line.x2 = 0;
                                                        }
                                                        if (second.coords.length > 1) {
                                                            line.y2 = fourth.coords[1];
                                                        } else {
                                                            line.y2 = 0;
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
                                                if (hideImmune && (i.faces[j].points[0].immune || i.faces[j + 1 % i.faces.length].points[0].immune)) {
                                                    continue;
                                                }
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
                                                if (fourth.coords.length > 0) {
                                                    line.x2 = fourth.coords[0] + stereoCurDelta;
                                                } else {
                                                    line.x2 = 0;
                                                }
                                                if (second.coords.length > 1) {
                                                    line.y2 = fourth.coords[1];
                                                } else {
                                                    line.y2 = 0;
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
                                g.setColor(Color.red);
                                for (int j = 0; j < lattice.incompleteFaces.size(); j++) {
                                    for (int k = 0; k < lattice.incompleteFaces.get(j).points.length - 1; k++) {
                                        if (hideImmune && lattice.incompleteFaces.get(j).points[k].immune) {
                                            continue;
                                        }
                                        for (int l = k + 1; l < lattice.incompleteFaces.get(j).points.length; l++) {
                                            try {
                                                if (hideImmune && lattice.incompleteFaces.get(j).points[l].immune) {
                                                    continue;
                                                }
                                                Line2D.Double line = new Line2D.Double();
                                                NVector start = lattice.incompleteFaces.get(j).points[k].pos;
                                                NVector second = viewRotate(start, rot, rotCoords);
                                                start = lattice.incompleteFaces.get(j).points[l].pos;
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
                                                if (fourth.coords.length > 0) {
                                                    line.x2 = fourth.coords[0] + stereoCurDelta;
                                                } else {
                                                    line.x2 = 0;
                                                }
                                                if (second.coords.length > 1) {
                                                    line.y2 = fourth.coords[1];
                                                } else {
                                                    line.y2 = 0;
                                                }
                                                //bucket.width = 0.02;
                                                //bucket.height = 0.02;
                                                g.draw(line);
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                }
                                for (Camera c : lattice.cameras) {
                                    try {//c.pos.length()
                                        g.setColor(Color.ORANGE);
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
                                            if (fourth.coords.length > 0) {
                                                line.x2 = fourth.coords[0] + stereoCurDelta;
                                            } else {
                                                line.x2 = 0;
                                            }
                                            if (second.coords.length > 1) {
                                                line.y2 = fourth.coords[1];
                                            } else {
                                                line.y2 = 0;
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
                                                if (fourth.coords.length > 0) {
                                                    line.x2 = fourth.coords[0] + stereoCurDelta;
                                                } else {
                                                    line.x2 = 0;
                                                }
                                                if (second.coords.length > 1) {
                                                    line.y2 = fourth.coords[1];
                                                } else {
                                                    line.y2 = 0;
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
        g.scale(0.01, 0.01);
        g.translate(-250, -250);
    }
    public double repulsionCutoff = -1;
    public int repulsionMode = 0;

    public void repel() {
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
                            force.coords[k] /= 10000;
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

    public void clickPoint(int mx, int my) {
        double x = (mx - 250) * 0.01;
        double y = (my - 250) * 0.01;
        switch (mode) {
            case 0: // 2-2 Triangulation
                if (lattice != null) {
                    if (chosens.size() >= 3) {
                        chosens.clear();
                        break;
                    }
                    NPoint closest = null;
                    double dist = 0;
                    for (NPoint i : lattice.points) {
                        double newdist = sqr(i.pos.coords[0] - x) + sqr(i.pos.coords[1] - y);
                        if ((closest == null) || newdist < dist) {
                            closest = i;
                            dist = newdist;
                        }
                    }
                    chosens.add(closest);
                }
                break;
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
        //NFace leaf = lattice.incompleteFaces.get(faceNum);
        NPoint a = lattice.points.get(r.nextInt(lattice.points.size()));
        double nearestDist = -1;
        NPoint b = null;
        for (NPoint i : lattice.points) {
            if (((nearestDist == -1) || (a.distSqr(i) < nearestDist)) && (a != i)) {
                b = i;
                nearestDist = a.distSqr(i);
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
        double radius = radiusSize * leaf.points[0].dist(b);
        ArrayList<NPoint> nearest = new ArrayList<NPoint>();
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
                anchors.add(i);
                //TODO I really ought to change this to just pick one in withinCircle and go from there, but hang on.
                if ((maxThinness == -1) || checkThinness(anchors, maxThinness)) {
                    ArrayList<NPoint> withinCircle = findCircleContentsALT(anchors);
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
                        lattice.cells.add(newCell);
                        lattice.incompleteFaces.add(leaf);
                        lattice.faces.add(leaf);
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

    public ArrayList<NPoint> findCircleContents(ArrayList<NPoint> anchors) {
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
            Matrix augmented = new Matrix(3, dims);
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
        }
    }

    public ArrayList<NPoint> findCircleContentsALT(ArrayList<NPoint> anchors) {
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
                for (int i = 0; i < dims; i++) {
                    blank.coords[i] = 0;
                }
                points.add(blank);
                for (int i = 1; i < anchors.size(); i++) {
                    NVector bucket = new NVector(dims);
                    NVector bucket2 = new NVector(dims);
                    for (int j = 0; j < dims; j++) {
                        bucket.coords[j] = anchors.get(i).pos.coords[j] - anchors.get(0).pos.coords[j];
                        bucket2.coords[j] = anchors.get(i).pos.coords[j] - anchors.get(0).pos.coords[j];
                    }
                    bases.add(bucket);
                    points.add(bucket2);
                }
                basis = new Matrix(bases.size(), dims);
                // Gram-Schmidt orthogonalization
                for (int i = 0; i < bases.size(); i++) {
                    for (int j = 0; j < i; j++) {
                        bases.set(i, bases.get(i).minusB(NVector.lrProj(bases.get(j), bases.get(i))));
                    }//System.out.println(bases);
                    bases.get(i).ipNormalize();
                    for (int j = 0; j < dims; j++) {
                        basis.val[i][j] = bases.get(i).coords[j];
                    }
                }
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
            Matrix cramer = new Matrix(newAnchors.get(0).dims + 1, newAnchors.size());
            for (int row = 0; row < newAnchors.size(); row++) {
                for (int col = 0; col < newAnchors.get(0).dims; col++) {
                    cramer.val[col][row] = 2 * newAnchors.get(row).coords[col];
                }
                cramer.val[newAnchors.get(0).dims][row] = 1;
            }
//            if (0 == 1) {
            Matrix test = new Matrix(newAnchors.get(0).dims, newAnchors.size());
            for (int row = 0; row < newAnchors.size(); row++) {
                for (int col = 0; col < newAnchors.get(0).dims; col++) {
                    test.val[col][row] = newAnchors.get(row).coords[col];
                }
            }
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
                Matrix temp = cramer.copy();
                for (int row = 0; row < temp.rows; row++) {
                    temp.val[col][row] = b.coords[row];
                }//System.out.println(temp);
                centerV.coords[col] = temp.det() / denom;
//                System.out.println(temp.det());
            }
//            System.out.println(centerV);

            double radius = 0;
            for (int j = 0; j < newAnchors.size(); j++) {
                radius = 0;
                for (int i = 0; i < centerV.dims; i++) {
                    radius += sqr(centerV.coords[i] - newAnchors.get(j).coords[i]);
                }//System.out.println(Math.sqrt(radius));
//                System.out.println("flatrad" + j + "=" +Math.sqrt(radius));
            }

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
            } else {
                center.pos = centerV;
            }
            // Congrats!  You now have the center of whatever n-sphere is appropriate for the number of points you have!

            radius = 0;
            for (int j = 0; j < anchors.size(); j++) {
                radius = 0;
                for (int i = 0; i < dims; i++) {
                    radius += sqr(center.pos.coords[i] - anchors.get(j).pos.coords[i]);
                }//System.out.println(Math.sqrt(radius));
//                System.out.println("rad" + j + "=" +Math.sqrt(radius));
            }
            ArrayList<NPoint> result = new ArrayList<NPoint>();
            for (int i = 0; i < center.pos.coords.length; i++) {
                if (Double.isNaN(center.pos.coords[i]) || Double.isInfinite(center.pos.coords[i])) {
                    throw new Exception("Straight line sphere");
                }
            }
            for (NPoint i : lattice.points) { //TODO This seems to be the slowest point ever.
                if ((center.distSqr(i) <= radius) && (!anchors.contains(i)) && (!i.immune)) {
                    result.add(i);
                }
            }
            if (result.size() == 0) {
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

    public boolean crystallize() {
        if (lattice != null) {
            if (!lattice.incompleteFaces.isEmpty()) {
                int top = 0;
                if (!allowSkipHardFaces) {
                    top = 0;
                } else {
                    top = lattice.incompleteFaces.size() - 1;
                }
                for (int faceNum = 0; faceNum <= top; faceNum++) {
                    NFace leaf = lattice.incompleteFaces.get(faceNum);
                    double radius;
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
                    ArrayList<NPoint> nearest = new ArrayList<NPoint>();
                    if (radiusSize != -1) {
                        for (NPoint i : lattice.points) {
                            if ((leaf.points[0].dist(i) <= radius) && (!leaf.cellA.isaPoint(i)) && (!i.immune)) {
                                nearest.add(i);
                            }
                        }
                    } else {
                        for (NPoint i : lattice.points) {
                            if (!leaf.cellA.isaPoint(i) && (!i.immune)) {
                                nearest.add(i);
                            }
                        }
                    }
                    ArrayList<NPoint> anchors = new ArrayList<NPoint>();
                    for (NPoint i : leaf.points) {
                        anchors.add(i);
                    }
                    for (NPoint i : nearest) {
                        anchors.add(i);
                        //TODO I really ought to change this to just pick one in withinCircle and go from there, but hang on.
                        if ((maxThinness == -1) || checkThinness(anchors, maxThinness)) {
                            ArrayList<NPoint> withinCircle = findCircleContentsALT(anchors);
                            if (withinCircle.isEmpty()) {
                                NCell newCell = new NCell(dims, lattice.internalDims);
                                for (int j = 0; j < newCell.points.length; j++) {
                                    newCell.points[j] = anchors.get(j);
                                }
                                newCell.faces[0] = leaf;
                                leaf.cellB = newCell;
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
                                lattice.cells.add(newCell);
                                lattice.incompleteFaces.remove(leaf);
                                return true;
                            }//faceNum
                        }
                        anchors.remove(i);
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkThinness(ArrayList<NPoint> frame, double thinness) {
        // This could actually be done in series with the second part.
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
                return false;
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
                return false;
            }
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
    public double donutCircumfrence = 30;

    public void placeNDonut() {
        if (lattice != null) {
            if ((dims / 2) >= lattice.internalDims) {
                NVector cursor = new NVector(lattice.internalDims);
                for (int i = 0; i < cursor.dims; i++) {
                    cursor.coords[i] = 0;
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
                placeNDonutRecurse(cursor, 0, inc, intCursor, donutMesh, circ);
                // Going to try to triangulate this sucker right here.  Won't be deLaunay, but that's ok.

            } else {
                // Well, can't really make one of those non-curved donuts, so...go fish.
            }
        }
    }

    public void placeNDonutRecurse(NVector cursor, int dim, double inc, NVector intCursor, NPoint[] donutMesh, int circ) {
        if (dim < cursor.dims) {
            int i = 0;
            for (double d = 0; d < Math.PI * 2; d += inc) {
                cursor.coords[dim] = d;
                intCursor.coords[dim] = i++;
                placeNDonutRecurse(cursor, dim + 1, inc, intCursor, donutMesh, circ);
            }
        } else {
            NPoint bucket = new NPoint(dims);
            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[i * 2] = Math.sin(cursor.coords[i] + (inc * 0.25 * (r.nextDouble() - 0.5)));
                bucket.pos.coords[i * 2] = Math.sin(cursor.coords[i]);
            }
            for (int i = 0; i < cursor.dims; i++) {
//                bucket.pos.coords[(i * 2) + 1] = Math.cos(cursor.coords[i] + (inc * 0.25 * (r.nextDouble() - 0.5)));
                bucket.pos.coords[(i * 2) + 1] = Math.cos(cursor.coords[i]);
            }
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
        }
        System.out.println(pointsComplete);
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

    public void checkIncomplete() {
        if (lattice != null) {
            HashSet<NFace> remove = new HashSet<NFace>();
            for (NFace i : lattice.incompleteFaces) {
                if ((i.cellA != null) && (i.cellB != null)) {
                    remove.add(i);
                }
            }
            lattice.incompleteFaces.removeAll(remove);
        }
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
}
