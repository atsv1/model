package mp.elements;

import java.util.Vector;

/**
 * User: atsv
 * Date: 06.01.2007
 *
 * Данный абстрактный класс предназначен для осуществления соединения владельца мультиплексора с блоком,
 * который выбрал мультиплексор.
 * Соединение выполняется после того, как мультиплексор выберет наиболее подходящий блок.
 * Метод Link() данного класса должен вызывается самим мультиплексором.  
 *
 */
public abstract class ModelMultiplexorLinker {
  protected ModelMultiplexor FMux = null;
  /**Владелец мультиплексора. Т.е. это блок, который будет присоединяться к выбранному мультиплексором блоку.
   * Так, для случая, когда выбирается один из нескольких источников для одного приемника, FDynamicOwner должен быть
   * содержать ссылку на приемник. А если выбирается один из нескольких приемником для одного источника, то
   * FDynamicOwner должно содержать ссылку на приемник
   */
  protected ModelBlock FMuxOwner = null;
  /** Содержит список  параметров владельца мультиплексора, с которыми нужно производить соединение
   * выбранного мультиплексором блока 
   */
  protected Vector FMuxOwnerParamsList = null;
  /**Список названий ( индексорв названий ) параметров выбираемых блоков, с которыми нужно будет производить
   * соединение соответствующих  параметров владельца мультиплексора.
   * Списки FMuxOwnerParamsList и FNamesToLink синхронизированы. Это означает, что параметр №1  из списка
   * FMuxOwnerParamsList нужно соединять с параметром №1 из списка FNamesToLink 
   */
  protected Vector FNamesToLink = null;

  public ModelMultiplexorLinker( ModelMultiplexor aMultiplexor, ModelBlock aBlock ){
    FMux = aMultiplexor;
    FMuxOwner = aBlock;
    FMuxOwnerParamsList = new Vector();
    FNamesToLink = new Vector();
  }

  public abstract void Link() throws ModelException;

  protected abstract ModelBlockParam GetDependParam( ModelBlockParam aParam );

  protected abstract Integer GetNameIndexToLink( ModelBlockParam aMuxParam, ModelBlockParam aDependParam );

  /**Метод производит добавление в мультиплексор выбираемых блоков - для того, чтобы мультиплексор мог выбирать
   * один из них и соединять со своим владельцем.
   * 
   */
  public abstract void BuildBlockList() throws ModelException;

  /**При помощи данного метода производится построение списков соединяемых параметров. В одном из списков содержатся
   * параметры владельца мультиплексора, которые необходимо соединять с выбранным блоком. В другом списке будут
   * храниться названия параметров выбираемого блока.
   * Вызов этого метода производится перед  началом процедуры соединения соотвествующих параметров владельца
   * мультиплексора и выбранного блока. 
   */
  public void BuildParamsList( ){
    /**Производится перебор всех входных параметров мультиплексора для того чтобы определить, . 
     * Перебор именно входных параметров мультиплексора  основан на следующих фактах:
     * 1. Входные параметры мультиплексора содержат аналоги всех выходных параметров эталонного блока.
     * 2. Выходные параметры выбираемых блоков соединяются с входными параметрами мультиплексора на этапе создания
     * модели.
     * 3. Уточнение п.2: либо входные параметры выбираемых блоков соединяются с входными параметрами мультиплексора 
     */
    FMuxOwnerParamsList.clear();
    FNamesToLink.clear();
    ModelBlockParam muxParam = FMux.GetInpParam(0);
    ModelBlockParam dependParam = null;
    int i = 1;
    while ( muxParam != null ){
      dependParam = GetDependParam( muxParam );
      if ( dependParam != null ){
        Integer ind = GetNameIndexToLink(muxParam, dependParam);
        if ( ind != null ){
          FMuxOwnerParamsList.add( dependParam );
          FNamesToLink.add( ind );
        }
      }
      muxParam = FMux.GetInpParam(i);
      i++;
    }
  }

  public int GetDestParamsCount(){
    return FMuxOwnerParamsList.size();
  }

}
