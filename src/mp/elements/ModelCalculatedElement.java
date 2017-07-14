package mp.elements;


import mp.parser.*;
import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

/**
 * User: atsv
 * Date: 15.09.2006
 */
public class ModelCalculatedElement extends ModelBlockParam {
  private boolean IsElementPrepared = false;
  private ScriptParser FParser = null;
  private boolean FIsCodeExists = true;
  private ChangeListener FListener = new ChangeListener( this ) {
      public void VariableChanged(VariableChangeEvent changeEvent) {
        try {
          InputParamChanged();
        } catch (ModelException e) {
          e.printStackTrace();
        }
      }
    };
  private ModelBlockParam FKeyParam = null;
  private Vector FExecutionRecordsList = null;
  private ExecutionStructure FDefaultExecutionStructure = null;
  private ExecutionStructure FActiveExecutionStructure = null;
  private String FSwitchException = null;
  
  


  public ModelCalculatedElement(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    //FParser = new PascalParser();
    this.SetParamType( ModelBlockParam.PARAM_TYPE_INFORM );
  }

  /** "��������" ���� ���������, ���������� ������� ������������ � ������� � ���, ��� ��� ������������ � ����
   *  ���������.
   *  ����� � ������� ��������, �� �������� ������� ������ �������, ����������� ��������� ���������
   */
  private void ConnectoToInpParams( ModelElementContainer aElementList ) throws ModelException {
    int i = 0;
    ModelBlockParam element = null;
    int length =  aElementList.size();

    while ( i < length ){
      element = (ModelBlockParam)aElementList.get(i);
      element.AddInDependParams( this );
      element.AddChangeListener(FListener);
      i++;
    }

  }

  /**
   * ������� ���������� � ���� �� ���� ���������, �� ������� ��������
   * @param aElementList
   */
  private void DisconnectFromInputElements( ModelElementContainer aElementList ){
    if ( aElementList == null ){
      return;
    }
    int i = 0;
    int length = aElementList.size();
    ModelBlockParam element = null;
    while ( i < length ){
      element = (ModelBlockParam)aElementList.get(i);
      element.RemoveFromDependList( this );
      element.RemoveChangeListener( this );
      i++;
    }
  }

  /**��������� �������������� ������� � ������. ��� ����� ������������ ��������� ��������:
   * 1. ���������� ������ ���� ���������� �����
   * 2. ������ ���������� ������� � ����������� ������� ��������� ����
   * 3. ���� ������� ������ ��� ������, �� ����������� ������ ����������, ������� ������������ � ������ �������.
   *   ���������� ������ ����������� � ������ FInpParamsList
   * @param aSourceCode
   * @throws ScriptException
   * @throws ModelException
   */
  private void PrepareElement(String aSourceCode) throws ScriptException, ModelException{
    if ( IsElementPrepared ){
      //UnPrepare();
    }
    //�������� ������� ������ ���� ���������� �����-��������� ����� ���������
   // ModelElement owner = this.GetOwner();
    if ( FLanguageExt == null){
      ModelException e = new ModelException("������� ������ ���������� �������� " + this.GetName() +
              " � ������ ������������ �����");
      throw e;
    }
    FParser = ParserFactory.GetParser( FLanguageExt, aSourceCode);
    //FParser.SetLanguageExt( FLanguageExt );
    //������ �������� ���
    //FParser.ParseScript( aSourceCode );
    //��������� ������ ������� ������������ ����������
    //����������� ������ ������������ ����� ��������� ����������� ������ FInpElements - � ��� ����� ���������
    // ���������, ������� ������������ ��� ���������� ����� ���������
    ModelInpParamsIterator iterator = new ModelInpParamsIterator();
    iterator.parser = FParser;
    iterator.sourceList = this.GetOwner().GetElements();
    iterator.ownerElement = this;
    ModelAddExecutor executor = new ModelAddExecutor( iterator );
    executor.container = FInpElements;
    executor.SetUniqueFlag( true );
    executor.Execute();
    //��������� ���������� � ����
    ConnectoToInpParams( FInpElements );
    //� ����� ����� ����������� ���� ����������
    IsElementPrepared = true;
  }

