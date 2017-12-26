package mp.gui;

import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

import mp.elements.ModelElementDataSource;
import mp.elements.ModelException;

public class ElementHistoryTable extends ModelGUIAbstrTable {
	private Vector FNamesList = null; //названия колонок в таблице
  private Vector FRows = null;
  private int currentMaxCounter = 0;

	@Override
	public void AddGUIElement(ModelGUIElement aElement) {

	}

	@Override
	protected void ReadParamNames(Vector aCaptionVector, Vector aParamsVector)
	    throws ModelException {
		// TODO Auto-generated method stub

	}

	
	protected void CreateRows(Vector aDataRows, Object aColumnsContainer,	    Object aRowsContainer) throws ModelException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void UpdateCell() throws ModelException {
		ReadConnectedParamInfo();
		if ( !FIsConnected ){
      ReadConnectedParamInfo();
    }
		// проверяем, что указанный параметр действительно хранит историю
		if ( !FConnector.IsHistoryExists(FAddress) ) {
			throw new ModelException("Отсутствует история в параметре для элемента \"" + this.GetDataSource().GetCaption() + "\"");
		}
		String s = FConnector.GetHistoryStringValue(FAddress,currentMaxCounter );
		while (s != null) {
			Vector newRow = new Vector(2);
			newRow.add(currentMaxCounter);
			newRow.add(s);
			FRows.insertElementAt(newRow, 0);
			currentMaxCounter++;
			s = FConnector.GetHistoryStringValue(FAddress,currentMaxCounter );
		}
		FTable.updateUI();
	}

	@Override
	public void ReadDataFromNode() throws ModelException {
		if ( FPanel == null ) {
			FPanel = new JPanel( null );
		}		
    FCaption = new JLabel("");
    FCaption.setText( this.GetDataSource().GetCaption() );

    FNamesList = new Vector();
    FRows = new Vector();

    FNamesList.add("№");
    FNamesList.add("Значение");
    Vector firstRow = new Vector(2);
    firstRow.add("0");
    firstRow.add("Начало");
    FRows.add(firstRow);

    FTable = new JTable(  FRows, FNamesList );
    ComponentPlacing();


	}

	@Override
	protected boolean StaticUpdate() throws ModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void DynamicUpdate() throws ModelException {
		// TODO Auto-generated method stub
		
	}



}
