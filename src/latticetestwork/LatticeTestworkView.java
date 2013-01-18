/*
 * LatticeTestworkView.java
 */
package latticetestwork;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * The application's main frame.
 */
public class LatticeTestworkView extends FrameView {

//            todo
//\/            make zoom in more detailed
//\/            widen viewscreen
//\/            add hide incomplete faces
//            check sphere code
//\/            reset start zoom
//            add <auto-form externally completed cells>
//\/            add click selection
//\/            click-point account for stereo
//            add thinness and stick lengths to status
//            optimize render code
//                store sticks and completion status upon change
//                draw sticks, not cells or faces.
//                sheesh, could probably cut rendering in quarters.
    public DisplayPanel dp = null;

    public LatticeTestworkView(SingleFrameApplication app) {
        super(app);

        initComponents();
        spinDims.getModel().setValue(4);
        spinLatticeDims.getModel().setValue(2);
        dp = new DisplayPanel(true);
//        panelDisplay.add(dp);
//        dp.setSize(500, 500);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panelDisplay);
        panelDisplay.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(dp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(dp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        dp.addMouseListener(new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                if (dp.engine != null) {
                    if (e.getButton() == 1) {
                        dp.engine.taggedCell = null;
                        dp.engine.taggedFace = null;
                        dp.engine.highlightedCells.clear();
                        dp.engine.highlighteds.clear();
                        dp.engine.clickPoint(e.getPoint(), ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0), ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0), ((e.getModifiers() & ActionEvent.ALT_MASK) != 0), e.getButton());
                        dp.repaint();
                    } else if (e.getButton() == 3) {
                        JPopupMenu m = new JPopupMenu();
                        JMenuItem itemCircumsphere = m.add("Circumsphere");
                        JMenuItem itemPlusStats = m.add("+Stats");
                        JMenuItem itemTagCell = m.add("Tag cell");
                        JMenuItem itemDistance = m.add("Distance");
                        JMenuItem itemTagFace = m.add("Tag face");
                        JMenuItem itemShowFaceCells = m.add("Show face's cells");
                        JMenuItem itemNearest10 = m.add("Nearest 10 pts");
                        JMenuItem itemCoords = m.add("Coords");
                        if (dp.engine.chosens.size() != 1) {
                            itemNearest10.setEnabled(false);
                            itemCoords.setEnabled(false);
                        }
                        if (dp.engine.chosens.size() != 2) {
                            itemDistance.setEnabled(false);
                        }
                        if (dp.engine.chosens.size() != (dp.engine.lattice.internalDims + 1)) {
                            itemCircumsphere.setEnabled(false);
                            itemPlusStats.setEnabled(false);
                            itemTagCell.setEnabled(false);
                        }
                        if (dp.engine.chosens.size() != (dp.engine.lattice.internalDims)) {
                            itemShowFaceCells.setEnabled(false);
                            itemTagFace.setEnabled(false);
                        }
                        itemCircumsphere.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                dp.engine.highlighteds.clear();
                                dp.engine.highlighteds.addAll(dp.engine.findCircleContentsALT(dp.engine.chosens, false, true));
                                dp.repaint();
                            }
                        });
                        itemPlusStats.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                dp.engine.highlighteds.clear();
                                dp.engine.highlighteds.addAll(dp.engine.findCircleContentsALT(dp.engine.chosens, false, true));
                                dp.repaint();
                                checkCell(dp.engine.permacenter, dp.engine.permaradius, dp.engine.chosens.toArray(new NPoint[0]), "EX: " + dp.engine.highlighteds.size());
                            }
                        });
                        itemTagCell.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                NCell bucket = new NCell(dp.engine.lattice.dims, dp.engine.lattice.internalDims);
                                for (int i = 0; i < bucket.points.length; i++) {
                                    bucket.points[i] = dp.engine.chosens.get(i);
                                }
                                dp.engine.taggedCell = bucket;
                            }
                        });
                        itemDistance.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                JOptionPane.showMessageDialog(mainPanel, "D: " + engine.chosens.get(0).pos.dist(engine.chosens.get(1).pos));
                            }
                        });
                        itemTagFace.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                NFace bucket = new NFace(dp.engine.lattice.dims, dp.engine.lattice.internalDims);
                                for (int i = 0; i < bucket.points.length; i++) {
                                    bucket.points[i] = dp.engine.chosens.get(i);
                                }
                                dp.engine.taggedFace = bucket;
                            }
                        });
                        itemShowFaceCells.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                dp.engine.highlightedCells.clear();
                                NFace bucket = new NFace(dp.engine.lattice.dims, dp.engine.lattice.internalDims);
                                for (int i = 0; i < bucket.points.length; i++) {
                                    bucket.points[i] = dp.engine.chosens.get(i);
                                }
                                for (NFace f : dp.engine.lattice.faces) {
                                    if (bucket.equivalent(f)) {
                                        if (f.cellA != null) {
                                            System.out.println("Selecting cellA");
                                            dp.engine.highlightedCells.add(f.cellA);
                                        }
                                        if (f.cellB != null) {
                                            System.out.println("Selecting cellB");
                                            dp.engine.highlightedCells.add(f.cellB);
                                        }
//                                        break;
                                    }
                                }
                                dp.repaint();
                            }
                        });
                        itemNearest10.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                //TODO Inefficient.
                                final NPoint pt = dp.engine.chosens.get(0);
                                //HashMap<NPoint, Double> dists = new HashMap<NPoint, Double>();
                                ArrayList<NPoint> points = new ArrayList<NPoint>();
                                for (NPoint p : dp.engine.lattice.points) {
                                    if (p != pt) {
                                        //dists.put(p, p.pos.dist(pt.pos));
                                        points.add(p);
                                    }
                                }
                                Collections.sort(points, new Comparator<NPoint>() {

                                    public int compare(NPoint o1, NPoint o2) {
                                        double d1 = o1.dist(pt);
                                        double d2 = o2.dist(pt);
                                        return Double.compare(d1, d2);
                                    }
                                });
                                StringBuilder sb = new StringBuilder();
                                dp.engine.highlighteds.clear();
                                for (int i = 0; i < 10; i++) {
                                    if (i >= points.size()) {
                                        break;
                                    }
                                    sb.append(i + ": " + points.get(i).dist(pt) + "\n");
                                    dp.engine.highlighteds.add(points.get(i));
                                }
                                JOptionPane.showMessageDialog(null, sb.toString());
                                dp.repaint();
                            }
                        });
                        itemCoords.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                //TODO Inefficient.
                                final NPoint pt = dp.engine.chosens.get(0);
                                StringBuilder sb = new StringBuilder();
                                for (int i = 0; i < pt.pos.dims; i++) {
                                    sb.append(i + ": " + pt.pos.coords[i] + "\n");
                                }
                                JOptionPane.showMessageDialog(null, sb.toString());
                            }
                        });
                        m.show(dp, e.getX(), e.getY());

