package edu.tufts.cs.ml.learning.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import edu.tufts.cs.ml.FeatureVector;
import edu.tufts.cs.ml.LabeledFeatureVector;
import edu.tufts.cs.ml.Metadata;
import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.UnlabeledFeatureVector;
import edu.tufts.cs.ml.classify.KnnClassifier;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.util.MathUtil;

public abstract class Sampler<E> extends Observable {
  /** The training data. */
  protected TrainRelation<E> origData;

  /**
   * Default constructor.
   * @param train
   */
  public Sampler( TrainRelation<E> train ) {
    this.origData = train;
  }

  /**
   * Take numClasses/k instances out of the training data and treat it as
   * unlabeled data.
   * @param labeledSet
   * @return
   */
  public TestRelation<E> createUnlabeledSet( TrainRelation<E> remaining ) {
    TestRelation<E> unlabeledSet = new TestRelation<E>(
        "Unlabeled", (Metadata) origData.getMetadata().clone() );
    unlabeledSet.getMetadata().remove( LabeledFeatureVector.CLASS_MARKER  );

    // choose v random instances and move them into the validation set
    for ( FeatureVector<E> fv : remaining ) {
      UnlabeledFeatureVector<E> ufv = new UnlabeledFeatureVector<E>(
          fv.getId() );
      ufv.putAll( fv );
      unlabeledSet.add( ufv );
    }

    return unlabeledSet;
  }

  /**
   * Take numClasses/k instances out of the training data and treat it as
   * unlabeled data.
   * @return
   */
  public TrainRelation<E> createLabeledSet( TrainRelation<E> s, int k ) {
    TrainRelation<E> labeledSet = new TrainRelation<E>(
        origData.getName(), (Metadata) origData.getMetadata().clone() );

    int numClasses = getNumClasses();
    // the number of labeled instances to start with
    int labeledSizePerClass = (int) Math.ceil( (double) numClasses/(double) k );
    int totalInstances = labeledSizePerClass*numClasses;

    Map<E, Integer> seenClasses = new HashMap<E, Integer>();
    // get m random items from a supersized random set
    while ( labeledSet.size() < totalInstances ) {  // do until set is full
      Set<LabeledFeatureVector<E>> superset = MathUtil.randomSample(
          new ArrayList<LabeledFeatureVector<E>>(
              s ), totalInstances );

      for ( LabeledFeatureVector<E> lfv : superset ) {
        if ( labeledSet.size() == totalInstances ) {
          break;
        }
        E label = lfv.getLabel();
        if ( !seenClasses.containsKey( label ) ) {
          labeledSet.add( lfv );
          s.remove( lfv );
          seenClasses.put( label, 1 );
        } else {
          int numInstancesOfClass = seenClasses.get( label );
          if ( numInstancesOfClass < labeledSizePerClass ) {
            labeledSet.add( lfv );
            s.remove( lfv );
            seenClasses.put( label, ++numInstancesOfClass );
          }
        }
      }
    }

    return labeledSet;
  }

  /**
   * Take v instances out of the training data and put it in the validation
   * set.
   * @return
   */
  public TrainRelation<E> createValidationSet( TrainRelation<E> s, int v ) {
    TrainRelation<E> validationSet = new TrainRelation<E>(
        origData.getName(), (Metadata) origData.getMetadata().clone() );

    // get m random items from a supersized random set
    while ( validationSet.size() < v ) {  // do it until the set is full
      Set<LabeledFeatureVector<E>> superset = MathUtil.randomSample(
          new ArrayList<LabeledFeatureVector<E>>( s ), v );

      for ( LabeledFeatureVector<E> lfv : superset ) {
        if ( validationSet.size() == v ) {
          break;
        }
        if ( !validationSet.contains( lfv ) ) { // no repeats
          validationSet.add( lfv );
          s.remove( lfv ); // take it out so no dupes in other sets
        }
      }
    }

    return validationSet;
  }

  /**
   * Calculate the accuracy of the predictions of the active learner.
   * @param labeled
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected double evaluate( TrainRelation<E> validation,
      TrainRelation<E> labeled, int k )
    throws IncomparableFeatureVectorException {
    KnnClassifier<E> knn = new KnnClassifier<E>();
    knn.train( labeled );

    int correct = 0;
    for ( LabeledFeatureVector<E> fv : validation ) {
      UnlabeledFeatureVector<E> testInstance = new UnlabeledFeatureVector<E>(
          labeled.getName() );
      testInstance.putAll( fv );

      int[] ks = {k};
      knn.classify( testInstance, ks );
      E label = testInstance.getClassification( k );
      if ( label.equals( fv.getLabel() ) ) {
        correct++;
      }
    }

    return (double) correct/(double) validation.size();
  }

  /**
   * Select the next m instances to learn.
   * @param labeled
   * @param unlabeled
   * @param m
   * @return
   * @throws IncomparableFeatureVectorException
   */
  protected abstract Set<UnlabeledFeatureVector<E>> getNextLearningSet(
      TrainRelation<E> labeled, TestRelation<E> unlabeled, int k, int m )
    throws IncomparableFeatureVectorException;

