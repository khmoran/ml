package edu.tufts.cs.ml.classify.drivers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TestRelation;
import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.classify.Classifier;
import edu.tufts.cs.ml.classify.NaiveBayesClassifier;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.normalize.Normalizer;
import edu.tufts.cs.ml.normalize.ZScoreNormalizer;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.validate.LOOCValidator;
import edu.tufts.cs.ml.writer.CsvWriter;
import edu.tufts.cs.ml.writer.Writer;

/**
 * Classify the provided examples given a training set.
 * @author Kelly Moran
 *
 */
public class ClassifyWithNaiveBayes {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      ClassifyWithNaiveBayes.class.getName() );

  /**
   * Private constructor for utility class.
   */
  private ClassifyWithNaiveBayes() {
    // purposely not instantiable
  }

  /**
   * Print the configuration to the console.
   * @param cmd
   */
  protected static void printInfo( ClassifyArguments cmd ) {
    LOG.log( Level.INFO, "Running Naive Bayes classifier with: " +
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
    File trainFile = cmd.getTrainingFile();
    Reader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    /*
     * Run the Classifier on the test data.
     */
    File testFile = cmd.getTestingFile();
    TestRelation<String> test = (TestRelation<String>) reader.read( testFile );

    /*
     * Normalize the data per the user's specification.
     */
    if ( cmd.normalize() ) {
      Normalizer<String> n = new ZScoreNormalizer<String>( train );
      n.normalize( test );
    }

    Classifier<String> c = new NaiveBayesClassifier<String>();
    c.train( train );
    c.classify( test );

    /*
     * Gather performance statistics.
     */
    LOOCValidator<String> validator = new LOOCValidator<String>( train );
    double accuracy = validator.validate( c );

    LOG.log( Level.INFO, "Accuracy: " + accuracy );

    /*
     * Write the output.
     */
    File outFile = cmd.getOutputFile();
    Writer writer = new CsvWriter();
    writer.write( test, outFile );

    LOG.log( Level.INFO, "Complete!" );
  }

}
