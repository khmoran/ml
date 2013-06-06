package edu.tufts.cs.ml;

public class MissingFeature extends Feature<String> {

  /**
   * Default constructor.
   * @param value
   * @param idx
   */
  public MissingFeature( String name ) {
    super( name, "?" );
  }

  @Override
  public Double getDifference( Feature<String> f ) {
    return null;
  }

}
