package edu.tufts.cs.ml.cluster;

import java.util.ArrayList;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;



public class Cluster<E> extends ArrayList<FeatureVector<E>> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = 6236305380886206787L;
  /** This Cluster's Centroid. */
  private Centroid<E> centroid;

  /**
   * Default constructor.
   * @param centroid
   */
  public Cluster( Centroid<E> centroid ) {
    this.centroid = centroid;
  }

  /**
   * Get the Cluster Centroid.
   * @return
   */
  public Centroid<E> getCentroid() {
    return this.centroid;
  }

  /**
   * Calculate the sum of the squared errors for this Cluster.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public double calculateSSE() throws IncomparableFeatureVectorException {
    double err = 0.0;
    for ( FeatureVector<?> fv : this ) {
      double dist = fv.getEuclideanDistance( this.getCentroid() );

      err += ( dist * dist );
    }

    return err;
  }

}
