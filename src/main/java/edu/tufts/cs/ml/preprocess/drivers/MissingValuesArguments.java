/**
 * CommandLineOptions.java
 */
package edu.tufts.cs.ml.preprocess.drivers;

import java.io.File;

import edu.tufts.cs.ml.classify.drivers.ClassifyArguments;
import edu.tufts.cs.ml.exception.CommandLineArgumentException;
import edu.tufts.cs.ml.preprocess.MissingValuesPreprocessor.Method;
import edu.tufts.cs.ml.util.PrettyPrinter;


/**
 * A class containing the various command-line options for the composite-match
 * program.
 *
 * These options can also be set in a properties.xml file.
 */
public class MissingValuesArguments extends ClassifyArguments {
  /*
   * Usage statements for command-line use.
   */
  /** The argument name for preprocessing method. */
  public static final String ARG_METHOD = "method";
  /** The argument name for the output test file. */
  public static final String ARG_OUT_TEST_FILE = "out_test_file";
  /** The usage message for the truth data. */
  public static final String USAGE_METHOD = "The method to use for filling " +
    "in missing values. Possible values are: " + PrettyPrinter.prettyPrint(
      Method.values() );
  /** The usage message for the output test file. */
  public static final String USAGE_OUT_TEST_FILE = "The filename to output" +
    "the preprocessed test data to.";
  /** The usage message. */
  protected static String usage = "preprocess " + ARG_TRAIN_FILE + " " +
    ARG_TEST_FILE + " " + ARG_OUT_FILE + " " + ARG_OUT_TEST_FILE + " " +
    ARG_METHOD + "\n\n" + ARG_TRAIN_FILE + ":\t " + USAGE_TRAIN_FILE +
    "\n" + ARG_TEST_FILE + ":\t " + USAGE_TEST_FILE + "\n" + ARG_OUT_FILE +
    ":\t" + USAGE_OUT_FILE + "\n" + ARG_OUT_TEST_FILE + ":\t" +
    USAGE_OUT_TEST_FILE + "\n" + ARG_METHOD + ":\t" + USAGE_METHOD;

  /*
   * Argument definitions for command line use.
   */
  /** The preprocessing method. */
  private Method method;
  /** The output test file. */
  private File outTestFile;

  /**
   * Options from the command line arguments override default settings
   * defined in this class.
   */
  public MissingValuesArguments( String[] args )
    throws CommandLineArgumentException {
    super( args );

    String outputTest = args[3];
    outTestFile = new File( outputTest );

    String methodName = args[4];
    method = Method.valueOf( methodName );
  }

  @Override
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
   * Get the preprocessing method.
   *
   * @return
   */
  public Method getMethod() {
    return this.method;
  }

  /**
   * Get the output test file.
   * @return
   */
  public File getOutTestFile() {
    return this.outTestFile;
  }
}
