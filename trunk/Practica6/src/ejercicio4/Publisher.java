package ejercicio4;

import java.util.concurrent.PriorityBlockingQueue;

public abstract class Publisher extends Thread {
	
	private PriorityBlockingQueue<Message> queue;
	
	public Publisher() {
		queue = new PriorityBlockingQueue<Message>();
	}
	
	public String addMessage(Message m){
		queue.add(m);
		return "aniadido";
	}
	
	public abstract void publish(Message m);
	
	public void run(){   //
		while(true){
			if(!queue.isEmpty()){
				Message m=queue.poll();
				while(m!=null){
					publish(m);
					m=queue.poll();
				}
			}
		}
	}
}
