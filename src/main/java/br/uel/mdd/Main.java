package br.uel.mdd;

import br.uel.mdd.wave.Wave;
import math.jwave.exceptions.JWaveFailure;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args ) throws JWaveFailure {
        ex1();
    }



    private static void ex1(){
        Wave wave = new Wave("/22.wav");
        Processor processor = new Processor(wave);
        processor.putEcho(1.5).saveNewWave("echo_wav.wav");
    }

    private static void ex2(){
        Wave wave = new Wave("/22.wav");
        Processor processor = new Processor(wave);
        processor.toStereo().saveNewWave("edited_22_stereo.wav");
    }

    private static void ex5(){
        Wave wave= new Wave("/dangerousjob.wav");
        Processor processor = new Processor(wave);

        if (processor.isHumanVoice()){
            System.out.println("HUMAN VOICE");
        } else {
            System.out.println("NOT HUMAN VOICE");
        }
    }

    private static void ex3(){
        Wave sound = new Wave("/WelcomeRuidoso.wav");
        Wave noise = new Wave("/Noise.wav");
        Processor processor = new Processor(sound);
        processor.removeWhiteNoise(noise, sound).saveNewWave("welcome_denoised.wav");
        processor.playMusic(Main.class.getResourceAsStream("welcome_denoised.wav"));
    }

}
