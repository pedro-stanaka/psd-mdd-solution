package br.uel.mdd.utils;

/**
 * Created by pedro on 16/04/14.
 */
public class MyArrayUtils {
    /**
     *
     * @param firstArray
     * @param secondArray
     * @param <T>
     * @return An array with the following layout: <br/>
     *      [firstArray[0], secondArray[0], firstArray[1], secondArray[1] ...]
     */
    public static  <T> T[] concatenateInsert(T[]firstArray, T[]secondArray){
        T[] result = null;
        if(firstArray.length != secondArray.length){
            throw new IllegalArgumentException("The given arrays must have the same size/length.");
        }else{
            result = (T[]) java.lang.reflect.Array.newInstance(firstArray.getClass().getComponentType(), firstArray.length*2);
            for (int i = 0, j = 0; i < firstArray.length; i++, j+=2) {
               result[j] = firstArray[i];
               result[j+1] = secondArray[i];
            }
        }
        return result;
    }

    public static  double[] concatenateInsert(double[] firstArray, double[] secondArray){
        double[] result = null;
        if(firstArray.length != secondArray.length){
            throw new IllegalArgumentException("The given arrays must have the same size/length.");
        }else{
            result = new double[firstArray.length * 2];
            for (int i = 0, j = 0; i < firstArray.length; i++, j+=2) {
                result[j] = firstArray[i];
                result[j+1] = secondArray[i];
            }
        }
        return result;
    }
}
