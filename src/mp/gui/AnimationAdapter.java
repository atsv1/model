package mp.gui;

import mp.elements.ModelException;
import mp.utils.ServiceLocator;

import java.util.Vector;
import java.util.Properties;
import java.lang.reflect.Field;
import java.awt.*;

import jcckit.GraphicsPlotCanvas;
import jcckit.transformation.CartesianTransformation;
import jcckit.graphic.*;
import jcckit.plot.Plot;
import jcckit.plot.CartesianCoordinateSystem;
import jcckit.data.DataPlot;
import jcckit.data.DataCurve;
import jcckit.data.DataPoint;
import jcckit.util.ConfigParameters;
import jcckit.util.PropertiesBasedConfigData;

import javax.swing.*;

/** Класс предназначен для отображения фигур с конкретным графическим пакетом - пакетом jcckit 
 * User: atsv
 * Date: 24.06.2007
 */
public class AnimationAdapter {
  private JPanel FOwnerComponent = null;
  private Vector FFiguresList = null;
  private boolean FIsFirstUpdate = true;
  private GraphicsPlotCanvas FPlotCanvas;
  private CartesianTransformation FTransformation = null;
  private ModelGUIAnimation FAnimation = null;
  private DataPlot FDataPlot = null;
  private double FCurrentMinXCoord = 0;
  private double FCurrentMinYCoord = 0;
  private double FCurrentMaxXCoord = 100;
  private double FCurrentMaxYCoord = 100;
  private ConfigParameters FConfig = null;
  private GraphicalElement[] FElements = null;

  public AnimationAdapter( JPanel aOwnerComponent, ModelGUIAnimation aAnimation,Vector figuresList){
    FOwnerComponent = aOwnerComponent; 
    FFiguresList = figuresList;
    FAnimation = aAnimation;
  }

  /** Запись в конфигуратор параметров, которые не зависят от конкретных фигур
   *
   * @param aConfig
   * @throws ModelException
   */
  private void UpdateConfigConstants( Properties aConfig) throws ModelException{
    if ( aConfig == null ){
      ModelException e = new ModelException("Внутрення ошибка - пустой параметр для конфигурирования анимации");
      throw e;
    }
    aConfig.put("foreground", "0xffffff");
    aConfig.put("background", "100");
    aConfig.put("doubleBuffering ", "false");
    aConfig.put("plot/legendVisible", "false");
    aConfig.put("paper","0.05 0.5 1.1 0");

    aConfig.put("plot/coordinateSystem/className", "jcckit.plot.CartesianCoordinateSystem" );
    aConfig.put("plot/coordinateSystem/xAxis/minimum", Double.toString( FCurrentMinXCoord) );
    aConfig.put("plot/coordinateSystem/xAxis/maximum", Double.toString( FCurrentMaxXCoord ) );
    aConfig.put("plot/coordinateSystem/xAxis/ticLabelFormat", "%d");
    aConfig.put("plot/coordinateSystem/xAxis/automaticTicCalculation", "true");
    aConfig.put("plot/coordinateSystem/xAxis/grid", "true");
    aConfig.put("plot/coordinateSystem/xAxis/axisLabel", "x");

    aConfig.put("plot/coordinateSystem/yAxis/axisLabel", "y");
    aConfig.put("plot/coordinateSystem/yAxis/minimum", Double.toString(FCurrentMinYCoord ));
    aConfig.put("plot/coordinateSystem/yAxis/maximum",  Double.toString( FCurrentMaxYCoord ) );
    aConfig.put("plot/coordinateSystem/yAxis/automaticTicCalculation", "true");
    aConfig.put("plot/coordinateSystem/yAxis/ticLabelFormat", "%d");
    aConfig.put("plot/coordinateSystem/yAxis/grid", "true");
  }

  private static String GetJCCKitClassName( ModelAnimationFigure aFigure ) throws ModelException{
    String s = aFigure.FigureType;
    if ( "circle".equalsIgnoreCase( s ) ){
      return "jcckit.plot.CircleSymbolFactory";
    }
    if ( "rectangle".equalsIgnoreCase( s ) ){
      return "jcckit.plot.SquareSymbolFactory";
    }
    ModelException e = new ModelException("Неизвестный тип фигуры");
    throw e;
  }

  private static String GetFormattedColour( int aColour){
    return "0x" + Integer.toHexString( aColour );  
  }

