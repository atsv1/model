package mp.elements;

import mp.parser.*;
import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;

import java.util.Hashtable;
import java.util.List;
import java.util.Enumeration;
import java.util.NoSuchElementException;


/**
 * User: atsv
 * Date: 07.03.2007
 *
 * ����� ������������ ��� �������� ������������ �������. ��� "������������� �������" ����� ���������� ��������� ����,
 * ������� ������ ����������� ��� ������������� �����-���� �������. ��� ���� �� ����� ��������, ��� �������� ����
 * �������: � �����, ������� �������� ���������� ����������� �������, ��� ��� ����� �����.
 * ��������� ������� ������ ��������� ������ ����� � �������� ��� �� ����������� �����, ��� � ����.
 */
public class ModelEventProcessorContainer implements ModelForReadInterface{
  private ScriptLanguageExt FLanguageExt = null;
  private ModelElement FOwner = null;
  private Hashtable FEventList = null;
  private String FOwnerName = null;
  private ModelElementDataSource dataSource = null;
  private ModelBuilder elementBuilder = null;

  public ModelEventProcessorContainer( ModelElement aOwner ){
    FOwner = aOwner;
    if ( FOwner != null ){
      FOwnerName = "\""+  FOwner.GetFullName() + "\"";
    } else {
      FOwnerName = "\"NoName\"";
    }
    FEventList = new Hashtable();

  }

  public void SetLanguageExt( ScriptLanguageExt aLanguageExt ){
    FLanguageExt = aLanguageExt;
  }

  protected void AddEventProcessor( String aEventName, String aScriptSource ) throws ModelException{
    if ( aEventName == null  ){
      ModelException e = new ModelException("������ � �������� " + FOwnerName +
              ": ������� �������� ���������� ��� ����� ");
      throw e;
    }
    EventProcessor proc = (EventProcessor) FEventList.get( aEventName.toUpperCase() );
    if ( proc != null ){
      ModelException e = new ModelException("������ � �������� " + FOwnerName + ": ���������� � ������ " + aEventName +
              " ��� ������������ � ������ ������������");
      throw e;
    }
    try {
      proc = new EventProcessor(FLanguageExt, aEventName, aScriptSource);
    } catch (ScriptException e) {
      ModelException e1 = new ModelException("������ � �������� " + FOwnerName + ": " + e.getMessage());
      throw e1;
    }
    FEventList.put( aEventName.toUpperCase(), proc ); 
  }

  /** ���������� �������� ���� ��������� �������. ���� ������� �� ��������� (����������� � ������� �� ���� ��������
   * � �����), �� ��� ��� �� �����������.
   */
  public void Execute() throws ModelException {
    Enumeration e =  FEventList.elements();
    EventProcessor proc = null;
    try{
      proc = (EventProcessor) e.nextElement();
    } catch ( NoSuchElementException e3 ){
      return;
    }
    while ( proc != null ){
      try {
        proc.ExecIfNeed();
      } catch (ScriptException e1) {
        ModelException e2 = new ModelException("������ � �������� " + FOwnerName + " ��� ���������� ���� ������� " +
                proc.GetName() + " �������� ������ " + e1.getMessage());
        throw e2;
      }
      try{
        proc = (EventProcessor) e.nextElement();
      } catch ( NoSuchElementException e3 ){
        break;
      }
    }
  }

  public void EventFired( String aEventName ) throws ModelException{
    if ( aEventName == null ){
      ModelException e = new ModelException( "������ � �������� " + FOwnerName + " ������� ������� ������� � ������ ���������" );
      throw e;
    }
    EventProcessor proc = (EventProcessor) FEventList.get( aEventName.toUpperCase() );
    if ( proc == null ){
      ModelException e = new ModelException( "������ � �������� " + FOwnerName + " ����������� ���������� ������� " + aEventName );
      throw e;
    }
    proc.SetExecFlag( true );
  } 

  private void ReadEventInfo(ModelElementDataSource elementSource) throws ModelException {    
    String eventName = elementSource.GetAttrName();
    if ( eventName == null || "".equalsIgnoreCase( eventName ) ){
      ModelException e = new ModelException("������ � �������� " + FOwnerName + ": ������ ��� �������");
      throw e;
    }
    
    String eventSource = elementSource.GetexecutionCode();    
    AddEventProcessor( eventName, eventSource );
  }

  public void ApplyNodeInfo() throws ModelException{
    if ( dataSource == null ){
      return;
    }
    List<ModelElementDataSource> eventSources =   dataSource.GetChildElements("Event");
    if ( eventSources == null ) {
    	return;
    }
    for (ModelElementDataSource eventSource : eventSources) {
      ReadEventInfo( eventSource );
    }
  }

  private static class EventProcessor{
    private String FEventName = null;
    private ScriptParser FParser = null;
    private boolean FIsNeedToExecute = false;
    
    public EventProcessor( ScriptLanguageExt aLanguageExt, String aEventName, String aEventSourceCode ) throws ScriptException {
      //FParser = new PascalParser();
      FParser = ParserFactory.GetParser( aLanguageExt, aEventSourceCode );
      //FParser.SetLanguageExt( aLanguageExt );
      //FParser.ParseScript( aEventSourceCode );
      FEventName = aEventName;
    }

    public String GetName(){
      return FEventName;
    }

    public void ExecuteScript() throws ScriptException {
      if ( FParser != null ){
        FParser.ExecuteScript();
        FIsNeedToExecute = false;
      }
    }

    public void SetExecFlag( boolean aFlagValue ){
      FIsNeedToExecute = aFlagValue;
    }

    public void ExecIfNeed() throws ScriptException {
      if ( FIsNeedToExecute ) {
        ExecuteScript();
      }
    }

  }

	@Override
	public ModelElementDataSource GetDataSource() {		
		return dataSource;
	}

	@Override
	public void SetDataSource(ModelElementDataSource dataSource) {
		this.dataSource = dataSource;	
	}
	
	@Override
	public ModelBuilder getElementBuilder() {
		return elementBuilder;
	}

	@Override
	public void setElementBuilder(ModelBuilder elementBuilder) {
		this.elementBuilder = elementBuilder;
	}


}
