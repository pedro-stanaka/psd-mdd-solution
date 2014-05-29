package br.uel.mdd.wave;

import com.google.common.io.LittleEndianDataInputStream;
import com.google.common.io.LittleEndianDataOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


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
    private int dataSize;

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
            this.stream = new LittleEndianDataInputStream(is);
            this.readData();
            if(is.markSupported()){
                try {
                    is.reset();
                    this.rawData = new byte[this.header.getSubChunk2Size()];
                    is.read(this.rawData);
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

            int subChunk2Size = values.length * bytesPerSample;
            this.header.setSubChunk2Size(subChunk2Size);
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
    }

    public byte[] getRawData() {
        return rawData;
    }
}
