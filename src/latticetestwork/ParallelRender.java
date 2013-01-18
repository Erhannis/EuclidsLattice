/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

/**
 *
 * @author erhannis
 */
public class ParallelRender {
    
//    public Tensor<Integer> aRender(double dtl, int... picDims) {
//        // So, the idea is to sweep from corner to corner of the aperture and ping pixels.
//        Tensor<Integer> result = Tensor.getIntTensor(picDims);
//        //TODO Sweep like the wind!
//        int[] coord = new int[picDims.length];
//        aRenderRecurse(picDims, coord, 0, result, dtl);
//        return result;
//    }
//    
//    public void aRenderRecurse(int[] picDims, int[] coord, int index, Tensor<Color> result, double dtl) {
//        if (index < picDims.length) {
//            if (latticeDims == 2 && picDims.length == 2 && index == 1) {
//                coord[index] = 0;
//                aRenderRecurse(picDims, coord, index + 1, result, dtl);
//                Color col = result.get(coord[0], 0);
//                for (int y = 0; y < picDims[1]; y++) {
//                    coord[1] = y;
//                    result.put(col, coord);
//                }
//            } else {
//                for (int i = 0; i < picDims[index]; i++) {
//                    coord[index] = i;
//                    aRenderRecurse(picDims, coord, index + 1, result, dtl);
//                }
//            }
//        } else {//System.out.println(orientation[2]);
//            NVector dir = new NVector(dims);
//            //NVector apDir = aperture[0];
//            //TODO Fix this horrible patched mess here.  (Should have aperture.)  I just want to see the pretty pictures!
//            // Actually, this works pretty well.  I might leave it.
//            dir = dir.plusB(orientation[0].multS(0.5));
//            for (int i = 1; i < latticeDims; i++) { //THINK ARGH, is this even the right bound?
//                dir = dir.plusB(orientation[i].multS((((double)coord[i - 1]) / picDims[i - 1]) - 0.5)); //THINK Ugh.  Way jury-rigged.
//            }
//            SimplePhoton photon = new SimplePhoton(dims, pos, dir, Color.BLACK, cell, dtl);            
//            //result.put(Color.blue, coord);
//            result.put(photon.proceed(), coord);
//        }
//    }
}
