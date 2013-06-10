package edu.tufts.cs.ml.classify;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;

public class KnnClassifier<E> implements Classifier<E> {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      KnnClassifier.class.getName() );
  /** The default k values to use if none are specified. */
  protected static final int[] DEFAULT_K_VALS = { 1, 3, 5, 7, 9, 11, 13, 15 };
  /** The k-values to use. */
  protected int[] kVals = DEFAULT_K_VALS;
  /** The training data. */
  protected TrainRelation<E> trainingData;

  /**
   * Default constructor.
   * @param train
   */
  public KnnClassifier() {

  }

  /**
   * Constructor that accepts k-values.
   * @param kVals
   */
  public KnnClassifier( int[] kVals ) {
    this.kVals = kVals;
  }

  /**
   * Break a tie using the total cumulative distances between the points
   * and the contending labels.
   * @param ties
   * @param nearestNeighbors
   * @return
   */
  protected E breakTie( Set<E> ties, TreeMap<Double, List<E>>
    nearestNeighbors ) {
    TreeMap<Double, E> cumulativeDistances = new TreeMap<Double, E>();
    for ( E label : ties ) {
      double cumulativeDistance = 0;
      for ( Double dist : nearestNeighbors.keySet() ) {
        if ( nearestNeighbors.get( dist ).contains( label ) ) {
          cumulativeDistance += dist;
        }
      }
      cumulativeDistances.put( cumulativeDistance, label );
    }

    return cumulativeDistances.firstEntry().getValue();
  }

  /**
   * Calculate the distances between the training set and the test vector.
   * @param test
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected TreeMap<Double, List<E>> calculateDistances( FeatureVector<E> test )
    throws IncomparableFeatureVectorException {
    TreeMap<Double, List<E>> distances = new TreeMap<Double, List<E>>();
    for ( LabeledFeatureVector<E> v : trainingData ) {
      Double eucDist = v.getEuclideanDistance( test );
      if ( distances.containsKey( eucDist ) ) {
        List<E> eList = distances.get( eucDist );
        eList.add( v.getLabel() );
      } else {
        List<E> eList = new ArrayList<E>();
        eList.add( v.getLabel() );
        distances.put( eucDist, eList );
      }
    }

    return distances;
  }

  /**
   * Classify a set of test data.
   */
  public void classify( TestRelation<E> testRelation )
    throws IncomparableFeatureVectorException {
    classify( testRelation, kVals );
  }

  /**
   * Classify a set of testing data.
   * @param testing
   * @param k
   * @throws IncomparableFeatureVectorException
   */
  public void classify( TestRelation<E> testing, int[] kVals )
    throws IncomparableFeatureVectorException {
    for ( UnlabeledFeatureVector<E> fv : testing ) {
      classify( fv, kVals );
    }
  }

  /**
   * Classify the point based on its nearest neighbors.
   * @param nearestNeighbors
   * @return
   */
  protected Entry<E, Double> classify(
      TreeMap<Double, List<E>> nearestNeighbors ) {
    TreeMap<E, Integer> counts = new TreeMap<E, Integer>();
    for ( List<E> labelList : nearestNeighbors.values() ) {
      for ( E label : labelList ) {
        if ( counts.containsKey( label ) ) {
          int count = counts.get( label )+1;
          counts.put( label, count );
        } else {
          counts.put( label, 1 );
        }
      }
    }

    // TODO optimize this
    Set<E> ties = new HashSet<E>();
    Entry<E, Integer> max = counts.firstEntry();
    for ( Entry<E, Integer> entry : counts.entrySet() ) {
      if ( entry.getValue() > max.getValue() ) {
        max = entry;
        ties.clear();
      } else if ( entry.getValue() == max.getValue() && !entry.equals( max ) ) {
        ties.add( entry.getKey() );
        ties.add( max.getKey() );
      }
    }

    double certainty = max.getValue()/nearestNeighbors.size();
    if ( ties.size() == 0  ) {
      return new SimpleEntry<E, Double>( max.getKey(), certainty );
    } else {
      LOG.log( Level.CONFIG, "Breaking tie between " + ties + " of " + counts );
      return new SimpleEntry<E, Double>(
          breakTie( ties, nearestNeighbors ), certainty );
    }
  }

  /**
   * Classify an instance.
   */
  public void classify( UnlabeledFeatureVector<E> testInstance )
    throws IncomparableFeatureVectorException {
    classify( testInstance, kVals );
  }

  /**
   * Classify the given piece of test data with the provided k-value.
   * @param test
   *          The test (non-training) data to be classified.
   * @param kVals
   *          The numbers of nearest neighbors to consider.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public void classify( UnlabeledFeatureVector<E> test, int[] kVals )
    throws IncomparableFeatureVectorException {
    LOG.log( Level.CONFIG, "Classifying " + test.getId() );

    /*
     * Calculate the distances between the test vector and all training vectors.
     */
    TreeMap<Double, List<E>> distances = calculateDistances( test );

    for ( int k : kVals ) {
      /*
       * Get the k-nearest feature vectors.
       */
      TreeMap<Double, List<E>> nearestNeighbors = getNearestNeighbors(
          distances, k );

      /*
       * Figure out which is the most popular classification in the nearest k.
       */
      E classification = classify( nearestNeighbors ).getKey();

      /*
       * Add the classification to the vector.
       */
      test.addClassification( k, classification );
    }

    // TODO take the majority vote
    test.setClassification( test.getClassification( 3 ) );
  }

  /**
   * Get the k nearest neighbors.
   * @param distances
   * @param k
   * @return
   */
  protected TreeMap<Double, List<E>> getNearestNeighbors(
      TreeMap<Double, List<E>> distances, int k ) {
    TreeMap<Double, List<E>> nearestNeighbors = new TreeMap<Double, List<E>>();

    int i = 0;
    for ( Double key : distances.keySet() ) {
      if ( i >= k ) {
        break;
      }
      for ( E label : distances.get( key ) ) {
        if ( nearestNeighbors.containsKey( key ) ) {
          List<E> eList = nearestNeighbors.get( key );
          eList.add( label );
        } else {
          List<E> eList = new ArrayList<E>();
          eList.add( label );
          nearestNeighbors.put( key, eList );
        }
        i++;
      }
    }

    return nearestNeighbors;
  }

  public double getCertainty( UnlabeledFeatureVector<E> test )
    throws IncomparableFeatureVectorException {
    return getCertainty( test, kVals[0] );
  }

  /**
   * Classify the given piece of test data with the provided k-value.
   * @param test
   *          The test (non-training) data to be classified.
   * @param k
   *          The number of nearest neighbors to consider.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public double getCertainty( UnlabeledFeatureVector<E> test, int k )
    throws IncomparableFeatureVectorException {
    LOG.log( Level.CONFIG, "Classifying " + test.getId() );

    /*
     * Calculate the distances between the test vector and all training vectors.
     */
    TreeMap<Double, List<E>> distances = calculateDistances( test );

    /*
     * Get the k-nearest feature vectors.
     */
    TreeMap<Double, List<E>> nearestNeighbors = getNearestNeighbors(
        distances, k );

    /*
     * Figure out which is the most popular classification in the nearest k,
     * and get its uncertainty.
     */
    return classify( nearestNeighbors ).getValue();
  }

  /**
   * Train the classifier.
   */
  public void train( TrainRelation<E> trainRelation ) {
    this.trainingData = trainRelation;
  }

}
