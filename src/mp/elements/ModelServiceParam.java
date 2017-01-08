package mp.elements;

import mp.parser.ScriptException;

/**
 *  Класс предназначен для использования в сервисных параметрах блока - т.е. в таких параметрах,
 * которые не должны вызываться на выполнением самим блоком.
 *
 *
 * User: Администратор
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
