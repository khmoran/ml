package edu.tufts.cs.ml.text;

import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;

public class CosineSimilarity<E> {
  /** The second citation. */
  protected TrainRelation<E> compareTo;

  /**
   * Default constructor.
   */
  public CosineSimilarity( TrainRelation<E> compareTo ) {
    this.compareTo = compareTo;
  }

  /**
   * Set the comparators.
   * @param compareTo
   */
  public void setCompareTo( TrainRelation<E> compareTo ) {
    this.compareTo = compareTo;
  }

  /**
   * Calculate the similarity between the citation and the review's seeds.
   *
   * @param c
   * @return
   */
  public double calculateSimilarity( UnlabeledFeatureVector<E> ufv ) {
    if ( compareTo.isEmpty() ) {
      return 0.0;
    }

    double sum = 0.0;
    for ( LabeledFeatureVector<E> lfv : compareTo ) {
      double sim = ( ufv.dot( lfv ) /
                ufv.magnitude() * lfv.magnitude() );
      sum += sim;
    }
    double avg = sum / (double) compareTo.size();

    return avg;
  }

}
