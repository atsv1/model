package mp.parser;

/**Этот интерфейс должны поддерживать классы, которые хотят получать от
 * класса Variable уведомление о том, что значение переменной изменилось
 */
public interface VariableChange {

  void VariableChanged( VariableChangeEvent changeEvent );

  boolean IsListenerEquals( Object aAnotherListener );

}
