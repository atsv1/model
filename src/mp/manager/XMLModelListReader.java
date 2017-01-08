package mp.manager;

import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import mp.elements.ModelException;

public class XMLModelListReader implements ModelDatastoreReader {
	private static String PARAM_PATH_NAME = "XML_MODEL_FILE";
	private String fileNameFromProperties = null;
	private static String MODEL_LIST_NODE_NAME = "ModelList";
	private static String MODEL_NOE_NAME = "Model";

	private Vector<ModelInfoBean> modelList = new Vector <ModelInfoBean>();
	private int currentModelIndex = 0;

	@Override
	public void Init(Properties props) throws ModelException{
    if ( props == null ) {
      throw new ModelException("Пустой объект свойств");
    }
    fileNameFromProperties = props.getProperty(PARAM_PATH_NAME);
    if ( "".equals(fileNameFromProperties) || fileNameFromProperties == null) {
    	throw new ModelException("Отсутствует имя файла со списком моделей");
    }
	}

	private void ReadModelsFromRootNode( Element rootNode ) throws ModelException{
		if ( rootNode == null ) {
			return;
		}
		Vector<ModelInfoBean> modelList = new Vector <ModelInfoBean>();
		NodeList childNodes = rootNode.getChildNodes();
		int i = 0;
    Node childNode;
    while ( i < childNodes.getLength() && MODEL_LIST_NODE_NAME.equalsIgnoreCase(rootNode.getNodeName())){
      childNode = childNodes.item(i);
      if ( childNode.getNodeType() == Node.ELEMENT_NODE  ){
      	ModelInfoBean modelBean = new ModelInfoBean();

      	NamedNodeMap attributes = childNode.getAttributes();
        Node attr = attributes.getNamedItem( "name" );
        if (attr == null) {
        	throw new ModelException("Отсутствует название у модели в файле со списком моделей");
        }
      	modelBean.setModelName(attr.getNodeValue());

        attr = attributes.getNamedItem( "modelFile" );
        if ( attr == null ) {
        	throw new ModelException("Отсутствует имя файла модели для модели " + modelBean._getModelName());
        }
        modelBean.setModelFileName(attr.getNodeValue());

        attr = attributes.getNamedItem( "formFile" );
        if (attr != null) {
        	modelBean.setFormFileName(attr.getNodeValue());
        }

        attr = attributes.getNamedItem( "formEncoding" );
        if (attr != null) {
        	modelBean.setEncoding(attr.getNodeValue());
        }
        modelList.add(modelBean);
      	System.out.println( childNode.getNodeName() );
      }
      i++;
    }
    this.modelList = modelList;
	}

	@Override
	public void Reload() {
		DocumentBuilderFactory factory  = DocumentBuilderFactory.newInstance();
		try {
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    Document document = builder.parse(fileNameFromProperties);
	    Element rootNode = document.getDocumentElement();
	    ReadModelsFromRootNode(rootNode);
	    currentModelIndex = 0;
    } catch (ParserConfigurationException e) {
	    e.printStackTrace();
    } catch (SAXException e) {
	    e.printStackTrace();
    } catch (IOException e) {
	    e.printStackTrace();
    } catch (ModelException e) {
	    e.printStackTrace();
    }

	}

	@Override
	public ModelInfoBean getNext() {
    if ( modelList.size() <= currentModelIndex ) {
    	return null;
    }
    currentModelIndex++;
		return modelList.get(currentModelIndex-1);
	}

}