  /** Добавления в конфигурацию параметров, описывающих конкретные фигуры
   *
   * @param aConfig
   */
  private void UpdateFiguresConfig( Properties aConfig ) throws ModelException{
    String curveName = "curve";
    String currentCurveName = null;
    int figureCounter = 0;
    ModelAnimationFigure figure = null;
    String curvesList = "";
    while ( figureCounter < FFiguresList.size() ){
      figure = (ModelAnimationFigure) FFiguresList.get( figureCounter );
      if ( figure != null ){
        currentCurveName = curveName + Integer.toString( figureCounter );
        curvesList = curvesList + " " + currentCurveName; 
        aConfig.put("plot/curveFactory/" + currentCurveName + "/initialHintForNextPoint/className",
                "jcckit.plot.ShapeAttributesHint");
        aConfig.put("plot/curveFactory/" + currentCurveName + "/initialHintForNextPoint/initialAttributes/fillColor",
                /*GetFormattedColour( (int)figure.GetColour() )*/  "100");
        aConfig.put("plot/curveFactory/" + currentCurveName + "/initialHintForNextPoint/fillColorHSBIncrement",
                 "0.0 0.0 0.5");
        aConfig.put("plot/curveFactory/" + currentCurveName + "/withLine", "false");
        if ( "circle".equalsIgnoreCase( figure.FigureType ) ) {
          aConfig.put("plot/curveFactory/" + currentCurveName + "/symbolFactory/size", Double.toString( 0 )  );
        } else {
          aConfig.put("plot/curveFactory/" + currentCurveName + "/symbolFactory/size", Double.toString( 0)  );
        }
        aConfig.put("plot/curveFactory/" + currentCurveName + "/symbolFactory/className", GetJCCKitClassName(figure) );
      }
      figureCounter++;
    }
    aConfig.put("plot/curveFactory/definitions", curvesList);
  }

  private void RemoveAllDataElements(){
    while (  FDataPlot.getNumberOfElements() > 0){
      FDataPlot.removeElementAt(0);
    }
  }

  /**Функция выясняет, нужно ли обновлять координатную сетку  - т.е. нужно ли изменять значения  левого нижнего угла
   * координат и правого верхнего угла.
   * Обновление координат производится таким образом, чтобы были видны все фигуры
   * Одновременно обновляются значения переменных, которые отвечают за указанные углы 
   *
   * @return - возвращает true, если есть необходимость обновления координатной сетки
   */
  private boolean UpdateCoord_ShowAllFigures() throws ModelException {
    boolean result = false;
    int i = 0;
    ModelAnimationFigure figure = null;
    while ( i < FFiguresList.size() ){
      figure = (ModelAnimationFigure) FFiguresList.get( i );
      if ( figure != null ){
        if ( ServiceLocator.CompareDouble( FCurrentMinXCoord, figure.GetXCoord() ) == 1 ) {
           result = true;
           FCurrentMinXCoord =  figure.GetXCoord();
        }
        if ( ServiceLocator.CompareDouble( FCurrentMinYCoord , figure.GetYCoord() ) == 1 ){
          result = true;
          FCurrentMinYCoord = figure.GetYCoord();
        }
        if ( ServiceLocator.CompareDouble( FCurrentMaxXCoord, figure.GetXCoord() ) == -1 ){
          result = true;
          FCurrentMaxXCoord = figure.GetXCoord();
        }
        if ( ServiceLocator.CompareDouble( FCurrentMaxYCoord, figure.GetYCoord() ) == -1 ){
          result = true;
          FCurrentMaxYCoord = figure.GetYCoord();
        }
      }

      i++;
    }
    return result;
  }

  /**устанавливает новые размеры координатной сетки
   *
   */
  private void SetNewPlotSize(){
    Plot plot = FPlotCanvas.getPlot();
    Properties props = new Properties();
    ConfigParameters config  = new ConfigParameters(new PropertiesBasedConfigData(props));
    props.put("xAxis/minimum", Double.toString( FCurrentMinXCoord ));
    props.put("xAxis/maximum", Double.toString( FCurrentMaxXCoord ) );
    props.put("xAxis/ticLabelFormat", "%d");
    props.put("xAxis/grid", "true");
    props.put("xAxis/axisLabel", "x");

    props.put("yAxis/axisLabel", "y");
    props.put("yAxis/minimum", Double.toString(  FCurrentMinYCoord ));
    props.put("yAxis/maximum",  Double.toString( FCurrentMaxYCoord ) );
    props.put("yAxis/ticLabelFormat", "%d");
    props.put("yAxis/grid", "true");

    CartesianCoordinateSystem coord = new CartesianCoordinateSystem(config);
    plot.setCoordinateSystem( coord );
  }

