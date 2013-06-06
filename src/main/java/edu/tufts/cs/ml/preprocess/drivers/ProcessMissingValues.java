package edu.tufts.cs.ml.preprocess.drivers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.preprocess.MissingValuesPreprocessor;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.writer.ArffWriter;
import edu.tufts.cs.ml.writer.Writer;

/**
 * Classify the provided examples given a training set.
 * @author Kelly Moran
 *
 */
public class ProcessMissingValues {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      ProcessMissingValues.class.getName() );

  /**
   * Private constructor for utility class.
   */
  private ProcessMissingValues() {
    // purposely not instantiable
  }

  /**
   * Print the configuration to the console.
   * @param cmd
   */
  protected static void printInfo( MissingValuesArguments cmd ) {
    LOG.log( Level.INFO, "Running missing values processor with: " +
      "\n\tTraining file:\t" + cmd.getTrainingFile() +
      "\n\tTesting file:\t" + cmd.getTestingFile() +
      "\n\tOutput file:\t" + cmd.getOutputFile() +
      "\n\tOutput test file:\t" + cmd.getOutTestFile() +
      "\n\tMethod:\t" + cmd.getMethod() +
      "\n\tNormalize:\t" + cmd.normalize() );
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
    MissingValuesArguments cmd = new MissingValuesArguments( args );
    printInfo( cmd );

    /*
     * Train the Classifier.
     */
    File trainFile = cmd.getTrainingFile();
    Reader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    /*
     * Run the Classifier on the test data.
     */
    File testFile = cmd.getTestingFile();
    TrainRelation<String> test = (TrainRelation<String>)
        reader.read( testFile );

    MissingValuesPreprocessor<String> p =
        new MissingValuesPreprocessor<String>( train );
    p.preprocess( test, cmd.getMethod() );

    /*
     * Write the output.
     */
    File outFile = cmd.getOutputFile();
    Writer writer = new ArffWriter();
    writer.write( train, outFile );

    File outTestFile = cmd.getOutTestFile();
    writer.write( test, outTestFile );
    LOG.log( Level.INFO, "Complete!" );
  }

}
