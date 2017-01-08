package mp.parser;


import java.util.Vector;

/**
 * User: atsv
 * Date: 10.09.2006
 */
public class ScriptLinker {
  /**� ������� FAddressList �������� ���������� � �������, �� ������� � ��������� �������� ����������-�����
   * � � �������� � ���� ����������. ���������� �������� � ���� �������, � ������� ������� �������� - �������
   * ���������� � ���������, � ������ ������� - �������� ������.
   */
  private Vector FAddressList = null;
  private Vector FLabelList = null;
  private Vector FProgram = null;
  private Object[] FCurrentObject = null;

  public ScriptLinker()
  {
    super();
    FAddressList = new Vector();
    FLabelList = new Vector();
  }

  private static void AddNewRecord(int aPosition, int aLinkId, Vector aRecordsList)
  {
    Object[] newRecord;
    newRecord = new Object[2];
    Integer position = new Integer( aPosition );
    Integer linkId = new Integer( aLinkId );
    newRecord[0] = position;
    newRecord[1] = linkId;
    aRecordsList.add( newRecord );
  }

  private int GetPosition(int aLinkId, Vector aRecordsList)
  {
    int i = 0;
    Object[] record;
    Integer position;
    Integer linkId;
    while ( i < aRecordsList.size() )
    {
      record = (Object[]) aRecordsList.get(i);
      linkId = (Integer) record[1];
      if ( linkId.intValue() == aLinkId)
      {
        position = (Integer) record[0];
        FCurrentObject = record;
        return position.intValue();
      }
      i++;
    }
    return -1;
  }

  private void DeleteCurrentRecord( Vector aRecordList )
  {
    int i = aRecordList.indexOf( FCurrentObject );
    if ( i != -1)
    {
      aRecordList.removeElementAt( i );
    }
  }

  private void CheckLabel(Variable aLabel, int aPosition) throws ScriptException
  {
    int address = GetPosition( aLabel.GetIntValue(), FAddressList );
    if (address == -1)
    {
      AddNewRecord( aPosition, aLabel.GetIntValue(), FLabelList);
    } else
    {//���� ����� �����
      //������ �������� � ������ �� ���������� ������� � ���������
      Variable addressVar = (Variable) FProgram.get( address );
      Integer newPos = (Integer) FCurrentObject[0];
      addressVar.SetValue( aPosition );
      //������ ������-����� �� ������ �� �������� �������� NOP
      ScriptOperationNOP operation = new ScriptOperationNOP();
      FProgram.setElementAt( operation, aPosition );
      //������� ������ �� ������ �����
      DeleteCurrentRecord( FAddressList );
    }
  }

  private void CheckAddress(Variable aAddress, int aPosition) throws ScriptException
  {
    int labelPosition = GetPosition( aAddress.GetIntValue(), FLabelList );
    if ( labelPosition == -1 )
    {//����� ����� � ������ ��� ���
      AddNewRecord( aPosition, aAddress.GetIntValue(),  FAddressList );
    } else
    {
      //����������� ���������� ����� � ����������-������
      Integer labelPos = (Integer) FCurrentObject[0];
      Variable addressVar = (Variable) FProgram.get( aPosition );
      addressVar.SetValue( labelPos.intValue() );
      //������ ����� �� NOP
      ScriptOperationNOP operation = new ScriptOperationNOP();
      FProgram.setElementAt( operation, labelPos.intValue() );
      DeleteCurrentRecord( FLabelList );
    }
  }

  private void CheckVariable(Variable aVariable, int aPosition) throws ScriptException
  {
    if (aVariable.GetName().equalsIgnoreCase("�����") ){
      CheckLabel( aVariable, aPosition );
    } else
    {
      if ( aVariable.GetName().equalsIgnoreCase("�����") )
      {
        CheckAddress( aVariable, aPosition );
      }
    }
  }

  public void LinkProgram(Vector aProgram) throws ScriptException
  {
    int i = 0;
    FProgram = aProgram;
    /**��������� � ����� ������ ��������� �������� NOP, ��� ����, ����� ��������� ����������� ��������
     * �� 0-� ������� ���������. 
     *
     */
    FProgram.insertElementAt(new ScriptOperationNOP(), 0);
    Variable programObject = null;
    while ( i < aProgram.size() )
    {
      try{
         programObject = (Variable)aProgram.get( i );
         CheckVariable(programObject, i);
      } catch (Exception e){
        //System.out.println(e.getMessage());
        }
      i++;
    }
  }

}
