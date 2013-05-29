/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.password.PasswordFinder;

/**
 *
 * @author mewer12
 */
public class MainTest {

    public static int[] forTest() {
        int[] result = new int[]{0, 1, 2, 3};
        return result;
    }

    public static String lineToMathematica(NVector lPos, NVector lDir) {
        return lPos.toString() + "+" + lDir.toString() + "l1";
    }

    public static String planeToMathematica(NVector origin, NVector... dirs) {
        StringBuilder sb = new StringBuilder();
        sb.append(origin.toString());
        for (int i = 0; i < dirs.length; i++) {
            sb.append("+" + dirs[i].toString() + "p" + (i + 1));
        }
        return sb.toString();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        {
            Object dbls = Array.newInstance(double.class, 2, 3, 4);
            MeUtils.setInNArray(dbls, 5.3, 0, 1, 2);
            double[][][] randdbl = (double[][][])MeUtils.randomDArray(new Random(), 2,2,2);
            if (1==1) return;
        }
        {
            //NewClass<NPoint> blah = new NewClass<NPoint>();
            //NPoint[] npa = blah.genericArray(5);
            double[] rdbl = (double[])MeUtils.concatArrays(new double[]{1,2,3.5}, new double[]{1,2.1,3,4});
            char[] rchar = (char[])MeUtils.concatArrays(new char[]{'a','b','c'}, "what".toCharArray(),"the".toCharArray(),"deuce".toCharArray());
            int[] rint = (int[])MeUtils.concatArrays(new int[]{1,2,3}, new int[]{1,2,3,4});
            NPoint[] rnpoint = (NPoint[])MeUtils.concatArrays(new NPoint[]{new NPoint(5), new NPoint(3), new NPoint(6)}, new NPoint[]{new NPoint(1),new NPoint(2),new NPoint(3),new NPoint(4)});
            if (1==1) return;
        }
        NVector av = new NVector(new double[] {1, 0, 0, 0});
        NVector bv = new NVector(new double[] {0, 1, 0, 0});
        System.out.println(NVector.angle(av, bv));
        System.out.println(NVector.angle(bv, av));
        if (1==1) return;
        long large = 0;
        System.out.println("begin");
        long small = 0;
        for (large = 0; large < 1000000000L; ) {
            large++;
            small--;
        }
        System.out.println(large + " " + small);
        if (1==1) return;
        SSHClient sc = new SSHClient();
        char[] pwd = new char[8];
        String pswd = String.copyValueOf(pwd);
        ArrayList<String> hosts = new ArrayList<String>();
        Process pr = Runtime.getRuntime().exec(new String[]{"bash", "-c", "/home/mewer12/stuff/nmap/usr/bin/nmap -p 22 10.101.6.1-255 | grep -E -o \"report.*[0-9]\" | grep -o -E \"[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\""});
        BufferedReader br = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            hosts.add(line);
        }
        for (String host : hosts) {
            try {
                final SSHClient ssh = new SSHClient();
                ssh.addHostKeyVerifier(new HostKeyVerifier() {

                    @Override
                    public boolean verify(String hostname, int port, PublicKey key) {
                        return true;
                    }
                });
                ssh.connect(host);
                try {
                    ssh.authPassword("mewer12", pswd);
//                ssh.authPublickey(System.getProperty("user.name"));
                    final Session session = ssh.startSession();
                    try {
                        final Command cmd = session.exec("users");
                        String usrs = IOUtils.readFully(cmd.getInputStream()).toString();
                        System.out.println("Users " + usrs.length() + ": " + usrs);
                        cmd.join(5, TimeUnit.SECONDS);
                        //System.out.println("\n** exit status: " + cmd.getExitStatus());
                    } finally {
                        session.close();
                    }
                } finally {
                    ssh.disconnect();
                }
            } catch (Throwable e) {
                // System.err.println("Error on " + host);
            }
        }

        if (1 == 1) {
            return;
        }

