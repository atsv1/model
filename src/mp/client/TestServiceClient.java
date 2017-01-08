package mp.client;

import mp.manager.ServiceServerTest;
import junit.framework.TestCase;

public class TestServiceClient extends TestCase {

	public TestServiceClient(String testName) {
		super(testName);
	}

	public static ModelManager ConnectToService(){
		ModelManagerService mgrSrv = new ModelManagerService();
		ModelManager mgr = mgrSrv.getModelManagerPort();
		return mgr;
	}

	/**
	 * Запускаем сервис и просто подключаемся к нему
	 */
	public void testConnectToService(){
		boolean f = false;
		try {
	    ServiceServerTest.StartService();
	    f = true;
    } catch (Exception e) {
	    e.printStackTrace();
    }
		assertTrue(f);
		ModelManager mgr = ConnectToService();
		assertTrue(mgr != null);
		ServiceServerTest.StopService();
	}

}
