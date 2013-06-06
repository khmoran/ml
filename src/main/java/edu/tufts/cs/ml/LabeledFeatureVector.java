package edu.tufts.cs.ml;


public class LabeledFeatureVector<E> extends FeatureVector<E> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = -5250706109618829326L;
  /** The line text that marks the class attribute. */
  public static final String CLASS_MARKER = "CLASS";
  /** The label. */
  protected E label;

  /**
   * Default constructor that takes an id.
   * @param id
   */
  public LabeledFeatureVector( E label, String id ) {
    super( id );
    this.label = label;
  }

  /**
   * Get the label.
   */
  public E getLabel() {
    return this.label;
  }

  /**
   * Set the label.
   */
  public void setLabel( E label ) {
    this.label = label;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    for ( Feature<?> f : this.values() ) {
      if ( f != null ) {
        sb.append( f.getValue() );
        sb.append( "," );
      }
    }
    sb.append( this.getLabel() );
    sb.append( "," );
    sb.append( this.getId() );
    sb.append(  "\n" );

    return sb.toString();
  }
}
