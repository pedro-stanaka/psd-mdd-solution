package br.uel.mdd.wave;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

import java.io.*;


/**
 * This class abstract an wave file. Load the from resources folder inside maven project.
 *
 * @author Pedro Tanaka
 */
public class Wave {

    private WaveHeader header;
    private double[] dataValues;
    private byte[] rawData;
    private LittleEndianDataInputStream stream;

    /**
     * @TODO documentation here
     * @param fileName
     */
    public Wave(String fileName) {

        try {
            InputStream is = this.getClass().getResourceAsStream(fileName);
            this.header = new WaveHeader(is);
            if(is.markSupported()){
                is.mark(this.header.getSubChunk2Size()+2);
            }
            this.rawData = new byte[this.header.getSubChunk2Size()];
            is.read(this.rawData);
            if(is.markSupported()){
                try {
                    is.reset();
                    this.stream = new LittleEndianDataInputStream(is);
                    this.readData();
                } catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
            stream.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Wave(double[] values, WaveHeader header){
        this.dataValues = values;
        this.header = header;
        int subChunk2Size = values.length * this.header.getBytesPerSample();
        this.header.setSubChunk2Size(subChunk2Size);
        this.header.setChunkSize(subChunk2Size+36);
    }

    public Wave(byte[] binData, WaveHeader header){
        this.header = header;
        this.rawData = binData;
        ByteArrayInputStream li = new ByteArrayInputStream(this.rawData);

    }

    private void readFromRaw(){

    }


    /**
     * @TODO documentation here
     */
    private void readData() throws IOException {
        this.setDataValues(new double[this.header.getSubChunk2Size() / (this.header.getBytesPerSample())]);
        switch (this.header.getBytesPerSample()) {
            case 1: this._readData8bits(); break;
            case 2: this._readData16bits(); break;
            case 4: this._readData32bits(); break;
        }
    }

    /**
     * @TODO documentation here
     */
    private void _readData32bits() throws IOException {
        for(int i=0; i< this.getDataValues().length; i++) {
            this.getDataValues()[i] = stream.readInt();
        }
    }

    /**
     * @TODO documentation here
     */
    private void _readData16bits() throws IOException {
        for(int i=0; i< this.getDataValues().length; i++) {
            this.getDataValues()[i] = stream.readShort();
        }
    }

    /**
     * @TODO documentation here
     */
    private void _readData8bits() throws IOException {
        for(int i=0; i< this.getDataValues().length; i++) {
            this.getDataValues()[i] = stream.readByte();
        }
    }


    /**
     * @TODO documentation here
     * @return
     */
    public double[] getDataValues() {
        return dataValues;
    }

    /**
     * @TODO documentation here
     * @return
     */
    public WaveHeader getHeader() {
        return header;
    }

    public void setHeader(WaveHeader header) {
        this.header = header;
    }

    public void saveFile(double[] values, String fileName) {
        int bytesPerSample = header.getBytesPerSample();

        try {
            File f = new File(fileName);
            if(f.exists())f.delete();

            FileOutputStream fos = new FileOutputStream(fileName);

            WaveHeader header = this.header;
            int subChunk2Size = values.length * bytesPerSample;
            header.setSubChunk2Size(subChunk2Size);
            header.setChunkSize(subChunk2Size+36);

            fos.write(header.getRawHeader());
            LittleEndianDataOutputStream outputStream = new LittleEndianDataOutputStream(fos);
            write(values, bytesPerSample, outputStream);
            outputStream.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(double[] values, int bytesPerSample, LittleEndianDataOutputStream outputStream) throws IOException {
        int normalizedNum;
        switch (bytesPerSample){
            case 1:
                for (double value : values) {

                    normalizedNum = normalizeNumber(value, Byte.MAX_VALUE, Byte.MIN_VALUE);
                    outputStream.writeByte(normalizedNum);
                    System.out.print(normalizedNum+", ");
                }
                break;
            case 2:
                for (double value : values) {
                    normalizedNum = normalizeNumber(value, Short.MAX_VALUE, Short.MIN_VALUE);
                    outputStream.writeShort(normalizedNum);
                }
                break;
            case 4:
                for (double value : values) {
                    normalizedNum = normalizeNumber(value, Integer.MAX_VALUE, Integer.MIN_VALUE);
                    outputStream.writeInt(normalizedNum);
                }
                break;
        }
    }

    private int normalizeNumber(double value, int upperBound, int lowerBound) {
        int normalizedNumber = (int) value;

        if (value > upperBound) {
            normalizedNumber = upperBound;
        }

        if (value < lowerBound) {
            normalizedNumber = lowerBound;
        }

        return normalizedNumber;
    }

    public void setDataValues(double[] dataValues) {
        this.dataValues = dataValues;
        this.fixHeader();
    }

    private void fixHeader() {
        this.header.setSubChunk2Size(this.getDataValues().length * this.header.getBytesPerSample());
        this.header.setChunkSize(36+this.header.getSubChunk2Size());
        this.header.setByteRate(this.header.getSampleRate() * this.header.getNumChannels() * this.header.getBytesPerSample());
        this.header.setBlockAlign(this.header.getNumChannels() * this.header.getBytesPerSample());
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void saveRaw(String s) throws IOException {
        File f = new File(s);
        if(f.exists()) f.delete();
        FileOutputStream fos = new FileOutputStream(s);
        fos.write(this.header.getRawHeader());
        fos.write(this.rawData);
        fos.close();
    }


    public void setRawData(byte[] rawData) {
        this.rawData = rawData.clone();
        System.out.println("Gravando dados: " + rawData.length);
        this.header.setSubChunk2Size(rawData.length);
        this.header.setChunkSize(36+rawData.length);
    }

}
