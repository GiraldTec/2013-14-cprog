// Estas dos clases podran estar en ficheros distintos.

import java.util.*;

class Filosofo extends Thread {

   // Generador de numeros aleatorios
   static Random rnd = new Random();

   private int id = 0;
   private MonitorFilosofos monitor = null;

   public Filosofo(int id, MonitorFilosofos monitor) {
      this.id = id;
      this.monitor = monitor;
      System.out.println("Filosofo " + this.id + " esta vivo");
   }

   private void think() {
      System.out.println("Filosofo " + this.id + " esta pensando");
	try {sleep(rnd.nextInt(5000));}
	catch (InterruptedException e) {System.err.println("Interrupted while sleeping");}
   }

   private void eat() {
      System.out.println("Filosofo " + this.id + " esta comiendo");
	try {sleep(rnd.nextInt(2000));}
	catch (InterruptedException e) {System.err.println("Interrupted while sleeping");}
   }

   public void run() {
      while (true) {
         think();
         System.out.println("Filosofo " + this.id + " quiere comer");
         monitor.takeForks(id);
         eat();
         monitor.putForks(id);
      }
   }

}

class ComidaFilosofos {

   public static void main(String[] args) {

   // numero de filosofos por defecto
   int numFilosofos = 5;

   // parsing de los argumentos de la linea de comandos
   if (args.length > 1)
	System.out.println("Uso: ComidaFilosofos <numero_de_filosofos>");
   else if (args.length == 1)
	try{
	   numFilosofos = Integer.parseInt(args[0]);
	} catch(NumberFormatException e){ // El parametro no es un entero
	   System.out.println("Uso: ComidaFilosofos <numero_de_filosofos>");
	   System.exit(0);
	}
   
   // crear el objeto MonitorPhils
   MonitorFilosofos monitor = new MonitorFilosofos(numFilosofos);
	
   // crear los filosofos y arrancar sus hilos
   for (int i = 0; i < numFilosofos; i++)
      new Filosofo(i, monitor).start();
   System.out.println("Todos los hilos han sido arrancados");

   }

}
