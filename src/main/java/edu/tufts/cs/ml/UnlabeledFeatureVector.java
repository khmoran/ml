package edu.tufts.cs.ml;

import java.util.TreeMap;



public class UnlabeledFeatureVector<E> extends FeatureVector<E> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = -5250706109618829326L;
  /** The default key to use. */
  private static final int DEFAULT_KEY = 0;
  /** The classifications based on k. */
  private TreeMap<Integer, E> classifications = new TreeMap<Integer, E>();

  /**
   * Default constructor that takes an id.
   * @param id
   */
  public UnlabeledFeatureVector( String id ) {
    super( id );
  }

  /**
   * Set the classification.
   * @param classification
   */
  public void setClassification( E classification ) {
    classifications.put( DEFAULT_KEY, classification );
  }

  /**
   * Add a classification for a given k value.
   * @param k
   * @param classification
   */
  public void addClassification( int k, E classification ) {
    classifications.put( k, classification );
  }

  /**
   * Get the classification.
   * @return
   */
  public E getClassification() {
    return classifications.get( DEFAULT_KEY );
  }

  /**
   * Get the classification for the given k value.
   * @param k
   * @return
   */
  public E getClassification( int k ) {
    return classifications.get( k );
  }

  /**
   * Get the classifications.
   * @return
   */
  public TreeMap<Integer, E> getClassifications() {
    return classifications;
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
    sb.append( this.getId() );
    for ( E classification : classifications.values() ) {
      sb.append( "," );
      sb.append( classification );
    }
    sb.append(  "\n" );

    return sb.toString();
  }

}
