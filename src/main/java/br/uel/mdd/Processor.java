package br.uel.mdd;

import br.uel.mdd.utils.MyArrayUtils;
import br.uel.mdd.wave.Wave;
import br.uel.mdd.wave.WaveHeader;
import com.google.common.primitives.Doubles;
import math.jwave.Transform;
import math.jwave.exceptions.JWaveFailure;
import math.jwave.transforms.FastWaveletTransform;
import math.jwave.transforms.WaveletPacketTransform;
import math.jwave.transforms.wavelets.Haar1;
import math.jwave.transforms.wavelets.Wavelet;
import math.jwave.transforms.wavelets.daubechies.Daubechies20;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.io.IOException;
import java.io.InputStream;
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
        Transform transform;
        try {
            transform = new Transform(new WaveletPacketTransform(new Haar1()));


            assert transform != null;
            double[] dataFreq;
            double[] data = Arrays.copyOf(waveFile.getDataValues(), findNextTwoPotency(waveFile.getDataValues().length));

            dataFreq = transform.forward(data);

            int lowBoundary = findSampleFromSampleRate(dataFreq.length, waveFile.getHeader().getSampleRate(), 65);
            int highBoundary = findSampleFromSampleRate(dataFreq.length, waveFile.getHeader().getSampleRate(), 255);

            double energy = getEnergy(Arrays.copyOfRange(dataFreq, lowBoundary, highBoundary));
//            System.out.println("ENERGY " + space + " " + energy);
            double energyTotal = getEnergy(dataFreq);
//            System.out.println("ENERGY TOTAL " + space + " " + energyTotal);
            double perc = (energy * 100) / energyTotal;
//            System.out.println("PERC: " + perc);

//            System.out.println("BOUNDARIES:\tM="+lowBoundary + "  H="+ highBoundary + "  DIFF=" + (highBoundary-lowBoundary));

//            ChartCreator cc = new ChartCreator(space);
//            cc.addValues(dataFreq);
//            System.out.println(dataFreq.length);
//            cc.createLineChart("DFT", "X", "Y");


//            System.out.println("\n\n");
            if (perc > 44 || perc < 1.0) {
                return false;
            } else {
                return true;
            }
        } catch (JWaveFailure jWaveFailure) {
            jWaveFailure.printStackTrace();
        }


        return false;
    }


    /**
     * @param freqValues        values returned from fft
     * @param freqPass          Start of the stop band in Hz
     * @param attenuationDegree A value of attenuation from 1 to 10
     * @return filteredValues
     */
    public double[] fineLowPass(double[] freqValues, int freqPass, int attenuationDegree) {

        int lowerBound = findSampleFromSampleRate(freqValues.length, waveFile.getHeader().getSampleRate(), freqPass);

        List<Double> valuesList = Doubles.asList(freqValues);
        double max = Collections.max(valuesList);
        double[] result = freqValues.clone();

        Arrays.fill(result, lowerBound, result.length, max / attenuationDegree);

        return result;
    }

    public Processor removeWhiteNoise(Wave noise, Wave sound) {
        int windowSize = 2048;
        double[] noiseValues = noise.getDataValues();
        double[] soundValues = sound.getDataValues();
        FourierTransform transform = new FourierTransform();

        double[] finalValues = new double[soundValues.length];
        double[] noiseWindowed = Arrays.copyOfRange(noiseValues, 22 * windowSize, 23 * windowSize);
        double[] noiseFft = getRealPart(transform.fft(noiseWindowed, true));

        List<Value> pitches = getPitches(noiseFft, 3);

        int windowsNumber = soundValues.length / windowSize;
        for (int i = 0; i < windowsNumber ; i++) {
            double[] soundWindowed = Arrays.copyOfRange(soundValues, i * windowSize, (i + 1) * windowSize);

            double[] soundWindowedFft  = transform.fft(soundWindowed, true);
            double[] soundWindowedFftReal = getRealPart(soundWindowedFft);
            for (Value pitch : pitches) {
                int pitchFrequency = pitch.getPos();

                double ratio = Math.pow(pitch.getAmplitude(), 2) / getEnergy(noiseFft);

                double oldSampleValue = Math.pow(soundWindowedFftReal[pitchFrequency], 2);

                soundWindowedFftReal[pitchFrequency] *= ratio;

                double amountToBeDistributed = oldSampleValue - soundWindowedFftReal[pitchFrequency];

                double ratioDistribution = amountToBeDistributed / (soundWindowedFftReal.length - 1);

                for (int k = 0; k < soundWindowedFftReal.length; k++) {
                    if (k != pitchFrequency) {
                        double energySignal = Math.pow(soundWindowedFftReal[k], 2);
                        energySignal += ratioDistribution;
                        if (soundWindowedFftReal[k] < 0) {
                            soundWindowedFftReal[k] = Math.sqrt(energySignal) * -1;
                        } else {
                            soundWindowedFftReal[k] = Math.sqrt(energySignal);
                        }
                    }
                }

                double[] ifft = transform.ifft(soundWindowedFftReal, mirrorReal(soundWindowedFftReal));
                System.arraycopy(ifft, 0, finalValues, i * windowSize, windowSize);
            }
        }

        waveFile.setDataValues(finalValues);
        return this;
    }

    public Processor removeNoiseFft(String fileName) {
        FourierTransform transform = new FourierTransform();
        double[] values = Arrays.copyOf(this.waveFile.getDataValues(), findNextTwoPotency(waveFile.getDataValues().length));
        double[] fftResult = transform.fft(values, true);

        double[] realFilteredSignal = fineLowPass(fftResult, 3000, 10);

        double[] ifftResults = transform.ifft(realFilteredSignal, mirrorReal(realFilteredSignal));

        this.waveFile.setDataValues(ifftResults);

        return this;
    }


    public Processor removeWhiteNoise(Wave whiteNoise) {


        return this;
    }

    public double[] bandPassFilter(double[] freqValues, int bandBegin, int bandEnd, int sampleRate, int aPass, int aStop) {
        int lowerBound = findSampleFromSampleRate(freqValues.length, sampleRate, bandBegin);
        int upperBound = findSampleFromSampleRate(freqValues.length, sampleRate, bandEnd);

        System.out.println(lowerBound);
        System.out.println(upperBound);

        double[] result = Arrays.copyOf(freqValues, freqValues.length);
        for (int i = 0; i < lowerBound; i++) {
            if (result[i] > aPass) {

            }
        }
        System.out.println(result.length);
        Arrays.fill(result, 0, lowerBound, 0.0);
        Arrays.fill(result, upperBound, freqValues.length, 0.0);
        return result;
    }

    public double[] mirrorReal(double[] real) {
        int j = 0;
        double[] mirror = new double[real.length];
        for (int i = real.length - 1; i >= 0; i--) {
            mirror[j] = real[i];
        }
        return real;
    }


    private double getEnergy(double[] data) {
        double energy = 0;

        for (int i = 0; i < data.length; i++) {
            energy += Math.pow(data[i], 2);
        }

        return energy;
    }

    private double[] getRealPart(double[] fftResult) {
        return Arrays.copyOf(fftResult, fftResult.length / 2);
    }

    public List<Value> getPitches(double[] signal, int maxElements) {

        PriorityQueue<Value> q = new PriorityQueue<Value>(100, new Comparator<Value>() {
            @Override
            public int compare(Value o1, Value o2) {
                return (o1.getAmplitude() > o2.getAmplitude()) ? -1 : (o1.getAmplitude() == o2.getAmplitude()) ? 0 : 1;
            }
        });

        for (int i = 0; i < signal.length; i++) {
            Value value = new Value(i, signal[i]);
            q.add(value);
        }

        List<Value> positions = new ArrayList<Value>();

        for (int i = 0; i < maxElements; i++) {
            positions.add(q.poll());
        }

        Value v = q.poll();
        while (v.getAmplitude() == positions.get(maxElements - 1).getAmplitude()) {
            positions.add(v);
            v = q.poll();
        }

        return positions;
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

    public void playMusic(InputStream file) {
        try {
            AudioStream audioStream = new AudioStream(file);
            AudioPlayer.player.start(audioStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
