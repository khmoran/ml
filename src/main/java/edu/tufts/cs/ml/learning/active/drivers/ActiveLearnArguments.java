/**
 * CommandLineOptions.java
 */
package edu.tufts.cs.ml.learning.active.drivers;

import java.io.File;

import edu.tufts.cs.ml.exception.CommandLineArgumentException;


/**
 * A class containing the various command-line options for the active learning
 * program.
 */
public class ActiveLearnArguments {
  /*
   * Usage statements for command-line use.
   */
  /** The argument name for the training data. */
  public static final String ARG_TRAIN_FILE = "training_file";
  /** The argument name for the output of the random method. */
  public static final String ARG_OUT_RANDOM = "output_random";
  /** The argument name for the output of the uncertainty method. */
  public static final String ARG_OUT_UNCERTAINTY = "output_uncertainty";
  /** The usage message for the training data. */
  public static final String USAGE_TRAIN_FILE = "The name of the file for " +
    "the training data.";
  /** The usage message for the output of the random method. */
  public static final String USAGE_OUT_RANDOM = "The name of output file for " +
    "the results of the random sampling method.";
  /** The usage message for the output of the uncertain method. */
  public static final String USAGE_OUT_UNCERTAINTY = "The name of the output " +
    "file for the results of the uncertainty sampling method.";
  /** The usage message. */
  protected static String usage = "active-learn " + ARG_TRAIN_FILE + " " +
    ARG_OUT_RANDOM + " " + ARG_OUT_UNCERTAINTY + "\n" +
    ARG_TRAIN_FILE + ":\t " + USAGE_TRAIN_FILE + "\n" + ARG_OUT_RANDOM +
    ":\t " + USAGE_OUT_RANDOM + "\n" + ARG_OUT_UNCERTAINTY + ":\t" +
    USAGE_OUT_UNCERTAINTY;

  /*
   * Argument definitions for command line use.
   */
  /** The input training file location. */
  private File trainFile;
  /** The file for the output of the random method. */
  private File outRandom;
  /** The file for the output of the uncertainty method. */
  private File outUncertainty;

  /**
   * Options from the command line arguments override default settings
   * defined in this class.
   */
  public ActiveLearnArguments( String[] args )
    throws CommandLineArgumentException {
    if ( args.length >= 1 && args[0].toUpperCase().contains( "USAGE" ) ) {
      printUsage( "Usage:" );
    } else if ( args.length < 3 ) {
      printUsage( CommandLineArgumentException.DIFF_NUM_ARGS );
    }

    String train = args[0];
    String random = args[1];
    String uncertainty = args[2];

    trainFile = new File( train );
    outRandom = new File( random );
    outUncertainty = new File( uncertainty );

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
   * Get the file for the output of the random sampling method.
   *
   * @return
   */
  public File getOutputRandom() {
    return this.outRandom;
  }

  /**
   * Get the file for the output of the uncertainty sampling method.
   *
   * @return
   */
  public File getOutputUncertainty() {
    return this.outUncertainty;
  }

}
