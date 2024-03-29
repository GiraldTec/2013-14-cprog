package parte_1;

/**************************************************************************/
/***                                                                    ***/
/***                             Banco.java                             ***/
/***                                                                    ***/
/***               Una simulacion simplificada de un banco              ***/
/***                                                                    ***/
/***             Benjamin Pierce, University of Pennsylvania            ***/
/***            Simon Pickin, Universidad Carlos III de Madrid          ***/
/***                                                                    ***/
/**************************************************************************/

interface Cuenta {
  // el parametro de tipo Cajero permite conocer el Cajero llamante
  public int numCuenta(Cajero caj);
  public void actualizarSaldo(Cajero caj, int cantidad);
  public int saldo(Cajero caj);
}

class Cajero {
   public void realizar_deposito(Cuenta cta, int cantidad, Cliente depositador) {
     synchronized (cta){
	   cta.actualizarSaldo(this, cta.saldo(this) + cantidad);
	   cta.notifyAll();
     }
   }

   public void realizar_reintegro(Cuenta cta, int cantidad, Cliente reintegrador) {
	  synchronized (cta){
		  while (cantidad > cta.saldo(this)) { 
	    	  try {
	    		  System.err.println("Cliente: " + reintegrador + " solicita reintegro en cajero: " +
	        			  this.id + ", en cuenta: "+ cta.numCuenta(this)+ "; hay ingresado: "
	        			  + cta.saldo(this) + ", imposible extraer importe: " + cantidad +
	        			  " [El cliente: "+ reintegrador + " ENTRA en espera]");
	    		  
	              cta.wait();
	              
	              if (cantidad <= cta.saldo(this))
	            	  System.err.println("Cajero: " + this.id + ", cuenta: " + cta.numCuenta(this)
	            	//+ "; hay ingresado: " + cta.saldo(this) + ", extrae importe: " + cantidad 
	            	+ " [El cliente: "+ reintegrador + " SALE de la espera]");
	          } catch (InterruptedException e) {}
	      }
	  	  
	      cta.actualizarSaldo(this, cta.saldo(this) - cantidad);
   
	  }
   }

  int id;

  public String toString() {
    return String.valueOf(id);
  }

  static int proximaNumeroDeCajero = 0;
  Cajero() {
    id = proximaNumeroDeCajero++;
  }
}

/*----------------------------------------------------------------------*
 *   Para el primer ejercicio, no hay que modificar esta parte...       *
 *----------------------------------------------------------------------*/

class CuentaImpl implements Cuenta {
  static int num_de_cuenta = 0;
  private int numCuen;
  private int saldo = 0;

  private static int total = 0;
  public static int totalSaldo() {
    return total;
  }

  private void delay() { 
    for(int i=0; i<1000; i++) { Thread.currentThread().yield(); }; 
  }

  CuentaImpl() { numCuen = num_de_cuenta++; }

  public int numCuenta(Cajero caj) { return numCuen; }

  public void actualizarSaldo(Cajero caj, int nuevo_saldo) { 
    delay();
    synchronized (this) {
      total = total + (nuevo_saldo - saldo);
      saldo=nuevo_saldo; 
      System.out.println("Cuenta #" + numCuen + ": actualizado a " + nuevo_saldo +
                         " Euros por cajero " + caj);
      if (saldo < 0) {
        System.out.println("ERROR: saldo negativo");
        System.exit(1); }
    }
  }

  public int saldo(Cajero caj) { 
    delay();
    synchronized (this) {
      //System.out.println("Cuenta #" + numCuen + ": saldo es " + saldo +
       //                  " Euros (pedido por Cajero " + caj + ")");
      return saldo;
    }
  }

  public String toString() {
    return String.valueOf(numCuen);
  }
}

class Cliente extends Thread {
  int id;
  final int delay;

  static int ids=0;
  Cliente(int retardo) {
    id = ids++;
    delay = retardo;
  }

  public String toString() {
    return String.valueOf(id);
  }

  protected void hacer_deposito(int caj, int cue, int cantidad) {
    System.out.println("Cliente " + id + ": deposito de " + cantidad +
                       " Euros en cuenta #" + cue + " con cajero " + caj);
    Banco1.cajeros[caj].realizar_deposito(Banco1.cuentas[cue],cantidad,this);
    Banco1.liquidez += cantidad;
  }

