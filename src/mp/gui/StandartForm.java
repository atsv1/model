package mp.gui;



import mp.elements.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * User: atsv
 * Date: 30.09.2006
 * Класс является основным классом для  организации интерфеса между  пользователем и моделью.
 * Класс создает две видимых панели. На одной панели будут располагаться все элементы, описанные в пользовательском
 * файле, на другой панели будут располагаться элементы управления, управляющие всей моделью: кнопки Старт, Стоп, Пауза,
 * возможность для ввода интервала обновления формы
 */
public class StandartForm extends ModelGUIAbstrElement  implements ModelGUIElement, ModelForReadInterface{
  private JPanel FMainPanel = null;
  private JPanel FUserPanel = null;
  private JPanel FControlPanel = null;
  private Thread FUpdateThread = null;
  private StandartStatusPanel FStatusPanel = null;
  private boolean FPauseFlag = true;
  JButton FPauseButton;
  //private JLabel FStatusLabel = null;


  //@todo Сделать обработку исключительной ситуации - отсутствия файла модели

  protected JPanel GetUserPanel(){
    if ( FUserPanel == null ){
      FUserPanel = new JPanel( new BorderLayout() );
    }
    return FUserPanel;
  }

  private JPanel GetNewControlPanel(){
    JPanel panel = new JPanel( new BorderLayout() );
    JPanel buttonPanel = new JPanel( );
    panel.add( buttonPanel, BorderLayout.WEST );

    JButton startButton = new JButton("Start");
    startButton.setBounds( 1,2, 20, 10 );
    //panel.add( startButton, BorderLayout.WEST);
    buttonPanel.add( startButton );

    startButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        try {
          FConnector.StartModel();
          FStatusPanel.SetStatusValue("Модель запущена");
        } catch (ModelException e1) {
          e1.printStackTrace();
        }
        StartUpdating();
      }
    }
    );

    JButton stopButton = new JButton("Stop");
    stopButton.setBounds( 22, 1, 20, 10 );
    //panel.add( stopButton, BorderLayout.CENTER);
    buttonPanel.add( stopButton );
    stopButton.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent e){
        FUpdateThread = null;
        FConnector.StopModel();
        FStatusPanel.SetStatusValue("Модель остановлена");
      }
    }
    );

    JButton sendButton = new JButton("Send");
    sendButton.setBounds( 45, 1, 20, 10 );
    //panel.add( sendButton, BorderLayout.EAST);
    buttonPanel.add( sendButton );
    sendButton.addActionListener(
            new ActionListener(){
              public void actionPerformed(ActionEvent e) {
                try {
                  Send();
                } catch (ModelException e1) {
                  FStatusPanel.SetStatusValue(e1.getMessage() );
                  FUpdateThread = null;
                  FConnector.StopModel();
                }
              }
            }
    );

    FPauseButton = new JButton("Pause");
    FPauseButton.setBounds( 80, 1, 20, 10 );
    buttonPanel.add( FPauseButton );
    FPauseButton.addActionListener(  new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if ( FPauseFlag ) {
          FConnector.PauseModel();
          FPauseFlag = false;
          FPauseButton.setText( "Resume" );
        } else {
          FConnector.ResumeModel();
          FPauseFlag = true;
          FPauseButton.setText( "Pause" );
        }
      }
    });

    panel.add( FStatusPanel.GetComponent(), BorderLayout.SOUTH);

    return panel;
  }

  protected JPanel GetControlPanel(){
    if ( FControlPanel == null ){
      FControlPanel = GetNewControlPanel();
    }
    return FControlPanel;
  }

  public StandartForm() {
    super();
    FMainPanel = new JPanel();
    FMainPanel.setLayout( new BorderLayout() );
    FMainPanel.add( GetUserPanel(), BorderLayout.CENTER );
    FStatusPanel = new StandartStatusPanel();
    FMainPanel.add( GetControlPanel(), BorderLayout.SOUTH );
  }

  public void ReadDataFromNode() throws ModelException {
  }

  public Component GetComponent() {
    return FMainPanel;
  }

  public void AddGUIElement( ModelGUIElement aElement ) {
    FUserPanel.add( aElement.GetComponent() );
    AddElement( (ModelGUIAbstrElement) aElement );
  }


  public void StartUpdating(){
    FUpdateThread = new Thread(){

      public void StopUpdating( String errorString ){
        FConnector.StopModel();
        FUpdateThread = null;
        FStatusPanel.SetStatusValue( errorString );
      }

      public void run(){
        String errorString;
        while ( FUpdateThread != null ){
          try {
            errorString = FConnector.GetErrorString();
            if ( errorString == null || "".equalsIgnoreCase( errorString ) )
            {
              Update();
              sleep(100);
            } else{
              StopUpdating( errorString );
            }
          } catch (ModelException e) {
            e.printStackTrace();
            StopUpdating( e.getMessage() );
          } catch (InterruptedException e) {
            e.printStackTrace();
            StopUpdating( e.getMessage() );
          }
        }
      }
    };
    FUpdateThread.start();
  }

}
