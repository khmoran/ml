package edu.tufts.cs.ml.features;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.MathUtil;
import edu.tufts.cs.ml.util.Util;

public class FeatureFilterTest extends TestCase {

  /**
   * Test the z-score normalizer.
   */
  @Test
  public void testFeatureFilter() {
  }

  /**
   * Test the covariance.
   */
  @Test
  public void testCovariance() {
    List<Double> x = new ArrayList<Double>();
    x.add( 1.0 );
    x.add( 3.0 );
    x.add( 3.0 );
    x.add( 5.0 );
    List<Double> y = new ArrayList<Double>();
    y.add( 12.0 );
    y.add( 12.0 );
    y.add( 11.0 );
    y.add( 7.0 );

    double result = MathUtil.calcCovariance( x, y );
    System.out.println( result );
    assertTrue( result == -2.5 );
  }

  /**
   * Test the covariance.
   * @throws IncomparableFeatureVectorException
   */
  @Test
  public void testPCC() throws IncomparableFeatureVectorException {
    List<Double> x = new ArrayList<Double>();
    x.add( 1.0 );
    x.add( 3.0 );
    x.add( 5.0 );
    x.add( 6.0 );
    x.add( 8.0 );
    x.add( 9.0 );
    x.add( 6.0 );
    x.add( 4.0 );
    x.add( 3.0 );
    x.add( 2.0 );
    List<Double> y = new ArrayList<Double>();
    y.add( 2.0 );
    y.add( 5.0 );
    y.add( 6.0 );
    y.add( 6.0 );
    y.add( 7.0 );
    y.add( 7.0 );
    y.add( 5.0 );
    y.add( 3.0 );
    y.add( 1.0 );
    y.add( 1.0 );
    double result = MathUtil.calcPCC( x, y );
    System.out.println( result );
    assertTrue( Util.round( result, 2 ) == .85 );
  }
}
