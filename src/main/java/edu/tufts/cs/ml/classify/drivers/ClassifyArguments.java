/**
 * CommandLineOptions.java
 */
package edu.tufts.cs.ml.classify.drivers;

import java.io.File;

import edu.tufts.cs.ml.exception.CommandLineArgumentException;


/**
 * A class containing the various command-line options for the composite-match
 * program.
 *
 * These options can also be set in a properties.xml file.
 */
public class ClassifyArguments {
  /*
   * Usage statements for command-line use.
   */
  /** The argument name for the truth data. */
  public static final String ARG_TRAIN_FILE = "training_file";
  /** The argument name for the results file. */
  public static final String ARG_TEST_FILE = "testing_file";
  /** The argument name for the output file. */
  public static final String ARG_OUT_FILE = "output_file";
  /** The argument name for the output file. */
  public static final String OPT_NORMALIZE = "normalize";
  /** The usage message for the truth data. */
  public static final String USAGE_TRAIN_FILE = "The name of the file for " +
    "the training data.";
  /** The usage message for the results file. */
  public static final String USAGE_TEST_FILE = "The name of the file for " +
    "the testing data.";
  /** The usage message for the output file. */
  public static final String USAGE_OUT_FILE = "The name for the output file.";
  /** The usage message for the output file. */
  public static final String USAGE_NORMALIZE =
      "(Optional) Whether to normalize the data.";
  /** The usage message. */
  protected static String usage = "classify " + ARG_TRAIN_FILE + " " +
    ARG_TEST_FILE + " " + ARG_OUT_FILE + " [--normalize]\n\n" +
    ARG_TRAIN_FILE + ":\t " + USAGE_TRAIN_FILE + "\n" + ARG_TEST_FILE +
    ":\t " + USAGE_TEST_FILE + "\n" + ARG_OUT_FILE + ":\t" + USAGE_OUT_FILE +
    "\n" + OPT_NORMALIZE + ":\t" + USAGE_NORMALIZE;

  /*
   * Argument definitions for command line use.
   */
  /** The input training file location. */
  private File trainFile;
  /** The input test file location. */
  private File testFile;
  /** The output file location. */
  private File outFile;
  /** Whether to normalize the data. */
  private boolean normalize = false;

  /**
   * Options from the command line arguments override default settings
   * defined in this class.
   */
  public ClassifyArguments( String[] args )
    throws CommandLineArgumentException {
    if ( args.length >= 1 && args[0].toUpperCase().contains( "USAGE" ) ) {
      printUsage( "Usage:" );
    } else if ( args.length < 2 ) {
      printUsage( CommandLineArgumentException.DIFF_NUM_ARGS );
    }

    String train = args[0];
    String test = args[1];
    String output = args[2];

    for ( String arg: args ) {
      if ( arg.trim().equalsIgnoreCase( "--normalize" ) ) {
        normalize = true;
      }
    }

    trainFile = new File( train );
    testFile = new File( test );
    outFile = new File( output );

    if ( !trainFile.exists() || !trainFile.isFile() ) {
      printUsage( CommandLineArgumentException.FILE_NOT_EXIST, train );
    } else if ( !testFile.exists() || !testFile.isFile() ) {
      printUsage( CommandLineArgumentException.FILE_NOT_EXIST, test );
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
   * Get the output filename.
   *
   * @return
   */
  public File getOutputFile() {
    return this.outFile;
  }

  /**
   * Get the filename for the testing data.
   *
   * @return
   */
  public File getTestingFile() {
    return this.testFile;
  }

  /**
   * Whether to normalize the data.
   * @return
   */
  public boolean normalize() {
    return this.normalize;
  }
}
