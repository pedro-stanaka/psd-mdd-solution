package br.uel.mdd;

import br.uel.mdd.wave.Wave;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author ${user}
 * @TODO Auto-generated comment
 * <p/>
 * Created by pedro on 29/05/14.
 */
public class Processor {

    private Wave waveFile;

    private static final String RESOURCES_FOLDER= "src/main/resources";

    public Processor(Wave wave){
        this.waveFile = wave;
    }


    public Processor putEcho(double seconds){

        int valuesSize = waveFile.getDataValues().length;
        try {
            double samplingFactor = Math.floor(waveFile.getDataValues().length/(waveFile.getHeader().getSampleRate() * seconds));
            double[] data = this.downSample(this.waveFile, (int) samplingFactor);
            int sampleRate = (int) ((int) ((samplingFactor-1)*waveFile.getHeader().getSampleRate())/samplingFactor);


            int halfSecondSamples = sampleRate/2;
            double[] dataEcho = Arrays.copyOf(data, data.length + (int)(sampleRate*seconds));


            for (int i = 0; i < (seconds / 0.5); i++) {
                System.arraycopy(data, data.length - halfSecondSamples, dataEcho, data.length+((i)*halfSecondSamples), halfSecondSamples);
            }


            double gain = 1, step = 1/sampleRate*seconds;
            for (int i = data.length; i < dataEcho.length; i++) {
                dataEcho[i] = dataEcho[i]*gain;
                gain-=step;
            }

            this.waveFile.setDataValues(dataEcho);

            this.waveFile.getHeader().setSampleRate(sampleRate);
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param wave Wave instance
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
            if(i%samplingFactor != 0){
                newData[j++] = wave.getDataValues()[i];
            }
        }
        return newData;
    }




    public void saveNewWave(String fileName){
        this.waveFile.saveFile(this.waveFile.getDataValues(), fileName);
    }

}
