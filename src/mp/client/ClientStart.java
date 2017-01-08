package mp.client;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.swing.JFrame;
import mp.elements.ModelConnector;
import mp.elements.ModelXMLReader;
import mp.gui.ModelGUIElementFactory;
import mp.gui.StandartForm;

public class ClientStart {

	public static void main(String[] args) {
		ModelManagerService mgrSrv = new ModelManagerService();
		ModelManager mgr = mgrSrv.getModelManagerPort();
    String guid = mgr.createModel("Пивная игра");

    byte[] form = mgr.getFormDescrAsByteArray(guid);
    ByteArrayInputStream inputStream = null;

    try {
    	ModelGUIElementFactory elementGUIFactory = new ModelGUIElementFactory();
    	ModelConnector connector = new ModelServiceConnector(mgr, guid);
    	elementGUIFactory.SetConnector( connector );
	    ModelXMLReader formReader = new ModelXMLReader( elementGUIFactory );

	    inputStream = new ByteArrayInputStream( form );
	    formReader.ReadModel( inputStream );

      JFrame frame = new JFrame("Modelling Demo");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      StandartForm standartForm = (StandartForm) formReader.GetRootElement();
      frame.getContentPane().add( standartForm.GetComponent() );

      frame.setPreferredSize( new Dimension(600,600) );
      frame.pack();
      frame.setVisible(true);

    } catch(Exception e) {
    	e.printStackTrace();
    } finally {
    	if (inputStream != null) {
    		try {
	        inputStream.close();
        } catch (IOException e) {
	        e.printStackTrace();
        }
    	}
    }
	}


}
