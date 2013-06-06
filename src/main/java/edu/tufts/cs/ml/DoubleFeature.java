package edu.tufts.cs.ml;

public class DoubleFeature extends Feature<Double> {

  /**
   * Default constructor.
   * @param value
   * @param idx
   */
  public DoubleFeature( String name, Double value ) {
    super( name, value );
  }

  @Override
  public Double getDifference( Feature<Double> f ) {
    return f.getValue() - this.getValue();
  }

}