  /**
   * Get the number of classes.
   * @return
   */
  protected Integer getNumClasses() {
    for ( String feature : origData.getMetadata().keySet() ) {
      if ( feature.equalsIgnoreCase( LabeledFeatureVector.CLASS_MARKER ) ) {
        String classVals = origData.getMetadata().get(
            feature ).toString();
        String[] classes = classVals.split( "," );
        return classes.length;
      }
    }

    return null;
  }

  /**
   * Learn the labels of the unlabeled feature vectors in the learning set.
   * @param learningSet
   * @param labeled
   * @param unlabeled
   */
  protected void learn( Set<UnlabeledFeatureVector<E>> learningSet,
      TrainRelation<E> labeled, TestRelation<E> unlabeled ) {
    for ( UnlabeledFeatureVector<E> fv : learningSet ) {
      for ( LabeledFeatureVector<E> ofv : origData ) {
        if ( fv.getId() == ofv.getId() ) {
          labeled.add( ofv );
        }
      }
    }

    unlabeled.removeAll( learningSet );
  }

  /**
   * Run a single iteration of the sampler.
   * @param labeled
   * @param unlabeled
   * @param k
   * @param m
   * @throws IncomparableFeatureVectorException
   */
  protected double runIteration( TrainRelation<E> validation,
      TrainRelation<E> labeled, TestRelation<E> unlabeled, int k, int m )
    throws IncomparableFeatureVectorException {
    Set<UnlabeledFeatureVector<E>> learningSet = getNextLearningSet(
        labeled, unlabeled, k, m );
    learn( learningSet, labeled, unlabeled );
    return evaluate( validation, labeled, k );
  }

  /**
   * Learn on the train relations using an uncertainty sampling technique.
   * @param test
   * @param k The number of "nearest neighbors" to look at
   * @param v The size of the validation set
   * @param m The batch size for label acquisition
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  public double sample( int k, int v, int m )
    throws IncomparableFeatureVectorException {
    // report parameters
    setChanged();
    notifyObservers( "dataset: " + origData.getName() + "\n|V|: " + v +
        "\nsigma: " + origData.getSigma() + "\nm: " + m + "\nk: " + k + "\n" );

    TrainRelation<E> s = (TrainRelation<E>) origData.clone();

    // create partitions based on parameters
    TrainRelation<E> validationSet = createValidationSet( s, v );
    TrainRelation<E> labeledSet = createLabeledSet( s, k );
    labeledSet.setSigma( origData.getSigma() );
    TestRelation<E> unlabeledSet = createUnlabeledSet( s );
    setChanged();
    notifyObservers( "Total size: " + origData.size() + "\n" +
                     "Validation set size: " + validationSet.size() + "\n" +
                     "Labeled set size: " + labeledSet.size() + "\n" +
                     "Unlabeled set size: " + unlabeledSet.size() + "\n" );

    // output the initial accuracy
    int i = 0;
    double accuracy = evaluate( validationSet, labeledSet, k );
    setChanged();
    notifyObservers( "Accuracy on iteration " + ++i + ": " + accuracy );

    // run iterations and report accuracy at each step
    while ( unlabeledSet.size() > 0 ) {
      accuracy = runIteration( validationSet, labeledSet, unlabeledSet, k, m );
      setChanged();
      notifyObservers( "Accuracy on iteration " + ++i + ": " + accuracy );
    }

    return accuracy;
  }

  /**
   * Learn on the train relations using an uncertainty sampling technique.
   * @param test
   * @param k The number of "nearest neighbors" to look at
   * @param m The batch size for label acquisition
   * @throws IncomparableFeatureVectorException
   */
  public double sample( TrainRelation<E> validationSet,
      TrainRelation<E> labeledSet, TestRelation<E> unlabeledSet,
      int k, int v, int m )
    throws IncomparableFeatureVectorException {
    // report parameters and partitions
    setChanged();
    notifyObservers( "dataset: " + origData.getName() + "\n|V|: " + v +
        "\nsigma: " + origData.getSigma() + "\nm: " + m + "\nk: " + k + "\n\n" +
        "Total size: " + origData.size() + "\n" +
        "Validation set size: " + validationSet.size() + "\n" +
        "Labeled set size: " + labeledSet.size() + "\n" +
        "Unlabeled set size: " + unlabeledSet.size() + "\n" );

    // output the initial accuracy
    double accuracy = evaluate( validationSet, labeledSet, k );
    setChanged();
    notifyObservers( accuracy );

    // run iterations and report accuracy at each step
    while ( unlabeledSet.size() > 0 ) {
      accuracy = runIteration( validationSet, labeledSet, unlabeledSet, k, m );
      setChanged();
      notifyObservers( accuracy );
    }

    return accuracy;
  }
}
