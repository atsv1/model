package mp.elements;

import java.util.Vector;


import mp.parser.ParserFactory;
import mp.parser.ScriptArray;
import mp.parser.ScriptException;
import mp.parser.ScriptLanguageExt;
import mp.parser.ScriptOperationUserFunction;
import mp.parser.ScriptParser;
import mp.parser.Variable;
import mp.utils.ModelAttributeReader;

public class ModelFunction extends ModelCalculatedElement {
	private ScriptOperationUserFunction Function;
	private Vector<ModelBlockParam> InnerVariables = null;
	

	private String NODE_NAME_FORMULA = "Formula";
	private String NODE_NAME_PARAM = "Param";

	public ModelFunction(ModelElement aOwner, String aElementName, int aElementId) {
	  super(aOwner, aElementName, aElementId);
 	  Function = new ScriptOperationUserFunction();
  }

	public ScriptOperationUserFunction GetFunction(){
		return Function;
	}

	public void ReadFunctionInfo(ModelElementDataSource dataSource) throws ModelException{
		String funcName = dataSource.GetAttrName();
    String typeName = dataSource.GetAttrParamType();
    
    Function.SetName(funcName);
    try {
	    Function.SetResultInfo(typeName);
    } catch (ScriptException e) {
	    throw new ModelException(e.getMessage());
    }
	}

	public void ApplyNodeInformation() throws ModelException{
		ModelElementDataSource functionSource = GetDataSource();
		NodeList nodes = functionNode.getChildNodes();
    if (nodes == null || nodes.getLength() == 0) {
      throw new ModelException("В функции " + Function.GetName() + " отсутствует исполняемый код");
    }
    String sourceCode = null;
    int i = 0;
    Node curNode;
    while (i < nodes.getLength()) {
    	curNode = nodes.item(i);
    	if ( NODE_NAME_FORMULA.equalsIgnoreCase(curNode.getNodeName()) ) {
    		sourceCode = GetFormulaSourceCode(curNode);
    	}
    	if (NODE_NAME_PARAM.equalsIgnoreCase(curNode.getNodeName() )) {
    		FAttrReader.SetNode(curNode);
        String paramName = FAttrReader.GetAttrName();
        String typeName = FAttrReader.GetAttrParamType();
        int orderNum;
        try {
          orderNum = FAttrReader.GetNumberAttr();
        } catch (Exception e) {
        	throw new ModelException("Ошибка при обработке порядкового номера переменной в функции " + Function.GetName() + ": " + e.getMessage());
        }
        try {
        	if ( IsVarParam(FAttrReader)  ) {
        		Variable var = null;
            if ("array".equals(typeName)) {
            	var = new ScriptArray();
            	var.SetName(paramName);
            	Function.AddVarInpParam(var, orderNum);
            }
        	} else {
	          Function.AddInpVar(orderNum, paramName, typeName);
        	}
        } catch (ScriptException e) {
        	throw new ModelException("Ошибка при обработке входной переменной в функции " + Function.GetName() + ": " + e.getMessage());
        }
    	}
    	i++;
    }
    if (sourceCode == null || "".equals(sourceCode)) {
    	throw new ModelException("В функции " + Function.GetName() + " отсутствует исполняемый код");
    }
    if (InnerVariables != null && InnerVariables.size() > 0) {
    	ModelBlockParam param;
    	i = 0;
    	while ( i < InnerVariables.size()) {
    		try {
    			param = InnerVariables.get(i);
	        FLanguageExt.AddVariable(param.GetVariable());
        } catch (ScriptException e) {
        	throw new ModelException("Ошибка при внутренней переменной функции " + Function.GetName() + " " + e.getMessage());
        }
    		i++;
    	}
    }
    try {
	    SetSourceCode(sourceCode);
    } catch (ScriptException e) {
    	throw new ModelException("Ошибка при обработке функции " + Function.GetName() + " " + e.getMessage());
    }
	}

	/**
	 * Функция определяет, как передается параметр в функцию - непосредственно объектом, или своей копией.
	 * @param attrReader
	 * @return
	 * @throws ModelException
	 */
	private boolean IsVarParam(ModelElementDataSource attrReader) throws ModelException{
		String paramType = attrReader.GetAttrParamType();
		if ("array".equalsIgnoreCase(paramType)) {
			return true;
		}
		return false;
	}


	public void SetSourceCode(String code) throws ModelException, ScriptException{
		if ( FLanguageExt == null){
      ModelException e = new ModelException("Попытка начать подготовку элемента " + this.GetName() +
              " с пустым расширителем языка");
      throw e;
    }
		Function.SetExternalLanguageExt(FLanguageExt);
		Function.SetSourceCode(code);
	}

	public void AddInnerParam( ModelBlockParam aParam ){
		if ( InnerVariables == null ) {
			InnerVariables = new Vector<ModelBlockParam>();
		}
		InnerVariables.add(aParam);
	}

}
