package edu.tufts.cs.ml.normalize;

import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TrainRelation;

public class ZScoreNormalizerTest extends TestCase {

  /**
   * Test the z-score normalizer.
   */
  @Test
  public void testZScoreNormalizer() {
    String testFeatureName = "TestFeature";
    String testClassName = "TestLabel";
    Metadata m = new Metadata();
    m.put( testFeatureName, Double.class );
    TrainRelation<String> trainingData = new TrainRelation<String>(
        "Testing", m );

    double[] initialNumbers = { 1, 3, 4, 6, 9, 19 };
    for ( double d : initialNumbers ) {
      LabeledFeatureVector<String> lfv = new LabeledFeatureVector<String>(
          testClassName, testFeatureName );
      lfv.put( testFeatureName, new DoubleFeature( testFeatureName, d ) );
      trainingData.add( lfv );
    }

    System.out.println( trainingData.toString() );

    ZScoreNormalizer<String> zNorm = new ZScoreNormalizer<String>(
        trainingData );
    double mean = zNorm.meanMap.get( testFeatureName );
    double sampleSd = zNorm.sampleSdMap.get( testFeatureName );
    System.out.println( "Mean:\t" + mean );
    System.out.println( "Sample standard deviation:\t" + sampleSd );

    double truthMean = 7;
    double truthSampleSd = 6.48;
    assertTrue( mean == truthMean );
    assertTrue( truthSampleSd == 6.48 );

    System.out.println( trainingData.toString() );
  }
}
