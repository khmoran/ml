package edu.tufts.cs.ml.classify.drivers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.classify.KnnClassifier;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.normalize.Normalizer;
import edu.tufts.cs.ml.normalize.ZScoreNormalizer;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.util.PrettyPrinter;
import edu.tufts.cs.ml.validate.KnnLOOCValidator;
import edu.tufts.cs.ml.writer.CsvWriter;
import edu.tufts.cs.ml.writer.Writer;

/**
 * Classify the provided examples given a training set.
 * @author Kelly Moran
 *
 */
public class ClassifyWithKNN {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      ClassifyWithKNN.class.getName() );

  /**
   * Private constructor for utility class.
   */
  private ClassifyWithKNN() {
    // purposely not instantiable
  }

  /**
   * Print the configuration to the console.
   * @param cmd
   */
  protected static void printInfo( ClassifyArguments cmd ) {
    LOG.log( Level.INFO, "Running k-Nearest Neighbors classifier with: " +
      "\n\tTraining file:\t" + cmd.getTrainingFile() +
      "\n\tTesting file:\t" + cmd.getTestingFile() +
      "\n\tOutput file:\t" + cmd.getOutputFile() +
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
    ClassifyArguments cmd = new ClassifyArguments( args );
    printInfo( cmd );

    /*
     * Train the Classifier.
     */
    LOG.info( "Training the classifier." );
    File trainFile = cmd.getTrainingFile();
    Reader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    /*
     * Run the Classifier on the test data.
     */
    LOG.info( "Running the classifier." );
    File testFile = cmd.getTestingFile();
    TestRelation<String> test = (TestRelation<String>) reader.read( testFile );

    /*
     * Normalize the data per the user's specification.
     */
    if ( cmd.normalize() ) {
      Normalizer<String> n = new ZScoreNormalizer<String>( train );
      n.normalize( test );
    }

    KnnClassifier<String> c = new KnnClassifier<String>();
    c.train( train );
    int[] kVals = {1, 3, 5, 7, 9};
    c.classify( test, kVals );

    /*
     * Gather performance statistics.
     */
    KnnLOOCValidator<String> validator = new KnnLOOCValidator<String>( train );
    double[] accuracy = validator.validate( kVals );

    LOG.log( Level.INFO, "Accuracy: " + PrettyPrinter.prettyPrint( accuracy ) );

    /*
     * Write the output.
     */
    File outFile = cmd.getOutputFile();
    Writer writer = new CsvWriter();
    writer.write( test, outFile );

    LOG.log( Level.INFO, "Complete!" );
  }

}
