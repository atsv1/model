package mp.parser;

import java.util.EventObject;

/**
 */
public class VariableChangeEvent extends EventObject{
  /**
   * Constructs a prototypical Event.
   *
   * @param source The object on which the Event initially occurred.
   */
  public VariableChangeEvent(Object source) {
    super(source);
  }
}
