package mp.elements;

import mp.parser.*;
import mp.utils.ModelAttributeReader;

/**
 * User: atsv
 * Date: 18.09.2006
 * ������ ����� ������������ ��� ��������� ���������� �� ������� �����.
 */
public class ModelInputBlockParam extends ModelBlockParam{
  private ModelBlock FLinkedBlock = null;
  /**������� �� ������� �����, � �������� ���� ��������� ����������� ������ ���������
   */
  private ModelBlockParam FLinkedElement = null;
  private ModelElement FRealOwner = null;
  /**���������� �� �������� FLinkedElement. �������� � ���� ��������� ���������� ������ ��� �������� �������
   */
  private Variable FSourceVar = null;

  public ModelInputBlockParam(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
    FRealOwner = aOwner;
    this.SetParamType( ModelBlockParam.PARAM_TYPE_INFORM );
  }

  protected void UpdateParam() throws ScriptException, ModelException {
    if ( FLinkedElement == null ){
      return;
    }
    this.GetVariable().StoreValueOf( FSourceVar );
  }

  public boolean IsNeedRuntimeUpdate() {
    return true;
  }

  public ModelElement GetOwner(){
    return FLinkedBlock;
  }

  protected void UpdateLink(ModelBlock aNewBlock,  ModelBlockParam aNewElement) throws ModelException{
    if ( aNewElement == null){
      ModelException e = new ModelException("������� ������� ����� � ������ ��������� � �������� " + this.GetFullName());
      throw e;
    }
    FLinkedElement = aNewElement;
    FLinkedBlock = aNewBlock;
    FSourceVar = aNewElement.GetVariable();
  }

  protected void SetLinkedElementToNull(){
    FLinkedBlock = null;
    FLinkedElement = null;

  }

  protected void UnLink() throws ModelException {
    if ( FLinkedElement == null ){
      LoadInitValue();
      return;
    }
    FLinkedElement.GetVariable().RemoveChangeListener( this );
    FLinkedElement.RemoveFromDependList( this );
    SetLinkedElementToNull();
    LoadInitValue();
  }

  /**����� ��������� ����������� � ���������� � ���������� ��������. ��� "������������" ����� ���������� ���������:
   * ������ �� ���������, � �������� ����������� �����������,
   *
   * @param aLinkOwner - ��������� ��������� ���� ���������, � �������� ����������� �����������
   * @param aElement - �������, � �������� ����� ����������� �����������.
   * @throws ModelException
   */
  public void Link(ModelBlock aLinkOwner, ModelBlockParam aElement) throws ModelException{
    FLinkedBlock = aLinkOwner;
    FLinkedElement = aElement;
    ModelException e;
    if ( FLinkedElement == null ){
      e = new ModelException("������� ������� ����� � ������ ��������� � �������� " + this.GetFullName());
      throw e;
    }
    FSourceVar = FLinkedElement.GetVariable();
    FLinkedElement.AddInDependParams( this );
    ChangeListener listener = new ChangeListener(this) {
      public void VariableChanged(VariableChangeEvent changeEvent)  {
        try {
          InputParamChanged();
        } catch (ModelException e1) {
          e1.printStackTrace();
        }
      }
    };
    aElement.AddChangeListener( listener );
    try {
      UpdateParam();
    } catch (ScriptException e1) {
      //e1.printStackTrace();
      e = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e1.getMessage());
    }
  }

  protected ModelElement GetLinkedBlock( ModelElementDataSource elementSource ) throws ModelException {
    String blockName = elementSource.GetLinkedBlockName();
    if ( blockName == null){
      return null;
    }
    ModelElement result = null;
    Model model = null;
    String modelName = elementSource.GetLinkedModelName();
    if ( modelName == null || "".equalsIgnoreCase( modelName ) ){
      model = (Model) FRealOwner.GetOwner();
    } else {
      // ������� �������� ������, � ������� ���������� ����������. �������� �������� ������ �� ��� ������
      ModelExecutionManager manager = ModelExecutionContext.GetManager( modelName );
      if ( manager == null ){
        ModelException e = new ModelException( "������ � �������� \"" + GetFullName() + "\": " +
                "����������� ������ \"" + modelName + "\"" );
        throw e;
      }
      model = (Model) manager;
    }
    String blockIndex = elementSource.GetBlockLinkIndex();
    if ( blockIndex == null || "".equalsIgnoreCase( blockIndex ) ){
      return model.Get( blockName) ;
    }
    int intBlockIndex = 0;
    if ( "selfIndex".equalsIgnoreCase( blockIndex ) ){
      ModelBlockParam selfIndexParam = (ModelBlockParam) FRealOwner.Get("selfindex");
      try {
        intBlockIndex = selfIndexParam.GetVariable().GetIntValue();
      } catch (ScriptException e) {
        //e.printStackTrace();
        ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage());
        throw e1;
      }
      return model.Get( blockName, intBlockIndex );
    }
    String error;
    String s;
    try{
      intBlockIndex = Integer.parseInt( blockIndex );
    } catch (Exception e) {
      error = e.getMessage();
      /*ModelException e1 = new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage());
      throw e1;*/
      //s = elementSource.GetConstantValue( blockIndex );
      s= BuildContext.getBuildContext().getConstantValue(blockIndex);
      if ( s == null ){
        throw new ModelException("������ � �������� \"" + GetFullName() + "\": " + e.getMessage());        
      }
      intBlockIndex = Integer.parseInt( s );
    }

    result = model.Get( blockName, intBlockIndex );
    return result;
  }

  private void ReadLinkInfo(ModelElementDataSource elementSource  ) throws ModelException{
    String blockName = elementSource.GetLinkedBlockName();
    String paramName = elementSource.GetLinkedParamName();
    if ( blockName == null && paramName == null ){
      return;
    }
    if ( blockName != null && paramName == null ){
      ModelException e = new ModelException("������ �� ������� ����������. � ��������� " + GetName() +
              " ������������ �������� �����, �� ���������� �������� ���������");
      throw e;
    }
    if ( blockName == null  ){
      ModelException e = new ModelException("������ �� ������� ����������.  � ��������� " + GetName() +
              "������������ �������� ����������, �� ����������� �������� �����");
      throw e;
    }
    ModelElement block = GetLinkedBlock( elementSource );
    if ( block == null ){
      ModelException e = new ModelException("������ � �������� \"" + GetFullName() + "\": ������������� ���� \"" +
              blockName + "\"");
      throw e;
    }
    ModelElement param = block.Get( paramName );
    Link((ModelBlock)block,(ModelBlockParam)param);
  }

  /**����� ������ ���������� �� ����� ������.
   * � ���� ������ (��� � �������, ������� ���������� �� ����� ������) ����������� ������ �����������
   * ����� Link(). ��� �������� � ����������� ������� ������. ��� ����� ��� ����, ����� ��������� ������� �������������,
   * ������ - ��������� ����������� ��� ����������� � ��������������.
   * @throws ModelException
   */
  public void ApplyNodeInformation() throws ModelException{
  	super.ApplyNodeInformation();     
     ReadLinkInfo( elementSource );
  }

  public ModelElement GetRealOwner(){
    return FRealOwner;
  }

  public ModelElement GetLinkedElement(){
    return FLinkedElement;
  }

  public ModelElement GetLinkedElementOwner(){
    return FLinkedBlock;
  }

}
