package mp.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Properties;
import java.util.UUID;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.Endpoint;

import mp.elements.Model;
import mp.elements.ModelAddress;
import mp.elements.ModelConnector;
import mp.elements.ModelDirectConnector;
import mp.elements.ModelElementFactory;
import mp.elements.ModelException;
import mp.elements.ModelXMLReader;
import mp.elements.TestUtils;
import mp.parser.Variable;

@WebService

@SOAPBinding(style=SOAPBinding.Style.DOCUMENT,use=SOAPBinding.Use.LITERAL,
             parameterStyle=SOAPBinding.ParameterStyle.WRAPPED)
public class ModelManager {
	private ModelContainer modelContainer = null;
	private Hashtable<String, ConnectInfoBean> connections = new Hashtable  <String, ConnectInfoBean>();


	@WebMethod
	public int getAvailModelCount(){
		if (modelContainer == null) {
			return 0;
		}
		return modelContainer.getModelCount();
	}

	@WebMethod
	public ModelInfoBean getModelInfo( int modelIndex ) {
		if (modelContainer == null) {
			return null;
		}
    return modelContainer.get(modelIndex);
	}


	protected void SetModelContainer(ModelContainer container){
    this.modelContainer = container;
	}

	private Model CreateModelFromXML(String modelFileName) throws Exception{
		Model result = null;
    ModelElementFactory modelElementFactory = new ModelElementFactory();
    ModelXMLReader reader = new ModelXMLReader( modelElementFactory );
    reader.ReadModel( modelFileName );
    result = (Model) reader.GetRootElement();
    result.ApplyNodeInformation();
    //result.InitAllBlockStatecharts();
    return result;
	}

	@WebMethod
	public String StartModel(String guid) {
		ConnectInfoBean mb = connections.get(guid);
		if (mb == null) {
			return "guid not exist";
		}
    if ( mb.isRunning ) {
    	mb.lastError = "Модель уже запущена";
    	mb.lastOperationError = mb.lastError;
    	return mb.lastError;
    }
    Thread FModelThread = new Thread( mb.model );
    FModelThread.start();
    mb.isRunning = true;
    return "ok";
	}

	@WebMethod
	public String GetFormDescr(String guid){
		ConnectInfoBean mb = connections.get(guid);
		if (mb == null) {
			return "Отсутствует GUID";
		}
    ModelInfoBean modelInfo = mb.infoBean;

    try {
	    FileInputStream fis = new FileInputStream(modelInfo.getFormFileName());

	    FileChannel channel = fis.getChannel();
	    MappedByteBuffer buff = channel.map(MapMode.READ_ONLY, 0, channel.size());
	    byte[] bytes = new byte[buff.remaining()];
      buff.get(bytes);
      String res = new String(bytes,  modelInfo.getEncoding() );
      return res;
    } catch (Exception e) {
	    e.printStackTrace();
    }
    return "Вообще ничего не нашли";
	}

	@WebMethod
	public byte[] GetFormDescrAsByteArray(String guid){
		ConnectInfoBean mb = connections.get(guid);
		if (mb == null) {
			return null;
		}
    ModelInfoBean modelInfo = mb.infoBean;
    try {
	    FileInputStream fis = new FileInputStream(modelInfo.getFormFileName());

	    FileChannel channel = fis.getChannel();
	    MappedByteBuffer buff = channel.map(MapMode.READ_ONLY, 0, channel.size());
	    byte[] bytes = new byte[buff.remaining()];
      buff.get(bytes);
      return bytes;
    } catch (Exception e) {
	    e.printStackTrace();
    }
    return null;
	}

	@WebMethod
	public String CreateModel(String modelName){
    if ( modelContainer == null ) {
    	return null;
    }
    ModelInfoBean modelInfo = modelContainer.get(modelName);
    if ( modelInfo == null ) {
    	return null;
    }
    Model newModel = null;
    try {
	    newModel = CreateModelFromXML(modelInfo.getModelFileName());
    } catch (Exception e) {
    	e.printStackTrace();
	    return null;
    }
    ConnectInfoBean mb = new ConnectInfoBean();
    mb.model = newModel;
    mb.creatorGUID = java.util.UUID.randomUUID().toString();
    mb.infoBean = modelInfo;
    try {
	    ModelDirectConnector connector = new ModelDirectConnector(newModel);
	    mb.connector = connector;
    } catch (ModelException e) {
	    e.printStackTrace();
	    mb.lastOperationError = e.getMessage();
	    mb.lastError = mb.lastOperationError;
	    return null;
    }
    connections.put( mb.creatorGUID, mb );
    mb.lastOperationError = null;
    return mb.creatorGUID;
	}


	private static class ConnectInfoBean{
		Model model = null;
		boolean isRunning = false;
		String creatorGUID = null;
		ModelInfoBean infoBean = null;
		ModelConnector connector;
		String lastOperationError = null;
		String lastError;
	}

	public static void main(String[] args) {
		ModelDatastoreReader modelReader = new XMLModelListReader();
		InputStream config = ModelManager.class.getResourceAsStream("/model.properties");
		Properties  properties = new Properties();
		try {
	    properties.load(config);
	    modelReader.Init(properties);
	    modelReader.Reload();
    } catch (Exception e) {
	    e.printStackTrace();
	    return;
    }
    ModelContainer container = new ModelContainer(modelReader);
    // запускаем отдельный поток для отслеживания списка моделей
    Thread FModelThread = new Thread( container );
    FModelThread.start();
    ModelManager manager = new ModelManager();
    manager.SetModelContainer(container);
	  Endpoint.publish("http://localhost:8085/WS/modelService", manager);
	}

