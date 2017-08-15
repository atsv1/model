package mp.elements;


import java.util.UUID;

import mp.parser.ScriptException;
import mp.parser.Variable;

/**
 * User: ����
 * Date: 04.05.2008
 */

public class ModelConstant extends ModelBlockParam implements ModelForReadInterface {

  public void SetConstantDescr( String aConstName, String aConstType, String aConstValue ) throws ModelException{
    try {      
      this.SetVarInfo( aConstType, aConstValue );
    } catch (Exception e) {
      e.printStackTrace();
      ModelException e1 = new ModelException( e.getMessage() );
      throw e1;
    }
  }

   public ModelConstant(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }

  protected void UpdateParam() throws ScriptException, ModelException {
    //������ ����� �� ������, � ��������� �� ����� ���� ������������ ����
  }

  public boolean IsNeedRuntimeUpdate() {
    return false;
  }

  public void SetVariable (Variable aVariable){
  	FVariable = aVariable;
  }
  
  public void fixState(UUID stateLabel) throws ModelException{
  	//������ �� ������
  }
    
  public void rollbackTo(UUID stateLabel) throws ModelException{
    //������ �� ������
  }

}
