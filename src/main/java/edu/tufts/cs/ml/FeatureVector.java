package edu.tufts.cs.ml;

import java.util.LinkedHashMap;

import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.Util;

public abstract class FeatureVector<E> extends
  LinkedHashMap<String, Feature<?>> implements Comparable<FeatureVector<E>> {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = -5250706109618829326L;
  /** The vector id. */
  protected String id;
  /** Store the magnitude. */
  protected Double magnitude = null; // TODO this may need to be updated?
  /** The rank (for svm-light). */
  protected Integer rank = null;
  /** The query id (for svm-light). */
  protected Integer qid = null;

  /**
   * Default constructor that takes an id.
   * @param id
   */
  public FeatureVector( String id ) {
    this.id = id;
  }

  /**
   * Get the FeatureVector's id.
   * @return
   */
  public String getId() {
    return this.id;
  }

  /**
   * Set the rank (for svm-light).
   * @param rank
   */
  public void setRank( Integer rank ) {
    this.rank = rank;
  }

  /**
   * Get the rank (for svm-light).
   * @return
   */
  public Integer getRank() {
    return this.rank;
  }

  /**
   * Set the query id (for svm-light).
   * @param qid
   */
  public void setQid( Integer qid ) {
    this.qid = qid;
  }

  /**
   * Get the qid (for svm-light).
   * @return
   */
  public Integer getQid() {
    return this.qid;
  }

  /**
   * Get the Euclidean distance between the two feature vectors.
   * @param fv
   * @return
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  public double getEuclideanDistance( FeatureVector<?> fv )
    throws IncomparableFeatureVectorException {
    FeatureVector<?> feat1 = fv;
    FeatureVector<?> feat2 = this;

    if ( feat1.size() != feat2.size() ) {
      throw new IncomparableFeatureVectorException( this, fv,
          IncomparableFeatureVectorException.DIFF_NUM_FEATURES );
    }

    double sum = 0;
    for ( String featName : feat1.keySet() ) {
      Feature<?> f1 = feat1.get( featName );
      Feature<?> f2 = feat2.get( featName );

      if ( !f1.getValue().getClass().equals( f2.getValue().getClass() ) ) {
        throw new IncomparableFeatureVectorException( this, fv,
            IncomparableFeatureVectorException.DIFF_TYPE_FEATURES );
      }

      Double diff = f1.getDifference( f1.getClass().cast( f2 ) );
      Double diffsq = diff*diff;
      sum += diffsq;
    }

    double sqrt = Math.sqrt( sum );
    return Util.round( sqrt, 6 );
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ( ( this.id == null ) ? 0 : this.id.hashCode() );
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( !super.equals( obj ) )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    FeatureVector<?> other = (FeatureVector<?>) obj;
    if ( this.id == null ) {
      if ( other.id != null )
        return false;
    } else if ( !this.id.equals( other.id ) )
      return false;
    return true;
  }

  /**
   * Normalize this vector.
   * @return
   */
  public void normalize() {
    if ( this.magnitude == null ) {
      magnitude();
    }

    for ( Feature<?> f : this.values() ) {
      DoubleFeature feat = (DoubleFeature) f;
      double unnormalized = Double.parseDouble( f.getValue().toString() );
      Double normalized = unnormalized/this.magnitude;
      feat.setValue( normalized );
    }
  }

  /**
   * Calculate the magnitude of this vector:
   * |a| = sqrt((ax * ax) + (ay * ay) + (az * az)).
   * @return
   */
  public double magnitude() {
    if ( this.magnitude == null ) {
      double total = 0.0;
      for ( Feature<?> f : this.values() ) {
        double val = Double.parseDouble( f.getValue().toString() );
        total += ( val*val );
      }

      this.magnitude = Math.sqrt( total );
    }

    return this.magnitude;
  }

  /**
   * Return the inner product of this Vector a and b.
   * @param that
   * @return
   */
  public double dot( FeatureVector<E> that ) {
    double sum = 0.0;
    for ( String featureName : this.keySet() ) {
      double f1 = Double.valueOf(
          this.get( featureName ).getValue().toString() );
      double f2 = 0.0;
      if ( that.containsKey( featureName ) ) {
        f2 = Double.valueOf( that.get( featureName ).getValue().toString() );
      }
      sum = sum + ( f1 * f2 );
    }
    return sum;
  }

  public int compareTo( FeatureVector<E> o ) {
    return ( (Integer) this.size() ).compareTo( o.size() );
  }
}
