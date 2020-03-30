package teoInfoTPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;

public class Header implements Serializable{

	int alto;
	int ancho;
	boolean[] esHuffman;//arreglo que me indica si el bloque esta codificado por huffman (true) o por RLC (false)
	Hashtable<Integer,int[]> frecuencias; //si esta codificado por huffman, tengo que guardar su arreglo de frecuencias
	int[] largoCodificaciones;
	
	public Header (int alto, int ancho, int cantBloques) {
		this.alto = alto;
		this.ancho = ancho;
		this.esHuffman = new boolean[cantBloques];
		this.frecuencias = new Hashtable<Integer,int[]>();
		this.largoCodificaciones = new int[cantBloques];
	}
	
	public Header (byte[] arrByte) { //Inicializa al header con la info de arch recuperado
		try {
			ByteArrayInputStream bs= new ByteArrayInputStream(arrByte); // arrByte es el byte[]
			ObjectInputStream is = new ObjectInputStream(bs);
			Header headeraux = (Header)is.readObject();
			
			this.alto = headeraux.getAlto();
			this.ancho = headeraux.getAncho();
			this.esHuffman = headeraux.getEsHuffman();
			this.frecuencias = headeraux.getFrecuencias();
			this.largoCodificaciones = headeraux.getLargoCodificaciones();
			
			is.close();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setEsHuffman(int posicion, boolean b){
		esHuffman[posicion] = b;
	}
	
	public void setFrecuancias(Integer clave, int[] frec){
		frecuencias.put(clave, frec);
	}
	
	public void setLargoCodBloque(int bloque, int largo) {
		largoCodificaciones[bloque] = largo; 
	}
	
	public int getAlto() {
		return alto;
	}
	
	public int getAncho() {
		return ancho;
	}
	
	public boolean[] getEsHuffman(){
		return esHuffman;
	}
	
	public Hashtable<Integer,int[]> getFrecuencias() {
		return frecuencias;
	}
	
	public boolean bloqueEsHuffman(int bloque){
		return esHuffman[bloque];
	}
	
	public int[] getFrecuenciasBloque(int bloque) {
		return frecuencias.get(bloque);
	}
	
	public int getCantBloques() {
		return esHuffman.length;
	}
	
	public int[] getLargoCodificaciones(){
		return largoCodificaciones;
	}
	
	public int getLargoCodBloque(int bloque) {
		return largoCodificaciones[bloque];
	}
	
	public byte[] toByteArr() {
		ByteArrayOutputStream bs= new ByteArrayOutputStream();
		ObjectOutputStream os;
		try {
			os = new ObjectOutputStream (bs);
			os.writeObject(this);
			os.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bs.toByteArray(); // devuelve byte[]
	}
	
}
