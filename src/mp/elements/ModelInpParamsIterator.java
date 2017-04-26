package mp.elements;

import mp.parser.*;

/**
 * User: atsv
 * Date: 16.09.2006
 * Класс предназначен для формирования списка переменных, которые присутствуют в откомпилированном скрипте
 */
public class ModelInpParamsIterator extends ModelIterator {

  /**Список 
   *
   */
  public ModelElementContainer sourceList = null;
  public ScriptParser parser = null;
  public ModelElement ownerElement = null;

  private void Check() throws ModelException{
    ModelException e;
    if ( parser == null )
    {
      e = new ModelException("Пустой парсер. Невозможно получить список используемых переменных.");
      throw e;
    }
    if ( sourceList == null )
    {
      e = new ModelException("Пустой список входных переменных. Невозможно сформировать список используемых переменных.");
      throw e;
    }
    if ( ownerElement == null){
      e = new ModelException("Пустой элемент-владелец. Невозможно сформировать список используемых переменных.");
      throw e;
    }
  }

  private ModelElement GetResult(Variable aVariable) throws ModelException{
    
   if ( aVariable == null ) {
      return null;
    }    
    return sourceList.Get(aVariable.GetName());
  }

  /**Данная процедура определяет первую неслужебную переменную. Дело в том, что парсер добавляем в тело программы
   * свои служебные переменные, которые начинаются с префикса, содержащегося в ScriptLanguageDef.GetTempVarPrefix()
   * Сейчас этот префикс выглядит так: Tmp_Var_XXX, где вместо ХХХ подставляется номер служебной переменной.
   * Также производится проверка - не является ли полученная переменная результатом операции MOV. Если является, то
   * нужно узнать - не является ли объект, для которого производится подбор зависимых параметров, владельцем этой
   * переменной. Данная проверка нужна для того, чтобы правильно обрабатывались зависимости типа:
   * var1 := var1 + 1;  И вместе с ними правильно обрабатывались зависимости вида:
   * var1 := 4;
   * В первом случае var1 будет фигурировать не только как операнд операции Mov. Во втором случае var1 будет как
   * раз результатом Mov, и при этом владелец var1 будет как раз тем объектом, для которого производится определение
   * списка входных параметров
   * @param aInitVariable
   * @return
   * @throws ModelException
   */
  private ModelElement GetNoServiceVariable(Variable aInitVariable) throws ModelException {
    ModelElement result = null;
    Variable currentVar = aInitVariable;
    while ( currentVar != null ){
      // проверяем, не является ли возвращенная переменная временной переменной
      if ( !ScriptLanguageDef.IsServiceName(currentVar.GetName()) ){
        if ( !parser.IsMovResult() ){//переменная - не результат опрации Mov
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
