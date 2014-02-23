package Parte2;

import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
 
public class EjemploCompletionService {
 
    // Defina un método estático sin argumentos llamado crearListaTareas
    // que cree una lista de, por ejemplo, diez TareasLargas.
	public static ArrayList<TareaLarga> crearListaTareas(){
		ArrayList<TareaLarga>listaTareas = new ArrayList<TareaLarga>();
		for (int i=0; i<10; i++){
			listaTareas.add(new TareaLarga());
		}
		return listaTareas;
	}

    // El main:
	 public static void main (String[] args){
		 
      // Utilice un método factoría para crear un ThreadPool (implementación
      // de la interfaz ExecutorService) con el numero de hilos requerido:
      // por ejemplo, diez. 
	  ExecutorService pool = Executors.newFixedThreadPool(10);
 
      // Cree un nuevo CompletionService para tareas que devuelvan un String,
      // pasándole el ExecutorService creado en la instrucción anterior
	  CompletionService<String> completionService = new ExecutorCompletionService<String>(pool);
  
      // Cree una lista de TareaLarga con el método crearListaTareas
	  ArrayList<TareaLarga> listaTareas = crearListaTareas();

	  // COMENZAR BUCLE (DE TIPO FOR-EACH). Para cada tarea de la lista
	  for (TareaLarga tarea : listaTareas){
		  // Presente esta tarea al CompletionService para su ejecución
		  completionService.submit(tarea);
	  } 
      // TERMINAR BUCLE.

	  try {
	      // COMENZAR BUCLE. Para un número de veces = el tamaño de la lista de tareas
	      for (int i=0; i<listaTareas.size(); i++){
	    	// Pida una tarea completada al CompletionService
	          // (espere si no ha terminado ninguna tarea todavía)
	    	  Future<String> resultTarea = completionService.take();	// take() espera automáticamente a que se complete alguna tarea
	          // Imprima el resultado de la tarea en la salida estándar.
	    	  System.out.println(resultTarea.get());
	      }
	  	  // TERMINAR BUCLE.
	      
		  } catch (InterruptedException e) {
	          Thread.currentThread().interrupt();
		  } catch (ExecutionException e) {
	          Thread.currentThread().interrupt(); 
		  }
	      // Cierre el ExecutionService
		  finally {
	          if (pool != null) {
	        	  pool.shutdownNow();
	          }
		  }
	 }
}