//                    dp.engine.addTriangle();
//                    dp.repaint();
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseReleased(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        panelDisplay.revalidate();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = LatticeTestworkApp.getApplication().getMainFrame();
            aboutBox = new LatticeTestworkAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        LatticeTestworkApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        panelDisplay = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        boxShowCrystallization = new javax.swing.JCheckBox();
        btnSwapHands = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        btnJoggle = new javax.swing.JButton();
        editSearchRadius = new javax.swing.JTextField();
        btnShear1 = new javax.swing.JButton();
        btnCrystallize = new javax.swing.JButton();
        btnShearN = new javax.swing.JButton();
        btnFull = new javax.swing.JButton();
        boxAutoNCorners = new javax.swing.JCheckBox();
        btnBind = new javax.swing.JButton();
        btnSeed = new javax.swing.JButton();
        boxSkipHardFaces = new javax.swing.JCheckBox();
        btnStatus = new javax.swing.JButton();
        btnRepel100 = new javax.swing.JButton();
        btnBind100 = new javax.swing.JButton();
        radio32TriCube = new javax.swing.JRadioButton();
        editStereoDelta = new javax.swing.JTextField();
        btnBind1000 = new javax.swing.JButton();
        editMaxThinness = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btn34Preset = new javax.swing.JButton();
        editDonutCircumfrence = new javax.swing.JTextField();
        editJoggleScale = new javax.swing.JTextField();
        btnFinishPrep = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        btnPlaceNCube = new javax.swing.JButton();
        boxStereo = new javax.swing.JCheckBox();
        editStereoDegrees = new javax.swing.JTextField();
        btnPlaceCamera = new javax.swing.JButton();
        btnCalcProperties = new javax.swing.JButton();
        btnPlaceNDonut = new javax.swing.JButton();
        radio22Tri = new javax.swing.JRadioButton();
        radio43Tri = new javax.swing.JRadioButton();
        radioManual = new javax.swing.JRadioButton();
        btnRepel = new javax.swing.JButton();
        editNumPoints = new javax.swing.JTextField();
        spinLatticeDims = new javax.swing.JSpinner();
        btnClear = new javax.swing.JButton();
        spinDims = new javax.swing.JSpinner();
        btnPlacePoints = new javax.swing.JButton();
        radio33Tri = new javax.swing.JRadioButton();
        radio32Tri = new javax.swing.JRadioButton();
        btnCheckCell = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        editContainmentFudgeValue = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        editMinAngle = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        editMaxVolume = new javax.swing.JTextField();
        boxSlowCrystallization = new javax.swing.JCheckBox();
        jLabel7 = new javax.swing.JLabel();
        editMinVolume = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        editMinLength = new javax.swing.JTextField();
        editMaxLength = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        modeGroup = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        jSplitPane1.setDividerLocation(489);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        panelDisplay.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelDisplay.setName("panelDisplay"); // NOI18N

        javax.swing.GroupLayout panelDisplayLayout = new javax.swing.GroupLayout(panelDisplay);
        panelDisplay.setLayout(panelDisplayLayout);
        panelDisplayLayout.setHorizontalGroup(
            panelDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 823, Short.MAX_VALUE)
        );
        panelDisplayLayout.setVerticalGroup(
            panelDisplayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1072, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(panelDisplay);

        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(latticetestwork.LatticeTestworkApp.class).getContext().getResourceMap(LatticeTestworkView.class);
        boxShowCrystallization.setText(resourceMap.getString("boxShowCrystallization.text")); // NOI18N
        boxShowCrystallization.setToolTipText(resourceMap.getString("boxShowCrystallization.toolTipText")); // NOI18N
        boxShowCrystallization.setName("boxShowCrystallization"); // NOI18N

        btnSwapHands.setText(resourceMap.getString("btnSwapHands.text")); // NOI18N
        btnSwapHands.setToolTipText(resourceMap.getString("btnSwapHands.toolTipText")); // NOI18N
        btnSwapHands.setName("btnSwapHands"); // NOI18N
        btnSwapHands.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSwapHandsActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        btnJoggle.setText(resourceMap.getString("btnJoggle.text")); // NOI18N
        btnJoggle.setToolTipText(resourceMap.getString("btnJoggle.toolTipText")); // NOI18N
        btnJoggle.setName("btnJoggle"); // NOI18N
        btnJoggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJoggleActionPerformed(evt);
            }
        });

        editSearchRadius.setText(resourceMap.getString("editSearchRadius.text")); // NOI18N
        editSearchRadius.setToolTipText(resourceMap.getString("editSearchRadius.toolTipText")); // NOI18N
        editSearchRadius.setName("editSearchRadius"); // NOI18N

        btnShear1.setText(resourceMap.getString("btnShear1.text")); // NOI18N
        btnShear1.setToolTipText(resourceMap.getString("btnShear1.toolTipText")); // NOI18N
        btnShear1.setName("btnShear1"); // NOI18N
        btnShear1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShear1ActionPerformed(evt);
            }
        });

        btnCrystallize.setText(resourceMap.getString("btnCrystallize.text")); // NOI18N
        btnCrystallize.setName("btnCrystallize"); // NOI18N
        btnCrystallize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCrystallizeActionPerformed(evt);
            }
        });

        btnShearN.setText(resourceMap.getString("btnShearN.text")); // NOI18N
        btnShearN.setToolTipText(resourceMap.getString("btnShearN.toolTipText")); // NOI18N
        btnShearN.setName("btnShearN"); // NOI18N
        btnShearN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShearNActionPerformed(evt);
            }
        });

        btnFull.setText(resourceMap.getString("btnFull.text")); // NOI18N
        btnFull.setName("btnFull"); // NOI18N
        btnFull.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFullActionPerformed(evt);
            }
        });

        boxAutoNCorners.setText(resourceMap.getString("boxAutoNCorners.text")); // NOI18N
        boxAutoNCorners.setName("boxAutoNCorners"); // NOI18N

        btnBind.setText(resourceMap.getString("btnBind.text")); // NOI18N
        btnBind.setName("btnBind"); // NOI18N
        btnBind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBindActionPerformed(evt);
            }
        });

        btnSeed.setText(resourceMap.getString("btnSeed.text")); // NOI18N
        btnSeed.setToolTipText(resourceMap.getString("btnSeed.toolTipText")); // NOI18N
        btnSeed.setName("btnSeed"); // NOI18N
        btnSeed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeedActionPerformed(evt);
            }
        });

        boxSkipHardFaces.setSelected(true);
        boxSkipHardFaces.setText(resourceMap.getString("boxSkipHardFaces.text")); // NOI18N
        boxSkipHardFaces.setName("boxSkipHardFaces"); // NOI18N

        btnStatus.setText(resourceMap.getString("btnStatus.text")); // NOI18N
        btnStatus.setName("btnStatus"); // NOI18N
        btnStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStatusActionPerformed(evt);
            }
        });

        btnRepel100.setText(resourceMap.getString("btnRepel100.text")); // NOI18N
        btnRepel100.setName("btnRepel100"); // NOI18N
        btnRepel100.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepel100ActionPerformed(evt);
            }
        });

        btnBind100.setText(resourceMap.getString("btnBind100.text")); // NOI18N
        btnBind100.setName("btnBind100"); // NOI18N
        btnBind100.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBind100ActionPerformed(evt);
            }
        });

        modeGroup.add(radio32TriCube);
        radio32TriCube.setText(resourceMap.getString("radio32TriCube.text")); // NOI18N
        radio32TriCube.setName("radio32TriCube"); // NOI18N

        editStereoDelta.setText(resourceMap.getString("editStereoDelta.text")); // NOI18N
        editStereoDelta.setToolTipText(resourceMap.getString("editStereoDelta.toolTipText")); // NOI18N
        editStereoDelta.setName("editStereoDelta"); // NOI18N
        editStereoDelta.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                editStereoDeltaPropertyChange(evt);
            }
        });

        btnBind1000.setText(resourceMap.getString("btnBind1000.text")); // NOI18N
        btnBind1000.setName("btnBind1000"); // NOI18N
        btnBind1000.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBind1000ActionPerformed(evt);
            }
        });

        editMaxThinness.setText(resourceMap.getString("editMaxThinness.text")); // NOI18N
        editMaxThinness.setToolTipText(resourceMap.getString("editMaxThinness.toolTipText")); // NOI18N
        editMaxThinness.setName("editMaxThinness"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setToolTipText(resourceMap.getString("jLabel2.toolTipText")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        btn34Preset.setText(resourceMap.getString("btn34Preset.text")); // NOI18N
        btn34Preset.setName("btn34Preset"); // NOI18N
        btn34Preset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn34PresetActionPerformed(evt);
            }
        });

        editDonutCircumfrence.setText(resourceMap.getString("editDonutCircumfrence.text")); // NOI18N
        editDonutCircumfrence.setToolTipText(resourceMap.getString("editDonutCircumfrence.toolTipText")); // NOI18N
        editDonutCircumfrence.setName("editDonutCircumfrence"); // NOI18N

        editJoggleScale.setText(resourceMap.getString("editJoggleScale.text")); // NOI18N
        editJoggleScale.setToolTipText(resourceMap.getString("editJoggleScale.toolTipText")); // NOI18N
        editJoggleScale.setName("editJoggleScale"); // NOI18N

        btnFinishPrep.setText(resourceMap.getString("btnFinishPrep.text")); // NOI18N
        btnFinishPrep.setName("btnFinishPrep"); // NOI18N
        btnFinishPrep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishPrepActionPerformed(evt);
            }
        });

        jCheckBox1.setText(resourceMap.getString("jCheckBox1.text")); // NOI18N
        jCheckBox1.setName("jCheckBox1"); // NOI18N

        btnPlaceNCube.setText(resourceMap.getString("btnPlaceNCube.text")); // NOI18N
        btnPlaceNCube.setName("btnPlaceNCube"); // NOI18N
        btnPlaceNCube.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlaceNCubeActionPerformed(evt);
            }
        });

        boxStereo.setSelected(true);
        boxStereo.setText(resourceMap.getString("boxStereo.text")); // NOI18N
        boxStereo.setName("boxStereo"); // NOI18N
        boxStereo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxStereoActionPerformed(evt);
            }
        });

        editStereoDegrees.setText(resourceMap.getString("editStereoDegrees.text")); // NOI18N
        editStereoDegrees.setToolTipText(resourceMap.getString("editStereoDegrees.toolTipText")); // NOI18N
        editStereoDegrees.setName("editStereoDegrees"); // NOI18N
        editStereoDegrees.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                editStereoDegreesPropertyChange(evt);
            }
        });

        btnPlaceCamera.setText(resourceMap.getString("btnPlaceCamera.text")); // NOI18N
        btnPlaceCamera.setToolTipText(resourceMap.getString("btnPlaceCamera.toolTipText")); // NOI18N
        btnPlaceCamera.setName("btnPlaceCamera"); // NOI18N
        btnPlaceCamera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlaceCameraActionPerformed(evt);
            }
        });

        btnCalcProperties.setText(resourceMap.getString("btnCalcProperties.text")); // NOI18N
        btnCalcProperties.setName("btnCalcProperties"); // NOI18N
        btnCalcProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcPropertiesActionPerformed(evt);
            }
        });

        btnPlaceNDonut.setText(resourceMap.getString("btnPlaceNDonut.text")); // NOI18N
        btnPlaceNDonut.setToolTipText(resourceMap.getString("btnPlaceNDonut.toolTipText")); // NOI18N
        btnPlaceNDonut.setName("btnPlaceNDonut"); // NOI18N
        btnPlaceNDonut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlaceNDonutActionPerformed(evt);
            }
        });

        modeGroup.add(radio22Tri);
        radio22Tri.setSelected(true);
        radio22Tri.setText(resourceMap.getString("radio22Tri.text")); // NOI18N
        radio22Tri.setName("radio22Tri"); // NOI18N

        modeGroup.add(radio43Tri);
        radio43Tri.setText(resourceMap.getString("radio43Tri.text")); // NOI18N
        radio43Tri.setName("radio43Tri"); // NOI18N

        modeGroup.add(radioManual);
        radioManual.setText(resourceMap.getString("radioManual.text")); // NOI18N
        radioManual.setName("radioManual"); // NOI18N

        btnRepel.setText(resourceMap.getString("btnRepel.text")); // NOI18N
        btnRepel.setName("btnRepel"); // NOI18N
        btnRepel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRepelActionPerformed(evt);
            }
        });

        editNumPoints.setText(resourceMap.getString("editNumPoints.text")); // NOI18N
        editNumPoints.setName("editNumPoints"); // NOI18N

        spinLatticeDims.setName("spinLatticeDims"); // NOI18N

        btnClear.setText(resourceMap.getString("btnClear.text")); // NOI18N
        btnClear.setName("btnClear"); // NOI18N
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        spinDims.setName("spinDims"); // NOI18N

        btnPlacePoints.setText(resourceMap.getString("btnPlacePoints.text")); // NOI18N
        btnPlacePoints.setName("btnPlacePoints"); // NOI18N
        btnPlacePoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlacePointsActionPerformed(evt);
            }
        });

        modeGroup.add(radio33Tri);
        radio33Tri.setText(resourceMap.getString("radio33Tri.text")); // NOI18N
        radio33Tri.setName("radio33Tri"); // NOI18N

        modeGroup.add(radio32Tri);
        radio32Tri.setText(resourceMap.getString("radio32Tri.text")); // NOI18N
        radio32Tri.setName("radio32Tri"); // NOI18N

        btnCheckCell.setText(resourceMap.getString("btnCheckCell.text")); // NOI18N
        btnCheckCell.setToolTipText(resourceMap.getString("btnCheckCell.toolTipText")); // NOI18N
        btnCheckCell.setName("btnCheckCell"); // NOI18N
        btnCheckCell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckCellActionPerformed(evt);
            }
        });

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        editContainmentFudgeValue.setText(resourceMap.getString("editContainmentFudgeValue.text")); // NOI18N
        editContainmentFudgeValue.setToolTipText(resourceMap.getString("editContainmentFudgeValue.toolTipText")); // NOI18N
        editContainmentFudgeValue.setName("editContainmentFudgeValue"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        editMinAngle.setText(resourceMap.getString("editMinAngle.text")); // NOI18N
        editMinAngle.setToolTipText(resourceMap.getString("editMinAngle.toolTipText")); // NOI18N
        editMinAngle.setName("editMinAngle"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        editMaxVolume.setText(resourceMap.getString("editMaxVolume.text")); // NOI18N
        editMaxVolume.setToolTipText(resourceMap.getString("editMaxVolume.toolTipText")); // NOI18N
        editMaxVolume.setName("editMaxVolume"); // NOI18N

        boxSlowCrystallization.setText(resourceMap.getString("boxSlowCrystallization.text")); // NOI18N
        boxSlowCrystallization.setToolTipText(resourceMap.getString("boxSlowCrystallization.toolTipText")); // NOI18N
        boxSlowCrystallization.setName("boxSlowCrystallization"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        editMinVolume.setText(resourceMap.getString("editMinVolume.text")); // NOI18N
        editMinVolume.setToolTipText(resourceMap.getString("editMinVolume.toolTipText")); // NOI18N
        editMinVolume.setName("editMinVolume"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        editMinLength.setText(resourceMap.getString("editMinLength.text")); // NOI18N
        editMinLength.setToolTipText(resourceMap.getString("editMinLength.toolTipText")); // NOI18N
        editMinLength.setName("editMinLength"); // NOI18N

        editMaxLength.setText(resourceMap.getString("editMaxLength.text")); // NOI18N
        editMaxLength.setToolTipText(resourceMap.getString("editMaxLength.toolTipText")); // NOI18N
        editMaxLength.setName("editMaxLength"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(radio22Tri)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn34Preset))
                        .addComponent(radio32Tri)
                        .addComponent(radio33Tri)
                        .addComponent(btnClear)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnRepel)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnRepel100))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnBind)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnBind100)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnBind1000))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnSeed)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnCheckCell))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnCrystallize)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnFull)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnStatus))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(boxShowCrystallization)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(boxSlowCrystallization))
                        .addComponent(jLabel1)
                        .addComponent(radio32TriCube)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnPlaceNCube)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnShear1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnShearN))
                        .addComponent(boxAutoNCorners)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnPlacePoints)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(editNumPoints, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnPlaceNDonut)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(editDonutCircumfrence, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCheckBox1))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(radioManual)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(spinDims))
                                .addComponent(radio43Tri, javax.swing.GroupLayout.Alignment.LEADING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(spinLatticeDims, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnSwapHands)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(btnJoggle)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(editJoggleScale, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(editSearchRadius, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(boxSkipHardFaces))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(editMaxThinness, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(editContainmentFudgeValue, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addGap(13, 13, 13)))
                    .addComponent(jLabel4)
                    .addComponent(btnCalcProperties)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnFinishPrep)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnPlaceCamera))
                    .addComponent(editMinAngle, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(boxStereo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editStereoDegrees, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editStereoDelta, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(editMinVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(editMinLength, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(editMaxVolume, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(editMaxLength, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(170, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radio22Tri)
                    .addComponent(btn34Preset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio32Tri)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio32TriCube)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio33Tri)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radio43Tri)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioManual)
                    .addComponent(spinDims, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinLatticeDims, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnClear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPlacePoints)
                    .addComponent(editNumPoints, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPlaceNDonut)
                    .addComponent(editDonutCircumfrence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPlaceNCube)
                    .addComponent(btnShear1)
                    .addComponent(btnShearN))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(boxAutoNCorners)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSwapHands)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnJoggle)
                    .addComponent(editJoggleScale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRepel)
                    .addComponent(btnRepel100))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBind)
                    .addComponent(btnBind100)
                    .addComponent(btnBind1000))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSeed)
                    .addComponent(btnCheckCell))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCrystallize)
                    .addComponent(btnFull)
                    .addComponent(btnStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(boxShowCrystallization)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addComponent(boxSlowCrystallization))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(editSearchRadius, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(boxSkipHardFaces))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editMaxThinness, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editContainmentFudgeValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editMinAngle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editMinVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editMaxVolume, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editMinLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editMaxLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(boxStereo)
                    .addComponent(editStereoDegrees, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editStereoDelta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCalcProperties)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFinishPrep)
                    .addComponent(btnPlaceCamera))
                .addContainerGap(76, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1322, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1076, Short.MAX_VALUE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(latticetestwork.LatticeTestworkApp.class).getContext().getActionMap(LatticeTestworkView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);

        jMenuItem7.setText(resourceMap.getString("jMenuItem7.text")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        menuBar.add(jMenu1);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents
    public Engine engine = null;
    public int mode = 0;

    public void updateOptions() {
        if (engine != null) {
            engine.triangulateNCube = boxAutoNCorners.isSelected();
            engine.stereo = boxStereo.isSelected();
            engine.stereoDegrees = Double.valueOf(editStereoDegrees.getText());
            engine.stereoDelta = Double.valueOf(editStereoDelta.getText());
            for (int i = 0; i < rotationForm.scrollBars.length; i++) {
                engine.rot[i] = rotationForm.scrollBars[i].getValue() / 100.0;
            }
            engine.radiusSize = Double.valueOf(editSearchRadius.getText());
            engine.maxThinness = Double.valueOf(editMaxThinness.getText());
            engine.containmentFudgeValue = Double.valueOf(editContainmentFudgeValue.getText());
            engine.minAngle = Double.valueOf(editMinAngle.getText());
            engine.minVolume = Double.valueOf(editMinVolume.getText());
            engine.maxVolume = Double.valueOf(editMaxVolume.getText());
            engine.minLength = Double.valueOf(editMinLength.getText());
            engine.maxLength = Double.valueOf(editMaxLength.getText());
            engine.allowSkipHardFaces = boxSkipHardFaces.isSelected();
            engine.hideImmune = rotationForm.boxHideImmune.isSelected();
            engine.joggleScale = Double.valueOf(editJoggleScale.getText());
            engine.donutCircumfrence = Double.valueOf(editDonutCircumfrence.getText());
            engine.hideComplete = rotationForm.boxHideComplete.isSelected();
            engine.hideIncomplete = rotationForm.boxHideIncomplete.isSelected();
            engine.hidePoints = rotationForm.boxHidePoints.isSelected();
            if (repulsionForm != null) {
                engine.repulsionCutoff = Integer.valueOf(repulsionForm.editRepulsionCutoff.getText());
                if (repulsionForm.groupRepulsionMode.isSelected(repulsionForm.radioRepelCurrent.getModel())) {
                    engine.repulsionMode = 0;
                } else if (repulsionForm.groupRepulsionMode.isSelected(repulsionForm.radioRepelStandard.getModel())) {
                } else if (repulsionForm.groupRepulsionMode.isSelected(repulsionForm.radioRepelFunction.getModel())) {
                } else {
                }
            }
        }
    }
    public NGon was = null;

    public String latticeGenus() {
        if (engine != null) {
            return Double.toString((engine.lattice.faces.size() - engine.lattice.cells.size() - engine.lattice.points.size() + 2) / 2.0);
        } else {
            return "ERR";
        }
    }
    public RotationForm rotationForm = null;

    public void rotate() {
        if (engine != null) {
//        engine.xRot = barVertAngle.getValue() / -100.0;
//        engine.yRot = barHorizAngle.getValue() / 100.0;
            updateOptions();
            dp.repaint();
        }
    }

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    if (rotationForm != null) {
        rotationForm.show();
    }
}//GEN-LAST:event_jMenuItem1ActionPerformed
    public LatticeForm latticeForm = null;

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    if (engine != null) {
        if (latticeForm != null) {
            latticeForm.show();
        } else {
            latticeForm = new LatticeForm(this);
            latticeForm.show();
        }
    }
}//GEN-LAST:event_jMenuItem2ActionPerformed
    public MiscForm miscForm = null;

