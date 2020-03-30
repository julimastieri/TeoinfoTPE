package teoInfoTPE;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class Canales {
	
	private static final int cantColores = 256;
	
	BufferedImage imagenSalida;
	BufferedImage imagenLlegada;
	StringBuilder fileName = new StringBuilder();
	
	public void getRuido () {
		fileName.append("Ruido-");
		VentanaPrincipal.progressBar.setValue(40);
		imagenSalida = pedirImagen();
		fileName.append("-");
		imagenLlegada = pedirImagen();
		VentanaPrincipal.progressBar.setValue(60);
		
		double ruido = this.CalcularRuido();
		
		//Guardo en archivo txt
    	StringBuilder txt = new StringBuilder();
    	txt.append("El ruido es: " + ruido);
    	fileName.append(".txt");
    	PrintWriter out;
		try {
			out = new PrintWriter(fileName.toString());
			out.println(txt);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		VentanaPrincipal.progressBar.setValue(100);
	}
	
	public void getPerdida () {
		fileName.append("Perdida-");
		VentanaPrincipal.progressBar.setValue(40);
		imagenSalida = pedirImagen();
		fileName.append("-");
		imagenLlegada = pedirImagen();
		VentanaPrincipal.progressBar.setValue(60);
		
		double perdida = this.CalcularPerdida();
		
		//Guardo en archivo txt
    	StringBuilder txt = new StringBuilder();
    	txt.append("La perdida es: " + perdida);
    	fileName.append(".txt");
    	PrintWriter out;
		try {
			out = new PrintWriter(fileName.toString());
			out.println(txt);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		VentanaPrincipal.progressBar.setValue(100);
	}
	
	public BufferedImage pedirImagen () {
		BufferedImage image = null;
		
		//Cargar imagen desde la PC
    	File miDir = new File (".");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(miDir);
        
        System.out.println(" ");
        
        fileChooser.showOpenDialog(fileChooser);
        String ruta = fileChooser.getSelectedFile().getAbsolutePath();
    	File file = new File(ruta); 
        	
        	try {
        		//leo la imagen
				image = ImageIO.read(file);
				String nombre = file.getName();
				String nombreArchivo = nombre.substring(0,nombre.lastIndexOf("."));
				fileName.append(nombreArchivo);
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        return image;	
	}
	
	//-------------------------------------------------------------------------------------------
	 
	public double CalcularRuido() { 
	    	
	    	double ruido;
	    	
	    	double[] probMarginal;
	    	double[][] matrizCondicional;
	    	
	        probMarginal = calcularProbabilidadesMarginales(imagenSalida);//calculo de prob marginales
	        matrizCondicional = calcularProbabilidadesCondicionales(imagenSalida, imagenLlegada); //calculo de prob condicionales
	        ruido = entropiaConMemoria(matrizCondicional, probMarginal); //calculo entropia del bloque con memoria
	    	
	    	return ruido;
	    }
	
	//-------------------------------------------------------------------------------------------
	 
	public double CalcularPerdida() { 
	    	
	    	double perdida;
	    	
	    	double[] probMarginal;
	    	double[][] matrizCondicional;
	    	
	        probMarginal = calcularProbabilidadesMarginales(imagenLlegada);//calculo de prob marginales
	        matrizCondicional = calcularProbabilidadesCondicionales(imagenLlegada, imagenSalida); //calculo de prob condicionales
	        perdida = entropiaConMemoria(matrizCondicional, probMarginal); //calculo entropia del bloque con memoria
	    	
	    	return perdida;
	    }
	
	//-------------------------------------------------------------------------------------------
	
	double[] calcularProbabilidadesMarginales(BufferedImage imagen){
	    	
	    	double[] resultado  = new double[cantColores];
	    	
	    	resultado = contarColoresImagen (imagen);
	    	
	    	//divido por el numero total de pixeles (ancho * alto)
	    	for (int j=0; j<cantColores; j++) {
	    		resultado[j] = resultado[j] / ( imagen.getWidth() *  imagen.getHeight() );
	    	}
	    	
	    	return resultado;
	    }
	 
	//-------------------------------------------------------------------------------------------
	    
	double[] contarColoresImagen (BufferedImage imagen) {
	    	
	    	double[] resultado  = new double[cantColores];
	    	for (int i=0; i<cantColores; i++) { //inicializacion en 0
	    		resultado[i] = 0;
	    	}
	    	
	    	int r = 0;
	    	//recorro los pixeles de la imagen
	    	
	    	for (int x = 0; x < imagen.getWidth(); x++) { //recorro por columnas
	    		for (int y = 0; y < imagen.getHeight(); y++) {
	    			
	    			int rgb = imagen.getRGB(x, y);
	    			Color color = new Color(rgb, true); 
	    			r = color.getRed(); //extraigo el color
	    			resultado[r]++; //sumo 1 aparicion de ese color
	    		} 
	    	}
	    	return resultado;
	    }
	    
	//-------------------------------------------------------------------------------------------
	    
	double[][] calcularProbabilidadesCondicionales(BufferedImage ImSalida, BufferedImage ImLlegada){
	    	
	    	int ancho= ImSalida.getWidth();
	    	int alto = ImSalida.getHeight();
	    	
	    	double [][] resultado = new double[cantColores][cantColores];
	    	for (int i=0; i<cantColores; i++) { //inicializo matriz en 0
	    		for (int j=0; j< cantColores; j++) {
	    			resultado[i][j] = 0;
	    		}
	    	}
	    	
	    	int colorSalida=0,colorLlegada=0;
	    	
	    	//recorro los pixeles del bloque por filas
	    	for (int x = 0; x < ancho; x++) {
	    		for (int y = 0; y < alto; y++) {
	    			
	    			int salida = ImSalida.getRGB(x, y);
	    			Color cSalida = new Color(salida, true);
	    			colorSalida = cSalida.getRed();
	    			
	    			int llegada = ImLlegada.getRGB(x, y);
	    			Color cLlegada = new Color(llegada, true);
	    			colorLlegada = cLlegada.getRed();		
	    			 
	    			resultado[colorLlegada][colorSalida]++; //sumo 1 en casilla de salida cruce con llegada
	    													// [x][y]
	    		}
	    	}
	    	
	    	//divido cada casilla por la suma de su columna
	    	int sumaCol;
	    
	    	for (int h=0; h<cantColores; h++) { //por cada columna
	    		sumaCol=0;
	    		for (int f=0; f<cantColores; f++) { //calculo la suma de columna h
		    		sumaCol += resultado[f][h];
		    	}
	    		
	    		if (sumaCol !=0) { //evito dividir por 0
		    		for (int c=0; c<cantColores; c++) { //recorro la columna
		    			resultado[c][h] = resultado[c][h] / sumaCol;
		    		}
	    		}
	    	}
	    	
	    	return resultado;
	    }
	   
	//-------------------------------------------------------------------------------------------
	    
	double entropiaConMemoria(double[][]probabilidadesCM, double[] probMarginal) {
	    	double entropia=0, probMarg=0, Hi=0, probCond=0;
	    	
	    	for (int i=0; i<cantColores; i++) { //Por cada columna
	    		probMarg = probMarginal[i];
	    		Hi = 0;
		    	for(int j=0; j<cantColores; j++) { // (hi) entropia de la columna i. //Sumatoria de fila de condicionales * logaritmo (condicionales)
		    		probCond = probabilidadesCM[j][i];
		    		if (probCond != 0) {
		    			Hi += ( probCond * (Math.log(probCond) / Math.log(2)) );
		    		}
	    		}
		    	entropia += (probMarg * Hi);
	    	}
	    	
	    	if (entropia != 0)
	    		return (-1 * entropia);
	    	else
	    		return 0;
	    	
	    }

}
