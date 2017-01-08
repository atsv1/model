package mp.elements;

import mp.parser.Variable;


/**Абстрактный класс, который обеспечивает обмен данными между моделью и интерфейсом пользователя.
 *
 * User: atsv
 * Date: 30.09.2006
 */
public interface ModelConnector {

  /**Функция возвращает значение из параметра, обозначенного в параметрах метода
   *
   * @param aBlockName - название блока, обязательный параметр
   * @param aBlockIndex - индекс блока. Если индекс равен -1, то это означает, что поиск блока осуществляется без
   * индекса. Т.е. если блоков с таким именем в модели много, то параметр найден не будет, поскольку модель не сможет
   * определить, из какого именно блока нужно получить параметр
   * @param aParamName - название параметра.
   * @return - возвращаемое числовое значение
   * @throws ModelException
   */
  //public abstract double GetValue(String aBlockName, int aBlockIndex, String aParamName) throws ModelException;

  public abstract double GetValue(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException;

  public abstract double GetValue(ModelAddress address) throws ModelException;

  public abstract boolean GetBooleanValue(ModelAddress address) throws ModelException;

  public abstract int GetIntValue(ModelAddress address) throws ModelException;

  public abstract String GetStringValue(ModelAddress address) throws ModelException;

  public abstract String GetStringValue(String aModelName, String aBlockName, int aBlockIndex, String aParamName) throws ModelException;

  public abstract void StartModel() throws ModelException;

  public abstract void StopModel();

  public abstract void PauseModel();

  public abstract void ResumeModel();

  public abstract String GetErrorString();

  /**Функция должна возвращать конкретный индекс блока.
   *
   * @param aBlockIndexValue - значение, взятое из атрибута "blockindex" файла описания формы. Может содержать как
   * конкретное числовое значение, так и ключевые слова "self", "any". Слово "all" запрещено.
   * @return - индекс блока. Если возвращается -1, то к блоку будет происходить обращение без индекса.
   */
  public abstract int GetBlockIndex(String aBlockIndexValue);

  /**Возвращает количество блоков в модели, у которых имя совпадает с именем, заданным в параметре
   *
   * @param aBlockName - название блоков, количество которых необходимо выяснить
   * @return - количество блоков
   * @throws ModelException
   */
  public abstract int GetBlockCount(String aModelName, String aBlockName) throws ModelException;

  public abstract void SendValue( double aValue,  String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;
  public abstract void SendValue( boolean aValue, String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;

  /** Функция проверки возможности присоединения к некоторому объекту модели.
   *  Проверки производятся с учетом подключившегося пользователя, типа элемента, к которому производится подключение.
   * @param aBlockName
   * @param aBlockIndex
   * @param aParamName
   * @return Возвращается true, если подключение возможно, false - если подключение невозможно
   */
  public abstract boolean IsConnectionEnabled(String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;

  /** Функция проверки возможности управления данным элементом модели. Проверяется, может ли данный подключившийся
   * пользователь передавать значение в этот параметр модели.
   *
   * @param aBlockName
   * @param aBlockIndex
   * @param aParamName
   * @return
   */
  public abstract boolean IsManagingEnabled(String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;

  public abstract int GetValueType(String aModelName, String aBlockName, int aBlockIndex, String aParamName ) throws ModelException;

  public abstract void FireBlockEvent(String aBlockName, int aBlockIndex, String aEventName) throws ModelException;

  /** Функция сравнивает значение, переданное в первом параметре, и значение из модели, описываемое остальными
   * параметрами
   *
   * @param aVarToCompare
   * @param aBlockName
   * @param aBlockIndex
   * @param aParamName
   * @return 0 - если значения равны, 1 - если значение в объекте больше, чем значение в параметре, -1 - если
   *          значение в параметре больше, чем в объекте
   * @throws ModelException
   */
  public abstract int Compare( Variable aVarToCompare, String aModelName, String aBlockName, int aBlockIndex, String aParamName  ) throws ModelException;

  public abstract boolean IsArray( ModelAddress address ) throws ModelException;

  public abstract int GetArrayDimensionCount( ModelAddress address ) throws ModelException;

  public abstract int GetArrayDimensionLength( ModelAddress address, int dimension ) throws ModelException;

  public abstract double GetArrayValue( ModelAddress address, int[] coordinates ) throws ModelException;

  public abstract boolean IsHistoryExists(ModelAddress address) throws ModelException;

  public abstract String GetHistoryStringValue(ModelAddress address, int index) throws ModelException;


}
