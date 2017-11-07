package mp.parser;

import java.util.UUID;

/**
 * Интерфейс предназначен для обеспечения возможности управления ходом моделирования изнутри скрипта модели
 *
 * User: Администратор
 * Date: 07.04.2008
 * Time: 21:51:53
 */
public interface ModelExecutionManager {

  public String GetManagerName();

  public void StopModelExecution() throws ScriptException;

  public void StartModelExecution() throws ScriptException;

  public void SetToInitCondition() throws ScriptException;

  public Variable GetVariable( String aBlockName, int aBlockIndex, String aParamValue )  throws ScriptException;

  public void ReConnectParam( String aBlockName, int aBlockIndex, String aParamName, 
                              String aModelToConnect, String aBlockToConnect, int aBlockIndexToConnect, String aParamToConnect ) throws ScriptException;
  
  public UUID fork(int modelTimePeriod, boolean nestedFork) throws ScriptException;
  
  public void rollback(UUID label) throws ScriptException;
  
  public int createNewBlock(String blockName, String parentParamName) throws ScriptException;
  

}
