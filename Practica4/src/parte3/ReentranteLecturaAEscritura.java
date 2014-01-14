package parte3;

import parte1.*;
import java.util.HashMap;

public class ReentranteLecturaAEscritura implements MonitorArbitraje {

	private int numLectores = 0;
	private long startTime;
	private int escritoresEnEspera = 0;
	private HashMap<Thread, Integer> threadMapLectura;
	private int accesoEscritura = 0;
	private Thread escritorDentro = null;

	ReentranteLecturaAEscritura() {
		startTime = System.currentTimeMillis();
		threadMapLectura = new HashMap<Thread, Integer>();	
	}
	
	/*
	 *  Se concede a un hilo la posibilidad de entrar en lectura si una de las siguientes condiciones es cierta:

		- puede conseguir acceso en lectura (no hay ni escritor actual ni escritores en espera)
		- ya tiene acceso en lectura (independientemente de si hay escritores en espera)
	 */

	public synchronized void entrarLeer() throws InterruptedException {
		while ((escritorDentro!=null || escritoresEnEspera != 0) && !tieneAccesoLectura(Thread.currentThread())) 
			wait();
		
		numLectores++;
		entraThreadLectura(Thread.currentThread());
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer");
	}

	public synchronized void salirLeer() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		numLectores--;
		saleThreadLectura(Thread.currentThread());
		
		if (numLectores == 0) 
			notify();
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		//Un hilo lector solo puede pasar a tener acceso en escritura si es el único lector que no está esperando
		//escribir es decir, se permite a un hilo escribir cuando hay lectores dentro si todos ellos están esperando escribir
		escritoresEnEspera++;
		while ( (accesoEscritura>0 && escritorDentro!=Thread.currentThread())
				//|| (escritorDentro!=null) 
				|| numLectores != 0) wait();
		
		if(escritorDentro==null) escritorDentro=Thread.currentThread();
		accesoEscritura++;
		
		escritoresEnEspera--;

		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName()  + " va a empezar a escribir por vez "+accesoEscritura);
	}

	public synchronized void salirEscribir() {
		
		accesoEscritura--;
		if(accesoEscritura==0)escritorDentro = null;
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
				+ Thread.currentThread().getName() + " ha terminado de escribir, le quedan "+accesoEscritura);

		
		notifyAll();
	}
	
	private void entraThreadLectura(Thread t){
		Integer count = threadMapLectura.get(t);
		if (count == null)
			threadMapLectura.put(t, 1);
		else{
			threadMapLectura.put(t, ++count);
			System.out.println("Entra thread lectura: " +t.getName()+ ", count: "+ count);
		}
	}
	
	private void saleThreadLectura(Thread t){
		Integer count = threadMapLectura.get(t);
		if (count == null)
			System.err.println("Hubo un error: saleThreadLectura, el thread " +t.getName()+ " no existe!");
		else{
			threadMapLectura.put(t, --count);
			System.out.println("Sale thread lectura: " +t.getName()+ ", count: "+ count);
		}
	}
	
	private boolean tieneAccesoLectura(Thread t){
		// Si count es > 0, el thread ya ha entrado a leer por lo que tiene acceso lectura
		
		Integer count = threadMapLectura.get(t);
		return count == null ? false : count > 0;
	}
}
