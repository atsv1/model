package mp.gui;

import mp.elements.ModelException;
import mp.utils.ModelAttributeReader;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.Vector;

import org.w3c.dom.Node;

/**
 * User: atsv
 * Date: 05.06.2007
 */
public class ModelGUIAnimation extends ModelGUIAbstrElement {
  private JPanel FMainPanel = null;
  private JLabel FCaptionLabel = null;
  private Vector FFiguresList = null;
  private JPanel FPanel = null;
  private AnimationAdapter FAdapter = null;
  private double FInitXCoord = 0;
  private double FInitYCoord = 0;
  private double FInitWidth = 100;
  private double FInitHeight = 100;
  private boolean FResizeFlag = false;

  public ModelGUIAnimation(){
    FPanel = new JPanel( new BorderLayout() );
    FPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );
    FMainPanel = new JPanel( new BorderLayout() );
    FMainPanel.setBorder( new BevelBorder(BevelBorder.LOWERED) );
    FCaptionLabel = new JLabel();
    FMainPanel.add( FCaptionLabel, BorderLayout.NORTH );
    FMainPanel.add( FPanel, BorderLayout.CENTER );
    FFiguresList = new Vector();
    FAdapter = new AnimationAdapter( FPanel, this, FFiguresList );
  }

  private void ReadFigures() throws ModelException{
    int figureCounter = 1;
    Node figureNode = ModelAttributeReader.GetChildNodeByName( FNode, "Figure", figureCounter );
    ModelAnimationFigure currentFigure = null;
    FFiguresList.clear();
    int blockCount = 0;
    while ( figureNode != null ){
      currentFigure = new ModelAnimationFigure( figureNode, FCaptionLabel.getText(), FConnector );
      blockCount = currentFigure.ApplyNodeInformation();
      if ( blockCount > 0 ){
        int currentBlockIndex = 1;
        while ( currentBlockIndex <= blockCount ){
          currentFigure = new ModelAnimationFigure( figureNode, FCaptionLabel.getText(), FConnector );
          currentFigure.ApplyNodeInformation( currentBlockIndex );
          FFiguresList.add( currentFigure );
          currentBlockIndex++;
        }
      }
      FFiguresList.add( currentFigure );
      figureCounter++;
      figureNode = ModelAttributeReader.GetChildNodeByName( FNode, "Figure", figureCounter );
    }
    if ( FFiguresList.size() == 0 ){
      ModelException e = new ModelException("Ошибка в анимации \"" + FCaptionLabel.getText() + "\": отсутствуют фигуры");
      throw e;
    }
  }

  public void ReadDataFromNode() throws ModelException {
    if ( FNode == null ){
      return;
    }
    FAttrReader.SetNode( FNode );
    this.ReadCoordFromNode(FMainPanel);
    FCaptionLabel.setText( this.GetName() );
    FInitXCoord = FAttrReader.GetAnimationInitXCoord();
    FInitYCoord = FAttrReader.GetAnimationInitYCoord();
    FInitWidth =  FAttrReader.GetAnimationInitWidth();
    FInitHeight =  FAttrReader.GetAnimationInitHeight();
    FResizeFlag = FAttrReader.GetAnimationResizeFlag();
    ReadFigures();
  }

  public Component GetComponent() {
    return FMainPanel;
  }

  public void AddGUIElement(ModelGUIElement aElement) {
  }

  public void Update() throws ModelException{
    FAdapter.Update();
  }

  public double GetInitXCoord() {
    return FInitXCoord;
  }

  public double GetInitYCoord() {
    return FInitYCoord;
  }

  public double GetInitWidth() {
    return FInitWidth;
  }

  public double GetInitHeight() {
    return FInitHeight;
  }

  public boolean GetResizeFlag() {
    return FResizeFlag;
  }
}
