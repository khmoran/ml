package edu.tufts.cs.ml.cluster;

import java.util.HashMap;

import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.Util;



public class ClusterSet<E> extends HashMap<Centroid<E>, Cluster<E>> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = 6236305380886206787L;

  /**
   * Calculate the sum of the squared errors for this set of Clusters.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public double calculateSSE() throws IncomparableFeatureVectorException {
    double err = 0.0;

    for ( Cluster<E> c : this.values() ) {
      err += c.calculateSSE();
    }

    err = Util.round( err, 2 );
    return err;
  }

}
