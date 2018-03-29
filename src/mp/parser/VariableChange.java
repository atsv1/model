package mp.parser;

import mp.elements.ModelException;

/**���� ��������� ������ ������������ ������, ������� ����� �������� ��
 * ������ Variable ����������� � ���, ��� �������� ���������� ����������
 */
public interface VariableChange {

  void VariableChanged( VariableChangeEvent changeEvent ) throws ScriptException;

  boolean IsListenerEquals( Object aAnotherListener );

}