	private ModelConnector GetConnector(String guid) throws ModelException {
		ConnectInfoBean mb = connections.get(guid);
  	if ( mb == null ) {
  		throw new ModelException("Отсутствует GUID");
  	}
  	ModelConnector connector = mb.connector;
  	if ( connector == null ) {
  		throw new ModelException("Отсутствует соединения с моделью");
  	}
		return connector;
	}

	@WebMethod
  public double GetValue(String guid, String aModelName, String aBlockName, int aBlockIndex,
      String aParamName) throws ModelException {
  	return GetConnector(guid).GetValue(aModelName, aBlockName, aBlockIndex, aParamName);
  }

	@WebMethod
  public double GetValueByAddress(String guid, ModelAddress address) throws ModelException {
	  return GetConnector(guid).GetValue(address);
  }

	@WebMethod
  public boolean GetBooleanValue(String guid, ModelAddress address) throws ModelException {
	  return GetConnector(guid).GetBooleanValue(address);
  }

	@WebMethod
  public int GetIntValue(String guid, ModelAddress address) throws ModelException {
	  return GetConnector(guid).GetIntValue(address);
  }

	@WebMethod
  public String GetStringValueByAddress(String guid, ModelAddress address) throws ModelException {
	  return GetConnector(guid).GetStringValue(address);
  }

	@WebMethod
  public String GetStringValue(String guid, String aModelName, String aBlockName,
      int aBlockIndex, String aParamName) throws ModelException {

	  return GetConnector(guid).GetStringValue(aModelName, aBlockName, aBlockIndex, aParamName);
  }

	@WebMethod
  public void StartModelByGuid(String guid ) throws ModelException {
  	GetConnector(guid).StartModel();
  }

	@WebMethod
  public void StopModel(String guid ) {
  	try {
	    GetConnector(guid).StopModel();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
  }

	@WebMethod
  public void PauseModel(String guid) {
  	try {
	    GetConnector(guid).PauseModel();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
  }

	@WebMethod
  public void ResumeModel(String guid) {
  	try {
	    GetConnector(guid).ResumeModel();
    } catch (ModelException e) {
	    e.printStackTrace();
    }
  }

	@WebMethod
  public String GetErrorString(String guid) {
	  // TODO Auto-generated method stub
	  return null;
  }

	@WebMethod
  public int GetBlockIndex(String guid, String aBlockIndexValue) {
	  try {
	    return GetConnector(guid).GetBlockIndex(aBlockIndexValue);
    } catch (ModelException e) {
	    e.printStackTrace();
	    return -1;
    }
  }

	@WebMethod
  public int GetBlockCount(String guid, String aModelName, String aBlockName)
      throws ModelException {
	  return GetConnector(guid).GetBlockCount(aModelName, aBlockName);
  }

	@WebMethod
  public void SendDoubleValue(String guid, double aValue, String aModelName, String aBlockName,
      int aBlockIndex, String aParamName) throws ModelException {
  	GetConnector(guid).SendValue(aValue, aModelName, aBlockName, aBlockIndex, aParamName);

  }

	@WebMethod
  public void SendBooleanValue(String guid, boolean aValue, String aModelName, String aBlockName,
      int aBlockIndex, String aParamName) throws ModelException {
  	GetConnector(guid).SendValue(aValue, aModelName, aBlockName, aBlockIndex, aParamName);

  }

	@WebMethod
  public boolean IsConnectionEnabled(String guid, String aModelName, String aBlockName,
      int aBlockIndex, String aParamName) throws ModelException {
	  return GetConnector(guid).IsConnectionEnabled(aModelName, aBlockName, aBlockIndex, aParamName);
  }

	@WebMethod
  public boolean IsManagingEnabled(String guid, String aModelName, String aBlockName,
      int aBlockIndex, String aParamName) throws ModelException {
	  return GetConnector(guid).IsManagingEnabled(aModelName, aBlockName, aBlockIndex, aParamName);
  }

	@WebMethod
  public int GetValueType(String guid, String aModelName, String aBlockName,
      int aBlockIndex, String aParamName) throws ModelException {
	  return GetConnector(guid).GetValueType(aModelName, aBlockName, aBlockIndex, aParamName);
  }

	@WebMethod
  public void FireBlockEvent(String guid, String aBlockName, int aBlockIndex,
      String aEventName) throws ModelException {
  	GetConnector(guid).FireBlockEvent(aBlockName, aBlockIndex, aEventName);

  }

	@WebMethod
  public int Compare(String guid, Variable aVarToCompare, String aModelName,
      String aBlockName, int aBlockIndex, String aParamName)
      throws ModelException {

	  return GetConnector(guid).Compare(aVarToCompare, aModelName, aBlockName, aBlockIndex, aParamName);
  }

	@WebMethod
  public boolean IsArray(String guid, ModelAddress address) throws ModelException {
	  return GetConnector(guid).IsArray(address);
  }

	@WebMethod
  public int GetArrayDimensionCount(String guid, ModelAddress address) throws ModelException {
	  return GetConnector(guid).GetArrayDimensionCount(address);
  }

	@WebMethod
  public int GetArrayDimensionLength(String guid, ModelAddress address, int dimension)
      throws ModelException {
	  return GetConnector(guid).GetArrayDimensionLength(address, dimension);
  }

	@WebMethod
  public double GetArrayValue(String guid, ModelAddress address, int[] coordinates)
      throws ModelException {

	  return GetConnector(guid).GetArrayValue(address, coordinates);
  }

	@WebMethod
  public boolean IsHistoryExists(String guid, ModelAddress address) throws ModelException {
	  return GetConnector(guid).IsHistoryExists(address);
  }

	@WebMethod
  public String GetHistoryStringValue(String guid, ModelAddress address, int index)
      throws ModelException {
	  return GetConnector(guid).GetHistoryStringValue(address, index);
  }

}
