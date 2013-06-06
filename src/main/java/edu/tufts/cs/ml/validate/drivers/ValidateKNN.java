package edu.tufts.cs.ml.validate.drivers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.normalize.ZScoreNormalizer;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.validate.KnnLOOCValidator;
import edu.tufts.cs.ml.writer.ArffWriter;
import edu.tufts.cs.ml.writer.Writer;

/**
 * Classify the provided examples given a training set.
 * @author Kelly Moran
 *
 */
public class ValidateKNN {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      ValidateKNN.class.getName() );

  /**
   * Private constructor for utility class.
   */
  private ValidateKNN() {
    // purposely not instantiable
  }

  /**
   * Print the configuration to the console.
   * @param cmd
   */
  protected static void printInfo( ValidatorArguments cmd ) {
    LOG.log( Level.INFO, "Running k-Nearest Neighbors/LOOCV Validator with: " +
      "\n\tTraining file:\t" + cmd.getTrainingFile() +
      "\n\tOutput file:\t" + cmd.getOutputFile() +
      "\n\rNormalize:\t" + cmd.normalize() );
  }

  /**
   * @param args
   * @throws CommandLineArgumentException
   * @throws IOException
   * @throws IncomparableFeatureVectorException
   */
  @SuppressWarnings( "unchecked" )
  public static void main( String[] args ) throws CommandLineArgumentException,
    IOException, IncomparableFeatureVectorException {
    ValidatorArguments cmd = new ValidatorArguments( args );
    printInfo( cmd );

    /*
     * Train the Classifier.
     */
    File trainFile = cmd.getTrainingFile();
    Reader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    /*
     * Normalize the data per the user's specification.
     */
    if ( cmd.normalize() ) {
      new ZScoreNormalizer<String>( train );

      // write the normalized output
      File outFile = cmd.getOutputFile();
      Writer writer = new ArffWriter();
      writer.write( train, outFile );
    }

    /*
     * Run the Validator on the data.
     */
    KnnLOOCValidator<String> v = new KnnLOOCValidator<String>( train );
    int[] kVals = { 1, 3, 5, 7, 9, 11, 13, 15 };
    double[] kAccuracies = v.validate( kVals );

    LOG.log( Level.INFO, train.toString() + "\n\n" );
    for ( int i = 0; i < kVals.length; i++ ) {
      LOG.log( Level.INFO, "k: " + kVals[i] + "\taccuracy:" + kAccuracies[i] );
    }

    LOG.log( Level.INFO, "Complete!" );
  }

}
