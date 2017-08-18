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

	private void CreateElementInstances(ModelElementDataSource previousSource, ModelElementDataSource aCurrentSource,   ModelForReadInterface aElementsOwner) throws ModelException {
		if (getElementFactory().IsLastElement(aElementsOwner)) {
			return;
		}

		ModelElementDataSource attrReader = aCurrentSource;
		int instancesCount = attrReader.GetAttrCount();
		ModelForReadInterface element;
		while (instancesCount > 0) {
			int newId = GetNewId();
			element = getElementFactory().GetNewElement(previousSource,  aElementsOwner, attrReader, newId);
			if (element == null) {
				return;
			}
			if (element != aElementsOwner && element != null) {
				element.SetDataSource(attrReader);
			}
			getElementFactory().ExecuteDoSomethingFunction(previousSource, attrReader, aElementsOwner, element);
			WalkOnDocument(aCurrentSource, element, attrReader);
			instancesCount--;
		}
	}

	/**����� ������ ������. ���� ����� ������������ � ������� ��������. �� �������� ����� CreateElementInstances,
	 * ������� � ���� ������� �������� WalkOnDocument.
	 *
	 * @param aCurrentNode �� ���� ���� ������� �������� ����, ������� �������� ����������  � ���������, ��� �������
	 * ���������� ����� ������� aCurrentElement
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

}
