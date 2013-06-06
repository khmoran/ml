package edu.tufts.cs.ml.cluster;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.Util;

public class KMedoids<E> extends KMethod<E> {

  /**
   * Default constructor.
   *
   * @param dataset
   */
  public KMedoids( Relation<? extends FeatureVector<E>> dataset ) {
    super( dataset );
  }

  /**
   * Compute the median vector of the cluster.
   *
   * @param cluster
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected Centroid<E> computeClusterCentroid( Cluster<E> cluster, int num )
    throws IncomparableFeatureVectorException {
    Map<Integer, Double> errMap = new HashMap<Integer, Double>();

    for ( int i = 0; i < cluster.size(); i++ ) {
      FeatureVector<?> fv1 = cluster.get( i );
      double err = 0.0;
      for ( FeatureVector<?> fv2 : cluster ) {

        double dist = fv1.getEuclideanDistance( fv2 );
        err += ( dist*dist );
      }
      errMap.put( i, err );
    }

    SortedSet<Entry<Integer, Double>> errs = Util.sortByEntries( errMap );
    Entry<Integer, Double> entry = errs.last(); // should be feature vector with
                                                // smallest error

    Centroid<E> c = new Centroid<E>( cluster.get( entry.getKey() ) );

    return c;
  }

}
