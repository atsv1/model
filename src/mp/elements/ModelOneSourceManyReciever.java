package mp.elements;

import java.util.Vector;

/**
 * User: atsv
 * Date: 13.01.2007
 * Класс выполняет соединение между источником и приемником данных в такой вот ситуации: есть один источник
 * и несколько приемников. Мультиплексором выбирается приемник данных, и к нему производится присоединение
 * источника. Источник, как и мультиплексор, должны быть переданы в класс при его создании, в параметрах конструктора. 
 *
 */
public class ModelOneSourceManyReciever extends ModelMultiplexorLinker {
  private boolean FListPrepareFlag = false;
  private Vector FBlockList = null;

  /**Блок, выбранный мультиплексором в прошлый раз.
   */
  private ModelBlock FPreviousBlock = null;

  /**Конструктор
   *
   * @param aMultiplexor - мультиплексор, который производит выбор одного приемника из нескольких
   * @param aBlock - владелец мультиплексора, он же источник данных для выбираемых блоков
   */
  public ModelOneSourceManyReciever(ModelMultiplexor aMultiplexor, ModelBlock aBlock) {
    super(aMultiplexor, aBlock);
  }

  private void UnLinkAll( ModelBlock aBlock ) throws ModelException {
    if ( aBlock == null ){
      return;
    }
    int i = 0;
    Integer currentNameIndex = null;
    ModelInputBlockParam param = null;
    /*while ( param != null ){
      param.UnLink();
      i++;
      currentName = (String) FNamesToLink.get( i );
      param = (ModelInputBlockParam) aBlock.Get( currentName );
    }*/
    while ( i < FNamesToLink.size() ){
      currentNameIndex = (Integer) FNamesToLink.get( i );
      param = (ModelInputBlockParam) aBlock.Get( currentNameIndex );
      if ( param != null ) {
        param.UnLink();
      } else {
        ModelException e = new ModelException(" Не произвести отсоединение мультиплексора от блока \"" + aBlock.GetFullName() + "\"");
        throw e;
      }
      i++;
    }
  }

  /** Функция возвращает параметр владельца мультиплексора (источника), на основании которого был создан переданный
   * в параметр параметр мультиплексора.
   *
   * @param aParam - параметр мультиплексора, для которого нужно найти его аналог во владельце мультиплексора (источнике)
   * @return - возвращается параметр владельца мультиплексора, либо null - если такого параметра нет
   */
  protected ModelBlockParam GetDependParam( ModelBlockParam aParam ){

    if ( aParam == null ){
      return null;
    }
    try{
      ModelInputBlockParam param = (ModelInputBlockParam) aParam;
      ModelBlockParam linkedParam = (ModelBlockParam) param.GetLinkedElement();
      if ( linkedParam != null ){
        ModelElement owner;
        if ( linkedParam.GetParamType() == ModelBlockParam.PARAM_TYPE_INFORM ){
          owner = linkedParam.GetOwner();
        } else {
          ModelMaterialParam mp = (ModelMaterialParam) linkedParam;
          owner = mp.GetRealOwner();
        }
        if ( owner != null && owner.GetElementId() == FMuxOwner.GetElementId() ){
          return linkedParam;
        }
      }
      return null;
    } catch (Exception e){
      return null;
    }
  }

  protected Integer GetNameIndexToLink(ModelBlockParam aMuxParam, ModelBlockParam aDependParam) {
    ModelElement param = aMuxParam.GetDependElement( 0 );
    if ( param != null ){
      return param.GetNameIndexObj();
    }
    return null;
  }

  /**Добавление  в список блоков, которые зависят от переданного в параметре процедуры параметра мультиплексора
   *
   * @param aLinkedMuxParam - параметр мультиплексора, от которого  
   */
  private void AddDependBlocks( ModelBlockParam aLinkedMuxParam ){
    if ( aLinkedMuxParam == null ){
      return;
    }
    if ( FBlockList == null ){
      FBlockList = new Vector();
    }
    ModelInputBlockParam param = (ModelInputBlockParam) aLinkedMuxParam.GetDependElement( 0 );
    ModelElement paramOwner;
    int i = 0;
    while ( param != null ){
      paramOwner = param.GetRealOwner();
      if ( !FBlockList.contains( paramOwner ) ){
        FBlockList.add( paramOwner );
      }
      i++;
      param = (ModelInputBlockParam) aLinkedMuxParam.GetDependElement( i );
    }
  }

