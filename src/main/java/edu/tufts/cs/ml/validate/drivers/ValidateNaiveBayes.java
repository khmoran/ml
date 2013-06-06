package edu.tufts.cs.ml.validate.drivers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.classify.Classifier;
import edu.tufts.cs.ml.classify.NaiveBayesClassifier;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.normalize.ZScoreNormalizer;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.validate.LOOCValidator;
import edu.tufts.cs.ml.writer.ArffWriter;
import edu.tufts.cs.ml.writer.Writer;

/**
 * Classify the provided examples given a training set.
 * @author Kelly Moran
 *
 */
public class ValidateNaiveBayes {
  /** The Logger. */
  private static final Logger LOG =  Logger.getLogger(
      ValidateNaiveBayes.class.getName() );

  /**
   * Private constructor for utility class.
   */
  private ValidateNaiveBayes() {
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
    LOG.info( "Reading the data..." );
    File trainFile = cmd.getTrainingFile();
    Reader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader.read(
        trainFile );

    LOG.info( "# instances: " + train.size() );

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
    LOG.info( "Training the classifier..." );
    Classifier<String> c = new NaiveBayesClassifier<String>();
    c.train( train );
    LOG.info( "Validating the classifier..." );
    LOOCValidator<String> v = new LOOCValidator<String>( train );
    double accuracy = v.validate( c );

    LOG.log( Level.INFO, "Accuracy: " + accuracy );

    LOG.log( Level.INFO, "Complete!" );
  }

}
