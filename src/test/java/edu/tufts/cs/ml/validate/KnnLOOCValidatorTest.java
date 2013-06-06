package edu.tufts.cs.ml.validate;
import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.reader.ArffReader;


public class KnnLOOCValidatorTest extends TestCase {

  /**
   * Test the Classifier.
   */
  @Test
  public void testClassifier() {
  }

  /**
   * Test the function that calculates distances.
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  @Test
  public void testKnnLOOCValidator() throws IOException,
    IncomparableFeatureVectorException {
    File trainFile = new File( "src/main/resources/knn-train.arff" );
    ArffReader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    int[] kVals = { 1, 3, 5, 7, 9, 11, 13, 15 };
    KnnLOOCValidator<String> validator = new KnnLOOCValidator<String>( train );
    double[] kAccuracies = validator.validate( kVals );

    for ( int i = 0; i < kVals.length; i++ ) {
      System.out.println( "k: " + kVals[i] + "\taccuracy:" + kAccuracies[i] );
    }
  }


}
