package edu.tufts.cs.ml.cluster;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.UnlabeledFeatureVector;

public class Centroid<E> extends UnlabeledFeatureVector<E> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = 9119256326166938046L;

  /**
   * Default constructor.
   *
   * @param id
   */
  public Centroid( String id ) {
    super( id );
  }

  /**
   * Copy constructor.
   *
   * @param fv
   */
  public Centroid( FeatureVector<?> fv ) {
    super( fv.getId() );
    this.putAll( fv );
  }
}
