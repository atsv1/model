package mp.elements;


/**
 * User: atsv
 * Date: 22.12.2006
 *
 * Класс предназначен для хранения настроечных параметров для элементов модели.
 * @noinspection ALL
 */
public  class GlobalParams {

  /**Метод возвращает значение флага, разрешающего выводить в Java-консоль время выполнения каждого элемента. Под
   * "временем" выполнения понимается значение экземпляра класса ModelTime 
   * @return - возвращает true, если элементы модели должны выводить время своего выполнения значение модельного времени.
   */
  public static boolean ExecTimeOutputEnabled(){    
    return false;
  }

  /** Метод возвращает значение флага, разрешающего выводить в Java-консоль названия текущих состояний для стэйтчартов
   * @return возвращает true, если нужно выводить названия состояний
   */
  public static boolean StateNameOutputEnabled(){
    return false;
  }

  public static boolean MuxOutputEnabled(){
    return false;
  }

}
