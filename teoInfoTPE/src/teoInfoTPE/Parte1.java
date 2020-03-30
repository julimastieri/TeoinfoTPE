package teoInfoTPE;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;


import org.jfree.chart.ChartFactory;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import org.jfree.chart.plot.PlotOrientation;

import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;


public class Parte1 {
	
		private static final int cantColores = 256;
		private static final int anchoBloque = 500;
		private static final int altoBloque = 500;
		BufferedImage bloquesArr[];
		int altoImagen=0;
		int anchoImagen=0;
		Entropia entropia;
		StringBuilder fileName;
		
		private static final float epsilon = 0.000005f;
		private static final int minTiradas = 100000;
		
		private int bloqueMayorH;//nro en arreglo del bloque con mayor entropia (con memoria)
		private int bloqueMenorH;//nro en arreglo del bloque con menor entropia (con memoria)
		private int bloqueMediaH;//nro en arreglo del bloque que posee la entropia mas cercana al promedio
		
	  //-------------------------------------------------------------------------------------------
		//Constructor
		
	    public Parte1() {
	    
	    	//Cargar imagen desde la PC
	    	File miDir = new File (".");
	        JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setCurrentDirectory(miDir);
	        
	        fileChooser.showOpenDialog(fileChooser);
	        String ruta = fileChooser.getSelectedFile().getAbsolutePath();
	    	File file = new File(ruta); 
	    	
	        try {
	        	
	        	BufferedImage image = ImageIO.read(file); //leo la imagen
	        	
	        	String nombre = file.getName();
				String nombreArchivo = nombre.substring(0,nombre.lastIndexOf("."));
				fileName = new StringBuilder();
				fileName.append(nombreArchivo);
	        	
	        	altoImagen = image.getHeight();
	        	anchoImagen = image.getWidth();
	        	
	        	int rows = image.getHeight() / altoBloque; 
	 	        int cols = image.getWidth() / anchoBloque;
	 	        int bloques = rows * cols;
	 	        int count = 0;

		        bloquesArr = new BufferedImage[bloques]; //Arreglo de imagenes para guardar los bloques
		        
		        
		        for (int x = 0; x < rows; x++) {
		            for (int y = 0; y < cols; y++) {
		                //Guardo en el arreglo una imagen de las dimensiones del bloque
		                bloquesArr[count] = new BufferedImage(anchoBloque, altoBloque, image.getType());
		                
		                //Dibujo la imagen en el bloque que cree y aumento el count 
		                Graphics2D gr = bloquesArr[count++].createGraphics();
		                gr.drawImage(image, 0, 0, anchoBloque , altoBloque , anchoBloque * y, altoBloque * x, anchoBloque * y + anchoBloque, altoBloque * x + altoBloque, null);
		                gr.dispose();
		                
		            }
		        }
    
	        } catch (IOException e) {
	        	System.out.println(e.getMessage());
	        }
	        
	        this.entropia = new Entropia(bloquesArr,cantColores);
	        this.bloqueMayorH = entropia.getBloqueMayorH();
	    	this.bloqueMediaH = entropia.getBloqueMediaH();
	    	this.bloqueMenorH = entropia.getBloqueMenorH();
	    }
	    
	  //-------------------------------------------------------------------------------------------
	  //--------------------- INCISO A ------------------------------------------------------------
	    
