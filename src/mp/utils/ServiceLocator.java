package mp.utils;


/**
 * User: atsv
 * Date: 08.11.2006
 * @noinspection UtilityClass
 */
public  class ServiceLocator {
  private static UniqueIdGenerator FUniqueGeneratorGenerator = new UniqueIdGenerator();  
  public static int COMPARE_FIDELITY = 1000;
  private static NameService names = new NameService();

  public static int GetNextId(){
    return FUniqueGeneratorGenerator.GetNextId();
  }

  public static ModelAttributeReader GetAttributeReader(){
    return null; 
  }

  /**Функция сравнивает два дробных значения. Сравнение производится с учетом установленного в системе коэффициента
   * точности (пеерменная COMPARE_FIDELITY)
   *
   * @param val1 - первое сравниваемое значение
   * @param val2 - второе сравниваемое значение
   * @return - возвращается 0, если переменные (с учетом нужной точности) равны. 1 - если переменная val1 больше
   * переменной val2. -1 - если переменная val1 меньше переменной val2
   */
  public static int CompareDouble( double val1, double val2 ){
    int val1int = (int)val1;
    int val2int = (int)val2;
    if ( val1int > val2int ){
      return 1;
    }
    if ( val1int < val2int ){
      return -1;
    }
    //целые части равны, сравниваем дробные
    val1int = (int) ((val1 - val1int) * COMPARE_FIDELITY);
    val2int = (int) ((val2 - val2int) * COMPARE_FIDELITY);
    if ( val1int > val2int ){
      return 1;
    }
    if ( val1int < val2int ){
      return -1;
    }
    return 0;
  }

  public static NameService GetNamesList(){
    return names;
  }

}
