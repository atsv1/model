package mp.elements;

/**
 * User: atsv
 * Date: 06.01.2007
 *
 * ������ ����� ���������� ���������� ������ �������������� � ����� ������. �.�. ����������� �������� ����-��������,
 * ��������� ��������������� � ����-��������, ����������� � ���������������.
 * ������� �������, ������������ ���������� ������ �� ������ ���������� � ����� ����������, "����������� �� ��������". 
 */
public class ModelMuxBlockLinker extends ModelMultiplexorLinker {
  private boolean FListPrepareFlag = false;
  private ModelBlock FPreviousBlock = null;

  /** �����������
   *
   * @param aMultiplexor - �������������, ������� ����� �������� ������ ����.
   * @param aBlock - �������� ��������������
   */
  public ModelMuxBlockLinker(ModelMultiplexor aMultiplexor, ModelBlock aBlock) {
    super(aMultiplexor, aBlock);
  }

  protected ModelBlockParam GetDependParam( ModelBlockParam aParam ){
    ModelBlock muxOwner = FMux.GetMuxOwner();
    if ( muxOwner == null ){
      return null;
    }
    ModelInputBlockParam dependParam = (ModelInputBlockParam) muxOwner.GetInpParam( 0 );
    int i = 0;
    while ( dependParam != null ){
      if ( aParam.IsDependElement( dependParam ) ){
        return dependParam;
      }
      i++;
      dependParam = (ModelInputBlockParam) muxOwner.GetInpParam( i );
    }
    return null;
  }

  protected Integer GetNameIndexToLink(ModelBlockParam aMuxParam, ModelBlockParam aDependParam) {
    //return null;
    return aMuxParam.GetNameIndexObj();
  }

  public void BuildBlockList() throws ModelException {
    /**���������� ���������� ������ � �������������.
     * ����������� ������ ����� ������ ������ � ��������� ������ (��������� ����������)
     */
    ModelBlock etalon = FMux.GetEtalon();
    if ( etalon == null ){
      return;
    }
    String etalonName = etalon.GetName();
    FMux.AddAllBlock( etalonName );
    
  }

  public void Link() throws ModelException{
    if ( !FListPrepareFlag ){
      BuildParamsList();
      FListPrepareFlag = true;
    }
    if ( FMuxOwnerParamsList.size() == 0 ){
      return;
    }
    ModelBlock source = FMux.GetMaxCriteriaBlock();
    if ( source != null &&  FPreviousBlock != null && source.GetElementId() == FPreviousBlock.GetElementId() ){
      //��������� ���� �� ���������, ������ ������� �� ������
      return;
    }
    FPreviousBlock = source;
    int i = 0;
    ModelInputBlockParam currentParam = (ModelInputBlockParam) FMuxOwnerParamsList.get(0);
    Integer nameIndex = null;
    while ( currentParam != null ){
      currentParam.UnLink();
      if ( source != null ){
        nameIndex = (Integer) FNamesToLink.get(i);
        currentParam.Link( source, (ModelBlockParam)source.Get( nameIndex ) );
      }
      i++;
      if ( i < FMuxOwnerParamsList.size() ){
        currentParam = (ModelInputBlockParam) FMuxOwnerParamsList.get(i);
      } else currentParam = null;
    }//else
  }

 
}
