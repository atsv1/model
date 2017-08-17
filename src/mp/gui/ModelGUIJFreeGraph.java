package mp.gui;


import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import mp.elements.ModelException;

import javax.swing.*;
import java.awt.*;

/**
 * User: Администратор
 * Date: 30.11.2008
 */
public class ModelGUIJFreeGraph extends ModelGUIGraph {
  private JPanel FMainPanel = null;
  private boolean FCreateFlag = true;
  private ModelGUIFreeChartCurves FCurves = null;
  private JFreeChart FChart = null;
  private String FCaption = null;
  private GraphBoundsManager FBoundsManager = null;

  public ModelGUIJFreeGraph(){
    FMainPanel = new JPanel( ); 
  }



  protected void CreateNewCurve(ModelGUICurveDescr aCurveDescr) throws ModelException {
    if ( FCurves == null ){
      FCurves = new ModelGUIFreeChartCurves();
      FCurves.SetBoundsManager( FBoundsManager );
    }
    FCurves.CreateNewCurve( aCurveDescr );
  }

  public void SetBoundsManager(GraphBoundsManager aManager) {
    FBoundsManager = aManager;

  }

  protected void UpdateCanvasBorder() {
  }


  public void ReadDataFromNode() throws ModelException {    
    this.ReadCoordFromNode( FMainPanel );
    ReadCurvesInfo();
  }

  public Component GetComponent() {
    return FMainPanel;
  }

  private void CreateChart() throws ModelException{
    FChart = ChartFactory.createXYLineChart(
       FCaption,
       "x",
       "y",
       FCurves,
       PlotOrientation.VERTICAL,
       true,
       false,
       false
    );
    ChartPanel panel = new ChartPanel( FChart );
    Rectangle r = FMainPanel.getBounds();
    panel.setPreferredSize( new Dimension( r.width, (int)(r.height *0.975) ) );
    FMainPanel.add( panel );
    XYPlot linePlot = (XYPlot)FChart.getPlot();

    ValueAxis yAxis;
    ValueAxis xAxis;
    yAxis = linePlot.getRangeAxis();
    xAxis = linePlot.getDomainAxis();
    if ( FCurves != null ) {
      FCurves.SetBoundsManager( FBoundsManager );
      FCurves.SetAxis( xAxis, yAxis );

    }
  }

  public void Update() throws ModelException{
    if ( FCreateFlag ){
      CreateChart();
      FCreateFlag = false;
    }
    if ( FCurves != null ) {
      FCurves.UpdateAll();
    }
  }

}
