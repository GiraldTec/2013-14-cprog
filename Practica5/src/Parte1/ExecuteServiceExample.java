package Parte1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecuteServiceExample
{
  final static int TASK_COUNT = 1000;
  final static int MAX_THREAD_COUNT = 50;
  final static int MAX_COUNT = 100000;
  
  public static void main (String[] args)
  {
    
    
    for (int numThreads = 1; numThreads <= MAX_THREAD_COUNT; numThreads++) {
      long startTime = System.nanoTime();
      ExecutorService pool = Executors.newFixedThreadPool(numThreads);
      
      // Cuanto tiempo tardas en ejecutar 1000 tareas con numThread hilos?
      for(int i=1; i <= TASK_COUNT; i++){
        pool.submit(new TareaSimple(MAX_COUNT));
      }
      
      // close the pool
      try {
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        pool.shutdownNow();
        e.printStackTrace();
      }
      
      long now = (System.nanoTime() - startTime);
      System.out.println("Hilos: " + numThreads + "; \t tard—: " + now);
    }
    
    
  }
  
  

}
