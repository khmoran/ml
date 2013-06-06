package edu.tufts.cs.ml.cluster.outlier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.cluster.Centroid;
import edu.tufts.cs.ml.cluster.Cluster;
import edu.tufts.cs.ml.cluster.ClusterSet;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.Util;

public class OutlierDetector<E> {

  /**
   * Detect the outlier clusters from the given Clusters.
   *
   * @param clusters
   *          The clusters.
   * @param p
   *          The percentage of the dataset to use as a (proportional)
   *          threshold.
   * @return
   */
  public ClusterSet<E> detectSmallClusters( ClusterSet<E> clusters, double p ) {
    int datasetSize = 0;
    for ( Cluster<E> c : clusters.values() ) {
      datasetSize += c.size();
    }

    int t = (int) Math.ceil( p * datasetSize );

    return detectSmallClusters( clusters, t );
  }

  /**
   * Detect the outlier clusters from the given Clusters given a threshold t
   * that separates clusters from outliers based on cluster size.
   *
   * @param clusters
   *          The clusters.
   * @param t
   *          Size threshold: clusters of size t or above are clusters;
   *          otherwise they are outliers.
   * @return
   */
  public ClusterSet<E> detectSmallClusters( ClusterSet<E> clusters, int t ) {
    ClusterSet<E> outliers = new ClusterSet<E>();

    for ( Cluster<E> c : clusters.values() ) {
      if ( c.size() < t ) {
        outliers.put( c.getCentroid(), c );
      }
    }

    return outliers;
  }

  /**
   * Detect the outliers within the given Clusters.
   *
   * @param clusters
   *          The clusters.
   * @param p
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public Set<FeatureVector<?>> detectIntraCluster( ClusterSet<E> clusters,
      double t ) throws IncomparableFeatureVectorException {
    Set<FeatureVector<?>> outliers = new HashSet<FeatureVector<?>>();

    for ( Cluster<E> c : clusters.values() ) {
      Map<FeatureVector<?>, Double> distMap =
          new HashMap<FeatureVector<?>, Double>();

      double totalDist = 0.0;
      for ( FeatureVector<E> fv : c ) {
        double dist = fv.getEuclideanDistance( c.getCentroid() );
        totalDist += dist;
        distMap.put( fv, dist );
      }
      double avgDist = totalDist / c.size();
      double maxDist = t * avgDist;

      SortedSet<Entry<FeatureVector<?>, Double>> sorted = Util
          .sortByEntries( distMap );
      for ( Entry<FeatureVector<?>, Double> e : sorted ) {
        double dist = e.getValue();

        if ( dist > maxDist ) {
          outliers.add( e.getKey() );
        } else {
          break;
        }
      }
    }

    return outliers;
  }

  /**
   * Detect the outliers within the given Clusters.
   *
   * @param clusters
   *          The clusters.
   * @param p
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public Set<FeatureVector<?>> detectAll( ClusterSet<E> clusters,
      double pCluster, double tIntra )
    throws IncomparableFeatureVectorException {
    ClusterSet<E> subset = detectSmallClusters( clusters, pCluster );

    ClusterSet<E> remainder = new ClusterSet<E>();
    for ( Centroid<E> c : clusters.keySet() ) {
      if ( !subset.containsKey( c ) ) {
        remainder.put( c, clusters.get( c ) );
      }
    }

    Set<FeatureVector<?>> outliers = detectIntraCluster( remainder, tIntra );

    for ( Cluster<E> c : subset.values() ) {
      outliers.addAll( c );
    }

    return outliers;
  }

  /**
   * Detect the outliers within the given Clusters.
   *
   * @param clusters
   *          The clusters.
   * @param t
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public Set<FeatureVector<?>> detectAll( ClusterSet<E> clusters, int tCluster,
      double tIntra ) throws IncomparableFeatureVectorException {
    ClusterSet<E> subset = detectSmallClusters( clusters, tCluster );

    ClusterSet<E> remainder = new ClusterSet<E>();
    for ( Centroid<E> c : clusters.keySet() ) {
      if ( !subset.containsKey( c ) ) {
        remainder.put( c, clusters.get( c ) );
      }
    }

    Set<FeatureVector<?>> outliers = detectIntraCluster( remainder, tIntra );

    for ( Cluster<E> c : subset.values() ) {
      outliers.addAll( c );
    }

    return outliers;
  }

  /**
   * Detect the outliers within the given Clusters with a fuzzy method.
   *
   * @param clusters
   *          The clusters.
   * @param t
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public Map<FeatureVector<?>, Double> detectAllFuzzy( ClusterSet<E> clusters,
      double t ) throws IncomparableFeatureVectorException {
    Map<FeatureVector<?>, Double> results =
        new HashMap<FeatureVector<?>, Double>();
    final double minTcluster = .025;
    final double maxTcluster = .1;
    final double minTintra = 1.1;
    final double maxTintra = 2;
    final double tClusterIncr = .025;
    final double tIntraIncr = .1;

    int i = 0;
    double tCluster = minTcluster;
    double tIntra = maxTintra;
    Map<FeatureVector<?>, Integer> partial =
        new HashMap<FeatureVector<?>, Integer>();
    while ( tCluster <= maxTcluster ) {
      while ( tIntra >= minTintra ) {
        Set<FeatureVector<?>> outliers =
            detectAll( clusters, tCluster, tIntra );

        for ( FeatureVector<?> fv : outliers ) {
          int numDetections = 1;
          if ( partial.containsKey( fv ) ) {
            numDetections = partial.get( fv ) + 1;
          }
          partial.put( fv, numDetections );
        }

        tIntra -= tIntraIncr;
        i++;
      }
      tIntra = maxTintra;
      tCluster += tClusterIncr;
    }

    for ( FeatureVector<?> fv : partial.keySet() ) {
      double conf = (double) partial.get( fv )/(double) i;
      if ( conf >= t ) {
        double rounded = Util.round( conf, 4 );
        results.put( fv, rounded );
      }
    }

    return results;
  }
}
