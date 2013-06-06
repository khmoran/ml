package edu.tufts.cs.ml.features;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;

import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.MathUtil;
import edu.tufts.cs.ml.util.Util;
import edu.tufts.cs.ml.validate.KnnLOOCValidator;

public class ComboSelector<E> extends FeatureSelector<E> {

  /**
   * Default constructor.
   *
   * @param train
   */
  public ComboSelector( TrainRelation<E> train ) {
    super( train );
  }

  /**
   * Constructor that takes k values for LOOCV validation.
   *
   * @param train
   */
  public ComboSelector( TrainRelation<E> train, int[] kVals ) {
    super( train, kVals );
  }

  @Override
  public Entry<Double, TrainRelation<E>> selectFeatures( int m )
    throws IncomparableFeatureVectorException {
    return null;
  }

  /**
   * Remove these features from the provided relation.
   * @param feature
   * @param relation
   * @return
   */
  protected TrainRelation<E> removeFeature( String feature,
      TrainRelation<E> relation ) {

    for ( LabeledFeatureVector<E> v : relation ) {
      v.remove( feature );
    }

    relation.getMetadata().remove( feature );

    return relation;
  }

  @SuppressWarnings( "unchecked" )
  @Override
  public Entry<Double, TrainRelation<E>> selectFeatures()
    throws IncomparableFeatureVectorException {
    TrainRelation<E> stillUnused = new TrainRelation<E>( trainingData.getName(),
        (Metadata) trainingData.getMetadata().clone() );
    for ( LabeledFeatureVector<E> fv : trainingData ) {
      stillUnused.add( (LabeledFeatureVector<E>) fv.clone() );
    }
    TrainRelation<E> combo = new TrainRelation<E>(
      trainingData.getName(), new Metadata() );

    Entry<Double, TrainRelation<E>> best =
        new SimpleEntry<Double, TrainRelation<E>>( 0.0, combo );
    while ( combo.getMetadata().size() < trainingData.getMetadata().size()-1 ) {
      FilterSelector<E> fs = new FilterSelector<E>( stillUnused );
      WrapperSelector<E> ws = new WrapperSelector<E>( stillUnused );
      TreeMap<Double, String> pccVals = fs.calcPCCvalues();
      TreeMap<Double, String> loocvVals = ws.calcLOOCVvalues( combo );

      /**
       * Calculate which makes the strongest case -- which is the highest
       * relative to the other values?
       */
      double normalizedPCC = MathUtil.calcZscore(
          pccVals.keySet(), pccVals.lastKey() );

      double normalizedLOOCV = MathUtil.calcZscore( loocvVals.keySet(),
          loocvVals.lastKey() );

      log( Level.CONFIG, "\tBest PCC: " + pccVals.lastKey() +
        "\t Normalized: " + normalizedPCC +
        "\tFeature: " + pccVals.lastEntry().getValue() + "\n" );

      log( Level.CONFIG, "\tBest LOOCV: " + loocvVals.lastKey() +
        "\t Normalized: " + normalizedLOOCV +
        "\tFeature: " + loocvVals.lastEntry().getValue() + "\n" );

      String newFeature = loocvVals.lastEntry().getValue();
      double newAccuracy = loocvVals.lastKey();
      if ( normalizedPCC > normalizedLOOCV ) {
        newFeature = pccVals.lastEntry().getValue();
      }

      combo = ws.addFeature( newFeature, combo );
      removeFeature( newFeature, stillUnused );

      if ( normalizedPCC > normalizedLOOCV ) {
        // if we go with the PCC, we have to calculate the current accuracy
        KnnLOOCValidator<E> v = new KnnLOOCValidator<E>( combo );
        double[] accuracies = v.validate( kVals );
        newAccuracy = 0;
        for ( double a : accuracies ) {
          newAccuracy += a;
        }
        newAccuracy = Util.round( newAccuracy/accuracies.length, 6 );
      }

      if ( newAccuracy > best.getKey() ) {
        TrainRelation<E> deepCopy = new TrainRelation<E>( combo.getName(),
            (Metadata) combo.getMetadata().clone() );
        for ( LabeledFeatureVector<E> fv : combo ) {
          deepCopy.add( (LabeledFeatureVector<E>) fv.clone() );
        }

        best = new SimpleEntry<Double, TrainRelation<E>>(
            newAccuracy, deepCopy );
      }

      log( Level.CONFIG, "Iteration " + combo.getMetadata().size() +
           ": add " + newFeature + "\t accuracy: " + newAccuracy + "\n\n" );
    }


    log( Level.CONFIG, "Final features:\n " + best.getValue().getMetadata() +
         "\n\n Final accuracy: " + best.getKey() );
    return best;
  }

}
