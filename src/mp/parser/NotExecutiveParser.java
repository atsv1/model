package mp.parser;

import java.util.Vector;

/**
 * User: atsv
 * Date: 05.09.2007
 *
 * Класс "Неисполняемый парсер" предназначен для обеспечения доступа клиентов к исполняемой программе. Причина появления
 * данного класса состоит в том, что держать в памяти несколько сотен парсеров с одной и той же программой - слишком
 * расточительно. Для избежания такого непродуктивного расходования памяти создается один экземпляр парсера с
 * откомпилированной программой, и создается много классов, которые организуют доступ к этой откомпилированной
 * программе.
 * Вот этот класс как раз и организует доступ к этой программе.
 */
public class NotExecutiveParser implements ScriptParser {
  private ScriptParser FParser = null;
  private ScriptLanguageExt FLanguageExt = null;
  private int[] FSelfVarIndexArray = null;
  private int[] FParserVarIndexArray = null;
  private VariableList FParserVarsList = null;
  private ExecutionContext FContext = null;

  protected NotExecutiveParser( ScriptParser aParser, ScriptLanguageExt aLanguageExt ) throws ScriptException{
    if ( aParser == null ){
      ScriptException e = new ScriptException("Пустой парсер");
      throw e;
    }
    if ( aLanguageExt == null){
      ScriptException e = new ScriptException("Пустой расширитель языка");
      throw e;
    }
    FParser = aParser;
    FParserVarsList = FParser.GetVariables();
    FLanguageExt = aLanguageExt;
    /*String s = IsRelativeToParser( Parser, FLanguageExt );
    if ( s != null ){
      ScriptException e = new ScriptException(s);
      throw e;
    }*/
    CreateUsedVariablesList();
  }

  /** Функция проверяет, можно ли использовать переданный в параметре расширитель языка совместно с переданным парсером.
   * По каким критериям происходит проверка: все переменные в расширителе языка и в парсере должны полностью совпадать:
   * по типу и по имени.
   * Также переменных должно быть одинаковое количество.
   *
   * @param aParser
   * @param aLanguageExt
   * @return возвращается null, если парсер можно использовать с этим расширителем языка. Иначе возвращается текст
   * ошибки
   */
  private String IsRelativeToParser(ScriptParser aParser, ScriptLanguageExt aLanguageExt){
    VariableList parserVariables = aParser.GetVariables();
    VariableList variables = aLanguageExt.GetVariables();
    if ( parserVariables.GetNoServiceVariablesCount() != variables.GetNoServiceVariablesCount() ){
      return "Не равны размеры списков";
    }
    int i = 0;
    Variable parserVar = null;
    Variable listVar = null;
    while ( i < variables.GetSize() ){
      parserVar = parserVariables.GetVariable( i );
      listVar = variables.GetVariable( i );
      if ( !parserVar.GetName().equalsIgnoreCase(listVar.GetName()) ){
        return "Не совпадают имена переменных \"" + parserVar.GetName() + "\" и \"" + listVar.GetName() + "\"";
      }
      if ( !parserVar.GetTypeName().equalsIgnoreCase( listVar.GetTypeName() ) ) {
        return "Не совпадают типы переменных \"" + parserVar.GetName() + "\" ";
      }
      i++;
    }
    return null;
  }

  private boolean IsExistInList( Vector aList, Variable var ){
    return aList.indexOf( var ) != -1;
  }

  /** Метод создает список переменных (объектов Variable), которые используются в программе, находящейся внутри
   * парсера. Одновременно для каждой используемой переменной ищется ее аналог в списке переменных FLanguageExt.
   * Смысл данной процедуры в том, чтобы ускорить работу парсера, а именно - ускорить работу процедуры загрузки
   * значений в исполняемый парсер и выгрузки из него значений.
   * Во время составления списка осуществляются такие проверки:
   * 1. Все переменные, которые используются в исполняемом парсере, должны иметь полный аналог среди переменных,
   * список которых передан в этот неисполняемый парсер (полный аналог - совпадание типа и названия)
   * 2.
   *
   */
  private void CreateUsedVariablesList() throws ScriptException{
    Variable currentParserVar = null;
    Vector usedSelfVarsList = new Vector();
    Vector usedParsersVarsList = new Vector();
    Variable selfVar = null;
    currentParserVar = (Variable) FParser.First("mp.parser.Variable", "mp.parser.ScriptArray");
    while ( currentParserVar != null ){
      if ( !ScriptLanguageDef.IsServiceName(currentParserVar.GetName()) ) {
        selfVar = FLanguageExt.Get(currentParserVar.GetName());
        if ( selfVar == null ){
          //throw new ScriptException("В собственном списке переменных отсутствует переменная \""  + currentParserVar.GetName() + "\"");
          
        }
        if ( selfVar != null && !selfVar.GetTypeName().equalsIgnoreCase( currentParserVar.GetTypeName() ) ){
          throw new ScriptException("Не совпадают типы у переменных \"" + currentParserVar.GetName() + "\": " + selfVar.GetTypeName() + " и " + currentParserVar.GetTypeName());
          
        }
        if ( selfVar != null && !IsExistInList( usedSelfVarsList, selfVar ) ){
          usedSelfVarsList.add( selfVar );
          usedParsersVarsList.add( currentParserVar );
        }
      }
      currentParserVar = (Variable) FParser.Next("mp.parser.Variable", "mp.parser.ScriptArray");
    }
    if ( usedSelfVarsList.size() > 0 ) {
      FSelfVarIndexArray = new int[ usedSelfVarsList.size() ];
      FParserVarIndexArray = new int[ usedSelfVarsList.size() ];
      int i = 0;
      while ( i < usedSelfVarsList.size() ){
        FSelfVarIndexArray[i] = FLanguageExt.GetVariables().IndexOf((Variable) usedSelfVarsList.get(i));
        FParserVarIndexArray[i] = FParserVarsList.IndexOf((Variable) usedParsersVarsList.get( i ));
        i++;
      }
    }
  }

