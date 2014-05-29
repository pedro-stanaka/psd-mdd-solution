package Model;


/**
 *
 *
 * @author jpescola
 *
 * ATENÇÃO: LONG TROCADO POR INT
 *
 */
public class Wavelet {

    public double[] transformada_wavelet_nivel_k(double[] f, int inicio, int n, int nivel, char ordem, double[] filtro) {

        double[] sf = filtro;
        int csf = sf.length;
        double[] wf = new double[csf];
        for (int i = 0; i < csf; i++) {
            wf[i] = sf[csf - i - 1];
            if (i % 2 != 0) {
                wf[i] *= -1;
            }
        }
        int cwf = csf;
        int j = 0;
        double[] t = new double[n];
        if (ordem == 'n') // n de normal para wavelet
        {
            for (int i = 0; i < n; i += 2) //trend
            {
                t[j] = 0;
                for (int k = 0; k < csf; k++) {
                    t[j] += f[inicio + (i + k) % n] * sf[k];
                }
                j++;
            }
            for (int i = 0; i < n; i += 2) //fluctuation
            {
                t[j] = 0;
                for (int k = 0; k < cwf; k++) {
                    t[j]+=f[inicio+(i+k)%n]*wf[k];
                    //System.out.println(inicio+(i+k)%n);
                    //try{System.in.read();}catch(Exception e){}

                }
                j++;
            }
        } else // i de invertido para wavelet packet
        {
            for (int i = 0; i < n; i += 2) //fluctuation
            {
                t[j] = 0;
                for (int k = 0; k < cwf; k++) {
                    t[j] += f[inicio + (i + k) % n] * wf[k];
                }
                j++;
            }
            for (int i = 0; i < n; i += 2) //trend
            {
                t[j] = 0;
                for (int k = 0; k < csf; k++) {
                    t[j] += f[inicio + (i + k) % n] * sf[k];
                }
                j++;
            }
        }
        for (int i = 0; i < n; i++) {
            f[inicio + i] = t[i];
        }
        nivel--;
        n /= 2;

        if (nivel > 0) {
            transformada_wavelet_nivel_k(f, inicio, n, nivel, ordem, filtro);
        }


        return f;
    }

    public double[] transformada_wavelet_packet_nivel_k(Wave wave, int nivel, double[] filtro) {
        int inicio = 0;
        int comprimento = wave.getTamanho();
        for (int i = 1; i <= nivel; i++) // por exemplo, para nivel 5, vou chamar 5 vezes a fun?o de tranasformada, cada vez em n?el 1.
        {
            inicio = 0;
            comprimento = (int) (wave.getTamanhoChunk() / Math.pow(2, i - 1));
            for (int j = 0; j < Math.pow(2, i - 1); j++) {
                if (j % 2 == 0) {
                    transformada_wavelet_nivel_k(wave.getDoubleData(), inicio, comprimento, 1, 'n', filtro); // n de ordem normal: primeiro passa-baixa e depois passa-alta
                } else {
                    transformada_wavelet_nivel_k(wave.getDoubleData(), inicio, comprimento, 1, 'i', filtro); // i de invertido: primeiro passa-alta e depois passa-baixa
                }
                inicio += comprimento;
                //System.out.println("inicio: "+inicio);
            }
        }
        return wave.getDoubleData();
    }

        public double[] transformada_wavelet_packet_nivel_k(double[] sinal, int nivel, double[] filtro) {
        int inicio = 0;
        int comprimento = sinal.length;
        for (int i = 1; i <= nivel; i++) // por exemplo, para nivel 5, vou chamar 5 vezes a fun?o de tranasformada, cada vez em n?el 1.
        {
            inicio = 0;
            comprimento = (int) (sinal.length / Math.pow(2, i - 1));
            for (int j = 0; j < Math.pow(2, i - 1); j++) {
                if (j % 2 == 0) {
                    transformada_wavelet_nivel_k(sinal, inicio, comprimento, 1, 'n', filtro); // n de ordem normal: primeiro passa-baixa e depois passa-alta
                } else {
                    transformada_wavelet_nivel_k(sinal, inicio, comprimento, 1, 'i', filtro); // i de invertido: primeiro passa-alta e depois passa-baixa
                }
                inicio += comprimento;
                //System.out.println("inicio: "+inicio);
            }
        }
        return sinal;
    }
}
