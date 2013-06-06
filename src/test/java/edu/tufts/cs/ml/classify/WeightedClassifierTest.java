package edu.tufts.cs.ml.classify;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.reader.ArffReader;


public class WeightedClassifierTest extends TestCase {

  /**
   * Test the set partitioning.
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  @Test
  public void testGaussianWeighting() throws IOException,
  IncomparableFeatureVectorException {
    /* includes:
     *  6.1,2.9,4.7,1.4,versicolor,x4
        6.0,2.7,5.1,1.6,versicolor,x5
        6.0,2.7,5.1,1.6,virginica,x6 // an engineered tie with the one above
        ...
     */
    File trainFile = new File( "src/test/resources/train.arff" );
    ArffReader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
      trainFile );
    double sigma = 0.45;
    train.setSigma( sigma );
    // assertTrue( train.size() == 3 );

    /* contents:
       6.7,3.1,4.4,1.4,x1
     */
    File testFile = new File( "src/test/resources/test.arff" );
    TestRelation<String> test = (TestRelation<String>) reader.read( testFile );

    // distances: 0.7, 1.1
    WeightedKnnClassifier<String> knn = new WeightedKnnClassifier<String>();
    knn.train( train );

    FeatureVector<?> fvI1 = train.get( 0 );
    FeatureVector<?> fvI2 = train.get( 1 );
    FeatureVector<?> fvI3 = train.get( 2 );
    UnlabeledFeatureVector<?> fvTest = test.get( 0 );

    System.out.println( "FV-test: " + fvTest );
    System.out.println( "Sigma: " + sigma );

    // first dist/vote
    double dist1 = fvI1.getEuclideanDistance( fvTest );
    double vote1 = knn.vote( dist1 ).doubleValue();
    assertTrue( vote1 == Math.exp( ( dist1*dist1*-1 )/( 2*sigma*sigma ) ) );
    System.out.println( "FV-1: " + fvI1 );
    System.out.println( "Dist: " + dist1 );
    System.out.println( "Vote: " + vote1 + "\n" );

    // second dist/vote
    double dist2 = fvI2.getEuclideanDistance( fvTest );
    double vote2 = knn.vote( dist2 ).doubleValue();
    assertTrue( vote2 == Math.exp( ( dist2*dist2*-1 )/( 2*sigma*sigma ) ) );
    System.out.println( "FV-2: " + fvI2 );
    System.out.println( "Dist: " + dist2 );
    System.out.println( "Vote: " + vote2 + "\n" );

    // third dist/vote
    double dist3 = fvI3.getEuclideanDistance( fvTest );
    double vote3 = knn.vote( dist3 ).doubleValue();
    assertTrue( vote3 == Math.exp( ( dist3*dist3*-1 )/( 2*sigma*sigma ) ) );
    System.out.println( "FV-3: " + fvI3 );
    System.out.println( "Dist: " + dist3 );
    System.out.println( "Vote: " + vote3 + "\n" );

    // uncertainty
    double uncert = knn.getCertainty( test.get( 0 ), 5 );
    int[] ks = {5};
    knn.classify( test, ks );
    System.out.println( "Classification: " + fvTest.getClassification( 5 ) );
    System.out.println( "Certainty: " + uncert );

  }

}
