/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Matthew Ewer <Ewer.Matthew@gmail.com>
 */
public class RemoteWorkerIcon {

    public String address = null;
    public int port = 10700;
    public Socket socket = null;
    public DataInputStream dis = null;
    public DataOutputStream dos = null;
    public int status = -1;
    public String lastMessage = null;
    public boolean checkedOut = false;

    public RemoteWorkerIcon() {
    }

    public void updateWorker(Lattice lattice) throws IOException {
        updateWorker(lattice.toByteArray());
    }

    public void updateWorker(byte[] lattice) throws IOException {
        dos.writeInt(RemoteWorker.BOSS_READLATTICE);
        dos.write(lattice);
        dos.flush();
    }

    public Tensor<Color> renderFrame(int camNum, double dtl, double fov, int... picDims) throws IOException {
        if (camNum == -1) {
            //THINK Probably return an error.
            return null;
        }
        dos.writeInt(RemoteWorker.BOSS_TAKETASK);
        dos.writeInt(RemoteWorker.BOSS_RENDERFRAME);
        dos.writeInt(camNum);
        dos.writeDouble(dtl);
        dos.writeDouble(fov);
        dos.writeInt(picDims.length);
        for (int i = 0; i < picDims.length; i++) {
            dos.writeInt(picDims[i]);
        }
        dos.flush();
        //THINK Should I clear the queue before this?
        //asdf
        int response = dis.readInt();
        if (response != RemoteWorker.WORK_DONE) {
            //TODO Display error or something.
        }
        dos.writeInt(RemoteWorker.BOSS_ACKNOWLDEGE);
        dos.flush();
        response = dis.readInt();
        if (response != RemoteWorker.WORK_RESULTS) {
            //TODO Display error or something.
        }
        return Tensor.fromBytes(dis);
    }
    public static Object renderPace = new Object();

    /**
     * This one has a variation - it tries to prevent memory crunches by only taking and converting one image at a time.
     * @param camNum
     * @param dtl
     * @param imWidth
     * @param imHeight
     * @param picDims
     * @return
     * @throws IOException 
     */
    public BufferedImage renderFrameToImagePaced(int camNum, double dtl, double fov, int imWidth, int imHeight, int... picDims) throws IOException {
        if (camNum == -1) {
            //THINK Probably return an error.
            return null;
        }
        dos.writeInt(RemoteWorker.BOSS_TAKETASK);
        dos.writeInt(RemoteWorker.BOSS_RENDERFRAME);
        dos.writeInt(camNum);
        dos.writeDouble(dtl);
        dos.writeDouble(fov);
        dos.writeInt(picDims.length);
        for (int i = 0; i < picDims.length; i++) {
            dos.writeInt(picDims[i]);
        }
        dos.flush();
        //THINK Should I clear the queue before this?
        //asdf
        int response = dis.readInt();
        if (response != RemoteWorker.WORK_DONE) {
            //TODO Display error or something.
        }
        synchronized (renderPace) {
            dos.writeInt(RemoteWorker.BOSS_ACKNOWLDEGE);
            dos.flush();
            response = dis.readInt();
            if (response != RemoteWorker.WORK_RESULTS) {
                //TODO Display error or something.
            }
            Tensor<Color> result = Tensor.fromBytes(dis);
            BufferedImage image = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_3BYTE_BGR);
            for (int x = 0; x < imWidth; x++) {
                for (int y = 0; y < imHeight; y++) {
                    image.setRGB(x, y, result.get(x, y).getRGB());
                }
            }
            result = null;
            Runtime.getRuntime().gc();
            return image;
        }
    }
}
