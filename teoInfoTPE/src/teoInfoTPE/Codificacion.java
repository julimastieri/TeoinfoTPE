package teoInfoTPE;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;


public class Codificacion {
	
	private static final int cantColores = 256;
	private static final int tamHeader = 3; //3 bytes
	
  //-------------------------------------------------------------------------------------------
	
	public void codificarImagen(double Ht) {
		
		VentanaPrincipal.progressBar.setValue(20);
		
		Parte1 p1 = new Parte1();
		double [] entropiasConMemoria = p1.entropia.getEntropiasConMemoria();
		String nombreArch= "imagenCodificada_"+p1.fileName.toString()+".txt";
		
		VentanaPrincipal.progressBar.setValue(40);
		
		ArrayList<Byte> dataList = new ArrayList<Byte>();
		byte[] dataAux;
		
		Huffman huffman = new Huffman();
		RLC rlc = new RLC();
		Header header = new Header (p1.altoImagen, p1.anchoImagen, p1.bloquesArr.length);
		
		for(int i=0; i< p1.bloquesArr.length; i++) { //por cada bloque del arreglo
			if (entropiasConMemoria[i] < Ht) {
				header.setEsHuffman(i, true);
				int[] frecuencias = contarColoresBloque(p1.bloquesArr[i]);
				header.setFrecuancias(i, frecuencias);
				dataAux = huffman.codificar(frecuencias, p1.bloquesArr[i]);
				header.setLargoCodBloque(i, dataAux.length);
			}
			else {
				header.setEsHuffman(i, false);
				dataAux = rlc.codificar(p1.bloquesArr[i]);
				header.setLargoCodBloque(i, dataAux.length);
			}
			
			//agrego dataAux al final de dataList
			for (int h = 0; h < dataAux.length; h++) 
				dataList.add(dataAux[h]);
	
		}
		
		VentanaPrincipal.progressBar.setValue(60);
		
		byte[] dataArray = new byte[dataList.size()];

		for (int w = 0; w < dataArray.length; w++) {
			dataArray[w] = dataList.get(w);
		}
		
		guardarANivelBit(nombreArch, header, dataArray);
	}		
	    
  //-------------------------------------------------------------------------------------------
    
