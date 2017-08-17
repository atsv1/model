package mp.gui;


import mp.elements.ModelAddress;
import mp.elements.ModelException;
import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;
import mp.elements.ModelConnector;
import mp.elements.ModelElementDataSource;

import java.awt.*;

/**����� �������� �� ��������� ����� �� ������� Animation.
 *
 * User: atsv
 * Date: 23.06.2007
 */
public class ModelAnimationFigure {  
  private String FAnimationName = null;
  private ModelConnector FConnector = null;
  private ModelAddress FAddress = null;
  //�������� ���������� �����, �� ������� ����� ����� ��������������� ����������
  protected String CoordX = null;
  protected String CoordY = null;
  protected String FigureSize = null;
  protected String FigureColour = null;
  protected String FigureHeight = null;
  protected String FigureWidth = null;
  //������������ �����, �� �������� ����� ���������� ������
  protected String FBlockName = null;
  protected int FBlockIndex = -1;
  protected String FigureType = null;
  private double FPreviousColour = 0;
  private double FPreviousSize = 0;
  private double FPreviousHeight = 0;
  private double FCurrentWidth = 0;
  private double FCurrentHeight = 0;
  private double FPreviousWidth = 0;
  private double FCurrentColour = 0;
  private double FCurrentSize = 0;
  private boolean FIsDoubleSizeFigure = false;
  private double FPreviousXCoord = 0;
  private double FPreviousYCoord = 0;
  private double FCurrentXCoord = 0;
  private double FCurrentYCoord = 0;
  private ModelElementDataSource elementSource;

  public ModelAnimationFigure( ModelElementDataSource aNode, String aAnimationName, ModelConnector aConnector){
  	elementSource = aNode;
    FAnimationName = aAnimationName;
    FConnector = aConnector;
  }

  protected ModelAddress GetAddress(ModelElementDataSource aAttrReader){
    String blockname = aAttrReader.GetBlockName();
    String paramName = aAttrReader.GetParamName();
    String blockIndex = aAttrReader.GetBlockIndex();
    String modelName = aAttrReader.GetModelAttrValue();
    return new ModelAddress( modelName, blockname, FConnector.GetBlockIndex( blockIndex ), paramName  );
  }

  private void ReadNodeInfo() throws ModelException {
    
    CoordX = elementSource.GetAnimationXCoord();
    if ( CoordX == null || "".equalsIgnoreCase( CoordX ) ){
      ModelException e = new ModelException("������ �������� \"" + FAnimationName + "\": ������ X-����������");
      throw e;
    }
    CoordY = elementSource.GetAnimationYCoord();
    if ( CoordY == null || "".equalsIgnoreCase( CoordY ) ){
      ModelException e = new ModelException("������ �������� \"" + FAnimationName + "\": ������ Y-����������");
      throw e;
    }
    FigureSize = elementSource.GetAnimationFigureSizeParamName();
    if ( FigureSize == null ){
      //� ����������� ������ ��� ��������� size. ������, ������ ������������ ����� ����������� - ������� � �������
      // ������� ��������� ��
      FigureHeight = elementSource.GetAnimationFigureHeightParamName();
      FigureWidth = elementSource.GetAnimationFigureWidthParamName();
      if ( FigureHeight == null || FigureWidth == null ){
        ModelException e = new ModelException("����������� ������� ��� �������� \"" + FAnimationName + "\"");
        throw e;
      }
      FIsDoubleSizeFigure = true;
    }
    FigureColour = elementSource.GetAnimationColour();
    FBlockName = elementSource.GetBlockName();
    if ( FBlockName == null || "".equalsIgnoreCase( FBlockName ) ){
      ModelException e = new ModelException("������ �������� \"" + FAnimationName + "\": ����������� �������� �����");
      throw e;
    }
     FigureType = elementSource.GetAnimationFigureType();
    if ( FigureType == null || FigureType.equalsIgnoreCase("") ){
      ModelException e = new ModelException("������ �������� \"" + FAnimationName + "\": ����������� ��� �������� ������");
      throw e;
    }
    FAddress = GetAddress(elementSource);
  }

