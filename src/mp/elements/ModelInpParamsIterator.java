package mp.elements;

import mp.parser.*;

/**
 * User: atsv
 * Date: 16.09.2006
 * ����� ������������ ��� ������������ ������ ����������, ������� ������������ � ����������������� �������
 */
public class ModelInpParamsIterator extends ModelIterator {

  /**������ 
   *
   */
  public ModelElementContainer sourceList = null;
  public ScriptParser parser = null;
  public ModelElement ownerElement = null;

  private void Check() throws ModelException{
    ModelException e;
    if ( parser == null )
    {
      e = new ModelException("������ ������. ���������� �������� ������ ������������ ����������.");
      throw e;
    }
    if ( sourceList == null )
    {
      e = new ModelException("������ ������ ������� ����������. ���������� ������������ ������ ������������ ����������.");
      throw e;
    }
    if ( ownerElement == null){
      e = new ModelException("������ �������-��������. ���������� ������������ ������ ������������ ����������.");
      throw e;
    }
  }

  private ModelElement GetResult(Variable aVariable) throws ModelException{
    
   if ( aVariable == null ) {
      return null;
    }    
    return sourceList.Get(aVariable.GetName());
  }

  /**������ ��������� ���������� ������ ����������� ����������. ���� � ���, ��� ������ ��������� � ���� ���������
   * ���� ��������� ����������, ������� ���������� � ��������, ������������� � ScriptLanguageDef.GetTempVarPrefix()
   * ������ ���� ������� �������� ���: Tmp_Var_XXX, ��� ������ ��� ������������� ����� ��������� ����������.
   * ����� ������������ �������� - �� �������� �� ���������� ���������� ����������� �������� MOV. ���� ��������, ��
   * ����� ������ - �� �������� �� ������, ��� �������� ������������ ������ ��������� ����������, ���������� ����
   * ����������. ������ �������� ����� ��� ����, ����� ��������� �������������� ����������� ����:
   * var1 := var1 + 1;  � ������ � ���� ��������� �������������� ����������� ����:
   * var1 := 4;
   * � ������ ������ var1 ����� ������������ �� ������ ��� ������� �������� Mov. �� ������ ������ var1 ����� ���
   * ��� ����������� Mov, � ��� ���� �������� var1 ����� ��� ��� ��� ��������, ��� �������� ������������ �����������
   * ������ ������� ����������
   * @param aInitVariable
   * @return
   * @throws ModelException
   */
  private ModelElement GetNoServiceVariable(Variable aInitVariable) throws ModelException {
    ModelElement result = null;
    Variable currentVar = aInitVariable;
    while ( currentVar != null ){
      // ���������, �� �������� �� ������������ ���������� ��������� ����������
      if ( !ScriptLanguageDef.IsServiceName(currentVar.GetName()) ){
        if ( !parser.IsMovResult() ){//���������� - �� ��������� ������� Mov
          return GetResult( currentVar );
        } else {
          result = GetResult( currentVar );
          if ( result != ownerElement ){
            return result;
          }
        }
      }
      currentVar = (Variable) parser.Next("mp.parser.Variable");
    }
    return null;
  }

  public ModelElement First() throws ModelException{
    Check();
    Variable var = (Variable) parser.First("mp.parser.Variable", "mp.parser.ScriptArray");
    return GetNoServiceVariable(var);
  }

  public ModelElement Next() throws ModelException{
    Variable var = (Variable) parser.Next("mp.parser.Variable", "mp.parser.ScriptArray");
    return GetNoServiceVariable(var);
  }
  
}
