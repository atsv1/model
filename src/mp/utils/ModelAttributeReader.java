package mp.utils;

import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.CDATASection;

import java.awt.*;
import java.util.Hashtable;

import mp.elements.ModelElementDataSource;
import mp.elements.ModelException;

/**
 * User: atsv
 * Date: 23.09.2006
 * ����� ������ �������� ���������
 */
public class ModelAttributeReader implements ModelElementDataSource{
  private Node FNode = null;
  private ModelElementDataSource parentElement = null;
  private Hashtable FConstantList = new Hashtable();


  public ModelAttributeReader( Node aNode, ModelElementDataSource parent ) {
    FNode = aNode;
    this.parentElement = parent;
  }

  public Node GetNode() {
    return FNode;
  }
  
  public ModelElementDataSource getParent(){
  	return parentElement;
  }
  
   

  public void SetNode(Node aNode) throws ModelException {
    if (aNode == null){
      ModelException e = new ModelException("�������� ������ ������ �� ���� � ������ ������ ���������");
      throw e;
    }
    this.FNode = aNode;
  }

  private String GetAttrValue(String attrName){
    if ( FNode ==  null){
      return null;
    }
    NamedNodeMap attributes = FNode.getAttributes();
    Node attr = attributes.getNamedItem( attrName );
    if ( attr == null ){
      return null;
    }
    return attr.getNodeValue();
  }

  @Override
  public String GetAttrName() throws ModelException{
    String result = GetAttrValue("name");
    if ( result == null ){
      ModelException e = new ModelException("�� �������� �������� ��������. ���� " + FNode.getNodeName());
      throw e;
    }
    return result;
  }

  @Override
  public String GetValueAttr(){
    return GetAttrValue("value");
  }

  @Override
  public int GetNumberAttr(){
  	String s = GetAttrValue("number");
  	int res;
    res = Integer.parseInt(s);
    return res;
  }

  @Override
  public int GetAttrCount() throws ModelException{
    String s = GetAttrValue( "count" );
    if ( s == null || s.equalsIgnoreCase("") )
    {
      return 1;
    }
    int i = 0;
    String constValue = GetConstantValue( s );
    if ( constValue != null ) {
      s = constValue;
    }
    try{
      i = Integer.parseInt(s);
    } catch (Exception e){
      ModelException e1 = new ModelException("�������� ������ ���������� ���������� �������� " +   GetAttrName() +
              " : " + s);
      throw e1;
    }
    if ( i <= 0 ) return 1;
    return i;
  }

  @Override
  public String GetAttrParamType() throws ModelException{
    return GetAttrValue("type");
  }

  @Override
  public String GetAttrInitValue() throws ModelException{
    String s = GetAttrValue("initvalue");
    String constValue = GetConstantValue( s );
    if ( constValue != null ) {
      s = constValue;
    }
    return s;
  }

  @Override
  public String GetLinkedModelName(){
    return GetAttrValue("modelLink");
  }

  @Override
  public String GetLinkedBlockName() throws ModelException{
    return GetAttrValue("blockLink");
  }

  @Override
  public String GetLinkedParamName() throws ModelException{
    return GetAttrValue("paramLink");
  }


  @Override
  public String GetTransitionType() throws ModelException{
    return GetAttrValue("type");
  }

  @Override
  public String GetAutomatCodeType() throws ModelException{
    return GetAttrValue("type");
  }

  @Override
  public String GetTransitionValue()throws ModelException{
    return GetAttrValue("value");
  }

  @Override
  public String GetNextStateName() throws ModelException{
    return GetAttrValue("nextstate");
  }

  @Override
  public int GetTransitionPriority() throws ModelException {
    String val = GetAttrValue("priority");
    if ( val == null || "".equalsIgnoreCase( val ) ){
      return 0;
    }
    try{
      //System.out.println( "prior " + val );
      return Integer.parseInt( val );
    } catch (Exception e){
      ModelException e1 = new ModelException("�������� �������� ���� priority: " + val);
      throw e1;
    }
  }

  @Override
  public String GetModelStep() throws ModelException {
    return GetAttrValue("step");
  }

  @Override
  public String GetFormulaType(){
    return GetAttrValue("type");
  }

