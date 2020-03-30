package teoInfoTPE;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Entropia {
	
	private int cantColores = 256;
	private BufferedImage[] bloquesArr;
	private double [] entropiasConMemoria;
	private int bloqueMayorH;
	private int bloqueMenorH;
	private int bloqueMediaH;
	
	//-------------------------------------------------------------------------------------------
	
	public Entropia (BufferedImage[] bloquesArr, int cantColores) {
		this.bloquesArr = bloquesArr;
		this.cantColores = cantColores;
		entropiasConMemoria  = new double[this.bloquesArr.length];
    	for (int i=0; i<entropiasConMemoria.length; i++) { //inicializacion en 0
    		entropiasConMemoria[i] = 0;
    	}
    	this.calcularHMaxMedMin();
	}
	
	//-------------------------------------------------------------------------------------------
	
	public String calcularEntropiaSinMemoria() { //ENTROPIA SIN MEMORIA
	    StringBuilder salida = new StringBuilder();
	    salida.append("ENTROPIA SIN MEMORIA \n");
	    double[] probabilidadesSM;
	    double entropia;
	    	
	    for(int i=0; i< bloquesArr.length; i++) { //por cada bloque del arreglo
	    	probabilidadesSM = calcularProbabilidadesMarginales(bloquesArr[i]); //calculo probabilidades del bloque
	    	entropia = calcularEntropiaBloqueSinMemoria(probabilidadesSM); //calculo entropia del bloque
	    	salida.append("Bloque " + (i) + " = "+ entropia +"\n");
	    }
	   	
	   	return salida.toString();
	   }
	
	//-------------------------------------------------------------------------------------------
	
	public double[] calcularProbabilidadesMarginales(BufferedImage bloque){
    	
    	double[] resultado  = new double[cantColores];
    	
    	resultado = contarColoresBloque (bloque);
    	
    	//divido por el numero total de pixeles (ancho * alto)
    	for (int j=0; j<cantColores; j++) {
    		resultado[j] = resultado[j] / ( bloque.getWidth() *  bloque.getHeight() );
    	}
    	
    	return resultado;
    }
	
	//-------------------------------------------------------------------------------------------
	
	private double[] contarColoresBloque (BufferedImage bloque) {
    	double[] resultado  = new double[cantColores];
    	for (int i=0; i<cantColores; i++) { //inicializacion en 0
    		resultado[i] = 0;
    	}
    	
    	int r = 0;
    	//recorro los pixeles del bloque
    	
    	for (int x = 0; x < bloque.getWidth(); x++) {
    		for (int y = 0; y < bloque.getHeight(); y++) {
    			
    			int rgb = bloque.getRGB(x, y);
    			Color color = new Color(rgb, true); 
    			r = color.getRed(); //extraigo el color
    			resultado[r]++; //sumo 1 aparicion de ese color
    		} 
    	}
    	return resultado;
    }
	
	//-------------------------------------------------------------------------------------------
	
	private double calcularEntropiaBloqueSinMemoria(double[] probabilidades) {
    	
    	double entropia=0;
    	double elem = 0;
    	
    	for (int i=0; i< cantColores ;i++) {
    		elem = probabilidades[i];
    		if (elem != 0) {
    			entropia += ( elem * (Math.log(elem) / Math.log(2)) );
    		}	
    	}
    	return (-1 * entropia);
    }
	
	//-------------------------------------------------------------------------------------------
	
	public String calcularEntropiaConMemoria() { //ENTROPIA CON MEMORIA
	   	
	    	StringBuilder salida= new StringBuilder();
	    	salida.append("ENTROPIA CON MEMORIA \n");
	    	
	    	double[] probMarginales ;
	    	double[][] probabilidadesCM;
	    	
	    	for(int i=0; i< bloquesArr.length; i++) { //por cada bloque del arreglo
	    		probMarginales = calcularProbabilidadesMarginales(bloquesArr[i]);//calculo de prob marginales
	    		probabilidadesCM = calcularProbabilidadesCondicionales(bloquesArr[i]); //calculo de prob condicionales
	    		entropiasConMemoria[i] = calcularEntropiaBloqueConMemoria(probabilidadesCM, probMarginales); //calculo entropia del bloque con memoria
	    		
	    		salida.append("Bloque " + (i) + " = "+ entropiasConMemoria[i] +"\n");
	    	}
	    	
	    	return salida.toString();
	    }
	
	//-------------------------------------------------------------------------------------------
	
	public double[][] calcularProbabilidadesCondicionales(BufferedImage bloque){
	    	double [][] resultado = new double[cantColores][cantColores];
	    	for (int i=0; i<cantColores; i++) { //inicializo matriz en 0
	    		for (int j=0; j<cantColores; j++) {
	    			resultado[i][j] = 0;
	    		}
	    	}
	    	
	    	int anterior=0,actual=0;
	    	boolean primerPixel=true;//primer pixel no tiene anterior, no se tiene en cuenta para la probabilidad
	    	
	    	//recorro los pixeles del bloque por columnas
	    	for (int y = 0; y < bloque.getWidth(); y++) {
	    		for (int x = 0; x < bloque.getHeight(); x++) {
	    			
	    			int rgb = bloque.getRGB(x, y);
	    			Color color = new Color(rgb, true); 
	    			actual = color.getRed(); //extraigo el color
	    			
	    			if (!primerPixel) 
	    				resultado[anterior][actual]++; //sumo 1 en casilla de anterior cruce con actual
	    			else
	    				primerPixel=false;
	    			
	    			anterior = actual;
	    		}
	    	}
	    	
	    	//divido cada casilla por la suma de su fila
	    	int sumaFila;
	    
	    	for (int f=0; f<cantColores; f++) { //por cada fila
	    		sumaFila=0;
	    		for (int h=0; h<cantColores; h++) { //calculo la suma de fila f
		    		sumaFila += resultado[f][h];
		    	}
	    		
	    		if (sumaFila !=0) { //evito dividir por 0
		    		for (int c=0; c<cantColores; c++) {
		    				resultado[f][c] = resultado[f][c] / sumaFila;
		    		}
	    		}
	    	}
	    	
	    	return resultado;
	    }
	
	//-------------------------------------------------------------------------------------------
	
	public void calcularHMaxMedMin(){
		
		this.calcularEntropiaConMemoria();
		
    	bloqueMayorH = 0;
    	double valorBloqueMayor = entropiasConMemoria[0];
    	
    	bloqueMenorH = 0;
    	double valorBloqueMenor = entropiasConMemoria[0];
    	
    	bloqueMediaH = 0;
    	double sumatoriaH = 0;
    	double promedioDeH;
    	double diferenciaMin;
    	double diferencia=0;
    	
    	for(int i=0; i<entropiasConMemoria.length; i++) {
    		
    		if ( valorBloqueMayor < entropiasConMemoria[i] ) {
    			bloqueMayorH = i;
    			valorBloqueMayor = entropiasConMemoria[i];
    		}
    		
    		if ( entropiasConMemoria[i] < valorBloqueMenor ) {
    			bloqueMenorH = i;
    			valorBloqueMenor = entropiasConMemoria[i];
    		}	
    		
    		sumatoriaH += entropiasConMemoria[i];
    		
    	}
    	
    	promedioDeH = sumatoriaH/entropiasConMemoria.length;
    	
    	//BUSCO BLOQUE CON ENTROPIA MAS PARECIDA AL PROMEDIO
    	
    	diferenciaMin = Math.abs(promedioDeH-entropiasConMemoria[0]);
    	for (int j=1; j<entropiasConMemoria.length; j++) {
    		
    		diferencia = Math.abs(promedioDeH-entropiasConMemoria[j]);
    		
    		if (diferencia < diferenciaMin) {
    			diferenciaMin = diferencia;
    			bloqueMediaH = j;
    		}
    	}
    }
	
	//-------------------------------------------------------------------------------------------
	
	public int getBloqueMayorH() {
		return bloqueMayorH;
	}
	
	//-------------------------------------------------------------------------------------------
	
	public int getBloqueMenorH() {
		return bloqueMenorH;
	}
	
	//-------------------------------------------------------------------------------------------
	
	public int getBloqueMediaH() {
		return bloqueMediaH;
	}
	
	//-------------------------------------------------------------------------------------------
	
	public double[] getEntropiasConMemoria(){
		return this.entropiasConMemoria;
	}
	
	//-------------------------------------------------------------------------------------------
	
	private double calcularEntropiaBloqueConMemoria(double[][]probabilidadesCM, double[] probMarginales) {
    	double entropia=0, probMarg=0, Hi=0, probCond=0;
    	
    	for (int i=0; i<cantColores; i++) { //Por cada fila
    		probMarg = probMarginales[i];
    		Hi = 0;
	    	for(int j=0; j<cantColores; j++) { // (hi) entropia de la fila i. Sumatoria de fila de condicionales * logaritmo (condicionales)
	    		probCond = probabilidadesCM[i][j];
	    		if (probCond != 0) {
	    			Hi += ( probCond * (Math.log(probCond) / Math.log(2)) );
	    		}	
    		}
	    	entropia += (probMarg * Hi);
    	}
    	
    	return (-1 * entropia);
    }
	
}
