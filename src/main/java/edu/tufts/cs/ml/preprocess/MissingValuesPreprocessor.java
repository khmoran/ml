package edu.tufts.cs.ml.preprocess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.MissingFeature;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.util.MathUtil;
import edu.tufts.cs.ml.util.Util;

public class MissingValuesPreprocessor<E> {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      MissingValuesPreprocessor.class.getName() );
  /** The method for filling in the missing values. */
  public enum Method { MEAN, MEDIAN, MEAN_SAME_CLASS }
  /** The training data. */
  protected TrainRelation<E> trainingData;

  /**
   * Default constructor.
   * @param train
   */
  public MissingValuesPreprocessor( TrainRelation<E> train ) {
    this.trainingData = train;
  }

  /**
   * Preprocess the train and test relations with the default values.
   * @param test
   */
  public void preprocess( Relation<? extends FeatureVector<E>> test ) {
    preprocess( test, Method.MEAN_SAME_CLASS );
  }

  /**
   * Get the features relevant for this MissingFeature given the Method.
   * @param r
   * @param method
   * @param f
   * @return
   */
  protected Set<Feature<?>> getRelevantFeatures(
      Relation<? extends FeatureVector<E>> r,
          Method method, Feature<?> f, E label ) {
    Set<Feature<?>> relevantFeatures = new HashSet<Feature<?>>();
    for ( LabeledFeatureVector<E> fv : trainingData ) {
      Feature<?> matchingFeature = fv.get( f.getName() );
      if ( !( matchingFeature instanceof MissingFeature ) ) {
        if ( method == Method.MEAN_SAME_CLASS ) {
          if ( label == null || fv.getLabel().equals( label ) ) {
            relevantFeatures.add( matchingFeature );
          }
        } else {
          relevantFeatures.add( matchingFeature );
        }
      }
    }

    return relevantFeatures;
  }

  /**
   * Fill in the values of the provided set given the Method.
   * @param r
   * @param method
   */
  protected void fillInValues( Relation<? extends FeatureVector<E>> r,
      Method method, boolean test ) {
    Map<Feature<?>, FeatureVector<E>> newFeatures =
        new HashMap<Feature<?>, FeatureVector<E>>();

    for ( FeatureVector<E> fv : r ) {
      for ( Feature<?> f : fv.values() ) {
        if ( f instanceof MissingFeature ) {
          E label = null;
          if ( fv instanceof LabeledFeatureVector && !test ) {
            label = ( (LabeledFeatureVector<E>) fv ).getLabel();
          }
          Set<Feature<?>> relevantFeatures = getRelevantFeatures(
              r, method, f, label );
          double valToAssign = 0;
          if ( method == Method.MEAN || method == Method.MEAN_SAME_CLASS ) {
            valToAssign = Util.round( MathUtil.calcMean(
                          Util.featuresToDoubles( relevantFeatures ) ), 1 );
            LOG.log( Level.INFO, "Assigning mean " + valToAssign +
                " to features " + f.getName() );
          } else {
            valToAssign = Util.round( MathUtil.calcMedian(
                          Util.featuresToDoubles( relevantFeatures ) ), 1 );
            LOG.log( Level.INFO, "Assigning median " + valToAssign +
                " to features " + f.getName() );
          }
          f = new DoubleFeature( f.getName(), valToAssign );
          newFeatures.put( f, fv );
        }
      }
    }

    for ( Feature<?> f : newFeatures.keySet() ) {
      String fName = f.getName();
      FeatureVector<E> fv = newFeatures.get( f );
      fv.put( fName, f );
    }
  }

  /**
   * Preprocess the data with a given Method.
   * @param test
   * @param method
   */
  public void preprocess( Relation<? extends FeatureVector<E>> test,
      Method method ) {
    fillInValues( test, method, true );
    fillInValues( trainingData, method, false );
  }

}
