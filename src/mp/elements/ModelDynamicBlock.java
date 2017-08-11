package mp.elements;


import mp.parser.ScriptException;
import mp.parser.Variable;
import mp.parser.ScriptLanguageExt;
import mp.utils.ModelAttributeReader;
import mp.utils.ServiceLocator;

/**
 * Date: 31.01.2007
 */
public abstract class ModelDynamicBlock extends ModelBlock{
  protected boolean FIsParamsBuild;
  protected ModelBlock FEtalon = null;
  protected ModelBlock FDynamicOwner = null;
  protected ScriptLanguageExt FLanguageExt = null;

  public ModelDynamicBlock(ModelElement aOwner, String aElementName, int aElementId) {
    super(aOwner, aElementName, aElementId);
  }


  public boolean IsDynamicParamCreate() {
    return true;
  }

  public void BuildParams() throws ModelException{
    if ( FIsParamsBuild ){
      return;
    }    
    if ( elementSource == null ){
      ModelException e = new ModelException("ѕуста€ нода в элементе \"" + this.GetFullName() + "\"");
      throw e;
    }
    ReadEtalon();
    ReadOwner();
    FIsParamsBuild = true;
  }

  public abstract void SetDynamicLinker( ) throws ModelException;

  public abstract ModelBlock GetDynamicBlockOwner();

  public abstract String GetDynamicBlockEtalonName();

  public abstract void AddSource( ModelBlock aSourceBlock ) throws ModelException;

  /**ћетод выполн€ет добавление всех блоков с таким именем, содержащихс€ в модели.
   *
   * @param aBlockName - название блока
   */
  protected void AddAllBlock( String aBlockName ) throws ModelException{
    ModelBlock block;
    int i = 0;
    Model model = (Model) this.GetOwner();
    block = model.Get( aBlockName, i );
    while ( block != null ){
      this.AddSource( block );
      i++;
      block = model.Get( aBlockName, i );
    }
  }

  protected abstract void ReCreateAllInputParams() throws ModelException;

  public void SetEtalon( ModelBlock aEtalonBlock ) throws ModelException{
    if ( aEtalonBlock == null ){
      ModelException e = new ModelException("ѕопытка добавить пустой эталонный элемент в элемент \"" + GetFullName() + "\"");
      throw e;
    }
    FEtalon = aEtalonBlock;
    ModelBlockParam param = FEtalon.GetOutParam(0);
    if ( param == null ){
      /**¬ эталонном блоке об€зательно должны присутствовать выходные параметры, иначе невозможно будет выбрать
       * один источник из нескольких, поскольку выбирающие скрипты как раз и оперируют с выходными параметрами
       * блоков-источников
       */
      ModelException e = new ModelException("¬ блоке \"" + FEtalon.GetFullName() + "\" нет ни одного выходного параметра. " +
      "он не может служить входом дл€ мультиплексора \"" + GetFullName() + "\"");
      FEtalon = null;
      throw e;
    }
    ReCreateAllInputParams();
  }

  protected int GetSelfIndexVariable() throws ModelException {
    ModelBlockParam selfIndexParam = (ModelBlockParam) Get("selfIndex");
    if ( selfIndexParam == null ){
      return -1;
    }
    try {
      int i = selfIndexParam.GetVariable().GetIntValue();
      return i;
    } catch (ScriptException e) {
      return -1;
    }
  }

  private void ReadEtalon() throws ModelException {    
    String etalonName = elementSource.GetDynamicEtalonName();
    if ( etalonName == null || "".equalsIgnoreCase( etalonName ) ){
      return;
    }
    ModelElement etalon = this.GetOwner().Get( etalonName );
    if ( etalon != null ){
      this.SetEtalon((ModelBlock) etalon);
      return;
    }
    Model model = (Model) this.GetOwner();
    etalon = model.Get( etalonName, GetSelfIndexVariable() );
    if ( etalon == null ){
      etalon = model.Get( etalonName, 0 );
    }
    if ( etalon == null ){
      ModelException e = new ModelException("Ќе найти эталонный элемент \"" + etalonName + "\"");
      throw e;
    }
    this.SetEtalon((ModelBlock) etalon);
  }

  public void SetMuxOwner( ModelBlock aMuxOwner ) throws ModelException {
    FDynamicOwner = aMuxOwner;
    ModelBlockParam param = FDynamicOwner.GetOutParam(0);
    if ( param == null ){
      /**¬ эталонном блоке об€зательно должны присутствовать выходные параметры, иначе невозможно будет выбрать
       * один источник из нескольких, поскольку выбирающие скрипты как раз и оперируют с выходными параметрами
       * блоков-источников
       */
      ModelException e = new ModelException("¬ блоке \"" + FDynamicOwner.GetFullName() + "\" нет ни одного выходного параметра. " +
      "он не может служить входом дл€ мультиплексора \"" + GetFullName() + "\"");
      FDynamicOwner = null;
      throw e;
    }
    ReCreateAllInputParams();
  }

  private void ReadOwner() throws ModelException {    
    String ownerName = elementSource.GetDynamicOwnerName();
    if ( ownerName == null || "".equalsIgnoreCase( ownerName ) ){
      return;
    }
    ModelElement muxOwner = this.GetOwner().Get( ownerName );
    if ( muxOwner != null ){
      this.SetMuxOwner( (ModelBlock) muxOwner );
      return;
    }
    Model model = (Model) this.GetOwner();
    int selfIndex = GetSelfIndexVariable();
    muxOwner = model.Get( ownerName, selfIndex );
    if ( muxOwner == null ){
      ModelException e = new ModelException("ќшибка в мультиплексоре \"" + GetFullName() + "\": отсутствует элемент \"" +
            ownerName + "\" с индексом " + Integer.toString( selfIndex ) );
      throw e;
    }
    this.SetMuxOwner( (ModelBlock) muxOwner );
  }


  public void SetLanguageExt(ScriptLanguageExt aLanguageExt) {
    FLanguageExt = aLanguageExt;
  }

  public ScriptLanguageExt GetLanguageExt() {
    return FLanguageExt;
  }
}
