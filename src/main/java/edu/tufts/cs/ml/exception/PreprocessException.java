package edu.tufts.cs.ml.exception;


public class PreprocessException extends Exception {
  /** Generated serial version UID. */
  private static final long serialVersionUID = 2663005175589347871L;
  /** The exception message template. */
  public static final String LABELS_TO_BINARY_MSG = "Attempted to change " +
    "labels to binary with more than 2 classes";
  /** The Exception message. */
  protected String message;

  /**
   * Default constructor.
   * @param fv1
   * @param fv2
   * @param reason
   */
  public PreprocessException( String reason ) {
    message = reason;
  }

  /**
   * Get the Exception message.
   */
  public String getMessage() {
    return message;
  }
}
