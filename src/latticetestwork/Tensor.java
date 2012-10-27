/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * If I actually understand what a tensor is, it's like a matrix of arbitrary dimensions.
 * Currently just holds values.
 * @author mewer12
 */
public class Tensor<T> {

    public int[] dims = {};
    public int[] digitValues = {};
    public T[] values = null;
//    public T example = null;

    /**
     * Not really meant to be used publicly.
     */
    public Tensor() {
    }

    public Tensor(T[] values, int... dims) {
        this.dims = dims;
        this.digitValues = new int[dims.length];
        int prod = 1;
        for (int i = 0; i < dims.length; i++) {
            this.digitValues[i] = prod; //TODO Check this.  I thik it should work.
            prod *= dims[i];
        }
        //TODO AAARGH JAVA Y U NO HAV GENERIK ARRY CONSTRUKTRS
        this.values = values;

    }

    public static Tensor<Integer> getIntTensor(int... dims) {
        Tensor<Integer> bucket = new Tensor<Integer>();
        bucket.dims = dims;
        int prod = 1;
        bucket.digitValues = new int[dims.length];
        for (int i = 0; i < dims.length; i++) {
            bucket.digitValues[i] = prod; //TODO Check this.  I thik it should work.
            prod *= dims[i];
        }
        bucket.values = new Integer[prod];
        return bucket;
    }

    public static Tensor<Color> getColorTensor(int... dims) {
        Tensor<Color> bucket = new Tensor<Color>();
        bucket.dims = dims;
        int prod = 1;
        bucket.digitValues = new int[dims.length];
        for (int i = 0; i < dims.length; i++) {
            bucket.digitValues[i] = prod; //TODO Check this.  I thik it should work.
            prod *= dims[i];
        }
        bucket.values = new Color[prod];
        return bucket;
    }

    public T get(int... coord) {
        return this.values[Tensor.coordValue(digitValues, coord)];
    }

    public void put(T value, int... coord) {
        this.values[Tensor.coordValue(digitValues, coord)] = value;
    }

    public static int coordValue(int[] digitValues, int[] coord) {
        int sum = 0;
        for (int i = 0; i < coord.length; i++) {
            sum += coord[i] * digitValues[i];
        }
        return sum;
    }

    public void toBytes(DataOutputStream dos) throws IOException {
        String classType = null;
        if (values != null) {
            classType = values.getClass().getComponentType().toString();
        }
        
        if (classType == null) {
            dos.writeUTF("null");
            dos.writeInt(digitValues.length);
            for (int i = 0; i < digitValues.length; i++) {
                dos.writeInt(digitValues[i]);
            }
            dos.writeInt(dims.length);
            for (int i = 0; i < dims.length; i++) {
                dos.writeInt(dims[i]);
            }
        } else if ("class java.awt.Color".equals(classType)) {
            dos.writeUTF(classType);
            dos.writeInt(digitValues.length);
            for (int i = 0; i < digitValues.length; i++) {
                dos.writeInt(digitValues[i]);
            }
            dos.writeInt(dims.length);
            for (int i = 0; i < dims.length; i++) {
                dos.writeInt(dims[i]);
            }
            dos.writeInt(values.length);
            for (int i = 0; i < values.length; i++) {
                Color c = (Color) values[i];
                dos.writeByte(c.getRed());
                dos.writeByte(c.getGreen());
                dos.writeByte(c.getBlue());
                dos.writeByte(c.getAlpha());
            }
        }
    }
    
    public static Tensor fromBytes(DataInputStream dis) throws IOException {
        String classType = dis.readUTF();
        
        if ("null".equals(classType)) {
            Tensor result = new Tensor();
            result.digitValues = new int[dis.readInt()];
            for (int i = 0; i < result.digitValues.length; i++) {
                result.digitValues[i] = dis.readInt();
            }
            result.dims = new int[dis.readInt()];
            for (int i = 0; i < result.dims.length; i++) {
                result.dims[i] = dis.readInt();
            }
            result.values = null;
            return result;
        } else if ("class java.awt.Color".equals(classType)) {
            Tensor<Color> result = new Tensor<Color>();
            result.digitValues = new int[dis.readInt()];
            for (int i = 0; i < result.digitValues.length; i++) {
                result.digitValues[i] = dis.readInt();
            }
            result.dims = new int[dis.readInt()];
            for (int i = 0; i < result.dims.length; i++) {
                result.dims[i] = dis.readInt();
            }
            result.values = new Color[dis.readInt()];
            for (int i = 0; i < result.values.length; i++) {
                int r = dis.readUnsignedByte();
                int g = dis.readUnsignedByte();
                int b = dis.readUnsignedByte();
                int a = dis.readUnsignedByte();
                result.values[i] = new Color(r,g,b,a);
            }
            return result;
        } else {
            return null;
        }
    }
}