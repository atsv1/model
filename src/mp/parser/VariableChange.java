package mp.parser;

/**���� ��������� ������ ������������ ������, ������� ����� �������� ��
 * ������ Variable ����������� � ���, ��� �������� ���������� ����������
 */
public interface VariableChange {

  void VariableChanged( VariableChangeEvent changeEvent );

  boolean IsListenerEquals( Object aAnotherListener );

}