  protected void hacer_reintegro(int caj, int cue, int cantidad) {
    System.out.println("Cliente " + id + ": reintegro de " + cantidad +
                       " Euros en cuenta #" + cue + " con cajero " + caj);
    Banco1.cajeros[caj].realizar_reintegro(Banco1.cuentas[cue],cantidad,this);
    Banco1.liquidez -= cantidad;
  }
}

class Cliente0 extends Cliente {
  Cliente0(int i){super(i);}
  public void run() {

    hacer_deposito(0,0,20);

    try {sleep(delay);} 
    catch (InterruptedException e) {System.err.println("Interrumpido mientras dormido");}

    hacer_reintegro(0,0,100);

    try {sleep(delay);} 
    catch (InterruptedException e) {System.err.println("Interrumpido mientras dormido");}

    hacer_deposito(0,1,100);
  }
}

class Cliente1 extends Cliente {
  Cliente1(int i){super(i);}
  public void run() {

    hacer_deposito(0,0,50);
 
    try {sleep(delay);} 
    catch (InterruptedException e) {System.err.println("Interrumpido mientras dormido");}

    hacer_deposito(0,0,60);

    try {sleep(delay);} 
    catch (InterruptedException e) {System.err.println("Interrumpido mientras dormido");}

    hacer_reintegro(0,1,22);

    try {sleep(delay);} 
    catch (InterruptedException e) {System.err.println("Interrumpido mientras dormido");}

    hacer_deposito(1,1,200);
  }
}

class Cliente2 extends Cliente {
  Cliente2(int i){super(i);}
  public void run() {

    hacer_deposito(1,1,10);

    try {sleep(delay);} 
    catch (InterruptedException e) {System.err.println("Interrumpido mientras dormido");}

    hacer_reintegro(1,1,50);

    try {sleep(delay);} 
    catch (InterruptedException e) {System.err.println("Interrumpido mientras dormido");}

    hacer_deposito(1,0,20);
  }
}

class Cliente3 extends Cliente {
  Cliente3(int i){super(i);}
  public void run() {

    hacer_deposito(1,1,10);

    try {sleep(delay);} 
    catch (InterruptedException e) {System.err.println("Interrumpido mientras dormido");}

    hacer_reintegro(1,1,100);
  }
}

class Banco1 {
  public final static int num_cue=4;
  public final static int num_caj=2;
  public static int liquidez=0;
  
  static Cajero[] cajeros = new Cajero[num_caj];
  public static Cuenta[] cuentas = new Cuenta[num_cue];

  public static void main(String[] args) {
    for(int i=0;i<num_cue;i++) cuentas[i] = new CuentaImpl();
    for(int i=0;i<num_caj;i++) cajeros[i] = new Cajero();

    Cliente c0 = new Cliente0(0);
    Cliente c1 = new Cliente1(0);
    Cliente c2 = new Cliente2(0);
    Cliente c3 = new Cliente3(0);

    c0.start();
    c1.start();
    c2.start();
    c3.start();

    try {
      c0.join(1000);
      c1.join(1000);
      c2.join(1000); 
      c3.join(1000); }
    catch (InterruptedException e) {
      System.out.println("Interumpido mientras esperaba que terminen los clientes");
      System.exit(1);
    }

    if (c0.isAlive()) {
      System.out.println("Cliente 0 no ha terminado!");
    }      
    if (c1.isAlive()) {
      System.out.println("Cliente 1 no ha terminado!");
     }      
    if (c2.isAlive()) {
      System.out.println("Cliente 2 no ha terminado!");
     }      
    if (c3.isAlive()) {
      System.out.println("Cliente 3 no ha terminado!");
    }      

    if (liquidez != CuentaImpl.totalSaldo()) {
      System.out.println( "ERROR: discrepancia entre la liquidez calculada "
                          + "a partir de los depositos y reintegros ("
                          + liquidez 
                          + ") y el total de dinero en las cuentas ("
                          + CuentaImpl.totalSaldo()
				  + ")" );
      System.exit(1);
    }

     if ( c0.isAlive() || c1.isAlive() || c2.isAlive() || c3.isAlive() ) {
	System.exit(1);
    }

   System.out.println("Terminado con exito");
  }
}

