package edu.tufts.cs.ml.validate;

import java.util.Map.Entry;

import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.classify.Classifier;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.Util;

public class LOOCValidator<E> {
  /** The training data. */
  protected TrainRelation<E> trainingData;

  /**
   * Default constructor.
   * @param train
   * @param test
   */
  public LOOCValidator( TrainRelation<E> trainingData ) {
    this.trainingData = trainingData;
  }

  /**
   * Compute the accuracy of the validation instance over the given k values.
   * @param validationInstance
   * @param kVals
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public double computeAccuracy( Classifier<E> cl,
      LabeledFeatureVector<E> validationInstance )
    throws IncomparableFeatureVectorException {
    // train the classifier on the training data subset
    cl.train( trainingData );

    // turn the labeled validation instance into an unlabeled test instance
    UnlabeledFeatureVector<E> ufv = new UnlabeledFeatureVector<E>(
        validationInstance.getId() );
    for ( Entry<String, Feature<?>> e : validationInstance.entrySet() ) {
      ufv.put( e.getKey(), e.getValue() );
    }

    // classify this "unlabeled" test instance
    cl.classify( ufv );

    E validationClass = ufv.getClassification();
    if ( validationClass.equals( validationInstance.getLabel() ) ) {
      return 1;
    } else {
      return 0;
    }
  }

  /**
   * Validate the data given the provided k values.
   * @param validationData
   * @param kVals
   * @return
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  public double validate( Classifier<E> cl )
    throws IncomparableFeatureVectorException {
    TrainRelation<E> trainingDataIterable =
        (TrainRelation<E>) trainingData.clone();

    double totalAccuracy = 0.0;
    for ( LabeledFeatureVector<E> v : trainingDataIterable ) {
      // remove this training feature from the set to use it as validation
      trainingData.remove( v );

      // now test the classified by seeing how accurately it is classified
      double accuracy = computeAccuracy( cl, v );

      totalAccuracy += accuracy;

      // now put it back in so it forms part of the training corpus
      trainingData.add( v );
    }

    double avgAccuracy = Util.round( totalAccuracy/trainingData.size(), 4 );

    return avgAccuracy;
  }
}
