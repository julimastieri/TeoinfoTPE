package teoInfoTPE;

import java.util.Comparator;

public class ComparadorHeap implements Comparator<NodoHuffman> {
	
	public int compare(NodoHuffman x, NodoHuffman y) { 
		
	   return x.frecuencia - y.frecuencia; 
	   
	}  

}

