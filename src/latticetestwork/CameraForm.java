/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CameraForm.java
 *
 * Created on Feb 4, 2012, 10:57:54 PM
 */
package latticetestwork;

//import com.xuggle.mediatool.IMediaWriter;
//import com.xuggle.mediatool.ToolFactory;
//import com.xuggle.xuggler.IRational;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matthew Ewer <Ewer.Matthew@gmail.com>
 */
public class CameraForm extends javax.swing.JFrame {

    public DisplayPanel renderPanel = null;
    public Engine engine = null;
    public Camera camera = null;
    public double dtl = 2.0;
    public LatticeTestworkView parent = null;
    public boolean render;

    /** Creates new form CameraForm */
    public CameraForm(final Engine engine0, Camera cam, LatticeTestworkView parent) {
        this.engine = engine0;
        this.camera = cam;
        this.parent = parent;
        this.render = true;
        initComponents();
        renderPanel = new DisplayPanel(false) {

            public Engine engine = engine0;
            public int renderCount = 0;

            @Override
            protected void paintComponent(Graphics g1) {
                super.paintComponent(g1);
                if (engine != null && render) {
                    System.out.println("Start render " + renderCount);
                    Graphics2D g = (Graphics2D) g1;
                    camera.renderCamera(g, Camera.PROJ_MERCATOR, this.getWidth(), this.getHeight(), dtl);
                    System.out.println("Finish render " + renderCount++);
                    //engine.render(g, 0, this.getWidth(), this.getHeight(), transX, transY, scaleX, scaleY);
                }
            }
        };
        this.jSplitPane1.setLeftComponent(renderPanel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        intrumentPanel = new javax.swing.JPanel();
        btnDTL = new javax.swing.JButton();
        btnEncode = new javax.swing.JButton();
        btnStrafeL = new javax.swing.JButton();
        btnStrafeR = new javax.swing.JButton();
        btnForward = new javax.swing.JButton();
        btnForwardOne = new javax.swing.JButton();
        btnRightOne = new javax.swing.JButton();
        btnLeftOne = new javax.swing.JButton();
        btnBackwardOne = new javax.swing.JButton();
        btnUpOne = new javax.swing.JButton();
        btnDownOne = new javax.swing.JButton();
        boxRender = new javax.swing.JCheckBox();
        btnRealign = new javax.swing.JButton();
        btnCenter = new javax.swing.JButton();
        boxRenderMain = new javax.swing.JCheckBox();
        btnTracer = new javax.swing.JButton();
        btnControl = new javax.swing.JButton();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(latticetestwork.LatticeTestworkApp.class).getContext().getResourceMap(CameraForm.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jSplitPane1.setDividerLocation(284);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        intrumentPanel.setName("intrumentPanel"); // NOI18N
        intrumentPanel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                intrumentPanelKeyTyped(evt);
            }
        });

