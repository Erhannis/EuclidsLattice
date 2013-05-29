/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package latticetestwork;

import java.lang.reflect.Array;
import java.util.Random;

/**
 *
 * @author erhannis
 */
public class MeUtils {

    public static Object concatArrays(Object... arrays) {
        int length = 0;
        if (arrays.length == 0) {
            return null;
        }
        Class<?> c = null;
        for (int i = 0; i < arrays.length; i++) {
            if (arrays[i] != null) {
                c = arrays[i].getClass();
                break;
            }
        }
        if (c == null || !c.isArray()) {
            return null;
        }
//        try {
        Class<?> ctype = c.getComponentType();

        if (ctype == byte.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((byte[])arrays[i]).length;
                }
            }
            byte[] result = new byte[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((byte[])arrays[i]).length);
                    index += ((byte[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == short.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((short[])arrays[i]).length;
                }
            }
            short[] result = new short[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((short[])arrays[i]).length);
                    index += ((short[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == int.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((int[])arrays[i]).length;
                }
            }
            int[] result = new int[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((int[])arrays[i]).length);
                    index += ((int[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == long.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((long[])arrays[i]).length;
                }
            }
            long[] result = new long[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((long[])arrays[i]).length);
                    index += ((long[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == float.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((float[])arrays[i]).length;
                }
            }
            float[] result = new float[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((float[])arrays[i]).length);
                    index += ((float[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == double.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((double[])arrays[i]).length;
                }
            }
            double[] result = new double[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((double[])arrays[i]).length);
                    index += ((double[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == boolean.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((boolean[])arrays[i]).length;
                }
            }
            boolean[] result = new boolean[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((boolean[])arrays[i]).length);
                    index += ((boolean[])arrays[i]).length;
                }
            }
            return result;
        } else if (ctype == char.class) {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((char[])arrays[i]).length;
                }
            }
            char[] result = new char[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((char[])arrays[i]).length);
                    index += ((char[])arrays[i]).length;
                }
            }
            return result;
        } else {
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    length += ((Object[])arrays[i]).length;
                }
            }
            Object result = null;
            result = Array.newInstance(ctype, length); // AHA!  I HAVE FINALLY FOUND
                                                       //   VICTORY OVER JAVA AND ITS
                                                       //   GENERICS! - see http://stackoverflow.com/questions/529085/java-how-to-generic-array-creation
                                                       //   Well...most of a victory.
                                                       //   Still not total.  Grrr.
            //Object[] result = new Object[length];
            int index = 0;
            for (int i = 0; i < arrays.length; i++) {
                if (arrays[i] != null) {
                    System.arraycopy(arrays[i], 0, result, index, ((Object[])arrays[i]).length);
                    index += ((Object[])arrays[i]).length;
                }
            }
            return result;
        }
    }
    
    public static interface RecursionRunnable {
        public void run(int... dims);
    }
    
    public static void runRecursion(RecursionRunnable r, int level, int[] indices, int[] dims) {
        if (level < dims.length) {
            for (int i = 0; i < dims[level]; i++) {
                indices[level] = i;
                runRecursion(r, level + 1, indices, dims);
            }
        } else {
            r.run(indices);
        }
    }

    public static void runRecursion(RecursionRunnable r, int... dims) {
        int[] indices = new int[dims.length];
        runRecursion(r, 0, indices, dims);
    }
    
    public static Object randomDArray(final Random r, int... dimensions) {
        final Object result = Array.newInstance(double.class, dimensions);
        runRecursion(new RecursionRunnable() {
            public void run(int... index) {
                setInNArray(result, r.nextDouble(), index);
            }
        }, dimensions);
        return result;
    }
    
    public static Object getFromNArray(Object array, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        return bucket;
    }

    public static void setInNArrayObj(Object array, Object value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((Object[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, byte value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((byte[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, short value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((short[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, int value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((int[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, long value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((long[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, float value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((float[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, double value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((double[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, boolean value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((boolean[])bucket)[index[index.length - 1]] = value;
    }

    public static void setInNArray(Object array, char value, int... index) {
        Object bucket = array;
        for (int i = 0; i < index.length - 1; i++) {
            bucket = ((Object[])bucket)[index[i]];
        }
        ((char[])bucket)[index[index.length - 1]] = value;
    }
}
