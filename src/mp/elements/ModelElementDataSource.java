package mp.elements;

public interface ModelElementDataSource {
	
	public ModelElementDataSource getParent();
	
	public String GetElementName();
	
	public String GetAttrName() throws ModelException;
	
	public String GetValueAttr();
	
	public int GetNumberAttr();
	
	public int GetAttrCount() throws ModelException;

	String GetLinkedBlockName() throws ModelException;

	String GetLinkedModelName();

	String GetAttrInitValue() throws ModelException;

	String GetAttrParamType() throws ModelException;

	String GetModelAttrValue();

	boolean GetSaveHistoryFlag();

	String GetSwitchValue();

	String GetSwitchParamName();

	String GetArray_CoordinateParamName(int aCoordinateNum);

	String GetArray_CoordinateParamName();

	String GetArray_ForEachValue();

	String GetArray_EnableFlagName();

	String GetArrayDimensionValue();

	String GetArrayDimensionValue(int aDimensionNum);

	String GetAggregatorFunctionType();

	int GetDurationPrintInterval();

	int GetStepDelay() throws ModelException;

	int GetMaxEnableBlockCount() throws ModelException;

	String GetBlockLinkIndex();

	String GetSkipFirstValue();

	String GetDynamicOwnerName();

	String GetDynamicEtalonName();

	String GetValueType();

	String GetSourceParamName();

	String GetSourceBlockIndex();

	String GetSourceBlockName();

	String GetCountVar();

	boolean IsOutgoingFormula();

	boolean IsEnableFormula();

	boolean IsCountFormula();

	String GetFormulaType();

	String GetModelStep() throws ModelException;

	int GetTransitionPriority() throws ModelException;

	String GetNextStateName() throws ModelException;

	String GetTransitionValue() throws ModelException;

	String GetAutomatCodeType() throws ModelException;

	String GetTransitionType() throws ModelException;

	String GetLinkedParamName() throws ModelException;
	
	void AddConstant(String aConstName, String aConstValue) throws ModelException;
	
	 

}