	public void guardarANivelBit (String nombreArch, Header header, byte[] dataArray) {
		
		try {
			byte[] head = header.toByteArr(); //paso objeto a byte array
			byte[] tamHead = tamHeadAByte(head.length); //paso int a byte
			
			//pongo todo en un solo array
			int tam = head.length + dataArray.length + tamHead.length;
			byte[] outArray = new byte[tam];
			
			int contador = 0;
			int contAux = 0;
			
			while (contAux < tamHead.length) { //Tamanio del header
				outArray[contador] = tamHead[contAux];
				contador++;
				contAux++;
			}
			
			contAux=0;
			
			while (contAux < head.length){ //Header
				outArray[contador] = head[contAux];
				contador++;
				contAux++;
			}
			
			contAux = 0;
			
			while (contAux < dataArray.length) { //Data
				outArray[contador] = dataArray[contAux];
				contador++;
				contAux++;
			}
			
			VentanaPrincipal.progressBar.setValue(80);
			
			//ahora lo escribo
			FileOutputStream out = new FileOutputStream(nombreArch);
			out.write(outArray);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		VentanaPrincipal.progressBar.setValue(100);
		
	}
	
  //-------------------------------------------------------------------------------------------
	
	private byte[] tamHeadAByte(int data) {
		return new byte[] {
		        (byte)((data >> 16) & 0xff), //tengo 24 bits los 8 primeros (de adelante hacia atras)
		        (byte)((data >> 8) & 0xff), 
		        (byte)((data >> 0) & 0xff), //8 ultimos
		    };//leyendo el arreglo de 0 a 2 es como leer bits de izq a der.
	}
	
  //-------------------------------------------------------------------------------------------
    
	public int[] decodificarBloques(Header header, byte[] data) {
			
			int cantBloques = header.getCantBloques();
			int cantPixeles = (header.getAlto() * header.getAncho())/cantBloques;
			
			int[] salida = new int[header.getAlto() * header.getAncho()];
			int[] salidAux;
			int posSalida = 0;
			
			Huffman huffman = new Huffman();
			RLC rlc = new RLC();
			
			int inicioDataBloque=0;
			
			for(int i=0; i < cantBloques; i++) {
				if (header.bloqueEsHuffman(i)) {
					salidAux = huffman.decodificar(data, inicioDataBloque, cantPixeles, header.getFrecuenciasBloque(i));
				}
				else {
					salidAux = rlc.decodificar(data, inicioDataBloque, cantPixeles);
				}
				
				inicioDataBloque += header.getLargoCodBloque(i);
				
				//Inserto aux al final de salida
				for (int h = 0; h < salidAux.length; h++) {
					salida[posSalida] = salidAux[h];
					posSalida++;
				}
			}
			return salida;
		}
 
  //-------------------------------------------------------------------------------------------
    
    public void decodificarImagen() {
    	try {
    		
    		VentanaPrincipal.progressBar.setValue(20);
    		//Cargar imagen desde la PC
	    	File miDir = new File (".");
	        JFileChooser fileChooser = new JFileChooser();
	        fileChooser.setCurrentDirectory(miDir);
	        
	        fileChooser.showOpenDialog(fileChooser);
	        String ruta = fileChooser.getSelectedFile().getAbsolutePath();
	        File file = new File(ruta);
	        byte[] archEnBytes = Files.readAllBytes(new File(ruta).toPath());
	        
	        String nombre = file.getName();
	        String fileName = nombre.substring(nombre.indexOf("_"), nombre.lastIndexOf("."));
			
	        VentanaPrincipal.progressBar.setValue(40);
	        
			//extraigo tamanio header
			byte[] tamHead = new byte[tamHeader];
			for (int i = 0; i<tamHead.length; i++)
			 	tamHead[i] = archEnBytes[i];
			
			//paso tam head a bytes
			int tamHeadInt = tamHeadAInt(tamHead);
			
			//extraigo header
			int itByte = tamHeader;
			byte[] head = new byte[tamHeadInt];
			
			for (int j = 0; j<head.length; j++) {
			 	head[j] = archEnBytes[itByte];
			 	itByte++;
			}
			Header header = new Header(head);
			
			//extraigo data
			int tamData = archEnBytes.length - itByte;
			byte[] data = new byte[tamData];
			
			for (int h = 0; h<data.length; h++) {
			 	data[h] = archEnBytes[itByte];
			 	itByte++;
			}
			
			VentanaPrincipal.progressBar.setValue(60);
			
			//Decodifico bloques
			int[] rgbs = decodificarBloques(header, data);
			
			VentanaPrincipal.progressBar.setValue(80);
			
			//Reconstruyo imagen
			reconstruirImagen (rgbs, header.getAncho(), header.getAlto(), fileName); //rgbs arreglo int[] de colores
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	VentanaPrincipal.progressBar.setValue(100);
    }
    
  //-------------------------------------------------------------------------------------------
    
    public int tamHeadAInt(byte[] tamHead) {
    	return   tamHead[2] & 0xFF |
                (tamHead[1] & 0xFF) << 8 |
                (tamHead[0] & 0xFF) << 16;
    }
    
  //-------------------------------------------------------------------------------------------
    
    int[] contarColoresBloque (BufferedImage bloque) {
    	int[] resultado  = new int[cantColores];
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
	public void reconstruirImagen (int[] rgbs, int width, int height, String fileName) {
		
		BufferedImage nuevaImagen = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		
		int rgb;
		int j=0;
		
		
		for (int x = 0; x < height; x += 500) { //Recorro por filas la nueva imagen
			for (int y = 0; y < width; y += 500) {
					
				for (int c = y; c< y+500; c++) { 
					for (int f = x; f < x+500; f++) {
						
						rgb = rgbs[j];
						rgb = (rgb << 8) + rgbs[j]; 
						rgb = (rgb << 8) + rgbs[j];
						nuevaImagen.setRGB(c, f, rgb); //(columna,fila,color)
						j++;
					}	
				}		       
			 }
		}

		 File outputFile = new File("ImagenDecodificada_"+fileName+".bmp");
		 try {
			  
			ImageIO.write(nuevaImagen, "bmp", outputFile);
			
		 } catch (IOException e) {
			e.printStackTrace();
		 }
	}

  //-------------------------------------------------------------------------------------------
}

