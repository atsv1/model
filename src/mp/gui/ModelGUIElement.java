package mp.gui;

import org.w3c.dom.Node;

import java.awt.*;
import java.util.Vector;

import mp.elements.*;

/**
 * User: atsv
 * Date: 29.09.2006
 */
public interface ModelGUIElement {

  Component GetComponent() ;

  void AddGUIElement( ModelGUIElement aElement );

  void Update() throws ModelException;

  void SetConnector( ModelConnector connector );

  Node GetNode();

  void Send() throws ModelException;

  Vector GetElementList();
}
