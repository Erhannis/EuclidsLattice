/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SkeletonForm.java
 *
 * Created on Nov 30, 2011, 12:03:49 PM
 */
package latticetestwork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mewer12
 */
public class SkeletonForm extends javax.swing.JFrame {

    public LatticeTestworkView parent = null;
    public int dims = 0;
    public DefaultTableModel modelPoints = null;
    public DefaultTableModel modelBones = null;

    /** Creates new form SkeletonForm */
    public SkeletonForm(LatticeTestworkView parent, int dims) {
        this.parent = parent;
        this.dims = dims;
        initComponents();
        String[] labels = new String[dims + 1];
        labels[0] = "Index";
        final Class[] extTypes = new Class[dims + 1];
        extTypes[0] = java.lang.Integer.class;
        final boolean[] extCanEdit = new boolean[dims + 1];
        extCanEdit[0] = false;
        for (int i = 1; i < dims + 1; i++) {
            labels[i] = Integer.toString(i);
            extTypes[i] = java.lang.Double.class;
            extCanEdit[i] = true;
        }

        modelPoints = new javax.swing.table.DefaultTableModel(new Object[][]{}, labels) {

            Class[] types = extTypes;
            boolean[] canEdit = extCanEdit;

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        tblPoints.setModel(modelPoints);

        modelBones = (DefaultTableModel) tblBones.getModel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        groupSnapping = new javax.swing.ButtonGroup();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPoints = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblBones = new javax.swing.JTable();
        btnAddPt = new javax.swing.JButton();
        btnAddBone = new javax.swing.JButton();
        btnDelPt = new javax.swing.JButton();
        btnDelBone = new javax.swing.JButton();
        btnApply = new javax.swing.JButton();
        boxPreserve = new javax.swing.JCheckBox();
        btnBind100 = new javax.swing.JButton();
        radioHardSnap = new javax.swing.JRadioButton();
        radioElasticSnap = new javax.swing.JRadioButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("latticetestwork/resources/SkeletonForm"); // NOI18N
        setTitle(bundle.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N

        jSplitPane1.setDividerLocation(89);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblPoints.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Index"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPoints.setName("tblPoints"); // NOI18N
        jScrollPane1.setViewportView(tblPoints);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tblBones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Point 1", "Point 2", "Distance"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblBones.setName("tblBones"); // NOI18N
        jScrollPane2.setViewportView(tblBones);

        jSplitPane1.setRightComponent(jScrollPane2);

        btnAddPt.setText(bundle.getString("btnAddPt.text")); // NOI18N
        btnAddPt.setName("btnAddPt"); // NOI18N
        btnAddPt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddPtActionPerformed(evt);
            }
        });

        btnAddBone.setText(bundle.getString("btnAddBone.text")); // NOI18N
        btnAddBone.setName("btnAddBone"); // NOI18N
        btnAddBone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBoneActionPerformed(evt);
            }
        });

        btnDelPt.setText(bundle.getString("btnDelPt.text")); // NOI18N
        btnDelPt.setName("btnDelPt"); // NOI18N
        btnDelPt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelPtActionPerformed(evt);
            }
        });

        btnDelBone.setText(bundle.getString("btnDelBone.text")); // NOI18N
        btnDelBone.setName("btnDelBone"); // NOI18N
        btnDelBone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelBoneActionPerformed(evt);
            }
        });

        btnApply.setText(bundle.getString("btnApply.text")); // NOI18N
        btnApply.setName("btnApply"); // NOI18N
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        boxPreserve.setSelected(true);
        boxPreserve.setText(bundle.getString("boxPreserve.text")); // NOI18N
        boxPreserve.setName("boxPreserve"); // NOI18N

        btnBind100.setText(bundle.getString("btnBind100.text")); // NOI18N
        btnBind100.setName("btnBind100"); // NOI18N
        btnBind100.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBind100ActionPerformed(evt);
            }
        });

        groupSnapping.add(radioHardSnap);
        radioHardSnap.setSelected(true);
        radioHardSnap.setText(bundle.getString("radioHardSnap.text")); // NOI18N
        radioHardSnap.setName("radioHardSnap"); // NOI18N

        groupSnapping.add(radioElasticSnap);
        radioElasticSnap.setText(bundle.getString("radioElasticSnap.text")); // NOI18N
        radioElasticSnap.setEnabled(false);
        radioElasticSnap.setName("radioElasticSnap"); // NOI18N

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setText(bundle.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem1.setText(bundle.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem3.setText(bundle.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        jMenu1.add(jMenuItem3);

        jMenuItem2.setText(bundle.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu2.setText(bundle.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem4.setText(bundle.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnDelPt)
                            .addComponent(btnAddPt))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(radioHardSnap)
                            .addComponent(radioElasticSnap))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 107, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(boxPreserve)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnApply))
                            .addComponent(btnBind100))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnAddBone)
                            .addComponent(btnDelBone))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddPt)
                    .addComponent(btnAddBone)
                    .addComponent(btnApply)
                    .addComponent(boxPreserve)
                    .addComponent(radioHardSnap))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelPt)
                    .addComponent(btnDelBone)
                    .addComponent(btnBind100)
                    .addComponent(radioElasticSnap))
                .addContainerGap())
        );

        setBounds(0, 0, 550, 555);
    }// </editor-fold>//GEN-END:initComponents
    public int pointCount = 0;