        btnDTL.setText(resourceMap.getString("btnDTL.text")); // NOI18N
        btnDTL.setName("btnDTL"); // NOI18N
        btnDTL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDTLActionPerformed(evt);
            }
        });

        btnEncode.setText(resourceMap.getString("btnEncode.text")); // NOI18N
        btnEncode.setName("btnEncode"); // NOI18N
        btnEncode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEncodeActionPerformed(evt);
            }
        });

        btnStrafeL.setText(resourceMap.getString("btnStrafeL.text")); // NOI18N
        btnStrafeL.setName("btnStrafeL"); // NOI18N
        btnStrafeL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStrafeLActionPerformed(evt);
            }
        });

        btnStrafeR.setText(resourceMap.getString("btnStrafeR.text")); // NOI18N
        btnStrafeR.setName("btnStrafeR"); // NOI18N
        btnStrafeR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStrafeRActionPerformed(evt);
            }
        });

        btnForward.setText(resourceMap.getString("btnForward.text")); // NOI18N
        btnForward.setName("btnForward"); // NOI18N
        btnForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardActionPerformed(evt);
            }
        });

        btnForwardOne.setText(resourceMap.getString("btnForwardOne.text")); // NOI18N
        btnForwardOne.setToolTipText(resourceMap.getString("btnForwardOne.toolTipText")); // NOI18N
        btnForwardOne.setName("btnForwardOne"); // NOI18N
        btnForwardOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnForwardOneActionPerformed(evt);
            }
        });

        btnRightOne.setText(resourceMap.getString("btnRightOne.text")); // NOI18N
        btnRightOne.setToolTipText(resourceMap.getString("btnRightOne.toolTipText")); // NOI18N
        btnRightOne.setName("btnRightOne"); // NOI18N
        btnRightOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRightOneActionPerformed(evt);
            }
        });

        btnLeftOne.setText(resourceMap.getString("btnLeftOne.text")); // NOI18N
        btnLeftOne.setToolTipText(resourceMap.getString("btnLeftOne.toolTipText")); // NOI18N
        btnLeftOne.setName("btnLeftOne"); // NOI18N
        btnLeftOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeftOneActionPerformed(evt);
            }
        });

        btnBackwardOne.setText(resourceMap.getString("btnBackwardOne.text")); // NOI18N
        btnBackwardOne.setToolTipText(resourceMap.getString("btnBackwardOne.toolTipText")); // NOI18N
        btnBackwardOne.setName("btnBackwardOne"); // NOI18N
        btnBackwardOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackwardOneActionPerformed(evt);
            }
        });

        btnUpOne.setText(resourceMap.getString("btnUpOne.text")); // NOI18N
        btnUpOne.setToolTipText(resourceMap.getString("btnUpOne.toolTipText")); // NOI18N
        btnUpOne.setName("btnUpOne"); // NOI18N
        btnUpOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpOneActionPerformed(evt);
            }
        });

        btnDownOne.setText(resourceMap.getString("btnDownOne.text")); // NOI18N
        btnDownOne.setToolTipText(resourceMap.getString("btnDownOne.toolTipText")); // NOI18N
        btnDownOne.setName("btnDownOne"); // NOI18N
        btnDownOne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownOneActionPerformed(evt);
            }
        });

        boxRender.setSelected(true);
        boxRender.setText(resourceMap.getString("boxRender.text")); // NOI18N
        boxRender.setName("boxRender"); // NOI18N
        boxRender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxRenderActionPerformed(evt);
            }
        });

        btnRealign.setText(resourceMap.getString("btnRealign.text")); // NOI18N
        btnRealign.setToolTipText(resourceMap.getString("btnRealign.toolTipText")); // NOI18N
        btnRealign.setName("btnRealign"); // NOI18N
        btnRealign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRealignActionPerformed(evt);
            }
        });

        btnCenter.setText(resourceMap.getString("btnCenter.text")); // NOI18N
        btnCenter.setMinimumSize(new java.awt.Dimension(71, 23));
        btnCenter.setName("btnCenter"); // NOI18N
        btnCenter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCenterActionPerformed(evt);
            }
        });

        boxRenderMain.setText(resourceMap.getString("boxRenderMain.text")); // NOI18N
        boxRenderMain.setToolTipText(resourceMap.getString("boxRenderMain.toolTipText")); // NOI18N
        boxRenderMain.setName("boxRenderMain"); // NOI18N

        btnTracer.setText(resourceMap.getString("btnTracer.text")); // NOI18N
        btnTracer.setMinimumSize(new java.awt.Dimension(71, 23));
        btnTracer.setName("btnTracer"); // NOI18N
        btnTracer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTracerActionPerformed(evt);
            }
        });

        btnControl.setText(resourceMap.getString("btnControl.text")); // NOI18N
        btnControl.setName("btnControl"); // NOI18N
        btnControl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnControlKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                btnControlKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout intrumentPanelLayout = new javax.swing.GroupLayout(intrumentPanel);
        intrumentPanel.setLayout(intrumentPanelLayout);
        intrumentPanelLayout.setHorizontalGroup(
            intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, intrumentPanelLayout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTracer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRealign, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCenter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(intrumentPanelLayout.createSequentialGroup()
                        .addComponent(boxRender)
                        .addGap(8, 8, 8)
                        .addComponent(boxRenderMain)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, intrumentPanelLayout.createSequentialGroup()
                        .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnEncode, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnStrafeR, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnForward, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDTL, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnStrafeL, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, intrumentPanelLayout.createSequentialGroup()
                                .addComponent(btnLeftOne, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRightOne, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnBackwardOne, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnForwardOne, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, intrumentPanelLayout.createSequentialGroup()
                                .addComponent(btnUpOne, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDownOne, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())))
        );
        intrumentPanelLayout.setVerticalGroup(
            intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(intrumentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDTL)
                    .addComponent(btnRealign))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStrafeL)
                    .addComponent(btnCenter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStrafeR)
                    .addComponent(btnTracer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnForward)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnForwardOne)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRightOne)
                    .addComponent(btnLeftOne))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBackwardOne)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnDownOne)
                    .addComponent(btnUpOne))
                .addGap(5, 5, 5)
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(boxRenderMain)
                    .addComponent(boxRender))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(intrumentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEncode)
                    .addComponent(btnControl))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(intrumentPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    public int imWidth = 256;
    public int imHeight = 256;
    public int imagecount = 0;
    private void btnDTLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDTLActionPerformed
        images.clear();
        int count = 0;
        for (double ldtl = 3.0625; ldtl > 0; ldtl -= 0.125) {
            count++;

            int[] picDims = new int[]{imWidth, imHeight};
            Tensor<Color> result = camera.aRender(ldtl, picDims);
            BufferedImage image = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_3BYTE_BGR);
            for (int x = 0; x < imWidth; x++) {
                for (int y = 0; y < imHeight; y++) {
                    image.setRGB(x, y, result.get(x, y).getRGB());
                }
            }

            try {
                java.io.FileOutputStream fout = new java.io.FileOutputStream("image" + imagecount++ + ".jpg");
                javax.imageio.ImageIO.write(image, "JPG", fout);
//                com.sun.image.codec.jpeg.JPEGImageEncoder jie = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(fout);
//                com.sun.image.codec.jpeg.JPEGEncodeParam enParam = jie.getDefaultJPEGEncodeParam(image);
//                enParam.setQuality(1.0f, true);
//                jie.setJPEGEncodeParam(enParam);
//                jie.encode(image);
                fout.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            images.add(image);
        }
        btnEncodeActionPerformed(evt);
    }//GEN-LAST:event_btnDTLActionPerformed

    private void btnEncodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEncodeActionPerformed
//        final IMediaWriter writer = ToolFactory.makeWriter("output.mp4");
//
//        // We tell it we're going to add one video stream, with id 0,
//        // at position 0, and that it will have a fixed frame rate of
//        // FRAME_RATE.
//        IRational FRAME_RATE = IRational.make(1, 20);
//        int width = imWidth;
//        int height = imHeight;
//        writer.addVideoStream(0, 0,
//                FRAME_RATE,
//                width + 1, height + 1);
//
//        // Now, we're going to loop
//        long startTime = System.nanoTime();
//        for (int i = 0; i < images.size(); i++) {
////            try {
////                java.io.FileOutputStream fout = new java.io.FileOutputStream("image" + i + ".jpg");
////                com.sun.image.codec.jpeg.JPEGImageEncoder jie = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(fout);
////                com.sun.image.codec.jpeg.JPEGEncodeParam enParam = jie.getDefaultJPEGEncodeParam(images.get(i));
////                enParam.setQuality(1.0f, true);
////                jie.setJPEGEncodeParam(enParam);
////                jie.encode(images.get(i));
////                fout.close();
////            } catch (ImageFormatException ex) {
////                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (FileNotFoundException ex) {
////                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
////            } catch (IOException ex) {
////                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
////            }
//            writer.encodeVideo(0, images.get(i),
//                    1000 * i, TimeUnit.NANOSECONDS);
//            System.out.println("encoded image: " + i);
//
//        }
//        writer.close();
//
    }//GEN-LAST:event_btnEncodeActionPerformed
    public double gaitLength = 0.005;
    public double prevFloating = 0;
    public double curFloating = 0;

    private void btnStrafeLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStrafeLActionPerformed
        images.clear();
        //int count = 0;
        for (int count = 0; count < 2000; count++) {
            //count++;

            camera.move(camera.orientation[1].multS(-gaitLength), null);
            prevFloating = curFloating;
            curFloating = camera.checkFloating();
            if (curFloating > 0.005) {
                try {
                    boolean escape = false;
                    System.err.println("Floating away! Count: " + count + " - " + curFloating);
                    if (escape) {
                        break;
                    }
                    NVector prevPos = camera.pos.copy();
                    camera.reanchor();
                    camera.realignOrientation(camera.cell);
                    System.err.println("Changed dist: " + camera.pos.minusB(prevPos).length());
                } catch (Exception ex) {
                    Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (!parent.distributeComputing) {
                int[] picDims = new int[]{imWidth, imHeight};
                Tensor<Color> result = camera.aRender(dtl, picDims);
                BufferedImage image = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_3BYTE_BGR);
                for (int x = 0; x < imWidth; x++) {
                    for (int y = 0; y < imHeight; y++) {
                        image.setRGB(x, y, result.get(x, y).getRGB());
                    }
                }


                try {
                    java.io.FileOutputStream fout = new java.io.FileOutputStream("image" + imagecount++ + ".jpg");
                    javax.imageio.ImageIO.write(image, "JPG", fout);
                    fout.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
                }

                //images.add(image);
            } else {
                final byte[] latticeBytes = parent.engine.lattice.toByteArray();

                final RemoteWorkerIcon worker = parent.mobBoss.requestWorker();
                try {
                    worker.updateWorker(latticeBytes);
                    final int[] picDims = new int[]{imWidth, imHeight};
                    final int camNum = parent.engine.lattice.cameras.indexOf(this.camera);

//                    System.out.println("CamPos: " + parent.engine.lattice.cameras.get(camNum).pos);
//                    System.out.println("CamOrient:");
//                    for (int i = 0; i < parent.engine.lattice.cameras.get(camNum).orientation.length; i++) {
//                        System.out.println(parent.engine.lattice.cameras.get(camNum).orientation[i]);
//                    }
                    final int imageNum = imagecount++;
                    new Thread(new Runnable() {

                        public void run() {
                            try {
                                BufferedImage image = worker.renderFrameToImagePaced(camNum, dtl, imWidth, imHeight, picDims);
                                try {
                                    java.io.FileOutputStream fout = new java.io.FileOutputStream("image" + imageNum + ".jpg");
                                    javax.imageio.ImageIO.write(image, "JPG", fout);
                                    fout.close();
                                } catch (FileNotFoundException ex) {
                                    Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (IOException ex) {
                                    Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                //images.add(image);
                                parent.mobBoss.returnWorker(worker);
                            } catch (IOException ex) {
                                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (Throwable e) {
                                parent.mobBoss.returnWorker(worker);
                            }
                        }
                    }).start();
//                    Tensor<Color> result2 = camera.aRender(dtl, picDims);
//                    BufferedImage image2 = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_3BYTE_BGR);
//                    for (int x = 0; x < imWidth; x++) {
//                        for (int y = 0; y < imHeight; y++) {
//                            image2.setRGB(x, y, result2.get(x, y).getRGB());
//                        }
//                    }
//                    try {
//                        java.io.FileOutputStream fout2 = new java.io.FileOutputStream("image2-" + imagecount++ + ".jpg");
//                        javax.imageio.ImageIO.write(image2, "JPG", fout2);
//                        fout2.close();
//                    } catch (FileNotFoundException ex) {
//                        Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
//                    } catch (IOException ex) {
//                        Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
//                    }

                } catch (IOException ex) {
                    Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        //btnEncodeActionPerformed(evt);
    }//GEN-LAST:event_btnStrafeLActionPerformed

    private void btnStrafeRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStrafeRActionPerformed
        images.clear();
        //int count = 0;
        for (int count = 0; count < 40; count++) {
            //count++;

            camera.move(camera.orientation[1].multS(gaitLength), null);

            int[] picDims = new int[]{imWidth, imHeight};
            Tensor<Color> result = camera.aRender(2, picDims);
            BufferedImage image = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_3BYTE_BGR);
            for (int x = 0; x < imWidth; x++) {
                for (int y = 0; y < imHeight; y++) {
                    image.setRGB(x, y, result.get(x, y).getRGB());
                }
            }


            try {
                java.io.FileOutputStream fout = new java.io.FileOutputStream("image" + imagecount++ + ".jpg");
                javax.imageio.ImageIO.write(image, "JPG", fout);
//                com.sun.image.codec.jpeg.JPEGImageEncoder jie = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(fout);
//                com.sun.image.codec.jpeg.JPEGEncodeParam enParam = jie.getDefaultJPEGEncodeParam(image);
//                enParam.setQuality(1.0f, true);
//                jie.setJPEGEncodeParam(enParam);
//                jie.encode(image);
                fout.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            images.add(image);
        }
        btnEncodeActionPerformed(evt);
    }//GEN-LAST:event_btnStrafeRActionPerformed

    private void btnForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardActionPerformed
        images.clear();
        //int count = 0;
        for (int count = 0; count < 40; count++) {
            //count++;

            camera.move(camera.orientation[0].multS(gaitLength), null);

            int[] picDims = new int[]{imWidth, imHeight};
            Tensor<Color> result = camera.aRender(2, picDims);
            BufferedImage image = new BufferedImage(imWidth, imHeight, BufferedImage.TYPE_3BYTE_BGR);
            for (int x = 0; x < imWidth; x++) {
                for (int y = 0; y < imHeight; y++) {
                    image.setRGB(x, y, result.get(x, y).getRGB());
                }
            }


            try {
                java.io.FileOutputStream fout = new java.io.FileOutputStream("image" + imagecount++ + ".jpg");
                javax.imageio.ImageIO.write(image, "JPG", fout);
//                com.sun.image.codec.jpeg.JPEGImageEncoder jie = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(fout);
//                com.sun.image.codec.jpeg.JPEGEncodeParam enParam = jie.getDefaultJPEGEncodeParam(image);
//                enParam.setQuality(1.0f, true);
//                jie.setJPEGEncodeParam(enParam);
//                jie.encode(image);
                fout.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            images.add(image);
        }
        btnEncodeActionPerformed(evt);
    }//GEN-LAST:event_btnForwardActionPerformed

    private void btnLeftOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftOneActionPerformed
        try {
            camera.move(camera.orientation[1].multS(gaitLength), null);
        } catch (StackOverflowError e) {
            System.err.println("Stack overflow! - " + camera.checkFloating());
        }
        if (boxRender.isSelected()) {
            renderPanel.repaint();
        }
        if (boxRenderMain.isSelected()) {
            parent.dp.repaint();
        }
    }//GEN-LAST:event_btnLeftOneActionPerformed

    private void btnForwardOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnForwardOneActionPerformed
        camera.move(camera.orientation[0].multS(gaitLength), null);
        if (boxRender.isSelected()) {
            renderPanel.repaint();
        }
        if (boxRenderMain.isSelected()) {
            parent.dp.repaint();
        }
    }//GEN-LAST:event_btnForwardOneActionPerformed

    private void btnRightOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightOneActionPerformed
        camera.move(camera.orientation[1].multS(-gaitLength), null);
        if (boxRender.isSelected()) {
            renderPanel.repaint();
        }
        if (boxRenderMain.isSelected()) {
            parent.dp.repaint();
        }
    }//GEN-LAST:event_btnRightOneActionPerformed

    private void btnBackwardOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackwardOneActionPerformed
        camera.move(camera.orientation[0].multS(-gaitLength), null);
        if (boxRender.isSelected()) {
            renderPanel.repaint();
        }
        if (boxRenderMain.isSelected()) {
            parent.dp.repaint();
        }
    }//GEN-LAST:event_btnBackwardOneActionPerformed

    private void btnUpOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpOneActionPerformed
        camera.move(camera.orientation[2].multS(gaitLength), null);
        if (boxRender.isSelected()) {
            renderPanel.repaint();
        }
        if (boxRenderMain.isSelected()) {
            parent.dp.repaint();
        }
    }//GEN-LAST:event_btnUpOneActionPerformed

    private void btnDownOneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownOneActionPerformed
        camera.move(camera.orientation[2].multS(-gaitLength), null);
        if (boxRender.isSelected()) {
            renderPanel.repaint();
        }
        if (boxRenderMain.isSelected()) {
            parent.dp.repaint();
        }
    }//GEN-LAST:event_btnDownOneActionPerformed

    private void btnRealignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRealignActionPerformed
        if ((evt.getModifiers() & ActionEvent.CTRL_MASK) != 0) {
            new Thread(new Runnable() {

                public void run() {
                    try {
                        camera.reanchor();
                        camera.realignOrientation(camera.cell);
                    } catch (Exception ex) {
                        Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }).start();
        } else {
            try {
                camera.reanchor();
                camera.realignOrientation(camera.cell);
            } catch (Exception ex) {
                Logger.getLogger(CameraForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (boxRender.isSelected()) {
            renderPanel.repaint();
        }
        if (boxRenderMain.isSelected()) {
            parent.dp.repaint();
        }
    }//GEN-LAST:event_btnRealignActionPerformed

    private void boxRenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxRenderActionPerformed
        render = boxRender.isSelected();
    }//GEN-LAST:event_boxRenderActionPerformed

    private void btnCenterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCenterActionPerformed
        NVector center = new NVector(camera.dims);
        for (NPoint i : camera.cell.points) {
            center = center.plusB(i.pos);
        }
        center = center.multS(1.0 / camera.cell.points.length);
        camera.pos = center;
        if (boxRender.isSelected()) {
            renderPanel.repaint();
        }
        if (boxRenderMain.isSelected()) {
            parent.dp.repaint();
        }
    }//GEN-LAST:event_btnCenterActionPerformed

private void btnTracerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTracerActionPerformed
    // Do tracer
    camera.renderCameraTracer(Camera.PROJ_MERCATOR, renderPanel.getWidth(), this.getHeight(), 2);
    if (boxRender.isSelected()) {
        renderPanel.repaint();
    }
    if (boxRenderMain.isSelected()) {
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnTracerActionPerformed

private void intrumentPanelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_intrumentPanelKeyTyped
}//GEN-LAST:event_intrumentPanelKeyTyped

private void btnControlKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnControlKeyTyped
    switch (evt.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            btnLeftOneActionPerformed(null);
            break;
        case KeyEvent.VK_RIGHT:
            btnRightOneActionPerformed(null);
            break;
        case KeyEvent.VK_UP:
            btnForwardOneActionPerformed(null);
            break;
        case KeyEvent.VK_DOWN:
            btnBackwardOneActionPerformed(null);
            break;
    }
}//GEN-LAST:event_btnControlKeyTyped

private void btnControlKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnControlKeyPressed
    switch (evt.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            btnLeftOneActionPerformed(null);
            break;
        case KeyEvent.VK_RIGHT:
            btnRightOneActionPerformed(null);
            break;
        case KeyEvent.VK_UP:
            btnForwardOneActionPerformed(null);
            break;
        case KeyEvent.VK_DOWN:
            btnBackwardOneActionPerformed(null);
            break;
    }
}//GEN-LAST:event_btnControlKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new CameraForm(null, null, null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBox boxRender;
    public javax.swing.JCheckBox boxRenderMain;
    private javax.swing.JButton btnBackwardOne;
    private javax.swing.JButton btnCenter;
    private javax.swing.JButton btnControl;
    private javax.swing.JButton btnDTL;
    private javax.swing.JButton btnDownOne;
    private javax.swing.JButton btnEncode;
    private javax.swing.JButton btnForward;
    private javax.swing.JButton btnForwardOne;
    private javax.swing.JButton btnLeftOne;
    private javax.swing.JButton btnRealign;
    private javax.swing.JButton btnRightOne;
    private javax.swing.JButton btnStrafeL;
    private javax.swing.JButton btnStrafeR;
    private javax.swing.JButton btnTracer;
    private javax.swing.JButton btnUpOne;
    private javax.swing.JPanel intrumentPanel;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
}
