package teoInfoTPE;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class RLC {

	private static final int maxByte = 255;
	
	public byte[] codificar(BufferedImage bloque) {
    	
    	int r = -1;
    	int rAnt = 0;
    	int cant=0;
    	int resto=0;
    	
    	ArrayList<Integer> codificacion = new ArrayList<Integer>();
    	
    	for (int x = 0; x < bloque.getWidth(); x++) {
    		for (int y = 0; y < bloque.getHeight(); y++) {
    			int rgb = bloque.getRGB(x, y);
    			Color color = new Color(rgb, true); 
    			rAnt = r;
    			r = color.getRed(); //extraigo el color
    			if (r == rAnt)
    				cant++;
    			else { //preg si no es -1
    				if (rAnt != -1) {
    					resto = cant;
    					while (resto > maxByte) {
    						resto = resto - maxByte;
	    					codificacion.add(rAnt);
	    					codificacion.add(maxByte);
    					}
    					codificacion.add(rAnt);
    					codificacion.add(resto);
    				}
    				cant = 1;//reinicio cuenta
    			}
    		}
    	}
    	codificacion.add(rAnt);
		codificacion.add(cant);
    	 byte[] arrByte = intListToByteArray(codificacion); // Pasa codificacion a Byte[]
         return arrByte;
    }
	
	//-------------------------------------------------------------------------------------------

	private byte[] intListToByteArray(ArrayList<Integer> lista) {
	
		byte[] salida = new byte[lista.size()];
		byte[] aux;
		
		Iterator<Integer> it = lista.iterator();
	    int valor;
	    int posSalida=0;
	
	    while (it.hasNext()){
	        valor = it.next();
	        aux = new byte[] {(byte)((valor >> 0) & 0xff)};
	        salida[posSalida] = aux[0];
	        posSalida++;
	    }
	    
		return salida;
	}
	
	//-------------------------------------------------------------------------------------------
	
	public int[] decodificar (byte[] data, int inicioDataBloque, int cantPixeles) {
		
		int[] salida = new int[cantPixeles];
		
		int posData = inicioDataBloque;
		
		int color;
		int rep;
		
		int posSalida=0;
		
		while (posSalida < cantPixeles) {
			color = (data[posData] & 0xFF); //En RLC 1 byte = 1 int
			posData++;
			rep = (data[posData] & 0xFF);
			posData++;
			
			for (int h = 0; h < rep; h++) { //repito el color en el arreglo tantas veces como diga rep
				
				salida[posSalida] = color;
				posSalida++;
			}
			
			if(posSalida == cantPixeles) {
				break;
			}
		}	
		return salida;
		
	}
	
	//-------------------------------------------------------------------------------------------
}
