package edu.tufts.cs.ml.exception;


public class CommandLineArgumentException extends Exception {
  /** Default generated serial version UID. */
  private static final long serialVersionUID = -6722946762668700868L;
  /** The exception message template. */
  protected static final String MSG_TEMPLATE = "Incorrect command line" +
    "arguments: %s";
  /** The reason text for different number of features. */
  public static final String DIFF_NUM_ARGS = "missing arguments.";
  /** The reason text for different types of features at the same index. */
  public static final String FILE_NOT_EXIST = "the input file %s does not " +
    "exist.";
  /** The Exception message. */
  protected String message;

  /**
   * Default constructor.
   * @param reason
   */
  public CommandLineArgumentException( String reason ) {
    message = String.format( MSG_TEMPLATE, reason );
  }

  /**
   * Constructor that takes a filename.
   * @param reason
   */
  public CommandLineArgumentException( String reason, String filename ) {
    message = String.format( MSG_TEMPLATE, String.format( reason, filename ) );
  }

  /**
   * Get the Exception message.
   */
  public String getMessage() {
    return message;
  }
}
