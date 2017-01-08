package mp.elements;

import org.xml.sax.SAXException;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;

/**
 * User: atsv
 * Date: 21.09.2006
 * Класс предназначен для чтения модели из XML-файла.
 */
public  class ModelXMLReader {
  private DocumentBuilder FBuilder;
  private boolean FValidating = false;
  private Document FDocument = null;
  private ModelForReadInterface FRootElement = null;
  private ModelAttributeReader FAttrReader = null;
  private ModelElementAbstractFactory FElementFactory = null;


  public ModelXMLReader( ModelElementAbstractFactory elementFactory ) throws ModelException{
    DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
    factory.setValidating(FValidating);
    try {
      FBuilder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      ModelException e1 = new ModelException(" При создании объекта-строителя произошла ошибка: " + e.getMessage());
      throw e1;
    }
    if ( elementFactory == null ){
      ModelException e = new ModelException("Не указан класс для создания объектов");
      throw e;
    }
    FElementFactory = elementFactory;
    //FAttrReader = new ModelAttributeReader( null );
    FAttrReader = ServiceLocator.GetAttributeReader();
  }

  private static int GetNewId(){
    return ServiceLocator.GetNextId();
  }

  private void CreateElementInstances(Node aPreviousNode, Node aCurrentNode,
                                    ModelForReadInterface aElementsOwner) throws ModelException {
  //ModelException e;
    if ( FElementFactory.IsLastElement( aElementsOwner) ){
      return;
    }
    FAttrReader.SetNode( aCurrentNode );
    int instancesCount = FAttrReader.GetAttrCount();
    ModelForReadInterface element;
    while (instancesCount > 0){
      element = FElementFactory.GetNewElement( aPreviousNode, aElementsOwner, aCurrentNode, GetNewId() );
      FElementFactory.ExecuteDoSomethingFunction( aPreviousNode, aCurrentNode, aElementsOwner, element);
      WalkOnDocument( aCurrentNode, element );
      instancesCount--;
    }
  }

  /**Метод чтения модели. Этот метод используется в двойной рекурсии. Он вызывает метод CreateElementInstances,
   * который в свою очередь вызывает WalkOnDocument.
   *
   * @param aCurrentNode из этой ноды берутся дочерние ноды, которые содержут информацию  о элементах, для которых
   * владельцем будет элемент aCurrentElement
   * @param aCurrentElement
   * @throws ModelException
   */
  private void WalkOnDocument(Node aCurrentNode, ModelForReadInterface aCurrentElement) throws ModelException {
    if ( FElementFactory.IsLastElement(aCurrentElement) ){
      return;
    }
    NodeList childNodes = aCurrentNode.getChildNodes();
    int i = 0;
    Node childNode;
    while ( i < childNodes.getLength() ){
      childNode = childNodes.item(i);
      if ( childNode.getNodeType() == Node.ELEMENT_NODE && !FElementFactory.IsLastNode( childNode ) ){
        CreateElementInstances(aCurrentNode, childNode, aCurrentElement);
      }
      i++;
    }
  }

  private void Read() throws ModelException, SAXException, IOException{
  	Element rootNode = FDocument.getDocumentElement();
    FRootElement = FElementFactory.GetNewElement(null, null, rootNode, GetNewId());
    WalkOnDocument( rootNode, FRootElement );
  }

  public void ReadModel(String aFileName) throws ModelException, SAXException, IOException{
    FDocument = FBuilder.parse( aFileName );
    Read();
  }

  public void ReadModel(File aFile) throws ModelException, SAXException, IOException{
    FDocument = FBuilder.parse( aFile );
    Read();
  }

  public void ReadModel(InputStream  aModelFile) throws ModelException, SAXException, IOException{
    FDocument = FBuilder.parse( aModelFile );
    Read();
  }

  public ModelForReadInterface GetRootElement(){
    return FRootElement;
  }

}