private void btnAddPtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddPtActionPerformed
    Object[] row = new Object[dims + 1];
    row[0] = pointCount++;
    for (int i = 1; i < dims + 1; i++) {
        row[i] = 0.0;
    }
    modelPoints.addRow(row);
}//GEN-LAST:event_btnAddPtActionPerformed

private void btnAddBoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBoneActionPerformed
    modelBones.addRow(new Object[]{-1, -1, 1});
}//GEN-LAST:event_btnAddBoneActionPerformed

private void btnDelPtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelPtActionPerformed
    int[] selected = tblPoints.getSelectedRows();
    for (int i = 0; i < selected.length; i++) {
        modelPoints.removeRow(selected[i] - i);
    }
}//GEN-LAST:event_btnDelPtActionPerformed

private void btnDelBoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelBoneActionPerformed
    int[] selected = tblBones.getSelectedRows();
    for (int i = 0; i < selected.length; i++) {
        modelBones.removeRow(selected[i] - i);
    }
}//GEN-LAST:event_btnDelBoneActionPerformed

private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
    if (parent.engine != null) {
        parent.engine.skeleton = new ArrayList<NLatticeBone>();
        HashMap<Integer, NVector> points = new HashMap<Integer, NVector>();
        for (int i = 0; i < modelPoints.getRowCount(); i++) {
            int index = (Integer) modelPoints.getValueAt(i, 0);
            NVector bucket = new NVector(dims);
            for (int j = 0; j < dims; j++) {
                bucket.coords[j] = (Double) modelPoints.getValueAt(i, j + 1);
            }
            points.put(index, bucket);
        }

        for (int i = 0; i < modelBones.getRowCount(); i++) {
            NVector a = points.get((Integer) modelBones.getValueAt(i, 0));
            NVector b = points.get((Integer) modelBones.getValueAt(i, 1));
            Double radius = (Double) modelBones.getValueAt(i, 2);
            if ((a != null) && (b != null) && (radius != null)) {
                parent.engine.skeleton.add(new NLatticeBone(dims, a, b, radius));
            }
        }
    }
}//GEN-LAST:event_btnApplyActionPerformed

private void btnBind100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBind100ActionPerformed
    if (groupSnapping.isSelected(radioHardSnap.getModel())) {
        for (int i = 0; i < 100; i++) {
            parent.engine.repel(0.0001);
            parent.engine.bindToSkeletonHard();
        }
        parent.dp.repaint();
    } else if (groupSnapping.isSelected(radioElasticSnap.getModel())) {
        for (int i = 0; i < 100; i++) {
            parent.engine.repel(0.0001);
            parent.engine.bindToSkeletonElastic();
        }
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnBind100ActionPerformed

    public JFileChooser chooser = new JFileChooser();
    {
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Skeleton file", "skl"));
    }

private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    // Save
    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        saveSkeleton(chooser.getSelectedFile());
    }
}//GEN-LAST:event_jMenuItem2ActionPerformed

private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    // Load
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        DefaultTableModel[] result = loadSkeleton(chooser.getSelectedFile());
        if (result[0] == null) {
            return;
        }
        int fileDims = result[0].getColumnCount();
        if (fileDims != dims + 1) {
            JOptionPane.showMessageDialog(null, "Number of dimensions doesn't match: " + fileDims);
            return;
        }
        modelPoints = result[0];
        modelBones = result[1];
        tblPoints.setModel(modelPoints);
        tblBones.setModel(modelBones);
    }    
}//GEN-LAST:event_jMenuItem1ActionPerformed

