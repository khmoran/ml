package edu.tufts.cs.ml;

import java.util.LinkedHashMap;

public class Metadata extends LinkedHashMap<String, Object> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = 3889614613286541299L;
  /** The line text that marks the start of the attribute information. */
  public static final String ATTRIBUTE_MARKER = "@ATTRIBUTE";

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for ( String key : this.keySet() ) {
      sb.append( ATTRIBUTE_MARKER );
      sb.append( " " );
      sb.append( key );
      sb.append( "\t" );
      sb.append( this.get( key ).toString() );
      sb.append( "\n" );
    }

    return sb.toString();
  }

}
