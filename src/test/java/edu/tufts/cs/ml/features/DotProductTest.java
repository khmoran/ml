package edu.tufts.cs.ml.features;


import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.util.Util;

public class DotProductTest extends TestCase {

  /**
   * Test the dot product calculation.
   */
  @Test
  public void testDotProduct() {
    Metadata m = new Metadata();
    m.put( "one", "numeric" );
    m.put( "two", "numeric" );
    m.put( "three", "numeric" );
    TestRelation<String> test = new TestRelation<String>( "test", m );
    UnlabeledFeatureVector<String> fv1 =
        new UnlabeledFeatureVector<String>( "t1" );
    fv1.put( "one", new DoubleFeature( "one", 1.0 ) );
    fv1.put( "two", new DoubleFeature( "two", 2.0 ) );
    fv1.put( "three", new DoubleFeature( "two", 3.0 ) );

    UnlabeledFeatureVector<String> fv2 =
        new UnlabeledFeatureVector<String>( "t2" );
    fv2.put( "one", new DoubleFeature( "one", 4.0 ) );
    fv2.put( "two", new DoubleFeature( "two", -5.0 ) );
    fv2.put( "three", new DoubleFeature( "one", 6.0 ) );

    test.add( fv1 );
    test.add( fv2 );

    // test a non-normalized dotProduct
    double dotProduct = fv1.dot( fv2 );
    assertTrue( dotProduct == 12 );

    m = new Metadata();
    m.put( "affectation", "numeric" );
    m.put( "jealous", "numeric" );
    m.put( "gossip", "numeric" );
    test = new TestRelation<String>( "test", m );
    fv1 = new UnlabeledFeatureVector<String>( "SaS" );
    fv1.put( "affectation", new DoubleFeature( "affectation", 115.0 ) );
    fv1.put( "jealous", new DoubleFeature( "jealous", 10.0 ) );
    fv1.put( "gossip", new DoubleFeature( "gossip", 2.0 ) );

    fv2 = new UnlabeledFeatureVector<String>( "PaP" );
    fv2.put( "affectation", new DoubleFeature( "affectation", 58.0 ) );
    fv2.put( "jealous", new DoubleFeature( "jealous", 7.0 ) );
    fv2.put( "gossip", new DoubleFeature( "gossip", 0.0 ) );

    UnlabeledFeatureVector<String> fv3 =
        new UnlabeledFeatureVector<String>( "WH" );
    fv3.put( "affectation", new DoubleFeature( "affectation", 20.0 ) );
    fv3.put( "jealous", new DoubleFeature( "jealous", 11.0 ) );
    fv3.put( "gossip", new DoubleFeature( "gossip", 6.0 ) );

    fv1.normalize();
    fv2.normalize();
    fv3.normalize();

    test.add( fv1 );
    test.add( fv2 );
    test.add( fv3 );

    assertTrue( Util.round( Double.parseDouble(
        fv1.get( "affectation" ).getValue().toString() ), 3 ) == .996 );
    assertTrue( Util.round( Double.parseDouble(
        fv2.get( "gossip" ).getValue().toString() ), 3 ) == 0 );

    dotProduct = fv1.dot( fv2 );

    assertTrue( Util.round( dotProduct, 3 ) == .999 );
  }
}
