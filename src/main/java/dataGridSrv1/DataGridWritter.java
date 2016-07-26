package dataGridSrv1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Clase que pone y obtiene datos de la caché JBoss Datagrid 6.5.
 * 
 * @author pedro.alonso.garcia
 *
 */
@RestController
@EnableAutoConfiguration
public class DataGridWritter {

	// Constantes de la clase
	private static final String JDG_HOST = "jdg.host";
	private static final String HOTROD_PORT = "jdg.hotrod.port";
	private static final String PROPERTIES_FILE = "jdg.properties";
	private static final String PRENDAS_KEY = "prendas";
	public static final String ID_TRAZA = "###===>>>";

	// Variables globales
	private RemoteCacheManager cacheManager;
	private RemoteCache<String, Object> cache = null;
	private final static DatagridListener listener = new DatagridListener();

	@RequestMapping("/")
	String homeMethod() {
		
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "<br><h1><strong>dataGridSrv1</strong></h1></br>"
				+ "<br>recibiendo eventos del DataGrid ...</h1></br>";
	}



	private void init() throws Exception {
		if (cache==null){
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.addServer().host(jdgProperty(JDG_HOST)).port(Integer.parseInt(jdgProperty(HOTROD_PORT)));
				System.out.println(
						"###===>>> Conectando a host : " + jdgProperty(JDG_HOST) + ", puerto: " + jdgProperty(HOTROD_PORT));
				cacheManager = new RemoteCacheManager(builder.build());
				cache = cacheManager.getCache("prendas");
				
				//Añadimos el listener
				
				cache.addClientListener(listener);
				
				// Inicializo la caché con el mapa de prendas
				if (!cache.containsKey(PRENDAS_KEY)) {
					Map<String, Prenda> prendasMap = new HashMap<String, Prenda>();
					cache.put(PRENDAS_KEY, prendasMap);
				}
			}
			catch (Exception e) {
				System.out.println("Init Caught: " + e);
				e.printStackTrace();
				throw e;
	
			}
		}
	}

	
	/**
	 * Método que obtiene una propiedad del fichero
	 * src\main\resources\jdg.properties
	 * 
	 * @param name
	 * @return
	 * 
	 */
	public static String jdgProperty(String name) {
		Properties props = new Properties();
		try {
			props.load(DataGridWritter.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		return props.getProperty(name);
	}

	/*******************************************
	 * MAIN *
	 * 
	 * @param args
	 *            *
	 * @throws Exception
	 *             *
	 ******************************************/
	public static void main(String[] args) throws Exception {
		SpringApplication.run(DataGridWritter.class, args);
	}

}
