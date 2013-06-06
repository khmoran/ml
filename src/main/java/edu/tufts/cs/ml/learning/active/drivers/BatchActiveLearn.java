package edu.tufts.cs.ml.learning.active.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.logging.Level;

import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.exception.PreprocessException;
import edu.tufts.cs.ml.learning.active.RandomSampler;
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
public class BatchActiveLearn extends ActiveLearn {
  /** Output from the random method. */
  protected static List<List<Double>> randomResults =
      new ArrayList<List<Double>>();
  /** Output from the random method. */
  protected static List<List<Double>> uncertResults =
      new ArrayList<List<Double>>();
  /** The current iteration. */
  protected static int iteration = 0;
  /** The number of iterations to run. */
  protected static final int NUM_ITERATIONS = 10;

  /**
   * Private constructor for utility class.
   */
  private BatchActiveLearn() {
    super();
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
    instance = new BatchActiveLearn();
    int k = 11;
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

    for ( iteration = 0; iteration < NUM_ITERATIONS; iteration++ ) {
      performIteration( train, k, v, m );
    }

    prepareOutputs();

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
   * Update the outputs.
   * @param source
   * @param msg
   */
  public void update( Observable source, Object msg ) {
    String s = msg.toString().trim();
    double d;
    try {
      d = Double.parseDouble( s );

      if ( source instanceof RandomSampler ) {
        if ( randomResults.size() == iteration ) {
          randomResults.add( new ArrayList<Double>() );
        }
        randomResults.get( iteration ).add( d );
      } else if ( source instanceof UncertaintySampler ) {
        if ( uncertResults.size() == iteration ) {
          uncertResults.add( new ArrayList<Double>() );
        }
        uncertResults.get( iteration ).add( d );
      }
    } catch ( NumberFormatException e ) {
      return; // swallow this error
    }
  }

  /**
   * Prepare the outputs from list form into String form.
   */
  protected static void prepareOutputs() {
    for ( int i = 0; i < randomResults.get( 0 ).size(); i++ ) {
      for ( List<Double> rList : randomResults ) {
        randomOutput.append( rList.get( i ) + "," );
      }
      randomOutput.replace( randomOutput.length()-1,
          randomOutput.length(), "" );
      randomOutput.append( "\n" );
      for ( List<Double> uList : uncertResults ) {
        uncertaintyOutput.append( uList.get( i ) + "," );
      }
      uncertaintyOutput.replace( uncertaintyOutput.length()-1,
          uncertaintyOutput.length(), "" );
      uncertaintyOutput.append( "\n" );
    }
  }

}
