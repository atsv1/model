package mp.gui;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import mp.elements.ModelException;
import mp.elements.ModelAddress;
import mp.utils.ModelAttributeReader;

import java.util.Vector;

/**
 * User: Администратор
 * Date: 30.11.2008
 */
public abstract class ModelGUIGraph extends ModelGUIAbstrElement {
  protected double FMinX = 0;
  protected double FMaxX = 100;
  protected double FMinY = 0;
  protected double FMaxY = 100;
  protected double FXIncrement = 10;
  protected double FYIncrement = 10;
  protected Vector FCurvesList;


  protected void CreateNewCurves(Node aFunctionNode) throws ModelException {
    ModelGUICurve curve;
    FAttrReader.SetNode( aFunctionNode );
    String blockIndex = FAttrReader.GetBlockIndex();
    if ( blockIndex == null || "all".equalsIgnoreCase( blockIndex ) ){
      //создаем одну функцию
      //curve = GetNewCurve( aFunctionNode );
      //AddCurve( curve );
      CreateNewCurve( GetCurvesDescrFromNode( aFunctionNode ) );
    }
  }

  private void ReadCurvesInfoFromChildNodes( Node aCurrentNode ) throws ModelException{
    NodeList nodes = aCurrentNode.getChildNodes();
    if ( nodes == null ){
      return;
    }
    int i = 0;
    Node currentNode;
    ///ModelJCCKitCurve curve;
    while ( i < nodes.getLength() ){
      currentNode = nodes.item( i );
      if ( currentNode.getNodeType() == Node.ELEMENT_NODE && currentNode.getNodeName().equalsIgnoreCase("Func") ){
        CreateNewCurves(currentNode);
      }
      i++;
    }

  }

  protected ModelGUICurveDescr GetCurvesDescrFromNode( Node aNode ) throws ModelException{
    ModelGUICurveDescr result = new ModelGUICurveDescr();
    FAttrReader.SetNode( aNode );
    result.Caption = FAttrReader.GetCaption();
    NodeList nodes = aNode.getChildNodes();
    if (nodes == null){
      ModelException e = new ModelException("Отсутствует описание осей в функции " + result.Caption);
      throw e;
    }
    //сначала выясняем - не массив ли это.
    Node arrayNode = ModelAttributeReader.GetChildNodeByName(aNode,"ArrayAxis",1);
    if ( arrayNode != null ){
      result.ArrayAddress = GetNewAddressFromNode( arrayNode );
      result.ISArray = FConnector.IsArray( result.ArrayAddress );
      if ( !result.ISArray ){
        ModelException e = new ModelException( "Параметр \"" + result.ArrayAddress.GetParamName() + "\" не является массивом" );
        throw e;
      }
      result.Connector = FConnector;
      result.XAxisMaxValue = FAttrReader.GetXAxisMaxValue();
      result.XAxisMinValue =FAttrReader.GetXAxisMinValue();
      result.YAxisMaxValue = FAttrReader.GetYAxisMaxValue();
      result.XIncrement = FAttrReader.GetXAxisIncrement();
      result.YIncrement = FAttrReader.GetYAxisIncrement();
      return result;
    }

    Node xAxisNode = ModelAttributeReader.GetChildNodeByName(aNode,"AxisX",1);
    if ( xAxisNode == null ){
      ModelException e = new ModelException( "В функции " + result.Caption + " отсутствует нода с описание оси Х");
      throw e;
    }
    Node yAxisNode = ModelAttributeReader.GetChildNodeByName(aNode,"AxisY",1);
    if ( yAxisNode == null ){
      ModelException e = new ModelException( "В функции " + result.Caption + " отсутствует нода с описание оси Y");
      throw e;
    }
    result.XAddress = GetNewAddressFromNode( xAxisNode );
    result.YAddress = GetNewAddressFromNode( yAxisNode );
    result.Connector = FConnector;
    
    FAttrReader.SetNode( xAxisNode );
    result.XAxisMaxValue = FAttrReader.GetAxisMaxValue();
    result.XAxisMinValue =FAttrReader.GetAxisMinValue();
    result.XIncrement = FAttrReader.GetAxisIncrement();

    FAttrReader.SetNode( yAxisNode );
    result.YAxisMaxValue = FAttrReader.GetAxisMaxValue();
    result.YAxisMinValue = FAttrReader.GetAxisMinValue();
    result.YIncrement = FAttrReader.GetAxisIncrement();
    return result;
  }

