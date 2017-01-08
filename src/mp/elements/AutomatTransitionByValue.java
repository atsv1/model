package mp.elements;

import mp.parser.Variable;
import mp.parser.ScriptException;

/**
 */
public class AutomatTransitionByValue extends AutomatTransition {
  private boolean FApplyFlag = false;

  public AutomatTransitionByValue(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }


  public boolean IsTransitionEnabled(ModelTime aCurrentTime) throws ModelException {
    if ( !FApplyFlag ){
      ApplyNodeInformation();
    }
    return FTransitionVar.GetBooleanValue();
  }

  public ModelTime GetTransitionTime() {
    return null;
  }

  protected boolean IsValue(String aTransValue) {
    if ( "true".equalsIgnoreCase( aTransValue ) ){
      FTransitionVar = new Variable(true);
      return true;
    }
    if ( "false".equalsIgnoreCase( aTransValue ) ){
      FTransitionVar = new Variable(false);
      return true;
    }
    return false;
  }

  protected void SetTransitionVariable(String aVarName) throws ModelException{
    FTransitionVar = GetVarByName(aVarName);
    if (!FTransitionVar.GetTypeName().equalsIgnoreCase("boolean") ){
      ModelException e = new ModelException("Переменная " + aVarName + " должна быть булевского типа");
      throw e;
    }
    FApplyFlag = true;
  }

  public void ApplyNodeInformation() throws ModelException{
    ReadValueInfo();
    ReadNextState();
    try{
      ReadTransitionCode();
    } catch ( ScriptException e ){
      ModelElement owner = this.GetOwner();
      String ownerName = "";
      if ( owner != null ){
        ownerName = owner.GetName();
      }
      ModelException e1 = new ModelException("Ошибка при обработке скрипта перехода в элементе \"" +
              ownerName + "." + this.GetName() + "\" Ошибка: " + e.getMessage());
      throw e1;
    }
    FApplyFlag = true;
  }

}