	    public void calcularEntropias(){
	    	VentanaPrincipal.progressBar.setValue(40);
	    	
	    	Entropia entropia = new Entropia(bloquesArr,cantColores);
	    	
	    	VentanaPrincipal.progressBar.setValue(60);
	    	
	    	//Guardo en archivo txt
	    	StringBuilder txt = new StringBuilder();
	    	txt.append(entropia.calcularEntropiaSinMemoria());
	    	txt.append(entropia.calcularEntropiaConMemoria());
	    	PrintWriter out;
			try {
				out = new PrintWriter("Entropias_de_"+ fileName.toString() +".txt");
				out.println(txt);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			VentanaPrincipal.progressBar.setValue(100);
			
	    }
	      
	  //-------------------------------------------------------------------------------------------
	  //--------------------- INCISO B ------------------------------------------------------------
	    
	    public void getHistogramas () {
	    	VentanaPrincipal.progressBar.setValue(40);
	    	generarHistograma(bloqueMayorH, "Mayor_"+fileName.toString());
	    	VentanaPrincipal.progressBar.setValue(60);
	    	generarHistograma(bloqueMenorH, "Menor_"+fileName.toString());
	    	VentanaPrincipal.progressBar.setValue(80);
	    	generarHistograma(bloqueMediaH, "Media_"+fileName.toString());
	    	VentanaPrincipal.progressBar.setValue(100);
	    }
	    
	  //-------------------------------------------------------------------------------------------
	    
	    private void generarHistograma (int posBloque, String nombre) { //posBloque = posicion del bloque en bloquesArr 
	    	
	    	BufferedImage bloque = bloquesArr[posBloque];
	    	double[] datos = new double[bloque.getWidth() * bloque.getHeight()];
	    	
	    	//Paso los colores del bloque al arreglo "datos"
	    	int r; 
	    	int pos=0;//para llenar el arreglo de datos
	    	for (int x = 0; x < bloque.getWidth(); x++) {
	    		for (int y = 0; y < bloque.getHeight(); y++) {
	    			
	    			int rgb = bloque.getRGB(x, y);
	    			Color color = new Color(rgb, true); 
	    			r = color.getRed(); //extraigo el color 
	    			datos[pos] = r; //Lo guardo en el arreglo
	    			pos++; //Avanzo en el arreglo "datos"
	    		} 
	    	}
	    	
	    	
	    	HistogramDataset dataset = new HistogramDataset();
	        dataset.setType(HistogramType.FREQUENCY);
	        
	         
	        dataset.addSeries("Histograma", datos, cantColores,0,255); //(Nombre,datos,cant_barras,min,max)
	        
	        String plotTitle = "Histograma"; 
	        String x = "Colores";
	        String y = "Cantidad de pixeles";
	        
	        JFreeChart chart = ChartFactory.createHistogram(plotTitle, x, y, 
	                 dataset, PlotOrientation.VERTICAL, false, false, false);
	        
	        int ancho = 2000;
	        int alto = 500;
	        try { //guardo el histograma como imagen
	         ChartUtilities.saveChartAsPNG(new File("Histograma_De_Bloque_"+ (posBloque) + "_Entropia_" + nombre + ".PNG"), chart, ancho, alto);
	        } catch (IOException e) {}
	    }
	    
	  //-------------------------------------------------------------------------------------------
	  //--------------------- INCISO C ------------------------------------------------------------
	    
	    public void getMatricesCondicionalesMayorYMenorH() {
	    	
	    	StringBuilder salida = new StringBuilder();
	    	salida.append("\n");
	    	
	    	salida.append("Matriz condicional de transicion entre las intensidades de gris para el bloque "+ bloqueMayorH +" de mayor entropia \n");
	    	double[][] matrizCondBloqueMayorH = entropia.calcularProbabilidadesCondicionales(bloquesArr[bloqueMayorH]);
	    	salida.append(matrizATexto(matrizCondBloqueMayorH));
	    	
	    	VentanaPrincipal.progressBar.setValue(40);
	    	
	    	salida.append("\nMatriz condicional de transicion entre las intensidades de gris para el bloque "+ bloqueMenorH + " de menor entropia \n");
	    	double[][] matrizCondBloqueMenorH = entropia.calcularProbabilidadesCondicionales(bloquesArr[bloqueMenorH]);
	    	salida.append(matrizATexto(matrizCondBloqueMenorH));
	    
	    	VentanaPrincipal.progressBar.setValue(60);
	    	
		    String txt = salida.toString();
		    
		    PrintWriter out;
			try {
				out = new PrintWriter("MatricesCondicionales_"+fileName.toString()+".txt");
				out.println(txt);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			VentanaPrincipal.progressBar.setValue(100);
		    
	    }
	    
	  //-------------------------------------------------------------------------------------------
	    
	    private String matrizATexto(double[][] matrizCondicional) {
	    	
	    	StringBuilder out = new StringBuilder();
	    	
		    for (int x=0; x < matrizCondicional.length; x++) { //Por cada fila
		    	
			    out.append(x +"\n |"); //Numero de fila
			   	for (int y=0; y < matrizCondicional[x].length; y++) { //recorro las columnas de esa fila
			   		out.append((matrizCondicional[x][y]));
			   	    if (y!=matrizCondicional[x].length-1) //separacion si no es el ultimo valor a mostrar
			   	    	out.append(" | ");
		    	}
		    	out.append("| \n");
		    }
		    return out.toString();
	    }
	    
	  //-------------------------------------------------------------------------------------------
	  //--------------------- INCISO D ------------------------------------------------------------
	   
	    public void calcularMediaYDesvioBloqueMayorYMenorH() {
	    	
	    	StringBuilder salida = new StringBuilder();
	    	double[][] matrizCondBloqueMayorH = entropia.calcularProbabilidadesCondicionales(bloquesArr[bloqueMayorH]);
	    	double[][] matrizCondBloqueMenorH = entropia.calcularProbabilidadesCondicionales(bloquesArr[bloqueMenorH]);
	    	
	    	double[] probMarginalesMayorH = entropia.calcularProbabilidadesMarginales(bloquesArr[bloqueMayorH]);
	    	salida.append("Bloque con entropia Mayor" + " con numero de bloque: " + (bloqueMayorH) + "\n");
	    	salida.append(CalcularMediaYDesvio(probMarginalesMayorH, matrizCondBloqueMayorH));
	    	salida.append("\n");
	    	
	    	double[] probMarginalesMenorH = entropia.calcularProbabilidadesMarginales(bloquesArr[bloqueMenorH]);
	    	salida.append("Bloque con entropia Menor" + " con numero de bloque: " + (bloqueMenorH) + "\n");
	    	salida.append(CalcularMediaYDesvio(probMarginalesMenorH, matrizCondBloqueMenorH));
	    	
	    	String txt = salida.toString();
	    	 PrintWriter out;
				try {
					out = new PrintWriter("MediaYDesvio_"+fileName.toString()+".txt");
					out.println(txt);
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			
				VentanaPrincipal.progressBar.setValue(100);

	    }
	    
	  //-------------------------------------------------------------------------------------------
	    
	    private String CalcularMediaYDesvio(double[] probMarginales, double[][] matrizCondBloque)
		{ 
	    	StringBuilder salida = new StringBuilder();
	    	
	    	//Inicializo variables que utilizo
	    	
	    	double[] distAcumColores = calcularDistAcumColores(probMarginales);
	    	double[][] distAcumColorDadoColor = calcularDistAcumColorDadoColor(matrizCondBloque);
	    	
			int [] exitos = new int[cantColores]; //cant de veces que aparecio cada color
			for (int i=0; i<exitos.length; i++) {
				exitos[i] = 0;
			}
			
			int tiradas = 0;
			
			double [] probAct = new double[cantColores]; //probabilidad que salga cada color calculada por simulacion
			for (int j=0; j<probAct.length; j++) {
				probAct[j] = 0;
			}
			
			double [] probAnterior = new double[cantColores]; //probabilidad anterior que salga cada color para ver si convergen los valores
			for (int j=0; j<probAnterior.length; j++) {
				probAnterior[j] = -1;
			}
			
			double sumatoria=0; //para calcular la media
			double sumatoriadecuadrados=0;//para calcular varianza y por ende desvio
			
			//Simulacion
			
			//Calculo <X>
			int color = primerColor(distAcumColores);
			int	colorAnt;
			
			while (!this.ConvergeVector(probAct, probAnterior) || tiradas < minTiradas) 
			{	
					exitos[color]++;
					tiradas++;
					sumatoria += color;
					
					for (int i = 0; i < probAct.length; i++) 
					{
						probAnterior[i] = probAct [i];
						probAct[i] = ((double) exitos[i]) / tiradas;
					}
				
				colorAnt = color;
				color = colorDadoColor(distAcumColorDadoColor, color);
				while ( (color < 0) || (color > 255) )
					color = colorDadoColor(distAcumColorDadoColor, colorAnt);
			}
			
			VentanaPrincipal.progressBar.setValue(VentanaPrincipal.progressBar.getValue()+20);
				
			double media = sumatoria/tiradas;
			
			//Calculo Varianza
				//Reseteo estructuras

			tiradas = 0;
			for (int h=0; h<exitos.length;h++) {
				exitos[h] = 0;
			}
			for (int j=0; j<probAct.length; j++) {
				probAct[j] = 0;
			}
			for (int j=0; j<probAnterior.length; j++) {
				probAnterior[j] = -1;
			}
		
			color = primerColor(distAcumColores);
			
			while (!this.ConvergeVector(probAct, probAnterior) || tiradas < minTiradas) 
			{	
					exitos[color]++;
					tiradas++;
					sumatoriadecuadrados += Math.pow((color-media),2);
					
					for (int i = 0; i < probAct.length; i++) 
					{
						probAnterior[i] = probAct [i];
						probAct[i] = ((double) exitos[i]) / tiradas;
					}
				
				colorAnt = color;
				color = colorDadoColor(distAcumColorDadoColor, color);
				while ( (color < 0) || (color > 255) )
					color = colorDadoColor(distAcumColorDadoColor, colorAnt);
			}
			
			VentanaPrincipal.progressBar.setValue(VentanaPrincipal.progressBar.getValue()+20);
			
			double varianza= (double) sumatoriadecuadrados/tiradas;
			
			salida.append("Media= " + media + "\n");
			salida.append("Desvio Estandar= " + Math.sqrt(varianza) + "\n");
			
			return salida.toString();
	
		}
	    
      //-------------------------------------------------------------------------------------------

		private boolean ConvergeVector(double[] probAct, double[] probAnterior) 
		{
			for (int i = 0; i < probAct.length; i++) {
				if (!(Math.abs(probAct[i]-probAnterior[i]) < epsilon))
					return false;
			}
			return true;
		}
		
	  //-------------------------------------------------------------------------------------------
		
		private double[] calcularDistAcumColores(double[] probMarginales) {
			
			
			double[] acumuladas = new double[cantColores];
			double sumaAcum = 0;
			
			for (int i=0; i<probMarginales.length; i++) {
				acumuladas[i] = sumaAcum + probMarginales[i];
				sumaAcum += probMarginales[i];
			}
			
			return acumuladas;
		}
		
	  //-------------------------------------------------------------------------------------------
		
		private double[][] calcularDistAcumColorDadoColor(double[][] matrizCondBloque) {
			
			//se acumula por fila (cada fila es un arreglo de acumuladas)
			
			double[][] matrizCondAcum = new double[cantColores][cantColores];
			double sumaAcum;
			
			for (int i=0; i<cantColores; i++) {
				sumaAcum=0;
				for (int j=0; j<cantColores; j++) {
					matrizCondAcum[i][j] = matrizCondBloque[i][j] + sumaAcum;
					sumaAcum += matrizCondBloque[i][j];
				}
			}
			
			return matrizCondAcum;
		}
		
	  //-------------------------------------------------------------------------------------------
	  
		private int primerColor(double[] distAcumColor){
			
			double prob = (double) Math.random();
			for (int i = 0; i < distAcumColor.length; i++) 
			{
				if (prob < distAcumColor[i])
				{
					return i;
				}
			}
			
			return -1;
		}
		
	  //-------------------------------------------------------------------------------------------
		
		private int colorDadoColor(double[][] distAcumColorDadoColor, int color){
			
			float prob = (float) Math.random();
			int i = 0;
			while (i < cantColores)
			{
				if (prob < distAcumColorDadoColor[color][i])
				{
					break;
				}
				
				i++;
			}		
			
			return i;	
		}
		
	  //-------------------------------------------------------------------------------------------

	 
}

