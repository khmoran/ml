package edu.tufts.cs.ml.classify;

import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;

public interface Classifier<E> {

  /**
   * Train the classifier.
   * @param trainRelation
   */
  void train( TrainRelation<E> trainRelation );

  /**
   * Classify a test instance.
   * @param testInstance
   * @throws IncomparableFeatureVectorException
   */
  void classify( UnlabeledFeatureVector<E> testInstance )
    throws IncomparableFeatureVectorException;

  /**
   * Classify a test relation.
   * @param testRelation
   * @throws IncomparableFeatureVectorException
   */
  void classify( TestRelation<E> testRelation )
    throws IncomparableFeatureVectorException;

  /**
   * Get the certainty of a classification.
   * @param testInstance
   * @return
   */
  double getCertainty( UnlabeledFeatureVector<E> testInstance )
    throws IncomparableFeatureVectorException;
}
