package parte1;

import java.util.HashMap;

public class ReentranteLectura implements MonitorArbitraje {

	private int numLectores = 0;
	private boolean hayEscritor = false; 
	private long startTime;
	private int escritoresEnEspera = 0;
	private HashMap<Long, Integer> threadMapLectura;

	ReentranteLectura() {
		startTime = System.currentTimeMillis();
		threadMapLectura = new HashMap<Long, Integer>();	
	}
	
	/*
	 *  Se concede a un hilo la posibilidad de entrar en lectura si una de las siguientes condiciones es cierta:

		- puede conseguir acceso en lectura (no hay ni escritor actual ni escritores en espera)
		- ya tiene acceso en lectura (independientemente de si hay escritores en espera)
	 */

	public synchronized void entrarLeer() throws InterruptedException {
		while ((hayEscritor || escritoresEnEspera != 0) && !tieneAccesoLectura(Thread.currentThread().getId())) 
			wait();
		
		numLectores++;
		entraThreadLectura(Thread.currentThread().getId());
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer");
	}

	public synchronized void salirLeer() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		numLectores--;
		saleThreadLectura(Thread.currentThread().getId());
		
		if (numLectores == 0) 
			notify();
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		escritoresEnEspera++;
		while (hayEscritor || numLectores != 0) wait();
		hayEscritor = true;
		escritoresEnEspera--;

		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName()  + " va a empezar a escribir");
	}

	public synchronized void salirEscribir() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de escribir");

		hayEscritor = false;
		notifyAll();
	}
	
	private void entraThreadLectura(long id){
		Integer count = threadMapLectura.get(id);
		if (count == null)
			threadMapLectura.put(id, 1);
		else{
			threadMapLectura.put(id, ++count);
			System.out.println("Entra thread lectura: " +id+ ", count: "+ count);
		}
	}
	
	private void saleThreadLectura(long id){
		Integer count = threadMapLectura.get(id);
		if (count == null)
			System.err.println("Hubo un error: saleThreadLectura, el thread " +id+ " no existe!");
		else{
			threadMapLectura.put(id, --count);
			System.out.println("Sale thread lectura: " +id+ ", count: "+ count);
		}
	}
	
	private boolean tieneAccesoLectura(long id){
		// Si count es > 0, el thread ya ha entrado a leer por lo que tiene acceso lectura
		
		Integer count = threadMapLectura.get(id);
		return count == null ? false : count > 0;
	}
}
