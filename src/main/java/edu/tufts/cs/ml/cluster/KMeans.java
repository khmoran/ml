package edu.tufts.cs.ml.cluster;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;

public class KMeans<E> extends KMethod<E> {

  /**
   * Default constructor.
   *
   * @param dataset
   */
  public KMeans( Relation<? extends FeatureVector<E>> dataset ) {
    super( dataset );
  }

  /**
   * Compute the mean vector of the cluster.
   *
   * @param cluster
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected Centroid<E> computeClusterCentroid( Cluster<E> cluster, int num )
    throws IncomparableFeatureVectorException {
    Centroid<E> mean = new Centroid<E>( "centroid" + num );

    Map<String, BigDecimal> totals = new HashMap<String, BigDecimal>();
    // initialize the values
    for ( String f : dataset.get( 0 ).keySet() ) {
      totals.put( f, new BigDecimal( 0.0 ) );
    }

    for ( FeatureVector<E> fv : cluster ) {
      for ( String f : fv.keySet() ) {
        Feature<?> feat = fv.get( f );
        if ( !( feat instanceof DoubleFeature ) ) {
          throw new IncomparableFeatureVectorException(
              "Must be DoubleFeature to compute mean." );
        }
        if ( !totals.containsKey( f ) ) {
          throw new IncomparableFeatureVectorException(
              "Unknown feature: " + f );
        }
        DoubleFeature df = (DoubleFeature) feat;

        BigDecimal total = totals.get( f );
        total = total.add( new BigDecimal( df.getValue() ) );
        totals.put( f, total );
      }
    }

    for ( String s : totals.keySet() ) {
      BigDecimal total = totals.get( s );
      if ( total.doubleValue() != 0.0 ) {
        BigDecimal clusterSize = new BigDecimal( cluster.size() );
        BigDecimal m = total.divide( clusterSize, RoundingMode.HALF_UP );
        mean.put( s, new DoubleFeature( s, m.doubleValue() ) );
      } else {
        mean.put( s, new DoubleFeature( s, 0.0 ) );
      }
    }

    return mean;
  }
}
