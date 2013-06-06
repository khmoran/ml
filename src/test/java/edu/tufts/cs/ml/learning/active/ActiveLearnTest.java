package edu.tufts.cs.ml.learning.active;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.reader.ArffReader;

public class ActiveLearnTest extends TestCase {

  /**
   * Test the set partitioning.
   * @throws IOException
   */
  @SuppressWarnings( "unchecked" )
  @Test
  public void testSetPartitioning() throws IOException {

    File trainFile = new File( "src/main/resources/soyBean.arff" );
    ArffReader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );
    Sampler<String> s = new UncertaintySampler<String>( train );

    int v = 40;
    int k = 5;
    int numClasses = 15;
    int numInstancesPerClass = (int) Math.ceil(
        (double) k/(double) numClasses );

    TrainRelation<String> trc = (TrainRelation<String>) train.clone();

    TrainRelation<String> validationSet = s.createValidationSet( trc, v );
    TrainRelation<String> labeledSet = s.createLabeledSet( trc, k );
    TestRelation<String> unlabeledSet = s.createUnlabeledSet( trc );

    // need to be exclusive partitions
    assert validationSet.size() + labeledSet.size() +
      unlabeledSet.size() == train.size();

    for ( FeatureVector<?> fv : validationSet ) {
      assertFalse( labeledSet.contains( fv ) );
      assertTrue( train.contains( fv ) );

      for ( FeatureVector<?> ulfv : unlabeledSet ) {
        assertTrue( ulfv.getId() != fv.getId() );
      }
    }

    Map<String, Integer> seenLabels = new HashMap<String, Integer>();
    for ( LabeledFeatureVector<String> fv : labeledSet ) {
      if ( !seenLabels.containsKey( fv.getLabel() ) ) {
        seenLabels.put( fv.getLabel(), 1 );
      } else {
        int numSeen = seenLabels.get( fv.getLabel() );
        seenLabels.put( fv.getLabel(), ++numSeen );
      }

      assertFalse( validationSet.contains( fv ) );
      assertTrue( train.contains( fv ) );
      assertTrue( seenLabels.get( fv.getLabel() ) <= numInstancesPerClass );

      for ( FeatureVector<?> ulfv : unlabeledSet ) {
        assertTrue( ulfv.getId() != fv.getId() );
      }
    }

    for ( FeatureVector<?> fv : unlabeledSet ) {
      for ( FeatureVector<?> lfv : labeledSet ) {
        assertTrue( lfv.getId() != fv.getId() );
      }
      for ( FeatureVector<?> lfv : validationSet ) {
        assertTrue( lfv.getId() != fv.getId() );
      }
      boolean found = false;
      for ( FeatureVector<?> lfv : train ) {
        if ( lfv.getId() == fv.getId() ) {
          found = true;
          break;
        }
      }
      assertTrue( found );
    }

  }
}
