package edu.tufts.cs.ml.learning.active;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.util.MathUtil;

public class RandomSampler<E> extends Sampler<E> {

  /**
   * Default constructor.
   *
   * @param train
   */
  public RandomSampler( TrainRelation<E> train ) {
    super( train );
  }

  @Override
  protected Set<UnlabeledFeatureVector<E>> getNextLearningSet(
      TrainRelation<E> labeled, TestRelation<E> unlabeled, int k, int m ) {
    Set<UnlabeledFeatureVector<E>> learningSet =
        new HashSet<UnlabeledFeatureVector<E>>();

    if ( m > unlabeled.size() ) m = unlabeled.size();

    // get m random items from a random set
    while ( learningSet.size() < m ) {  // do it until the learning set is full
      Set<UnlabeledFeatureVector<E>> randset = MathUtil.randomSample(
          new ArrayList<UnlabeledFeatureVector<E>>( unlabeled ), m );

      for ( UnlabeledFeatureVector<E> ufv : randset ) {
        if ( learningSet.size() == m ) {
          break;
        }
        if ( !learningSet.contains( ufv ) ) { // no repeats
          learningSet.add( ufv );
        }
      }
    }

    return learningSet;
  }
}
