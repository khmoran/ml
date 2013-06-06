package edu.tufts.cs.ml.learning.active.drivers;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.exception.PreprocessException;
import edu.tufts.cs.ml.learning.active.RandomSampler;
import edu.tufts.cs.ml.learning.active.Sampler;
import edu.tufts.cs.ml.learning.active.UncertaintySampler;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.writer.ResultWriter;

/**
 * Use active learning to classify the provided examples given a training set.
 *
 * @author Kelly Moran
 *
 */
public class ActiveLearn implements Observer {
  /** The Logger. */
  protected static final Logger LOG = Logger.getLogger( ActiveLearn.class
      .getName() );
  /** The singleton instance. */
  protected static ActiveLearn instance;
  /** Output from the random method. */
  protected static StringBuilder randomOutput = new StringBuilder();
  /** Output from the uncertainty method. */
  protected static StringBuilder uncertaintyOutput = new StringBuilder();
  /** Input types. */
  protected enum InputType { SOYBEAN, TWSP, VOWEL };

  /**
   * Private constructor for utility class.
   */
  protected ActiveLearn() {
    // purposely not instantiable
  }

  /**
   * Print the configuration to the console.
   *
   * @param cmd
   */
  protected static void printInfo( ActiveLearnArguments cmd ) {
    LOG.log( Level.INFO, "Running select features with: "
        + "\n\tTraining file:\t" + cmd.getTrainingFile()
        + "\n\tOutput of the random sapling method:\t" + cmd.getOutputRandom()
        + "\n\tOutput of the uncertainty sampling method:\t" +
        cmd.getOutputUncertainty() );
  }

  /**
   * @param args
   * @throws CommandLineArgumentException
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   * @throws PreprocessException
   */
  @SuppressWarnings( "unchecked" )
  public static void main( String[] args ) throws CommandLineArgumentException,
      IOException, IncomparableFeatureVectorException, PreprocessException {

    /////////////
    //  SETUP  //
    /////////////
    instance = new ActiveLearn();
    int k = 5;
    ActiveLearnArguments cmd = new ActiveLearnArguments( args );
    printInfo( cmd );


    /*
     * Initialize the training data.
     */
    File trainFile = cmd.getTrainingFile();
    Reader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader
        .read( trainFile );

    // Get the correct parameters for the given input type
    InputType it = InputType.valueOf( train.getName().toUpperCase() );

    int v;
    double sigma;
    int m;
    switch ( it ) {
      case SOYBEAN:
        v = 40;
        sigma = 0.45;
        m = 5;
        break;
      case VOWEL:
        v = 100;
        sigma = 0.5;
        m = 5;
        break;
      case TWSP:
        v = 40;
        sigma = 1.25;
        m = 5;
        break;
      default:
        throw new RuntimeException( "Unknown dataset: " + train.getName() +
            "; could not set parameters |V|, sigma, and m." );
    }

    train.setSigma( sigma );

    performIteration( train, k, v, m );

    /*
     * Write the random results to a file
     */
    File outFile = cmd.getOutputRandom();
    ResultWriter rw = new ResultWriter();
    rw.write( randomOutput.toString(), outFile );

    /*
     * Write the uncertainty results to a file
     */
    outFile = cmd.getOutputUncertainty();
    rw.write( uncertaintyOutput.toString(), outFile );

    LOG.log( Level.INFO, "Done!" );
  }

  /**
   * Perform an iteration of active learning.
   * @param train
   * @param k
   * @param v
   * @param m
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  protected static void performIteration( TrainRelation<String> train, int k,
      int v, int m ) throws IncomparableFeatureVectorException {
    TrainRelation<String> c = (TrainRelation<String>) train.clone();

    // Create the initial partitions for the random method
    Sampler<String> rand = new RandomSampler<String>( train );
    TrainRelation<String> validationRand = rand.createValidationSet( c, v );
    TrainRelation<String> labeledRand = rand.createLabeledSet( c, k );
    labeledRand.setSigma( train.getSigma() );
    TestRelation<String> unlabeledRand = rand.createUnlabeledSet( c );

    // Duplicate them for the uncertainty method
    Sampler<String> uncert = new UncertaintySampler<String>( train );
    TrainRelation<String> validationUncert =
        (TrainRelation<String>) validationRand.clone();
    TrainRelation<String> labeledUncert =
        (TrainRelation<String>) labeledRand.clone();
    TestRelation<String> unlabeledUncert =
        (TestRelation<String>) unlabeledRand.clone();

    ///////////////////
    // RANDOM METHOD //
    ///////////////////
    LOG.log( Level.INFO, "Running the random sampling learner..." );
    rand.addObserver( instance );
    double accuracy = rand.sample(
        validationRand, labeledRand, unlabeledRand, k, v, m );
    LOG.log( Level.INFO, "Random sampling accuracy: " + accuracy );

    ////////////////////////
    // UNCERTAINTY METHOD //
    ////////////////////////
    LOG.log( Level.INFO, "Running the uncertainty sampling learner..." );
    uncert.addObserver( instance );
    accuracy = uncert.sample(
        validationUncert, labeledUncert, unlabeledUncert, k, v, m );
    LOG.log( Level.INFO, "Uncertainty sampling accuracy: " + accuracy );
  }

  /**
   * Update the outputs.
   * @param source
   * @param msg
   */
  public void update( Observable source, Object msg ) {
    if ( source instanceof RandomSampler ) {
      randomOutput.append( msg.toString() + "\n" );
    } else if ( source instanceof UncertaintySampler ) {
      uncertaintyOutput.append( msg.toString() + "\n" );
    }
  }

}
