package mp.elements;

import mp.parser.*;
import mp.utils.ServiceLocator;
import mp.utils.ModelAttributeReader;

import java.util.Hashtable;
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
  private Node FNode = null;

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

  public Node GetNode() {
    return FNode;
  }

  public void SetNode(Node aNode) {
    FNode = aNode;
  }

  private void ReadEventInfo(Node aNode) throws ModelException {
    ModelAttributeReader attrReader = ServiceLocator.GetAttributeReader();
    attrReader.SetNode( aNode );
    String eventName = attrReader.GetAttrName();
    if ( eventName == null || "".equalsIgnoreCase( eventName ) ){
      ModelException e = new ModelException("������ � �������� " + FOwnerName + ": ������ ��� �������");
      throw e;
    }
    NodeList nodes = aNode.getChildNodes();
    int i = 0;
    Node node;
    String eventSource = null;
    while ( i < nodes.getLength() ){
      node = nodes.item( i );
      if ( node.getNodeType() == Node.CDATA_SECTION_NODE ){
        eventSource = node.getNodeValue();
        break;
      }
      i++;
    }
    AddEventProcessor( eventName, eventSource );
  }

  public void ApplyNodeInfo() throws ModelException{
    if ( FNode == null ){
      return;
    }
    NodeList nodes = FNode.getChildNodes();
    int i = 0;
    Node currentNode = null;
    while ( i < nodes.getLength() ){
      currentNode = nodes.item( i );
      if ( currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getNodeName().equalsIgnoreCase("Event")){
        ReadEventInfo( currentNode );
      }
      i++;
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

}
