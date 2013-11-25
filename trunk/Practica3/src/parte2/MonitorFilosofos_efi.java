package parte2;

class DeNotificacion {}

class MonitorFilosofos_efi {

   private int numFils = 0;
   private int[] estado = null;
   private static final int
      THINKING = 0, HUNGRY = 1, EATING = 2, STARVING = 3;
   private DeNotificacion[] notif;

   public MonitorFilosofos_efi(int numFils) {
      this.numFils = numFils;
      estado = new int[numFils];
      for (int i = 0; i < numFils; i++) estado[i] = THINKING;
      this.notif = new DeNotificacion[numFils];
      for (int i = 0; i < numFils; i++) notif[i] = new DeNotificacion();
   }

   private final int izquierda(int i) {
	return (numFils + i - 1) % numFils;
   }

   private final int derecha(int i) {
	return (i + 1) % numFils;
   }

   /* 
      - Un filósofo entra en STARVING si, cuando uno de sus vecinos suelta el tenedor,
    	tiene hambre, pero no puede comer porque el otro tenedor está en uso.
      - Un filósofo hambriento no puede comer (ponerse en el estado EATING) si tiene un vecino famélico (STARVING).
      - Tampoco puede ponerse famélico si uno de sus vecinos ya lo está.
   */
   
	private void prueba(int k, boolean firstTime) {
		if (firstTime){
			if ((estado[izquierda(k)] != EATING
		           && estado[k] == HUNGRY
		           && estado[derecha(k)] != EATING ) 
		           && !(estado[izquierda(k)] == STARVING || estado[derecha(k)] == STARVING))
		         estado[k] = EATING;
		}else{
			if (((estado[izquierda(k)] == EATING || estado[derecha(k)] == EATING)
			           && estado[k] == HUNGRY ) 
				&& !(estado[izquierda(k)] == STARVING || estado[derecha(k)] == STARVING))
			{
				estado[k] = STARVING;
				System.out.println("Filosofo " + k + " esta famelico");
			}
			
			if ((estado[izquierda(k)] != EATING
	           && (estado[k] == STARVING || estado[k] == HUNGRY)
	           && estado[derecha(k)] != EATING ) 
	           && !(estado[izquierda(k)] == STARVING || estado[derecha(k)] == STARVING))
	         estado[k] = EATING;
		}
	}

   public synchronized void takeForks(int i) {
      estado[i] = HUNGRY;
      prueba(i, true);
      if (estado[i] != EATING)
    	  synchronized (notif[i]){
	         try {
	        	 notif[i].wait();	
	         } catch (InterruptedException e) {}
    	  }
   }

   public synchronized void putForks(int i) {
      estado[i] = THINKING;
      prueba(izquierda(i), false);
      prueba(derecha(i), false);
      synchronized(notif[izquierda(i)]){
	      if(estado[izquierda(i)]==EATING) {notif[izquierda(i)].notify();}
      }
	  synchronized(notif[derecha(i)]){
	      if(estado[derecha(i)]==EATING) {notif[derecha(i)].notify();}
	  }
      //notifyAll();
   }
}