private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
    if (miscForm != null) {
        miscForm.show();
    } else {
        miscForm = new MiscForm(this);
        miscForm.show();
    }
}//GEN-LAST:event_jMenuItem3ActionPerformed
    public SkeletonForm skeletonForm = null;

private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
    if (skeletonForm != null) {
        skeletonForm.show();
    } else {
        skeletonForm = new SkeletonForm(this, (engine != null ? engine.dims : 0));
        skeletonForm.show();
    }
}//GEN-LAST:event_jMenuItem4ActionPerformed
    public BindingForm bindingForm = null;

private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
    if (bindingForm != null) {
        bindingForm.show();
    } else {
        bindingForm = new BindingForm(this);
        bindingForm.show();
    }
}//GEN-LAST:event_jMenuItem5ActionPerformed
    public RepulsionForm repulsionForm = null;

private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
    if (repulsionForm != null) {
        repulsionForm.show();
    } else {
        repulsionForm = new RepulsionForm(this);
        repulsionForm.show();
    }
}//GEN-LAST:event_jMenuItem6ActionPerformed
    public DistributedComputingForm mobBossForm = null;
    public MobBoss mobBoss = new MobBoss(null);
    public boolean distributeComputing = false;

private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
    if (mobBossForm != null) {
        mobBossForm.show();
    } else {
        mobBossForm = new DistributedComputingForm(this);
        mobBossForm.show();
    }
}//GEN-LAST:event_jMenuItem7ActionPerformed

