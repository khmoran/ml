package edu.tufts.cs.ml.preprocess;

import java.util.HashMap;
import java.util.Map;

import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.PreprocessException;

public class LabelsToBinary<E> {

  /**
   * Private constructor for utility class.
   */
  private LabelsToBinary() {
    // purposely not instantiable
  }

  /**
   * Convert the labels to binary.
   * @param train
   * @return
   * @throws PreprocessException
   */
  public static <E> TrainRelation<Integer> preprocess( TrainRelation<E> train )
    throws PreprocessException {
    TrainRelation<Integer> converted = new TrainRelation<Integer>(
        train.getName(), train.getMetadata() );

    Map<E, Integer> binaryMap = new HashMap<E, Integer>();
    for ( LabeledFeatureVector<E> v : train ) {
      if ( !binaryMap.containsKey( v.getLabel() ) && binaryMap.size() > 2 ) {
        throw new PreprocessException(
            PreprocessException.LABELS_TO_BINARY_MSG );
      } else { // this is still valid
        if ( !binaryMap.containsKey( v.getLabel() ) && binaryMap.isEmpty() ) {
          binaryMap.put( v.getLabel(), 0 );
        } else if ( !binaryMap.containsKey( v.getLabel() )
            && binaryMap.size() == 1 ) {
          binaryMap.put( v.getLabel(), 1 );
        }
        LabeledFeatureVector<Integer> binV = new LabeledFeatureVector<Integer>(
            binaryMap.get( v.getLabel() ), v.getId() );
        binV.putAll( v );
        converted.add( binV );
      }
    }

    return converted;
  }
}
