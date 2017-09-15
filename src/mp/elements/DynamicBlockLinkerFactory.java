package mp.elements;

/**
 * User: atsv
 * Date: 26.01.2007
 *
 * Класс предназначен для создания линкера, который будет соединять выбранный
 * мультиплексором блок с блоком-владельцем этого же мультиплексора.  
 */
public class DynamicBlockLinkerFactory {

  private static boolean IsBlockLinked( int aId, ModelBlock aLinkedBlock ){
    ModelInputBlockParam param = (ModelInputBlockParam) aLinkedBlock.GetInpParam(0);
    int elementId = 0;
    ModelElement linkedElementOwner = null;
    int i = 0;
    while ( param != null ){
      linkedElementOwner = param.GetLinkedElementOwner();
      if ( linkedElementOwner != null ){
        elementId = linkedElementOwner.GetElementId();
        if ( elementId == aId ){
          return true;
        }
      }
      i++;
      param = (ModelInputBlockParam) aLinkedBlock.GetInpParam( i );
    }
    return false;    
  }

  /**Метод проверяет, является ли созданный мультиплексор таким мультиплексором, при помощи которого организована
   * ситуация "конкуренция за потребителя", то есть когда много источников и один приемник,
   *
   * @param aMux - мультиплексор, для которого производится проверка
   * @return - возвращается true, если мультиплексор действительно организует ситуацию "конкуренция за потребителя".
   * Иначе возвращается false
   */
  private static boolean IsRecieverCompetition( ModelMultiplexor aMux ){
    /**Алгоритм основан на том факте, что входные параметры владельца мультиплексора выполняют
     * присоединение к элементам мультиплексора. Если во владельце мультиплексора есть хотя бы один входной
     * элемент, который был бы присоединен к мультиплексору, то нужно возвращать true
     */
    ModelBlock muxOwner = aMux.GetMuxOwner();
    if ( muxOwner == null ){
      return false;
    }
    int muxId = aMux.GetElementId();
    return IsBlockLinked( muxId, muxOwner );

  }

  /** Метод проверяет, является ли переданный в параметре мультиплексор таким мультиплексором, с помощью которого
   * организована ситуация "конкуренция за источник" - т.е. когда несколько приемников черех мультиплексор
   * пытаются присоединиться к одному источнику
   *
   * @param aMux - мультиплексор, для которого производится проверка
   * @return - возвращает true, если много приемников присоединены к одному источнику, иначе возвращает false  
   */
  private static boolean IsSenderCompetition( ModelDynamicBlock aMux ){
    /**Алгоритм основан на следующем факте: эталонный блок присоединяется своими входными параметрами к мультиплексору.
     * Если хотя бы один из входных параметров эталонного блока ссылается на   элемент мультиплексора, то метод в
     * возвращает true
     */
    ModelBlock muxEtalon = aMux.GetEtalon();
    if ( muxEtalon == null ){
      return false;
    }
    int muxId = aMux.GetElementId();
    return IsBlockLinked( muxId, muxEtalon );
  }

  public static ModelMultiplexorLinker GetLinker( ModelMultiplexor aMux ) throws ModelException {
    if ( aMux == null ){
      ModelException e = new ModelException("В LinkerFactory передан пустой мультиплексор");
      throw e;
    }
    //проверяем ситуацию "конкуренция за приемник"
    boolean recieverCompetition = IsRecieverCompetition( aMux );
    //проверяем ситуацию "конкуренция за источник"
    boolean senderCompetition = IsSenderCompetition( aMux );
    if ( recieverCompetition && !senderCompetition ){
      return new ModelMuxBlockLinker(aMux, aMux.GetMuxOwner());
    }
    if ( !recieverCompetition && senderCompetition ) {
      return new ModelOneSourceManyReciever( aMux, aMux.GetMuxOwner() );
    }
    //return null;
    if ( !recieverCompetition && !senderCompetition ){
      ModelException e = new ModelException("Не определить тип соединения мультиплексора \"" + aMux.GetFullName() +
              "\": не присутствует ни один из типов конкуренции");
      throw e;
    }
    ModelException e = new ModelException("Не определить тип соединения мультиплексора \"" + aMux.GetFullName() +
              "\":  присутствуют все типы конкуренции");
      throw e;
  }

}
