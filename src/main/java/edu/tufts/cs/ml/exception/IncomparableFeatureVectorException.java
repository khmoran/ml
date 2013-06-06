package edu.tufts.cs.ml.exception;

import edu.tufts.cs.ml.FeatureVector;

public class IncomparableFeatureVectorException extends Exception {
  /** Generated serial version UID. */
  private static final long serialVersionUID = 2663005175589347871L;
  /** The exception message template. */
  protected static final String MSG_TEMPLATE = "Cannot compare feature " +
    "vectors %s and %s because %s.";
  /** The exception message template. */
  protected static final String MSG_TEMPLATE_NO_FVS = "Cannot compare " +
    "feature vectors because %s.";
  /** The reason text for different number of features. */
  public static final String DIFF_NUM_FEATURES = "they contain a " +
    "different number of arguments";
  /** The reason text for different types of features at the same index. */
  public static final String DIFF_TYPE_FEATURES = "they contain at least two" +
    "features of different types at the same index";
  /** The Exception message. */
  protected String message;

  /**
   * Default constructor.
   * @param fv1
   * @param fv2
   * @param reason
   */
  public IncomparableFeatureVectorException( FeatureVector<?> fv1,
      FeatureVector<?> fv2, String reason ) {
    message = String.format(
        MSG_TEMPLATE, fv1.toString(), fv2.toString(), reason );
  }

  /**
   * Default constructor.
   * @param fv1
   * @param fv2
   * @param reason
   */
  public IncomparableFeatureVectorException( String reason ) {
    message = String.format( MSG_TEMPLATE_NO_FVS, reason );
  }

  /**
   * Get the Exception message.
   */
  public String getMessage() {
    return message;
  }
}
