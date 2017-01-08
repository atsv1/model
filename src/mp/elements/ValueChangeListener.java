package mp.elements;

import java.util.Vector;

import mp.parser.ExecutionContext;
import mp.parser.ExecutionContextLocator;
import mp.parser.Operand;
import mp.parser.ScriptException;
import mp.parser.Variable;
import mp.parser.VariableChangeEvent;

public class ValueChangeListener extends mp.parser.ChangeListener{
	private Vector<ValueChangeListener.HistoryBean> VariableHistory = new Vector<HistoryBean>();
	private ExecutionContext context = null;
	private int counterValue = 0;

	public ValueChangeListener(){
		super();
	}

		@Override
  public void VariableChanged(VariableChangeEvent changeEvent) {
		if ( changeEvent.getSource() instanceof Operand) {
			try {
				context = ExecutionContextLocator.getContext();
				if (context == null) {
					ScriptException e = new ScriptException("");
					e.printStackTrace();
				}
	      HistoryBean newbean = new HistoryBean(context == null ? "nop" : context.getContextName(), counterValue, (Operand)changeEvent.getSource());
	      VariableHistory.add(newbean);
	      counterValue++;
      } catch (ScriptException e) {
      }
		}

  }

	public int GetHistoryRecordCount(){
		return VariableHistory.size();
	}

	public String GetStringValue(int i){
		if ( i >= VariableHistory.size() ) {
			return null;
		}
		HistoryBean bean = VariableHistory.get(i);
		if (bean==null){
			return null;
		}
		return bean.toString();
	}

  public HistoryBean GetBean(int i){
  	if ( i >= VariableHistory.size() ) {
			return null;
		}
		return VariableHistory.get(i);

  }

	public static class HistoryBean{
		private String operationName;
		private int orderNum;
		private Operand currentOperand = null;
		private String stringValue = null;


		public HistoryBean(String operationName, int orderNum, Operand operand) throws ScriptException{
			this.operationName = operationName;
      this.orderNum = orderNum;
      if ( this.currentOperand == null ) {
      	Variable var = new Variable();
      	var.StoreValueOf(operand);
      	currentOperand = var;
      	stringValue = currentOperand.GetStringValue();
      }
		}

		public String toString(){
			return Integer.toString(orderNum) + " " + operationName + "" + stringValue;
		}

		public int getOrderNum(){
			return orderNum;
		}

		public String getStringValue(){
			return stringValue;
		}

		public String getOperationName(){
			return operationName;
		}

	} // class HistoryBean

}
