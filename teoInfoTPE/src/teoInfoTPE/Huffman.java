package teoInfoTPE;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;

public class Huffman {

	private static final int cantColores = 256;
	private static int bufferLength = 8;
	Hashtable<Integer,String> hashCodificaciones = new Hashtable<Integer,String>();
	
	public byte[] codificar (int [] frecuencias, BufferedImage bloque) {
		
        NodoHuffman raiz = armarArbolHuffman(frecuencias); //Creo arbol
        
        hashCodificaciones.clear();
        generarCodHuffman(raiz, ""); //lleno hash
        
        String codificacion = codificarConHash(bloque); //codifico con hash
        
        char[] arrCod = codificacion.toCharArray(); //paso a char[]
        
        byte[] dataArray = codificacionAByte(arrCod); //paso de arreglo char a arreglo byte
      
        return dataArray;
    } 
	
  //-------------------------------------------------------------------------------------------
	
	private NodoHuffman armarArbolHuffman(int [] frecuencias) {
		
		 // Heap de nodos de Huffman, ordenados de menor a mayor por frecuencia
        PriorityQueue<NodoHuffman> heapHuffman = new PriorityQueue<NodoHuffman>(new ComparadorHeap());
        
       //Creo un nodo huffman para cada color y lo agrego al Heap
        for (int i = 0; i < cantColores; i++) {
        	 if(frecuencias[i] > 0) {
        		 NodoHuffman nodoHuff = new NodoHuffman(); 
	        	 nodoHuff.color = i;
	        	 nodoHuff.frecuencia = (int) frecuencias[i];
	        	 nodoHuff.derecho = null;
	        	 nodoHuff.izquierdo = null;
	        	 heapHuffman.add(nodoHuff);
        	 }
        }
  
        // Nodo raiz del arbol
        NodoHuffman raiz = null; 

        while (heapHuffman.size() > 1) {  //Mientras queden nodos en el Heap
            
        	NodoHuffman x = heapHuffman.poll(); // Extraigo el primero (el de menor frecuencia)
            NodoHuffman y = heapHuffman.poll();  // Extraigo otro (de menor frecuencia)

            NodoHuffman nuevoNodo = new NodoHuffman(); //Nodo intermedio
  
            nuevoNodo.frecuencia = x.frecuencia + y.frecuencia; // La frecuencia del nuevo nodo es la suma 
            													// de las frecuencias de los dos.
            nuevoNodo.color = -1; //Valor discernible, para representar que no es hoja.
            nuevoNodo.izquierdo = x; 
            nuevoNodo.derecho = y; 
 
            raiz = nuevoNodo; 
            heapHuffman.add(nuevoNodo); //Agrego al Heap
        } 
        return raiz;
	}
	
  //-------------------------------------------------------------------------------------------
	
	//Recorre el arbol para generar un codigo a cada color y guardarlo en el hashCodificaciones
    private void generarCodHuffman(NodoHuffman raiz, String codigo) {
        if ( (raiz.izquierdo == null) && (raiz.derecho == null) && (raiz.color != -1) ) { //Si es una hoja (un color)
        	hashCodificaciones.put(raiz.color, codigo); //Guardo el color y su codificacion en el hash
        }
        else {
        	generarCodHuffman(raiz.izquierdo, codigo + "0"); //Si voy a la izquierda, codifico con 0
        	generarCodHuffman(raiz.derecho, codigo + "1");  //A la derecha, con 1
        }
    }
    
  //-------------------------------------------------------------------------------------------
    //Recorre los colores del bloque y guarda su codificacion en un String.
    
    private String codificarConHash(BufferedImage bloque){
    	
    	StringBuilder resultado = new StringBuilder();
    	int r = 0;
  
    	for (int x = 0; x < bloque.getWidth(); x++) { 
    		for (int y = 0; y < bloque.getHeight(); y++) {
    			int rgb = bloque.getRGB(x, y);
    			Color color = new Color(rgb, true); 
    			r = color.getRed(); //extraigo el color
    			resultado.append(hashCodificaciones.get(r));
       		} 
    	}
    	return resultado.toString();
    }
    
  //-------------------------------------------------------------------------------------------
    
