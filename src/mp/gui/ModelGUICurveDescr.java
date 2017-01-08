package mp.gui;

import mp.elements.ModelAddress;
import mp.elements.ModelConnector;

/**
 * User: Администратор
 * Date: 30.11.2008
 */
class ModelGUICurveDescr {
  protected String Caption;
  protected String XAxisLabel = "x";
  protected String YAxisLabel = "y";
  double XAxisMinValue = 0;
  double YAxisMinValue = 0;
  double XAxisMaxValue = 0;
  double YAxisMaxValue = 0;
  ModelAddress XAddress = null;
  ModelAddress YAddress = null;
  ModelAddress ArrayAddress = null;
  double XIncrement = 0;
  double YIncrement = 0;
  ModelConnector Connector;
  boolean ISArray = false;
  int XDimensionIndex = 0;
  int YDimensionIndex = 0;
}
