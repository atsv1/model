package mp.elements;

import java.util.Vector;

/**
 * User: atsv
 * Date: 13.01.2007
 * ����� ��������� ���������� ����� ���������� � ���������� ������ � ����� ��� ��������: ���� ���� ��������
 * � ��������� ����������. ��������������� ���������� �������� ������, � � ���� ������������ �������������
 * ���������. ��������, ��� � �������������, ������ ���� �������� � ����� ��� ��� ��������, � ���������� ������������. 
 *
 */
public class ModelOneSourceManyReciever extends ModelMultiplexorLinker {
  private boolean FListPrepareFlag = false;
  private Vector FBlockList = null;

  /**����, ��������� ��������������� � ������� ���.
   */
  private ModelBlock FPreviousBlock = null;

  /**�����������
   *
   * @param aMultiplexor - �������������, ������� ���������� ����� ������ ��������� �� ����������
   * @param aBlock - �������� ��������������, �� �� �������� ������ ��� ���������� ������
   */
  public ModelOneSourceManyReciever(ModelMultiplexor aMultiplexor, ModelBlock aBlock) {
    super(aMultiplexor, aBlock);
  }

  private void UnLinkAll( ModelBlock aBlock ) throws ModelException {
    if ( aBlock == null ){
      return;
    }
    int i = 0;
    Integer currentNameIndex = null;
    ModelInputBlockParam param = null;
    /*while ( param != null ){
      param.UnLink();
      i++;
      currentName = (String) FNamesToLink.get( i );
      param = (ModelInputBlockParam) aBlock.Get( currentName );
    }*/
    while ( i < FNamesToLink.size() ){
      currentNameIndex = (Integer) FNamesToLink.get( i );
      param = (ModelInputBlockParam) aBlock.Get( currentNameIndex );
      if ( param != null ) {
        param.UnLink();
      } else {
        ModelException e = new ModelException(" �� ���������� ������������ �������������� �� ����� \"" + aBlock.GetFullName() + "\"");
        throw e;
      }
      i++;
    }
  }

  /** ������� ���������� �������� ��������� �������������� (���������), �� ��������� �������� ��� ������ ����������
   * � �������� �������� ��������������.
   *
   * @param aParam - �������� ��������������, ��� �������� ����� ����� ��� ������ �� ��������� �������������� (���������)
   * @return - ������������ �������� ��������� ��������������, ���� null - ���� ������ ��������� ���
   */
  protected ModelBlockParam GetDependParam( ModelBlockParam aParam ){

    if ( aParam == null ){
      return null;
    }
    try{
      ModelInputBlockParam param = (ModelInputBlockParam) aParam;
      ModelBlockParam linkedParam = (ModelBlockParam) param.GetLinkedElement();
      if ( linkedParam != null ){
        ModelElement owner;
        if ( linkedParam.GetParamType() == ModelBlockParam.PARAM_TYPE_INFORM ){
          owner = linkedParam.GetOwner();
        } else {
          ModelMaterialParam mp = (ModelMaterialParam) linkedParam;
          owner = mp.GetRealOwner();
        }
        if ( owner != null && owner.GetElementId() == FMuxOwner.GetElementId() ){
          return linkedParam;
        }
      }
      return null;
    } catch (Exception e){
      return null;
    }
  }

  protected Integer GetNameIndexToLink(ModelBlockParam aMuxParam, ModelBlockParam aDependParam) {
    ModelElement param = aMuxParam.GetDependElement( 0 );
    if ( param != null ){
      return param.GetNameIndexObj();
    }
    return null;
  }

  /**����������  � ������ ������, ������� ������� �� ����������� � ��������� ��������� ��������� ��������������
   *
   * @param aLinkedMuxParam - �������� ��������������, �� ��������  
   */
  private void AddDependBlocks( ModelBlockParam aLinkedMuxParam ){
    if ( aLinkedMuxParam == null ){
      return;
    }
    if ( FBlockList == null ){
      FBlockList = new Vector();
    }
    ModelInputBlockParam param = (ModelInputBlockParam) aLinkedMuxParam.GetDependElement( 0 );
    ModelElement paramOwner;
    int i = 0;
    while ( param != null ){
      paramOwner = param.GetRealOwner();
      if ( !FBlockList.contains( paramOwner ) ){
        FBlockList.add( paramOwner );
      }
      i++;
      param = (ModelInputBlockParam) aLinkedMuxParam.GetDependElement( i );
    }
  }

