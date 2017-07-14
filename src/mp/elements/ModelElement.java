package mp.elements;
import java.util.UUID;
import mp.parser.ScriptException;
import mp.utils.NameService;
import mp.utils.ServiceLocator;

/**
 * User: atsv
 * Date: 15.09.2006
 */
public class ModelElement implements ModelForReadInterface {
  private int FId = -1;
  //private String FElementName = null;
  private int FNameIndex = -1;
  private NameService FNames = ServiceLocator.GetNamesList();
  private ModelElement FOwner = null;
  private ModelElementContainer FElements = null;
  protected ModelElementDataSource elementSource = null;
  private String FFullName = null;
  private Integer FNameIndexObj = null;

  private void UpdateFullName(){
    String ownerName;
    if ( FOwner == null ){
      ownerName = "";
    } else{
      ownerName = FOwner.GetName() + ".";
    }
    FFullName = ownerName + FNames.GetName( FNameIndex );
  }

  public ModelElement( ModelElement aOwner, String aElementName, int aElementId ) {
    SetOwner(aOwner);
    SetName(aElementName);
    SetElementId(aElementId);
    FElements = new ModelElementContainer();
  }

  public static ModelElement CreateModelElement(ModelElement aOwner, String aElementName, int aElementId) {
    return new ModelElement(aOwner, aElementName, aElementId);
  }

  public int GetElementId() {
    return FId;
  }

  public void SetElementId(int aId) {
    this.FId = aId;
  }

  public String GetName() {
    return FNames.GetName( FNameIndex );
  }

  public void SetName(String aElementName) {
    FNameIndex = FNames.GetNameIndex( aElementName );
    FNameIndexObj = new Integer( FNameIndex );
    UpdateFullName();
  }

  public ModelElement GetOwner() {
    return FOwner;
  }

  public void SetOwner(ModelElement aOwner) {
    this.FOwner = aOwner;
    UpdateFullName();
  }

  public ModelElementContainer GetElements() {
    return FElements;
  }
  
  public void AddElement(ModelElement aElement) throws ModelException{
    FElements.AddElement( aElement );
  }

  public ModelElement Get(String aName) throws ModelException{
    return FElements.Get( aName );
  }

  public ModelElement Get( Integer aNameIndex )throws ModelException{
    return FElements.GetByNameIndex( aNameIndex );
  }


  public int size(){
    return FElements.size();
  }

  public ModelElement GetByIndex(int aIndex)  throws ModelException{
    return FElements.get( aIndex );
  }

  public ModelElement GetById(int aId ) throws ModelException{
    return FElements.Get( aId );
  }

  protected void ApplyAllElementsNodeInformation() throws ModelException, ScriptException{
    int i = size()-1;
    ModelElement element;
    while ( i >= 0 ){
      element = GetByIndex(i);
      element.ApplyNodeInformation();
      i--;
    }
  }

  public void ApplyNodeInformation() throws ModelException, ScriptException{
    ApplyAllElementsNodeInformation();
  }

  public String GetFullName(){
    return FFullName;
  }

  public void ClearElements(){
    FElements.Clear();
  }

  public void RemoveElement( ModelElement aElement ) throws ModelException {
    FElements.RemoveElement( aElement );
  }

  public int GetNameIndex(){
    return FNameIndex;
  }

  public Integer GetNameIndexObj(){
    return FNameIndexObj;
  }

  public String toString(){
    return FFullName;
  }
  
  public void fixState(UUID stateLabel) throws ModelException{
  	throw new  ModelException("invalid object operation");  	
  }
  
  public void rollbackTo(UUID stateLabel) throws ModelException{
  	throw new  ModelException("invalid object operation");  	
  }

	@Override
	public ModelElementDataSource GetDataSource() {		 
		return elementSource;
	}

	@Override
	public void SetDataSource(ModelElementDataSource dataSource) {
		this.elementSource = dataSource;
	}

}
