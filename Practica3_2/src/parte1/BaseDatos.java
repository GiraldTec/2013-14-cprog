package parte1;
public class BaseDatos {

	private String contenido = "estado inicial";
	
	public void escribir() {
		contenido = Thread.currentThread().getName() + " ha estado aqui";
	}

	public String leer() { return contenido; }

}
