package br.uel.mdd;

import br.uel.mdd.utils.MyArrayUtils;
import br.uel.mdd.wave.Wave;
import br.uel.mdd.wave.WaveHeader;
import math.jwave.Transform;
import math.jwave.exceptions.JWaveFailure;
import math.jwave.transforms.FastWaveletTransform;
import math.jwave.transforms.wavelets.Wavelet;
import math.jwave.transforms.wavelets.daubechies.Daubechies20;

import java.io.IOException;
import java.util.*;

/**
 * @author ${user}
 * @TODO Auto-generated comment
 * <p/>
 * Created by pedro on 29/05/14.
 */
public class Processor {

    private Wave waveFile;

    private static final String RESOURCES_FOLDER = "src/main/resources/";

    public Processor(Wave wave) {
        this.waveFile = wave;
    }


    public Processor putEcho(double seconds) {

        int valuesSize = waveFile.getDataValues().length;
        try {
            double samplingFactor = Math.floor(waveFile.getDataValues().length / (waveFile.getHeader().getSampleRate() * seconds));
            double[] data = this.downSample(this.waveFile, (int) samplingFactor);
            int sampleRate = (int) ((int) ((samplingFactor - 1) * waveFile.getHeader().getSampleRate()) / samplingFactor);


            int halfSecondSamples = sampleRate / 2;
            double[] dataEcho = Arrays.copyOf(data, data.length + (int) (sampleRate * seconds));


            for (int i = 0; i < (seconds / 0.5); i++) {
                System.arraycopy(data, data.length - halfSecondSamples, dataEcho, data.length + ((i) * halfSecondSamples), halfSecondSamples);
            }


            double gain = 1, step = 1 / sampleRate * seconds;
            for (int i = data.length; i < dataEcho.length; i++) {
                dataEcho[i] = dataEcho[i] * gain;
                gain -= step;
            }

            this.waveFile.setDataValues(dataEcho);

            this.waveFile.getHeader().setSampleRate(sampleRate);
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public double[] lowPassFilter(double[] freqValues, int cutThreshold) {
        double[] result = Arrays.copyOf(freqValues, freqValues.length);
        Arrays.fill(result, cutThreshold, freqValues.length, 0.0);
        return result;
    }


    public int findNextTwoPotency(int value) {
        return (int) Math.pow(2, (int) (Math.log((double) value) / Math.log(2)) + 1);
    }

    public static int findSampleFromSampleRate(int dataLength, int sampleRate, int desiredFreq) {
        return ((desiredFreq * dataLength) / (sampleRate / 2));
    }


    public double[] highPassFilter(double[] freqValues, int cutThreshold) {
        double[] result = freqValues.clone();
        Arrays.fill(result, 0, cutThreshold, 0.0);
        return result;
    }

    private double[] applyHighPass(int cutFreq, double[] dataValues, int sampleRate) {
        Wavelet wavelet = new Daubechies20();
        Transform transform = null;
        try {
            transform = new Transform(new FastWaveletTransform(wavelet));

            int nextTwoPotency = findNextTwoPotency(dataValues.length);
            int cut = findSampleFromSampleRate(nextTwoPotency, sampleRate, cutFreq);

            double[] values = Arrays.copyOf(dataValues, nextTwoPotency);
            double[] fftRes;
            fftRes = transform.forward(values);
            double[] newVals = highPassFilter(fftRes, cut);
            return Arrays.copyOf(transform.reverse(newVals), dataValues.length);
        } catch (JWaveFailure jWaveFailure) {
            jWaveFailure.printStackTrace();
            return null;
        }
    }

    /**
     * @param wave           Wave instance
     * @param samplingFactor An positive integer greater than one. Should not be very big, otherwise aliasing
     * @return WaveProcessor The instance of this class (this).
     * @TODO documentation here
     * @todo Create an alternative way to downsample in order to avoid aliasing
     */
    public double[] downSample(Wave wave, int samplingFactor) throws IOException {

        int newLength = (int) ((1 - (1.0 / samplingFactor)) * wave.getDataValues().length);
        double[] newData = new double[newLength];
        int j = 0;
        for (int i = 0; i < wave.getDataValues().length; i++) {
            if (i % samplingFactor != 0) {
                newData[j++] = wave.getDataValues()[i];
            }
        }
        return newData;
    }


    public void saveNewWave(String fileName) {
        this.waveFile.saveFile(this.waveFile.getDataValues(), RESOURCES_FOLDER + fileName);
    }

    public Processor toStereo() {
        double[] low = applyHighPass(1000, waveFile.getDataValues(), waveFile.getHeader().getSampleRate());
        double[] result;

        result = MyArrayUtils.concatenateInsert(low, waveFile.getDataValues());

        WaveHeader header = waveFile.getHeader();
        header.setNumChannels(2);
        this.waveFile.setHeader(header);

        this.waveFile.setDataValues(result);
        return this;
    }

    public boolean isHumanVoice() {
        double[] data = waveFile.getDataValues();
        PriorityQueue<Value> q = new PriorityQueue<Value>(100, new Comparator<Value>() {
            @Override
            public int compare(Value o1, Value o2) {
                return (o1.getAmplitude() > o2.getAmplitude()) ? -1 : (o1.getAmplitude() == o2.getAmplitude()) ? 0 : 1;
            }
        });


        List<Value> positions = new ArrayList<Value>();

        for (int i = 0; i < data.length; i++) {
            Value value = new Value(i, data[i]);
            q.add(value);
        }

        Value v = q.poll();
        while (v.getAmplitude() == positions.get(3 - 1).getAmplitude()) {
            positions.add(v);
            v = q.poll();
        }
        int lowBoundary = findSampleFromSampleRate(waveFile.getDataValues().length, waveFile.getHeader().getSampleRate(), 85);
        int highBoundary = findSampleFromSampleRate(waveFile.getDataValues().length)
        boolean isHumanVoice = true;
        for (Value position : positions) {
            isHumanVoice = isHumanVoice && (position.getPos() < 220);
        }



        return false;
    }

    private class Value {
        private int pos;
        private double amplitude;

        private Value(int pos, double amplitude) {
            this.pos = pos;
            this.amplitude = amplitude;
        }

        public int getPos() {
            return pos;
        }

        public double getAmplitude() {
            return amplitude;
        }
    }

}
