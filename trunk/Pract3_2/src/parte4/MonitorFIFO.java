package parte4;

import parte1.*;

public class MonitorFIFO implements MonitorArbitraje {

	private int numLectores = 0;
	private boolean escritor = false; 
	private long startTime;
	private int escritoresEnEspera = 0;
	private boolean turnoLectores = true;
	
	class Secuenciador{
		int seq = 0;
		public synchronized int ticket(){
			return seq++;
		}
	}
	
	MonitorFIFO() {
		startTime = System.currentTimeMillis();
	}

	public synchronized void entrarLeer() throws InterruptedException {
		while (!turnoLectores || escritor || escritoresEnEspera != 0) wait();
		
		numLectores++;
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer");
	}

	public synchronized void salirLeer() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		numLectores--;
		if (numLectores == 0){
			turnoLectores = false;
			notify();
		} 
			
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		escritoresEnEspera++;
		if(turnoLectores && numLectores==0) turnoLectores = false;
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
		//if(escritoresEnEspera==0)
		turnoLectores = true;
		notifyAll();

	}
	
}