  public int ApplyNodeInformation() throws ModelException {
    ReadNodeInfo();
    int result = 0;
    
    String temp = elementSource.GetBlockIndex();
    if ( "all".equalsIgnoreCase(temp) ){
      FBlockIndex = 0;
      int blockCount = FConnector.GetBlockCount(FAddress.GetModelName(), FBlockName );
      result = blockCount - 1;
    } else{
      if ( temp != null && !temp.equalsIgnoreCase("") ){
        try {
          FBlockIndex = Integer.parseInt( temp );
        } catch (Exception e){
          ModelException e1 = new ModelException("������ �������� \"" + FAnimationName + "\": �������� �������� ������� �����");
          throw e1;
        }
      }
    }
    return result;
  }

  public void ApplyNodeInformation( int aBlockIndex ) throws ModelException {
    ReadNodeInfo();
    FBlockIndex = aBlockIndex;
  }

  public double GetXCoord() throws ModelException {
    FPreviousXCoord = FCurrentXCoord;
    FCurrentXCoord = FConnector.GetValue( FAddress.GetModelName(), FBlockName, FBlockIndex, CoordX );
    return FCurrentXCoord;
  }

  public double GetYCoord() throws ModelException {
    FPreviousYCoord = FCurrentYCoord;
    FCurrentYCoord = FConnector.GetValue( FAddress.GetModelName(), FBlockName, FBlockIndex, CoordY );
    return FCurrentYCoord;
  }

  public double GetColour() throws ModelException {
    FPreviousColour = FCurrentColour;
    FCurrentColour = FConnector.GetValue( FAddress.GetModelName(),FBlockName, FBlockIndex, FigureColour );
    return FCurrentColour;
  }

  public Color GetColorObject() throws ModelException {
    FPreviousColour = FCurrentColour;
    FCurrentColour = FConnector.GetValue( FAddress.GetModelName(), FBlockName, FBlockIndex, FigureColour );
    Color result = new Color( (int)FCurrentColour );
    return result;
  }

  public double GetSize() throws ModelException {
    if ( FigureSize == null ){
      return 0;
    }
    FPreviousSize = FCurrentSize;
    FCurrentSize = FConnector.GetValue( FAddress.GetModelName(),FBlockName, FBlockIndex, FigureSize );
    return FCurrentSize;
  }

  public double GetWidth() throws ModelException{
    if ( FigureWidth == null ){
      return 0;
    }
    FPreviousWidth = FCurrentWidth;
    FCurrentWidth = FConnector.GetValue( FAddress.GetModelName(), FBlockName, FBlockIndex, FigureWidth );
    return FCurrentWidth;
  }

  public double GetHeight() throws ModelException {
    if ( FigureHeight == null ){
      return 0;
    }
    FPreviousHeight = FCurrentHeight;
    FCurrentHeight = FConnector.GetValue( FAddress.GetModelName(), FBlockName, FBlockIndex, FigureHeight );
    return FCurrentHeight;
  }

  /**������� ������������� ��� ���������� �� ��������� �������� ������. ��������� �������� ������ ��� ������ �������
   * GetSize().
   *
   * @return true -���� ������ ���������
   */
  public boolean IsSizeChanged(){
    if ( !FIsDoubleSizeFigure ) {
      return ServiceLocator.CompareDouble( FPreviousSize,  FCurrentSize ) != 0;
    }
    return ( (ServiceLocator.CompareDouble( FPreviousHeight, FCurrentHeight ) != 0) ||
            (ServiceLocator.CompareDouble( FPreviousWidth, FCurrentWidth ) != 0) );
  }

  /** ������� ������������� ��� ���������� �� ��������� ����� ������. ��������� �������� ������ ��� ������ �������
   * GetColour()
   * @return true -���� ���� ���������
   */
  public boolean IsColourChanged(){
    int res = ServiceLocator.CompareDouble( FCurrentColour, FPreviousColour );
    return ( res != 0);
  }

  public boolean IsPositionChanged(){
    return ( (ServiceLocator.CompareDouble( FPreviousXCoord, FCurrentXCoord ) != 0) ||
            (ServiceLocator.CompareDouble( FPreviousYCoord, FCurrentYCoord ) != 0) );
  }

}