   private void SetTransformation() throws ModelException{
     Plot plot = FPlotCanvas.getPlot();
     Class cl = plot.getClass();
     Field f = null;
     try {
       f = cl.getDeclaredField("_transformation");
       f.setAccessible( true );
       FTransformation = (CartesianTransformation) f.get( plot );
     } catch (NoSuchFieldException e) {
       ModelException e1 = new ModelException("Не получить объект преобразования координат. Ошибка " + e.getMessage());
       throw e1;
     } catch (IllegalAccessException e) {
       ModelException e1 = new ModelException("Не получить объект преобразования координат. Ошибка " + e.getMessage());
       throw e1;
     }
   }

  /**Метод возвращает размер фигуры, пересчитанный из пользовательского размера в размер, используемый при
   * реальном изображении
   *
    * @return
   */
  private double GetTransformedSize(double aSize){
    if ( FTransformation == null) {
      return aSize;
    }
    GraphPoint gpoint = null;
    DataPoint zeroPoint = new DataPoint(0,0);
    GraphPoint gZeroPoint = FTransformation.transformToGraph( zeroPoint );
    gpoint = FTransformation.transformToGraph( new DataPoint( aSize,0 ) );
    return  Math.abs(  gZeroPoint.getX() -  gpoint.getX());
  }

