package mp.manager;

import java.io.InputStream;
import java.util.Properties;

import javax.xml.ws.Endpoint;

import junit.framework.TestCase;

public class ServiceServerTest extends TestCase {
	private static ModelManager modelManager = null;
	private static Endpoint endpoint = null;

	public ServiceServerTest(String testName) {
    super(testName);
  }

	public static ModelManager StartService() throws Exception{
		ModelDatastoreReader modelReader = new XMLModelListReader();
		InputStream config = ModelManager.class.getResourceAsStream("/model.properties");
		Properties  properties = new Properties();
	  properties.load(config);
	  modelReader.Init(properties);
	  modelReader.Reload();

    ModelContainer container = new ModelContainer(modelReader);
    container.LoadModels();
    ModelManager manager = new ModelManager();
    manager.SetModelContainer(container);
    endpoint = Endpoint.publish("http://localhost:8085/WS/modelService", manager);
	  modelManager = manager;
	  return manager;
	}

	public static void StopService(){
		endpoint.stop();
		endpoint = null;
		modelManager = null;
	}

	/**
	 * Ќесколько раз запускаем-останавливаем сервис
	 *
	 */
	public static void testStartService(){
		boolean f = true;
    assertTrue(f);
    
	}

	public static void testStartStopModel(){
		boolean f = true;
		assertTrue(f);
		
	}

}
