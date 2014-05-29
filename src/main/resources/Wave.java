package Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;

public class Wave {

    private short[] rowData ;
    private double[] doubleData;
    private FileReader leitorArquivo;
    private File arquivo;
    private int tamanho;
    private String tipoArquivo;
    private String formato;
    private String indentificador;
    private int comprimentoChunk;
    private String categoriaFormato;
    private String numCanais;
    private String taxaAmostragem;
    private int medeiabits;
    private int alinhamento;
    private int resolucao;
    private String indentificacao;
    private int tamanhoChunk;
    private String path;

    private FileInputStream fis;


    public String getPath(){
        return this.path;
    }

    public void setPath(String path){
        this.path=path;
    }

    public FileReader getLeitorArquivo() {
        return leitorArquivo;
    }

    public void setLeitorArquivo(FileReader leitorArquivo) {
        this.leitorArquivo = leitorArquivo;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }


    public int getTamanho() {
        return tamanho;
    }

    public void setTamanho(int tamanho) {
        this.tamanho = tamanho;
    }

    public String getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(String tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getIndentificador() {
        return indentificador;
    }

    public void setIndentificador(String indentificador) {
        this.indentificador = indentificador;
    }

    public int getComprimentoChunk() {
        return comprimentoChunk;
    }

    public void setComprimentoChunk(int comprimentoChunk) {
        this.comprimentoChunk = comprimentoChunk;
    }

    public String getCategoriaFormato() {
        return categoriaFormato;
    }

    public void setCategoriaFormato(String categoriaFormato) {
        this.categoriaFormato = categoriaFormato;
    }

    public String getNumCanais() {
        return numCanais;
    }

    public void setNumCanais(String numCanais) {
        this.numCanais = numCanais;
    }

    public String getTaxaAmostragem() {
        return taxaAmostragem;
    }

    public void setTaxaAmostragem(String taxaAmostragem) {
        this.taxaAmostragem = taxaAmostragem;
    }

    public int getMedeiabits() {
        return medeiabits;
    }

    public void setMedeiabits(int medeiabits) {
        this.medeiabits = medeiabits;
    }

    public int getAlinhamento() {
        return alinhamento;
    }

    public void setAlinhamento(int alinhamento) {
        this.alinhamento = alinhamento;
    }

    public int getResolucao() {
        return resolucao;
    }

    public void setResolucao(int resolucao) {
        this.resolucao = resolucao;
    }

    public String getIndentificacao() {
        return indentificacao;
    }

    public void setIndentificacao(String indentificacao) {
        this.indentificacao = indentificacao;
    }

    public int getTamanhoChunk() {
        return tamanhoChunk;
    }


    public void setTamanhoChunk(int comprimento) {
        this.tamanhoChunk = comprimento;
    }

    public short[] getRowData() {
        return rowData;
    }

    public void setRowData(short[] rowData) {
        this.rowData = rowData;
    }

    public double[] getDoubleData() {
        return doubleData;
    }

    public void setDoubleData(double[] doubleData) {
        this.doubleData = doubleData;
    }

    /**
     * @return the fis
     */
    public FileInputStream getFis() {
        return fis;
    }

    /**
     * @param fis the fis to set
     */
    public void setFis(FileInputStream fis) {
        this.fis = fis;
    }

    

}