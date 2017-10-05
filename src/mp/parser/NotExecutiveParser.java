package mp.parser;

import java.util.Vector;

/**
 * User: atsv
 * Date: 05.09.2007
 *
 * ����� "������������� ������" ������������ ��� ����������� ������� �������� � ����������� ���������. ������� ���������
 * ������� ������ ������� � ���, ��� ������� � ������ ��������� ����� �������� � ����� � ��� �� ���������� - �������
 * �������������. ��� ��������� ������ ��������������� ������������ ������ ��������� ���� ��������� ������� �
 * ����������������� ����������, � ��������� ����� �������, ������� ���������� ������ � ���� �����������������
 * ���������.
 * ��� ���� ����� ��� ��� � ���������� ������ � ���� ���������.
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
      ScriptException e = new ScriptException("������ ������");
      throw e;
    }
    if ( aLanguageExt == null){
      ScriptException e = new ScriptException("������ ����������� �����");
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

  /** ������� ���������, ����� �� ������������ ���������� � ��������� ����������� ����� ��������� � ���������� ��������.
   * �� ����� ��������� ���������� ��������: ��� ���������� � ����������� ����� � � ������� ������ ��������� ���������:
   * �� ���� � �� �����.
   * ����� ���������� ������ ���� ���������� ����������.
   *
   * @param aParser
   * @param aLanguageExt
   * @return ������������ null, ���� ������ ����� ������������ � ���� ������������ �����. ����� ������������ �����
   * ������
   */
  private String IsRelativeToParser(ScriptParser aParser, ScriptLanguageExt aLanguageExt){
    VariableList parserVariables = aParser.GetVariables();
    VariableList variables = aLanguageExt.GetVariables();
    if ( parserVariables.GetNoServiceVariablesCount() != variables.GetNoServiceVariablesCount() ){
      return "�� ����� ������� �������";
    }
    int i = 0;
    Variable parserVar = null;
    Variable listVar = null;
    while ( i < variables.GetSize() ){
      parserVar = parserVariables.GetVariable( i );
      listVar = variables.GetVariable( i );
      if ( !parserVar.GetName().equalsIgnoreCase(listVar.GetName()) ){
        return "�� ��������� ����� ���������� \"" + parserVar.GetName() + "\" � \"" + listVar.GetName() + "\"";
      }
      if ( !parserVar.GetTypeName().equalsIgnoreCase( listVar.GetTypeName() ) ) {
        return "�� ��������� ���� ���������� \"" + parserVar.GetName() + "\" ";
      }
      i++;
    }
    return null;
  }

  private boolean IsExistInList( Vector aList, Variable var ){
    return aList.indexOf( var ) != -1;
  }

  /** ����� ������� ������ ���������� (�������� Variable), ������� ������������ � ���������, ����������� ������
   * �������. ������������ ��� ������ ������������ ���������� ������ �� ������ � ������ ���������� FLanguageExt.
   * ����� ������ ��������� � ���, ����� �������� ������ �������, � ������ - �������� ������ ��������� ��������
   * �������� � ����������� ������ � �������� �� ���� ��������.
   * �� ����� ����������� ������ �������������� ����� ��������:
   * 1. ��� ����������, ������� ������������ � ����������� �������, ������ ����� ������ ������ ����� ����������,
   * ������ ������� ������� � ���� ������������� ������ (������ ������ - ���������� ���� � ��������)
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
          //throw new ScriptException("� ����������� ������ ���������� ����������� ���������� \""  + currentParserVar.GetName() + "\"");
          
        }
        if ( selfVar != null && !selfVar.GetTypeName().equalsIgnoreCase( currentParserVar.GetTypeName() ) ){
          throw new ScriptException("�� ��������� ���� � ���������� \"" + currentParserVar.GetName() + "\": " + selfVar.GetTypeName() + " � " + currentParserVar.GetTypeName());
          
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

  /**����� ���� ������ ��� ��������������� ������������
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

  /** ������� ������������� ��� ����������� ������������� ������� �������, ������������� ��������, �� ������, ���������
   * � ���� ������������� �������
   *
   * @param aProgObj - ������, �������, ��������, ����� ���������
   * @return - ���������� true, ���� ������ ����� ���������
   */
  private  boolean IsNeedToChangeObject( ScriptProgramObject aProgObj ){
    if ( aProgObj != null && aProgObj instanceof mp.parser.Variable ){
      return true;
    }
    return false;
  }

  private ScriptProgramObject GetChangeObject( ScriptProgramObject aObjectToChange ){
    //��������������, ��� ���� ������ ������ Variable
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
    ScriptException e = new ScriptException("����������� �������� ���������� ����� � ������������� �������");
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