  /** ������� ������������� ��� �����������, �������� �� ����, ���������� � �������� ���������, ����� � ��������. ��� ����
   * ������� ������ ��������������� ��� �������� ����������, ������� ����� �������������� ��� ������ ����� �������������
   * ����������� � ������.
   * ���������: ���� ��������-��������, � ���� ��������-��������. � ��������� ��������� ��������������, ����� ����������
   * ����-���� �� ����� �� �������� �� ���������-���������. ������ ��� �������� ����� ���������� ������ �������.
   * @return  true - ���� ���������� ���� �������� ������� ��� ������� ����������. ����� ������������ false
   */
  @Override
  public boolean IsCountFormula(){
    String s = FNode.getNodeName();
    if ( !"formula".equalsIgnoreCase(s) ){
      return false;
    }
    s = GetAttrValue("type");
    if ( "count".equalsIgnoreCase(s) ){
      return true;
    }
    return false;
  }

  /** ������� ���������, �������� �� ���������� � ������ ���� �������, ������� ��������� ����� ����� ����� �������������
   * �����������.
   *
   * @return  -true, ���� ���� �������� �������, �� ������� ������������, �������� �� �����. false - ���� �� ��������
   */
  @Override
  public boolean IsEnableFormula(){
    String s = FNode.getNodeName();
    if ( !"formula".equalsIgnoreCase(s) ){
      return false;
    }
    s = GetAttrValue("type");
    if ( "enable".equalsIgnoreCase(s) ){
      return true;
    }
    return false;
  }

  /**������� ���������, �������� �� ���������� ���� ������� ��� ������� ���������� ����������.
   * ���������: ����������, ������� ������� ������ ������� �� ������ ��������� �� ����������� ������ ���� ����� ����
   * ����������, ������� �������� �� ������ ������� ��������.
   * @return
   */
  @Override
  public boolean IsOutgoingFormula(){
    String s = FNode.getNodeName();
    if ( !"formula".equalsIgnoreCase(s) ){
      return false;
    }
    s = GetAttrValue("type");
    if ( "out".equalsIgnoreCase(s) ){
      return true;
    }
    return false;

  }

  @Override
  public String GetCountVar(){
    return GetAttrValue("countvar");
  }

  @Override
  public String GetSourceBlockName(){
    return GetAttrValue("sourceblock");
  }

  @Override
  public String GetSourceBlockIndex(){
    return GetAttrValue("sourceindex");
  }

  @Override
  public String GetSourceParamName(){
    return GetAttrValue("sourceparam");
  }

  @Override
  public String GetValueType(){
    return GetAttrValue("valuetype");
  }

  @Override
  public String GetDynamicEtalonName(){
    return GetAttrValue("etalonname");
  }

  @Override
  public String GetDynamicOwnerName(){
    return GetAttrValue("ownername");
  }

  @Override
  public String GetSkipFirstValue(){
    return GetAttrValue("skipfirst");
  }

  @Override
  public String GetBlockLinkIndex(){
    return GetAttrValue("blockIndex");
  }

  @Override
  public int GetMaxEnableBlockCount() throws ModelException {
    String s = GetAttrValue("maxCount");
    if ( s == null || "".equalsIgnoreCase( s ) ){
      return -1;
    }
    try{
      return Integer.parseInt( s );
    } catch (Exception e){
      ModelException e1  = new ModelException("�������� �������� " + s);
      throw e1;
    }
  }

  @Override
  public int GetStepDelay() throws ModelException {
    String s = GetAttrValue("delay");
    if ( s == null || "".equalsIgnoreCase( s ) ){
      return -1;
    }
    try{
      return Integer.parseInt( s );
    } catch (Exception e){
      return -1;
    }
  }

  @Override
  public int GetDurationPrintInterval(){
    String s = GetAttrValue("printDurationInterval");
    if ( s == null || "".equalsIgnoreCase( s ) ){
      return -1;
    }
    try{
      return Integer.parseInt( s );
    } catch (Exception e){
      return -1;
    }
  }

  @Override
  public String GetAggregatorFunctionType(){
    return GetAttrValue("functiontype");
  }

  @Override
  public String GetArrayDimensionValue( int aDimensionNum ){
    return GetAttrValue("dimension" + Integer.toString( aDimensionNum ) );
  }

  @Override
  public String GetArrayDimensionValue( ){
    return GetAttrValue("dimension" );
  }

  @Override
  public String GetArray_EnableFlagName(){
    return GetAttrValue("enable" );
  }

  @Override
  public String GetArray_ForEachValue(){
    return GetAttrValue("arrayvalue" );
  }

