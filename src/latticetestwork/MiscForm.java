/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MiscForm.java
 *
 * Created on Nov 23, 2011, 10:54:23 AM
 */
package latticetestwork;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author mewer12
 */
public class MiscForm extends javax.swing.JFrame {

    public LatticeTestworkView parent = null;

    /** Creates new form MiscForm */
    public MiscForm(LatticeTestworkView parent) {
        this.parent = parent;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jPanel1 = new javax.swing.JPanel();
    bntBindSphere = new javax.swing.JButton();
    btnBindSphereX100 = new javax.swing.JButton();
    btnClearOuterFaces = new javax.swing.JButton();
    btnTruncate = new javax.swing.JButton();
    btnNewTruncate = new javax.swing.JButton();
    btnCheckIncomplete = new javax.swing.JButton();
    btnCheckDuplicates = new javax.swing.JButton();
    btnKatanaMath = new javax.swing.JButton();
    btnCellVolumes = new javax.swing.JButton();
    jButton1 = new javax.swing.JButton();
    btnFullCheckIncomplete = new javax.swing.JButton();
    btnAddGround = new javax.swing.JButton();
    editGroundElevation = new javax.swing.JTextField();
    btnMakeCompletePtsImmune = new javax.swing.JButton();
    btnClearImmunity = new javax.swing.JButton();
    btnClearLoggedDisplayPts = new javax.swing.JButton();
    btnAddFunction = new javax.swing.JButton();
    btnApplyFunction = new javax.swing.JButton();

    java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("latticetestwork/resources/MiscForm"); // NOI18N
    setTitle(bundle.getString("Form.title")); // NOI18N
    setName("Form"); // NOI18N

    jPanel1.setToolTipText(bundle.getString("jPanel1.toolTipText")); // NOI18N
    jPanel1.setName("jPanel1"); // NOI18N

    bntBindSphere.setText(bundle.getString("bntBindSphere.text")); // NOI18N
    bntBindSphere.setName("bntBindSphere"); // NOI18N
    bntBindSphere.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        bntBindSphereActionPerformed(evt);
      }
    });

    btnBindSphereX100.setText(bundle.getString("btnBindSphereX100.text")); // NOI18N
    btnBindSphereX100.setName("btnBindSphereX100"); // NOI18N
    btnBindSphereX100.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnBindSphereX100ActionPerformed(evt);
      }
    });

    btnClearOuterFaces.setText(bundle.getString("btnClearOuterFaces.text")); // NOI18N
    btnClearOuterFaces.setName("btnClearOuterFaces"); // NOI18N
    btnClearOuterFaces.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnClearOuterFacesActionPerformed(evt);
      }
    });

    btnTruncate.setText(bundle.getString("btnTruncate.text")); // NOI18N
    btnTruncate.setName("btnTruncate"); // NOI18N
    btnTruncate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnTruncateActionPerformed(evt);
      }
    });

    btnNewTruncate.setText(bundle.getString("btnNewTruncate.text")); // NOI18N
    btnNewTruncate.setName("btnNewTruncate"); // NOI18N
    btnNewTruncate.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnNewTruncateActionPerformed(evt);
      }
    });

    btnCheckIncomplete.setText(bundle.getString("btnCheckIncomplete.text")); // NOI18N
    btnCheckIncomplete.setName("btnCheckIncomplete"); // NOI18N
    btnCheckIncomplete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCheckIncompleteActionPerformed(evt);
      }
    });

    btnCheckDuplicates.setText(bundle.getString("btnCheckDuplicates.text")); // NOI18N
    btnCheckDuplicates.setName("btnCheckDuplicates"); // NOI18N
    btnCheckDuplicates.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCheckDuplicatesActionPerformed(evt);
      }
    });

    btnKatanaMath.setText(bundle.getString("btnKatanaMath.text")); // NOI18N
    btnKatanaMath.setName("btnKatanaMath"); // NOI18N
    btnKatanaMath.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnKatanaMathActionPerformed(evt);
      }
    });

    btnCellVolumes.setText(bundle.getString("btnCellVolumes.text")); // NOI18N
    btnCellVolumes.setName("btnCellVolumes"); // NOI18N
    btnCellVolumes.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnCellVolumesActionPerformed(evt);
      }
    });

    jButton1.setText(bundle.getString("jButton1.text")); // NOI18N
    jButton1.setName("jButton1"); // NOI18N
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        jButton1ActionPerformed(evt);
      }
    });

    btnFullCheckIncomplete.setText(bundle.getString("btnFullCheckIncomplete.text")); // NOI18N
    btnFullCheckIncomplete.setName("btnFullCheckIncomplete"); // NOI18N
    btnFullCheckIncomplete.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnFullCheckIncompleteActionPerformed(evt);
      }
    });

    btnAddGround.setText(bundle.getString("btnAddGround.text")); // NOI18N
    btnAddGround.setName("btnAddGround"); // NOI18N
    btnAddGround.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAddGroundActionPerformed(evt);
      }
    });

    editGroundElevation.setText(bundle.getString("editGroundElevation.text")); // NOI18N
    editGroundElevation.setToolTipText(bundle.getString("editGroundElevation.toolTipText")); // NOI18N
    editGroundElevation.setName("editGroundElevation"); // NOI18N

    btnMakeCompletePtsImmune.setText(bundle.getString("btnMakeCompletePtsImmune.text")); // NOI18N
    btnMakeCompletePtsImmune.setName("btnMakeCompletePtsImmune"); // NOI18N
    btnMakeCompletePtsImmune.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnMakeCompletePtsImmuneActionPerformed(evt);
      }
    });

    btnClearImmunity.setText(bundle.getString("btnClearImmunity.text")); // NOI18N
    btnClearImmunity.setName("btnClearImmunity"); // NOI18N
    btnClearImmunity.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnClearImmunityActionPerformed(evt);
      }
    });

    btnClearLoggedDisplayPts.setText(bundle.getString("btnClearLoggedDisplayPts.text")); // NOI18N
    btnClearLoggedDisplayPts.setName("btnClearLoggedDisplayPts"); // NOI18N
    btnClearLoggedDisplayPts.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnClearLoggedDisplayPtsActionPerformed(evt);
      }
    });

    btnAddFunction.setText(bundle.getString("MiscForm.btnAddFunction.text")); // NOI18N
    btnAddFunction.setName("btnAddFunction"); // NOI18N
    btnAddFunction.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnAddFunctionActionPerformed(evt);
      }
    });

    btnApplyFunction.setText(bundle.getString("MiscForm.btnApplyFunction.text")); // NOI18N
    btnApplyFunction.setName("btnApplyFunction"); // NOI18N
    btnApplyFunction.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnApplyFunctionActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(bntBindSphere)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnBindSphereX100)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 238, Short.MAX_VALUE)
            .addComponent(btnClearImmunity))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(btnClearOuterFaces)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
            .addComponent(btnMakeCompletePtsImmune))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(btnTruncate)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 205, Short.MAX_VALUE)
            .addComponent(btnClearLoggedDisplayPts))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(btnNewTruncate)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnCheckIncomplete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFullCheckIncomplete))
              .addComponent(btnCheckDuplicates)
              .addComponent(btnKatanaMath)
              .addComponent(btnCellVolumes)
              .addComponent(jButton1)
              .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(btnAddGround)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editGroundElevation, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
              .addComponent(btnAddFunction)
              .addComponent(btnApplyFunction))
            .addGap(0, 0, Short.MAX_VALUE)))
        .addContainerGap())
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(bntBindSphere)
          .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(btnBindSphereX100)
            .addComponent(btnClearImmunity)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(btnClearOuterFaces)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnTruncate)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnNewTruncate))
          .addGroup(jPanel1Layout.createSequentialGroup()
            .addComponent(btnMakeCompletePtsImmune)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnClearLoggedDisplayPts)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnCheckIncomplete)
          .addComponent(btnFullCheckIncomplete))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnCheckDuplicates)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnKatanaMath)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnCellVolumes)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jButton1)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnAddGround)
          .addComponent(editGroundElevation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnAddFunction)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnApplyFunction)
        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(25, Short.MAX_VALUE))
    );

    setBounds(50, 400, 522, 474);
  }// </editor-fold>//GEN-END:initComponents

