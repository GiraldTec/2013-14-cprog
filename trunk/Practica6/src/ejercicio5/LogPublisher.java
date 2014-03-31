package ejercicio5;

import ejercicio4.Message;
import ejercicio4.Publisher;


public class LogPublisher extends Publisher {
	
	private String direccion;
	
	public LogPublisher(String d){
		super();
		direccion = d;
	}

	public void publish(Message m) {
		// TODO abrir fichero y escribir
		
		System.out.println(m.getData());
	}

}