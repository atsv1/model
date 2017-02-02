package mp.elements;

import mp.parser.*;
import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * User: atsv
 * Date: 11.11.2006
 *
 *  ����� ������������ ��� ���������� ��������� ���������, ������� ������ � ���� �����������(������������)
 * �������, � ������� �� ������ ModelCalculatedElement, ������� ��������� � ��������������� (��������������)
 * ���������. ������� ������� ����� ������������ � �������������� ����������� ������� � �������� ������ �����������.
 * ������������� ��������� ������ �������� �������� �� �������������� ����������, � ��� ����� �� ������ �� ��������,
 * ���������� � ����������-����������.
 * ����� ������� ����� ������������ ����������� ���������� ��-�������. ��� �������� �������� �����
 * ����������-���������� � ����������-����������, �������� � ���������-��������� ����������� �� ���������� ����������.
 */
public class ModelMaterialParam extends ModelInputBlockParam {
  private ModelAttributeReader FAttrReader = ServiceLocator.GetAttributeReader();
  private ModelMaterialParam FSourceElement = null;
  /**���������� ������ ���������� �� ������ ������. ������������ ������ � ���������
   */
  private Variable FEnableFlag = null;
  /**���������� ������ ��������, ������� ��������� (false) ���� ��������� (true) ����� �������. ��� ����������
   * ������ ��� �� ��������, ��� � �� ��������.
   * �������������� ���������� � true � ������ ApplyNodeInformation()
   */
  private boolean FEnableTransfer = false;

  private Variable FRecieveQuantityVar = null;
  private double FRecieveQuantity = -1;
  private boolean FIsResieveQuantitySectionExist = false;

  private boolean FIsOutgoingSectionExist = false;
  private Variable FOrderQuantityVar = null;
  private ModelServiceParam FOutgoingQuantityParam = null;
  private ModelCalculatedElement FInnerFormula = null;

  private ModelServiceParam FIncomingValueParam = null;

  public ModelMaterialParam(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    this.SetParamType( ModelBlockParam.PARAM_TYPE_MATERIAL );
  }

  public void SetTransferFlag( boolean aFlagValue ){
    FEnableTransfer = aFlagValue;
  }

