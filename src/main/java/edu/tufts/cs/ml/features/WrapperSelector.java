package edu.tufts.cs.ml.features;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.Util;
import edu.tufts.cs.ml.validate.KnnLOOCValidator;

public class WrapperSelector<E> extends FeatureSelector<E> {

  /**
   * Default constructor.
   *
   * @param train
   */
  public WrapperSelector( TrainRelation<E> train ) {
    super( train );
  }

  /**
   * Constructor that takes k values for LOOCV validation.
   *
   * @param train
   */
  public WrapperSelector( TrainRelation<E> train, int[] kVals ) {
    super( train, kVals );
  }

  @Override
  public Entry<Double, TrainRelation<E>> selectFeatures( int m )
    throws IncomparableFeatureVectorException {
    Entry<Double, TrainRelation<E>> prevResult = null;
    Entry<Double, TrainRelation<E>> currResult =
        new SimpleEntry<Double, TrainRelation<E>>( 0.0,
            new TrainRelation<E>( trainingData.getName(), new Metadata() ) );
    while ( ( prevResult == null
              || currResult.getKey() > prevResult.getKey() )
             && currResult.getValue().getMetadata().size() <= m ) {
      prevResult = currResult;
      currResult = addNextFeature( currResult.getValue() );
    }

    log( Level.CONFIG, "\nFinal parameter list:\n" +
         prevResult.getValue().getMetadata() + "\nFinal accuracy: " +
         prevResult.getKey() );

    return prevResult;
  }

  /**
   * Add the given feature to the Relation.
   * @param feature
   * @return
   */
  @SuppressWarnings( "unchecked" )
  protected TrainRelation<E> addFeature( final String feature,
      final TrainRelation<E> wrapped ) {
    TrainRelation<E> filtered = new TrainRelation<E>( wrapped.getName(),
        (Metadata) wrapped.getMetadata().clone() );
    for ( LabeledFeatureVector<E> fv : wrapped ) {
      filtered.add( (LabeledFeatureVector<E>) fv.clone() );
    }

    // keep the metadata updated
    filtered.getMetadata().put(
        feature, trainingData.getMetadata().get( feature ) );

    for ( int i = 0; i < trainingData.size(); i++ ) {
      LabeledFeatureVector<E> vf = trainingData.get( i );
      LabeledFeatureVector<E> filteredFv;

      if ( i >= wrapped.size() ) {
        filteredFv = new LabeledFeatureVector<E>(
          vf.getLabel(), vf.getId() );
        filtered.add( filteredFv );
      } else {
        filteredFv = filtered.get( i );
      }

      for ( String f : vf.keySet() ) {
        if ( f.equals( feature ) ) {
          filteredFv.put( f, vf.get( f ) );
        }
      }
    }

    return filtered;
  }

  /**
   * Greedily add the next feature.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected TreeMap<Double, String> calcLOOCVvalues(
      final TrainRelation<E> currRelation )
    throws IncomparableFeatureVectorException {

    TreeMap<Double, String> results = new TreeMap<Double, String>();
    for ( String feature : trainingData.getMetadata().keySet() ) {
      if ( !currRelation.getMetadata().containsKey( feature ) // no duplicates
        && !feature.equals( LabeledFeatureVector.CLASS_MARKER ) ) {
        TrainRelation<E> withNewFeature = addFeature( feature, currRelation );

        KnnLOOCValidator<E> v = new KnnLOOCValidator<E>( withNewFeature );
        double[] accuracies = v.validate( kVals );
        double avgAccuracy = 0;
        for ( double a : accuracies ) {
          avgAccuracy += a;
        }
        avgAccuracy = Util.round( avgAccuracy/accuracies.length, 6 );
        results.put( avgAccuracy, feature );
        log( Level.CONFIG, "\n\tAdding feature " + feature +
             " \tproduces accuracy " + avgAccuracy );
      }
    }

    return results;
  }

  /**
   * Greedily add the next feature.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected Entry<Double, TrainRelation<E>> addNextFeature(
      final TrainRelation<E> currResult )
    throws IncomparableFeatureVectorException {

    Entry<Double, TrainRelation<E>> bestResult =
        new SimpleEntry<Double, TrainRelation<E>>( 0.0, null );
    for ( String feature : trainingData.getMetadata().keySet() ) {
      if ( !currResult.getMetadata().containsKey( feature ) // no duplicates
        && !feature.equals( LabeledFeatureVector.CLASS_MARKER ) ) {
        TrainRelation<E> withNewFeature = addFeature( feature, currResult );

        KnnLOOCValidator<E> v = new KnnLOOCValidator<E>( withNewFeature );
        double[] accuracies = v.validate( kVals );
        double avgAccuracy = 0;
        for ( double a : accuracies ) {
          avgAccuracy += a;
        }
        avgAccuracy = Util.round( avgAccuracy/accuracies.length, 6 );
        if ( avgAccuracy > bestResult.getKey() ) {
          bestResult = new SimpleEntry<Double, TrainRelation<E>>(
              avgAccuracy, withNewFeature );
        }

        log( Level.CONFIG, "\n\tAdding feature " + feature +
             " \tproduces accuracy " + avgAccuracy );
      }
    }

    log( Level.CONFIG, "\nIteration " + (
        bestResult.getValue().getMetadata().size() ) +
         ":\n" + bestResult.getValue().getMetadata() +
         "\t" + bestResult.getKey() );
    return bestResult;
  }

  @Override
  public Entry<Double, TrainRelation<E>> selectFeatures()
    throws IncomparableFeatureVectorException {
    return selectFeatures( trainingData.getMetadata().size() - 1 );
  }

}