private void btnPlacePointsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlacePointsActionPerformed
    if (engine != null) {
        engine.addPoints(Integer.valueOf(editNumPoints.getText()));
        dp.repaint();
    }
}//GEN-LAST:event_btnPlacePointsActionPerformed

private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
    //TODO All over the place, I may be able to make use of my RemoteWorkers.
    if (skeletonForm != null) {
        skeletonForm.dispose();
        skeletonForm = null;
    }
    if (bindingForm == null) {
        bindingForm = new BindingForm(this);
    }
    dp.scaleX = DisplayPanel.DEFscaleX;
    dp.scaleY = DisplayPanel.DEFscaleY;
    dp.transX = DisplayPanel.DEFtransX;
    dp.transY = DisplayPanel.DEFtransY;
    if (modeGroup.isSelected(radio22Tri.getModel())) {
        mode = 0;
        engine = new Engine(2, this);
        engine.init(2, mode);
        if (rotationForm != null) {
            rotationForm.dispose();
        }
        rotationForm = new RotationForm(2, this);
        rotationForm.show();
        dp.engine = engine;
        dp.repaint();
    } else if (modeGroup.isSelected(radio32Tri.getModel())) {
        mode = 1;
        engine = new Engine(3, this);
        engine.init(2, mode);
        if (rotationForm != null) {
            rotationForm.dispose();
        }
        rotationForm = new RotationForm(3, this);
        rotationForm.show();
        dp.engine = engine;
        dp.repaint();
    } else if (modeGroup.isSelected(radio32TriCube.getModel())) {
        mode = 2;
        engine = new Engine(3, this);
        engine.init(2, mode);
        if (rotationForm != null) {
            rotationForm.dispose();
        }
        rotationForm = new RotationForm(3, this);
        rotationForm.show();
        dp.engine = engine;
        dp.repaint();
    } else if (modeGroup.isSelected(radio33Tri.getModel())) {
        mode = 3;
        engine = new Engine(3, this);
        engine.init(3, mode);
        if (rotationForm != null) {
            rotationForm.dispose();
        }
        rotationForm = new RotationForm(3, this);
        rotationForm.show();
        dp.engine = engine;
        dp.repaint();
    } else if (modeGroup.isSelected(radio43Tri.getModel())) {
        mode = 1;
        engine = new Engine(4, this);
        engine.init(3, mode);
        if (rotationForm != null) {
            rotationForm.dispose();
        }
        rotationForm = new RotationForm(4, this);
        rotationForm.show();
        dp.engine = engine;
        dp.repaint();
    } else if (modeGroup.isSelected(radioManual.getModel())) {
        mode = 100;
        engine = new Engine((Integer) spinDims.getValue(), this);
        engine.init((Integer) spinLatticeDims.getValue(), mode);
        if (rotationForm != null) {
            rotationForm.dispose();
        }
        rotationForm = new RotationForm(engine.dims, this);
        rotationForm.show();
        dp.engine = engine;
        dp.repaint();
    }
    if (mobBoss != null) {
        mobBoss.lattice = engine.lattice;
    }
    updateOptions();
}//GEN-LAST:event_btnClearActionPerformed

