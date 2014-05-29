package br.uel.mdd.wave;


import br.uel.mdd.utils.EndianessConverter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 *
 */
public class WaveHeader {


    // CONSTANTS
    public final int PCM_FORMAT = 1;
    public final int COMPRESSED_FORMAT = 0;

    // Wave header info
    private String chunkID = "";
    private int chunkSize = 0;
    private String format = "";
    private String Subchunk1ID = "";
    private int Subchunk1Size = 0;
    private int AudioFormat = 0;
    private int NumChannels =0;
    private int SampleRate =0;
    private int byteRate = 0;
    private int blockAlign = 0;
    private int bitsPerSample = 0;
    private int subChunk2Id = 0;
    private int subChunk2Size = 0;

    private byte[] rawHeader;

    private final boolean debug = true;


    /**
     * Constructor method
     *
     * For documentation over WAVE format see.
     * link <a href="https://ccrma.stanford.edu/courses/422/projects/WaveFormat/">Stanford docs</a>
     */
    public WaveHeader(InputStream is) throws IOException {
        this.rawHeader = new byte[44];
        is.read(this.rawHeader);

        if(isDebug()){
            System.out.println("ChunkId: " + new String(Arrays.copyOfRange(this.rawHeader, 0, 4)));
        }

        this.chunkID = new String(Arrays.copyOfRange(this.rawHeader, 0, 4));

        if(isDebug()) {
            System.out.println("ChunkSize: " + EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 4, 8)));
        }
        this.chunkSize = EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 4, 8));

        if(isDebug()) {
            System.out.println("Format: " + new String(Arrays.copyOfRange(this.rawHeader, 8, 12)));
        }
        this.format = new String(Arrays.copyOfRange(this.rawHeader, 8, 12));


        if(isDebug()) {
            System.out.println("SubChunkId: " + new String(Arrays.copyOfRange(this.rawHeader, 12, 16))); // SubChunkSize
        }
        this.Subchunk1ID = new String(Arrays.copyOfRange(this.rawHeader, 12, 16));


        if(isDebug()) {
            System.out.println("SubCkSz: " + EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 16, 20)));
        }
        this.Subchunk1Size = EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 16, 20));


        if(EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 20, 22)) == 1){
            if(isDebug()) {
                System.out.println("AudioFormat: " + "PCM");
            }
            this.AudioFormat = this.PCM_FORMAT;
        }else{
            if(isDebug()) {
                System.out.println("AudioFormat: " + "Comprised");
            }
            this.AudioFormat = this.COMPRESSED_FORMAT;
        }


        if(EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 22, 24)) == 1){
            if(isDebug()) {
                System.out.println("Number of channels: " + "Mono");
            }
            this.NumChannels = 1;
        }else{
            if(isDebug()) {
                System.out.println("Number of channels: " + "Stereo");
            }
            this.NumChannels = 2;
        }


        if(isDebug()) {
            System.out.println("Sample Rate: " + EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 24, 28)));
        }
        this.SampleRate = EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 24, 28));


        if(isDebug()) {
            System.out.println("Byte Rate: " + EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 28, 32)));
        }
        this.byteRate = EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 28, 32));


        if(isDebug()) {
            System.out.println("Block Align: " + EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 32, 34)));
        }
        this.blockAlign = EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 32, 34));


        if(isDebug()) {
            System.out.println("Bits per sample: " + EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 34, 36)));
        }
        this.bitsPerSample = EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 34, 36));


        if(isDebug()) {
            System.out.println("SubChunk2Id: " + new String(Arrays.copyOfRange(this.rawHeader, 36, 40)));
        }
        this.subChunk2Id = EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 36, 40));

        if(isDebug()) {
            System.out.println("SubChunk2Size: " + EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 40, 44)) + " b");
        }
        this.subChunk2Size = EndianessConverter.convertLittleEndian(Arrays.copyOfRange(this.rawHeader, 40, 44));
    }


    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getChunkSize());
        sb.append(getChunkSize());
        sb.append(getFormat());
        sb.append(getSubChunk1ID());
        sb.append(getSubChunk1Size());
        sb.append(getAudioFormat());
        sb.append(getNumChannels());
        sb.append(getSampleRate());
        sb.append(getByteRate());
        sb.append(getBlockAlign());
        sb.append(getBitsPerSample());

        return null;
    }

    public String getChunkID() {
        return chunkID;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public String getFormat() {
        return format;
    }

    public String getSubChunk1ID() {
        return Subchunk1ID;
    }

    public int getSubChunk1Size() {
        return Subchunk1Size;
    }

    public int getAudioFormat() {
        return AudioFormat;
    }

    public int getNumChannels() {
        return NumChannels;
    }

    public int getSampleRate() {
        return SampleRate;
    }

    public int getByteRate() {
        return byteRate;
    }

    public int getBlockAlign() {
        return blockAlign;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public int getSubChunk2Id() {
        return subChunk2Id;
    }

    public int getSubChunk2Size() {
        return subChunk2Size;
    }

    public byte[] getRawHeader(){return  this.rawHeader;}

    public boolean isDebug() {
        return debug;
    }

    public void setSampleRate(int sampleRate) {
        SampleRate = sampleRate;
        byte[] b = EndianessConverter.convertBigEndian(sampleRate,4);
        System.arraycopy(b, 0, rawHeader, 24, b.length);
    }

    public void setByteRate(int byteRate) {
        this.byteRate = byteRate;
        byte[] b = EndianessConverter.convertBigEndian(byteRate,4);
        System.arraycopy(b, 0, rawHeader, 28, b.length);
    }


    public void setBitsPerSample(int bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
        byte[] b = EndianessConverter.convertBigEndian(bitsPerSample,2);
        System.arraycopy(b, 0, rawHeader, 34, b.length);
    }

    public void setSubChunk2Size(int subChunk2Size) {
        this.subChunk2Size = subChunk2Size;
        byte[] b = EndianessConverter.convertBigEndian(subChunk2Size,4);
        System.arraycopy(b, 0, rawHeader, 40, b.length);
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
        byte[] b = EndianessConverter.convertBigEndian(chunkSize,4);
        System.arraycopy(b, 0, rawHeader, 4, b.length);
    }

    public int getBytesPerSample() {
        return this.getBitsPerSample() / 8;
    }
}
