package br.uel.mdd;

import br.uel.mdd.wave.Wave;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        ex2();
    }



    private static void ex1(){
        Wave wave = new Wave("/welcome.wav");
        Processor processor = new Processor(wave);
        processor.putEcho(1.5).saveNewWave("edited_wave22.wav");
    }

    private static void ex2(){
        Wave wave = new Wave("/22.wav");
        Processor processor = new Processor(wave);
        processor.toStereo().saveNewWave("edited_22_stereo.wav");
    }

}
