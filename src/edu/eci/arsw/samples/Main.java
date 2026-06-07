package edu.eci.arsw.samples;

public class Main {

	public static void main(String[] args) {
		int numHilos=20;
		
		HiloProc[] hilos=new HiloProc[numHilos];
		
		for (int i=0;i<numHilos;i++){
			hilos[i]=new HiloProc(i);
		}
		for (int i=0;i<numHilos;i++){
			hilos[i].start();
		}

		long tiempoPromedio=0;
		
		for (int i=0;i<numHilos;i++){
			tiempoPromedio+=hilos[i].getResultado();
		}

		System.out.println("El tiempo promedio de la ejecución fue de:"+tiempoPromedio/numHilos);
	}
	
}
