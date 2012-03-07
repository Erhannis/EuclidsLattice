/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.SocketFactory;

/**
 *
 * @author Matthew Ewer <Ewer.Matthew@gmail.com>
 */
public class MobBoss {

    public static final int DEFAULT_PORT = 10700;
    public ArrayList<RemoteWorkerIcon> workers = null;
    public Object waitingForWorker = new Object();
    public Lattice lattice = null;

    public MobBoss(Lattice lattice) {
        this.workers = new ArrayList<RemoteWorkerIcon>();
        this.lattice = lattice;
    }

    public RemoteWorkerIcon recruitWorker(String address) throws UnknownHostException, IOException {
        return recruitWorker(address, DEFAULT_PORT);
    }

    public RemoteWorkerIcon recruitWorker(String address, int port) throws UnknownHostException, IOException {
        RemoteWorkerIcon worker = new RemoteWorkerIcon();
        worker.address = address;
        worker.port = port;
        Socket socket = SocketFactory.getDefault().createSocket(address, port);
        socket.setKeepAlive(true);
        worker.socket = socket;
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        worker.dis = dis;
        worker.dos = dos;
        dos.writeInt(RemoteWorker.BOSS_QUERY);
        dos.flush();
        //THINK Maybe turn this into a RemoteWorkerIcon call.
        try {
            int count = 0;
            while ((dis.available() == 0) && count++ < 100) {
                Thread.sleep(10);
            }
            if (dis.available() > 0) {
                int response = dis.readInt();
                if (response == RemoteWorker.WORK_READY) {
                    dos.writeInt(RemoteWorker.BOSS_SETPRIORITY);
                    dos.writeInt(Thread.MIN_PRIORITY);
                    worker.status = RemoteWorker.WORK_READY;
                    worker.lastMessage = "Waiting";
                    workers.add(worker);
                    return worker;
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(MobBoss.class.getName()).log(Level.SEVERE, null, ex);
        }
        dis.close();
        dos.close();
        socket.close();
        return null;
    }

    public void disconnectAll() {
        for (RemoteWorkerIcon w : workers) {
            if (w != null) {
                try {
                    w.dos.writeInt(RemoteWorker.BOSS_DISCONNECT);
                    w.dos.flush();
                } catch (Throwable anything) {
                    System.err.println(anything.toString());
                }
                if (w.dis != null) {
                    try {
                        w.dis.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MobBoss.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (w.dos != null) {
                    try {
                        w.dos.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MobBoss.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (w.socket != null) {
                    try {
                        w.socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(MobBoss.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    /**
     * Requests a worker node to perform calculations and stuff.
     * Remember to return it when you're done.
     * @return 
     */
    public RemoteWorkerIcon requestWorker() {
        RemoteWorkerIcon worker = null;
        synchronized (workers) {
            for (RemoteWorkerIcon w : workers) {
                if (!w.checkedOut) {
                    w.checkedOut = true;
                    worker = w;
                    break;
                }
            }
        }
        if (worker != null) {
            return worker;
        } else {
            synchronized (waitingForWorker) {
                while (worker == null) {
                    try {
                        waitingForWorker.wait(2000);
                    } catch (InterruptedException ex) {
                    }
                    synchronized (workers) {
                        for (RemoteWorkerIcon w : workers) {
                            if (!w.checkedOut) {
                                w.checkedOut = true;
                                worker = w;
                                break;
                            }
                        }
                    }
                }
                return worker;
            }
        }
    }

    public void returnWorker(RemoteWorkerIcon worker) {
        worker.checkedOut = false;
    }
//    public Tensor<Color> dFrameRender(Camera cam, double dtl, int... picDims) {
//        int camNum = lattice.cameras.indexOf(cam);
//        if (camNum == -1) {
//            //THINK Probably return an error.
//            return null;
//        }
//        //double dtl = dis.readDouble();
//
//        int picDimsCount = dis.readInt();
//        int[] picDims = new int[picDimsCount];
//        for (int i = 0; i < picDims.length; i++) {
//            picDims[i] = dis.readInt();
//        }
//
//    }
//
//    public Tensor<Color> dFrameRender(Camera cam, double dtl, int... picDims) {
//        int camNum = lattice.cameras.indexOf(cam);
//        if (camNum == -1) {
//            //THINK Probably return an error.
//            return null;
//        }
//        //double dtl = dis.readDouble();
//
//        int picDimsCount = dis.readInt();
//        int[] picDims = new int[picDimsCount];
//        for (int i = 0; i < picDims.length; i++) {
//            picDims[i] = dis.readInt();
//        }
//
//    }
}
