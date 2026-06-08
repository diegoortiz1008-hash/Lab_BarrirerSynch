package edu.eci.arsw.samples;

import java.util.concurrent.CountDownLatch;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		int numHilos=20;

		CountDownLatch latch = new CountDownLatch(numHilos);
		
		HiloProc[] hilos=new HiloProc[numHilos];
		
		for (int i=0;i<numHilos;i++){
			hilos[i] = new HiloProc(i, latch);
		}
		for (int i=0;i<numHilos;i++){
			hilos[i].start();
		}

		latch.await();

		long tiempoPromedio=0;
		
		for (int i=0;i<numHilos;i++){
			tiempoPromedio+=hilos[i].getResultado();
		}

		System.out.println("El tiempo promedio de la ejecuci�n fue de:"+tiempoPromedio/numHilos);
	}
	
}
