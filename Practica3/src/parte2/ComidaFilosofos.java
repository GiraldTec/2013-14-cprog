package parte2;
// Estas dos clases podran estar en ficheros distintos.

import java.util.*;

interface Monitor{
	public void takeForks(int i);
	public void putForks(int i);
}

class Filosofo extends Thread {

   // Generador de numeros aleatorios
   static Random rnd = new Random();

   private int id = 0;
   private Monitor monitor = null;

   public Filosofo(int id, Monitor monitor) {
      this.id = id;
      this.monitor = monitor;
      System.out.println("Filosofo " + this.id + " esta vivo");
   }

   private void think() {
      System.out.println("Filosofo " + this.id + " esta pensando");
	try {sleep(rnd.nextInt(1500));}
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
   if (args.length > 2)
	System.out.println("Uso: ComidaFilosofos <?numero_de_filosofos> <tipo_de_filosofos>");
   else 
	   if (args.length == 2){
		   try{
			   numFilosofos = Integer.parseInt(args[0]);
		   } catch(NumberFormatException e){ // El parametro no es un entero
			   System.out.println("Uso: ComidaFilosofos <?numero_de_filosofos> <tipo_de_filosofos>");
			   System.exit(0);
		   }
		   if((args[1].equals("equ"))||(args[1].equals("efi"))){
			   Monitor monitor;
			   // crear el objeto MonitorPhils
			   if (args[1].equals("equ")) 
				   monitor = new MonitorFilosofos_equ(numFilosofos);
			   else 
				   monitor = new MonitorFilosofos_efi(numFilosofos);
			   // crear los filosofos y arrancar sus hilos
			   for (int i = 0; i < numFilosofos; i++)
				   new Filosofo(i, monitor).start();
			   System.out.println("Todos los hilos han sido arrancados");
			   
			   
		   }else{
			   System.out.println("Uso: ComidaFilosofos <?numero_de_filosofos> <tipo_de_filosofos>");
			   System.exit(0);
		   }
	   }
   		
	   else	if (args.length == 1){
   			if((args[0].equals("equ"))||(args[0].equals("efi"))){
				   Monitor monitor;
				   // crear el objeto MonitorPhils
				   if (args[0]=="equ") 
					   monitor = new MonitorFilosofos_equ(numFilosofos);
				   else 
					   monitor = new MonitorFilosofos_efi(numFilosofos);
				   // crear los filosofos y arrancar sus hilos
				   for (int i = 0; i < numFilosofos; i++){
				      new Filosofo(i, monitor).start();
				   System.out.println("Todos los hilos han sido arrancados");
				   }
				   
			   }else{
				   System.out.println("Uso: ComidaFilosofos <?numero_de_filosofos> <tipo_de_filosofos>");
				   System.exit(0);
			   }
   		}else{
   			System.out.println(args[0]+"Uso: ComidaFilosofos <?numero_de_filosofos> <tipo_de_filosofos>");
			System.exit(0);
   		}
   	}
}


