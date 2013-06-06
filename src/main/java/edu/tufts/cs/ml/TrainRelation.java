package edu.tufts.cs.ml;

public class TrainRelation<E> extends Relation<LabeledFeatureVector<E>> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = 4909421864046057478L;

  /**
   * Default constructor.
   * @param name
   * @param metadata
   */
  public TrainRelation( String name, Metadata metadata ) {
    super( name, metadata );
  }

  /**
   * Get the FeatureVector with this id.
   * @param fvId
   * @return
   */
  public LabeledFeatureVector<E> get( String fvId ) {
    for ( LabeledFeatureVector<E> fv : this ) {
      if ( fv.getId().equals( fvId ) ) {
        return fv;
      }
    }

    return null;
  }

  /**
   * Count the feature vectors that are members of this class.
   * @param label
   * @return
   */
  public int countClass( E label ) {
    int count = 0;
    for ( LabeledFeatureVector<E> fv : this ) {
      if ( fv.getLabel().equals( label ) ) {
        count++;
      }
    }

    return count;
  }

}
