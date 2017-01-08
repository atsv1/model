package mp.elements;

/**
 * User: atsv
 * Date: 06.01.2007
 *
 * ƒанный класс производит соединение выхода мультиплексора с одним блоком. “.е. соедин€етс€ напр€мую блок-источник,
 * выбранный мультиплексором и блок-приемник, соединенный с мультиплексором.
 * ƒругими словами, производитс€ соединение одного из многих источников с одним приемником, "конкуренци€ за приемник". 
 */
public class ModelMuxBlockLinker extends ModelMultiplexorLinker {
  private boolean FListPrepareFlag = false;
  private ModelBlock FPreviousBlock = null;

  /**  онструктор
   *
   * @param aMultiplexor - мультиплексор, который будет выбирать нужный блок.
   * @param aBlock - владелец мультиплексора
   */
  public ModelMuxBlockLinker(ModelMultiplexor aMultiplexor, ModelBlock aBlock) {
    super(aMultiplexor, aBlock);
  }

  protected ModelBlockParam GetDependParam( ModelBlockParam aParam ){
    ModelBlock muxOwner = FMux.GetMuxOwner();
    if ( muxOwner == null ){
      return null;
    }
    ModelInputBlockParam dependParam = (ModelInputBlockParam) muxOwner.GetInpParam( 0 );
    int i = 0;
    while ( dependParam != null ){
      if ( aParam.IsDependElement( dependParam ) ){
        return dependParam;
      }
      i++;
      dependParam = (ModelInputBlockParam) muxOwner.GetInpParam( i );
    }
    return null;
  }

  protected Integer GetNameIndexToLink(ModelBlockParam aMuxParam, ModelBlockParam aDependParam) {
    //return null;
    return aMuxParam.GetNameIndexObj();
  }

  public void BuildBlockList() throws ModelException {
    /**ƒобавление выбираемых блоков в мультиплексор.
     * ƒобавл€ютс€ только блоки одного класса с эталонным блоком (эталонным источником)
     */
    ModelBlock etalon = FMux.GetEtalon();
    if ( etalon == null ){
      return;
    }
    String etalonName = etalon.GetName();
    FMux.AddAllBlock( etalonName );
    
  }

  public void Link() throws ModelException{
    if ( !FListPrepareFlag ){
      BuildParamsList();
      FListPrepareFlag = true;
    }
    if ( FMuxOwnerParamsList.size() == 0 ){
      return;
    }
    ModelBlock source = FMux.GetMaxCriteriaBlock();
    if ( source != null &&  FPreviousBlock != null && source.GetElementId() == FPreviousBlock.GetElementId() ){
      //выбранный блок не изменилс€, просто выходим из метода
      return;
    }
    FPreviousBlock = source;
    int i = 0;
    ModelInputBlockParam currentParam = (ModelInputBlockParam) FMuxOwnerParamsList.get(0);
    Integer nameIndex = null;
    while ( currentParam != null ){
      currentParam.UnLink();
      if ( source != null ){
        nameIndex = (Integer) FNamesToLink.get(i);
        currentParam.Link( source, (ModelBlockParam)source.Get( nameIndex ) );
      }
      i++;
      if ( i < FMuxOwnerParamsList.size() ){
        currentParam = (ModelInputBlockParam) FMuxOwnerParamsList.get(i);
      } else currentParam = null;
    }//else
  }

 
}