        Color c = new Color(10);
        System.out.println(c.getClass().toString());
        if ("class java.awt.Color".equals(c.getClass().toString())) {
            System.out.println("Equal!");
        } else {
            System.out.println("Noooo!");
        }
        NVector[] ary = new NVector[5];
        System.out.println(ary[2].dims);
        System.out.println(3 / 4);
        CameraForm cf = new CameraForm(null, null, null);
        cf.show();

        NVector lpos = new NVector(new double[]{2, -3, 6});
        NVector dir = new NVector(new double[]{-4, -2, 4});
        ArrayList<NVector> bases = new ArrayList<NVector>();
        bases.add(new NVector(new double[]{-5, -5.5, 13}));
        bases.add(new NVector(new double[]{1, 2, -3}));
//        bases.add(new NVector(new double[]{-2, 0, -5}));
        NBasis basis0 = NBasis.vectorsToBases(bases);
        NVector iresult = Matrix.lineNPlaneIntersect(lpos, dir, basis0.origin, basis0.bases);
        System.out.println("progResult=" + iresult);
        System.out.println("line1=" + lineToMathematica(lpos, dir));
        System.out.println("plane1=" + planeToMathematica(basis0.origin, basis0.bases));

        Matrix mtx = new Matrix(3, 2);
        mtx.val = new double[][]{{2, 3}, {1, -1}, {3, 4}};
        mtx.ipRRowFormError();
        System.out.println(mtx);
        System.out.println(mtx.val[2][0]);
        if (1 == 1) {
            return;
        }
//        for (int i : forTest()) {
//            System.out.println(i);
//        }
        ArrayList<NVector> vectors = new ArrayList<NVector>();
        for (NVector v : new NVector[]{new NVector(new double[]{0, 0, 1}), new NVector(new double[]{1, 0, 1}), new NVector(new double[]{1, 1, 1})}) {
            vectors.add(v);
        }
        NBasis basis = NBasis.vectorsToBases(vectors);
        basis.makeStandardized();
        NVector a = new NVector(new double[]{0, 0, 0});
        NVector b = new NVector(new double[]{0, 0, 1});
        Matrix.lrvMult(basis.projection, a);
        Matrix.lrvMult(basis.projection, b);

        if (1 == 1) {
            return;
        }

        Random r = new Random();
        NVector pos0 = new NVector(new double[]{(20 * r.nextDouble()) - 10, (20 * r.nextDouble()) - 10, (20 * r.nextDouble()) - 10});
        NVector source0 = new NVector(new double[]{10 * (20 * r.nextDouble()) - 10, (20 * r.nextDouble()) - 10, (20 * r.nextDouble()) - 10});
        NVector target0 = new NVector(new double[]{(20 * r.nextDouble()) - 10, (20 * r.nextDouble()) - 10, (20 * r.nextDouble()) - 10});
//        System.out.println(NVector.angle(source0.minusB(pos0), target0.minusB(pos0)));
        //if (1 == 1) return;

//        NVector pos0 = new NVector(new double[]{1,2,3});
//        NVector source0 = new NVector(new double[]{3,3,1});
//        NVector target0 = new NVector(new double[]{3.1,2.9,2});
        System.out.println(NVector.angle(source0.minusB(pos0), target0.minusB(pos0)));

        NVector result0 = NVector.rotate(pos0, source0, target0, source0, NVector.angle(source0.minusB(pos0), target0.minusB(pos0)));
//        NVector result1 = NVector.rotate(new NVector(new double[]{0, 0}), source0.minusB(pos0), target0.minusB(pos0), source0.minusB(pos0), NVector.angle(source0.minusB(pos0), target0.minusB(pos0))).plusB(pos0);
        System.out.println(NVector.angle(source0.minusB(pos0), result0.minusB(pos0)));
        System.out.println(result0.minusB(pos0).copy().ipNormalize());
        System.out.println(target0.minusB(pos0).copy().ipNormalize());
        System.out.println("{" + pos0 + "," + source0 + "," + target0 + "," + result0 + "}");
        for (int i = 0; i < pos0.dims; i++) {
            result0.coords[i] = ((int) (result0.coords[i] * 1000)) / 1000d;
        }
        System.out.println(result0);
        if (1 == 1) {
            return;
        }
        //NVector reslt2 = NVector.rotateOrthBasis(source.minusB(pos), target.minusB(pos), source, angle)