private void bntBindSphereActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bntBindSphereActionPerformed
    if (parent.engine != null) {
        parent.engine.bindSphere();
        parent.dp.repaint();
    }
}//GEN-LAST:event_bntBindSphereActionPerformed

private void btnBindSphereX100ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBindSphereX100ActionPerformed
    if (parent.engine != null) {
        for (int i = 0; i < 100; i++) {
            parent.engine.repel(0.0001);
            parent.engine.bindSphere();
        }
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnBindSphereX100ActionPerformed

private void btnClearOuterFacesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearOuterFacesActionPerformed
    if (parent.engine != null) {
        parent.engine.clearOuterFaces();
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnClearOuterFacesActionPerformed

private void btnTruncateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTruncateActionPerformed
    if (parent.engine != null) {
        parent.engine.truncate();
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnTruncateActionPerformed

private void btnNewTruncateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewTruncateActionPerformed
    if (parent.engine != null) {
        parent.engine.newNCubeWithTruncate();
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnNewTruncateActionPerformed

private void btnCheckIncompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckIncompleteActionPerformed
    if (parent.engine != null) {
        int result = parent.engine.checkIncomplete();
        JOptionPane.showMessageDialog(rootPane, result + " faces affected.");
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnCheckIncompleteActionPerformed

private void btnCheckDuplicatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckDuplicatesActionPerformed
    if (parent.engine != null) {
        parent.engine.checkDuplicates();
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnCheckDuplicatesActionPerformed

private void btnKatanaMathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKatanaMathActionPerformed
    if (parent.engine != null) {
        parent.engine.katanaMath();
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnKatanaMathActionPerformed

private void btnCellVolumesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCellVolumesActionPerformed
    if (parent.engine != null) {
        JOptionPane.showMessageDialog(null, "Total volume: " + parent.engine.cellVolumes());
    }
}//GEN-LAST:event_btnCellVolumesActionPerformed

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    if (parent.engine != null) {
        Random r = new Random();
        if (parent.engine.skeleton.isEmpty()) {
            NVector bucket = new NVector(3);
            for (int j = 0; j < 3; j++) {
                bucket.coords[j] = (r.nextDouble() * 2) - 1;
            }
            NVector a = bucket;
            bucket = new NVector(3);
            for (int j = 0; j < 3; j++) {
                bucket.coords[j] = (r.nextDouble() * 2) - 1;
            }
            NVector b = bucket;
            parent.engine.skeleton.add(new NLatticeBone(3, a, b, 0.5));
        }
        for (int i = 0; i < 100; i++) {
            parent.engine.repel(0.0001);
            parent.engine.bindToSkeletonHard();
        }
        parent.dp.repaint();
    }
}//GEN-LAST:event_jButton1ActionPerformed

private void btnFullCheckIncompleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFullCheckIncompleteActionPerformed
    if (parent.engine != null) {
        int result = parent.engine.joinPreCells();
        JOptionPane.showMessageDialog(rootPane, result + " cells formed.");
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnFullCheckIncompleteActionPerformed

private void btnAddGroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddGroundActionPerformed
    if (parent.engine != null) {
        parent.engine.addGround(Double.valueOf(editGroundElevation.getText()), Engine.GROUND_COLORFUL);
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnAddGroundActionPerformed

private void btnClearImmunityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearImmunityActionPerformed
    if (parent.engine != null && parent.engine.lattice != null) {
        for (NPoint p : parent.engine.lattice.points) {
            p.immune = false;
        }
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnClearImmunityActionPerformed

private void btnMakeCompletePtsImmuneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMakeCompletePtsImmuneActionPerformed
    if (parent.engine != null && parent.engine.lattice != null) {
        for (NPoint p : parent.engine.lattice.points) {
            if (p.calcComplete()) {
                p.immune = true;
            }
        }
        parent.dp.repaint();
    }
}//GEN-LAST:event_btnMakeCompletePtsImmuneActionPerformed

private void btnClearLoggedDisplayPtsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearLoggedDisplayPtsActionPerformed
    if (parent.engine != null && parent.engine.lattice != null) {
        for (NPoint p : parent.engine.lattice.points) {
            p.displayed = false;
        }
    }
}//GEN-LAST:event_btnClearLoggedDisplayPtsActionPerformed

    private void btnAddFunctionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddFunctionActionPerformed
        if (parent.engine != null) {
            try {
                parent.engine.placeFunctionTest();
                parent.dp.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
                Logger.getLogger(MiscForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnAddFunctionActionPerformed

  private void btnApplyFunctionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyFunctionActionPerformed
        if (parent.engine != null) {
            try {
                parent.engine.applyFunctionTest();
                parent.dp.repaint();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
                Logger.getLogger(MiscForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
  }//GEN-LAST:event_btnApplyFunctionActionPerformed

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
            java.util.logging.Logger.getLogger(MiscForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MiscForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MiscForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MiscForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MiscForm(null).setVisible(true);
            }
        });
    }
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton bntBindSphere;
  private javax.swing.JButton btnAddFunction;
  private javax.swing.JButton btnAddGround;
  private javax.swing.JButton btnApplyFunction;
  private javax.swing.JButton btnBindSphereX100;
  private javax.swing.JButton btnCellVolumes;
  private javax.swing.JButton btnCheckDuplicates;
  private javax.swing.JButton btnCheckIncomplete;
  private javax.swing.JButton btnClearImmunity;
  private javax.swing.JButton btnClearLoggedDisplayPts;
  private javax.swing.JButton btnClearOuterFaces;
  private javax.swing.JButton btnFullCheckIncomplete;
  private javax.swing.JButton btnKatanaMath;
  private javax.swing.JButton btnMakeCompletePtsImmune;
  private javax.swing.JButton btnNewTruncate;
  private javax.swing.JButton btnTruncate;
  private javax.swing.JTextField editGroundElevation;
  private javax.swing.JButton jButton1;
  private javax.swing.JPanel jPanel1;
  // End of variables declaration//GEN-END:variables
}
