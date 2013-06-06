package edu.tufts.cs.ml.features.drivers;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.tufts.cs.ml.TrainRelation;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.exception.IncomparableFeatureVectorException;
import edu.tufts.cs.ml.exception.PreprocessException;
import edu.tufts.cs.ml.features.ComboSelector;
import edu.tufts.cs.ml.features.FeatureSelector;
import edu.tufts.cs.ml.features.FilterSelector;
import edu.tufts.cs.ml.features.WrapperSelector;
import edu.tufts.cs.ml.preprocess.LabelsToBinary;
import edu.tufts.cs.ml.reader.ArffReader;
import edu.tufts.cs.ml.reader.Reader;
import edu.tufts.cs.ml.writer.ResultWriter;

/**
 * Classify the provided examples given a training set.
 *
 * @author Kelly Moran
 *
 */
public class SelectFeatures implements Observer {
  /** The Logger. */
  private static final Logger LOG = Logger.getLogger( SelectFeatures.class
      .getName() );
  /** The singleton instance. */
  private static SelectFeatures instance = new SelectFeatures();
  /** Output from the filter method. */
  protected static StringBuilder filterOutput = new StringBuilder();
  /** Output from the wrapper method. */
  protected static StringBuilder wrapperOutput = new StringBuilder();
  /** Output from the combo method. */
  protected static StringBuilder comboOutput = new StringBuilder();

  /**
   * Private constructor for utility class.
   */
  private SelectFeatures() {
    // purposely not instantiable
  }

  /**
   * Print the configuration to the console.
   *
   * @param cmd
   */
  protected static void printInfo( SelectFeaturesArguments cmd ) {
    LOG.log( Level.INFO, "Running select features with: "
        + "\n\tTraining file:\t" + cmd.getTrainingFile()
        + "\n\tOutput of the filter method:\t" + cmd.getOutputFilter()
        + "\n\tOutput of the wrapper method:\t" + cmd.getOutputWrapper()
        + "\n\tOutput of my own method:\t" + cmd.getOutputOwn() );
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

    // ///////////
    // SETUP //
    // ///////////
    int[] kVals = { 7 };
    SelectFeaturesArguments cmd = new SelectFeaturesArguments( args );
    printInfo( cmd );

    /*
     * Initialize the training data.
     */
    File trainFile = cmd.getTrainingFile();
    Reader<String> reader = new ArffReader<String>();
    TrainRelation<String> train = (TrainRelation<String>) reader
        .read( trainFile );

    /*
     * Preprocess it to replace labels with binary values.
     */
    TrainRelation<Integer> binTrain = LabelsToBinary.preprocess( train );

    // ///////////////////
    // FILTER METHOD //
    // ///////////////////
    LOG.log( Level.INFO, "Running the filter method..." );
    FeatureSelector<Integer> selector =
      new FilterSelector<Integer>( binTrain, kVals );
    selector.addObserver( instance );
    Entry<Double, TrainRelation<Integer>> result = selector.selectFeatures();
    LOG.log( Level.INFO, "Filter method accuracy: " + result.getKey() +
             "\nfeatures:\n" + result.getValue().getMetadata() );

    /*
     * Write the filter results to a file
     */
    File outFile = cmd.getOutputFilter();
    ResultWriter rw = new ResultWriter();
    rw.write( filterOutput.toString(), outFile );

    // ////////////////////
    // WRAPPER METHOD //
    // ////////////////////
    LOG.log( Level.INFO, "Running the wrapper method..." );
    selector = new WrapperSelector<Integer>( binTrain, kVals );
    selector.addObserver( instance );
    result = selector.selectFeatures();
    LOG.log( Level.INFO, "Wrapper method accuracy: " + result.getKey() +
        "\nfeatures:\n" + result.getValue().getMetadata() );

    /*
     * Write the wrapper results to a file
     */
    outFile = cmd.getOutputWrapper();
    rw.write( wrapperOutput.toString(), outFile );

    // //////////////////
    // COMBO METHOD //
    // //////////////////
    LOG.log( Level.INFO, "Running my own method..." );
    selector = new ComboSelector<Integer>( binTrain, kVals );
    selector.addObserver( instance );
    result = selector.selectFeatures();
    LOG.log( Level.INFO, "Own method accuracy: " + result.getKey() +
        "\nfeatures:\n" + result.getValue().getMetadata() );

    /*
     * Write the combo results to a file
     */
    outFile = cmd.getOutputOwn();
    rw.write( comboOutput.toString(), outFile );
  }

  /**
   * Update the outputs.
   * @param source
   * @param msg
   */
  public void update( Observable source, Object msg ) {
    if ( source instanceof ComboSelector ) {
      comboOutput.append( msg.toString() );
    } else if ( source instanceof FilterSelector ) {
      filterOutput.append( msg.toString() );
    } else if ( source instanceof WrapperSelector ) {
      wrapperOutput.append( msg.toString() );
    }
  }

}
