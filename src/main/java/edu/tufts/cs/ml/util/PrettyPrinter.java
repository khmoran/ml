package edu.tufts.cs.ml.util;

public class PrettyPrinter {

  /**
   * Private constructor for utility class.
   */
  private PrettyPrinter() {
    // purposely not instantiable
  }

  /**
   * Pretty-print an array.
   * @param array
   * @return
   */
  public static String prettyPrint( Object[] array ) {
    StringBuilder sb = new StringBuilder( "[ " );
    for ( Object o : array ) {
      sb.append( o + ", " );
    }
    sb.replace( sb.length()-2, sb.length()-1, " ]" );

    return sb.toString();
  }

  /**
   * Pretty-print an array.
   * @param array
   * @return
   */
  public static String prettyPrint( double[] array ) {
    StringBuilder sb = new StringBuilder( "[ " );
    for ( double o : array ) {
      sb.append( o + ", " );
    }
    sb.replace( sb.length()-2, sb.length()-1, " ]" );

    return sb.toString();
  }

}
