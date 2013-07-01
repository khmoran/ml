package edu.tufts.cs.ml.classify;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.MathUtil;
import edu.tufts.cs.ml.util.Util;

public class NaiveBayesClassifier<E> implements Classifier<E> {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      NaiveBayesClassifier.class.getName() );
  /** Feature means. */
  protected Map<E, Map<String, Double>> featureMeans =
      new HashMap<E, Map<String, Double>>();
  /** Feature standard deviations. */
  protected Map<E, Map<String, Double>> featureStdDevs =
      new HashMap<E, Map<String, Double>>();
  /** The prior probability for each label. */
  protected Map<E, Double> priorProbs = new HashMap<E, Double>();

  /**
   * Default constructor.
   */
  public NaiveBayesClassifier() {

  }

  /**
   * Train the classifier.
   */
  public void train( TrainRelation<E> trainRelation ) {
    // separate the training data by class
    Map<E, Integer> counts = new HashMap<E, Integer>();
    Map<E, TrainRelation<E>> subrelations = new HashMap<E, TrainRelation<E>>();
    for ( LabeledFeatureVector<E> fv : trainRelation ) {
      TrainRelation<E> subrelation = subrelations.get( fv.getLabel() );
      if ( subrelation == null ) {
        subrelation = new TrainRelation<E>( fv.getLabel().toString(),
            (Metadata) trainRelation.getMetadata().clone() );
        subrelations.put( fv.getLabel(), subrelation );
      }
      subrelation.add( fv );

      Integer count = counts.get( fv.getLabel() );
      if ( count == null ) count = 0;
      counts.put( fv.getLabel(), ++count );
    }

    // change the counts into the prior probabilities
    for ( E label : counts.keySet() ) {
      double priorProb = (double) counts.get( label ) /
          (double) trainRelation.size();
      priorProbs.put( label, priorProb );
    }

    // now get the means and std devs for each feature, separated by class
    for ( E label : subrelations.keySet() ) {
      TrainRelation<E> subrelation = subrelations.get( label );
      featureMeans.put( label, calculateFeatureMeans( subrelation ) );
      featureStdDevs.put( label, calculateFeatureStdDevs( subrelation ) );
    }
  }

  /**
   * Calculate the Maximum Likelihood Estimation.
   * @return
   */
  protected Map<E, Double> calculateMLE(
      UnlabeledFeatureVector<E> testInstance ) {
    Map<E, Double> probs = new HashMap<E, Double>();

    for ( E label : featureMeans.keySet() ) {
      Map<String, Double> means = featureMeans.get( label );
      Map<String, Double> stdevs = featureStdDevs.get( label );

      // start with the prior probabilities
      double prob = priorProbs.get( label );

      // then multiply the probability by each feature pdf for the label
      for ( String feat : testInstance.keySet() ) {
        // get the probability density function of this value (n)
        double mean = means.get( feat );
        double stdev = stdevs.get( feat );
        double x = (Double) testInstance.get( feat ).getValue();
        double pdf = MathUtil.calcPdf( x, mean, stdev );

        prob = prob * pdf;
      }

      probs.put( label, prob );
    }

    return probs;
  }

  /**
   * Classify the test feature vector.
   */
  public void classify( UnlabeledFeatureVector<E> testInstance )
    throws IncomparableFeatureVectorException {
    Map<E, Double> probs = calculateMLE( testInstance );

    // now get the maximum probability, and that label is the winner
    Entry<E, Double> max = null;
    for ( Entry<E, Double> entry : probs.entrySet() ) {
      if ( max == null || entry.getValue().compareTo( max.getValue() ) > 0 ) {
        max = entry;
      }
    }

    LOG.log( Level.FINE, "Probabilities for " + testInstance.getId() + ": " +
      probs );
    testInstance.setClassification( max.getKey() );
  }

  /**
   * Classify the test relation.
   */
  public void classify( TestRelation<E> testRelation )
    throws IncomparableFeatureVectorException {
    for ( UnlabeledFeatureVector<E> ufv : testRelation ) {
      classify( ufv );
    }
  }

  /**
   * Get the classifier's certainty.
   */
  public double getCertainty( UnlabeledFeatureVector<E> testInstance ) {
    Map<E, Double> probs = calculateMLE( testInstance );

    // now get the maximum probability, and that label is the winner
    Entry<E, Double> max = null;
    Entry<E, Double> second = null;
    for ( Entry<E, Double> entry : probs.entrySet() ) {
      if ( max == null || entry.getValue().compareTo( max.getValue() ) > 0 ) {
        second = max;
        max = entry;
      } else if ( second == null ) {
        second = entry;
      }
    }

    //LOG.info( "First place: " + max );
    //LOG.info( "Second place: " + second );

    if ( second == null ) return 1.0;
    else return max.getValue() - second.getValue();
  }

  /**
   * Calculate the mean for each feature.
   */
  protected Map<String, Double> calculateFeatureMeans( TrainRelation<E> data ) {
    Map<String, Double> meanMap = new HashMap<String, Double>();
    for ( String featureName : data.getMetadata().keySet() ) {
      List<Double> dVals = Util.featuresToDoubles( featureName, data );
      meanMap.put( featureName, MathUtil.calcMeanLaplace( dVals,
          priorProbs.keySet().size() ) );
    }

    return meanMap;
  }

  /**
   * Calculate the sample standard deviation for each feature.
   */
  protected Map<String, Double> calculateFeatureStdDevs(
      TrainRelation<E> data ) {
    Map<String, Double> stdDevMap = new HashMap<String, Double>();
    for ( String featureName : data.getMetadata().keySet() ) {
      List<Double> dVals = Util.featuresToDoubles( featureName, data );
      stdDevMap.put( featureName, MathUtil
          .calcStandardDeviationLaplace( dVals, priorProbs.keySet().size() ) );
    }

    return stdDevMap;
  }

}
