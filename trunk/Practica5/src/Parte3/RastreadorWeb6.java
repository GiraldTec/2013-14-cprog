package Parte3;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Parte3.BuscadorEnlaces;
import java.util.HashSet;

public class RastreadorWeb6 implements ProcesadorEnlaces {

 // TO DO: ver si el rendimiento sería mejor con algo de java.util.concurrent tal como:
 // Set<String> set = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private final Collection<String> enlacesVisitados = Collections.synchronizedSet(new HashSet<String>());
 // Para probar el tiempo que tarda en visitar enlaces sin tomar en cuenta si ya los ha visitado,
 // cambie el "synchronizedSet" por un "synchronizedList" y cambie el método "visitado" para que
 // devuelva siempre falso.
 //    private final Collection<String> enlacesVisitados = Collections.synchronizedList(new ArrayList<>());
 // TO DO: ver si el rendimiento sería mejor con algo de java.util.concurrent tal como ConcurrentLinkedList 
    private String url;
    private ExecutorService execService;

    public RastreadorWeb6(String inicioURL, int maxHilos) {
        this.url = inicioURL;
        execService = Executors.newFixedThreadPool(maxHilos);
    }

    @Override
    public void encolarEnlace(String link) throws Exception {
		execService.execute(new BuscadorEnlaces(link, this));
    }

    @Override
    public int cantidad() {
        return enlacesVisitados.size();
    }

    @Override
    public void anadirVisitado(String s) {
        enlacesVisitados.add(s);
    }

    @Override
    public boolean visitado(String s) {
        return enlacesVisitados.contains(s);
    }

    private void empezarRastreo() throws Exception {
		execService.execute(new BuscadorEnlaces(this.url, this));
    }

    /**
     * @param args los argumentos de la línea de comandos (debería pasar la URL)
     */
    public static void main(String[] args) throws Exception {
        new RastreadorWeb6("http://www.codeofhonor.com/blog/", 16).empezarRastreo();
    }
}