  private double CalculateTransferValue( double aOrderValue ) throws ModelException, ScriptException {
    if ( FIsOutgoingSectionExist ) {
      FOrderQuantityVar.SetValue( aOrderValue );
      try{
        FOutgoingQuantityParam.ServiceUpdateParam();
        return FOutgoingQuantityParam.GetVariable().GetFloatValue();
      } catch (ScriptException e){
        ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage());
        throw e1;
      }
    }
    if ( aOrderValue == -1 ){
      return GetVariable().GetFloatValue();
    }
    double result = aOrderValue;
    return result;
  }

  private static String GetOutgoingScript(Node aNode){
    NodeList nodes = aNode.getChildNodes();
    Node currentNode = null;
    int i = 0;
    while ( i < nodes.getLength() ){
      currentNode = nodes.item( i );
      if ( currentNode.getNodeType() == Node.CDATA_SECTION_NODE ){
        return currentNode.getNodeValue();
      }
      i++;
    }
    return null;
  }

  private void ReadOutgoingSection( Node aNode ) throws ModelException {
    FAttrReader.SetNode( aNode );
    String outgoingVarName = FAttrReader.GetValueAttr();
    String orderVarName = GetName() + "_orderQuantity";
    ModelBlock owner = (ModelBlock) GetRealOwner();
    ScriptLanguageExt ext = owner.GetLanguageExt();
    //�������������� ��������, � ������� ����� ��������� ����������, ������� ����� �������� �������� �� ���������
    ModelCalculatedElement param = new ModelCalculatedElement(owner, orderVarName, ServiceLocator.GetNextId());
    param.SetVarInfo("real", "0");
    FOrderQuantityVar = param.GetVariable();
    try {
      ext.AddVariable( FOrderQuantityVar );
      param.SetLanguageExt( ext );

      owner.AddInnerParam( param );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() +
              "\" ��� ��������� ������ OutgoingQuantity: " + e.getMessage());
      throw e1;
    }

    FOutgoingQuantityParam = new ModelServiceParam( owner, outgoingVarName, ServiceLocator.GetNextId() );
    FOutgoingQuantityParam.SetVarInfo("real", "0");
    try {
      ext.AddVariable( FOutgoingQuantityParam.GetVariable() );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() +
              "\" ��� ��������� ������ OutgoingQuantity: " + e.getMessage());
      throw e1;
    }
    FOutgoingQuantityParam.SetLanguageExt( ext );
    owner.AddInnerParam( FOutgoingQuantityParam );
    try {
      FOutgoingQuantityParam.SetSourceCode( GetOutgoingScript(aNode) );
      param.SetSourceCode( "[" + orderVarName + "]" + " := " + "[" + orderVarName + "]" + ";" );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() +
              "\" ��� ��������� ������� � ������  OutgoingQuantity: " + e.getMessage());
      throw e1;
    }
    /**��������� FOutgoingQuantityParam � ������ ����������, ������� �� ������ ����������� ����������� ��������.
     * ���� � ���, ��� ���� ���� �������� �� �������� � ���� ������, �� �� ����� ����������� ������� ��������, � ������
     * ����������� ������ �����, ����� ������������ �������� - �������� ���������� ������ ������ � ����������.
     */
    owner.AddToNotUpdatedList( FOutgoingQuantityParam );
  }

  protected Variable GetOrderQuantityVar(){
    return FOrderQuantityVar;
  }

  protected Variable GetOutgoingVar(){
    return FOutgoingQuantityParam.GetVariable();
  }

  /**������� ���������� �� ����������, ������� �������� ����� ������ ���������. ������������ ��������������
   * ���������� �������� � ��������� �� ������������ ��������. ����� ���������� ��� ���������-���������
   * @param aOrderValue - ����������, ������� ����� �������� �������� �� ���������. ���� -1, ���� ��������
   * ����� �������� ���
   * @return - ��������, ������� �������� ����� �������� � ��������. ������ �� ��� �������� ���������� ���������� �
   * ��������� � ���������� ���������� � ���������
   */
  public double GetTransferValue( double aOrderValue ) throws ScriptException, ModelException {
    double result = 0;

    /*int selfIndex = ( (ModelBlock)this.GetRealOwner() ).GetIntValue("selfIndex");
    if ( GetName().equalsIgnoreCase("�������� ����� �� �����������") && selfIndex  ==99){
      System.out.println("GetTransferValue " + GetFullName() + " orderValue = " + Double.toString( aOrderValue ) +
              " transferValue = " + Double.toString( result ) + " selfIndex = " + Integer.toString( selfIndex ) +
         " currentValue = " + GetVariable().toString()
      );
    }*/
    if ( !FEnableTransfer ){
      return 0.0;
    }

    result = CalculateTransferValue( aOrderValue );

    double currentValue = GetVariable().GetFloatValue();
    double oldValue = currentValue;
    currentValue = currentValue - result;
    if ( currentValue < 0 ){
      result = oldValue;
      GetVariable().SetValue( 0.0 );
    } else {
      GetVariable().SetValue( currentValue );
    }
    //log
   /* int selfIndex = ( (ModelBlock)this.GetRealOwner() ).GetIntValue("selfIndex");
    if ( GetName().equalsIgnoreCase("�������� ����� �� �����������") && selfIndex  == 99){
      System.out.println("GetTransferValue " + GetFullName() + " orderValue = " + Double.toString( aOrderValue ) +
              " transferValue = " + Double.toString( result ) + " selfIndex = " + Integer.toString( selfIndex ) +
         " currentValue = " + GetVariable().toString()
      );
    }*/
    return result;
  }

  /**������� ���������� �������� �����, ������� ��������� ������ ������ ������� � ����������.
   * ���������� ���� ������� ������ ������� �� ���������-���������
   * @return - true, ���� ����� ��������. ����� - false
   */
  private boolean IsTrasferEnabled(){
    if ( !FEnableTransfer  ) {
      return false;
    }
    if ( FEnableFlag == null ) {
      return true;
    }
    return FEnableFlag.GetBooleanValue();
  }

  /**������� ���������� ����������, ������� ����� �������� �������� �� ���������. ������� ���������� ��� ���������
   * ����� ������� ������
   *
   * @return  ���������� -1, ���� �������� ����� �������� ���, ��� ���������� � ���������, ����� ������������
   * �����-���� ��������
   */
  private double GetTransferOrderValue() throws ScriptException {
    if ( !FIsResieveQuantitySectionExist ){
      return -1;
    }
    if ( FRecieveQuantityVar == null ){
      return FRecieveQuantity;
    } else {
      return FRecieveQuantityVar.GetFloatValue();
    }
  }

  private void RecieveDataFromSource() throws ScriptException, ModelException {
    if (/*FSourceElement == null*/ GetLinkedElement() == null ){
      return;
    }
    //��������� - �������� �� �����?
    if ( !IsTrasferEnabled() ){
      return;
    }
    FSourceElement = (ModelMaterialParam) GetLinkedElement();
    //System.out.println("transfer begin " + GetFullName() );
    double transferValue = FSourceElement.GetTransferValue( GetTransferOrderValue() );
    double currentValue = GetVariable().GetFloatValue();
    GetVariable().SetValue( transferValue + currentValue );
    /*������������ �������� ������ ������ �����������, ��� ����������� �� ����, ��������� �� ����������, ��� ��
    ���������. ��� ������� ���, ��� � ������������ ��������� ����� ���� ����������� ������� �� ����� � ������, �������
    ����� ����������� ���� ����� ������� ������.
    ���� �� �������� ����� ����������� ������ ����� ������������ ������, �� ��� �������� � ����, ��� ��� �������
    ������ ����������� �� ������, ����� ��� �����, ����� ��� ����������� ������
    * */
    InputParamChanged();
    /*if ( transferValue != 0 ) {
      InputParamChanged();
    }*/
    if ( FIncomingValueParam != null ){
      FIncomingValueParam.GetVariable().SetValue( transferValue );
      FIncomingValueParam.ServiceUpdateParam();
    }
    //log code
    /*int selfIndex = ( (ModelBlock)this.GetRealOwner() ).GetIntValue("selfIndex");
    System.out.println("transfer end " + GetFullName() + " transferValue = " + Double.toString( transferValue )
       + " paramValue " + Double.toString(transferValue + currentValue) + " selfIndex = " + Integer.toString( selfIndex ));*/
  }

  protected void UpdateParam() throws ScriptException, ModelException {
    //log
   /* int selfIndex = ( (ModelBlock)this.GetRealOwner() ).GetIntValue("selfIndex");
    if ( GetName().equalsIgnoreCase("�����") && selfIndex  >= 0 ){
      System.out.println("update param" );
    }*/
    //������� �������� ������ �� ���������
    if ( !this.IsExecuteListInjected() ){
      ModelBlock owner = (ModelBlock) GetRealOwner();
      owner.InjectExecListToParam( this );
    }
    //������� �������� ������ �� ���������
    RecieveDataFromSource();
    if ( FInnerFormula != null ){
      FInnerFormula.Update();
    }
  }

  public void  Update() throws ScriptException, ModelException{
    UpdateParam();
  }

  protected void UnLink() throws ModelException {
    if ( FSourceElement == null ){
      return;
    }
    FSourceElement.GetVariable().RemoveChangeListener( this );
    FSourceElement.RemoveFromDependList( this );
    FSourceElement = null;
    SetLinkedElementToNull();
  }

  private void ReadLinkInfo(  ) throws ModelException{
    ModelElement owner = this.GetRealOwner();
    if ( owner == null ){
      ModelException e = new ModelException("����������� �������-�������� � �������� \"" + this.GetFullName() + "\"");
      throw e;
    }
    String paramName = FAttrReader.GetLinkedParamName();
    if ( paramName == null ){
      return;// ���������-��������� ����� � �� ����
    }
    String blockName = FAttrReader.GetLinkedBlockName();
    if ( blockName == null ){
      //�������������� � �������� ����� �� �����
      ModelBlockParam element = (ModelBlockParam) owner.Get( paramName );
      if ( element == null ){
        ModelException e = new ModelException("����������� ������� \"" + paramName + "\" (�������� ��� �������� \"" + this.GetFullName() + "\")");
        throw e;
      }
      if ( element.GetParamType() != ModelBlockParam.PARAM_TYPE_MATERIAL ){
        ModelException e = new ModelException("������ � �������� \"" + GetFullName() + "\": �������� " + paramName +
                " �� �������� ������������ ����������");
        throw e;
      }
      FSourceElement = (ModelMaterialParam) element;
      Link((ModelBlock) this.GetRealOwner(), FSourceElement);
      return;
    }
    // ���������� �������� �����.
    ModelBlock linkedBlock = (ModelBlock) GetLinkedBlock( FAttrReader );
    if ( linkedBlock == null ){
      ModelException e = new ModelException("������ � �������� \"" + GetFullName() + "\": ����������� ���� " + blockName);
      throw e;
    }
    ModelBlockParam element = (ModelBlockParam) linkedBlock.Get( paramName );
    if ( element == null ){
      ModelException e = new ModelException("����������� ������� \"" + paramName + "\" (�������� ��� �������� \"" + this.GetFullName() + "\")");
      throw e;
    }
    if ( element.GetParamType() != ModelBlockParam.PARAM_TYPE_MATERIAL ){
      ModelException e = new ModelException("������ � �������� \"" + GetFullName() + "\": �������� " + paramName +
              " �� �������� ������������ ����������");
      throw e;
    }
    FSourceElement = (ModelMaterialParam) element;
    Link( linkedBlock , FSourceElement);
  }

  protected void ReadVariableInfo( ModelAttributeReader aAttrReader ) throws ModelException{
    Variable var = this.GetVariable();
    if ( var != null ){
      ModelException e = new ModelException("������� ���������� �������� ���������� � �������� \"" + this.GetFullName() + "\"");
      throw e;
    }
    String typeName = aAttrReader.GetValueType();
    if ( typeName == null || "".equals(typeName) ) {
    	typeName = "integer"; 
    }
    String initValue = aAttrReader.GetAttrInitValue();
    SetVarInfo( typeName, initValue );
  }

  private void ReadIncomingScript( Node aNode ) throws ModelException {
    ModelBlock owner = (ModelBlock) this.GetRealOwner();
    FIncomingValueParam = new ModelServiceParam(owner, "incomingValue_" + GetName(), ServiceLocator.GetNextId());
    FIncomingValueParam.SetVarInfo("real", "0");

    owner.AddInnerParam( FIncomingValueParam );
    ScriptLanguageExt ext = owner.GetLanguageExt();
    try {
      ext.AddVariable( FIncomingValueParam.GetVariable() );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;
    }
    String sourceCode = GetOutgoingScript( aNode );
    FIncomingValueParam.SetLanguageExt( ext );
    try {
      FIncomingValueParam.SetSourceCode( sourceCode );
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;
    }
  }

  private void ReadSection(Node aNode) throws ModelException {
    if ( aNode == null ){
      return;
    }
    ModelBlock owner = (ModelBlock) GetRealOwner();
    if ( aNode.getNodeName().equalsIgnoreCase("RecieveDataFlag") ){
      FAttrReader.SetNode( aNode );
      ModelBlockParam enableParam = (ModelBlockParam) owner.Get( FAttrReader.GetValueAttr() );
      if ( enableParam == null ){
        ModelException e = new ModelException("������ � �������� \"" + GetFullName() + "\": ����������� �������� " +
                FAttrReader.GetValueAttr());
        throw e;
      }
      FEnableFlag = enableParam.GetVariable();
      enableParam.AddChangeListener( new ChangeListener() {
        public void VariableChanged(VariableChangeEvent changeEvent) {
          try {
            InputParamChanged();
          } catch (ModelException e) {
            e.printStackTrace();
          }
        }
      } );
      if ( !FEnableFlag.GetTypeName().equalsIgnoreCase("boolean") ) {
        ModelException e = new  ModelException( "������ � �������� \"" + GetFullName() +
                "\": � ������ RecieveDataFlag ������ ���� ���������� ����������� ���� " );
        throw e;
      }
      return;
    }
    if ( aNode.getNodeName().equalsIgnoreCase("RecieveQuantity") ){
      FIsResieveQuantitySectionExist = true;
      FAttrReader.SetNode( aNode );
      String s = FAttrReader.GetValueAttr();
      try {
        FRecieveQuantity = Double.parseDouble( s );
        return;
      } catch (Exception e){}
      ModelBlockParam quantityParam = (ModelBlockParam) owner.Get( s );
      if ( quantityParam == null ){
        ModelException e = new ModelException("������ � �������� \"" + GetFullName() + "\": ����������� �������� " +
                s );
        throw e;
      }
      FRecieveQuantityVar = quantityParam.GetVariable();
      return;
    }
    if ( aNode.getNodeName().equalsIgnoreCase("OutgoingQuantity") ){
      ReadOutgoingSection( aNode );
      FIsOutgoingSectionExist = true;
    }
    if ( aNode.getNodeName().equalsIgnoreCase("Formula") ){
      ReadInnerFormula( aNode );
    }
    if ( aNode.getNodeName().equalsIgnoreCase("IncomingCode") ){
      if ( this.GetParamPlacementType() != ModelBlockParam.PLACEMENT_TYPE_INPUT ){
        ModelException e = new ModelException("������ � ��������� \"" + GetFullName() +
           "\": ������ incomingValue ����� ���� ������ �� ������� ����������");
        throw e;
      }
      ReadIncomingScript( aNode );
    }
  }

  private void ReadInnerFormula(Node aNode) throws ModelException {
    ModelBlock owner = (ModelBlock) this.GetRealOwner();
    FInnerFormula = new ModelCalculatedElement( owner, this.GetName(), ServiceLocator.GetNextId() );
    FInnerFormula.SetVariable( GetVariable() );
    FInnerFormula.SetNode( aNode.getParentNode() );
    FInnerFormula.SetLanguageExt( owner.GetLanguageExt() );
    FInnerFormula.ApplyNodeInformation();
  }

  private void ReadAdditionalSections() throws ModelException {
    NodeList nodes = GetNode().getChildNodes();
    if ( nodes == null ){
      return;
    }
    int i = 0;
    Node currentNode;
    while ( i < nodes.getLength() ){
      currentNode = nodes.item( i );
      if ( currentNode.getNodeType() == Node.ELEMENT_NODE ) {
        ReadSection( currentNode );
      }
      i++;
    }
  }

  public void ApplyNodeInformation() throws ModelException{
  	super.ApplyNodeInformation();
    FEnableTransfer = false;
    FAttrReader.SetNode( this.GetNode() );
    ReadLinkInfo( );
    ReadAdditionalSections();
    FEnableTransfer = true;

  }

}
