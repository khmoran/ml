package edu.tufts.cs.ml.cluster;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.Util;

public abstract class KMethod<E> extends Observable {
  /** The Logger. */
  private static final Logger LOG = Logger.getLogger( KMethod.class.getName() );
  /** The dataset to cluster. */
  protected Relation<? extends FeatureVector<E>> dataset;
  /** The maximum number of iterations to run if k-Means hasn't converged. */
  protected static final int MAX_ITERATIONS = 50;

  /**
   * Default constructor.
   *
   * @param dataset
   */
  public KMethod( Relation<? extends FeatureVector<E>> dataset ) {
    this.dataset = dataset;
  }

  /**
   * Compute the new Centroid vector of the cluster.
   *
   * @param cluster
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected abstract Centroid<E> computeClusterCentroid(
      Cluster<E> cluster, int num ) throws IncomparableFeatureVectorException;

  /**
   * Detect the best value of k by drawing a line between (min-k, min-k-sse)
   * and (max-k, max-k-sse) and finding the k with the longest perpendicular
   * line segment from its (k, sse) point to that line. This is the "elbow"
   * or the "knee" of the error curve.
   * @param min
   * @param max
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public int detectK( int min, int max )
    throws IncomparableFeatureVectorException {
    LOG.log( Level.INFO, "Detecting best k between " + min + " and " + max );

    Map<Integer, Double> sseMap = new HashMap<Integer, Double>();

    for ( int k = min; k <= max; k++ ) {
      double sse = cluster( k ).calculateSSE();
      sseMap.put( k, sse );
    }

    // draw a line
    // x1, y1, x2, y2 = k1, sse1, k2, sse2
    Line2D.Double line = new Line2D.Double(
        min, sseMap.get( min ), max, sseMap.get( max ) );

    double maxDist = 0;
    int maxDistK = min;
    for ( Integer k : sseMap.keySet() ) {
      Point2D.Double kPt = new Point2D.Double( k, sseMap.get( k ) );
      double dist = line.ptLineDist( kPt );

      if ( dist > maxDist || ( dist == maxDist && k < maxDistK ) ) {
        maxDist = dist;
        maxDistK = k;
      }
    }

    LOG.log( Level.INFO, "Best k: " + maxDistK );
    return maxDistK;
  }

  /**
   * Update the centroids based on the clusters.
   *
   * @param clusters
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected Set<Centroid<E>> updateCentroids(
      ClusterSet<E> clusters )
    throws IncomparableFeatureVectorException {
    Set<Centroid<E>> centroids = new HashSet<Centroid<E>>();

    int i = 0;
    for ( Cluster<E> cluster : clusters.values() ) {
      Centroid<E> c = computeClusterCentroid( cluster, ++i );

      centroids.add( c );
    }

    assert centroids.size() == clusters.size();

    return centroids;
  }

  /**
   * Assign each feature vector to its closest centroid.
   *
   * @return
   *
   * @throws IncomparableFeatureVectorException
   */
  protected ClusterSet<E> assignClusters(
      Set<Centroid<E>> centroids )
    throws IncomparableFeatureVectorException {
    ClusterSet<E> clusters = new ClusterSet<E>();

    for ( Centroid<E> centroid : centroids ) { // initialize the clusters
      Cluster<E> c = new Cluster<E>( centroid );
      clusters.put( centroid, c );
    }

    // find the closest centroid for each feature vector
    for ( FeatureVector<E> fv : dataset ) {
      Entry<Centroid<E>, Double> closestCentroid = null;
      for ( Centroid<E> c : centroids ) {
        double dist = fv.getEuclideanDistance( c );
        if ( closestCentroid == null || dist < closestCentroid.getValue() ) {
          closestCentroid =
              new SimpleEntry<Centroid<E>, Double>( c, dist );
        }
      }
      Cluster<E> cluster = clusters.get( closestCentroid.getKey() );
      cluster.add( fv );
    }

    return clusters;
  }

