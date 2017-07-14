package mp.elements;


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

  protected static String GetName( ModelElementDataSource aDataSource ){
    if ( aDataSource == null ){
      return "";
    }
    return aDataSource.GetElementName();
  }

  protected String GetFunctionCode(ModelElementDataSource aCurrentSource, ModelElementDataSource aNewSource) throws ModelException{
    String currentNodeName = GetName( aCurrentSource );
    String newNodeName = GetName(aNewSource);
    String functionCode = GetFunctionCode(currentNodeName, newNodeName);
    if ( "".equalsIgnoreCase( functionCode ) ){
      ModelException e = new ModelException("Не получить функцию для работы с объектом.  Текущая нода " + currentNodeName +
              " Новая нода " + newNodeName);
      throw e;
    }
    return functionCode;
  }



  public abstract ModelForReadInterface GetNewElement(ModelElementDataSource aCurrentNode, ModelForReadInterface aCurrentElement,
  		ModelElementDataSource aNewNode, int aNewId) throws ModelException;

  public abstract String[][] GetMatrix();

  public abstract boolean IsLastNode( ModelElementDataSource aNode ); 
  

  public abstract boolean IsLastElement(ModelForReadInterface aElement);

  public abstract void ExecuteDoSomethingFunction( ModelElementDataSource aParentNode, ModelElementDataSource aCurrentNode,
                                                   ModelForReadInterface aCurrentElement,
                                           ModelForReadInterface aNewElement  ) throws ModelException;


}