        for (double mod = -2; mod <= 2; mod += 0.1) {
            NVector pos = new NVector(new double[]{mod, 0});
            NVector source = source0.plusB(pos);
            NVector target = target0.plusB(pos);
            NVector result = NVector.rotate(pos, source, target, source, NVector.angle(source.minusB(pos), target.minusB(pos)));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pos.dims; i++) {
                pos.coords[i] = ((int) (pos.coords[i] * 1000)) / 1000d;
            }
            for (int i = 0; i < pos.dims; i++) {
                source.coords[i] = ((int) (source.coords[i] * 1000)) / 1000d;
            }
            for (int i = 0; i < pos.dims; i++) {
                target.coords[i] = ((int) (target.coords[i] * 1000)) / 1000d;
            }
            for (int i = 0; i < pos.dims; i++) {
                result.coords[i] = ((int) (result.coords[i] * 1000)) / 1000d;
            }
            System.out.println((((int) (mod * 1000)) / 1000d) + " " + result);
        }

        if (1 == 1) {
            return;
        }

//        int sum = 0;
//        for (int i = 100; i <= 300; i += 2) {
//            sum += i;
//        }
//        System.out.println(sum);
//        if (1==1) {
//            return;
//        }

        //Random r = new Random();
        ArrayList<NPoint> blah = new ArrayList<NPoint>();
        blah.add(new NPoint(4));
        blah.add(new NPoint(4));
        blah.add(new NPoint(4));
        blah.add(new NPoint(4));
        for (NPoint p : blah) {
            if (r.nextBoolean()) {
                p = null;
            }
        }

        ArrayList<NPoint> list = new ArrayList<NPoint>();
        NPoint bucket = new NPoint(3);
        NVector basisPtA = new NVector(3);
        {
            double[] coords = {0, 0, 0};
            basisPtA.coords = coords;
        }
        bucket.pos = basisPtA;
        list.add(bucket);
        bucket = new NPoint(3);
        NVector basisPtB = new NVector(3);
        {
            double[] coords = {0, 0, 1};
            basisPtB.coords = coords;
        }
        bucket.pos = basisPtB;
        list.add(bucket);
        bucket = new NPoint(3);
        NVector basisPtC = new NVector(3);
        {
            double[] coords = {0, 1, 0};
            basisPtC.coords = coords;
        }
        bucket.pos = basisPtC;
        list.add(bucket);
        bucket = new NPoint(3);
        NVector basisPtD = new NVector(3);
        {
            double[] coords = {1, 0, 0};
            basisPtD.coords = coords;
        }
        bucket.pos = basisPtD;
        list.add(bucket);

        Engine.checkThinness(list, 4);

//        ArrayList<NPoint> list = new ArrayList<NPoint>();
//        NPoint bucket = new NPoint(4);
//        NVector basisPtA = new NVector(4);
//        {
//            double[] coords = {0, 0, 0, 0};
//            basisPtA.coords = coords;
//        }
//        bucket.pos = basisPtA;
//        list.add(bucket);
//        bucket = new NPoint(4);
//        NVector basisPtB = new NVector(4);
//        {
//            double[] coords = {0, 0, 0, 1};
//            basisPtB.coords = coords;
//        }
//        bucket.pos = basisPtB;
//        list.add(bucket);
//        bucket = new NPoint(4);
//        NVector basisPtC = new NVector(4);
//        {
//            double[] coords = {0, 0, 1, 0};
//            basisPtC.coords = coords;
//        }
//        bucket.pos = basisPtC;
//        list.add(bucket);
//        bucket = new NPoint(4);
//        NVector basisPtD = new NVector(4);
//        {
//            double[] coords = {0, 1, 0, 0};
//            basisPtD.coords = coords;
//        }
//        bucket.pos = basisPtD;
//        list.add(bucket);
//        bucket = new NPoint(4);
//        NVector basisPtE = new NVector(4);
//        {
//            double[] coords = {1, 0, 0, 0};
//            basisPtE.coords = coords;
//        }
//        bucket.pos = basisPtE;
//        list.add(bucket);
//        bucket = new NPoint(4);
//        
//        Engine.checkThinness(list, 1.3);