private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
    // Analyze
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        DefaultTableModel[] result = loadSkeleton(chooser.getSelectedFile());
        if (result[0] == null) {
            return;
        }
        int fileDims = result[0].getColumnCount();
        int pointCount = result[0].getRowCount();
        int boneCount = result[1].getRowCount();
        JOptionPane.showMessageDialog(null, "Dims: " + fileDims + "\nPoints: " + pointCount + "\nBones: " + boneCount);
    }    
}//GEN-LAST:event_jMenuItem4ActionPerformed
    public static final int VERSION = 1;

    public void saveSkeleton(File f) {
        try {
            FileOutputStream fos = new FileOutputStream(f);
            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(VERSION);
            dos.writeInt(dims);
            dos.writeInt(modelPoints.getRowCount());
            for (int i = 0; i < modelPoints.getRowCount(); i++) {
                dos.writeInt((Integer) modelPoints.getValueAt(i, 0));
                for (int j = 0; j < dims; j++) {
                    Double bucket = (Double) modelPoints.getValueAt(i, j + 1);
                    if (bucket == null) {
                        bucket = new Double(0);
                    }
                    dos.writeDouble(bucket);
                }
            }
            dos.writeInt(modelBones.getRowCount());
            for (int i = 0; i < modelBones.getRowCount(); i++) {
                dos.writeInt((Integer) modelBones.getValueAt(i, 0));
                dos.writeInt((Integer) modelBones.getValueAt(i, 1));
                Double bucket = (Double) modelBones.getValueAt(i, 2);
                if (bucket == null) {
                    bucket = new Double(0.5);
                }
                dos.writeDouble(bucket);
            }
            dos.flush();
            dos.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SkeletonForm.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SkeletonForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public DefaultTableModel[] loadSkeleton(File f) {
        DefaultTableModel[] result = new DefaultTableModel[2];
        result[0] = null;
        result[1] = null;
        try {
            FileInputStream fis = new FileInputStream(f);
            DataInputStream dis = new DataInputStream(fis);
            int version = dis.readInt();
            switch (version) {
                case 1:
                    int dims = dis.readInt();

                    String[] labels = new String[dims + 1];
                    labels[0] = "Index";
                    final Class[] extTypes = new Class[dims + 1];
                    extTypes[0] = java.lang.Integer.class;
                    final boolean[] extCanEdit = new boolean[dims + 1];
                    extCanEdit[0] = false;
                    for (int i = 1; i < dims + 1; i++) {
                        labels[i] = Integer.toString(i);
                        extTypes[i] = java.lang.Double.class;
                        extCanEdit[i] = true;
                    }
                    result[0] = new javax.swing.table.DefaultTableModel(new Object[][]{}, labels) {

                        Class[] types = extTypes;
                        boolean[] canEdit = extCanEdit;

                        public Class getColumnClass(int columnIndex) {
                            return types[columnIndex];
                        }

                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return canEdit[columnIndex];
                        }
                    };
                    result[1] = new javax.swing.table.DefaultTableModel(
                            new Object[][]{},
                            new String[]{
                                "Point 1", "Point 2", "Distance"
                            }) {

                        Class[] types = new Class[]{
                            java.lang.Integer.class, java.lang.Integer.class, java.lang.Double.class
                        };

                        public Class getColumnClass(int columnIndex) {
                            return types[columnIndex];
                        }
                    };

                    int rowCount = dis.readInt();
                    for (int i = 0; i < rowCount; i++) {
                        Object[] row = new Object[dims + 1];
                        row[0] = dis.readInt();
                        for (int j = 0; j < dims; j++) {
                            row[j + 1] = dis.readDouble();
                        }
                        result[0].addRow(row);

                    }
                    rowCount = dis.readInt();
                    for (int i = 0; i < rowCount; i++) {
                        Object[] row = new Object[3];
                        row[0] = dis.readInt();
                        row[1] = dis.readInt();
                        row[2] = dis.readDouble();
                        result[1].addRow(row);
                    }
                    dis.close();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Version not recognized: " + version);
                    break;
            }
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "File not found.");
        } catch (IOException ex) {
            Logger.getLogger(SkeletonForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SkeletonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SkeletonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SkeletonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SkeletonForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new SkeletonForm(null, 0).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox boxPreserve;
    private javax.swing.JButton btnAddBone;
    private javax.swing.JButton btnAddPt;
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnBind100;
    private javax.swing.JButton btnDelBone;
    private javax.swing.JButton btnDelPt;
    private javax.swing.ButtonGroup groupSnapping;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JRadioButton radioElasticSnap;
    private javax.swing.JRadioButton radioHardSnap;
    private javax.swing.JTable tblBones;
    private javax.swing.JTable tblPoints;
    // End of variables declaration//GEN-END:variables
}
