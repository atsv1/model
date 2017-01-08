package mp.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mp.elements.ModelAddress;
import mp.elements.ModelConnector;
import mp.elements.ModelException;
import mp.parser.Variable;

public class ModelServiceConnector implements ModelConnector {
	private ModelManager servicePort = null;
	private String guid;

	public ModelServiceConnector( ModelManager servicePort, String guid ){
		this.servicePort = servicePort;
		this.guid = guid;
	}

	private mp.client.ModelAddress TransformAddress(ModelAddress address){
		mp.client.ModelAddress addr = new mp.client.ModelAddress();
		addr.setFBlockIndex(address.GetBlockIndex());
		addr.setFBlockName(address.GetBlockName());
		addr.setFModelName(address.GetModelName());
		addr.setFParamName(address.GetParamName());
		return addr;
	}

	@Override
	public double GetValue(String aModelName, String aBlockName, int aBlockIndex,
	    String aParamName) throws ModelException {
		try {
		   return servicePort.getValue(guid, aModelName, aBlockName, aBlockIndex, aParamName);
		} catch (Exception e) {
			throw new ModelException(e.getMessage());
		}
	}

	@Override
	public double GetValue(ModelAddress address) throws ModelException {
		mp.client.ModelAddress addr = TransformAddress(address);
		try {
	    return servicePort.getValueByAddress(guid,  addr);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }
	}

	@Override
	public boolean GetBooleanValue(ModelAddress address) throws ModelException {
		try {
	    return servicePort.getBooleanValue(guid, TransformAddress(address) );
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public int GetIntValue(ModelAddress address) throws ModelException {
		try {
	    return servicePort.getIntValue(guid, TransformAddress(address) );
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public String GetStringValue(ModelAddress address) throws ModelException {
		try {
	    return servicePort.getStringValueByAddress(guid, TransformAddress(address) );
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }
	}

	@Override
	public String GetStringValue(String aModelName, String aBlockName,
	    int aBlockIndex, String aParamName) throws ModelException {
		try {
	    return servicePort.getStringValue(guid, aModelName, aBlockName, aBlockIndex, aParamName);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public void StartModel() throws ModelException {
		servicePort.startModel(guid);
	}

	@Override
	public void StopModel() {
		servicePort.stopModel(guid);
	}

	@Override
	public void PauseModel() {
		servicePort.pauseModel(guid);
	}

	@Override
	public void ResumeModel() {
		servicePort.resumeModel(guid);
	}

	@Override
	public String GetErrorString() {
		return servicePort.getErrorString(guid);

	}

	@Override
	public int GetBlockIndex(String aBlockIndexValue) {
		return servicePort.getBlockIndex(guid, aBlockIndexValue);
	}

	@Override
	public int GetBlockCount(String aModelName, String aBlockName)
	    throws ModelException {
		try {
	    return servicePort.getBlockCount(guid,  aModelName, aBlockName);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }
	}

	@Override
	public void SendValue(double aValue, String aModelName, String aBlockName,
	    int aBlockIndex, String aParamName) throws ModelException {
		try {
	    servicePort.sendDoubleValue(guid, aValue, aModelName, aBlockName, aBlockIndex, aParamName);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }
	}

	@Override
	public void SendValue(boolean aValue, String aModelName, String aBlockName,
	    int aBlockIndex, String aParamName) throws ModelException {
		try {
	    servicePort.sendBooleanValue(guid, aValue, aModelName, aBlockName, aBlockIndex, aParamName);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }
	}

	@Override
	public boolean IsConnectionEnabled(String aModelName, String aBlockName,
	    int aBlockIndex, String aParamName) throws ModelException {
		try {
	    return servicePort.isConnectionEnabled(guid, aModelName, aBlockName, aBlockIndex, aParamName);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public boolean IsManagingEnabled(String aModelName, String aBlockName,
	    int aBlockIndex, String aParamName) throws ModelException {
		try {
	    return servicePort.isManagingEnabled(guid, aModelName, aBlockName, aBlockIndex, aParamName);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public int GetValueType(String aModelName, String aBlockName,
	    int aBlockIndex, String aParamName) throws ModelException {
		try {
	    return servicePort.getValueType(guid, aModelName, aBlockName, aBlockIndex, aParamName);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public void FireBlockEvent(String aBlockName, int aBlockIndex,
	    String aEventName) throws ModelException {
		try {
	    servicePort.fireBlockEvent(guid,  aBlockName, aBlockIndex, aEventName);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public int Compare(Variable aVarToCompare, String aModelName,
	    String aBlockName, int aBlockIndex, String aParamName)
	    throws ModelException {
		throw new ModelException("Не работает пока");
	}

	@Override
	public boolean IsArray(ModelAddress address) throws ModelException {
		try {
	    return servicePort.isArray(guid, TransformAddress(address) );
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public int GetArrayDimensionCount(ModelAddress address) throws ModelException {
		try {
	    return servicePort.getArrayDimensionCount(guid,  TransformAddress(address));
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }
	}

	@Override
	public int GetArrayDimensionLength(ModelAddress address, int dimension)
	    throws ModelException {
		try {
	    return servicePort.getArrayDimensionLength(guid, TransformAddress(address), dimension);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public double GetArrayValue(ModelAddress address, int coordinates[])
	    throws ModelException {
    List<Integer> newList = new ArrayList<Integer>();
    for (int val : coordinates){
    	newList.add(val);
    }
		try {
	    return servicePort.getArrayValue(guid, TransformAddress(address), newList);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }

	}

	@Override
	public boolean IsHistoryExists(ModelAddress address) throws ModelException {
		try {
	    return servicePort.isHistoryExists(guid, TransformAddress(address));
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }
	}

	@Override
	public String GetHistoryStringValue(ModelAddress address, int index)
	    throws ModelException {
		try {
	    return servicePort.getHistoryStringValue(guid, TransformAddress(address), index);
    } catch (ModelException_Exception e) {
    	throw new ModelException(e.getMessage());
    }
	}

}
