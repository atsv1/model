package mp.gui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

/**
 * User: atsv
 * Date: 01.10.2006
 */
public class StandartStatusPanel {
  private JPanel FMainPanel = null;

  private JPanel FStatusPanel = null;
  private JLabel FStatusCaption = null;
  private JLabel FStatusValue = null;
  private JLabel FTimeLabel = null;

  public StandartStatusPanel(){
    FMainPanel = new JPanel( new BorderLayout() );
    FMainPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );

    //создание компонентов для отображения статуса:
    FStatusPanel = new JPanel( new BorderLayout() );
    FStatusPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );
    FStatusCaption = new JLabel("Статус: ");
    FStatusPanel.setSize( FMainPanel.getWidth(), FStatusCaption.getHeight() );
    FStatusValue = new JLabel();
    FStatusPanel.add( FStatusCaption, BorderLayout.WEST );
    FStatusPanel.add( FStatusValue, BorderLayout.CENTER );
    FMainPanel.add( FStatusPanel, BorderLayout.NORTH );


    //создание компонентов для отображение модельного времени и прочего
    JPanel panel1 = new JPanel();
    panel1.setBorder( new BevelBorder(BevelBorder.LOWERED) );
    panel1.add( new JLabel("Шаг: ") );
    FTimeLabel = new JLabel();
    panel1.add( FTimeLabel );
    FMainPanel.add( panel1, BorderLayout.SOUTH );
  }

  public Component GetComponent(){
    return FMainPanel;
  }

  public void SetStatusValue(String aStatusValue){
    FStatusValue.setText( aStatusValue );
  }

  public void SetModelTime( double aTimeValue ){
    FTimeLabel.setText( Double.toString( aTimeValue ) );
  }

}
