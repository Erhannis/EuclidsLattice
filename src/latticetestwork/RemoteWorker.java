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
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.net.ServerSocketFactory;
import org.jdesktop.application.Application;

/**
 *
 * @author Matthew Ewer <Ewer.Matthew@gmail.com>
 */
public class RemoteWorker {

    public ServerSocket serverSocket = null;
    public Socket socket = null;
    public DataOutputStream dos = null;
    public DataInputStream dis = null;
    public Lattice lattice = null;

    public class QuittingException extends Exception {
    }

    public RemoteWorker(String[] args) throws IOException {
        String port = null;
        if (args.length > 0) {
            port = args[0];
        } else {
            System.out.println("Usage: RemoteWorker port");
            System.out.println("Wherein \"port\" is the port on which to listen for work orders.");
            return;
        }
        serverSocket = ServerSocketFactory.getDefault().createServerSocket(Integer.valueOf(port));
        socket = serverSocket.accept();
        socket.setKeepAlive(true);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        boolean done = false;
        while (!done) {
            try {
                readCommand(dis, dos);
            } catch (QuittingException q) {
                System.out.println("Quitting.");
                done = true;
            }
        }
        socket.close();
        serverSocket.close();
    }
    public static final boolean ASK_CONFIRMATION = false;
    // Worker signals
    //Report
    //  Ready
    //  Working
    //  Task Done
    //  Error Message
    //  Status Message
    //Query Status
    //Sending Results
    public static final int WORK_READY = 1;
    public static final int WORK_WORKING = 9;
    public static final int WORK_DONE = 10;
    public static final int WORK_ERROR = 11;
    public static final int WORK_MESSAGE = 12;
    public static final int WORK_QUERY = 13;
    public static final int WORK_RESULTS = 14;
    // Boss signals
    //Query status
    //Boss Ready
    //Set Priority
    //Read lattice
    //Give Task
    //  Render Block
    //  (Render Frame?)
    //  (Calc Forces)
    //  (Calc Move Object)
    // (
    //Change Thing
    //  Point
    //  Face
    //  Cell
    // )
    //(Cancel)
    //Quit
    public static final int BOSS_QUERY = 2;
    public static final int BOSS_READY = 8;
    public static final int BOSS_SETPRIORITY = 6;
    public static final int BOSS_READLATTICE = 3;
    public static final int BOSS_TAKETASK = 4;
    public static final int BOSS_RENDERBLOCK = 7;
    public static final int BOSS_RENDERFRAME = 15;
    public static final int BOSS_DISCONNECT = 17;
    public static final int BOSS_QUITTINGTIME = 5;
    public static final int BOSS_ACKNOWLDEGE = 16;

    public void readCommand(DataInputStream dis, DataOutputStream dos) throws QuittingException {
        try {
            int command = dis.readInt();
            switch (command) {
                case BOSS_QUERY:
                    dos.writeInt(WORK_READY);
                    dos.flush();
                    break;
                case BOSS_SETPRIORITY:
                    Thread.currentThread().setPriority(dis.readInt());
                    break;
                case BOSS_READLATTICE:
                    lattice = LatticeForm.loadLattice(dis);
                    lattice = lattice;
                    break;
                case BOSS_TAKETASK:
                    switch (dis.readInt()) {
                        case BOSS_RENDERBLOCK:
                            break;
                        case BOSS_RENDERFRAME:
                            int camNum = dis.readInt();
                            double dtl = dis.readDouble();
                            int picDimsCount = dis.readInt();
                            int[] picDims = new int[picDimsCount];
                            for (int i = 0; i < picDims.length; i++) {
                                picDims[i] = dis.readInt();
                            }
                            System.out.println("CamPos: " + lattice.cameras.get(camNum).pos);
                            System.out.println("CamOrient:");
                            for (int i = 0; i < lattice.cameras.get(camNum).orientation.length; i++) {
                                System.out.println(lattice.cameras.get(camNum).orientation[i]);
                            }
                            Tensor<Color> result = lattice.cameras.get(camNum).aRender(dtl, picDims);
                            dos.writeInt(WORK_DONE);
                            dos.flush();
                            if (dis.readInt() == BOSS_ACKNOWLDEGE) {
                                dos.writeInt(WORK_RESULTS);
                                result.toBytes(dos);
                                dos.flush();
                            } else {
                                //TODO Error?
                            }
                            break;
                        default:
                            throw new Exception("Unrecognized task type!");
                    }
                    break;
                case BOSS_DISCONNECT:
                    //TODO Disconnect.
                    throw new QuittingException();
                case BOSS_QUITTINGTIME:
                    throw new QuittingException();
                default:
                    throw new Exception("Unrecognized signal: " + command);
            }
        } catch (Exception ex) {
            if (ex instanceof QuittingException) {
                throw (QuittingException)ex;
            }
            try {
                Logger.getLogger(RemoteWorker.class.getName()).log(Level.SEVERE, null, ex);
                dos.writeInt(WORK_ERROR);
                dos.writeUTF(ex.toString());
                dos.flush();
            } catch (IOException ex1) {
                Logger.getLogger(RemoteWorker.class.getName()).log(Level.SEVERE, null, ex1);
                throw new RuntimeException();
            }
        }
    }

    /**
     * Let's see, parameters:
     * port - The port on which to listen for work orders.
     * @param args
     */
    public static void main(String[] args) throws IOException {
        RemoteWorker worker = new RemoteWorker(args);
    }

    public static String statusCodeToString(int code) {
        switch (code) {
            case WORK_READY:
                return "Ready";
            case WORK_WORKING:
                return "Working";
            case WORK_DONE:
                return "Task Done";
            case WORK_ERROR:
                return "Error";
            default:
                return "Unknown";
        }
    }
}