//        NVector basisPtA = new NVector(3);
//        {
//            double[] coords = {1, 1, 1};
//            basisPtA.coords = coords;
//        }
//        NVector basisPtB = new NVector(3);
//        {
//            double[] coords = {1, 3, 1};
//            basisPtB.coords = coords;
//        }
//        NVector basisPtC = new NVector(3);
//        {
//            double[] coords = {3, 1, 1};
//            basisPtC.coords = coords;
//        }
//        NVector v = new NVector(3);
//        {
//            double[] coords = {2, 2, 2};
//            v.coords = coords;
//        }
//        NVector res = NVector.rotate(basisPtA, basisPtB, basisPtC, v, Math.PI);
//        System.out.println(res);
//        
//        if (1 == 1) {
//            return;
//        }
//
//        ArrayList<NVector> bases = new ArrayList<NVector>();
//        ArrayList<NVector> vectors = new ArrayList<NVector>();
//        {
//            NVector bucket = new NVector(3);
//            double[] coords = {1, 1, 3};
//            bucket.coords = coords;
//            bases.add(bucket);
//        }
//        {
//            NVector bucket = new NVector(3);
//            double[] coords = {-1, 2, 2};
//            bucket.coords = coords;
//            bases.add(bucket);
//        }
//        {
//            NVector bucket = new NVector(3);
//            double[] coords = {-1, 8, 12};
//            bucket.coords = coords;
//            vectors.add(bucket);
//        }
//        NVector[] result = Matrix.ipTransformCoords(bases, vectors);
//        for (int i = 0; i < result.length; i++) {
//            System.out.println(result[i]);
//        }
//
//        if (1 == 1) {
//            return;
//        }
//
//        Random r = new Random();
//        double[][] bucket = {{3, -2, -3},
//            {3, 6, 6},
//            {-3, 6, -3}};
//        double[][] bucket2 = {{1, 0, 0},
//            {0, 1, 0},
//            {0, 0, 1}};
//        Matrix left = new Matrix(3, 3);
//        Matrix right = new Matrix(3, 3);
//        left.val = bucket;
//        right.val = bucket2;
//        System.out.println(Matrix.invert(left));
//        if (1 == 1) {
//            return;
//        }
//        Matrix m = Matrix.lrJoin(left, right);
////        m.val = bucket;
////        m.val[0][0] = 2;
////        m.val[1][0] = 1;
////        m.val[2][0] = 4;
////        m.val[0][1] = 5;
////        m.val[1][1] = -3;
////        m.val[2][1] = 1;
//        m.debug = true;
//        m.ipRRowForm();
//        Matrix[] split = Matrix.lrSplit(m);
//        right = split[1];
//        Matrix product = Matrix.lrMult(left, right);
//        System.out.println(product);
//        IntersectionResult result = m.solveIntersect();
//        System.out.println("{" + result.q12 + ", " + result.value + "}");
//        if (result.q12 == 1) {
//            System.out.println(((2 * result.value) - 1));
//            System.out.println(((5 * result.value) + 2));
//        } else if (result.q12 == 2) {
//            System.out.println(((-1 * result.value) + 3));
//            System.out.println(((3 * result.value) + 3));
//        } else {
//            System.err.println("Fail!");
//        }        
    }
}
