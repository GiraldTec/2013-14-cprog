package parte2;

import parte1.*;


public class MonitorPrioridadEscritores implements MonitorArbitraje {

	private int numLectores = 0;
	private boolean escritor = false; 
	private long startTime;
	private int escritoresEnEspera = 0;
	
	MonitorPrioridadEscritores() {
		startTime = System.currentTimeMillis();
	}

	public synchronized void entrarLeer() throws InterruptedException {
		while (escritor || escritoresEnEspera != 0) wait();
		
		numLectores++;
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer");
	}

	public synchronized void salirLeer() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		numLectores--;
		if (numLectores == 0) 
			notify();
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		escritoresEnEspera++;
		while (escritor || numLectores != 0) wait();
		escritor = true;
		escritoresEnEspera--;

		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName()  + " va a empezar a escribir");
	}

	public synchronized void salirEscribir() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de escribir");

		escritor = false;
		notifyAll();

	}
	
}
