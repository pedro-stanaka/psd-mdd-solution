
package br.uel.mdd;


import br.uel.mdd.utils.MyArrayUtils;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;

import java.util.Arrays;

public class FourierTransform {



    public double[] vfft(double[] entry, double[] imaginary){
       DoubleFFT_1D dfft = new DoubleFFT_1D(entry.length);
        System.out.println(entry.length);
       double[] result = MyArrayUtils.concatenateInsert(entry, imaginary);
       dfft.complexForward(result);
       return  result;
    }

    public double[] iVfft(double[] a, boolean changeScale){
        DoubleFFT_1D doubleFFT_1D = new DoubleFFT_1D(a.length-2);
        doubleFFT_1D.complexInverse(a, changeScale);
        return extractReal(a);
    }


    public double[] extractReal(double[] data){
        double[] ret = new double[data.length/2];
        int j = 0;
        for (int i = 0; i < data.length; i++) {
            if(i%2==0){
                ret[j++] = data[i];
            }
        }
        return ret;
    }

    public double[] discreteFourierTransform(double[] sinal, int comprimento) {
        double[] re = new double[comprimento];
        double[] im = new double[comprimento];
        for (int u = 0; u < comprimento - 1; u++) {
            re[u] = 0;
            im[u] = 0;
            for (int i = 0; i < comprimento - 1; i++) {
                re[u] += (sinal[i] * Math.cos(2.0 * 3.1415927 * (double) u * (double) i / (double) comprimento));
                im[u] -= (sinal[i] * Math.sin(2.0 * 3.1415927 * (double) u * (double) i / (double) comprimento));
            }
            re[u] /= (double) comprimento;
            im[u] /= (double) comprimento;
        }
        for (int j = 0; j < comprimento - 1; j++) {
            sinal[j] = Math.log10(Math.sqrt((re[j] * re[j]) + (im[j] * im[j])));
        }
        return Arrays.copyOfRange(sinal, 0, comprimento);
    }


    public double[] fft(double[] values){
        double[] im = new double[values.length];
        return fastFourierTransform(values, im, direct);
    }

    public double[] ifft(double [] real, double[] imaginary){
        return fastFourierTransform(real, imaginary, false);
    }

    public double[] fastFourierTransform(final double[] inputReal, double[] inputImaginary, boolean direct) {

        int inputLength = inputReal.length;

        // Encontrando a potencia de 2
        double ld = Math.log(inputLength) / Math.log(2.0);
        if (((int) ld) - ld != 0) {
            System.out.println("O número tem que ser uma potência de 2.");
            return null;
        }

        int nu = (int) ld;
        int n2 = inputLength / 2;
        int nu1 = nu - 1;
        double[] xReal = new double[inputLength];
        double[] xImag = new double[inputLength];
        double tReal, tImag, p, arg, c, s;

        double constant;
        if (direct) {
            constant = -2 * Math.PI;
        }
        else {
            constant = 2 * Math.PI;
        }

        for (int i = 0; i < inputLength; i++) {
            xReal[i] = inputReal[i];
            xImag[i] = inputImaginary[i];
        }

        int k = 0;
        for (int l = 1; l <= nu; l++) {
            while (k < inputLength) {
                for (int i = 1; i <= n2; i++) {
                    p = bitReverseReference(k >> nu1, nu);
                    arg = constant * p / inputLength;
                    c = Math.cos(arg);
                    s = Math.sin(arg);
                    tReal = xReal[k + n2] * c + xImag[k + n2] * s;
                    tImag = xImag[k + n2] * c - xReal[k + n2] * s;
                    xReal[k + n2] = xReal[k] - tReal;
                    xImag[k + n2] = xImag[k] - tImag;
                    xReal[k] += tReal;
                    xImag[k] += tImag;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 /= 2;
        }

        k = 0;
        int r;
        while (k < inputLength) {
            r = bitReverseReference(k, nu);
            if (r > k) {
                tReal = xReal[k];
                tImag = xImag[k];
                xReal[k] = xReal[r];
                xImag[k] = xImag[r];
                xReal[r] = tReal;
                xImag[r] = tImag;
            }
            k++;
        }

        double[] newArray = new double[xReal.length * 2];
        double radice = 1 / Math.sqrt(inputLength);
        for (int i = 0; i < newArray.length; i += 2) {
            int i2 = i / 2;
            newArray[i] = xReal[i2] * radice;
            newArray[i + 1] = xImag[i2] * radice;
        }
        return newArray;
    }


    private int bitReverseReference(int j, int nu) {
        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++) {
            j2 = j1 / 2;
            k = 2 * k + j1 - 2 * j2;
            j1 = j2;
        }
        return k;
    }

}

