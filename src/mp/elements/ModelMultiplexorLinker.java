package mp.elements;

import java.util.Vector;

/**
 * User: atsv
 * Date: 06.01.2007
 *
 * ������ ����������� ����� ������������ ��� ������������� ���������� ��������� �������������� � ������,
 * ������� ������ �������������.
 * ���������� ����������� ����� ����, ��� ������������� ������� �������� ���������� ����.
 * ����� Link() ������� ������ ������ ���������� ����� ���������������.  
 *
 */
public abstract class ModelMultiplexorLinker {
  protected ModelMultiplexor FMux = null;
  /**�������� ��������������. �.�. ��� ����, ������� ����� �������������� � ���������� ��������������� �����.
   * ���, ��� ������, ����� ���������� ���� �� ���������� ���������� ��� ������ ���������, FDynamicOwner ������ ����
   * ��������� ������ �� ��������. � ���� ���������� ���� �� ���������� ���������� ��� ������ ���������, ��
   * FDynamicOwner ������ ��������� ������ �� ��������
   */
  protected ModelBlock FMuxOwner = null;
  /** �������� ������  ���������� ��������� ��������������, � �������� ����� ����������� ����������
   * ���������� ��������������� ����� 
   */
  protected Vector FMuxOwnerParamsList = null;
  /**������ �������� ( ��������� �������� ) ���������� ���������� ������, � �������� ����� ����� �����������
   * ���������� ���������������  ���������� ��������� ��������������.
   * ������ FMuxOwnerParamsList � FNamesToLink ����������������. ��� ��������, ��� �������� �1  �� ������
   * FMuxOwnerParamsList ����� ��������� � ���������� �1 �� ������ FNamesToLink 
   */
  protected Vector FNamesToLink = null;

  public ModelMultiplexorLinker( ModelMultiplexor aMultiplexor, ModelBlock aBlock ){
    FMux = aMultiplexor;
    FMuxOwner = aBlock;
    FMuxOwnerParamsList = new Vector();
    FNamesToLink = new Vector();
  }

  public abstract void Link() throws ModelException;

  protected abstract ModelBlockParam GetDependParam( ModelBlockParam aParam );

  protected abstract Integer GetNameIndexToLink( ModelBlockParam aMuxParam, ModelBlockParam aDependParam );

  /**����� ���������� ���������� � ������������� ���������� ������ - ��� ����, ����� ������������� ��� ��������
   * ���� �� ��� � ��������� �� ����� ����������.
   * 
   */
  public abstract void BuildBlockList() throws ModelException;

  /**��� ������ ������� ������ ������������ ���������� ������� ����������� ����������. � ����� �� ������� ����������
   * ��������� ��������� ��������������, ������� ���������� ��������� � ��������� ������. � ������ ������ �����
   * ��������� �������� ���������� ����������� �����.
   * ����� ����� ������ ������������ �����  ������� ��������� ���������� �������������� ���������� ���������
   * �������������� � ���������� �����. 
   */
  public void BuildParamsList( ){
    /**������������ ������� ���� ������� ���������� �������������� ��� ���� ����� ����������, . 
     * ������� ������ ������� ���������� ��������������  ������� �� ��������� ������:
     * 1. ������� ��������� �������������� �������� ������� ���� �������� ���������� ���������� �����.
     * 2. �������� ��������� ���������� ������ ����������� � �������� ����������� �������������� �� ����� ��������
     * ������.
     * 3. ��������� �.2: ���� ������� ��������� ���������� ������ ����������� � �������� ����������� �������������� 
     */
    FMuxOwnerParamsList.clear();
    FNamesToLink.clear();
    ModelBlockParam muxParam = FMux.GetInpParam(0);
    ModelBlockParam dependParam = null;
    int i = 1;
    while ( muxParam != null ){
      dependParam = GetDependParam( muxParam );
      if ( dependParam != null ){
        Integer ind = GetNameIndexToLink(muxParam, dependParam);
        if ( ind != null ){
          FMuxOwnerParamsList.add( dependParam );
          FNamesToLink.add( ind );
        }
      }
      muxParam = FMux.GetInpParam(i);
      i++;
    }
  }

  public int GetDestParamsCount(){
    return FMuxOwnerParamsList.size();
  }

}
