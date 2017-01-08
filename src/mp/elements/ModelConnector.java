package mp.elements;

import mp.parser.Variable;


/**����������� �����, ������� ������������ ����� ������� ����� ������� � ����������� ������������.
 *
 * User: atsv
 * Date: 30.09.2006
 */
public interface ModelConnector {

  /**������� ���������� �������� �� ���������, ������������� � ���������� ������
   *
   * @param aBlockName - �������� �����, ������������ ��������
   * @param aBlockIndex - ������ �����. ���� ������ ����� -1, �� ��� ��������, ��� ����� ����� �������������� ���
   * �������. �.�. ���� ������ � ����� ������ � ������ �����, �� �������� ������ �� �����, ��������� ������ �� ������
   * ����������, �� ������ ������ ����� ����� �������� ��������
   * @param aParamName - �������� ���������.
   * @return - ������������ �������� ��������
   * @throws ModelException
   */
  //public abstract double GetValue(String aBlockName, int aBlockIndex, String aParamName) throws ModelException;

  public abstract double GetValue(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException;

  public abstract double GetValue(ModelAddress address) throws ModelException;

  public abstract boolean GetBooleanValue(ModelAddress address) throws ModelException;

  public abstract int GetIntValue(ModelAddress address) throws ModelException;

  public abstract String GetStringValue(ModelAddress address) throws ModelException;

  public abstract String GetStringValue(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException;

  public abstract void StartModel() throws ModelException;

  public abstract void StopModel();

  public abstract void PauseModel();

  public abstract void ResumeModel();

  public abstract String GetErrorString();

  /**������� ������ ���������� ���������� ������ �����.
   *
   * @param aBlockIndexValue - ��������, ������ �� �������� "blockindex" ����� �������� �����. ����� ��������� ���
   * ���������� �������� ��������, ��� � �������� ����� "self", "any". ����� "all" ���������.
   * @return - ������ �����. ���� ������������ -1, �� � ����� ����� ����������� ��������� ��� �������.
   */
  public abstract int GetBlockIndex(String aBlockIndexValue);

  /**���������� ���������� ������ � ������, � ������� ��� ��������� � ������, �������� � ���������
   *
   * @param aBlockName - �������� ������, ���������� ������� ���������� ��������
   * @return - ���������� ������
   * @throws ModelException
   */
  public abstract int GetBlockCount(String aModelName, String aBlockName) throws ModelException;

  public abstract void SendValue( double aValue,  String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;
  public abstract void SendValue( boolean aValue, String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;

  /** ������� �������� ����������� ������������� � ���������� ������� ������.
   *  �������� ������������ � ������ ��������������� ������������, ���� ��������, � �������� ������������ �����������.
   * @param aBlockName
   * @param aBlockIndex
   * @param aParamName
   * @return ������������ true, ���� ����������� ��������, false - ���� ����������� ����������
   */
  public abstract boolean IsConnectionEnabled(String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;

  /** ������� �������� ����������� ���������� ������ ��������� ������. �����������, ����� �� ������ ��������������
   * ������������ ���������� �������� � ���� �������� ������.
   *
   * @param aBlockName
   * @param aBlockIndex
   * @param aParamName
   * @return
   */
  public abstract boolean IsManagingEnabled(String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;

  public abstract int GetValueType(String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;

  public abstract void FireBlockEvent(String aBlockName, int aBlockIndex, String aEventName) throws ModelException;

  /** ������� ���������� ��������, ���������� � ������ ���������, � �������� �� ������, ����������� ����������
   * �����������
   *
   * @param aVarToCompare
   * @param aBlockName
   * @param aBlockIndex
   * @param aParamName
   * @return 0 - ���� �������� �����, 1 - ���� �������� � ������� ������, ��� �������� � ���������, -1 - ����
   *          �������� � ��������� ������, ��� � �������
   * @throws ModelException
   */
  public abstract int Compare( Variable aVarToCompare, String aModelName, String aBlockName, int aBlockIndex, String aParamName  ) throws ModelException;

  public abstract boolean IsArray( ModelAddress address ) throws ModelException;

  public abstract int GetArrayDimensionCount( ModelAddress address ) throws ModelException;

  public abstract int GetArrayDimensionLength( ModelAddress address, int dimension ) throws ModelException;

  public abstract double GetArrayValue( ModelAddress address, int[] coordinates ) throws ModelException;

  public abstract boolean IsHistoryExists(ModelAddress address) throws ModelException;

  public abstract String GetHistoryStringValue(ModelAddress address, int index) throws ModelException;


}
