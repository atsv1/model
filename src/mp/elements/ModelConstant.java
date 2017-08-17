package mp.elements;


import java.util.UUID;

import mp.parser.ScriptException;
import mp.parser.Variable;

/**
 * User: саша
 * Date: 04.05.2008
 */

public class ModelConstant extends ModelBlockParam implements ModelForReadInterface {
	
	private String constVal = null;

  public void SetConstantDescr( String aConstName, String aConstType, String aConstValue ) throws ModelException{
    try {      
      this.SetVarInfo( aConstType, aConstValue );
    } catch (Exception e) {
      e.printStackTrace();
      throw new ModelException( e.getMessage() );      
    }
    constVal = aConstValue;
  }

   public ModelConstant(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }

  protected void UpdateParam() throws ScriptException, ModelException {
    //ничего здесь не делаем, в константе не может быть выполняемого кода
  }

  public boolean IsNeedRuntimeUpdate() {
    return false;
  }

  public void SetVariable (Variable aVariable){
  	FVariable = aVariable;
  }
  
  public String GetConstantStringValue(){
  	return constVal;
  }
  
  public void fixState(UUID stateLabel) throws ModelException{
  	//ничего не делаем
  }
    
  public void rollbackTo(UUID stateLabel) throws ModelException{
    //ничего не делаем
  }

}
