package mp.parser;

import mp.elements.ModelException;

/**Этот интерфейс должны поддерживать классы, которые хотят получать от
 * класса Variable уведомление о том, что значение переменной изменилось
 */
public interface VariableChange {

  void VariableChanged( VariableChangeEvent changeEvent ) throws ScriptException;

  boolean IsListenerEquals( Object aAnotherListener );

}