private void btnRepelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepelActionPerformed
    if (engine != null) {
        engine.repel(0.0001);
        dp.repaint();
    }
}//GEN-LAST:event_btnRepelActionPerformed

private void btnPlaceNDonutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlaceNDonutActionPerformed
    if (engine != null) {
        updateOptions();
        try {
            if ((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
                engine.placeNSaltLattice();
            } else if ((evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
                engine.randomRange = Double.valueOf(editJoggleScale.getText());
                engine.placeNDonut();
            } else {
                engine.randomRange = -1;
                engine.placeNMobiusDonut();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, e);
        }
        dp.repaint();
    }
}//GEN-LAST:event_btnPlaceNDonutActionPerformed

private void btnCalcPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcPropertiesActionPerformed
    if (engine != null) {
        engine.calcProperties();
    }
}//GEN-LAST:event_btnCalcPropertiesActionPerformed

private void btnPlaceCameraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlaceCameraActionPerformed
    if (engine != null) {
        if ((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
            engine.placeCamera(false);
        } else if ((evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0) {
            for (Camera c : engine.lattice.cameras) {
                if (c != null) {
                    if (c.camForm != null) {
                        c.camForm.show();
                    } else {
                        c.camForm = new CameraForm(engine, c, this);
                        c.camForm.show();
                    }
                }
            }
        } else {
            engine.placeCamera();
        }
    }
}//GEN-LAST:event_btnPlaceCameraActionPerformed

private void editStereoDegreesPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_editStereoDegreesPropertyChange
    rotate();
}//GEN-LAST:event_editStereoDegreesPropertyChange

