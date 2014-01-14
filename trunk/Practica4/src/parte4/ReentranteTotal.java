package parte4;

import java.util.HashMap;
import java.util.Map.Entry;

import parte1.MonitorArbitraje;

public class ReentranteTotal implements MonitorArbitraje {

	private int numLectores = 0;
	private long startTime;
	private int escritoresEnEspera = 0;
	private HashMap<Thread, Integer> threadMapLectura;
	private HashMap<Thread, Integer> threadMapLecturaEsperaEscritura;
	private int accesoEscritura = 0;
	private Thread escritorDentro = null;
	private Thread unicoLectorDentro = null;

	ReentranteTotal() {
		startTime = System.currentTimeMillis();
		threadMapLectura = new HashMap<Thread, Integer>();
		threadMapLecturaEsperaEscritura = new HashMap<Thread, Integer>();	
	}
	
	/*
	 *  Se concede a un hilo la posibilidad de entrar en lectura si una de las siguientes condiciones es cierta:

		- puede conseguir acceso en lectura (no hay ni escritor actual ni escritores en espera)
		- ya tiene acceso en lectura (independientemente de si hay escritores en espera)
	 */

	public synchronized void entrarLeer() throws InterruptedException {
		
		if (!(tieneAccesoEscritura(Thread.currentThread())||tieneAccesoLectura(Thread.currentThread()))){
			while((escritorDentro!=null || escritoresEnEspera != 0))
				wait();	
		}

		numLectores++;
		entraThreadLectura(Thread.currentThread());
		
		if (numLectores == 1)
			unicoLectorDentro = Thread.currentThread();
		else
			unicoLectorDentro = null;
		
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " va a empezar a leer");
	}

	public synchronized void salirLeer() {
		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName() + " ha terminado de leer");
		
		numLectores--;
		saleThreadLectura(Thread.currentThread());
		
		if (numLectores == 1)
			unicoLectorDentro = buscaUltimoLector();
		else
			unicoLectorDentro = null;
		
		if (numLectores == 0) 
			notify();
	}

	public synchronized void entrarEscribir() throws InterruptedException {
		//Un hilo lector solo puede pasar a tener acceso en escritura si es el único lector que no está esperando
		//escribir es decir, se permite a un hilo escribir cuando hay lectores dentro si todos ellos están esperando escribir
		escritoresEnEspera++;
		
		if (esLector(Thread.currentThread())){
			// si es lector, pasa al hashmap secundario de espera escritura
			threadMapLecturaEsperaEscritura.put(Thread.currentThread(), threadMapLectura.remove(Thread.currentThread()));
		}
		else{
			//En cuanto a un hilo que intenta entrar en escritura sin haber entrado previamente en lectura, 
			//podría escribirse en el segundo HashMap con valor 0 o null
			threadMapLecturaEsperaEscritura.put(Thread.currentThread(), 0);
		}
			
		while (accesoEscritura>0 && escritorDentro!=Thread.currentThread() && unicoLectorDentro!=Thread.currentThread()) 
			wait();
		
		if(escritorDentro==null){
			escritorDentro=Thread.currentThread();
		}
		
		// ha conseguido escribir, vuelve al hashmap primario
		threadMapLectura.put(escritorDentro, threadMapLectura.remove(escritorDentro));
		
		accesoEscritura++;
		escritoresEnEspera--;

		System.out.println( (System.currentTimeMillis()-startTime) + ": "
			+ Thread.currentThread().getName()  + " va a empezar a escribir por vez "+accesoEscritura);
	}

	public synchronized void salirEscribir() {
		accesoEscritura--;
		
		if(accesoEscritura==0)
			escritorDentro = null;
		
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
		if (count == null){
			count = threadMapLecturaEsperaEscritura.get(t);
			if (count == null)
				System.err.println("Hubo un error: saleThreadLectura, el thread " +t.getName()+ " no existe!");
			else{
				threadMapLecturaEsperaEscritura.put(t, --count);
				System.out.println("Sale thread lectura: " +t.getName()+ ", count: "+ count);
			}
		}
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
	
	private boolean tieneAccesoEscritura(Thread t){
		// Si el escritor que hay dentro es �l mismo, tiene acceso a escritura
		return escritorDentro == t;
	}
	
	private Thread buscaUltimoLector() {
		for (Entry<Thread, Integer> entry : threadMapLectura.entrySet()){
			if (entry.getValue()!=null && entry.getValue() > 0)
				return entry.getKey();
		}
		return null;
	}
	
	private boolean esLector(Thread t) {
		for (Entry<Thread, Integer> entry : threadMapLectura.entrySet()){
			if (t == entry.getKey() && entry.getValue() > 0)
				return true;
		}
		return false;
	}
}

