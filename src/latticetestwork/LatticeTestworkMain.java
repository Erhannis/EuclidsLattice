/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package latticetestwork;

/**
 *
 * @author erhannis
 */
public class LatticeTestworkMain {

    public LatticeTestworkView ltv = null;

    public LatticeTestworkMain() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                if (ltv != null && ltv.mobBoss != null) {
                    ltv.mobBoss.disconnectAll();
                }
            }
        }));
        ltv = new LatticeTestworkView(this);
        ltv.setVisible(true);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LatticeTestworkMain main = new LatticeTestworkMain();
    }
}