private void boxStereoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxStereoActionPerformed
    rotate();
}//GEN-LAST:event_boxStereoActionPerformed

private void btnPlaceNCubeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlaceNCubeActionPerformed
    if (engine != null) {
        updateOptions();
        engine.placeNCube();
        dp.repaint();
    }
}//GEN-LAST:event_btnPlaceNCubeActionPerformed

private void btnFinishPrepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishPrepActionPerformed
    if (engine != null) {
        for (NCell c : engine.lattice.cells) {
            c.makeBasis();
            c.color = new Color(c.color.getRed(), c.color.getGreen(), c.color.getBlue(), 0xFF);
        }
        for (int i = 0; i < 25; i++) {
            int j = engine.r.nextInt(engine.lattice.cells.size());
            Color color = engine.lattice.cells.get(j).color;
            engine.lattice.cells.get(j).color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 0xFF);
            //engine.lattice.cells.get(j).surfaces.add(new NSurface(engine.dims, engine.lattice.internalDims));
            engine.lattice.cells.get(j).surfaces.add(null);
        }
        for (NFace f : engine.lattice.faces) {
            f.calcAll();//engine.highlightedCells.add(f.cellA);
        }
    }
}//GEN-LAST:event_btnFinishPrepActionPerformed

private void btn34PresetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn34PresetActionPerformed
    this.radio43Tri.setSelected(true);
    btnClearActionPerformed(evt);
    btnPlacePointsActionPerformed(evt);
    editMaxThinness.setText("-1");
    btnBind1000ActionPerformed(evt);
    btnBind1000ActionPerformed(evt);
    btnBind1000ActionPerformed(evt);
    btnSeedActionPerformed(evt);
    btnFullActionPerformed(evt);
    btnFinishPrepActionPerformed(evt);
    btnPlaceCameraActionPerformed(evt);
}//GEN-LAST:event_btn34PresetActionPerformed

