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
        Wave wave = new Wave("/22.wav");
        Processor p = new Processor(wave);
        p.putEcho(1.5).saveNewWave("edited_wave22.wav");
//        p.saveNewWave("out_22.wav");
    }



    private static void ex1(){
        Wave wave = new Wave("/welcome.wav");
        Processor processor = new Processor(wave);
        System.out.println(wave.getDataValues().length);
    }

}
