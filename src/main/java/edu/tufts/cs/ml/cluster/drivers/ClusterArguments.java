/**
 * CommandLineOptions.java
 */
package edu.tufts.cs.ml.cluster.drivers;

import java.io.File;

import edu.tufts.cs.ml.exception.CommandLineArgumentException;


/**
 * A class containing the various command-line options for the composite-match
 * program.
 *
 * These options can also be set in a properties.xml file.
 */
public class ClusterArguments {
  /*
   * Usage statements for command-line use.
   */
  /** The argument name for the data set file. */
  public static final String ARG_TRAIN_FILE = "data_set";
  /** The argument name for the output file. */
  public static final String OPT_OUT_FILE = "output_file";
  /** The default output file name. */
  protected static final String DEFAULT_OUT_FILE = "kmeans.out";
  /** The usage message for the data set file. */
  public static final String USAGE_TRAIN_FILE = "The name of the file for " +
    "the data set.";
  /** The usage message for the output file. */
  public static final String USAGE_OUT_FILE = "The name for the output file.";
  /** The usage message. */
  protected static String usage = "cluster " + ARG_TRAIN_FILE  + " [" +
    OPT_OUT_FILE + "]\n\n" + ARG_TRAIN_FILE + ":\t " + USAGE_TRAIN_FILE +
    "\n" + OPT_OUT_FILE + ":\t" + USAGE_OUT_FILE;

  /*
   * Argument definitions for command line use.
   */
  /** The input data set location. */
  private File trainFile;
  /** The output file location. */
  private File outFile;

  /**
   * Options from the command line arguments override default settings
   * defined in this class.
   */
  public ClusterArguments( String[] args )
    throws CommandLineArgumentException {
    if ( args.length >= 1 && args[0].toUpperCase().contains( "USAGE" ) ) {
      printUsage( "Usage:" );
    } else if ( args.length < 1 ) {
      printUsage( CommandLineArgumentException.DIFF_NUM_ARGS );
    }

    String train = args[0];
    String output;
    if ( args.length > 1 ) {
      output = args[1];
    } else {
      output = DEFAULT_OUT_FILE;
    }

    trainFile = new File( train );
    outFile = new File( output );

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
   * Get the filename for the input data set.
   *
   * @return
   */
  public File getDataSet() {
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
}
