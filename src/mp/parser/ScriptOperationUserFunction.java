package mp.parser;

import java.util.Hashtable;

import mp.elements.ModelException;

public class ScriptOperationUserFunction extends ScriptOperation {

	private ScriptParser FParser = null;
	protected ScriptLanguageExt FLanguageExt = null;
	private Variable FResult = null;
  private String FFunctionName = null;
  private Hashtable<Integer, Variable> FInpvarList = null;
  private String FResultType;
  /**
   * Список входных var-параметров
   */
  private Hashtable<Integer, Object> FVarInpParamList = null;


	public int ExecOperation(int aProgramPointer) throws ScriptException {
		int i = 0;
		try {
		if ( FInpvarList != null  ) {
			// перекидываем значения из водных параметров во переменные функции
			Variable functionVar = FInpvarList.get(i);
			while (functionVar != null) {
				Operand op = InitOperand(aProgramPointer + 1 + i);
        if ( IsVarParam(i) ) {
        	//FInpvarList.put(i, (Variable) op);
        	functionVar.StoreValueOf(op);
        } else {
				  functionVar.StoreValueOf(op);
        }
				i++;
				functionVar = FInpvarList.get(i);
			}
		}
		FParser.ExecuteScript();
		if (FInpvarList == null) {
			Operand resVar = InitOperand(aProgramPointer + 1);
			((Variable)resVar).StoreValueOf(FResult);
			return 1;
		}
		Operand resVar = InitOperand(aProgramPointer + 1 + i);
		((Variable)resVar).StoreValueOf(FResult);
		return 1 + FInpvarList.size();
		} catch (Exception e) {
			throw new ScriptException("Ошибка в функции " + this.GetName() + ": " + e.getMessage());
		}
	}

	private boolean IsVarParam(int number){
		if (FVarInpParamList == null ) {
			return false;
		}
		return FVarInpParamList.get(number) != null;
	}

	public void SetName(String functionName){
		FFunctionName = functionName;
	}

	public String GetName(){
		return FFunctionName;
	}

	public String GetResultType() throws ScriptException {
		return FResultType;
	}

	/*public Variable GetResultVariable(int aProgramPointer) throws ScriptException {
		return FResult;
	}*/

	public Variable GetResultVariable(int aProgramPointer) throws ScriptException {
    String s = GetResultType();
    Variable result = null;
    if ( s.equalsIgnoreCase("integer") ) {
      int i = 0;
      result =  new Variable(i);
    }
    if ( s.equalsIgnoreCase("boolean") ) {
      boolean f = false;
      result =  new Variable(f);
    }
    if ( s.equalsIgnoreCase("real") ) {
      float r = (float) 7.1;
      result =  new Variable(r);
    }
    if ( s.equalsIgnoreCase( "string" ) ){
      result = new Variable("");
    }

    return result;
  }


	public void SetResultInfo(String aVarType) throws ScriptException{
		FResult = Variable.CreateNewInstance( this.FFunctionName, aVarType, null );
		FResultType = aVarType;
	}

	public void SetExternalLanguageExt(ScriptLanguageExt ext) throws ScriptException{
		FLanguageExt = ext;
		FLanguageExt.AddVariable(FResult);
	}

	public void SetSourceCode(String code) throws ModelException, ScriptException{
		if ( FLanguageExt == null){
      ModelException e = new ModelException("Попытка начать подготовку элемента " + this.GetName() +
              " с пустым расширителем языка");
      throw e;
    }
		if ( FInpvarList != null ) {
			// Добавляем входные параметры функции в список переменных, с которыми будет оперировать функция
			int i = 0;
			Variable var = FInpvarList.get(i);
			while ( var!= null ) {
				if ( FLanguageExt.Get(var.GetName()) != null) {
					// такая переменная уже есть в списке. Заменяем ее
					FLanguageExt.RemoveVariable(var.GetName());
				}
				FLanguageExt.AddVariable(var);
				i++;
				var = FInpvarList.get(i);
			}
		}
    FParser = ParserFactory.GetParser( FLanguageExt, code);
	}

	public Variable GetResultVariable() {
		return FResult;
	}

	public void AddInpVar(int orderNum, String name, String type) throws ScriptException{
		if (FInpvarList == null) {
			FInpvarList = new Hashtable<Integer, Variable>();
		}
		if ( FInpvarList.get(orderNum) != null) {
			throw new ScriptException("Для функции " + this.GetName() + " уже определен параметр с номером " + Integer.toString(orderNum));
		}
		Variable newVar =  Variable.CreateNewInstance( name, type, null );
		FInpvarList.put(orderNum, newVar);
	}

	public void AddVarInpParam(Variable aVarParam, int orderNum ) throws ScriptException {
		if (aVarParam == null || aVarParam.GetName() == null || "".equals(aVarParam.GetName() ) ) {
			throw new ScriptException(" Попытка добавить пустой входной var-параметр в функцию " + this.GetName() );
		}
		if ( FVarInpParamList == null ) {
			FVarInpParamList = new Hashtable<Integer, Object>();
		}
		if (FInpvarList == null) {
			FInpvarList = new Hashtable<Integer, Variable>();
		}
		FInpvarList.put(orderNum, aVarParam);
		FVarInpParamList.put(orderNum, aVarParam);
	}

	public String toString(){
		return FFunctionName;
	}

}
