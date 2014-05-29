package br.uel.mdd;

import br.uel.mdd.wave.Wave;

/**
 * @author ${user}
 * @TODO Auto-generated comment
 * <p/>
 * Created by pedro on 29/05/14.
 */
public class Processor {

    private Wave waveFile;

    public Processor(Wave wave){
        this.waveFile = wave;
    }


    public void saveNewWave(String fileName, double[] values){
        this.waveFile.saveFile(values, fileName);
    }

}
