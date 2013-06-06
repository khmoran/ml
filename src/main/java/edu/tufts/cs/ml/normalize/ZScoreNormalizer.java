package edu.tufts.cs.ml.normalize;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tufts.cs.ml.DoubleFeature;
import edu.tufts.cs.ml.Feature;
import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.Relation;
import edu.tufts.cs.ml.util.MathUtil;
import edu.tufts.cs.ml.util.Util;

public class ZScoreNormalizer<E> extends Normalizer<E> {
  /** The map from the feature name to the mean. */
  protected Map<String, Double> meanMap = new HashMap<String, Double>();
  /** The map from the feature name to the sample standard deviation. */
  protected Map<String, Double> sampleSdMap = new HashMap<String, Double>();

  /**
   * Default constructor.
   * @param train
   */
  public ZScoreNormalizer( Relation<? extends FeatureVector<E>> train ) {
    super( train );
    calculateFeatureMeans();
    calculateFeatureSampleStandardDeviations();
    normalize( train );
  }

  /**
   * Get the feature means.
   * @return
   */
  public Map<String, Double> getFeatureMeans() {
    return meanMap;
  }

  /**
   * Get the feature standard deviations.
   * @return
   */
  public Map<String, Double> getFeatureStdDevs() {
    return sampleSdMap;
  }

  /**
   * Calculate the mean for each feature.
   */
  protected void calculateFeatureMeans() {
    for ( String featureName : data.getMetadata().keySet() ) {
      List<Double> dVals = Util.featuresToDoubles( featureName, data );
      meanMap.put( featureName, MathUtil.calcMean( dVals ) );
    }
  }

  /**
   * Calculate the sample standard deviation for each feature.
   */
  protected void calculateFeatureSampleStandardDeviations() {
    for ( String featureName : data.getMetadata().keySet() ) {
      List<Double> dVals = Util.featuresToDoubles( featureName, data );
      sampleSdMap.put( featureName, MathUtil
          .calcStandardDeviation( dVals ) );
    }
  }

  @Override
  public void normalize( Relation<? extends FeatureVector<E>> test ) {
    for ( FeatureVector<E> fv : test ) {
      for ( Feature<?> f : fv.values() ) {
        if ( f instanceof DoubleFeature ) {
          double origValue = Double.parseDouble( f.getValue().toString() );
          double featureMean = meanMap.get( f.getName() );
          double featureSampleSd = sampleSdMap.get( f.getName() );
          double zscore = ( origValue - featureMean )/featureSampleSd;
          ( (DoubleFeature) f ).setValue( Util.round( zscore, 2 ) );
        } else {
          // TODO handle this
        }
      }
    }
  }

}
