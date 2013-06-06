package edu.tufts.cs.ml.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;

public class MathUtil {
  /** The random number generator. */
  protected static final Random RND = new Random();

  /**
   * Private constructor for utility class.
   */
  private MathUtil() {
    // purpose not instantiable
  }

  /**
   * Get a random sample of size m from items.
   * @param items
   * @param m
   * @return
   */
  public static <T> Set<T> randomSample( List<T> items, int m ) {
    HashSet<T> res = new HashSet<T>( m );
    int n = items.size();
    for ( int i = n - m; i < n; i++ ) {
      int pos = RND.nextInt( i + 1 );
      T item = items.get( pos );
      if ( res.contains( item ) )
        res.add( items.get( i ) );
      else
        res.add( item );
    }
    return res;
  }

  /**
   * Calculate the median of a collection of doubles.
   *
   * @param vals
   * @return
   */
  public static double calcMedian( Collection<Double> vals ) {
    List<Double> sorted = new ArrayList<Double>();
    sorted.addAll( vals );
    Collections.sort( sorted );

    if ( vals.size() % 2 == 1 ) {
      return sorted.get( ( vals.size() + 1 ) / 2 - 1 );
    } else {
      double lower = sorted.get( vals.size() / 2 - 1 );
      double upper = sorted.get( vals.size() / 2 );

      return ( lower + upper ) / 2.0;
    }
  }

  /**
   * Calculate the mean of a collection of doubles.
   *
   * @param vals
   * @return
   */
  public static double calcMean( Collection<Double> vals ) {
    double total = 0;
    long numCounted = 0;

    for ( Double d : vals ) {
      total += d;
      numCounted++;
    }

    return total / numCounted;
  }

  /**
   * Calculate the mean of a collection of doubles.
   *
   * @param vals
   * @return
   */
  public static double calcMeanLaplace( Collection<Double> vals,
      int numClasses ) {
    // laplace smoothing
    double total = numClasses; // this should really be # classes
    long numCounted = 1;

    for ( Double d : vals ) {
      total += d;
      numCounted++;
    }

    return total / numCounted;
  }

  /**
   * Calculate the covariance.
   *
   * @param x
   * @param y
   * @return
   */
  public static double calcCovariance( List<Double> x, List<Double> y ) {
    double meanX = calcMean( x );
    double meanY = calcMean( y );

    List<Double> mults = new ArrayList<Double>();
    for ( int i = 0; i < x.size(); i++ ) {
      mults.add( x.get( i ) * y.get( i ) );
    }

    double meanXY = calcMean( mults );
    double meanXmeanY = meanX * meanY;

    return meanXY - meanXmeanY;
  }

  /**
   * Calculate the sample standard deviation for the given feature.
   *
   * @param featureName
   * @return
   */
  public static double calcStandardDeviation( Collection<Double> vals ) {
    List<Double> sqDeviations = new ArrayList<Double>();
    double mean = calcMean( vals );

    for ( Double d : vals ) {
      double deviation = d - mean;
      sqDeviations.add( deviation * deviation );
    }

    double total = 0;
    for ( double d : sqDeviations ) {
      total += d;
    }

    double result = total / ( vals.size() - 1 );
    return Math.sqrt( result );
  }

  /**
   * Calculate the sample standard deviation for the given feature.
   *
   * @param featureName
   * @return
   */
  public static double calcStandardDeviationLaplace(
      Collection<Double> vals, int numClasses ) {
    List<Double> sqDeviations = new ArrayList<Double>();
    double mean = calcMeanLaplace( vals, numClasses );

    for ( Double d : vals ) {
      double deviation = d - mean;
      sqDeviations.add( deviation * deviation );
    }

    double total = 0;
    for ( double d : sqDeviations ) {
      total += d;
    }

    double result = total / ( vals.size() - 1 );
    return Math.sqrt( result );
  }

  /**
   * Calculate the Pearson Correlation Coefficient.
   * @param x
   * @param y
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public static double calcPCC( List<Double> x, List<Double> y )
    throws IncomparableFeatureVectorException {
    if ( x.size() != y.size() ) {
      throw new IncomparableFeatureVectorException(
          "the vectors are different sizes" );
    }
    double n = x.size();

    double sum_sq_x = 0;
    double sum_sq_y = 0;
    double sum_coproduct = 0;
    double mean_x = 0;
    double mean_y = 0;

    for ( int i = 0; i < n; i++ ) {
      double xi = x.get( i );
      double yi = y.get( i );
      sum_sq_x += xi * xi;
      sum_sq_y += yi * yi;
      sum_coproduct += xi * yi;
      mean_x += xi;
      mean_y += yi;
    }

    mean_x = mean_x / n;
    mean_y = mean_y / n;
    double pop_sd_x = Math.sqrt( ( sum_sq_x / n ) - ( mean_x * mean_x ) );
    double pop_sd_y = Math.sqrt( ( sum_sq_y / n ) - ( mean_y * mean_y ) );
    double cov_x_y = ( sum_coproduct / n ) - ( mean_x * mean_y );
    double correlation = cov_x_y / ( pop_sd_x * pop_sd_y );
    return correlation;
  }

  /**
   * Calculate the z-score for the value given the sample.
   * @param sample
   * @param example
   * @return
   */
  public static double calcZscore( Collection<Double> sample,
      Double rawScore ) {
    double sampleMean = calcMean( sample );
    double sampleSd = calcStandardDeviation( sample );
    double zscore = ( rawScore - sampleMean )/sampleSd;
    return zscore;
  }

  /**
   * Calculate the standard Gaussian pdf.
   * @param x
   * @return
   */
  public static double calcPdf( double x ) {
    return Math.exp( -x*x / 2 ) / Math.sqrt( 2 * Math.PI );
  }

  /**
   * Calculate the Gaussian pdf with mean mu and stddev sigma.
   * @param x Value
   * @param mu Mean
   * @param sigma Std dev
   * @return
   */
  public static double calcPdf( double x, double mu, double sigma ) {
    //System.out.println( x + ", " + mu + ", " + sigma );
    return calcPdf( ( x - mu ) / sigma ) / sigma;
  }

  /**
   * Calculate the standard Gaussian cdf using Taylor approximation.
   * @param z
   * @return
   */
  public static double calcCdf( double z ) {
    if ( z < -8.0 ) return 0.0;
    if ( z >  8.0 ) return 1.0;
    double sum = 0.0, term = z;
    for ( int i = 3; sum + term != sum; i += 2 ) {
      sum  = sum + term;
      term = term * z * z / i;
    }
    return 0.5 + sum * calcPdf( z );
  }

  /**
   * Calculate the Gaussian cdf with mean mu and stddev sigma.
   * @param z Value
   * @param mu Mean
   * @param sigma Std dev
   * @return
   */
  public static double calcCdf( double z, double mu, double sigma ) {
    return calcCdf( ( z - mu ) / sigma );
  }
}
