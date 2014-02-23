package Parte2;

import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
 
public class EjemploCompletionService {
 
    // Defina un m�todo est�tico sin argumentos llamado crearListaTareas
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
		 
      // Utilice un m�todo factor�a para crear un ThreadPool (implementaci�n
      // de la interfaz ExecutorService) con el numero de hilos requerido:
      // por ejemplo, diez. 
	  ExecutorService pool = Executors.newFixedThreadPool(10);
 
      // Cree un nuevo CompletionService para tareas que devuelvan un String,
      // pas�ndole el ExecutorService creado en la instrucci�n anterior
	  CompletionService<String> completionService = new ExecutorCompletionService<String>(pool);
  
      // Cree una lista de TareaLarga con el m�todo crearListaTareas
	  ArrayList<TareaLarga> listaTareas = crearListaTareas();

	  // COMENZAR BUCLE (DE TIPO FOR-EACH). Para cada tarea de la lista
	  for (TareaLarga tarea : listaTareas){
		  // Presente esta tarea al CompletionService para su ejecuci�n
		  completionService.submit(tarea);
	  } 
      // TERMINAR BUCLE.

	  try {
	      // COMENZAR BUCLE. Para un n�mero de veces = el tama�o de la lista de tareas
	      for (int i=0; i<listaTareas.size(); i++){
	    	// Pida una tarea completada al CompletionService
	          // (espere si no ha terminado ninguna tarea todav�a)
	    	  Future<String> resultTarea = completionService.take();	// take() espera autom�ticamente a que se complete alguna tarea
	          // Imprima el resultado de la tarea en la salida est�ndar.
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