private void btnBind1000ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBind1000ActionPerformed
    if ((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
        for (int i = 0; i < 1000; i++) {
            //btnRepelActionPerformed(evt);
            if (engine != null) {
                engine.repel(0.01);
                dp.repaint();
            }
            btnBindActionPerformed(evt);
        }
    } else {
        for (int i = 0; i < 1000; i++) {
            btnRepelActionPerformed(evt);
            btnBindActionPerformed(evt);
        }
    }
}//GEN-LAST:event_btnBind1000ActionPerformed

private void editStereoDeltaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_editStereoDeltaPropertyChange
// TODO add your handling code here:
}//GEN-LAST:event_editStereoDeltaPropertyChange

private void btnBind100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBind100ActionPerformed
    for (int i = 0; i < 100; i++) {
        btnRepelActionPerformed(evt);
        btnBindActionPerformed(evt);
    }
}//GEN-LAST:event_btnBind100ActionPerformed

private void btnRepel100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRepel100ActionPerformed
    for (int i = 0; i < 100; i++) {
        btnRepelActionPerformed(evt);
    }
}//GEN-LAST:event_btnRepel100ActionPerformed

private void btnStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStatusActionPerformed
    if (engine != null) {
        JOptionPane.showMessageDialog(null, "Points: " + engine.lattice.points.size() + "\nFaces: " + engine.lattice.faces.size() + "\nIncomplete Faces: " + engine.lattice.incompleteFaces.size() + "\nCells: " + engine.lattice.cells.size() + "\nGenus: " + latticeGenus());
    } else {
        JOptionPane.showMessageDialog(null, "Engine has not been initialized.");
    }
}//GEN-LAST:event_btnStatusActionPerformed

private void btnSeedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeedActionPerformed
    if (engine != null) {
        updateOptions();
        for (int i = 0; i < 10; i++) {
            if (engine.seed()) {
                break;
            }
        }
        dp.repaint();
    }
}//GEN-LAST:event_btnSeedActionPerformed

private void btnBindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBindActionPerformed
    if (engine != null) {
        engine.bind();
        dp.repaint();
    }
}//GEN-LAST:event_btnBindActionPerformed
    /**
     * This is incremented when you hit one of the crystallization buttons.
     * It marks potential changes of face criteria, signaling the engine to
     * recheck all incomplete faces.
     * (Previously I was rechecking them on every loop, and suddenly I thought,
     * "Why the heck am I doing that?  It's not like anything's changed.")
     */
    public int runTag = 0;

private void btnFullActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFullActionPerformed
//    new Thread(){
//        public void run() {
    if (engine != null) {
        updateOptions();
        runTag++;
        boolean slow = boxShowCrystallization.isSelected();
        boolean verySlow = boxSlowCrystallization.isSelected();
        boolean firstTime = true;
        //engine.refreshCompletePoints();
        final BoolHolder done = new BoolHolder(false);
        final Object repaint = new Object();
        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    while (!done.value) {
                        synchronized (repaint) {
                            repaint.wait();
                        }
                        dp.engine.parallelRendering = true;
                        dp.paintImmediately(dp.getBounds());
                        dp.engine.parallelRendering = false;
                    }
                } catch (InterruptedException ex) {
                    //Logger.getLogger(LatticeTestworkView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
        while (engine.crystallize(runTag) || firstTime) {
            firstTime = false;
            if (engine.dims == 2 && engine.lattice.internalDims == 2) {
                ArrayList<NFace> markedForRemoval = new ArrayList<NFace>();
                for (NFace i : engine.lattice.incompleteFaces) {
                    if (((i.points[0].pos.coords[0] == -1) && (i.points[1].pos.coords[0] == -1))
                            || ((i.points[0].pos.coords[0] == 1) && (i.points[1].pos.coords[0] == 1))
                            || ((i.points[0].pos.coords[1] == -1) && (i.points[1].pos.coords[1] == -1))
                            || ((i.points[0].pos.coords[1] == 1) && (i.points[1].pos.coords[1] == 1))) {
                        markedForRemoval.add(i);
                    }
                }
                for (NFace i : markedForRemoval) {
                    engine.lattice.incompleteFaces.remove(i);
                }
            }

            if (!slow) {
                dp.repaint();
            } else {
                if (verySlow) {
                    dp.paintImmediately(dp.getBounds());
                } else {
                    synchronized (repaint) {
                        repaint.notify();
                    }
                }
//                try {
//                    Thread.sleep(50);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(LatticeTestworkView.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        }
        done.value = true;
        t.interrupt();
        dp.engine.parallelRendering = false;
//        synchronized (repaint) {
//            repaint.notify();
//        }
        dp.paintImmediately(dp.getBounds());
        JOptionPane.showMessageDialog(null, "Crystallization can proceed no farther.\nPoints: " + engine.lattice.points.size() + "\nFaces: " + engine.lattice.faces.size() + "\nIncomplete Faces: " + engine.lattice.incompleteFaces.size() + "\nCells: " + engine.lattice.cells.size() + "\nGenus: " + latticeGenus());
    }
//        }
//    }.run();
}//GEN-LAST:event_btnFullActionPerformed

private void btnShearNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShearNActionPerformed
    if (engine != null) {
        if ((evt.getModifiers() & ActionEvent.SHIFT_MASK) == 0) {
            engine.shearN();
        } else {
            //TODO Um.  Undo it.  Go.
        }
        dp.repaint();
    }
}//GEN-LAST:event_btnShearNActionPerformed

