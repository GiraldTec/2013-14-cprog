package ejercicio4;

public class ScreenPublisher extends Publisher {

	public void publish(Message m) {
		System.out.println(m.getData());
	}

}