  protected ModelGUICurveDescr GetCurvesDescrFromRootNode( String aBlockName, Node aNode) throws ModelException {
    ModelGUICurveDescr result = new ModelGUICurveDescr();
    FAttrReader.SetNode( aNode );
    int blockIndex = -1;
    String blockIndexValue = FAttrReader.GetBlockIndex();
    String xName = FAttrReader.GetParamNameForXAxis();
    String yName = FAttrReader.GetParamNameForYAxis();
    if ( ModelAttributeReader.BLOCK_INDEX_SELF.equalsIgnoreCase( blockIndexValue ) ){
      blockIndex = FConnector.GetBlockIndex( blockIndexValue );
    }
    if ( ModelAttributeReader.BLOCK_INDEX_ALL.equalsIgnoreCase( blockIndexValue ) ){
      //пока ничего не делаем
    } else
    {
      blockIndex = FConnector.GetBlockIndex( blockIndexValue );
    }
    result.XAddress = new ModelAddress(aBlockName,blockIndex, xName);
    result.YAddress = new ModelAddress(aBlockName,blockIndex, yName);
    result.XAxisMaxValue = FAttrReader.GetXAxisMaxValue();
    result.XAxisMinValue =FAttrReader.GetXAxisMinValue();
    result.YAxisMaxValue = FAttrReader.GetYAxisMaxValue();
    result.XIncrement = FAttrReader.GetXAxisIncrement();
    result.YIncrement = FAttrReader.GetYAxisIncrement();
    result.Caption = FAttrReader.GetCaption();
    result.Connector = FConnector;
    return result;
  }


  protected abstract void CreateNewCurve( ModelGUICurveDescr aCurveDescr ) throws ModelException;

  public abstract void SetBoundsManager( GraphBoundsManager aManager );

  /**Процедура чтения информации о графиках, которые необходимо отображать в данно компоненте.
   * Предполагается, что текущее значение FNode установлено на корневую ноду элемента.
   */
  protected void ReadCurvesInfo()  throws ModelException{
    String blockName = FAttrReader.GetBlockName();
    if ( blockName != null && !blockName.equalsIgnoreCase("") ){
      //вся информация о графиках сосредоточена в корневой ноде элемента.
      //CreateCurvesFromRootNode(blockName);
      CreateNewCurve( GetCurvesDescrFromRootNode( blockName, FNode ) );
    }
    ReadCurvesInfoFromChildNodes( FNode );
  }

  public double GetMinX() {
   return FMinX;
 }

  public void SetMinX(double aMinX) {
       FMinX = aMinX;
 }

  public double GetMaxX() {
   return FMaxX;
 }

  public void SetMaxX(double aMaxX) {
       FMaxX = aMaxX;
 }

  public double GetMinY() {
   return FMinY;
 }

  public void SetMinY(double aMinY) {
       FMinY = aMinY;
 }

  public double GetMaxY() {
   return FMaxY;
 }

  public void SetMaxY(double aMaxY) {
       FMaxY = aMaxY;
 }

  private double GetCurvesMaxX(){
    int i = 0;
    double result = Double.MIN_VALUE;
    ModelJCCKitCurve curve;
    while ( i < FCurvesList.size() ){
      curve = (ModelJCCKitCurve) FCurvesList.get( i );
      if ( Double.compare( result, curve.GetMaxX() ) < 0   )
      {
        result = curve.GetMaxX();
      }
      i++;
    }
    if ( Double.compare(result, Double.MIN_VALUE) == 0 ){
      result = FMaxX;
    }
    return result;
  }