  /**
   * Select the initial cluster centers.
   *
   * @param k
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected Set<Centroid<E>> getInitialCentroids( int k )
    throws IncomparableFeatureVectorException {
    Set<Centroid<E>> centroids = new HashSet<Centroid<E>>();

    // / 1. Choose first centroid: the vector with the highest density (largest
    // / number of points within the average pairwise distance radius (r)

    // get the r value
    BigDecimal total = new BigDecimal( 0.0 );
    for ( FeatureVector<E> fv1 : dataset ) {
      for ( FeatureVector<E> fv2 : dataset ) {
        if ( !fv1.equals( fv2 ) ) {
          BigDecimal dist = new BigDecimal( fv1.getEuclideanDistance( fv2 ) );
          total = total.add( dist );
        }
      }
    }

    // average pairwise distance
    BigDecimal divisor = new BigDecimal( dataset.size() * dataset.size() );
    int r = total.divide( divisor, RoundingMode.HALF_UP ).intValue();

    // get the density for each feature vector
    Map<FeatureVector<E>, Integer> densityMap =
        new HashMap<FeatureVector<E>, Integer>();
    for ( FeatureVector<E> fv1 : dataset ) {

      int density = 0;
      for ( FeatureVector<E> fv2 : dataset ) {

        if ( !fv1.equals( fv2 ) ) {
          double dist = fv1.getEuclideanDistance( fv2 );

          if ( dist < r ) {
            density++;
          }
        }
      }

      densityMap.put( fv1, density );
    }

    SortedSet<Entry<FeatureVector<E>, Integer>> densities =
        Util.sortByEntries( densityMap );

    Centroid<E> centroid = new Centroid<E>( densities.first().getKey() );
    centroids.add( centroid ); // add the first centroid

    // / 2. Choose remaining centroids: vectors with the highest density that
    // / are at LEAST r distance away from other centroids
    int mindist = r;
    while ( centroids.size() < k ) {  // may have to loop if mindist too high
      addFarthesetDensestCentroids( k, mindist, centroids, densities );
      mindist = (int) ( (double) mindist * .75 );
    }

    return centroids;
  }

  /**
   *
   * @param k
   * @param centroids
   * @param densities
   * @throws IncomparableFeatureVectorException
   */
  protected void addFarthesetDensestCentroids( int k, int r,
      Set<Centroid<E>> centroids,
      SortedSet<Entry<FeatureVector<E>, Integer>> densities )
    throws IncomparableFeatureVectorException {

    for ( Entry<FeatureVector<E>, Integer> e : densities ) {
      boolean tooClose = false;
      if ( centroids.size() >= k ) return;
      for ( FeatureVector<E> c : centroids ) {
        if ( c.equals( e ) ) {  // don't compare to yourself
          tooClose = true;
          break;
        }
        double dist = c.getEuclideanDistance( e.getKey() );

        if ( dist < r ) {
          tooClose = true;
          break;  // this entry is too close to an existing centroid
        }
      }

      if ( !tooClose ) {
        centroids.add( new Centroid<E>( e.getKey() ) );
      }
    }
  }

  /**
   * Cluster the dataset into k clusters using k-means with a density-based
   * initialization technique.
   *
   * @param k
   *          The number of clusters.
   * @return The total SSE value
   * @throws IncomparableFeatureVectorException
   */
  public ClusterSet<E> cluster( int k )
    throws IncomparableFeatureVectorException {
    Set<Centroid<E>> centroids = getInitialCentroids( k );
    assert centroids.size() == k : "Initial centroid size != k: "
        + centroids.size() + " vs. " + k;

    return cluster( centroids );
  }

  /**
   * Cluster the dataset based on the provided initial centroids.
   *
   * @param centroids
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected ClusterSet<E> cluster( Set<Centroid<E>> centroids )
    throws IncomparableFeatureVectorException {
    ClusterSet<E> clusters = null;
    Set<Centroid<E>> prevCentroids = null;

    for ( int j = 0; j < MAX_ITERATIONS
      && !( centroids.equals( prevCentroids ) ); j++ ) {
      prevCentroids = centroids;
      clusters = assignClusters( centroids );
      centroids = updateCentroids( clusters );
    }

    return clusters;
  }

  /**
   * Cluster the dataset into k clusters using k-Means.
   *
   * @param k
   *          The number of clusters.
   * @return The total SSE value
   * @throws IncomparableFeatureVectorException
   */
  public ClusterSet<E> cluster( int k, int[] indices )
    throws IncomparableFeatureVectorException {
    assert indices.length == k : "Initial index size != k: " + indices.length
        + " vs. " + k;

    Set<Centroid<E>> centroids = new HashSet<Centroid<E>>();

    for ( int i : indices ) {
      FeatureVector<E> fv = dataset.get( i - 1 );
      centroids.add( new Centroid<E>( fv ) );
    }
    assert centroids.size() == k : "Initial centroid size != k: "
        + centroids.size() + " vs. " + k;

    return cluster( centroids );
  }

}
