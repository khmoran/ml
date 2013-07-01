package edu.tufts.cs.ml.classify;

import java.util.ArrayList;

import ca.uwo.csd.ai.nlp.kernel.LinearKernel;
import ca.uwo.csd.ai.nlp.mallet.libsvm.SVMClassifierTrainer;
import cc.mallet.classify.Classification;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.MalletConverter;

public class SVMClassifier implements Classifier<Integer> {
  /** Train relation. */
  protected TrainRelation<Integer> train;
  /** The Mallet classifier. */
  protected ca.uwo.csd.ai.nlp.mallet.libsvm.SVMClassifier svm;

  /**
   * Train the SVM.
   */
  public void train( TrainRelation<Integer> trainRelation ) {
    this.train = trainRelation;
    // "true" is to predict the probability
    SVMClassifierTrainer trainer = new SVMClassifierTrainer(
        new LinearKernel(), true );
    InstanceList instances = MalletConverter.convert( trainRelation );
    trainer.train( instances );
    this.svm = trainer.getClassifier();
  }

  /**
   * Classify the test instance.
   */
  public void classify( UnlabeledFeatureVector<Integer> testInstance )
    throws IncomparableFeatureVectorException {
    Instance instance = MalletConverter.convert( testInstance,
        train.getMetadata() );
    Classification c = svm.classify( instance );
    Integer label = Integer.valueOf(
        c.getLabeling().getBestLabel().toString() );
    testInstance.setClassification( label );
  }

  /**
   * Classify the test data.
   */
  public void classify( TestRelation<Integer> testRelation )
    throws IncomparableFeatureVectorException {
    InstanceList instances = MalletConverter.convert( testRelation );
    ArrayList<Classification> cls = this.svm.classify( instances );

    for ( int i = 0; i < testRelation.size(); i++ ) {

      Integer label = Integer.valueOf(
          cls.get( i ).getLabeling().getBestLabel().toString() );
      testRelation.get( i ).setClassification( label );
    }
  }

  /**
   * Calculate the SVM's certainty on the given test instance.
   */
  public double getCertainty( UnlabeledFeatureVector<Integer> testInstance )
    throws IncomparableFeatureVectorException {
    Instance instance = MalletConverter.convert( testInstance,
        train.getMetadata() );
    Classification c = svm.classify( instance );

    Integer label = Integer.valueOf(
        c.getLabeling().getBestLabel().toString() );
    testInstance.setClassification( label );

    if ( c.getLabeling().numLocations() > 1 ) {
      return c.getLabeling().getValueAtRank( 0 ) -
          c.getLabeling().getValueAtRank( 1 );
    } else {
      return c.getLabeling().getValueAtRank( 0 );
    }
  }

}
