package mp.parser;

import java.util.Vector;

/**
 * User: саша
 * Date: 30.06.2008
 */
public class ArrayDefinition {
  private Vector FDimensions = new Vector();
  private int FDimPointer = 0;
  private int FValueType = 0;
  private String FInitValue = null;

  public void AddDimension( int aElemensCount ){
    FDimensions.add(aElemensCount);
  }

  public int GetLastDimensionLength(){
    FDimPointer = FDimensions.size() - 1;
    if ( FDimPointer < 0 ){
      return -1;
    }
    Integer len = (Integer) FDimensions.get( FDimPointer );
    return len;
  }

  public int GetPreviousDimensionLength(){
    FDimPointer--;
    if ( FDimPointer < 0 ){
      return -1;
    }
    Integer len = (Integer) FDimensions.get( FDimPointer );
    return len;
  }

  public void SetValueType( int aValueType ){
    FValueType = aValueType;
  }

  public int GetValueType(){
    return FValueType; 
  }

  public int GetDimensionCount(){
    return FDimensions.size(); 
  }

  public int GetDimensionLength( int aDimensionNum ){
    if ( aDimensionNum < 0 || aDimensionNum > FDimensions.size() ){
      return -1;
    }
    return (Integer)FDimensions.get( aDimensionNum ); 
  }

  public void SetInitValue( String aInitValue ){
    FInitValue = aInitValue;
  }

  public String GetInitValue(){
    return FInitValue;
  }


}
