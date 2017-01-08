package mp.elements;

import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;
import mp.parser.Operand;
import mp.parser.ScriptException;
import mp.parser.Variable;

import java.util.Vector;

/**
 * User: саша
 * Date: 02.06.2008
 * Time: 11:26:38
 */
public class ModelMultiplexorWithSkipParam extends ModelMultiplexor{
  private ModelBlockParam FSkipFirstParam = null;
  //private ExistsService FSkipArray = null;
  private Variable FCurrentMinValue = null;
  private Variable FCurrentMaxValue;
  private Vector FMaxList = null;


  private int GetInsertPos( Variable aCurrentVar ) throws ScriptException {
    int i = 0;
    LinkedBlockRecord record = null;
    int res;
    while ( i < FMaxList.size() ){
      record = (LinkedBlockRecord) FMaxList.get( i );
      res = aCurrentVar.Compare( record.GetCriteriaValue() );
      if ( res == 1 ) {
        return i;
      }
      i++;
    }
    return i;
  }

  private void ClearRedundandRecords() throws ScriptException {
    int i = FSkipFirstParam.GetVariable().GetIntValue();
    LinkedBlockRecord lastRec = null;
    int lastIndex;
    while ( i < FMaxList.size() - 1 ){
      lastIndex = FMaxList.size() - 1;
      lastRec = (LinkedBlockRecord) FMaxList.get( lastIndex );
      FMaxList.remove( lastRec );
    }
    if ( lastRec != null ){
      FCurrentMinValue.StoreValueOf( lastRec.GetCriteriaValue() );  
    }
  }

  /**Метод используется тогда, когда в мультиплексоре определен параметр skipFirst
   *
   * @param aRecord
   */
  private boolean SaveCriteriaResult( LinkedBlockRecord aRecord ) throws ScriptException {
    boolean compareRes = false;
    Variable currentVar;
    currentVar = aRecord.GetCriteriaValue();
    // проверяем, не меньше ли переданное значение самого минимального текущего значения
    int intRes = currentVar.Compare( FCurrentMinValue );
    if ( intRes == -1 ) {
      return false;
    }
    //проверяем, не больше ли уже найденного максимума текущее значение
    intRes = currentVar.Compare( FCurrentMaxValue );
    if ( intRes == 1 ){
      //значение в currentVar является максимальным
      FCurrentMaxValue.StoreValueOf( currentVar );
      FMaxList.insertElementAt( aRecord, 0 );
      ClearRedundandRecords();
      return true;
    }
    int insertPos = GetInsertPos( currentVar );
    FMaxList.insertElementAt( aRecord, insertPos );
    ClearRedundandRecords();
    return compareRes;
  }

  public ModelMultiplexorWithSkipParam(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FCurrentMinValue = new Variable( Double.NEGATIVE_INFINITY );
    FCurrentMaxValue = new Variable( Double.MAX_VALUE );
    FMaxList = new Vector( 5 );
  }

  private void ReadSkipFirstParam() throws ModelException {
    ModelAttributeReader reader = ServiceLocator.GetAttributeReader();
    reader.SetNode( GetNode() );
    String text = reader.GetSkipFirstValue();
    if ( text == null || "".equalsIgnoreCase( text ) ){
      return;
    }
    //пробуем найти элемент с таким названием среди уже существующих параметров мультиплексора
    FSkipFirstParam = (ModelBlockParam) Get( text );
    if ( FSkipFirstParam != null ){
      //такой параметр есть, поэтому используем его
      if ( FSkipFirstParam.GetVariable().GetType() == Operand.OPERAND_TYPE_INTEGER ){
        return;
      }
      ModelException e = new ModelException("Ошибка в мультиплексоре \"" + GetFullName() +  "\": параметр \"" +
         FSkipFirstParam + "\" должен быть типа integer, чтобы использоваться как skipfirst");
      throw e;
    }
    FSkipFirstParam = new ModelBlockParam( this, GetName() + "_skipFirst", ServiceLocator.GetNextId() ) {
      protected void UpdateParam() throws ScriptException, ModelException {}
      public boolean IsNeedRuntimeUpdate() {
        return false;
      }
    };
    FSkipFirstParam.SetVarInfo( "integer", text );
  }


  private void PrepareSkipData(){
    FCurrentMinValue.SetValue( Double.NEGATIVE_INFINITY );
    FCurrentMaxValue.SetValue( Double.MAX_VALUE );
    FMaxList.clear();
  }

  public void ApplyNodeInformation() throws ModelException {
    super.ApplyNodeInformation();
    //читаем параметр skipfirst
    ReadSkipFirstParam();
  }

  protected int GetSkipFirstValue(){
    if ( FSkipFirstParam == null ){
      return 0;
    }
    try {
      return FSkipFirstParam.GetVariable().GetIntValue();
    } catch (ScriptException e) {
      return 0;
    }
  }

   protected  ModelBlock GetMaxCriteriaBlock_WithoutMaxCount() throws ModelException{
    if ( GlobalParams.MuxOutputEnabled()  ){
      System.out.println(" GetMaxCriteriaBlock started. mux: " + GetFullName());
    }
    if ( FSourceList.size() == 0 ){
      return null;
    }
    Variable currentMax = new Variable( Double.NEGATIVE_INFINITY );
    int maximumCount = 0;
    int i = 0;
    Variable currentVar;
    LinkedBlockRecord record;
    LinkedBlockRecord lastRecord = null;
    int compareRes = 0;
    int currentMaxIndex = -1;
    int queueSize = 0;
    while ( i < FSourceList.size() ) {
      record = (LinkedBlockRecord) FSourceList.get( i );
      if ( record.GetEnableResult() ){
        queueSize++;
        currentVar = record.GetCriteriaValue();
        try {
          SaveCriteriaResult( record );
          compareRes = currentVar.Compare( currentMax );
        } catch (ScriptException e) {
          //e.printStackTrace();
          ModelException e1 = new ModelException("Ошибка в мультиплексоре \"" + GetFullName() + "\": " + e.getMessage());
          throw e1;
        }
        lastRecord = record;
        if ( compareRes == 1){
          currentMaxIndex = i;
          currentMax = currentVar;
          maximumCount = 0;

        }
        if ( compareRes == 0 ){
          maximumCount++;
        }
      }
      i++;
    }//while
    if ( maximumCount > 0 || ( currentMaxIndex == -1 ) ){
      if ( GlobalParams.MuxOutputEnabled() ) {
        System.out.println(" mux " + GetFullName() + " no block ");
      }
      return null;
    }
    record = (LinkedBlockRecord) FSourceList.get( currentMaxIndex  );
    if ( GlobalParams.MuxOutputEnabled() ) {
      System.out.println(" mux " + GetFullName() + " block " + record.GetBlock().toString() );
    }

    FireCriteriaEvents( record.GetBlock() );
    return record.GetBlock();
  }

  private ModelBlock GetMaxSkippedMaxCriteriaBlock() throws ScriptException {
    ModelBlock result = null;
    LinkedBlockRecord rec;
    int i = FSkipFirstParam.GetVariable().GetIntValue();
    if ( i < FMaxList.size() ){
      rec = (LinkedBlockRecord) FMaxList.get( i );
      return rec.GetBlock();
    }
    return result;
  }

  protected ModelBlock GetMaxCriteriaBlock() throws ModelException{
    PrepareSkipData();
    super.GetMaxCriteriaBlock();
    try {
      return GetMaxSkippedMaxCriteriaBlock();
    } catch (ScriptException e) {
      e.printStackTrace();
      ModelException e1 = new ModelException("Ошибка в мультиплексоре \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;
    }
  }


}
