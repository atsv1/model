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
public  class ModelXMLReader extends ModelBuilder{
  private DocumentBuilder FBuilder;
  private boolean FValidating = false;
  private Document FDocument = null;
  private ModelForReadInterface FRootElement = null;  
  
  
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
    setElementFactory(elementFactory);
  }

  
  private void Read() throws ModelException, SAXException, IOException{
  	Element rootNode = FDocument.getDocumentElement();
  	ModelElementDataSource attrReader = new ModelAttributeReader(rootNode, null);
    FRootElement = getElementFactory().GetNewElement(null, null, attrReader, GetNewId());
    FRootElement.SetDataSource(attrReader);
    WalkOnDocument( attrReader, FRootElement, null );
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
