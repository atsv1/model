package mp.elements;

import mp.parser.Variable;
import mp.parser.ScriptException;

/** Класс отвечает за реализацию перехода по тайм-айту - то есть периодически, один раз за указанный в параметре
 * период времени.
 */
public class AutomatTransitionTimeout extends AutomatTransition {
  ModelTime FNextExecTime = null;
  ModelTime FOwnerActivateTime = null;
  ModelTime FTempTime = null;
  boolean FApplyFlag = false;


  public AutomatTransitionTimeout(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FNextExecTime = new ModelTime(0);
    AutomatState owner = (AutomatState) aOwner;
    if ( owner != null ){
      FOwnerActivateTime = owner.GetActivateTime();
    } else {
      FOwnerActivateTime = new  ModelTime( 0 );
    }
    FTempTime = new ModelTime( 0 );
  }


  /**Функция проверяет, возможен ли переход, за который "ответственен" данный объект
  * Внимание! Если переход возможен, то автоматически изменится дата следующего срабатывания.
  * @param aCurrentTime
  * @return
  * @throws ModelException
  */
  public boolean IsTransitionEnabled(ModelTime aCurrentTime) throws ModelException {
    if ( !FApplyFlag ){
      ApplyNodeInformation();
    }
    int i = FNextExecTime.Compare( aCurrentTime );
    if ( i == ModelTime.TIME_COMPARE_GREATER ){
      return false;
    }
    FTempTime.StoreValue( FOwnerActivateTime );
    FTempTime.Add( FTransitionVar );
    i = FTempTime.Compare( aCurrentTime );
    if ( i == ModelTime.TIME_COMPARE_LOW || i == ModelTime.TIME_COMPARE_EQUALS ) {
      FNextExecTime.StoreValue( aCurrentTime );
      FNextExecTime.Add( FTransitionVar );
      //System.out.println( "Изменения времени на " + FTransitionVar.toString() + " новое время = " + FNextExecTime.toString() );
      return true;
    }
    FNextExecTime.StoreValue( FOwnerActivateTime );
    FNextExecTime.Add( FTransitionVar );
    return false;
  }

  public ModelTime GetTransitionTime() throws ModelException {
    if ( !FApplyFlag ){
      ApplyNodeInformation();
    }
    return FNextExecTime;
  }

  protected boolean IsValue(String aTransValue) {
    double d;
    try{
      d = Double.parseDouble( aTransValue );
      FTransitionVar = new Variable(d);
      FNextExecTime.Add( FTransitionVar );
      return true;
    } catch (Exception e){
      return false;
    }
  }

  protected void SetTimeoutVariable( String aVarName ) throws ModelException{
    FTransitionVar = this.GetVarByName( aVarName );
    if ( !(FTransitionVar.GetTypeName().equalsIgnoreCase("real") ||
            FTransitionVar.GetTypeName().equalsIgnoreCase("integer") ) )  {
      ModelException e = new ModelException("Параметр \"" + aVarName + "\" должен быть числовым");
      throw e;
    }
    FNextExecTime.Add( FTransitionVar );
    FApplyFlag = true;
  }

  public void ApplyNodeInformation() throws ModelException{
    ReadValueInfo();
    ReadNextState();
    try{
      ReadTransitionCode();
    } catch ( ScriptException e ){
    	e.printStackTrace();
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
