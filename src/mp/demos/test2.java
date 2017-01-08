package mp.demos;

import mp.elements.*;
import mp.gui.*;

import javax.swing.*;

/**
 * User: atsv
 * Date: 30.09.2006
 */
public class test2 extends JApplet{

  private static String FPathToXMLFiles = "E:\\Work\\Model\\ModelFiles\\";

  public void init() {
    ModelXMLReader formReader = null;
    ModelXMLReader modelReader = null;
    Model newModel = null;
    try{
      ModelElementFactory elementFactory = new ModelElementFactory();
      modelReader = new ModelXMLReader( elementFactory );
      modelReader.ReadModel( FPathToXMLFiles + "form2Model.xml" );
      newModel = (Model) modelReader.GetRootElement();
      newModel.ApplyNodeInformation();
      
      ModelDirectConnector connector = new ModelDirectConnector( newModel );
      ModelGUIElementFactory elementGUIFactory = new ModelGUIElementFactory();
      elementGUIFactory.SetConnector( connector );
      formReader = new ModelXMLReader( elementGUIFactory );
      formReader.ReadModel( FPathToXMLFiles + "form2.xml" );
    } catch (Exception e) {
      System.out.println( e.getMessage() );
    };
    StandartForm form = (StandartForm) formReader.GetRootElement();
    getContentPane().add( form.GetComponent() );

  }

}