private void btnCrystallizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCrystallizeActionPerformed
    if (engine != null) {
        updateOptions();
        runTag++;
        if (engine.dims == 2 && engine.lattice.internalDims == 2) {
            ArrayList<NFace> markedForRemoval = new ArrayList<NFace>();
            for (NFace i : engine.lattice.incompleteFaces) {
                if (((i.points[0].pos.coords[0] == -1) && (i.points[1].pos.coords[0] == -1))
                        || ((i.points[0].pos.coords[0] == 1) && (i.points[1].pos.coords[0] == 1))
                        || ((i.points[0].pos.coords[1] == -1) && (i.points[1].pos.coords[1] == -1))
                        || ((i.points[0].pos.coords[1] == 1) && (i.points[1].pos.coords[1] == 1))) {
                    markedForRemoval.add(i);
                }
            }
            for (NFace i : markedForRemoval) {
                engine.lattice.incompleteFaces.remove(i);
            }
        }
        if (!engine.crystallize(runTag)) {
            JOptionPane.showMessageDialog(null, "Crystallization failed.");
        }
        if (engine.dims == 2 && engine.lattice.internalDims == 2) {
            ArrayList<NFace> markedForRemoval = new ArrayList<NFace>();
            for (NFace i : engine.lattice.incompleteFaces) {
                if (((i.points[0].pos.coords[0] == -1) && (i.points[1].pos.coords[0] == -1))
                        || ((i.points[0].pos.coords[0] == 1) && (i.points[1].pos.coords[0] == 1))
                        || ((i.points[0].pos.coords[1] == -1) && (i.points[1].pos.coords[1] == -1))
                        || ((i.points[0].pos.coords[1] == 1) && (i.points[1].pos.coords[1] == 1))) {
                    markedForRemoval.add(i);
                }
            }
            for (NFace i : markedForRemoval) {
                engine.lattice.incompleteFaces.remove(i);
            }
        }
        dp.repaint();
    }
}//GEN-LAST:event_btnCrystallizeActionPerformed

private void btnShear1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShear1ActionPerformed
    if (engine != null) {
        if ((evt.getModifiers() & ActionEvent.SHIFT_MASK) == 0) {
            engine.shear1();
        } else {
            //TODO Um.  Undo it.  Go.
        }
        dp.repaint();
    }
}//GEN-LAST:event_btnShear1ActionPerformed

private void btnJoggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnJoggleActionPerformed
    if (engine != null) {
        updateOptions();
        engine.joggle();
        dp.repaint();
    }
}//GEN-LAST:event_btnJoggleActionPerformed

private void btnSwapHandsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSwapHandsActionPerformed
    if (engine != null) {
        engine.swapHands();
        dp.repaint();
    }
}//GEN-LAST:event_btnSwapHandsActionPerformed

    public void checkCell(NPoint center, double radius, NPoint[] points, String extra) {
        StringBuilder sb = new StringBuilder();
        sb.append("PR: " + radius + "\n");
        for (int i = 0; i < points.length; i++) {
            NPoint p = points[i];
            sb.append("R" + (i + 1) + ": " + p.pos.dist(center.pos) + "\n");
        }
        sb.append("T1: " + Engine.calcThinness1(points) + "\n");
        sb.append("T2: " + Engine.calcThinness2(points) + "\n");
        sb.append("MA: " + Engine.calcMinAngle(points) + "\n");
        double min = -1;
        double max = -1;
        for (int i = 0; i < points.length - 1; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double dist = points[i].dist(points[j]);
                if (min == -1 || dist < min) {
                    min = dist;
                }
                if (max == -1 || dist > max) {
                    max = dist;
                }
            }
        }
        sb.append("MN: " + min + "\n");
        sb.append("MX: " + max + "\n");
        sb.append("VL: " + Engine.getSimplexNVolume(points) + "\n");
        ArrayList<NPoint> pts = new ArrayList<NPoint>();
        for (int i = 0; i < points.length; i++) {
            pts.add(points[i]);
        }
        sb.append("VC: " + Engine.checkMinMaxVolume(pts, engine.minVolume, engine.maxVolume) + "\n");
        sb.append(extra);
        //sb.deleteCharAt(sb.length() - 1);
        JOptionPane.showMessageDialog(null, sb.toString());
    }

private void btnCheckCellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckCellActionPerformed
    if (engine != null) {
        updateOptions();
        if (engine.permacenter != null && engine.lastCell != null) {
            checkCell(engine.permacenter, engine.permaradius, engine.lastCell.points, "EX: (0)");
        } else {
            JOptionPane.showMessageDialog(null, "No previous cell recorded.");
        }
    }
}//GEN-LAST:event_btnCheckCellActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox boxAutoNCorners;
    private javax.swing.JCheckBox boxShowCrystallization;
    private javax.swing.JCheckBox boxSkipHardFaces;
    private javax.swing.JCheckBox boxSlowCrystallization;
    private javax.swing.JCheckBox boxStereo;
    private javax.swing.JButton btn34Preset;
    private javax.swing.JButton btnBind;
    private javax.swing.JButton btnBind100;
    private javax.swing.JButton btnBind1000;
    private javax.swing.JButton btnCalcProperties;
    private javax.swing.JButton btnCheckCell;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCrystallize;
    private javax.swing.JButton btnFinishPrep;
    private javax.swing.JButton btnFull;
    private javax.swing.JButton btnJoggle;
    private javax.swing.JButton btnPlaceCamera;
    private javax.swing.JButton btnPlaceNCube;
    private javax.swing.JButton btnPlaceNDonut;
    private javax.swing.JButton btnPlacePoints;
    private javax.swing.JButton btnRepel;
    private javax.swing.JButton btnRepel100;
    private javax.swing.JButton btnSeed;
    private javax.swing.JButton btnShear1;
    private javax.swing.JButton btnShearN;
    private javax.swing.JButton btnStatus;
    private javax.swing.JButton btnSwapHands;
    private javax.swing.JTextField editContainmentFudgeValue;
    private javax.swing.JTextField editDonutCircumfrence;
    private javax.swing.JTextField editJoggleScale;
    private javax.swing.JTextField editMaxLength;
    private javax.swing.JTextField editMaxThinness;
    private javax.swing.JTextField editMaxVolume;
    private javax.swing.JTextField editMinAngle;
    private javax.swing.JTextField editMinLength;
    private javax.swing.JTextField editMinVolume;
    private javax.swing.JTextField editNumPoints;
    private javax.swing.JTextField editSearchRadius;
    private javax.swing.JTextField editStereoDegrees;
    private javax.swing.JTextField editStereoDelta;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.ButtonGroup modeGroup;
    private javax.swing.JPanel panelDisplay;
    private javax.swing.JRadioButton radio22Tri;
    private javax.swing.JRadioButton radio32Tri;
    private javax.swing.JRadioButton radio32TriCube;
    private javax.swing.JRadioButton radio33Tri;
    private javax.swing.JRadioButton radio43Tri;
    private javax.swing.JRadioButton radioManual;
    private javax.swing.JSpinner spinDims;
    private javax.swing.JSpinner spinLatticeDims;
    // End of variables declaration//GEN-END:variables
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
}