  @Override
  public String GetArray_CoordinateParamName(){
    return GetAttrValue("coordinate" );
  }

  @Override
  public String GetArray_CoordinateParamName(int aCoordinateNum){
    return GetAttrValue("coordinate" + Integer.toString( aCoordinateNum ));
  }

  @Override
  public String GetSwitchParamName(){
    return GetAttrValue("switchparam");
  }

  @Override
  public String GetSwitchValue(){
    return GetAttrValue("switchvalue");
  }

  @Override
  public boolean GetSaveHistoryFlag(){
  	String s = GetAttrValue("storeHistory");
  	if ("true".equalsIgnoreCase(s)) {
  		return true;
  	}
  	return false;
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////
  ////////// ������� ��� ������ ��������� ����� �������� �����
  ///////////////////////////////////////////////////////////////////////////////////////////////

  public static String BLOCK_INDEX_SELF = "self";
  public static String BLOCK_INDEX_ALL = "all";

  public String GetClassName() throws ModelException{
    return GetAttrValue("classname");
  }

  public String GetTitle(){
    return GetAttrValue("title");
  }

  public Rectangle GetRectangle(){
    int x = 0;
    int y = 0;
    int width = 0;
    int height = 0;
    String s = GetAttrValue("x");
    try{
      x = Integer.parseInt( s );
      s = GetAttrValue("y");
      y = Integer.parseInt( s );
      s = GetAttrValue("width");
      width = Integer.parseInt( s );
      s = GetAttrValue("height");
      height = Integer.parseInt( s );
    } catch (Exception e){
      return null;
    }
    Rectangle result = new Rectangle(x,y,width, height);
    return result;
  }

  public String GetCaption(){
    return GetAttrValue("caption");
  }

  public String GetBlockName(){
    return GetAttrValue("block");
  }

  public String GetParamName(){
    return GetAttrValue("param");
  }

  public String GetBlockIndex(){
    return GetAttrValue("blockindex");
  }

  public String GetEventName(){
    return GetAttrValue("eventname");
  }

  public String GetFilterValueType(){
    return GetAttrValue("filtervaluetype");
  }

  public String GetFilterValue(){
    return GetAttrValue("filtervalue");
  }

  public String GetParamNameForXAxis(){
    return GetAttrValue("axisx");
  }

  public String GetParamNameForYAxis(){
    return GetAttrValue("axisy");
  }

  public double GetXAxisMinValue(){
    double i = 0;
    String s = GetAttrValue("minx");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public double GetXAxisMaxValue(){
    double i = 0;
    String s = GetAttrValue("maxx");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public double GetYAxisMinValue(){
    double i = 0;
    String s = GetAttrValue("miny");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public double GetYAxisMaxValue(){
    double i = 0;
    String s = GetAttrValue("maxy");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public double GetXAxisIncrement(){
    double i = 0;
    String s = GetAttrValue("incrementx");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public double GetYAxisIncrement(){
    double i = 0;
    String s = GetAttrValue("incrementy");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public double GetAxisIncrement(){
    double i = 0;
    String s = GetAttrValue("increment");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public double GetAxisMaxValue(){
    double i = 0;
    String s = GetAttrValue("max");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public double GetAxisMinValue(){
    double i = 0;
    String s = GetAttrValue("min");
    try{
      i = Double.parseDouble( s );
    } catch(Exception e){}
    return i;
  }

  public String GetAnimationXCoord(){
    return GetAttrValue("x");
  }

  public String GetAnimationYCoord(){
    return GetAttrValue("y");
  }

  public String GetAnimationFigureSizeParamName(){
    return GetAttrValue("size");
  }

  public String GetAnimationFigureWidthParamName(){
    return GetAttrValue("width");
  }

  public String GetAnimationFigureHeightParamName(){
    return GetAttrValue("height");
  }

  public String GetAnimationColour(){
    return GetAttrValue("colour");
  }

  public String GetAnimationFigureType(){
    return GetAttrValue("type");
  }

  public double GetAnimationInitXCoord() throws ModelException{
    String s = GetAttrValue("initXCoord");
    if ( s == null || "".equalsIgnoreCase( s ) ){
      return 0;
    }
    try{
      return Double.parseDouble( s );
    } catch (Exception e){
      ModelException e1 = new ModelException("�������� ������ ������ � �������� initXCoord: " + s);
      throw e1;
    }
  }

  public double GetAnimationInitYCoord() throws ModelException{
    String s = GetAttrValue("initYCoord");
    if ( s == null || "".equalsIgnoreCase( s ) ){
      return 0;
    }
    try{
      return Double.parseDouble( s );
    } catch (Exception e){
      ModelException e1 = new ModelException("�������� ������ ������ � �������� initYCoord: " + s);
      throw e1;
    }
  }

  public double GetAnimationInitWidth() throws ModelException{
    String s = GetAttrValue("initWidth");
    if ( s == null || "".equalsIgnoreCase( s ) ){
      return 100;
    }
    try{
      return Double.parseDouble( s );
    } catch (Exception e){
      ModelException e1 = new ModelException("�������� ������ ������ � �������� initWidth: " + s);
      throw e1;
    }
  }

  public double GetAnimationInitHeight() throws ModelException{
    String s = GetAttrValue("initHeight");
    if ( s == null || "".equalsIgnoreCase( s ) ){
      return 100;
    }
    try{
      return Double.parseDouble( s );
    } catch (Exception e){
      ModelException e1 = new ModelException("�������� ������ ������ � �������� initHeight: " + s);
      throw e1;
    }
  }

  public boolean GetAnimationResizeFlag() throws ModelException{
    String s = GetAttrValue("resizeFlag");
    return "true".equalsIgnoreCase(s);
  }



  /**������� ���������� �������� ����
   *
   * @param parentNode - ����, � ������� ������ �������� ����
   * @param nodeName - ������������ �������� ����
   * @param fromIndex - ����� ����, ������� � ������� ����� ������� ����. ���� ����� ����� 0 ���� 1, �� ��������
   * ������ �� ����������� ����. ���� ����� ����� 2, �� �������� ������ ����������� ����. � ��� �����.
   * @return - ���������� �������� ����, ���� null, ���� ������� ���
   */
  public static Node GetChildNodeByName( Node parentNode, String nodeName, int fromIndex ){
    Node result = null;
    NodeList nodes = parentNode.getChildNodes();
    if ( nodes == null ){
      return null;
    }
    int i = 0;
    int count = 0;
    Node node;
    while ( i < nodes.getLength() ){
      node = nodes.item( i );
      if ( node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equalsIgnoreCase( nodeName ) ){
        count++;
      }
      if ( count >= fromIndex ){
        return node;
      }
      i++;
    }
    return result;
  }

  public static Node GetChildNodeByType(Node aNode, int aNodeType) throws ModelException{
    NodeList nodes = aNode.getChildNodes();
    if ( nodes == null ){
      ModelException e = new ModelException("��� �������� ��� ");
      throw e;
    }
    int i = 0;
    Node result = null;
    while ( i < nodes.getLength() ){
      result = nodes.item(i);
      if (  result.getNodeType() == aNodeType  ){
        return result;
      } else result = null;
      i++;
    }
    return result;
  }


   public static String GetSourceCode(Node aNode) throws ModelException {
    Node childNode = GetChildNodeByType( aNode, Node.CDATA_SECTION_NODE );
    if ( childNode ==null ){
      return null;
    }
    CDATASection sourceCode = (CDATASection)childNode;
    return sourceCode.getData();
  }

  public String GetSubModelFileName(){
    return GetAttrValue("file");
  }

  public void AddConstant( String aConstName, String aConstValue ) throws ModelException{
  	throw new ModelException("���������!!!!");
  	/*
    if ( aConstName == null || "".equalsIgnoreCase( aConstName ) ){
      return;
    }
    String[]  constRec = (String[])FConstantList.get( aConstName.toUpperCase() );
    if ( constRec == null ){
      constRec = new String[2];
      constRec[0] = aConstName;
      constRec[1] = aConstValue;
      FConstantList.put( aConstName.toUpperCase(), constRec );
    }
    */
  }

  public String GetConstantValue( String aConstName ){
    if ( aConstName == null || "".equalsIgnoreCase( aConstName ) ){
      return null;
    }
    String[]  constRec = (String[])FConstantList.get( aConstName.toUpperCase() );
    if ( constRec == null ){
      return null;
    }
    return constRec[1];
  }

  public void ClearConstantList(){
    FConstantList.clear();
  }

  @Override
	public String GetModelAttrValue(){
  	return GetAttrValue("model");
  }

	@Override
	public String GetElementName() {		
		return FNode.getNodeName();
	}

}
