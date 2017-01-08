package mp.utils;

/**
 * User: atsv
 * Date: 08.11.2006
 */
public class UniqueIdGenerator {
  private int FId = 0;

  public int GetNextId(){
    FId++;
    return FId;
  }


}
