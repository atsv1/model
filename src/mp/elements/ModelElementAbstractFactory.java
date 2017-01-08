package mp.elements;
import org.w3c.dom.Node;

/**
 * User: atsv
 * Date: 29.09.2006
 *
 */
public abstract class ModelElementAbstractFactory {

  protected String GetFunctionCode(String aCurrentNodeName, String aNewNodeName){
    int i = 0;
    String[][] modelDef = GetMatrix();
    while ( i < modelDef.length ){
      if ( aCurrentNodeName.equalsIgnoreCase( modelDef[i][0] ) &&  aNewNodeName.equalsIgnoreCase( modelDef[i][1] ) ){
        return modelDef[i][2];
      }
      i++;
    }
    return "";
  }

  protected static String GetNodeName( Node aNode ){
    if ( aNode == null ){
      return "";
    }
    return aNode.getNodeName();
  }

  protected String GetFunctionCode(Node aCurrentNode, Node aNewNode) throws ModelException{
    String currentNodeName = GetNodeName( aCurrentNode );
    String newNodeName = GetNodeName(aNewNode);
    String functionCode = GetFunctionCode(currentNodeName, newNodeName);
    if ( "".equalsIgnoreCase( functionCode ) ){
      ModelException e = new ModelException("Не получить функцию для работы с объектом.  Текущая нода " + currentNodeName +
              " Новая нода " + newNodeName);
      throw e;
    }
    return functionCode;
  }



  public abstract ModelForReadInterface GetNewElement(Node aCurrentNode, ModelForReadInterface aCurrentElement,
                                                      Node aNewNode, int aNewId) throws ModelException;

  public abstract String[][] GetMatrix();

  public abstract boolean IsLastNode( Node aNode );

  public abstract boolean IsLastElement(ModelForReadInterface aElement);

  public abstract void ExecuteDoSomethingFunction( Node aParentNode, Node aCurrentNode,
                                                   ModelForReadInterface aCurrentElement,
                                           ModelForReadInterface aNewElement  ) throws ModelException;


}
