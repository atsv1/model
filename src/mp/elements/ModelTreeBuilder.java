package mp.elements;

import org.xml.sax.SAXException;

import org.xml.sax.SAXException;
import org.w3c.dom.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;
import java.net.URL;
import java.net.URLConnection;

import mp.parser.ModelExecutionContext;
import mp.parser.ScriptException;
import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;

/** Класс предназначен для построения дерева моделей.
 *
 * Date: 10.04.2008
 */

public class ModelTreeBuilder {
  private ModelElementAbstractFactory FElementFactory = null;
  private Model FRootModel = null;
  private String FPath = null;
  String FFileSeparator = null;
  private static final String ModelListNodeName = "ModelList";
  private static final String SubModelNodeName = "SubModel";
  private static final String ParallelModelNodeName = "ParallelModel";
  private Vector<Model> modelList = new Vector();

  public void SetElementFactory( ModelElementAbstractFactory aElementFactory ){
    FElementFactory = aElementFactory;
  }

  private void ExtractPath( String aPathWithFileName ){
    if ( aPathWithFileName == null ){
      return;
    }
    Properties pr = System.getProperties();
    FFileSeparator = pr.getProperty( "file.separator" );
    int lastIndex = aPathWithFileName.lastIndexOf( FFileSeparator );
    FPath = aPathWithFileName.substring( 0, lastIndex );
    //System.out.println( "FPath = " + FPath );
  }

  private Node GetModelListNode( Node aRootNode ){
    NodeList childNodes = aRootNode.getChildNodes();
    int i = 0;
    Node childNode;
    while ( i < childNodes.getLength() ){
      childNode = childNodes.item(i);
      if ( childNode.getNodeType() == Node.ELEMENT_NODE
      		 &&  ( ModelListNodeName.equalsIgnoreCase( childNode.getNodeName() ) || ParallelModelNodeName.equalsIgnoreCase(childNode.getNodeName() )) ){
        return childNode;
      }
      i++;
    }
    return null;
  }

  private void ReadSubModels( ModelElementDataSource aRootDataSource, Model parentModel) throws ModelException, IOException, SAXException {
    if ( aRootDataSource == null ){
      return;
    }
    Node rootNode = null;
    if ( aRootDataSource instanceof ModelAttributeReader ) {
    	rootNode = ((ModelAttributeReader) aRootDataSource).GetNode();
    }
    Node modelListNode = GetModelListNode( rootNode );
    if ( modelListNode == null ){
      return;
    }

    NodeList childNodes = modelListNode.getChildNodes();
    int i = 0;
    Node childNode;
    String modelFileName = null;
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    Model subModel;
    String nodeName;
    while ( i < childNodes.getLength() ){
      childNode = childNodes.item(i);
      nodeName = childNode.getNodeName();
      if ( childNode.getNodeType() == Node.ELEMENT_NODE &&  ( SubModelNodeName.equalsIgnoreCase( nodeName ) || ParallelModelNodeName.equalsIgnoreCase(nodeName)) ){
        attrReader.SetNode( childNode );
        modelFileName = attrReader.GetSubModelFileName();
        ModelXMLReader modelReader = new ModelXMLReader( FElementFactory );
        modelReader.ReadModel( FPath + FFileSeparator + modelFileName );
        subModel = (Model)modelReader.GetRootElement();
        ReadSubModels( subModel.GetDataSource(), subModel );
        modelList.add(subModel);
        try {
	        ModelExecutionContext.AddModelExecutionManager(subModel);
        } catch (ScriptException e) {
        	throw new ModelException( e.getMessage() );
        }
        if (  SubModelNodeName.equalsIgnoreCase( nodeName ) ) {
        	parentModel.addSubModel(subModel);
        }
        if ( ParallelModelNodeName.equalsIgnoreCase(nodeName) ) {
        	parentModel.AddParallelModel(subModel);
        }
      }
      i++;
    }//while
  }

  public void ReadModelTree( String aRootModelFileName ) throws ModelException, IOException, SAXException {
    if ( FElementFactory == null ){
      ModelException e = new ModelException("Ошибка при чтении модели: отсутствует ModelElementFactory ");
      throw e;
    }
    if ( aRootModelFileName == null || "".equalsIgnoreCase( aRootModelFileName ) ){
      ModelException e = new  ModelException("Ошибка при чтении модели: отсутствует название файла с моделью");
      throw e;
    }
    ModelXMLReader modelReader = null;
    modelReader = new ModelXMLReader( FElementFactory );
    modelReader.ReadModel( aRootModelFileName );
    FRootModel = (Model) modelReader.GetRootElement();
    ExtractPath( aRootModelFileName );
    try {
			ModelExecutionContext.AddModelExecutionManager(FRootModel);
		} catch (ScriptException e) {
			throw new ModelException( e.getMessage() );
		}
    
    ReadSubModels( FRootModel.GetDataSource(), FRootModel );
    Model subModel;
    int i = 0;
    while (i < modelList.size()) {
    	subModel = modelList.get(i);
    	subModel.ApplyNodeInformation();
    	i++;
    }
    FRootModel.ApplyNodeInformation();
  }

  public void ReadModelTree( URL aRootModelURL ) throws ModelException, IOException, SAXException {
    if ( FElementFactory == null ){
      ModelException e = new ModelException("Ошибка при чтении модели: отсутствует ModelElementFactory ");
      throw e;
    }
    if ( aRootModelURL == null ){
      ModelException e = new  ModelException("Ошибка при чтении модели: отсутствует название файла с моделью");
      throw e;
    }
    ModelXMLReader modelReader = null;
    modelReader = new ModelXMLReader( FElementFactory );
    URLConnection connection = aRootModelURL.openConnection();
    System.out.println( "length = " + connection.getContentLength() );
    InputStream content = (InputStream) connection.getContent();
    modelReader.ReadModel( content );
    FRootModel = (Model) modelReader.GetRootElement();
    Model subModel;
    int i = 0;
    while (i < modelList.size()) {
    	subModel = modelList.get(i);
    	subModel.ApplyNodeInformation();
    	i++;
    }
    FRootModel.ApplyNodeInformation();

  }

  public Model GetRootModel(){
    return FRootModel;
  }


}