  private double GetCurvesMaxY(){
    int i = 0;
    double result = Double.MIN_VALUE;
    ModelJCCKitCurve curve;
    while ( i < FCurvesList.size() ){
      curve = (ModelJCCKitCurve) FCurvesList.get( i );
      if ( Double.compare( result, curve.GetMaxY() ) < 0   )
      {
        result = curve.GetMaxY();
      }
      i++;
    }
    if ( Double.compare(result, Double.MIN_VALUE) == 0 ){
      result = FMaxY;
    }
    return result;
  }

  private double GetCurvesMinX(){
    int i = 0;
    double result = Double.MAX_VALUE;
    ModelJCCKitCurve curve;
    while ( i < FCurvesList.size() ){
      curve = (ModelJCCKitCurve) FCurvesList.get( i );
      if ( Double.compare( result, curve.GetMinX() ) > 0   )
      {
        result = curve.GetMinX();
      }
      i++;
    }
    if ( Double.compare( result, Double.MAX_VALUE ) == 0 ){
      result = FMinX;
    }
    return result;
  }

  private double GetCurvesMinY(){
    int i = 0;
    double result = Double.MAX_VALUE;
    ModelJCCKitCurve curve;
    while ( i < FCurvesList.size() ){
      curve = (ModelJCCKitCurve) FCurvesList.get( i );
      if ( Double.compare( result, curve.GetMinY() ) > 0   )
      {
        result = curve.GetMinY();
      }
      i++;
    }
    if ( Double.compare( result, Double.MAX_VALUE ) == 0 ){
      result = FMinY;
    }
    return result;
  }

  protected abstract void UpdateCanvasBorder();

  private void SetNewBorderToCurves(){
    int i = 0;
    ModelJCCKitCurve curve;
    while ( i < FCurvesList.size() ){
      curve = (ModelJCCKitCurve) FCurvesList.get( i );
      curve.SetMaxX( GetMaxX() );
      curve.SetMaxY( GetMaxY() );
      curve.SetMinX( GetMinX() );
      curve.SetMinY( GetMinY() );
      curve.ClearNotVisiblePoints();
      i++;
    }
  }

  protected void UpdateBorder(){
    boolean isNeedToUpdateCurveBorder = false;
    double newVal = GetCurvesMaxX();
    if ( Double.compare( FMaxX, newVal ) != 0 ){
      isNeedToUpdateCurveBorder = true;
      SetMaxX( newVal );
    }
    newVal = GetCurvesMaxY();
    if ( Double.compare( FMaxY, newVal ) != 0 ){
      isNeedToUpdateCurveBorder = true;
      SetMaxY( newVal );
    }
    newVal = GetCurvesMinX();
    if ( Double.compare( FMinX, newVal ) != 0 ){
      isNeedToUpdateCurveBorder = true;
      SetMinX( newVal );
    }
    newVal = GetCurvesMinY();
    if ( Double.compare( FMinY, newVal ) != 0 ){
      isNeedToUpdateCurveBorder = true;
      SetMinY( newVal );
    }
    if ( isNeedToUpdateCurveBorder ){
      UpdateCanvasBorder();
      SetNewBorderToCurves();
    }
  }

  public void AddGUIElement(ModelGUIElement aElement) {
    //ничего не делаем
  }

  public double GetXIncrement() {
    return FXIncrement;
  }

  public void SetXIncrement(double aXIncrement) {
    this.FXIncrement = aXIncrement;
  }

  public double GetYIncrement() {
    return FYIncrement;
  }

  public void SetYIncrement(double aYIncrement) {
    this.FYIncrement = aYIncrement;
  }

  protected ModelAddress GetNewAddressFromNode( Node aAddressNode ) throws ModelException{
    FAttrReader.SetNode( aAddressNode );
    return new ModelAddress( FAttrReader.GetBlockName(), FConnector.GetBlockIndex( FAttrReader.GetBlockIndex() ),
            FAttrReader.GetParamName() );
  }

  
}
