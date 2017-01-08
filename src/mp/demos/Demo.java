package mp.demos;

import mp.elements.*;
import mp.gui.*;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;

/**
 * User: atsv
 * Date: 04.10.2006
 */
public class Demo extends JApplet{

   public void init() {
     String modelURL = this.getParameter("modelurl");
     if ( modelURL == null ) {
       ReadModel( getContentPane(), this.getParameter("modelfile"), getParameter("formfile") );
     } else {
       ReadModelFromURL( getContentPane(), this.getParameter("modelurl"), getParameter("formurl")  );
     }
  }

  private static void ReadModelFromURL( Container aContentPane, String aModelURL, String aFormURL ){
    ModelXMLReader formReader = null;
    ModelXMLReader modelReader = null;
    Model newModel = null;
    URL modelURL = null;
    URL formURL = null;
    URLConnection modelConnection;
    URLConnection formConnection;
    try{
      modelURL = new URL( aModelURL );
      //modelConnection = modelURL.openConnection();

      ModelTreeBuilder modelBuilder = new ModelTreeBuilder();
      modelBuilder.SetElementFactory( new ModelElementFactory() );
      modelBuilder.ReadModelTree( modelURL );
      newModel = modelBuilder.GetRootModel();

      formURL = new URL( aFormURL );
      formConnection = formURL.openConnection();

      ModelDirectConnector connector = new ModelDirectConnector( newModel );
      ModelGUIElementFactory elementGUIFactory = new ModelGUIElementFactory();
      elementGUIFactory.SetConnector( connector );
      formReader = new ModelXMLReader( elementGUIFactory );
      formReader.ReadModel((InputStream) formConnection.getContent());
    } catch (Exception e) {
      JLabel label = new JLabel(e.getMessage());
      aContentPane.add( label );
      //System.out.println( e.getMessage() );
      e.printStackTrace();
      return;
    }
    StandartForm form = (StandartForm) formReader.GetRootElement();
    aContentPane.add( form.GetComponent() );

  }

  private static void ReadModel( Container aContentPane, String modelFileName, String formFileName){
    /*String modelFileName = this.getParameter("modelfile");
    String formFileName = this.getParameter("formfile");*/
    if ( modelFileName == null ){
      JLabel label = new JLabel("Отсутствует параметр modelfile");
      aContentPane.add( label );
    }
     if ( formFileName == null ){
      JLabel label = new JLabel("Отсутствует параметр formfile");
      aContentPane.add( label );
    }

    ModelXMLReader formReader = null;

    Model newModel = null;
    try{
      ModelTreeBuilder modelBuilder = new ModelTreeBuilder();
      modelBuilder.SetElementFactory( new ModelElementFactory() );
      modelBuilder.ReadModelTree( modelFileName );
      newModel = modelBuilder.GetRootModel();

      ModelDirectConnector connector = new ModelDirectConnector( newModel );
      ModelGUIElementFactory elementGUIFactory = new ModelGUIElementFactory();
      elementGUIFactory.SetConnector( connector );
      formReader = new ModelXMLReader( elementGUIFactory );
      formReader.ReadModel( formFileName );
    } catch (Exception e) {
      JLabel label = new JLabel(e.getMessage());
      aContentPane.add( label );
      //System.out.println( e.getMessage() );
      e.printStackTrace();
      return;
    }
    StandartForm form = (StandartForm) formReader.GetRootElement();
    aContentPane.add( form.GetComponent() );
  }


  private static void CreateAndShowGUI( String modelFileName, String formFileName ) {
    JFrame frame = new JFrame("Modelling Demo");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ReadModel( frame.getContentPane(),modelFileName, formFileName );
    frame.setPreferredSize( new Dimension(600,600) );
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(final String[] args){
     SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              System.out.println("starting model");
              //Properties props = System.getProperties();
              //System.out.println( props.toString() );
              int i = 18;
              System.out.println("type programName modelFileName formFileName");
              if ( args == null || args.length < 2 ){
                if ( args != null ){
                  i = args.length;
                }
                System.out.println("Can't find model file names argscount=" + Integer.toString( i ) );
                Runtime.getRuntime().exit(1);
              }
              CreateAndShowGUI( args[0], args[1] ) ;
            }
        });

  }


}