  /**��������� �������� �������� ��� ��� �������. ���� �������� ��� �� ��������� � �����, ������������ � �������,
   * �� ���������� �������� ���������� �������� � ����������
   * @param aSourceCode ����������� ���
   * @throws ScriptException
   */
  public void SetSourceCode(String aSourceCode) throws ScriptException, ModelException{
    if (  aSourceCode == null) {
      ModelException e = new ModelException("������� ������� ������� ��� ������������ ����");
      throw e;
    }
    if (  aSourceCode.equalsIgnoreCase("")) {
      ModelException e = new ModelException("������� ������� ������� ��� ������������ ����");
      throw e;
    }
    if ( FParser == null){
      PrepareElement( aSourceCode );
    } else {
      String s = FParser.GetSource();
      if ( !aSourceCode.equalsIgnoreCase( s ) ) {
        PrepareElement( aSourceCode );
      }
    }
  }

  public void UpdateParam() throws ScriptException, ModelException{
    //����������� �������� �� ���� ���������: ���� �� �������� ��� ��� ����� ��������, � ������ �� ���� �������
    // ����������. ���� ��������� ���� ��� (������� FIsCodeExists), �� ���������� �������� �� ���������
    // ���� �������� ��� ����, �� ������� ����������� ������ ���� ��������������, �.�. ��� ���� �����������
    //����� ������� ��������� PrepareElement()
    if ( !IsElementPrepared && FIsCodeExists){
      ModelException e = new ModelException("������� ������ ���������� ����������������� �������� " + this.GetName());
      throw e;
    }
    if ( FSwitchException != null ){
      ModelException e = new ModelException( FSwitchException );
      throw e;
    }
    if ( FKeyParam != null ){
      if ( FActiveExecutionStructure != null ){
        try {
          FActiveExecutionStructure.Parser.ExecuteScript();
        } catch (ScriptException e) {
          ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage());
          throw e1;
        }
      }
      return;
    }
    if ( FIsCodeExists ){
      try {
      	FParser.AddExecutionContext( FExecutionContext );
        FParser.ExecuteScript();
      } catch (ScriptException e){
      	e.printStackTrace();
        ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage());
        throw e1;
      }
    }

  }

  private ExecutionStructure GetFormulaByValue( Variable aVariable ) throws ModelException{
    if ( FExecutionRecordsList == null || aVariable == null) return null;
    int i = 0;
    ExecutionStructure formula;
    int res;
    while ( i < FExecutionRecordsList.size() ){
      formula = (ExecutionStructure) FExecutionRecordsList.get( i );
      try {
        res =  aVariable.Compare( formula.SwitchValue );
      } catch (ScriptException e) {
        res = -1;
      }
      if ( res == 0 ) return formula;
      i++;
    }
    return null;
  }

  /**����� ���������� � ������, ���� ������������� �������� ������� ���� ��������.
   *
   */
  private void KeyParamChanged() {
    //System.out.println("KeyParamChanged " + FKeyParam.GetVariable().toString());
    try {
      //�������� �������, ������� ������ ����������� ��� �������� �������� ����������-�������������
      ExecutionStructure newFormula = GetFormulaByValue( FKeyParam.GetVariable() );
      if ( newFormula == null ) {
        //�������, ���������������� �������� �������� ���. ����� ��������� ������� �� ���������
        FActiveExecutionStructure = FDefaultExecutionStructure;
        InputParamChanged();
      } else {
        //������� ����
        //System.out.println("������� ����");
        if ( FActiveExecutionStructure != newFormula ){
          //������� ����������. � ����� ������ ������������� ��������� ��������:
          // - "�����������" ������� ���������� ���������� ������� (�.�. ���������, ������������ � ����������
          //   ������� �� ����� �������� ��� ����������� � ����� ���������)
          // - "������������" � ����� ������� ����������
          // - ������������� ������� ������� ������ ����� �������
          if ( FActiveExecutionStructure != null ) {
            DisconnectFromInputElements( FActiveExecutionStructure.InputParams );
          }
          FActiveExecutionStructure = newFormula;
          ConnectoToInpParams( newFormula.InputParams );
          InputParamChanged();
        }
      }
      FSwitchException = null;
    } catch (ModelException e) {
      FSwitchException = e.getMessage();
    }
  }

  private Vector GetChildNodesByName(String aNodeName) throws ModelException{
    Node paramNode = GetNode();
    NodeList nodes = paramNode.getChildNodes();
    if ( nodes == null ){
      ModelException e = new ModelException("��� �������� ��� � ��������� " + GetName());
      throw e;
    }
    int i = 0;
    Node result = null;
    Vector resultList = new Vector(10);
    while ( i < nodes.getLength() ){
      result = nodes.item(i);
      if ( aNodeName.equalsIgnoreCase( result.getNodeName() ) ){
        resultList.add( result );
      }
      i++;
    }
    return resultList;
  }

  protected Node GetChildNodeByName(String aNodeName) throws ModelException{
    Node result = null;
    Vector nodes = GetChildNodesByName( aNodeName );
    if ( nodes != null && nodes.size() >= 1 ) {
      result = (Node) nodes.get( 0 );
    }
    return result;
  }


  private Node GetChildNodeByType(Node aNode, int aNodeType) throws ModelException{
    NodeList nodes = aNode.getChildNodes();
    if ( nodes == null ){
      ModelException e = new ModelException("��� �������� ��� � ��������� " + GetName());
      throw e;
    }
    int i = 0;
    Node result = null;
    while ( i < nodes.getLength() ){
      result = nodes.item(i);
      if (  result.getNodeType() == aNodeType  ){
        return result;
      } else result = null;
      i++;
    }
    return result;
  }

  protected String GetFormulaSourceCode( Node aFormulaNode ) throws ModelException {
    Node sourceCodeNode = GetChildNodeByType(aFormulaNode, Node.CDATA_SECTION_NODE);
    if ( sourceCodeNode == null ){
      ModelException e = new ModelException("� ��������� \"" + GetName() + "\" � ����  Formula ����������� ������ � �������� �����. ���������� �������� �������� ������� CDATA" );
      throw e;
    }
    if ( sourceCodeNode.getNodeType() != Node.CDATA_SECTION_NODE ) {
      ModelException e = new ModelException("� ��������� \"" + GetName() + "\" �������� ���� �� �������� ����� CData" );
      throw e;
    }
    CDATASection sourceCode = (CDATASection)sourceCodeNode;
    return sourceCode.getData();
  }

  private void ReadSourceCodeInformation(  ) throws ModelException {
    Node formulaNode = GetChildNodeByName("Formula");
    if ( formulaNode == null ){
      //ModelException e = new ModelException("� ��������� " + GetName() + " ����������� ���� Formula");
      //throw e;
      FIsCodeExists = false;
      return;
    }
    String s = "";
    try {
      s = GetFormulaSourceCode( formulaNode );
      SetSourceCode( s );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ ��� ������ ������� � �������� \"" + GetFullName() + " \": " +  e.getMessage() );
      throw e1;
    }
  }

  private void CheckSwitchParamType() throws ModelException{
    int switchParamType = FKeyParam.GetVariable().GetType();
    if ( switchParamType == Operand.OPERAND_TYPE_ARRAY ) {
      ModelException e = new ModelException("������ � �������� \"" + GetFullName() + "\": ������ �� ����� �������������� � �������� �������������� ���������");
      throw e;
    }
    if ( switchParamType == Operand.OPERAND_TYPE_REAL ) {
      ModelException e = new ModelException("������ � �������� \"" + GetFullName() + "\": ��� real �� ����� �������������� � �������� �������������� ���������");
      throw e;
    }
  }

  private ExecutionStructure GetNewExecutionStructure( String aSourceCode, String aSwitchValue ) throws ScriptException, ModelException {
    ExecutionStructure result = new ExecutionStructure();
    result.Parser = ParserFactory.GetParser( FLanguageExt, aSourceCode);

    ModelInpParamsIterator iterator = new ModelInpParamsIterator();
    iterator.parser = result.Parser;
    iterator.sourceList = this.GetOwner().GetElements();
    iterator.ownerElement = this;

    ModelAddExecutor executor = new ModelAddExecutor( iterator );
    executor.container = result.InputParams;
    executor.SetUniqueFlag( true );
    executor.Execute();

    result.SwitchValue = (Variable) FKeyParam.GetVariable().clone();
    result.SwitchValue.SetValueWithTypeCheck( aSwitchValue );

    if ( GetFormulaByValue( result.SwitchValue ) != null ){
      ModelException e = new ModelException( " ������ � �������� \"" + GetFullName() + "\": ������� � ��������� \"" + aSwitchValue +
         "\" ��� ������������ � ������ ������" );
      throw e;
    }
    FIsCodeExists = true;
    return result;
  }

  private void PrepareExecutionRecords( Vector aNodeList ) throws ModelException, ScriptException {
    int i = 0;
    Node functionNode;
    ModelAttributeReader reader = ServiceLocator.GetAttributeReader();
    String sourceCode;
    String switchValue;
    FExecutionRecordsList = new Vector();
    while ( i < aNodeList.size() ){
      functionNode = (Node) aNodeList.get( i );
      sourceCode = ModelAttributeReader.GetSourceCode( functionNode );
      reader.SetNode( functionNode );
      switchValue = reader.GetSwitchValue();
      if ( switchValue == null || "".equalsIgnoreCase( switchValue ) ){
        //���� ��� ������ ���� ����� �� ���������
        if ( FDefaultExecutionStructure != null ){
          ModelException e = new ModelException( "������ � �������� \"" + GetFullName() + "\": � �������� ����� ���� ������ ���� ������� �� ���������" );
          throw e;
        }
        FDefaultExecutionStructure = GetNewExecutionStructure( sourceCode, switchValue );
      } else {
        FExecutionRecordsList.add( GetNewExecutionStructure( sourceCode, switchValue ) );
      }
      i++;
    }
    IsElementPrepared = true;
    //��������� �������� ���������� �� ��, ����� ��� ������� ��������� ��������� ����� UpdateParam(). � �� ��
    // ����� ����������, ���� �������� � ������ ��������� �� ������� �� �����-���� ������ ����������. �� ����,
    // ��� ������������ ������ ����������, ����-�������� ������� ��������� ������, ��� ������ �������� �����
    // ��������� ������ ���� ���, � �������� ��� � ������ ���������� �� �����.
    FKeyParam.AddInDependParams( this );
    if ( FDefaultExecutionStructure != null ){
      ConnectoToInpParams( FDefaultExecutionStructure.InputParams );
    }
    //��������, ����� ������ �������� � ��������� ���������, ����� ������� ������ �������� �������
    KeyParamChanged();
  }

  private void SetSwitchChangeListener(){
    ChangeListener listener = new ChangeListener() {
      public void VariableChanged(VariableChangeEvent changeEvent) {
        KeyParamChanged();
      }
    };
    FKeyParam.AddChangeListener( listener );
  }

  public void ApplyNodeInformation() throws ModelException{
    Node paramNode = GetNode();
    ModelAttributeReader reader = ServiceLocator.GetAttributeReader();
    reader.SetNode( paramNode );
    // ���������, �� ������ �� �������� ��������� ���� �������
    super.ApplyNodeInformation();
    String switchParamName = reader.GetSwitchParamName();
    if ( switchParamName == null || "".equalsIgnoreCase( switchParamName ) ){
      ReadSourceCodeInformation();
      return;
    }
    ModelElement element = GetOwner().Get( switchParamName );
    if ( element == null ){
      ModelException e = new ModelException(" ������ � �������� \"" + GetFullName() + "\": ���������� ����� �������� \"" + switchParamName + "\"");
      throw e;
    }
    if ( !( element instanceof ModelBlockParam ) ){
      ModelException e = new ModelException(" ������ � �������� \"" + GetFullName() +
              "\":������� \"" + switchParamName + "\" �� �������� ���������� � �����");
      throw e;
    }
    FKeyParam = (ModelBlockParam) element;
    CheckSwitchParamType();
    try {
      PrepareExecutionRecords( GetChildNodesByName("Formula") );
      SetSwitchChangeListener();
    } catch (ScriptException e) {
      ModelException e1 = new ModelException(" ������ � �������� \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;
    }

  }

  public void SetLanguageExt( ScriptLanguageExt aLanguageExt ){
    FLanguageExt = aLanguageExt;
  }

  public boolean IsNeedRuntimeUpdate() {
    if ( FKeyParam != null ){
      return true;
    }
    if ( FParser != null ){
      if ( FParser.GetSource() != null || "".equalsIgnoreCase( FParser.GetSource().trim() ) || FKeyParam != null){
        return true;
      }
    }
    return false;
  }

  protected int GetFormulaCount(){
    if ( FExecutionRecordsList == null  ) return 0;
    return FExecutionRecordsList.size();
  }

  protected boolean IsDefaultFormulaExists(){
    return FDefaultExecutionStructure != null;
  }

  private class ExecutionStructure{
    ScriptParser Parser = null;
    ModelElementContainer InputParams = new ModelElementContainer();
    Variable SwitchValue;
  }
  
  
}
