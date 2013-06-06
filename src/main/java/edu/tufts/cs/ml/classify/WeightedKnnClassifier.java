package edu.tufts.cs.ml.classify;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.tufts.cs.ml.util.BigMathUtil;

public class WeightedKnnClassifier<E> extends KnnClassifier<E> {
  /** A utility for BigDecimal calculations. */
  protected final BigMathUtil bmu = new BigMathUtil();

  /**
   * Calculate the magnitude of the vote of the neighbor, which is the
   * Gaussian weighting of its distance from the test point.
   * @param dist
   * @return
   */
  protected BigDecimal vote( Double dist ) {
    Double sigma = trainingData.getSigma();
    if ( sigma == null ) {
      throw new NullPointerException( "No sigma value in training dataset." );
    }

    BigDecimal num = new BigDecimal( dist*dist*-1 );
    BigDecimal denom = new BigDecimal( 2*sigma*sigma );
    BigDecimal div = num.divide( denom, RoundingMode.HALF_UP );
    BigDecimal vote = bmu.exp( div );
    vote = vote.setScale( 10, BigDecimal.ROUND_HALF_UP );
    return vote;
  }

  /**
   * Get the entry with the max value.
   * @return
   */
  protected Entry<E, BigDecimal> getMaxValue( TreeMap<E, BigDecimal> map ) {
    Entry<E, BigDecimal> max = map.firstEntry();
    for ( Entry<E, BigDecimal> entry : map.entrySet() ) {
      if ( entry.getValue().compareTo( max.getValue() ) == 1 ) {
        max = entry;
      }
    }

    return max;
  }

  @Override
  protected Entry<E, Double> classify( TreeMap<Double,
      List<E>> nearestNeighbors ) {
    TreeMap<E, BigDecimal> summedVotes = new TreeMap<E, BigDecimal>();
    for ( Double dist : nearestNeighbors.keySet() ) {
      List<E> labelList = nearestNeighbors.get( dist );
      BigDecimal vote = vote( dist );
      for ( E label : labelList ) {
        if ( summedVotes.containsKey( label ) ) {
          BigDecimal sum = summedVotes.get( label ).add( vote );
          summedVotes.put( label, sum );
        } else {
          summedVotes.put( label, vote );
        }
      }
    }

    Entry<E, BigDecimal> max = getMaxValue( summedVotes );
    BigDecimal certainty = max.getValue();

    if ( summedVotes.size() > 1 ) {
      // if there's a second place, the uncertainty is the difference between
      // the total vote for the winning label and the total vote for the
      // second place label
      summedVotes.remove( max.getKey() ); // so we can get the next-max
      Entry<E, BigDecimal> second = getMaxValue( summedVotes );
      certainty = certainty.subtract( second.getValue() );
    }

    certainty = certainty.setScale( 4, BigDecimal.ROUND_HALF_UP );
    if ( certainty.doubleValue() < 0.0 ) {
      throw new RuntimeException( "Certainty cannot be less than 0." );
    }

    Double dblVal = certainty.doubleValue();

    return new SimpleEntry<E, Double>( max.getKey(), dblVal );
  }


}
