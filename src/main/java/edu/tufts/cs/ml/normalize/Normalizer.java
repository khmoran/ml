package edu.tufts.cs.ml.normalize;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.Relation;

public abstract class Normalizer<E> {
  /** The training data. */
  protected Relation<? extends FeatureVector<E>> data;

  /**
   * Default constructor.
   * @param train
   */
  public Normalizer( Relation<? extends FeatureVector<E>> data ) {
    this.data = data;
  }

  /**
   * Normalize the training data.
   */
  public void normalize() {
    // do nothing
  }

  /**
   * Normalize the train and test relations.
   * @param test
   */
  public abstract void normalize( Relation<? extends FeatureVector<E>> test );
}
