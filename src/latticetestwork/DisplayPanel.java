/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 *
 * @author mewer12
 */
public class DisplayPanel extends JPanel {

    public static final double DEFscaleX = 200;
    public static final double DEFscaleY = 200;
    public static final double DEFtransX = 0;
    public static final double DEFtransY = 0;
    public double scaleX = DEFscaleX;
    public double scaleY = DEFscaleY;
    public double transX = DEFtransX;
    public double transY = DEFtransY;
    
    public Engine engine = null;
    public boolean defalt = true;
    
    public DisplayPanel(boolean defalt) {
        this.defalt = defalt;
    }
    
    public boolean clickRender = false;
    
    @Override
    protected void paintComponent(Graphics g1) {
        super.paintComponent(g1);
        if (defalt) {
            if (engine != null) {
                Graphics2D g = (Graphics2D)g1;
                engine.render(g, 0, this.getWidth(), this.getHeight(), transX, transY, scaleX, scaleY, clickRender);
                clickRender = false;
            }
        }
    }    
}
