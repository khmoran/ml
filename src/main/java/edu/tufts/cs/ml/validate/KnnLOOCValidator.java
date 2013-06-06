package edu.tufts.cs.ml.validate;

import java.util.Map.Entry;

import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.classify.KnnClassifier;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.Util;

public class KnnLOOCValidator<E> {
  /** The training data. */
  protected TrainRelation<E> trainingData;
  /** The default k values to use if none are specified. */
  protected static final int[] DEFAULT_K_VALS = { 1, 3, 5, 7, 9, 11, 13, 15 };

  /**
   * Default constructor.
   * @param train
   * @param test
   */
  public KnnLOOCValidator( TrainRelation<E> trainingData ) {
    this.trainingData = trainingData;
  }

  /**
   * Compute the accuracy of the validation instance over the given k values.
   * @param validationInstance
   * @param kVals
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public double[] computeAccuracy( LabeledFeatureVector<E> validationInstance,
      int[] kVals ) throws IncomparableFeatureVectorException {
    // train the classifier on the training data subset
    KnnClassifier<E> c = new KnnClassifier<E>();
    c.train( trainingData );

    // turn the labeled validation instance into an unlabeled test instance
    UnlabeledFeatureVector<E> ufv = new UnlabeledFeatureVector<E>(
        validationInstance.getId() );
    for ( Entry<String, Feature<?>> e : validationInstance.entrySet() ) {
      ufv.put( e.getKey(), e.getValue() );
    }

    // classify this "unlabeled" test instance
    c.classify( ufv, kVals );

    double[] accuracies = new double[ kVals.length ];

    // given an accuracy of 1 if the classification for the k-value
    // matched the labeled validation instance; 0 otherwise
    for ( int i = 0; i < kVals.length; i++ ) {
      E validationClass = ufv.getClassification( kVals[i] );
      if ( validationClass.equals( validationInstance.getLabel() ) ) {
        accuracies[i] = 1;
      } else {
        accuracies[i] = 0;
      }
    }

    return accuracies;
  }

  /**
   * Validate the data given the provided k values.
   * @param validationData
   * @param kVals
   * @return
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  public double[] validate( int[] kVals )
    throws IncomparableFeatureVectorException {
    TrainRelation<E> trainingDataIterable =
        (TrainRelation<E>) trainingData.clone();

    double[] compositeAccuracies = new double[ kVals.length ];
    for ( LabeledFeatureVector<E> v : trainingDataIterable ) {
      // remove this training feature from the set to use it as validation
      trainingData.remove( v );

      // now test the classified by seeing how accurately it is classified
      double[] accuracies = computeAccuracy( v, kVals );

      // add the accuracies for the various k-values to the composites
      for ( int i = 0; i < accuracies.length; i++ ) {
        compositeAccuracies[i] += accuracies[i];
      }

      // now put it back in so it forms part of the training corpus
      trainingData.add( v );
    }

    // get the average accuracy for each k-value
    for ( int i = 0; i < compositeAccuracies.length; i++ ) {
      compositeAccuracies[i] = Util.round(
          compositeAccuracies[i]/trainingData.size(), 4 );
    }

    return compositeAccuracies;
  }

  /**
   * Validate the data given default settings.
   * @param validationData
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public double[] validate()
    throws IncomparableFeatureVectorException {
    return validate( DEFAULT_K_VALS );
  }
}
