package edu.tufts.cs.ml.learning.active;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.classify.WeightedKnnClassifier;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;

public class UncertaintySampler<E> extends Sampler<E> {

  /**
   * Default constructor.
   * @param train
   */
  public UncertaintySampler( TrainRelation<E> train ) {
    super( train );
  }

  /**
   * Get the m most uncertain (least certain) points for learning.
   */
  @Override
  protected Set<UnlabeledFeatureVector<E>> getNextLearningSet(
      TrainRelation<E> labeled, TestRelation<E> unlabeled, int k, int m )
    throws IncomparableFeatureVectorException {
    Set<UnlabeledFeatureVector<E>> learningSet =
        new HashSet<UnlabeledFeatureVector<E>>();
    WeightedKnnClassifier<E> knn = new WeightedKnnClassifier<E>();
    knn.train( labeled );

    // get the uncertainty values for all unlabeled instances
    TreeMap<Double, Set<UnlabeledFeatureVector<E>>> map =
        new TreeMap<Double, Set<UnlabeledFeatureVector<E>>>();
    for ( UnlabeledFeatureVector<E> fv : unlabeled ) {
      Double certainty = knn.getCertainty( fv, k );
      if ( map.containsKey( certainty ) ) {
        map.get( certainty ).add( fv );
      } else {
        Set<UnlabeledFeatureVector<E>> s =
            new HashSet<UnlabeledFeatureVector<E>>();
        s.add( fv );
        map.put( certainty, s );
      }
    }

    // get the m most uncertain (ie. least certain) labeled instances
    for ( Double d : map.keySet() ) {
      Set<UnlabeledFeatureVector<E>> s = map.get( d );
      for ( UnlabeledFeatureVector<E> fv : s ) {
        if ( learningSet.size() == m ) {
          break;
        } else {
          //System.out.println( "\tLearning set + " + d + "(" + fv + ")" );
          learningSet.add( fv );
        }
      }
    }

    return learningSet;
  }

}
