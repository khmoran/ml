package edu.tufts.cs.ml.features;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.MathUtil;
import edu.tufts.cs.ml.util.Util;
import edu.tufts.cs.ml.validate.KnnLOOCValidator;

public class FilterSelector<E> extends FeatureSelector<E> {

  /**
   * Default constructor.
   *
   * @param train
   */
  public FilterSelector( TrainRelation<E> train ) {
    super( train );
  }

  /**
   * Constructor that takes k values for LOOCV validation.
   *
   * @param train
   */
  public FilterSelector( TrainRelation<E> train, int[] kVals ) {
    super( train, kVals );
  }

  /**
   * Calculate the PCC value for each feature.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected TreeMap<Double, String> calcPCCvalues()
    throws IncomparableFeatureVectorException {
    TreeMap<Double, String> pccMap = new TreeMap<Double, String>();
    List<Double> y = Util.labelsToDoubles( trainingData );
    for ( String feature : trainingData.getMetadata().keySet() ) {
      if ( !feature.equals( LabeledFeatureVector.CLASS_MARKER ) ) {
        List<Double> x = Util.featuresToDoubles( feature, trainingData );
        double pcc = Util.round( MathUtil.calcPCC( x, y ), 6 );
        pccMap.put( Math.abs( pcc ), feature ); // we want |r|, not r
      }
    }

    return pccMap;
  }

  /**
   * Filter the features based on PCC.
   *
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public Entry<Double, TrainRelation<E>> selectFeatures( int m )
    throws IncomparableFeatureVectorException {

    TreeMap<Double, String> pccMap = calcPCCvalues();

    if ( m > pccMap.size() ) m = pccMap.size();

    // get the features with the top m |r| values
    Set<String> keepFeatures = new HashSet<String>();
    Double avgM = 0.0;
    for ( int i = 0; i < m; i++ ) {
      Entry<Double, String> max = pccMap.lastEntry();
      keepFeatures.add( max.getValue() );
      avgM += max.getKey();
      pccMap.remove( max.getKey() );
      LOG.log( Level.CONFIG, max.getValue() + "\tPCC: " + max.getKey() );
    }
    avgM = avgM/m;

    TrainRelation<E> filtered = new TrainRelation<E>( trainingData.getName(),
        (Metadata) trainingData.getMetadata().clone() );

    // filter out the features that didn't make the top m
    for ( LabeledFeatureVector<E> vf : trainingData ) {
      LabeledFeatureVector<E> filteredFv = new LabeledFeatureVector<E>(
          vf.getLabel(), vf.getId() );

      for ( String f : vf.keySet() ) {
        if ( keepFeatures.contains( f ) ) {
          filteredFv.put( f, vf.get( f ) );
        }
      }
      filtered.add( filteredFv );
    }

    List<String> toRemove = new ArrayList<String>();
    for ( String s : filtered.getMetadata().keySet() ) {
      if ( !keepFeatures.contains( s ) ) {
        toRemove.add( s );
      }
    }
    for ( String s : toRemove ) {
      filtered.getMetadata().remove( s );
    }

    return new SimpleEntry<Double, TrainRelation<E>>( avgM, filtered );
  }

  @Override
  public Entry<Double, TrainRelation<E>> selectFeatures()
    throws IncomparableFeatureVectorException {
    TreeMap<Double, TrainRelation<E>> map =
        new TreeMap<Double, TrainRelation<E>>();
    for ( int i = 1; i < trainingData.getMetadata().size()-1; i++ ) {
      TrainRelation<E> filtered = this.selectFeatures( i ).getValue();

      KnnLOOCValidator<E> v = new KnnLOOCValidator<E>( filtered );
      try {
        double accuracy = v.validate( kVals )[0];
        log( Level.CONFIG, "m = " + i + "\taccuracy = " + accuracy + "\n" );
        map.put( accuracy, filtered );
      } catch ( IncomparableFeatureVectorException e ) {
        LOG.log( Level.SEVERE, e.getMessage() );
      }
    }

    return map.lastEntry();
  }
}
