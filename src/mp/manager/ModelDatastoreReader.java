package mp.manager;


import java.util.Properties;

import mp.elements.ModelException;

/**
 * Интерфейс предназначен для чтения списка моделей со всеми их необходимыми параметрами
 *
 * @author atsvetkov
 *
 */
public interface ModelDatastoreReader {

	 public void Init(Properties props) throws ModelException;

	 public void Reload();

	 public ModelInfoBean getNext();

}