  /**метод нуен только для автоматического тестирования
   *
   * @return
   */
  protected int[] GetUsedVars(){
    return FSelfVarIndexArray;
  }

  private void LoadVarsToParser() throws ScriptException {
    Variable parserVar = null;
    Variable selfVar = null;
    int i = 0;
    if ( FSelfVarIndexArray == null ) {
      return;
    }
    while ( i < FSelfVarIndexArray.length ){
      parserVar = FParserVarsList.GetVariable( FParserVarIndexArray[i] );
      selfVar = FLanguageExt.GetVariables().GetVariable( FSelfVarIndexArray[i] );
      parserVar.StoreValueOf( selfVar );
      i++;
    }

  }

  private void LoadVarsToSelfvars() throws ScriptException {
    Variable parserVar = null;
    Variable selfVar = null;
    int i = 0;
    if ( FSelfVarIndexArray == null ) {
      return;
    }
    while ( i < FSelfVarIndexArray.length ){
      parserVar = FParserVarsList.GetVariable( FParserVarIndexArray[i] );
      selfVar = FLanguageExt.GetVariables().GetVariable( FSelfVarIndexArray[i] );
      selfVar.StoreValueOf( parserVar );
      i++;
    }

  }

  public void ExecuteScript() throws ScriptException {
    LoadVarsToParser();
    FParser.AddExecutionContext(FContext);
    FParser.ExecuteScript();
    LoadVarsToSelfvars();
  }

  /** Функция предназначена для определения необходимости подмены объекта, возвращаемого парсером, на объект, имеющийся
   * в этом неисполняемом парсере
   *
   * @param aProgObj - объект, который, возможно, нужно подменять
   * @return - возвращает true, если объект нужно подменять
   */
  private  boolean IsNeedToChangeObject( ScriptProgramObject aProgObj ){
    if ( aProgObj != null && aProgObj instanceof mp.parser.Variable ){
      return true;
    }
    return false;
  }

  private ScriptProgramObject GetChangeObject( ScriptProgramObject aObjectToChange ){
    //предполагается, что сюда пердан объект Variable
    Variable parserVar = (Variable) aObjectToChange;
    while ( (parserVar != null) && ScriptLanguageDef.IsServiceName(parserVar.GetName()) ){
      parserVar = (Variable) FParser.Next( "mp.parser.Variable" );
    }
    if ( parserVar == null ){
      return null;
    }
    return FLanguageExt.Get(parserVar.GetName());
  }

  public ScriptProgramObject First(String ... aClassName) {
    ScriptProgramObject progObj = FParser.First( aClassName );
    if ( !IsNeedToChangeObject( progObj ) ){
      return progObj;
    }
    return GetChangeObject( progObj );
  }

  public String GetSource() {
    return FParser.GetSource();
  }

  public VariableList GetVariables() {
    return  FLanguageExt.GetVariables() ;
  }

  public boolean IsMovResult() {
    return FParser.IsMovResult();
  }

  public ScriptProgramObject Next(String ... aClassName) {
    ScriptProgramObject progObj = FParser.Next( aClassName );
    if ( !IsNeedToChangeObject( progObj ) ){
      return progObj;
    }
    return GetChangeObject( progObj );
  }

  public void ParseScript(String aScript) throws ScriptException {
    FParser.ParseScript( aScript );
  }

  public void SetLanguageExt(ScriptLanguageExt aLanguageExt) throws ScriptException {
    ScriptException e = new ScriptException("Запрещается Изменять расширение языка в неисполняемом парсере");
    throw e;
  }

  public void AddExecutionContext(ExecutionContext context){
  	FContext = context;
  }

	@Override
  public ExecutionContext GetExecutionContext() {
	  return FContext;
  }

}
