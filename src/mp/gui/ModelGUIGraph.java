package mp.gui;


import mp.elements.ModelException;
import mp.elements.ModelAddress;
import mp.elements.ModelElementDataSource;
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


  protected void CreateNewCurves(ModelElementDataSource aFunctionNode) throws ModelException {
    ModelGUICurve curve;
    
    String blockIndex = this.GetDataSource().GetBlockIndex();
    if ( blockIndex == null || "all".equalsIgnoreCase( blockIndex ) ){
      //создаем одну функцию
      //curve = GetNewCurve( aFunctionNode );
      //AddCurve( curve );
      CreateNewCurve( GetCurvesDescrFromNode( aFunctionNode ) );
    }
  }

  private void ReadCurvesInfoFromChildNodes( ModelElementDataSource aCurrentNode ) throws ModelException{
  	java.util.List<ModelElementDataSource> funcs = aCurrentNode.GetChildElements("Func");
  	if ( funcs == null ) {
  		return;
  	}
  	for (ModelElementDataSource func : funcs) {
  		CreateNewCurves(func);
  	}
  }

  protected ModelGUICurveDescr GetCurvesDescrFromNode( ModelElementDataSource aNode ) throws ModelException{
    ModelGUICurveDescr result = new ModelGUICurveDescr();
    
    result.Caption = this.GetDataSource().GetCaption();
    
    //сначала выясняем - не массив ли это.
    ModelElementDataSource arrayNode = aNode.GetChildElement("ArrayAxis");
    if ( arrayNode != null ){
      result.ArrayAddress = GetNewAddressFromNode( arrayNode );
      result.ISArray = FConnector.IsArray( result.ArrayAddress );
      if ( !result.ISArray ){
        ModelException e = new ModelException( "Параметр \"" + result.ArrayAddress.GetParamName() + "\" не является массивом" );
        throw e;
      }
      result.Connector = FConnector;
      result.XAxisMaxValue = arrayNode.GetXAxisMaxValue();
      result.XAxisMinValue =arrayNode.GetXAxisMinValue();
      result.YAxisMaxValue = arrayNode.GetYAxisMaxValue();
      result.XIncrement = arrayNode.GetXAxisIncrement();
      result.YIncrement = arrayNode.GetYAxisIncrement();
      return result;
    }

    ModelElementDataSource xAxisNode =  aNode.GetChildElement("AxisX"); 
    if ( xAxisNode == null ){
      ModelException e = new ModelException( "В функции " + result.Caption + " отсутствует нода с описание оси Х");
      throw e;
    }
    ModelElementDataSource yAxisNode = aNode.GetChildElement("AxisY");
    if ( yAxisNode == null ){
      ModelException e = new ModelException( "В функции " + result.Caption + " отсутствует нода с описание оси Y");
      throw e;
    }
    result.XAddress = GetNewAddressFromNode( xAxisNode );
    result.YAddress = GetNewAddressFromNode( yAxisNode );
    result.Connector = FConnector;
        
    result.XAxisMaxValue = xAxisNode.GetAxisMaxValue();
    result.XAxisMinValue =xAxisNode.GetAxisMinValue();
    result.XIncrement = xAxisNode.GetAxisIncrement();

    result.YAxisMaxValue = yAxisNode.GetAxisMaxValue();
    result.YAxisMinValue = yAxisNode.GetAxisMinValue();
    result.YIncrement = yAxisNode.GetAxisIncrement();
    return result;
  }

  protected ModelGUICurveDescr GetCurvesDescrFromRootNode( String aBlockName, ModelElementDataSource aNode) throws ModelException {
    ModelGUICurveDescr result = new ModelGUICurveDescr();
    
    int blockIndex = -1;
    String blockIndexValue = aNode.GetBlockIndex();
    String xName = aNode.GetParamNameForXAxis();
    String yName = aNode.GetParamNameForYAxis();
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
    result.XAxisMaxValue = aNode.GetXAxisMaxValue();
    result.XAxisMinValue =aNode.GetXAxisMinValue();
    result.YAxisMaxValue = aNode.GetYAxisMaxValue();
    result.XIncrement = aNode.GetXAxisIncrement();
    result.YIncrement = aNode.GetYAxisIncrement();
    result.Caption = aNode.GetCaption();
    result.Connector = FConnector;
    return result;
  }


  protected abstract void CreateNewCurve( ModelGUICurveDescr aCurveDescr ) throws ModelException;

  public abstract void SetBoundsManager( GraphBoundsManager aManager );

  /**Процедура чтения информации о графиках, которые необходимо отображать в данно компоненте.
   * Предполагается, что текущее значение FNode установлено на корневую ноду элемента.
   */
  protected void ReadCurvesInfo()  throws ModelException{
    String blockName = this.GetDataSource().GetBlockName();
    if ( blockName != null && !blockName.equalsIgnoreCase("") ){
      //вся информация о графиках сосредоточена в корневой ноде элемента.
      //CreateCurvesFromRootNode(blockName);
      CreateNewCurve( GetCurvesDescrFromRootNode( blockName, this.GetDataSource() ) );
    }
    ReadCurvesInfoFromChildNodes( this.GetDataSource() );
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

  protected ModelAddress GetNewAddressFromNode( ModelElementDataSource aAddressNode ) throws ModelException{    
    return new ModelAddress( aAddressNode.GetBlockName(), FConnector.GetBlockIndex( aAddressNode.GetBlockIndex() ),
    		aAddressNode.GetParamName() );
  }

  
}
