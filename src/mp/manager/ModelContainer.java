package mp.manager;

import java.util.Hashtable;

/**
 *  Контейнер предназначен для хранения описаний моделей, которые могут быть запущены на сервере
 *
 * @author atsvetkov
 *
 */
public class ModelContainer implements Runnable{
	private ModelDatastoreReader modelReader = null;
	private boolean stopFlag = true;
	private Hashtable<String,ModelInfoBean> modelList = new Hashtable <String,ModelInfoBean>();
	private Object[] modelArray = null;


	public ModelContainer(ModelDatastoreReader modelReader){
		this.modelReader = modelReader;
	}

	public void setModelReader(ModelDatastoreReader modelReader){
		this.modelReader = modelReader;
	}

	public int getModelCount(){
		return modelList.size();
	}

	public ModelInfoBean get(int index){
		if ( modelArray == null || modelArray.length <= index ) {
			return null;
		}
		return (ModelInfoBean) modelArray[index];
	}

	public ModelInfoBean get(String modeName) {
		return modelList.get(modeName);
	}



	public void Stop(){
		stopFlag = false;
	}

	public void LoadModels(){
		if (modelReader == null) {
			 return;
		 }
		modelReader.Reload();
		Hashtable<String,ModelInfoBean> modelList = new Hashtable <String,ModelInfoBean>();
		modelList.clear();
		ModelInfoBean model = modelReader.getNext();
		while (model != null) {
			modelList.put(model._getModelName(), model);
			model = modelReader.getNext();
		}
    this.modelList = modelList;
    modelArray = modelList.values().toArray();
	}

	@Override
  public void run() {
		while (stopFlag) {
			LoadModels();
			try {
	      Thread.sleep(60000);
      } catch (InterruptedException e) {
	      return;
      }
		}//while


  }

}
