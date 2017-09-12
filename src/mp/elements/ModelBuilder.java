package mp.elements;

import java.util.List;

import mp.utils.ServiceLocator;

public abstract class ModelBuilder {

	private ModelElementAbstractFactory FElementFactory = null;

	public ModelElementAbstractFactory getElementFactory() {
		return FElementFactory;
	}
	
	protected static int GetNewId(){
    return ServiceLocator.GetNextId();
  }

	public void setElementFactory(ModelElementAbstractFactory fElementFactory) {
		FElementFactory = fElementFactory;
	}
	
	public ModelForReadInterface CreateElementInstance(ModelElementDataSource previousSource, ModelElementDataSource aCurrentSource,   ModelForReadInterface aElementsOwner) throws ModelException {
		ModelForReadInterface element = null;
		ModelElementDataSource attrReader = aCurrentSource;
		int newId = GetNewId();
		element = getElementFactory().GetNewElement(previousSource,  aElementsOwner, attrReader, newId);
		if (element == null) {
			return null;
		}
		if (element != aElementsOwner && element != null) {
			element.SetDataSource(attrReader);
			element.setElementBuilder(this);
		}
		return element;
	}

	private void CreateElementInstances(ModelElementDataSource previousSource, ModelElementDataSource aCurrentSource,   ModelForReadInterface aElementsOwner) throws ModelException {
		if (getElementFactory().IsLastElement(aElementsOwner)) {
			return;
		}

		ModelElementDataSource attrReader = aCurrentSource;
		int instancesCount = attrReader.GetAttrCount();
		ModelForReadInterface element;
		while (instancesCount > 0) {
			element = CreateElementInstance(previousSource, aCurrentSource, aElementsOwner);
			getElementFactory().ExecuteDoSomethingFunction(previousSource, attrReader, aElementsOwner, element);
			WalkOnDocument(aCurrentSource, element, attrReader);
			instancesCount--;
		}
	}

	/**Метод чтения модели. Этот метод используется в двойной рекурсии. Он вызывает метод CreateElementInstances,
	 * который в свою очередь вызывает WalkOnDocument.
	 *
	 * @param aCurrentNode из этой ноды берутся дочерние ноды, которые содержут информацию  о элементах, для которых
	 * владельцем будет элемент aCurrentElement
	 * @param aCurrentElement
	 * @throws ModelException
	 */
	protected void WalkOnDocument(ModelElementDataSource aCurrentNode, ModelForReadInterface aCurrentElement, ModelElementDataSource parentElement)  throws ModelException {
		if (getElementFactory().IsLastElement(aCurrentElement)) {
			return;
		}
		List<ModelElementDataSource> childNodes = aCurrentNode.GetChildElements();
		for (ModelElementDataSource childElement : childNodes) {
			if (!getElementFactory().IsLastNode(childElement)) {
				CreateElementInstances(aCurrentNode, childElement, aCurrentElement);
			}
		}
	}
	
	public void buildElement(ModelElementDataSource elementSource, ModelForReadInterface element, ModelElementDataSource parentElement) throws ModelException{
		WalkOnDocument(elementSource, element, parentElement);
	}

}
