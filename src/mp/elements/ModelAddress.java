package mp.elements;

/**
 * User: atsv
 * Date: 02.10.2006
 */
public class ModelAddress implements java.io.Serializable{
	/**
   *
   */
  private static final long serialVersionUID = 1L;
	public String FModelName = null;
  public String FBlockName = null;
  public  int FBlockIndex  = -1;
  public String FParamName = null;

  public ModelAddress(){

  }

  public ModelAddress(String aModelName, String aBlockName, int aBlockIndex, String aParamName){
  	setFModelName(aModelName);
    FBlockName = aBlockName;
    FBlockIndex = aBlockIndex;
    FParamName = aParamName;
  }

  public ModelAddress(String aBlockName, int aBlockIndex, String aParamName){
    FBlockName = aBlockName;
    FBlockIndex = aBlockIndex;
    FParamName = aParamName;
  }

  public String GetModelName(){
  	return getFModelName();
  }

  public String GetBlockName() {
    return FBlockName;
  }

  public void SetBlockName(String aBlockName) {
    this.FBlockName = aBlockName;
  }

  public int GetBlockIndex() {
    return FBlockIndex;
  }

  public void SetBlockIndex(int aBlockIndex) {
    this.FBlockIndex = aBlockIndex;
  }

  public String GetParamName() {
    return FParamName;
  }

  public void SetParamName(String aParamName) {
    this.FParamName = aParamName;
  }

  public double GetValue(ModelConnector connector) throws ModelException{
  	return connector.GetValue( getFModelName(), FBlockName, FBlockIndex, FParamName );
  }

	private String getFModelName() {
	  return FModelName;
  }

	private void setFModelName(String fModelName) {
	  FModelName = fModelName;
  }

}
