package mp.gui;


import jcckit.GraphicsPlotCanvas;
import jcckit.plot.Plot;
import jcckit.plot.CartesianCoordinateSystem;
import jcckit.data.DataPlot;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.Properties;
import java.util.Vector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import mp.elements.*;
import mp.utils.ModelAttributeReader;

/**
 * User: atsv
 * Date: 01.10.2006
 */
public class ModelGUIJCCKitGraph extends ModelGUIGraph implements ModelGUIElement{
  private JPanel FMainPanel = null;
  private JLabel FCaptionLabel = null;

  private DataPlot FDataPlot;
  private GraphicsPlotCanvas FPlotCanvas;

  private GraphicsPlotCanvas GetNewGraphicCanvas(){
    Properties props = new Properties();
    ConfigParameters config  = new ConfigParameters(new PropertiesBasedConfigData(props));
    props.put("foreground", "0xffffff");
    props.put("background", "100");
    props.put("doubleBuffering ", "true");
    props.put("plot/legendVisible", "true");
    props.put("paper","0 0 1 0.6");
    //props.put("horizontalAnchor ", "left");
   // props.put("verticalAnchor ", "top");

    props.put("plot/coordinateSystem/className", "jcckit.plot.CartesianCoordinateSystem" );
    props.put("plot/coordinateSystem/xAxis/minimum", Double.toString(FMinX) );
    props.put("plot/coordinateSystem/xAxis/maximum", Double.toString(FMaxX) );
    props.put("plot/coordinateSystem/xAxis/ticLabelFormat", "%d");
    props.put("plot/coordinateSystem/xAxis/automaticTicCalculation", "true");
    props.put("plot/coordinateSystem/xAxis/grid", "true");

    props.put("plot/coordinateSystem/yAxis/axisLabel", "z");
    props.put("plot/coordinateSystem/yAxis/minimum", Double.toString(FMinY));
    props.put("plot/coordinateSystem/yAxis/maximum",  Double.toString(FMaxY) );
    props.put("plot/coordinateSystem/yAxis/ticLabelFormat", "%d");
    props.put("plot/coordinateSystem/yAxis/grid", "true");

    props.put("plot/curveFactory/definitions", "curve");
    props.put("plot/curveFactory/curve/initialHintForNextPoint/className","jcckit.plot.ShapeAttributesHint");
    props.put("plot/curveFactory/curve/initialHintForNextPoint/initialAttributes/fillColor", "0x40a");
    props.put("plot/curveFactory/curve/initialHintForNextPoint/fillColorHSBIncrement", "0.0 0.0 0.5");
    props.put("plot/curveFactory/curve/withLine", "true");
    props.put("plot/curveFactory/curve/symbolFactory/className","jcckit.plot.CircleSymbolFactory");
    props.put("plot/curveFactory/curve/symbolFactory/size", "0.001");

    return new GraphicsPlotCanvas(config);
  }


  public ModelGUIJCCKitGraph(){
    FMainPanel = new JPanel( new BorderLayout());
    FMainPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );
    FCaptionLabel = new JLabel("caption");
    FMainPanel.add( FCaptionLabel, BorderLayout.NORTH );
    FPlotCanvas = GetNewGraphicCanvas();
    FMainPanel.add( FPlotCanvas.getGraphicsCanvas(), BorderLayout.CENTER );
    FDataPlot = new DataPlot();
   // FDataPlot.addElement(new DataCurve("“ест1")); // говор€т, так надо
    FPlotCanvas.connect(FDataPlot);
    FCurvesList = new Vector();
  }

  public void ReadDataFromNode() throws ModelException {
    if ( FNode == null ){
      return;
    }
    FAttrReader.SetNode( FNode );
    this.ReadCoordFromNode( FMainPanel );
    //this.ReadCaption( FCaptionLabel );
    FCaptionLabel.setText( this.GetName() );
    ReadCurvesInfo();
    UpdateBorder();
    //Update();
    FMainPanel.updateUI();
  }


  public Component GetComponent() {
    return FMainPanel;
  }

  protected void AddCurve( ModelGUICurve aCurve ){
    if ( aCurve == null ){
      return;
    }
    FCurvesList.add( aCurve );
    ModelJCCKitCurve curve = (ModelJCCKitCurve) aCurve;
    FDataPlot.addElement( curve.GetCurve() );
  }

  protected void CreateNewCurve(ModelGUICurveDescr aCurveDescr) {
    ModelJCCKitCurve result = new ModelJCCKitCurve(aCurveDescr.XAddress, aCurveDescr.YAddress, aCurveDescr.Caption, FConnector);
    result.SetMaxX( aCurveDescr.XAxisMaxValue );
    result.SetMinX( aCurveDescr.XAxisMinValue );
    result.SetXIncrement( aCurveDescr.XIncrement );
    result.SetMaxY( aCurveDescr.YAxisMaxValue );
    result.SetMinY( aCurveDescr.YAxisMinValue );
    result.SetYIncrement( aCurveDescr.YIncrement );
    AddCurve( result );
  }

  public void SetBoundsManager(GraphBoundsManager aManager) {
    //ничего не делаем
  }


  protected void UpdateCanvasBorder(){
    if ( FMaxX < FMinX || FMaxY < FMinY ) {
      return;
    }
    if ( FMaxX == Double.MAX_VALUE || FMaxY == Double.MAX_VALUE || FMinX == Double.MIN_VALUE || FMinY == Double.MIN_VALUE ){
      return;
    }
    Plot plot = FPlotCanvas.getPlot();
    Properties props = new Properties();
    ConfigParameters config  = new ConfigParameters(new PropertiesBasedConfigData(props));
    props.put("xAxis/minimum", Double.toString(GetMinX()));
    props.put("xAxis/maximum", Double.toString(GetMaxX()) );
    props.put("xAxis/ticLabelFormat", "%d");
    props.put("xAxis/grid", "true");

    props.put("yAxis/axisLabel", "z");
    props.put("yAxis/minimum", Double.toString(GetMinY()));
    props.put("yAxis/maximum",  Double.toString(GetMaxY()) );
    props.put("yAxis/ticLabelFormat", "%d");
    props.put("yAxis/grid", "true");

    CartesianCoordinateSystem coord = new CartesianCoordinateSystem(config);
    plot.setCoordinateSystem( coord );

  }


  public void Update() throws ModelException{
    int i = 0;
    ModelJCCKitCurve curve;
    while ( i  < FCurvesList.size()){
      curve = (ModelJCCKitCurve) FCurvesList.get( i );
      curve.Update();
      i++;
    }
    UpdateBorder();
  }

}