  private void AddBlockToMux() throws ModelException {
    if ( FBlockList == null || FBlockList.size() == 0){
      return;
    }
    int i = 0;
    ModelBlock block;
    while ( i < FBlockList.size() ){
      block = (ModelBlock) FBlockList.get( i );
      FMux.AddSource( block );
      i++;
    }
  }

  public void BuildBlockList() throws ModelException {
    /** Добавление блоков в мультиплексор производится исходя из следующих принципов:
     * - во входящих параметрах эталона хранится информация о том, к каким параметрам мультиплексора они подключаются
     * - в параметрах мультиплексора (в самом начале работы модели) хранится информация о том, какие блоки к этим
     * параметрам присоединены
     * - вот эти-то блоки и нужно добавить в мультиплексор  
     */
    ModelBlock muxEtalon = FMux.GetEtalon();
    if ( muxEtalon == null ){
      return;
    }
    int i = 0;
    ModelInputBlockParam inputParam = (ModelInputBlockParam) muxEtalon.GetInpParam( 0 );
    ModelBlock linkedBlock;
    ModelBlockParam muxParam;
    while ( inputParam != null ){
      linkedBlock = (ModelBlock) inputParam.GetLinkedElementOwner();
      /*if ( linkedBlock == null ){
        System.out.println("NULLLLLLLLLLLLLLLLLLLLLLL!!!!!!!!!!!!! " + inputParam.GetFullName() );
        linkedBlock = (ModelBlock) inputParam.GetLinkedElementOwner();
        ModelException e = new ModelException( "Ошибка в элементе \"" + inputParam.GetFullName() + "\": отсутствует присоединенный блок" );
        throw e;
      }*/
      if ( linkedBlock != null && linkedBlock.GetElementId() == FMux.GetElementId() ){
        //входной параметр эталонного блока присоединен к мультиплексору
        muxParam = (ModelBlockParam) inputParam.GetLinkedElement();
        AddDependBlocks( muxParam );
      }
      i++;
      inputParam = (ModelInputBlockParam) muxEtalon.GetInpParam( i );
    }
    AddBlockToMux();
  }

  /**Производится соединение выходных параметров владельца мультиплексора и  входных параметров выбранного блока.
   *
   * @throws ModelException
   */
  public void Link() throws ModelException {
    if ( !FListPrepareFlag ){
      BuildParamsList();
      FListPrepareFlag = true;
    }
    ModelBlock reciever = FMux.GetMaxCriteriaBlock();
    if ( reciever == FPreviousBlock ){
      //выбранный блок не изменился, не нужно заново производить присоединение
      return;
    }
    UnLinkAll( FPreviousBlock );
    if ( reciever == null ){
      FPreviousBlock = null;
      return;
    }
    FPreviousBlock = reciever;
    if ( FMuxOwnerParamsList.size() == 0 ){
      return;
    }
    int i = 0;
    ModelBlockParam sourceParam = (ModelBlockParam) FMuxOwnerParamsList.get( i );
    Integer recieverParamNameIndex = (Integer) FNamesToLink.get( i );
    ModelInputBlockParam recieverParam = (ModelInputBlockParam) reciever.Get( recieverParamNameIndex );
    while ( sourceParam != null ){
      sourceParam = (ModelBlockParam) FMuxOwnerParamsList.get( i );
      recieverParamNameIndex = (Integer) FNamesToLink.get( i );
      recieverParam = (ModelInputBlockParam) reciever.Get( recieverParamNameIndex );
      if ( recieverParam == null ){
        /**Прохождение данной проверки означает, что для данного параметра-источника (sourceParam) в блоке-приемнике нет
         * параметра, который должен присоединяться к этому параметру-источнику.
         * Но возможно, что параметр источник используется в самом мультиплексоре. В таком случае в мультиплексоре
         * должен быть входной параметр с таким же именем, как и у параметра-источника.
         * Именно этим и занимается нижеследующий код: получает входной параметр мультиплексора с таким же именем, как
         * и у параметра-источника и присоединяет его к источнику.
         */
        recieverParam = (ModelInputBlockParam)FMux.Get( sourceParam.GetName() );
        if ( recieverParam == null ) {
          ModelException e = new ModelException("Ошибка при соединении параметра мультиплексора для источника \"" + sourceParam.GetFullName() + "\"" );
          throw e;
        }
      }
      recieverParam.Link( FMuxOwner,  sourceParam);
      i++;
      if ( i >= FMuxOwnerParamsList.size() ){
        break;
      }
    }//while
  }

}
