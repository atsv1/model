package mp.elements;

import mp.parser.ScriptException;

/**
 *  ����� ������������ ��� ������������� � ��������� ���������� ����� - �.�. � ����� ����������,
 * ������� �� ������ ���������� �� ����������� ����� ������.
 *
 *
 * User: �������������
 * Date: 29.04.2008
 */
public class ModelServiceParam extends ModelCalculatedElement{

  public ModelServiceParam(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }

  public void UpdateParam() throws ScriptException, ModelException{
    return;
  }

  public void ServiceUpdateParam() throws ScriptException, ModelException{
    super.UpdateParam();

  }

}
