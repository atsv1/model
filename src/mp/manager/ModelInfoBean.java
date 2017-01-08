package mp.manager;

public class ModelInfoBean implements java.io.Serializable{

  private static final long serialVersionUID = -4892836724394792761L;
	public String modelName = null;
	private String modelDescr = null;
	private String modelFileName = null;
	private String formFileName = null;
	private String encoding = null;


	public ModelInfoBean(){

	}

	public ModelInfoBean(String modelName){
		this.setModelName(modelName);
	}

	public String _getModelName() {
	  return modelName;
  }

	public void setModelName(String modelName) {
	  this.modelName = modelName;
  }

	public String getModelDescr() {
	  return modelDescr;
  }

	public void setModelDescr(String modelDescr) {
	  this.modelDescr = modelDescr;
  }

	public String getModelFileName() {
	  return modelFileName;
  }

	public void setModelFileName(String modelFileName) {
	  this.modelFileName = modelFileName;
  }

	public String getFormFileName() {
	  return formFileName;
  }

	public void setFormFileName(String formFileName) {
	  this.formFileName = formFileName;
  }

	public String getEncoding() {
	  return encoding;
  }

	public void setEncoding(String encoding) {
	  this.encoding = encoding;
  }

}
