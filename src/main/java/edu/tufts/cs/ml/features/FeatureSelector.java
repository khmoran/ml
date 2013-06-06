package edu.tufts.cs.ml.features;

import java.util.Map.Entry;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;

public abstract class FeatureSelector<E> extends Observable {
  /** The Logger. */
  protected static final Logger LOG =  Logger.getLogger(
      FeatureSelector.class.getName() );
  /** The training data. */
  protected TrainRelation<E> trainingData;
  /** The default k values with which to test the LOOCV accuracy. */
  protected static final int[] DEFAULT_K_VALS = {7};
  /** The provided k values with which to test the LOOCV accuracy. */
  protected int[] kVals = DEFAULT_K_VALS;

  /**
   * Default constructor.
   * @param train
   */
  public FeatureSelector( TrainRelation<E> train ) {
    this.trainingData = train;
  }

  /**
   * Constructor that accepts k values for LOOCV validation.
   * @param train
   */
  public FeatureSelector( TrainRelation<E> train, int[] kVals ) {
    this( train );
    this.kVals = kVals;
  }

  /**
   * Set the k values with which to test the LOOCV accuracy.
   * @param kVals
   */
  public void setKVals( int[] kVals ) {
    this.kVals = kVals;
  }

  /**
   * Select m features.
   * @param m The number of features to select.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public abstract Entry<Double, TrainRelation<E>> selectFeatures( int m )
    throws IncomparableFeatureVectorException;

  /**
   * Select the (selector-determined) optimal number of features.
   * @return
   * @throws IncomparableFeatureVectorException
   */
  public abstract Entry<Double, TrainRelation<E>> selectFeatures()
    throws IncomparableFeatureVectorException;

  /**
   * Log the given message and notify observers with the update.
   * @param logMsg
   */
  protected void log( Level level, String logMsg ) {
    LOG.log( level, logMsg );
    this.setChanged();
    notifyObservers( logMsg );
  }
}