    private byte[] codificacionAByte(char[] secuenciaOriginal) {
    	
    	List<Byte> encodedSequence = encodeSequence(secuenciaOriginal);
		byte[] dataArray = ConvertByteListToPrimitives(encodedSequence);
		
    	return dataArray;
    }
    
  //-------------------------------------------------------------------------------------------
    
    private static byte[] ConvertByteListToPrimitives(List<Byte> input) {
		byte[] ret = new byte[input.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = input.get(i);
		}
		return ret;
	}
    
  //-------------------------------------------------------------------------------------------
    
    private static List<Byte> encodeSequence(char[] sequence) {
		List<Byte> result = new ArrayList<Byte>();

		byte buffer = 0;
		int bufferPos = 0; //PARA SABER CUANTOS BITS VOY METIENDO EN EL BYTE(buffer)

		int i = 0;
		while (i < sequence.length) {
			// La operacion de corrimiento pone un '0'
			buffer = (byte) (buffer << 1); //Hago espacio para el primer bit (pongo un 0)
			bufferPos++; //AUMENTO 1 PORQUE VOY A AGREGAR 1	BIT
			
			if (sequence[i] == '1') { //El bit que tengo q agregar es un 1 y tengo un 0
				buffer = (byte) (buffer | 1); // PONGO UN 1(un OR con 1, siempre da 1)
			}

			if (bufferPos == bufferLength) { //bufferLength = 8 (CONSTANTE). //Si ya llene un byte (el buffer)
				result.add(buffer); //Agrego el byte a la lista de bytes
				buffer = 0; //reseteo valores
				bufferPos = 0;
			}

			i++;
		}

		if ((bufferPos < bufferLength) && (bufferPos != 0)) { //Si ya guarde todos los bits, pero no llegan a completar 1 byte
			buffer = (byte) (buffer << (bufferLength - bufferPos)); //lleno con 0s la cantidad de lugares que quedaron libres
			result.add(buffer); //agrego el byte a la lista de bytes
		}

		return result;
	}
    
  //-------------------------------------------------------------------------------------------
  //DECODIFICACION
  //-------------------------------------------------------------------------------------------
    public int[] decodificar(byte[] data, int inicioDataBloque, int cantPixeles, int[] frecuencias) {
		
    	int[] salida = new int[cantPixeles];//250.000
    	
    	for (int i = 0; i < salida.length; i++) {
    		salida[i]=0;
		}
    	
		int posSalida = 0;
		NodoHuffman raiz = armarArbolHuffman(frecuencias); //arbol de huffman para decodificar
		NodoHuffman nodoActual = raiz;
		
		byte mask = (byte) (1 << (bufferLength - 1)); // mask: 10000000
		int bufferPos = 0;
		int i = inicioDataBloque; //con este me muevo en la data
		
		while (posSalida < cantPixeles) //si es menor lo voy a seguir llenando hasta completarlo
		{
			byte buffer = data[i];	//saco el primer byte de la secuencia original
			while (bufferPos < bufferLength) { //Mientras queden bits por sacar del buffer
				
				if ((buffer & mask) == mask) { //Si da igual a la mascara, el 1er bit es un '1'

			        nodoActual = nodoActual.derecho; //Me voy por la rama derecha
			        // Verifico si llegue a una hoja
			        if ((nodoActual.izquierdo == null) && (nodoActual.derecho == null) && (nodoActual.color != -1)) {
			        	salida[posSalida] = nodoActual.color;
			            nodoActual = raiz;
			            posSalida++; 
			        }
				} else {//caso '0' //Me voy por la rama izquierda
					nodoActual = nodoActual.izquierdo;
					// Verifico si llegue a una hoja
			        if ((nodoActual.izquierdo == null) && (nodoActual.derecho == null) && (nodoActual.color != -1)) { 
			        	salida[posSalida] = nodoActual.color;
			        	nodoActual = raiz;
			            posSalida++; 
			        }
				}
				buffer = (byte) (buffer << 1); //Desplazo el buffer a la derecha, eliminando el bit que ya decodifique y guarde.
				bufferPos++; //Avanzo en el buffer
				
				
				
				if (posSalida == cantPixeles) { //Si ya decodifique todos los bytes del inputSequence
					break;  //TERMINO
				}
			}
			i++;
			bufferPos = 0; 
		}
	
	return salida;
}
    
  //-------------------------------------------------------------------------------------------
	
	
}
