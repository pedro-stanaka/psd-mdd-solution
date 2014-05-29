package br.uel.mdd.utils;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.List;


/**
 * JFreeChart wrapper
 */


public class ChartCreator {

    private XYSeries dataSet;



    public ChartCreator(String dataSetTitle){
        this.dataSet = new XYSeries(dataSetTitle);
    }

    public void addValues(byte[] values){
        int cont=0;
        for (byte i : values) {
            this.dataSet.add((double) cont++, (double) i);
        }
    }

    public void addValues(List<Double> list){
        int cont=0;
        for(Double i : list){
            this.dataSet.add(cont++, i);
        }
    }

    public int createLineChart(String chartTitle, String xTitle, String yTitle) {
        XYDataset sc = new XYSeriesCollection(this.dataSet);
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, xTitle, yTitle, sc, PlotOrientation.VERTICAL, true, true, false);
        ChartFrame frame1=new ChartFrame("Wave Format",chart);
        frame1.setVisible(true);
        frame1.setSize(1300, 250);
        return 0;
    }


    public void addValuesIntercalated(double[] values) {
        for (int i = 0; i < values.length; i+=2) {
            this.dataSet.add(i, values[i]);
        }
    }


    public void addValues(double[] values) {
        int cont=0;
        for(double i : values){
            this.dataSet.add(cont++, i);
        }
    }

    public void addValue(int y, int x) {
        this.dataSet.add(x, y);
    }
}
