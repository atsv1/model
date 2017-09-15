package mp.elements;

/**
 * User: atsv
 * Date: 26.01.2007
 *
 * ����� ������������ ��� �������� �������, ������� ����� ��������� ���������
 * ��������������� ���� � ������-���������� ����� �� ��������������.  
 */
public class DynamicBlockLinkerFactory {

  private static boolean IsBlockLinked( int aId, ModelBlock aLinkedBlock ){
    ModelInputBlockParam param = (ModelInputBlockParam) aLinkedBlock.GetInpParam(0);
    int elementId = 0;
    ModelElement linkedElementOwner = null;
    int i = 0;
    while ( param != null ){
      linkedElementOwner = param.GetLinkedElementOwner();
      if ( linkedElementOwner != null ){
        elementId = linkedElementOwner.GetElementId();
        if ( elementId == aId ){
          return true;
        }
      }
      i++;
      param = (ModelInputBlockParam) aLinkedBlock.GetInpParam( i );
    }
    return false;    
  }

  /**����� ���������, �������� �� ��������� ������������� ����� ���������������, ��� ������ �������� ������������
   * �������� "����������� �� �����������", �� ���� ����� ����� ���������� � ���� ��������,
   *
   * @param aMux - �������������, ��� �������� ������������ ��������
   * @return - ������������ true, ���� ������������� ������������� ���������� �������� "����������� �� �����������".
   * ����� ������������ false
   */
  private static boolean IsRecieverCompetition( ModelMultiplexor aMux ){
    /**�������� ������� �� ��� �����, ��� ������� ��������� ��������� �������������� ���������
     * ������������� � ��������� ��������������. ���� �� ��������� �������������� ���� ���� �� ���� �������
     * �������, ������� ��� �� ����������� � ��������������, �� ����� ���������� true
     */
    ModelBlock muxOwner = aMux.GetMuxOwner();
    if ( muxOwner == null ){
      return false;
    }
    int muxId = aMux.GetElementId();
    return IsBlockLinked( muxId, muxOwner );

  }

  /** ����� ���������, �������� �� ���������� � ��������� ������������� ����� ���������������, � ������� ��������
   * ������������ �������� "����������� �� ��������" - �.�. ����� ��������� ���������� ����� �������������
   * �������� �������������� � ������ ���������
   *
   * @param aMux - �������������, ��� �������� ������������ ��������
   * @return - ���������� true, ���� ����� ���������� ������������ � ������ ���������, ����� ���������� false  
   */
  private static boolean IsSenderCompetition( ModelDynamicBlock aMux ){
    /**�������� ������� �� ��������� �����: ��������� ���� �������������� ������ �������� ����������� � ��������������.
     * ���� ���� �� ���� �� ������� ���������� ���������� ����� ��������� ��   ������� ��������������, �� ����� �
     * ���������� true
     */
    ModelBlock muxEtalon = aMux.GetEtalon();
    if ( muxEtalon == null ){
      return false;
    }
    int muxId = aMux.GetElementId();
    return IsBlockLinked( muxId, muxEtalon );
  }

  public static ModelMultiplexorLinker GetLinker( ModelMultiplexor aMux ) throws ModelException {
    if ( aMux == null ){
      ModelException e = new ModelException("� LinkerFactory ������� ������ �������������");
      throw e;
    }
    //��������� �������� "����������� �� ��������"
    boolean recieverCompetition = IsRecieverCompetition( aMux );
    //��������� �������� "����������� �� ��������"
    boolean senderCompetition = IsSenderCompetition( aMux );
    if ( recieverCompetition && !senderCompetition ){
      return new ModelMuxBlockLinker(aMux, aMux.GetMuxOwner());
    }
    if ( !recieverCompetition && senderCompetition ) {
      return new ModelOneSourceManyReciever( aMux, aMux.GetMuxOwner() );
    }
    //return null;
    if ( !recieverCompetition && !senderCompetition ){
      ModelException e = new ModelException("�� ���������� ��� ���������� �������������� \"" + aMux.GetFullName() +
              "\": �� ������������ �� ���� �� ����� �����������");
      throw e;
    }
    ModelException e = new ModelException("�� ���������� ��� ���������� �������������� \"" + aMux.GetFullName() +
              "\":  ������������ ��� ���� �����������");
      throw e;
  }

}
