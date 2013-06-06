/**
 * CommandLineOptions.java
 */
package edu.tufts.cs.ml.features.drivers;

import java.io.File;

import edu.tufts.cs.ml.exception.CommandLineArgumentException;


/**
 * A class containing the various command-line options for the composite-match
 * program.
 *
 * These options can also be set in a properties.xml file.
 */
public class SelectFeaturesArguments {
  /*
   * Usage statements for command-line use.
   */
  /** The argument name for the truth data. */
  public static final String ARG_TRAIN_FILE = "training_file";
  /** The argument name for the output of the filter method. */
  public static final String ARG_OUT_FILTER = "output_filter";
  /** The argument name for the output of the wrapper method. */
  public static final String ARG_OUT_WRAPPER = "output_wrapper";
  /** The argument name for the output of my own method. */
  public static final String ARG_OUT_OWN = "output_own";
  /** The usage message for the truth data. */
  public static final String USAGE_TRAIN_FILE = "The name of the file for " +
    "the training data.";
  /** The usage message for the output of the filter method. */
  public static final String USAGE_OUT_FILTER = "The name of output file for " +
    "the results of the filter method.";
  /** The usage message for the output of the wrapper method. */
  public static final String USAGE_OUT_WRAPPER = "The name of the output " +
    "file for the results of the wrapper method.";
  /** The usage message for the output of my own method. */
  public static final String USAGE_OUT_OWN = "The name of the output file " +
      "for the results of my own method.";
  /** The usage message. */
  protected static String usage = "select-features " + ARG_TRAIN_FILE + " " +
    ARG_OUT_FILTER + " " + ARG_OUT_WRAPPER + " " + ARG_OUT_OWN + "\n" +
    ARG_TRAIN_FILE + ":\t " + USAGE_TRAIN_FILE + "\n" + ARG_OUT_FILTER +
    ":\t " + USAGE_OUT_FILTER + "\n" + ARG_OUT_WRAPPER + ":\t" +
    USAGE_OUT_WRAPPER + "\n" + ARG_OUT_OWN + ":\t" + USAGE_OUT_OWN;

  /*
   * Argument definitions for command line use.
   */
  /** The input training file location. */
  private File trainFile;
  /** The file for the output of the filter method. */
  private File outFilter;
  /** The file for the output of the wrapper method. */
  private File outWrapper;
  /** The file for the output of my own method. */
  private File outOwn;

  /**
   * Options from the command line arguments override default settings
   * defined in this class.
   */
  public SelectFeaturesArguments( String[] args )
    throws CommandLineArgumentException {
    if ( args.length >= 1 && args[0].toUpperCase().contains( "USAGE" ) ) {
      printUsage( "Usage:" );
    } else if ( args.length < 4 ) {
      printUsage( CommandLineArgumentException.DIFF_NUM_ARGS );
    }

    String train = args[0];
    String filter = args[1];
    String wrapper = args[2];
    String own = args[3];

    trainFile = new File( train );
    outFilter = new File( filter );
    outWrapper = new File( wrapper );
    outOwn = new File( own );

    if ( !trainFile.exists() || !trainFile.isFile() ) {
      printUsage( CommandLineArgumentException.FILE_NOT_EXIST, train );
    }
  }

  /**
   * Print the usage and the error message.
   * @param args
   * @throws CommandLineArgumentException
   */
  public void printUsage( String... args )
    throws CommandLineArgumentException {
    System.err.println( usage );

    if ( args.length > 0 && args[0].toUpperCase().contains( "USAGE" ) ) {
      System.exit( 0 );
    }
    if ( args.length == 1 ) {
      throw new CommandLineArgumentException( args[0] );
    } else {
      throw new CommandLineArgumentException( args[0], args[1] );
    }
  }

  /**
   * Get the filename for the input training data.
   *
   * @return
   */
  public File getTrainingFile() {
    return this.trainFile;
  }

  /**
   * Get the file for the output of the filter method.
   *
   * @return
   */
  public File getOutputFilter() {
    return this.outFilter;
  }

  /**
   * Get the file for the output of the wrapper method.
   *
   * @return
   */
  public File getOutputWrapper() {
    return this.outWrapper;
  }

  /**
   * Get the file for the output of my own method.
   * @return
   */
  public File getOutputOwn() {
    return this.outOwn;
  }
}
