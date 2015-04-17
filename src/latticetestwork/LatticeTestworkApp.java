/*
 * LatticeTestworkApp.java
 */

package latticetestwork;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class LatticeTestworkApp extends SingleFrameApplication {

    public LatticeTestworkView ltv = null;
    /**
     * At startup create and show the main frame of the application.
     */
    @Override
    protected void startup() {
        ltv = new LatticeTestworkView(this);
        show(ltv);
    }

    @Override
    protected void shutdown() {
        if (ltv.mobBoss != null) {
            ltv.mobBoss.disconnectAll();
        }
        super.shutdown();
    }
    
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of LatticeTestworkApp
     */
    public static LatticeTestworkApp getApplication() {
        return Application.getInstance(LatticeTestworkApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(LatticeTestworkApp.class, args);
    }
}
