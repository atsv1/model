package mp.elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestUtils {
	private static Properties  properties = null;

	public static String GetPath(){
    if ( properties == null ){
    	InputStream config = TestUtils.class.getResourceAsStream("/model.properties");
    	properties = new Properties();
    	try {
	      properties.load(config);
      } catch (IOException e) {
	      e.printStackTrace();
      }
    }
		return properties.getProperty("testFilesPath");
	}

	public static void main(String[] args){
    System.out.println( GetPath() );
	}

}