  private void AddBlockToMux() throws ModelException {
    if ( FBlockList == null || FBlockList.size() == 0){
      return;
    }
    int i = 0;
    ModelBlock block;
    while ( i < FBlockList.size() ){
      block = (ModelBlock) FBlockList.get( i );
      FMux.AddSource( block );
      i++;
    }
  }

  public void BuildBlockList() throws ModelException {
    /** ���������� ������ � ������������� ������������ ������ �� ��������� ���������:
     * - �� �������� ���������� ������� �������� ���������� � ���, � ����� ���������� �������������� ��� ������������
     * - � ���������� �������������� (� ����� ������ ������ ������) �������� ���������� � ���, ����� ����� � ����
     * ���������� ������������
     * - ��� ���-�� ����� � ����� �������� � �������������  
     */
    ModelBlock muxEtalon = FMux.GetEtalon();
    if ( muxEtalon == null ){
      return;
    }
    int i = 0;
    ModelInputBlockParam inputParam = (ModelInputBlockParam) muxEtalon.GetInpParam( 0 );
    ModelBlock linkedBlock;
    ModelBlockParam muxParam;
    while ( inputParam != null ){
      linkedBlock = (ModelBlock) inputParam.GetLinkedElementOwner();
      /*if ( linkedBlock == null ){
        System.out.println("NULLLLLLLLLLLLLLLLLLLLLLL!!!!!!!!!!!!! " + inputParam.GetFullName() );
        linkedBlock = (ModelBlock) inputParam.GetLinkedElementOwner();
        ModelException e = new ModelException( "������ � �������� \"" + inputParam.GetFullName() + "\": ����������� �������������� ����" );
        throw e;
      }*/
      if ( linkedBlock != null && linkedBlock.GetElementId() == FMux.GetElementId() ){
        //������� �������� ���������� ����� ����������� � ��������������
        muxParam = (ModelBlockParam) inputParam.GetLinkedElement();
        AddDependBlocks( muxParam );
      }
      i++;
      inputParam = (ModelInputBlockParam) muxEtalon.GetInpParam( i );
    }
    AddBlockToMux();
  }

  /**������������ ���������� �������� ���������� ��������� �������������� �  ������� ���������� ���������� �����.
   *
   * @throws ModelException
   */
  public void Link() throws ModelException {
    if ( !FListPrepareFlag ){
      BuildParamsList();
      FListPrepareFlag = true;
    }
    ModelBlock reciever = FMux.GetMaxCriteriaBlock();
    if ( reciever == FPreviousBlock ){
      //��������� ���� �� ���������, �� ����� ������ ����������� �������������
      return;
    }
    UnLinkAll( FPreviousBlock );
    if ( reciever == null ){
      FPreviousBlock = null;
      return;
    }
    FPreviousBlock = reciever;
    if ( FMuxOwnerParamsList.size() == 0 ){
      return;
    }
    int i = 0;
    ModelBlockParam sourceParam = (ModelBlockParam) FMuxOwnerParamsList.get( i );
    Integer recieverParamNameIndex = (Integer) FNamesToLink.get( i );
    ModelInputBlockParam recieverParam = (ModelInputBlockParam) reciever.Get( recieverParamNameIndex );
    while ( sourceParam != null ){
      sourceParam = (ModelBlockParam) FMuxOwnerParamsList.get( i );
      recieverParamNameIndex = (Integer) FNamesToLink.get( i );
      recieverParam = (ModelInputBlockParam) reciever.Get( recieverParamNameIndex );
      if ( recieverParam == null ){
        /**����������� ������ �������� ��������, ��� ��� ������� ���������-��������� (sourceParam) � �����-��������� ���
         * ���������, ������� ������ �������������� � ����� ���������-���������.
         * �� ��������, ��� �������� �������� ������������ � ����� ��������������. � ����� ������ � ��������������
         * ������ ���� ������� �������� � ����� �� ������, ��� � � ���������-���������.
         * ������ ���� � ���������� ������������� ���: �������� ������� �������� �������������� � ����� �� ������, ���
         * � � ���������-��������� � ������������ ��� � ���������.
         */
        recieverParam = (ModelInputBlockParam)FMux.Get( sourceParam.GetName() );
        if ( recieverParam == null ) {
          ModelException e = new ModelException("������ ��� ���������� ��������� �������������� ��� ��������� \"" + sourceParam.GetFullName() + "\"" );
          throw e;
        }
      }
      recieverParam.Link( FMuxOwner,  sourceParam);
      i++;
      if ( i >= FMuxOwnerParamsList.size() ){
        break;
      }
    }//while
  }

}
