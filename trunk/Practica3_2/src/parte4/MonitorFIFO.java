package parte4;

import parte1.*;

public class MonitorFIFO implements MonitorArbitraje {

	private boolean hayEscritor = false;
	private boolean habilitadoTicket = true;
	private boolean lectoresSeguidos = true;
	private int numLectores = 0;
	private long startTime;
	private int turnoActual = 0;
	private Secuenciador numTicket;

	class Secuenciador{
		public int seq = 0;
		
		public int ticket(){
			return ++seq;
		}
	}
	
	MonitorFIFO() {
		startTime = System.currentTimeMillis();
		numTicket = new Secuenciador();
	}

	public synchronized void entrarLeer() throws InterruptedException {
		while (!habilitadoTicket)
			wait();
		
		numTicket.ticket();
		
		while (hayEscritor || !lectoresSeguidos)
		    wait();
				
		lectoresSeguidos = true;
		numLectores++;
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer " + numTicket.seq);
	}

	public synchronized void salirLeer() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		turnoActual++;
		numLectores--;
		if (numLectores == 0){
			notify();
		}
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		while (!habilitadoTicket)
			wait();
		
		numTicket.ticket();
		
		lectoresSeguidos = false;
		habilitadoTicket = false;
		while (hayEscritor || (numTicket.seq > turnoActual + 1 && !hayEscritor))
		    wait();
		
		hayEscritor = true;
        lectoresSeguidos = true;
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName()  + " va a empezar a escribir " + numTicket.seq);
	}

	public synchronized void salirEscribir() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de escribir");

		hayEscritor = false;
		habilitadoTicket = true;
		turnoActual++;
		notifyAll();
	}
	
}