/**
 * CommandLineOptions.java
 */
package edu.tufts.cs.ml.cluster.outlier.drivers;

import java.io.File;

import edu.tufts.cs.ml.exception.CommandLineArgumentException;

/**
 * A class containing the various command-line options for the outlier detection
 * program.
 *
 * These options can also be set in a properties.xml file.
 */
public class OutlierArguments {
  /*
   * Usage statements for command-line use.
   */
  /** The argument name for the first data set file. */
  public static final String ARG_DATASET_1 = "dataset1";
  /** The argument name for the second data set file. */
  public static final String ARG_DATASET_2 = "dataset2";
  /** The argument name for the second data set file. */
  public static final String OPT_THRESHOLD = "threshold";
  /** The default value for the second data set file. */
  public static final double DEFAULT_THRESHOLD = .85;
  /** The default output file name. */
  protected static final String DEFAULT_OUT_FILE = "kmeans.out";
  /** The usage message for the first data set file. */
  public static final String USAGE_DATASET_1 = "The name of the file for "
      + "the first data set.";
  /** The usage message for the second data set file. */
  public static final String USAGE_DATASET_2 = "The name of the file for "
      + "the second data set.";
  /** The usage message for the second data set file. */
  public static final String USAGE_THRESHOLD = "The confidence threshold to "
      + "use for the fuzzy outlier detection method.";
  /** The usage message. */
  protected static String usage = "detect-outliers " + ARG_DATASET_1 + " "
      + ARG_DATASET_2 + " [" + OPT_THRESHOLD + "]\n\n" + ARG_DATASET_1 + ":\t "
      + USAGE_DATASET_1 + "\n" + ARG_DATASET_2 + ":\t" + USAGE_DATASET_2 + "\n"
      + OPT_THRESHOLD + ":\t" + USAGE_THRESHOLD;

  /*
   * Argument definitions for command line use.
   */
  /** The first input data set location. */
  private File dataset1;
  /** The second input dataset location. */
  private File dataset2;
  /** The confidence threshold. */
  private double threshold = DEFAULT_THRESHOLD;

  /**
   * Options from the command line arguments override default settings defined
   * in this class.
   */
  public OutlierArguments( String[] args ) throws CommandLineArgumentException {
    if ( args.length >= 1 && args[0].toUpperCase().contains( "USAGE" ) ) {
      printUsage( "Usage:" );
    } else if ( args.length < 2 ) {
      printUsage( CommandLineArgumentException.DIFF_NUM_ARGS );
    }

    String filename1 = args[0];
    String filename2 = args[1];

    dataset1 = new File( filename1 );
    dataset2 = new File( filename2 );

    if ( !dataset1.exists() || !dataset1.isFile() ) {
      printUsage( CommandLineArgumentException.FILE_NOT_EXIST, filename1 );
    } else if ( !dataset2.exists() || !dataset2.isFile() ) {
      printUsage( CommandLineArgumentException.FILE_NOT_EXIST, filename2 );
    }

    if ( args.length == 3 ) { // optional threshold
      threshold = Double.parseDouble( args[2] );
    }
  }

  /**
   * Print the usage and the error message.
   *
   * @param args
   * @throws CommandLineArgumentException
   */
  public void printUsage( String... args ) throws CommandLineArgumentException {
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
   * Get the filename for the first input data set.
   *
   * @return
   */
  public File getDataSet1() {
    return this.dataset1;
  }

  /**
   * Get the filename for the second input data set.
   *
   * @return
   */
  public File getDataSet2() {
    return this.dataset2;
  }

  /**
   * Get the confidence threshold.
   * @return
   */
  public double getThreshold() {
    return this.threshold;
  }
}
