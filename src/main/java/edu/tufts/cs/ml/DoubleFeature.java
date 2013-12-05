package edu.tufts.cs.ml;

import java.text.DecimalFormat;

public class DoubleFeature extends Feature<Double> {
  protected DecimalFormat format = new DecimalFormat();

  /**
   * Default constructor.
   * @param value
   * @param idx
   */
  public DoubleFeature( String name, Double value ) {
    super( name, value );
    this.format.setDecimalSeparatorAlwaysShown( false );
  }

  @Override
  public Double getDifference( Feature<Double> f ) {
    return f.getValue() - this.getValue();
  }

  @Override
  public String toString() {
    return format.format( value );
  }

}