  /** Метод выполняет обновление визуальных атрибутов фигуры - размера и цвета.
   *
   * @param aFigureIndex - индекс фигуры в списке FFiguresList
   */
  private void SetNewColour( int aFigureIndex, Color aNewColour ) throws ModelException{
    if ( aFigureIndex >=  FFiguresList.size()) {
      ModelException e = new ModelException("Индекс фигуры вышел за пределы массива при изменении цвета");
      throw e;
    }
    if ( FTransformation == null ){
      SetTransformation();
    }
    if ( FElements == null  ){
      return;
    }
    GraphicalElement element = FElements[aFigureIndex];
    Class cl = null;
    GraphicalComposite comp = null;
    cl = element.getClass();
    Vector o = null;
    Field f = null;
    try {
      f = cl.getDeclaredField("_elements");
      f.setAccessible( true );
      o = (Vector) f.get( element );
      if ( o.size() > 0 ){
        comp = (GraphicalComposite) o.get(0);
        o = (Vector) f.get( comp );
        if ( o.size() > 0 ) {
          BasicGraphicalElement ge = (BasicGraphicalElement) o.get(0);
          cl = Class.forName("jcckit.graphic.BasicGraphicalElement");
          f = cl.getDeclaredField("_attributes");
          f.setAccessible( true );
          ShapeAttributes attr = (ShapeAttributes) f.get( ge );
          cl = Class.forName("jcckit.graphic.ShapeAttributes");
          f = cl.getDeclaredField("_fillColor");
          f.setAccessible( true );
          f.set( attr, aNewColour );
        }
      }
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void SetNewSize( int aFigureIndex, ModelAnimationFigure aFigure, double aSize,
                           double aWidth, double aHeight ) throws ModelException{
    if ( aFigureIndex >=  FFiguresList.size()) {
      ModelException e = new ModelException("Индекс фигуры вышел за пределы массива при изменении размера");
      throw e;
    }
    String s = aFigure.FigureType;
    if ( FTransformation == null ){
        SetTransformation();
      }
    if ( "circle".equalsIgnoreCase( s ) ){

      if ( FElements == null  ){
        return;
      }
      GraphicalElement element = FElements[aFigureIndex];
      Class cl = null;
      GraphicalComposite comp = null;
      cl = element.getClass();
      Vector o = null;
      Field f = null;
      try {
        f = cl.getDeclaredField("_elements");
        f.setAccessible( true );
        o = (Vector) f.get( element );
        if ( o.size() > 0 && FTransformation != null ){
          comp = (GraphicalComposite) o.get(0);
          o = (Vector) f.get( comp );
          if ( o.size() > 0 ) {
            BasicGraphicalElement ge = (BasicGraphicalElement) o.get(0);
            cl = Class.forName("jcckit.graphic.Rectangle");
            f = cl.getDeclaredField("_width");
            f.setAccessible( true );
            double newSize;
            newSize = GetTransformedSize( aSize ) ;
            f.setDouble( ge, newSize );
            f = cl.getDeclaredField("_height");
            f.setAccessible( true );
            f.setDouble( ge, newSize );
            //System.out.println("size = " + Double.toString( newSize ));
          }
        }
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      return;
    }
    if ( "rectangle".equalsIgnoreCase(s) ){
      Plot plot = FPlotCanvas.getPlot();
      GraphicalElement[] elements = plot.getCurves();
      if ( elements == null  ){
        return;
      }
      GraphicalElement element = elements[aFigureIndex];
      Class cl = null;
      GraphicalComposite comp = null;
      cl = element.getClass();
      Vector o = null;
      Field f = null;
      try {
        f = cl.getDeclaredField("_elements");
        f.setAccessible( true );
        o = (Vector) f.get( element );
        if ( o.size() > 0 ){
          comp = (GraphicalComposite) o.get(0);
          o = (Vector) f.get( comp );
          if ( o.size() > 0 ) {
            double newSize = 0;
            BasicGraphicalElement ge = (BasicGraphicalElement) o.get(0);
            cl = Class.forName("jcckit.graphic.Rectangle");
            f = cl.getDeclaredField("_width");
            f.setAccessible( true );
            newSize = GetTransformedSize( aWidth );
            f.setDouble( ge, newSize );
            f = cl.getDeclaredField("_height");
            f.setAccessible( true );
            f.setDouble( ge, GetTransformedSize( aHeight ) );
            //System.out.println("width = " + Double.toString( newSize ));
          }
        }
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      return;
    }
    ModelException e = new ModelException("Неизвестный тип фигуры \"" + s + "\"");
    throw e;
  }

  private static void ClearCurve( DataCurve aCurve ){
    if ( aCurve == null || aCurve.getNumberOfElements() == 0 ){
      return;
    }
    while ( aCurve.getNumberOfElements() > 0 ){
      aCurve.removeElementAt( 0 );
    }

  }

  private void UpdateCurves() throws ModelException{
    if ( FAnimation.GetResizeFlag() ){
      //нужно автоматически обновлять размеры анимации
      /*if ( UpdateCoord_ShowAllFigures() ){
        SetNewPlotSize();
      }*/
    }
    int i = 0;
    ModelAnimationFigure figure = null;
    DataCurve curve = null;
    double x;
    double y;
    Color colour = null;
    double height;
    double width;
    double size;
    while ( i < FFiguresList.size() ){
      figure = (ModelAnimationFigure) FFiguresList.get( i );
      x = figure.GetXCoord();
      y = figure.GetYCoord();
      colour = figure.GetColorObject();
      height = figure.GetHeight();
      width = figure.GetWidth();
      size = figure.GetSize();
      if ( figure.IsPositionChanged() || figure.IsColourChanged() || figure.IsSizeChanged() ){
        curve = (DataCurve) FDataPlot.getElement( i );
        ClearCurve( curve );
        //curve.replaceElementAt(0, new DataPoint( x, y ));
        curve.addElement( new DataPoint( x, y ) );
        SetNewColour(i, colour);
        //System.out.println("height = " + Double.toString( height ));
        SetNewSize(i, figure, size, width, height);

      }
      i++;
    }
  }

  /**Начальное добавление фигур в анимацию
   *
   * @throws ModelException
   */
  private void AddCurves() throws ModelException {
    ModelAnimationFigure figure = null;
    int i  = 0;
    RemoveAllDataElements();//на всякий случай удаляем все объекты
    while ( i < FFiguresList.size() ){
      figure = (ModelAnimationFigure) FFiguresList.get( i );
      DataCurve curve = new DataCurve("123");
      curve.addElement( new DataPoint( figure.GetXCoord(), figure.GetYCoord() ) );
      FDataPlot.addElement( curve );
      i++;
    }
    Plot plot = FPlotCanvas.getPlot();
    FElements = plot.getCurves();
  }

  private void SetInitCoordinates(){
    FCurrentMinXCoord = FAnimation.GetInitXCoord();
    FCurrentMinYCoord = FAnimation.GetInitYCoord();
    FCurrentMaxXCoord = FAnimation.GetInitWidth() + FAnimation.GetInitXCoord();
    FCurrentMaxYCoord = FAnimation.GetInitHeight() + FAnimation.GetInitYCoord();
  }

  public void Update() throws ModelException {
    if ( FIsFirstUpdate  ){
      SetInitCoordinates();
      Properties properties = new Properties();
      UpdateConfigConstants( properties );
      UpdateFiguresConfig( properties );
      FConfig  = new ConfigParameters(new PropertiesBasedConfigData( properties ));
      FPlotCanvas = new GraphicsPlotCanvas( FConfig );

      FDataPlot = new DataPlot(  );
      //DataCurve curve = new DataCurve("trajectory");
      //FDataPlot.addElement( curve );
      FIsFirstUpdate = false;
      FPlotCanvas.connect( FDataPlot );
      FOwnerComponent.add( FPlotCanvas.getGraphicsCanvas() );
      AddCurves();
    }
    if ( FIsFirstUpdate && FPlotCanvas != null){
      FOwnerComponent.remove( FPlotCanvas.getGraphicsCanvas() );
      FIsFirstUpdate = false;
    }
    UpdateCurves();
  }

}
